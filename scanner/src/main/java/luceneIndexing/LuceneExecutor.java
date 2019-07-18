package luceneIndexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class LuceneExecutor{
	
	public static void main(String[] args) {
		String dataDirectory = "E:\\Lucene\\sample_directory_for_testing\\";
		String indexDirectory = "E:\\Lucene\\temp_test_index";
		String resultDirectory = "E:\\Lucene";
		String searchString = "public";
		ApacheLucene.entry(dataDirectory, indexDirectory, resultDirectory, searchString);
	}
}





class ApacheLucene {
	
	private static void createIndexFromDirectory(final File inputfolder, String outputDirectory,
			StandardAnalyzer standardAnalyzer) {
		if (null != inputfolder && null != outputDirectory && inputfolder.listFiles() != null
				&& inputfolder.listFiles().length > 0) {
			for (final File fileEntry : inputfolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					createIndexFromDirectory(fileEntry, outputDirectory, standardAnalyzer);
				} else {
					String inputFilePath;
					File inputfile;
					Directory directory;
					IndexWriterConfig config;
					IndexWriter writer;
					Document document;
					try {
						inputFilePath = fileEntry.getCanonicalFile().toString();
						if (inputFilePath.endsWith(".java") || inputFilePath.endsWith(".xml")
								|| inputFilePath.endsWith(".wsdl") || inputFilePath.endsWith(".xsd")
								|| inputFilePath.endsWith(".properties")) {
							inputfile = new File(inputFilePath);

							directory = FSDirectory.open(Paths.get(outputDirectory));
							config = new IndexWriterConfig(standardAnalyzer);
							config.setOpenMode(OpenMode.CREATE);
							writer = new IndexWriter(directory, config);
							document = new Document();
							// index for path
							document.add(new TextField("path", inputfile.getPath(), Field.Store.YES));
							try (BufferedReader br = new BufferedReader(new FileReader(inputfile))) {
								// index for content
								document.add(new TextField("content", br));
								writer.addDocument(document);
								writer.close();
							}
						}
					} catch (IOException e1) {
						System.out.println("Failed to create indices");
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private static void searchAndLog(String outputDirectory, String searchString, StandardAnalyzer standardAnalyzer,
			FileWriter csvWriter) {
		Directory directory;
		IndexReader reader;
		IndexSearcher searcher;
		QueryParser parser;
		Query query;
		TopDocs results;
		try {
			directory = FSDirectory.open(Paths.get(outputDirectory));
			reader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(reader);
			parser = new QueryParser("content", standardAnalyzer);
			query = parser.parse(searchString);
			results = searcher.search(query, 100);
			System.out.println("Hits for search -->" + results.totalHits);
			for (ScoreDoc scoreDoc : results.scoreDocs) {
				Document dtemp = searcher.doc(scoreDoc.doc);
				dtemp.getFields();
				for (Object o : dtemp.getFields()) {
					Field field = (Field) o;
					// System.out.println("Search found in this file : " + field.stringValue());
					writeToCSV(field.stringValue(), csvWriter);
				}
			}
		} catch (IOException | ParseException e) {
			System.out.println("Failed to Search from the indices");
			e.printStackTrace();
		}

	}

	private static void writeToCSV(String inputFilePath, FileWriter csvWriter) throws IOException {
		csvWriter.append(String.join(",", inputFilePath));
		csvWriter.append("\n");
		csvWriter.flush();
	}
	
	public static void entry(String dataFolder,String indexDirectory,String resultDirectory,String searchString) {
		long startTime = Calendar.getInstance().getTimeInMillis();
		final File dataDirectory = new File(dataFolder);
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		System.out.println("Indexing Started..");
		createIndexFromDirectory(dataDirectory, indexDirectory, standardAnalyzer);
		long midTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("Time taken for indexing : " + (midTime - startTime) + " ms");
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).replaceAll("\\.", "-");
		System.out.println("Started");
		String csvFile = resultDirectory + "\\search_result_" + timeStamp + ".csv";
		FileWriter csvWriter = null;
		try {
			csvWriter = new FileWriter(csvFile);
			csvWriter.append("Search result for '" + searchString + "'");
			csvWriter.append("\n");
			csvWriter.append("Path");
			csvWriter.append("\n");
			searchAndLog(indexDirectory, searchString, standardAnalyzer, csvWriter);
			csvWriter.close();
		} catch (IOException e) {
			System.out.println("Failed to write search results into the file");
			e.printStackTrace();
		}

		long endTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("Time taken for search : " + (endTime - midTime) + " ms");
		System.out.println("Attempting to open the result file..");
		String[] commands = { "cmd", "/c", csvFile };
		try {
			Runtime.getRuntime().exec(commands);
		} catch (Exception ex) {
			System.out.println("Failed to open the file, Please check : " + csvFile);
		}
	}
}