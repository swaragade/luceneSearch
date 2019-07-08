package luceneIndexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Calendar;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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

public class LuceneIndexing {
	public static void createIndexFromDirectory(final File inputfolder, String outputDirectory , StandardAnalyzer standardAnalyzer) throws IOException {
		for (final File fileEntry : inputfolder.listFiles()) {
			if (fileEntry.isDirectory()) {
				createIndexFromDirectory(fileEntry,outputDirectory, standardAnalyzer);
			} else {
				//System.out.println(fileEntry.getCanonicalFile());
				//System.out.println(fileEntry.getName());
				
				String inputFilePath = fileEntry.getCanonicalFile().toString();
				File file = new File(inputFilePath);

				Directory directory = FSDirectory.open(Paths.get(outputDirectory));
				IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
				config.setOpenMode(OpenMode.CREATE); 

				IndexWriter writer = new IndexWriter(directory, config);

				Document document = new Document();
				//index for path
				document.add(new StringField("path", file.getPath(), Field.Store.YES));
				//index for filename
				//document.add(new StringField("filename", file.getName(), Field.Store.YES));
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					//index for content
					document.add(new TextField("content", br));
					writer.addDocument(document);
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	
	public static void searchAndLog(String outputDirectory, StandardAnalyzer standardAnalyzer) throws IOException, ParseException {
		Directory directory = FSDirectory.open(Paths.get(outputDirectory));
		IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("content", standardAnalyzer);
        Query query = parser.parse("Chicago");
        TopDocs results = searcher.search(query, 100);
        System.out.println("Hits for search -->" + results.totalHits);
        
        for (ScoreDoc scoreDoc : results.scoreDocs) {
        	Document dtemp = searcher.doc(scoreDoc.doc);
        	dtemp.getFields();
        	 for (Object o : dtemp.getFields()) {
        	        
        	        Field field = (Field) o;
        	        //System.out.println("Field has no name  :"+field.name());
        	        System.out.println("Search found in this file : "+
        	                field.stringValue());
        	    }
        	//resultDocs.add(searcher.doc(scoreDoc.doc));
        }
        //System.out.println("last :: "+resultDocs.get(0).get("content"));
	}
	
	
	
	
	public static void main(String[] args) throws IOException, ParseException {
		long startTime = Calendar.getInstance().getTimeInMillis();
		long endTime;
		final File inputfolder = new File("E:\\Lucene\\sample_directory_for_testing\\");
		String outputDirectory = "E:\\Lucene\\temp_test_index\\";
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		createIndexFromDirectory(inputfolder,outputDirectory, standardAnalyzer);
		endTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("Time taken for indexing : "+(endTime-startTime)+" ms");
		searchAndLog(outputDirectory, standardAnalyzer);
		endTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("Total time taken for search : "+(endTime-startTime)+" ms");
	}
}