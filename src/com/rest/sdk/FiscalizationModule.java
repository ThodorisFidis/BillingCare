package com.rest.sdk;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.common.BaseOps;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.modules.pcm.PCMModuleBase;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseOps;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.ws.CorrectionsUIFields;
import com.oracle.communications.brm.cc.ws.DirectoratesInfo;
import com.oracle.communications.brm.cc.ws.IndividualFiscalizationFields;
import com.oracle.communications.brm.cc.ws.FiscalizationWorker;
import com.oracle.communications.brm.cc.ws.FiscalPrinterResultFields;
import com.oracle.communications.brm.cc.ws.FiscalUpdateResults;
import com.oracle.communications.brm.cc.ws.GroupedFiscalizedFields;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.AccessDeniedException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.ArrayOfParamNameValue;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.ArrayOfString;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.InvalidParametersException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.OperationFailedException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.ParamNameValue;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.PublicReportService;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.PublicReportServiceService;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.ReportRequest;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.ReportResponse;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.PortalContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import weblogic.security.internal.SerializedSystemIni;
import weblogic.security.internal.encryption.ClearOrEncryptedService;
import weblogic.security.internal.encryption.EncryptionService;

public class FiscalizationModule extends PCMModuleBase {

    private static CCLogger logger = CCLogger.getCCLogger(com.rest.sdk.FiscalizationModule.class);

