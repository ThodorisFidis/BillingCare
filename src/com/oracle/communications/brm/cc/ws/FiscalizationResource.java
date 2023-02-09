package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.AccessDeniedException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.InvalidParametersException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.OperationFailedException_Exception;
import com.rest.sdk.FiscalizationModule;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

@Path("fiscalization")
public class FiscalizationResource {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.FiscalizationResource.class);

    @Context
    HttpServletRequest servletRequest;

    @Path("/individual/directorates-names")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<DirectoratesInfo> getDirectorates() throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getDirectorates");
        List<DirectoratesInfo> result = null;
        try {
            logger.fine("getDirectorates: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            result = fiscalizationModule.getDirectorates();
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Directorates descriptions");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getDirectorates");
        return result;
    }

    @Path("/individual/non-fiscalized-results")
    @GET
    @Produces({"application/xml", "application/json"})
    public ArrayList<IndividualFiscalizationFields> getNonFiscalizedResults(@QueryParam("directorateCode") String directorateCode) throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getNonFiscalizedResults");
        ArrayList<IndividualFiscalizationFields> NonFiscalizedResults = null;
        try {
            logger.fine("getNonFiscalizedResults: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            NonFiscalizedResults = fiscalizationModule.getNonFiscalizedRecords(directorateCode);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Non-Fiscalized Results");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getNonFiscalizedResults");
        return NonFiscalizedResults;
    }

    @Path("/individual/update-non-fiscalized-record")
    @POST
    @Produces({"application/xml", "application/json"})
    public FiscalUpdateResults updateNonFiscalizedRecord(@QueryParam("netAmount") BigDecimal netAmount, @QueryParam("vatAmount") BigDecimal vatAmount, @QueryParam("glId") String glId, @QueryParam("productObj") String productObj, @QueryParam("printerId") String printerId, @QueryParam("poid") String poid, @QueryParam("rsSeqNo") int sequenceNumber) throws ParserConfigurationException, SAXException {
        logger.entering("FiscalizationResource", "updateNonFiscalizedRecord");
        FiscalUpdateResults resultFromUpdateAction = null;
        logger.fine("sequenceNumber: " +sequenceNumber);
        try {
            logger.fine("updateNonFiscalizedRecord: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            resultFromUpdateAction = fiscalizationModule.updateNonFiscalizedRecord(netAmount, vatAmount, glId, productObj, printerId, poid, sequenceNumber);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to update the Non-Fiscalized Result");
            ExceptionHelper.handleException(applicationException);
        }

        logger.exiting("FiscalizationResource", "updateNonFiscalizedRecord");
        return resultFromUpdateAction;
    }

    @Path("/group/get-selection-parameters")
    @GET
    @Produces({"application/xml", "application/json"})
    public ArrayList<Object> getGroupedSelectionParameters() throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getGroupedSelectionParameters");
        ArrayList<Object> groupedFiscalizationParameters = null;
        try {
            logger.fine("getGroupedSelectionParameters: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            groupedFiscalizationParameters = fiscalizationModule.getGroupedSelectionParameters();
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Grouped Fiscalization selection parameters");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getGroupedSelectionParameters");
        return groupedFiscalizationParameters;
    }

    @Path("/group/get-fiscal-items")
    @GET
    @Produces({"application/xml", "application/json"})
    public ArrayList<GroupedFiscalizedFields> getGroupFiscalizationItems(@QueryParam("directorateCode") String directorateCode, @QueryParam("technologyCode") String technologyCode, @QueryParam("accountingPeriod") String accountingPeriod) throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getGroupFiscalizationItems");
        ArrayList<GroupedFiscalizedFields> groupedFiscalizedResults = null;
        try {
            logger.fine("getGroupFiscalizationItems: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            groupedFiscalizedResults = fiscalizationModule.getGroupFiscalizationItems(directorateCode, technologyCode, accountingPeriod);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Group Fiscalization results");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getGroupFiscalizationItems");
        return groupedFiscalizedResults;
    }

    @Path("/group/update-grouped-records")
    @POST
    @Produces({"application/xml", "application/json"})
    public FiscalUpdateResults updateGroupedFiscalizationRecords(@QueryParam("netAmount") BigDecimal netAmount, @QueryParam("description") String description, @QueryParam("endpoint") String endpoint, @QueryParam("accountingPeriod") String accountingPeriod, @QueryParam("taxCode") String taxCode, @QueryParam("itemsCounter") BigDecimal itemsCounter) throws ParserConfigurationException, SAXException, IOException {
        logger.entering("FiscalizationResource", "updateGroupedFiscalizationRecords");
        FiscalUpdateResults resultFromUpdateAction = null;

        try {
            logger.fine("updateGroupedFiscalizationRecords: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            resultFromUpdateAction = fiscalizationModule.updateGroupedFiscalizationRecords(netAmount, description, endpoint, accountingPeriod, taxCode, itemsCounter);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to update the Grouped Fiscalized entries");
            ExceptionHelper.handleException(applicationException);
        }

        logger.exiting("FiscalizationResource", "updateGroupedFiscalizationRecords");
        return resultFromUpdateAction;
    }

    @Path("/group/create-fiscal-items")
    @POST
    @Produces({"application/xml", "application/json"})
    public int createFiscalItems(@QueryParam("accountingPeriod") String accountingPeriod) {
        logger.entering("FiscalizationResource", "createFiscalItems");
        int result = 0;
        try {
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            result = fiscalizationModule.createFiscalItems(accountingPeriod);

        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to create fiscal items");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "createFiscalItems");
        return result;
    }

    @Path("/corrections/get-selection-parameters")
    @GET
    @Produces({"application/xml", "application/json"})
    public ArrayList<Object> getCorrectionsSelectionParameters(@QueryParam("accountingPeriod") String accountingPeriod) throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getCorrectionsSelectionParameters");
        ArrayList<Object> correctSelectionParameters = null;
        try {
            logger.fine("getCorrectionsSelectionParameters: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            correctSelectionParameters = fiscalizationModule.getCorrectionsSelectionParameters(accountingPeriod);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Corrections Fiscalization selection parameters");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getCorrectionsSelectionParameters");
        return correctSelectionParameters;
    }

    @Path("/corrections/create-fiscal-items")
    @POST
    @Produces({"application/xml", "application/json"})
    public int createCorrectionsFiscalItems(@QueryParam("accountingPeriod") String accountingPeriod) {
        logger.entering("FiscalizationResource", "createCorrectionsFiscalItems");
        int result = 0;
        try {
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            result = fiscalizationModule.createCorrectionsFiscalItems(accountingPeriod);

        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to create fiscal items");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "createCorrectionsFiscalItems");
        return result;
    }

    @Path("/corrections/get-corrected-items")
    @GET
    @Produces({"application/xml", "application/json"})
    public ArrayList<CorrectionsUIFields> getCorrectionsFiscalItems(@QueryParam("directorateCode") String directorateCode, @QueryParam("technologyCode") String technologyCode, @QueryParam("accountingPeriod") String accountingPeriod) throws ParserConfigurationException {
        logger.entering("FiscalizationResource", "getCorrectionsFiscalItems");
        ArrayList<CorrectionsUIFields> correctionsGroupedItemsResultList = null;
        try {
            logger.fine("getCorrectionsFiscalItems: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            correctionsGroupedItemsResultList = fiscalizationModule.getCorrectionsFiscalItems(directorateCode, technologyCode, accountingPeriod);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to get the Corrections Fiscalization Results");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "getCorrectionsFiscalItems");
        return correctionsGroupedItemsResultList;
    }

    @Path("/corrections/update-corrections-records")
    @POST
    @Produces({"application/xml", "application/json"})
    public FiscalUpdateResults updateCorrectionsFiscalizationRecords(@QueryParam("amount") BigDecimal amount, @QueryParam("directorateCode") String directorateCode, @QueryParam("technologyCode") String technologyCode, @QueryParam("accountingPeriod") String accountingPeriod, @QueryParam("taxCode") String taxCode, @QueryParam("fiscalNumber") String fiscalNumber, @QueryParam("processingStage") int processingStage, @QueryParam("itemObj") String itemObj, @QueryParam("adjustmentType") String adjustmentType, @QueryParam("adjustmentStatus") int adjustmentStatus, @QueryParam("rsSeqNo") int sequenceNumber, @QueryParam("rsSeqAdjNo") int sequenceNumberAdj) throws ParserConfigurationException, SAXException, IOException {
        logger.entering("FiscalizationResource", "updateCorrectionsFiscalizationRecords");
        FiscalUpdateResults resultFromUpdateAction = null;

        try {
            logger.fine("updateCorrectionsFiscalizationRecords: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            resultFromUpdateAction = fiscalizationModule.updateCorrectionsFiscalizationRecords(amount, directorateCode, technologyCode, accountingPeriod, taxCode, fiscalNumber, processingStage, itemObj, adjustmentType, adjustmentStatus, sequenceNumber, sequenceNumberAdj);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to update the Corrections Fiscalized entries");
            ExceptionHelper.handleException(applicationException);
        }

        logger.exiting("FiscalizationResource", "updateCorrectionsFiscalizationRecords");
        return resultFromUpdateAction;
    }

    @Path("/corrections/cancel-duplicate-corrections-records")
    @POST
    @Produces({"application/xml", "application/json"})
    public FiscalUpdateResults cancelDuplicateCorrectionsFiscalizationRecords(@QueryParam("amount") BigDecimal amount, @QueryParam("directorateCode") String directorateCode, @QueryParam("technologyCode") String technologyCode, @QueryParam("accountingPeriod") String accountingPeriod, @QueryParam("taxCode") String taxCode, @QueryParam("fiscalNumber") String fiscalNumber, @QueryParam("processingStage") int processingStage, @QueryParam("itemObj") String itemObj, @QueryParam("adjustmentType") String adjustmentType, @QueryParam("adjustmentStatus") int adjustmentStatus, @QueryParam("adjFiscalNumber") String adjFiscalNumber, @QueryParam("rsSeqNo") int sequenceNumber, @QueryParam("rsSeqAdjNo") int sequenceNumberAdj) throws ParserConfigurationException, SAXException, IOException {
        logger.entering("FiscalizationResource", "cancelDuplicateCorrectionsFiscalizationRecords");
        FiscalUpdateResults resultFromCancelDuplicateAction = new FiscalUpdateResults();
        try {
            logger.fine("updateCorrectionsFiscalizationRecords: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            resultFromCancelDuplicateAction = fiscalizationModule.cancelDuplicateCorrectionsFiscalizationRecords(amount, directorateCode, technologyCode, accountingPeriod, taxCode, fiscalNumber, processingStage, itemObj, adjustmentType, adjustmentStatus, adjFiscalNumber, sequenceNumber, sequenceNumberAdj);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to cancel duplicate Corrections ");
            ExceptionHelper.handleException(applicationException);
        }

        logger.exiting("FiscalizationResource", "cancelDuplicateCorrectionsFiscalizationRecords");
        return resultFromCancelDuplicateAction;
    }

    @Path("/corrections/reportPdf")
    @GET
    @Produces({"application/pdf"})
    @ApiResponses({
        @ApiResponse(code = 200, message = "PDF for passed accounting period")
        , @ApiResponse(code = 500, message = "Exception containing errors will be raised on failure")})
    public Response getFiscalizationInvoice(@QueryParam("accountingPeriod") String accountingPeriod) throws IOException, FileNotFoundException, AccessDeniedException_Exception, AccessDeniedException_Exception, AccessDeniedException_Exception, OperationFailedException_Exception, InvalidParametersException_Exception, AccessDeniedException_Exception, AccessDeniedException_Exception, InvalidParametersException_Exception {
        if (accountingPeriod == null || accountingPeriod.isEmpty()) {
            Response.ResponseBuilder responseBuilder1 = Response.status(Response.Status.BAD_REQUEST);
            return responseBuilder1.build();
        }
        File file = null;
        String str = "";
        logger.entering("FiscalizationResource", "getFiscalizationInvoice");
        FiscalizationModule fiscalizationModule = new FiscalizationModule();
        file = fiscalizationModule.runFiscalizationReport(accountingPeriod);
        if (file != null && file.getName() != null) {
            str = file.getName();
        }
        logger.exiting("FiscalizationResource", "getFiscalizationInvoice");
        Response.ResponseBuilder responseBuilder = Response.ok(file);
        responseBuilder.header("Content-Disposition", "inline; filename=\"" + str + "\"");
        responseBuilder.header("Content-type", "application/pdf");
        return responseBuilder.build();
    }

    @Path("/corrections/batch-print-corrected-items")
    @GET
    @Produces({"application/xml", "application/json"})
    public int batchPrintCorrectedFiscalItems(@QueryParam("directorateCode") String directorateCode, @QueryParam("technologyCode") String technologyCode, @QueryParam("accountingPeriod") String accountingPeriod) throws ParserConfigurationException, SAXException, IOException {
        logger.entering("FiscalizationResource", "batchPrintCorrectedFiscalItems");
        int printedItemsNumber = 0;
        try {
            logger.fine("batchPrintCorrectedFiscalItems: initializing FiscalizationModule");
            FiscalizationModule fiscalizationModule = new FiscalizationModule();
            printedItemsNumber = fiscalizationModule.batchPrintCorrectedFiscalItems(directorateCode, technologyCode, accountingPeriod);
        } catch (ApplicationException applicationException) {
            logger.severe("FiscalizationResource: Failed to print the Corrected Fiscal Results");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("FiscalizationResource", "batchPrintCorrectedFiscalItems");
        return printedItemsNumber;
    }

}
