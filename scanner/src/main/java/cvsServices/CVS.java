/*
 * package cvsServices;
 * 
 * import java.io.File;
 * 
 * import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
 * 
 * import com.infy.tool.util.Constants; import com.infy.tool.vo.DiffRequest;
 * import com.infy.tool.vo.ExceptionAnalyerVO; import
 * com.infy.tool.vo.RevisionHistory;
 * 
 * public class CVS extends AbstractFileRevisionHistory{
 * 
 * @Override public ExceptionAnalyerVO fileRevisionHistoryAnalyser(
 * ExceptionAnalyerVO exceptionVO) throws Exception {
 * 
 * 
 * String filePath = Constants.CHECKOUT_DIR + exceptionVO.getModuleName() +
 * exceptionVO.getFileName();
 * System.out.println(" Revision History File path ::" + filePath );
 * 
 * for(RevisionHistory revisionHistory : exceptionVO.getRevisionHistory()){
 * 
 * // checkout the module CheckoutCommand checkoutCommand = new
 * CheckoutCommand(); checkoutCommand.setModule(filePath);
 * checkoutCommand.setUseHeadIfNotFound(Boolean.TRUE.booleanValue());
 * checkoutCommand.setCheckoutByRevision("branch_rel_19_40"); //File cvsFile =
 * new File( filePath); //checkoutCommand.setFiles(new File[]{cvsFile});
 * CommandExecutor.getInstance().executeCommand(checkoutCommand, new
 * CVSAbtractListener());
 * 
 * DiffRequest differenceRequest = new DiffRequest();
 * differenceRequest.setLeftRevision(revisionHistory.getNumber());
 * differenceRequest.setFilePath(exceptionVO.getCanonicalFileName());
 * differenceRequest.setIgnoreAllWhiteSpace(true);
 * differenceRequest.setIgnoreBlankLines(true);
 * differenceRequest.setIgnoreSpaceChange(true);
 * differenceRequest.setFilePath(filePath);
 * 
 * 
 * 
 * try{
 * CommandExecutor.getInstance().executeCommand(CommandExecutor.diffCommand(
 * differenceRequest), new CVSFileDifferenceListener(exceptionVO));
 * }catch(Exception exp){ exp.printStackTrace(); } }
 * 
 * return exceptionVO;
 * 
 * }
 * 
 * }
 */