    public List<DirectoratesInfo> getDirectorates() {
        logger.entering("FiscalizationModule", "getDirectorates");
        PortalContext ctx = null;
        BaseOps baseOps = null;
        List<DirectoratesInfo> result;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            result = fiscalWorker.getDistinctDirectorateCodesForIndividualFiscalization();

            logger.exiting("FiscalizationModule", "getDirectorates");
            return result;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the directorates names from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public ArrayList<IndividualFiscalizationFields> getNonFiscalizedRecords(String directorateCode) {
        logger.entering("FiscalizationModule", "getNonFiscalizedRecords");
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ArrayList<IndividualFiscalizationFields> NonFiscalizedItemsResultList;
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            FList outputFList = fiscalWorker.getResultsFlistForIndividualFiscalization(directorateCode);
            logger.fine("The individual fiscalization results Flist is: ", outputFList);
            NonFiscalizedItemsResultList = fiscalWorker.getRecordsForIndividualFiscalization(outputFList);

            logger.exiting("FiscalizationModule", "getNonFiscalizedRecords");
            return NonFiscalizedItemsResultList;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the non-fiscalized records from module ", ex);
            throw new ApplicationException(ex);
        } catch (ParserConfigurationException ex) {
            logger.severe("Unable to retrieve the non-fiscalized  records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public FiscalUpdateResults updateNonFiscalizedRecord(BigDecimal netAmount, BigDecimal vatAmount, String glId, String productObj, String printerId, String poid, int sequenceNumber) throws SAXException {
        logger.entering("FiscalizationModule", "updateNonFiscalizedRecord");
        FiscalPrinterResultFields returnedFieldsFromWs = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            FiscalUpdateResults fiscalUpdateResultsList = null;
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            FList inputFList = new FList();

            if (sequenceNumber == 0) {
                //get the invoice number
                returnedFieldsFromWs = fiscalWorker.printIndividualFiscalizationInvoice(netAmount, vatAmount, glId, productObj, printerId);
                //create the input flist
                inputFList = fiscalWorker.updateIndividualItemInputFlist(returnedFieldsFromWs, poid);
                logger.fine("The input Flist for update is:", inputFList);
            } else if (sequenceNumber != 0) {
                //get the invoice number
                returnedFieldsFromWs = fiscalWorker.getFiscalNumberForRSBySeqNo(sequenceNumber);
                //create the input flist
                inputFList = fiscalWorker.updateIndividualRSItemInputFlist(returnedFieldsFromWs, poid);
                logger.fine("The input Flist for update is: \n", inputFList);
            }

            //call the update opcode 
            fiscalUpdateResultsList = fiscalWorker.updateIndividualItem(inputFList);

            logger.exiting("FiscalizationModule", "updateNonFiscalizedRecord");
            return fiscalUpdateResultsList;
        } catch (EBufException ex) {
            logger.severe("Unable to update the non-fiscalized record from module ", ex);
            throw new ApplicationException(ex);
        } catch (ParserConfigurationException ex) {
            logger.severe("Unable to update the non-fiscalized record from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public ArrayList<Object> getGroupedSelectionParameters() {
        logger.entering("FiscalizationModule", "getGroupedSelectionParameters");
        ArrayList<Object> groupedFiscalizationSelectionParameters = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            groupedFiscalizationSelectionParameters = fiscalWorker.groupedSelectionParameters();

            logger.exiting("FiscalizationModule", "getGroupedSelectionParameters");
            return groupedFiscalizationSelectionParameters;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the grouped fiscalization selection parameters from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }

    }

    public ArrayList<GroupedFiscalizedFields> getGroupFiscalizationItems(String directorateCode, String technologyCode, String accountingPeriod) throws ParserConfigurationException {
        logger.entering("FiscalizationModule", "getGroupFiscalizationItems");
        PortalContext ctx = null;
        BaseOps baseOps = null;
        ArrayList<GroupedFiscalizedFields> groupedData = null;
        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            HashMap<Object, List<BigDecimal>> mapOfBhtGroupedFiscalItems = fiscalWorker.getGroupedDataFromBhtFiscalItem(directorateCode, technologyCode, accountingPeriod);
            groupedData = fiscalWorker.getGroupFiscalizedItems(mapOfBhtGroupedFiscalItems);

            logger.exiting("FiscalizationModule", "getGroupFiscalizationItems");
            return groupedData;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the grouped fiscalization items from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public FiscalUpdateResults updateGroupedFiscalizationRecords(BigDecimal netAmount, String description, String endpoint, String accountingPeriod, String taxCode, BigDecimal itemsCounter) throws SAXException, IOException, ParserConfigurationException {
        logger.entering("FiscalizationModule", "updateGroupedFiscalizationRecords");
        FiscalPrinterResultFields returnedFieldsFromWs = null;
        FiscalUpdateResults result = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            //get the invoice number
            returnedFieldsFromWs = fiscalWorker.printGroupedFiscalizationInvoice(netAmount, taxCode, description, endpoint);
            //update the grouped items
            result = fiscalWorker.invokeUpdateGroupFiscalNoOpcode(returnedFieldsFromWs, description, accountingPeriod, taxCode, itemsCounter);

            logger.exiting("FiscalizationModule", "updateGroupedFiscalizationRecords");
            return result;
        } catch (EBufException ex) {
            logger.severe("Unable to update the grouped fiscalized records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public int createFiscalItems(String accountingPeriod) {
        logger.entering("FiscalizationModule", "createFiscalItems");
        int result = 0;
        PortalContext ctx = null;
        BaseOps baseOps = null;
        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            FList inputFlistForStepSearch = fiscalWorker.createInputFlistForStepSearch(accountingPeriod);
            result = fiscalWorker.invokeSearchAndBulkCreate(inputFlistForStepSearch, accountingPeriod);

            logger.exiting("FiscalizationModule", "createFiscalItems");
            return result;
        } catch (EBufException ex) {
            logger.severe("Unable to create the fiscal items from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public ArrayList<Object> getCorrectionsSelectionParameters(String accountingPeriod) {
        logger.entering("FiscalizationModule", "getCorrectionsSelectionParameters");
        ArrayList<Object> correctionsFiscalizationSelectionParameters = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            correctionsFiscalizationSelectionParameters = fiscalWorker.correctionsSelectionParameters(accountingPeriod);

            logger.exiting("FiscalizationModule", "getCorrectionsSelectionParameters");
            return correctionsFiscalizationSelectionParameters;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the correctections fiscalization selection parameters from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public int createCorrectionsFiscalItems(String accountingPeriod) {
        logger.entering("FiscalizationModule", "createCorrectionsFiscalItems");
        int result = 0;
        PortalContext ctx = null;
        BaseOps baseOps = null;
        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            FList inputFlistForCorrectionsSearch = fiscalWorker.createInputFlistForCorrectionsSearch(accountingPeriod);
            result = fiscalWorker.invokeSearchAndCreateCorrections(inputFlistForCorrectionsSearch, accountingPeriod);

            logger.exiting("FiscalizationModule", "createCorrectionsFiscalItems");
            return result;
        } catch (EBufException ex) {
            logger.severe("Unable to create the corrections fiscal items from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public ArrayList<CorrectionsUIFields> getCorrectionsFiscalItems(String directorateCode, String technologyCode, String accountingPeriod) {
        logger.entering("FiscalizationModule", "getCorrectionsFiscalItems");
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);
            FList inputFlistForCorrectionsSearch = fiscalWorker.createInputFlistForCorrectionsSearch(directorateCode, technologyCode, accountingPeriod);
            FList outputFlistForCorrectionsSearch = fiscalWorker.callSearchOpcodeForCorrections(inputFlistForCorrectionsSearch);
            ArrayList<CorrectionsUIFields> correctionsUngroupedItemsResultList = fiscalWorker.getCorrectionItemsUngrouped(outputFlistForCorrectionsSearch);
            HashMap<Object, Object> mapOfGroupedCorrectionsItems = fiscalWorker.groupCorrections(correctionsUngroupedItemsResultList);
            ArrayList<CorrectionsUIFields> correctionsGroupedItemsResultList = fiscalWorker.getCorrectionsItems(mapOfGroupedCorrectionsItems);

            logger.exiting("FiscalizationModule", "getCorrectionsFiscalItems");
            return correctionsGroupedItemsResultList;
        } catch (EBufException ex) {
            logger.severe("Unable to retrieve the corrections fiscalized records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public FiscalUpdateResults updateCorrectionsFiscalizationRecords(BigDecimal amount, String directorateCode, String technologyCode, String accountingPeriod, String taxCode, String fiscalNumber, int processingStage, String itemObj, String adjustmentType, int adjustmentStatus, int sequenceNumber, int sequenceNumberAdj) throws SAXException, IOException, ParserConfigurationException {
        logger.entering("FiscalizationModule", "updateCorrectionsFiscalizationRecords");
        FiscalPrinterResultFields returnedFieldsFromWs = null;
        FiscalUpdateResults fiscalUpdateResults = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);

            //check if the printed item is grouped or individual fiscalized or fiscalized in rs
            //if it is fiscalized in RS then sequenceNumber != 0 if it is fiscalized in FBiH then sequenceNumber = 0
            if (sequenceNumber != 0 && adjustmentStatus != 3) {
                //find required information of fiscalized items
                List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(itemObj);
                String glId = fields.get(0);
                String description = fields.get(1);
                String orderNumber = fields.get(2);
                //String userCode = fields.get(3);
                
                //calling the ws to get printer id
                List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(directorateCode, technologyCode);
                String cashRegisterId = printerIdAndEndpoint.get(0);
                
                int typeOfRequest = 0;
                if (adjustmentType.equals("Storno")) {
                    typeOfRequest = 2;
                }

                //call web service in order to get the fiscal number
                returnedFieldsFromWs = fiscalWorker.printFiscalBillForRS(amount, taxCode, glId, description, orderNumber, cashRegisterId, typeOfRequest, fiscalNumber);

                //invoke update opcode for the correction items
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, sequenceNumber, sequenceNumberAdj);
            } else if (sequenceNumber != 0 && adjustmentStatus == 3) {
                //get the invoice number
                returnedFieldsFromWs = fiscalWorker.getFiscalNumberForRSBySeqNo(sequenceNumberAdj);
                //update the correction items
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, sequenceNumber, sequenceNumberAdj);

            } else if (sequenceNumber == 0 && processingStage == 1) {
                 //find required information of fiscalized items
                List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(itemObj);
                String glId = fields.get(0);
                String description = fields.get(1);
                String orderNumber = fields.get(2);
                String userCode = fields.get(3);
                String printerId = fiscalWorker.getPrinterIdFromWs(userCode, orderNumber);
                //get the invoice number
                int typeOfRequest = 0;
                if (adjustmentType.equals("Storno")) {
                    typeOfRequest = 2;
                }

                 //call web service in order to get the fiscal number
                returnedFieldsFromWs = fiscalWorker.printIndividualFiscalizationInvoiceForCorrections(amount, taxCode, glId, description, printerId, typeOfRequest);

                //invoke update opcode for the correction items
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, sequenceNumber, sequenceNumberAdj);

            } else if (sequenceNumber == 0 && processingStage == 2) {
                 //find required information of fiscalized items
                List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(directorateCode, technologyCode);
                String printerId = printerIdAndEndpoint.get(0);
                String endpoint = printerIdAndEndpoint.get(1);
                String description = "Billing - dir " + directorateCode + " teh-" + technologyCode;
                //get the invoice number
                int typeOfRequest = 0;
                if (adjustmentType.equals("Storno")) {
                    typeOfRequest = 2;
                }

                 //call web service in order to get the fiscal number
                returnedFieldsFromWs = fiscalWorker.printGroupedCorrectionsFiscalizationInvoice(amount, taxCode, description, endpoint, typeOfRequest);

                //invoke update opcode for the correction items
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, sequenceNumber, sequenceNumberAdj);
            }

            logger.exiting("FiscalizationModule", "updateCorrectionsFiscalizationRecords");
            return fiscalUpdateResults;
        } catch (EBufException ex) {
            logger.severe("Unable to update the corrections fiscalized records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public FiscalUpdateResults cancelDuplicateCorrectionsFiscalizationRecords(BigDecimal amount, String directorateCode, String technologyCode, String accountingPeriod, String taxCode, String fiscalNumber, int processingStage, String itemObj, String adjustmentType, int adjustmentStatus, String adjFiscalNumber, int sequenceNumber, int sequenceNumberAdj) throws SAXException, IOException, ParserConfigurationException {
        logger.entering("FiscalizationModule", "cancelDuplicateCorrectionsFiscalizationRecords");
        FiscalPrinterResultFields returnedFieldsFromWs = null;
        FiscalUpdateResults fiscalUpdateResults = null;
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);

            //check if the printed item is grouped or individual fiscalized or fiscalized in rs
            //if it is fiscalized in RS then sequenceNumber != 0 if it is fiscalized in FBiH then sequenceNumber = 0
            if (sequenceNumber != 0 && adjustmentStatus != 3) {
                //find required information of fiscalized items
                List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(itemObj);
                String glId = fields.get(0);
                String description = fields.get(1);
                String orderNumber = fields.get(2);
                //String userCode = fields.get(3);
                
                
                //calling the ws to get printer id
                List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(directorateCode, technologyCode);
                String cashRegisterId = printerIdAndEndpoint.get(0);
                
                int typeOfRequest = 0;
                if (adjustmentType.equals("Povećanje")) {
                    typeOfRequest = 2;
                }

                //call web service in order to get the fiscal number
                returnedFieldsFromWs = fiscalWorker.printFiscalBillForRS(amount, taxCode, glId, description, orderNumber, cashRegisterId, typeOfRequest, fiscalNumber);

                //invoke update opcode for cancel duplicate action
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, adjFiscalNumber, sequenceNumber, sequenceNumberAdj);
           
            } else if (sequenceNumber == 0 && processingStage == 1) { //processing stage = 1 means that is individual fiscalized
                List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(itemObj);
                String glId = fields.get(0);
                String description = fields.get(1);
                String orderNumber = fields.get(2);
                String userCode = fields.get(3);
                String printerId = fiscalWorker.getPrinterIdFromWs(userCode, orderNumber);
                //get the invoice number
                int typeOfRequest = 0;
                if (adjustmentType.equals("Povećanje")) {
                    typeOfRequest = 2;
                }
                //call the web service for printing the individual correction record
                returnedFieldsFromWs = fiscalWorker.printIndividualFiscalizationInvoiceForCorrections(amount, taxCode, glId, description, printerId, typeOfRequest);
                
                //invoke update opcode for cancel duplicate action
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, adjFiscalNumber, sequenceNumber, sequenceNumberAdj);

            } else if (sequenceNumber == 0 && processingStage == 2) {  //processing stage = 2 means that is grouped fiscalized 
                List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(directorateCode, technologyCode);
                String printerId = printerIdAndEndpoint.get(0);
                String endpoint = printerIdAndEndpoint.get(1);
                String description = "Billing - dir " + directorateCode + " teh-" + technologyCode;
                //get the invoice number
                int typeOfRequest = 0;
                if (adjustmentType.equals("Povećanje")) {
                    typeOfRequest = 2;
                }
                
                //call the web service for printing the grouped correction record
                returnedFieldsFromWs = fiscalWorker.printGroupedCorrectionsFiscalizationInvoice(amount, taxCode, description, endpoint, typeOfRequest);
                
                //update the corrected items
                fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate(returnedFieldsFromWs, directorateCode, technologyCode, taxCode, fiscalNumber, accountingPeriod, adjustmentType, adjustmentStatus, adjFiscalNumber, sequenceNumber, sequenceNumberAdj);

            }


            logger.exiting("FiscalizationModule", "cancelDuplicateCorrectionsFiscalizationRecords");
            return fiscalUpdateResults;
        } catch (EBufException ex) {
            logger.severe("Unable to cancel duplicate of corrections fiscalized records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public int batchPrintCorrectedFiscalItems(String directorateCode, String technologyCode, String accountingPeriod) throws ParserConfigurationException, SAXException, IOException {
        logger.entering("FiscalizationModule", "batchPrintCorrectedFiscalItems");
        PortalContext ctx = null;
        BaseOps baseOps = null;
        FiscalUpdateResults fiscalUpdateResults = null;
        int printedItemsNumber = 0;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            FiscalizationWorker fiscalWorker = new FiscalizationWorker();
            fiscalWorker.setBaseOps(baseOps);

            FList inputFlistForCorrectionsSearch = fiscalWorker.createInputFlistForCorrectionsBatchPrintSearch(directorateCode, technologyCode, accountingPeriod);
            FList outputFlistForCorrectionsSearch = fiscalWorker.callSearchOpcodeForCorrections(inputFlistForCorrectionsSearch);
            ArrayList<CorrectionsUIFields> correctionsUngroupedResultList = fiscalWorker.getCorrectionItemsUngrouped(outputFlistForCorrectionsSearch);
            HashMap<Object, Object> mapOfGroupedCorrectionsItems = fiscalWorker.groupCorrections(correctionsUngroupedResultList);
            ArrayList<CorrectionsUIFields> groupedIemsList = fiscalWorker.getCorrectionsItems(mapOfGroupedCorrectionsItems);

            for (CorrectionsUIFields item : groupedIemsList) {
                FiscalPrinterResultFields returnedFieldsFromWs = null;

                //check if the printed item is grouped(2) or individual fiscalized(1) or if the item is fiscalized in rs
                if (item.getRsSeqNo() != 0 && item.getAdjustmentStatus() != 3) {
                    List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(item.getItemObj());
                    String glId = fields.get(0);
                    String description = fields.get(1);
                    String orderNumber = fields.get(2);
                    //String userCode = fields.get(3);
                    
                    //calling the ws to get printer id
                    List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(item.getDirectorateCode(), item.getTechnologyCode());
                    String cashRegisterId = printerIdAndEndpoint.get(0);

                    int typeOfRequest = 0;
                    if (item.getAdjustmentType().equals("Storno")) {
                        typeOfRequest = 2;
                    }
                    returnedFieldsFromWs = fiscalWorker.printFiscalBillForRS(item.getAmount(), item.getTaxCode(), glId, description, orderNumber, cashRegisterId, typeOfRequest, item.getFiscalNumber());

                    fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, item.getDirectorateCode(), item.getTechnologyCode(), item.getTaxCode(), item.getFiscalNumber(), accountingPeriod, item.getAdjustmentType(), item.getAdjustmentStatus(), item.getRsSeqNo(), item.getRsSeqAdjNo());
                    printedItemsNumber += parseInt(fiscalUpdateResults.getMessage());
                    
                } else if (item.getRsSeqNo() != 0 && item.getAdjustmentStatus() == 3) {
                    
                    //get the invoice number
                    returnedFieldsFromWs = fiscalWorker.getFiscalNumberForRSBySeqNo(item.getRsSeqAdjNo());                  
                    //update the correction items
                    fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, item.getDirectorateCode(), item.getTechnologyCode(), item.getTaxCode(), item.getFiscalNumber(), accountingPeriod, item.getAdjustmentType(), item.getAdjustmentStatus(), item.getRsSeqNo(), item.getRsSeqAdjNo());
                    printedItemsNumber += parseInt(fiscalUpdateResults.getMessage());
                    
                } else if (item.getRsSeqNo() == 0 && item.getProcessingStage() == 1) {
                    List<String> fields = fiscalWorker.getIndividualItemInformationForCorrectionPrint(item.getItemObj());
                    String glId = fields.get(0);
                    String description = fields.get(1);
                    String orderNumber = fields.get(2);
                    String userCode = fields.get(3);
                    String printerId = fiscalWorker.getPrinterIdFromWs(userCode, orderNumber);

                    //get the invoice number
                    int typeOfRequest = 0;
                    if (item.getAdjustmentType().equals("Storno")) {
                        typeOfRequest = 2;
                    }
                    returnedFieldsFromWs = fiscalWorker.printIndividualFiscalizationInvoiceForCorrections(item.getAmount(), item.getTaxCode(), glId, description, printerId, typeOfRequest);

                    //update the correction items
                    fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, item.getDirectorateCode(), item.getTechnologyCode(), item.getTaxCode(), item.getFiscalNumber(), accountingPeriod, item.getAdjustmentType(), item.getAdjustmentStatus(), item.getRsSeqNo(), item.getRsSeqAdjNo());
                    printedItemsNumber += parseInt(fiscalUpdateResults.getMessage());

                } else if (item.getRsSeqNo() == 0 && item.getProcessingStage() == 2) {
                    List<String> printerIdAndEndpoint = fiscalWorker.getPrinterIdEndpointFromWs(item.getDirectorateCode(), item.getTechnologyCode());
                    String printerId = printerIdAndEndpoint.get(0);
                    String endpoint = printerIdAndEndpoint.get(1);
                    String description = "Billing - dir " + item.getDirectorateCode() + " teh-" + item.getTechnologyCode();

                    //get the invoice number
                    int typeOfRequest = 0;
                    if (item.getAdjustmentType().equals("Storno")) {
                        typeOfRequest = 2;
                    }
                    returnedFieldsFromWs = fiscalWorker.printGroupedCorrectionsFiscalizationInvoice(item.getAmount(), item.getTaxCode(), description, endpoint, typeOfRequest);

                    //update the correction items
                    fiscalUpdateResults = fiscalWorker.invokeFiscalUpdateAdjFiscalNoOpcode(returnedFieldsFromWs, item.getDirectorateCode(), item.getTechnologyCode(), item.getTaxCode(), item.getFiscalNumber(), accountingPeriod, item.getAdjustmentType(), item.getAdjustmentStatus(), item.getRsSeqNo(), item.getRsSeqAdjNo());
                    printedItemsNumber += parseInt(fiscalUpdateResults.getMessage());
                }
            }

            logger.exiting("FiscalizationModule", "batchPrintCorrectedFiscalItems");
            return printedItemsNumber;
        } catch (EBufException ex) {
            logger.severe("Unable to print the corrections fiscalization records from module ", ex);
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

    public File runFiscalizationReport(String accountingPeriod) throws FileNotFoundException, IOException, AccessDeniedException_Exception, InvalidParametersException_Exception, OperationFailedException_Exception {
        FileInputStream fileInputStream = null;
        String str1 = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        try {
            str1 = BRMUtility.getPropertyValue("BIP_URL");
            if (str1 == null) {
                throw new ApplicationException("Property BIP_URL missing in Infranet.Properties file or wallet ");
            }
            str2 = BRMUtility.getPropertyValue("BIP_USERID");
            if (str2 == null) {
                throw new ApplicationException("Property BIP_USERID missing in Infranet.Properties file or wallet ");
            }
            str3 = BRMUtility.getPropertyValue("BIP_PASSWORD");
            if (str3 == null) {
                throw new ApplicationException("Property BIP_PASSWORD missing in Infranet.Properties file or wallet ");
            }
            str4 = decryptPassword(str3);
        } catch (ApplicationException applicationException) {
            logger.severe("Unable to load the property file", (Throwable) applicationException);
            throw applicationException;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException iOException) {
                    logger.severe("Unable to close the stream ", iOException);
                }
            }
        }
        QName qName = new QName("http://xmlns.oracle.com/oxp/service/v11/PublicReportService", "PublicReportServiceService");
        PublicReportServiceService publicReportServiceService = new PublicReportServiceService(new URL(str1), qName);
        PublicReportService publicReportService = publicReportServiceService.getPublicReportServiceV11();
        ParamNameValue paramNameValue = new ParamNameValue();
        ArrayOfString arrayOfString = new ArrayOfString();
        arrayOfString.getItem().add(accountingPeriod);
        paramNameValue.setName("P_ACCOUNTING_PERIOD");
        paramNameValue.setValues(arrayOfString);
        ArrayOfParamNameValue arrayOfParamNameValue = new ArrayOfParamNameValue();
        arrayOfParamNameValue.getItem().add(paramNameValue);
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setReportAbsolutePath("BRM_Reports/BHT/FiscalItemsCorrections.xdo");
        reportRequest.setSizeOfDataChunkDownload(-1);
        reportRequest.setParameterNameValues(arrayOfParamNameValue);
        reportRequest.setAttributeFormat("pdf");
        ReportResponse reportResponse = publicReportService.runReport(reportRequest, str2, str4);
        byte[] arrayOfByte = reportResponse.getReportBytes();
        File file = new File("BRM_Fiscalization_Corrections_Report-" + accountingPeriod + ".pdf");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(arrayOfByte);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        return file;
    }

    public String decryptPassword(String password) {
        EncryptionService encryptionService = SerializedSystemIni.getEncryptionService();
        ClearOrEncryptedService clearOrEncryptedService = new ClearOrEncryptedService(encryptionService);
        return clearOrEncryptedService.decrypt(password);
    }
}
