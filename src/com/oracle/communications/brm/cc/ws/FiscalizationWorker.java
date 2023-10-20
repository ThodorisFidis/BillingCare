package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.enums.ErrorConstants;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseWorker;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.portal.pcm.ArrayField;
import com.portal.pcm.EBufException;
import static com.portal.pcm.Element.ELEMID_ANY;
import static com.portal.pcm.Element.ELEMID_ASSIGN;
import com.portal.pcm.EnumField;
import com.portal.pcm.FList;
import com.portal.pcm.Field;
import com.portal.pcm.IntField;
import com.portal.pcm.Poid;
import com.portal.pcm.PoidField;
import com.portal.pcm.SparseArray;
import com.portal.pcm.StrField;
import com.portal.pcm.TStampField;
import com.portal.pcm.fields.FldAccountNo;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldAdjustmentInfo;
import com.portal.pcm.fields.FldAdjustmentType;
import com.portal.pcm.fields.FldAmount;
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldCommonValues;
import com.portal.pcm.fields.FldCompany;
import com.portal.pcm.fields.FldCount;
import com.portal.pcm.fields.FldDescr;
import com.portal.pcm.fields.FldEndT;
import com.portal.pcm.fields.FldEventObj;
import com.portal.pcm.fields.FldEvents;
import com.portal.pcm.fields.FldExternalUser;
import com.portal.pcm.fields.FldFirstName;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldItemObj;
import com.portal.pcm.fields.FldLastName;
import com.portal.pcm.fields.FldLogin;
import com.portal.pcm.fields.FldName;
import com.portal.pcm.fields.FldNameinfo;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldProcessingStage;
import com.portal.pcm.fields.FldProductObj;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldServiceObj;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldTaxCode;
import com.portal.pcm.fields.FldTemplate;
import com.portal.pcm.fields.FldValues;
import customfields.BhtFldAccountingPeriod;
import customfields.BhtFldDeviceSaleItemObj;
import customfields.BhtFldDirectorate;
import customfields.BhtFldDirectorateCode;
import customfields.BhtFldFiscalItemObj;
import customfields.BhtFldFiscalNo;
import customfields.BhtFldFscAdjNo;
import customfields.BhtFldFscAdjStatus;
import customfields.BhtFldFscPrintStatus;
import customfields.BhtFldGlCode;
import customfields.BhtFldInstallmentPlanObj;
import customfields.BhtFldOrderItemNum;
import customfields.BhtFldRsSeqNo;
import customfields.BhtFldSalesDate;
import customfields.BhtFldSalesDirectorate;
import customfields.BhtFldTechnologyCode;
import customfields.BhtFldTransferDate;
import customfields.BhtFldUpdateDate;
import customfields.BhtFldUserCode;
import customfields.BhtFldVatAmount;
import customfields.BhtFldRsSeqAdjNo;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FiscalizationWorker extends PCMBaseWorker {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.FiscalizationWorker.class);

    public FiscalizationWorker() {
    }

    //this method builds the search flist for retrieving the directorate codes with non-fiscalized items
    public List<DirectoratesInfo> getDistinctDirectorateCodesForIndividualFiscalization() {
        logger.entering("FiscalizationWorker", "getDistinctDirectorateCodesForIndividualFiscalization");
        FList inputFlist = new FList();
        long dbNumber = getCurrentDB();
        int argsCount = 1;
        List<DirectoratesInfo> directoratesInfoList = new ArrayList();
        try {
            //construct the search input flist
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
            inputFlist.set((IntField) FldFlags.getInst(), 1280);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /bht_commission_sale 1, /bht_installment_plan 2 where 1.F1 is null and 1.F2 = V2 and 1.F3 = 2.F4 and ( 2.F5 = V5 or exists (select 1 from item_t where item_t.poid_id0=bht_installment_plan_t.device_sale_item_obj_id0 and item_t.status=1 ) or 1.F6 != V6 ) ");
            inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            SparseArray sparseArrayResults = new SparseArray();
            FList directorateCodeFlist = new FList();
            directorateCodeFlist.set((StrField) BhtFldSalesDirectorate.getInst(), "");
            sparseArrayResults.add(0, directorateCodeFlist);
            inputFlist.set((ArrayField) FldResults.getInst(), sparseArrayResults);
            //set Args list
            SparseArray sparseArrayArgs = new SparseArray();
            FList FiscalNoFlist = new FList();
            FiscalNoFlist.set((StrField) BhtFldFiscalNo.getInst(), "");
            sparseArrayArgs.add(argsCount, FiscalNoFlist);
            argsCount++;
            FList FldStatusFlist = new FList();
            FldStatusFlist.set((EnumField) FldStatus.getInst(), 1);
            sparseArrayArgs.add(argsCount, FldStatusFlist);
            argsCount++;
            FList BhtFldInstallmentPlanFlist = new FList();
            BhtFldInstallmentPlanFlist.set((PoidField) BhtFldInstallmentPlanObj.getInst());
            sparseArrayArgs.add(argsCount, BhtFldInstallmentPlanFlist);
            argsCount++;
            FList FldPoidFlist = new FList();
            FldPoidFlist.set((PoidField) FldPoid.getInst());
            sparseArrayArgs.add(argsCount, FldPoidFlist);
            argsCount++;
            FList FldDeviceSaleItemObjFlist = new FList();
            FldDeviceSaleItemObjFlist.set((PoidField) BhtFldDeviceSaleItemObj.getInst());
            sparseArrayArgs.add(argsCount, FldDeviceSaleItemObjFlist);
            argsCount++;
            FList FldRsSeqNoFlist = new FList();
            FldRsSeqNoFlist.set((IntField) BhtFldRsSeqNo.getInst(), 0);
            sparseArrayArgs.add(argsCount, FldRsSeqNoFlist);
            inputFlist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);
            logger.fine("Flist in order to get the distinct directorate codes is == " + inputFlist);

            //invoking the search opcode in order to get the distinct directorate codes
            FList distinctDirectoratesFlist = opcode(7, inputFlist);
            logger.fine("The flist with the distinct directorate codes is == " + distinctDirectoratesFlist);

            //get the distinct directorate codes from the flist 
            ArrayList<String> directorates = new ArrayList();
            if (distinctDirectoratesFlist.hasField((Field) FldResults.getInst())) {
                SparseArray array = distinctDirectoratesFlist.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fList = enumeration.nextElement();
                        directorates.add(fList.get((StrField) BhtFldSalesDirectorate.getInst()));
                    }
                }
            }

            if (!directorates.isEmpty()) {
                //call method per directorate in order to get their description
                for (int i = 0; i < directorates.size(); i++) {
                    try {
                        directoratesInfoList.add(getDirectoratesDescription(directorates.get(i)));
                    } catch (Exception exception) {
                        logger.severe("FiscalizationWorker, Couldn't find the directorate's " + directorates.get(i) + " name ", exception);
                        ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Can not find the name of directorate " + directorates.get(i), new Object[0]);
                    }
                }
            }

            logger.exiting("FiscalizationWorker", "getDistinctDirectorateCodesForIndividualFiscalization");

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }

        return directoratesInfoList;
    }

    //this method constructs the search flist in order to find the directorate name
    public DirectoratesInfo getDirectoratesDescription(String directorateCode) {
        logger.entering("FiscalizationWorker", "getDirectoratesDescription");

        FList inputFlist = new FList();
        long dbNumber = getCurrentDB();
        DirectoratesInfo directoratesInfo = new DirectoratesInfo();

        try {
            //construct the search input flist
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
            inputFlist.set((IntField) FldFlags.getInst(), 512);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /config/bht_directorate where F1 = V1 ");
            inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            //results
            SparseArray sparseArrayResults = new SparseArray();
            FList resultsFlist = new FList();
            resultsFlist.set((StrField) BhtFldDirectorateCode.getInst(), "");
            resultsFlist.set((StrField) FldName.getInst(), "");
            sparseArrayResults.add(ELEMID_ANY, resultsFlist);
            FList resultsFlist2 = new FList();
            resultsFlist2.set((ArrayField) BhtFldDirectorate.getInst(), sparseArrayResults);
            inputFlist.setElement((ArrayField) FldResults.getInst(), 0, resultsFlist2);
            //arguments
            FList BhtFldDirectorateCodeFlist = new FList();
            BhtFldDirectorateCodeFlist.set((StrField) BhtFldDirectorateCode.getInst(), directorateCode);
            SparseArray sparseArrayArgs = new SparseArray();
            sparseArrayArgs.add(0, BhtFldDirectorateCodeFlist);
            FList BhtFldDirectoratesFlist = new FList();
            BhtFldDirectoratesFlist.set((ArrayField) BhtFldDirectorate.getInst(), sparseArrayArgs);
            inputFlist.setElement((ArrayField) FldArgs.getInst(), 1, BhtFldDirectoratesFlist);

            logger.fine("The search flist per directorate code is == " + inputFlist);
            FList distinctDirectoratesNamesFlist = opcode(7, inputFlist);
            logger.fine("The flist containing directorates name(description) is == " + distinctDirectoratesNamesFlist);

            //parse the distinctDirectoratesNamesFlist in order to get the directorate name
            if (distinctDirectoratesNamesFlist.hasField((Field) FldResults.getInst())) {
                SparseArray array = distinctDirectoratesNamesFlist.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fldDirectoratesFlist = enumeration.nextElement();
                        if (fldDirectoratesFlist.hasField((Field) BhtFldDirectorate.getInst())) {
                            SparseArray array2 = fldDirectoratesFlist.get(BhtFldDirectorate.getInst());
                            if (array2 != null) {
                                Enumeration<FList> enumeration2 = array2.getValueEnumerator();
                                while (enumeration2.hasMoreElements()) {
                                    FList fldDirectoratesFlistValues = enumeration2.nextElement();
                                    //add the name and directorate code values to directoratesInfo object
                                    directoratesInfo.setDirectorateName(fldDirectoratesFlistValues.get(FldName.getInst()));
                                    directoratesInfo.setDirectorateCode(fldDirectoratesFlistValues.get(BhtFldDirectorateCode.getInst()));
                                }
                            }
                        } else {
                            logger.severe("FiscalizationWorker, Name for directorate code: " + directorateCode + " does not exist ");
                            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Name for directorate code: " + directorateCode + " doesn't not exist", new Object[0]);

                        }
                    }
                }
            } else {
                logger.severe("FiscalizationWorker, name for directorate code: " + directorateCode + " does not exist");
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Name for directorate code: " + directorateCode + " doesn't not exist", new Object[0]);
            }

            logger.exiting("FiscalizationWorker", "getDirectoratesDescription");

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Couldn't find the directorate's " + directorateCode + " name ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Can not find the name of directorate " + directorateCode, new Object[0]);
        }
        return directoratesInfo;
    }

    //this method builds the flist needed for searching the entries with unissued fiscal invoices and calls the search opcode
    public FList getResultsFlistForIndividualFiscalization(String directorateCode) {
        logger.entering("FiscalizationWorker", "getResultsFlistForIndividualFiscalization");

        FList inputFlist = new FList();
        long l = getCurrentDB();
        int resultsCounter = 0;
        byte argsCounter = 1;
        try {
            //construct the search input flist
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
            inputFlist.set((IntField) FldFlags.getInst(), 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /bht_commission_sale 1, /bht_installment_plan 2 where 1.F1 is null and 1.F2 = V2 and 1.F3 = V3 and 1.F4 = 2.F5 and ( 2.F6 = V6 or exists (select 1 from item_t where item_t.poid_id0=bht_installment_plan_t.device_sale_item_obj_id0 and item_t.status=1 ) or 1.F7 != V7 ) ");
            inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            //set Results list values
            SparseArray resultsSparseArray = new SparseArray();
            FList resultsFlist = new FList();
            resultsFlist.set((Field) FldPoid.getInst());
            resultsFlist.set((Field) BhtFldSalesDirectorate.getInst());
            resultsFlist.set((Field) BhtFldUserCode.getInst());
            resultsFlist.set((Field) FldAccountObj.getInst());
            resultsFlist.set((Field) FldProductObj.getInst());
            resultsFlist.set((Field) BhtFldSalesDate.getInst());
            resultsFlist.set((Field) FldServiceObj.getInst());
            resultsFlist.set((Field) BhtFldOrderItemNum.getInst());
            resultsFlist.set((Field) FldAmount.getInst());
            resultsFlist.set((Field) BhtFldGlCode.getInst());
            resultsFlist.set((Field) BhtFldVatAmount.getInst());
            resultsFlist.set((Field) FldEventObj.getInst());
            resultsFlist.set((Field) BhtFldRsSeqNo.getInst());
            resultsSparseArray.add(resultsCounter, resultsFlist);
            inputFlist.set((ArrayField) FldResults.getInst(), resultsSparseArray);
            //set Args list
            SparseArray ArgsSparseArray = new SparseArray();
            FList BhtFldFiscalNoFlist = new FList();
            BhtFldFiscalNoFlist.set((StrField) BhtFldFiscalNo.getInst(), "");
            ArgsSparseArray.add(argsCounter, BhtFldFiscalNoFlist);
            argsCounter++;
            FList FldStatusFlist = new FList();
            FldStatusFlist.set((EnumField) FldStatus.getInst(), 1);
            ArgsSparseArray.add(argsCounter, FldStatusFlist);
            argsCounter++;
            FList BhtFldDirectorateCodeFlist = new FList();
            BhtFldDirectorateCodeFlist.set((StrField) BhtFldSalesDirectorate.getInst(), directorateCode);
            ArgsSparseArray.add(argsCounter, BhtFldDirectorateCodeFlist);
            argsCounter++;
            FList BhtFldInstallmentPlanFlist = new FList();
            BhtFldInstallmentPlanFlist.set((PoidField) BhtFldInstallmentPlanObj.getInst());
            ArgsSparseArray.add(argsCounter, BhtFldInstallmentPlanFlist);
            argsCounter++;
            FList FldPoidFlist = new FList();
            FldPoidFlist.set((PoidField) FldPoid.getInst());
            ArgsSparseArray.add(argsCounter, FldPoidFlist);
            argsCounter++;
            FList FldDeviceSaleItemObjFlist = new FList();
            FldDeviceSaleItemObjFlist.set((PoidField) BhtFldDeviceSaleItemObj.getInst());
            ArgsSparseArray.add(argsCounter, FldDeviceSaleItemObjFlist);
            argsCounter++;
            FList FldRsSeqNoFlist = new FList();
            FldRsSeqNoFlist.set((IntField) BhtFldRsSeqNo.getInst(), 0);
            ArgsSparseArray.add(argsCounter, FldRsSeqNoFlist);
            inputFlist.set((ArrayField) FldArgs.getInst(), ArgsSparseArray);

            logger.fine("The input flist in order to find the individual items is:" + inputFlist);
            //invoking the search opcode in order to get the individual non fiscalized entries 
            FList outputfList = opcode(7, inputFlist);

            logger.exiting("FiscalizationWorker", "getResultsFlistForIndividualFiscalization");
            return outputfList;

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Problem while invoking the search opcode", new Object[0]);
            return null;
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, An error occured while building the non-fiscalized items search flist ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            return null;
        }

    }

    //this method retrieves data for the non fiscalized records from flist
    public ArrayList<IndividualFiscalizationFields> getRecordsForIndividualFiscalization(FList getfList) throws ParserConfigurationException, EBufException {
        logger.entering("FiscalizationWorker", "getRecordsForIndividualFiscalization");
        ArrayList<IndividualFiscalizationFields> arrayListOfEntries = new ArrayList();
        String firstName = null;
        String lastName = null;
        String company = null;

        //parse the main flist(getfList) with non-fiscalized items 
        if (getfList.hasField((Field) FldResults.getInst())) {
            SparseArray array = getfList.get(FldResults.getInst());
            IndividualFiscalizationFields individualFiscalizationFields = new IndividualFiscalizationFields();
            if (array != null) {
                Enumeration<FList> enumeration = array.getValueEnumerator();
                while (enumeration.hasMoreElements()) {
                    FList fList = enumeration.nextElement();
                    Poid poidFldAccountObj = fList.get((PoidField) FldAccountObj.getInst());
                    Poid poidFldProductObj = fList.get((PoidField) FldProductObj.getInst());
                    Poid poidFldServiceObj = fList.get((PoidField) FldServiceObj.getInst());

                    //read service object
                    if (!poidFldServiceObj.isNull()) {
                        FList readServiceFlist = readObject(poidFldServiceObj);
                        if (readServiceFlist.hasField((Field) FldLogin.getInst())) {
                            individualFiscalizationFields.setUserId(readServiceFlist.get((StrField) FldLogin.getInst()));
                        }
                    } else {
                        individualFiscalizationFields.setUserId("");
                    }

                    //read product object
                    FList readProductFlist = readObject(poidFldProductObj); //FldDescr
                    individualFiscalizationFields.setProductObj(readProductFlist.get((StrField) FldDescr.getInst()));

                    //read account object in order to get the accountNo
                    FList readAccountObjectFlist = readObject(poidFldAccountObj);

                    // get first name, last name from accountObject
                    if (readAccountObjectFlist.hasField((Field) FldNameinfo.getInst())) {
                        SparseArray arrayForObject = readAccountObjectFlist.get(FldNameinfo.getInst());
                        if (arrayForObject != null) {
                            Enumeration<FList> enumerationObject = arrayForObject.getValueEnumerator();
                            while (enumerationObject.hasMoreElements()) {
                                FList flistForObject = enumerationObject.nextElement();
                                firstName = flistForObject.get((StrField) FldFirstName.getInst());
                                lastName = flistForObject.get((StrField) FldLastName.getInst());
                                company = flistForObject.get((StrField) FldCompany.getInst());
                            }
                        }
                    } else {
                        logger.severe("FiscalizationWorker, couldn't find name information ");
                        ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "application couldn't find all information needed, regarding the FldNameinfo", new Object[0]);
                    }

                    individualFiscalizationFields.setCustomerName(firstName + " " + lastName);
                    individualFiscalizationFields.setOrderNumber(fList.get((StrField) BhtFldOrderItemNum.getInst())); //BhtFldOrderItemNum -> order number
                    individualFiscalizationFields.setAccountNo(readAccountObjectFlist.get((StrField) FldAccountNo.getInst()));
                    individualFiscalizationFields.setDirCode(fList.get((StrField) BhtFldSalesDirectorate.getInst()));
                    //searching for external user
                    Poid eventObjectId = fList.get((PoidField) FldEventObj.getInst());
                    individualFiscalizationFields.setUserCode(UserActivitySearch(eventObjectId));
                    individualFiscalizationFields.setCreationDate(convert(fList.get((TStampField) BhtFldSalesDate.getInst())));
                    individualFiscalizationFields.setNetAmount(fList.get(FldAmount.getInst()));
                    individualFiscalizationFields.setGlId(fList.get(BhtFldGlCode.getInst()));
                    individualFiscalizationFields.setVatAmount(fList.get(BhtFldVatAmount.getInst()));
                    individualFiscalizationFields.setPoid(BRMUtility.restIdFromPoid(fList.get(FldPoid.getInst())));
                    individualFiscalizationFields.setRsSeqNo(fList.get(BhtFldRsSeqNo.getInst()));

                    if (individualFiscalizationFields.getRsSeqNo() == 0) {
                        individualFiscalizationFields.setPrinterId(getPrinterIdFromWs(individualFiscalizationFields.getUserCode(), individualFiscalizationFields.getOrderNumber()));

                        if (individualFiscalizationFields.getPrinterId() == null) {
                            logger.severe("FiscalizationWorker, printer id has null value ");
                            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "application couldn't find all information needed, printer id has null value", new Object[0]);
                        }

                    }

                    arrayListOfEntries.add(individualFiscalizationFields);
                    individualFiscalizationFields = new IndividualFiscalizationFields();
                }
            }
        }

        logger.exiting("FiscalizationWorker", "getRecordsForIndividualFiscalization");
        return arrayListOfEntries;
    }

    //this method builds the flist and invokes search opcode in order to find the user
    protected String UserActivitySearch(Poid eventObjectId) throws EBufException {
        logger.entering("FiscalizationWorker", "UserActivitySearch");
        long dbNumber = getCurrentDB();
        String externalUser = null;
        FList inputSearchFlist = new FList();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" select X from /user_activity where F1=V1 and F2 != V2");
        inputSearchFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        inputSearchFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
        inputSearchFlist.set((IntField) FldFlags.getInst(), 256);
        SparseArray sparseArray = new SparseArray();
        FList BhtFldEventObjIdFlist = new FList();
        FList FldEventsFList = new FList();
        BhtFldEventObjIdFlist.set((PoidField) FldEventObj.getInst(), eventObjectId);
        FldEventsFList.setElement((ArrayField) FldEvents.getInst(), -1, BhtFldEventObjIdFlist);
        sparseArray.add(1, FldEventsFList);
        FList FldExternalUserFList = new FList();
        FldExternalUserFList.set((StrField) FldExternalUser.getInst(), " ");
        sparseArray.add(2, FldExternalUserFList);
        inputSearchFlist.set((ArrayField) FldArgs.getInst(), sparseArray);
        FList resultsfList = new FList();
        resultsfList.set((Field) FldExternalUser.getInst());
        inputSearchFlist.setElement((ArrayField) FldResults.getInst(), -1, resultsfList);
        logger.fine("Input flist for user_activity search ==", inputSearchFlist);

        FList outputSearchFlist = opcode(7, inputSearchFlist);
        logger.fine("Output flist for user_activity search ==", outputSearchFlist);

        if (outputSearchFlist.hasField((Field) FldResults.getInst())) {
            SparseArray array = outputSearchFlist.get(FldResults.getInst());
            if (array != null) {
                Enumeration<FList> enumeration = array.getValueEnumerator();
                while (enumeration.hasMoreElements()) {
                    FList fList = enumeration.nextElement();
                    externalUser = fList.get((StrField) FldExternalUser.getInst());
                }
            }
        }
        logger.exiting("FiscalizationWorker", "UserActivitySearch");

        return externalUser;
    }

    //this method calls an external ws and returns the printer id
    public String getPrinterIdFromWs(String userCode, String orderNumber) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "getPrinterIdFromWs");
        String printerID = null;
        try {
            Properties properties = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            properties.load(ip);
            String url = properties.getProperty("getPrinterIdUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:get=\"http://xmlns.oracle.com/pcbpel/adapter/db/sp/GetPrinterID\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <get:InputParameters>\n"
                    + "         <!--Optional:-->\n"
                    + "         <get:P_ORDNUM>" + orderNumber + "</get:P_ORDNUM>\n"
                    + "         <!--Optional:-->\n"
                    + "         <get:P_USER_ID>" + userCode + "</get:P_USER_ID>\n"
                    + "      </get:InputParameters>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the printer id is: \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response is == \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "get:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                printerID = err.getElementsByTagName(prefix + "FGET_PRINTER_ID").item(0).getTextContent();
                logger.severe("PrinterID is" + printerID);
            }*/
            NodeList errNodes = doc.getElementsByTagName("get:OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                printerID = err.getElementsByTagName("get:FGET_PRINTER_ID").item(0).getTextContent();
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service getPrinterId", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getPrinterId didn't bring a response, exception timeout ", new Object[0]);
        } catch (SAXException | IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service getPrinterId", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getPrinterId didn't bring a valid response ", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getPrinterIdFromWs");
        return printerID;
    }

    //this method calls the ws for printing the non-fiscalized item
    public FiscalPrinterResultFields printIndividualFiscalizationInvoice(BigDecimal netAmount, BigDecimal vatAmount, String glId, String productObj, String printerId) throws ParserConfigurationException, EBufException, SAXException {
        logger.entering("FiscalizationWorker", "printIndividualFiscalizationInvoice");
        FiscalPrinterResultFields fiscalPrinterResultFields = new FiscalPrinterResultFields();

        //call the ws in order to get the endpoint of the printer id 
        String endpoint = getEndpointForPrinterId(printerId);
        if (endpoint == null) {
            logger.severe("FiscalizationWorker, endpoint has null value");
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "endpoint from ws has null value", new Object[0]);
        }
        logger.fine("FiscalizationWorker, endpoint:" + endpoint);

        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("callWsForFiscalPrintersUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String rate = "K";
            if (vatAmount.signum() != 0) {
                rate = "E";
            }
            String numberOfRequest = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
            int typeOfRequest = 0;
            String unitOfMeasure = "kom";
            int group = 0;
            int plu = 0;
            String paymentMethod = "Virman";
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://www.bhtelecom.ba/FiscalPrinter/types\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <typ:printInvoiceInputElement>\n"
                    + "         <typ:BrojZahtjeva>" + numberOfRequest + "</typ:BrojZahtjeva>\n"
                    + "         <typ:VrstaZahtjeva>" + typeOfRequest + "</typ:VrstaZahtjeva>\n"
                    + "         <typ:PrinterEndpoint>" + endpoint + "</typ:PrinterEndpoint>\n"
                    + "         <typ:Kupac>\n"
                    + "            <typ:IDBroj></typ:IDBroj>\n"
                    + "            <typ:Naziv></typ:Naziv>\n"
                    + "            <typ:Adresa></typ:Adresa>\n"
                    + "            <typ:Grad></typ:Grad>\n"
                    + "            <typ:PostanskiBroj></typ:PostanskiBroj>\n"
                    + "         </typ:Kupac>\n"
                    + "         <typ:RacunStavka>\n"
                    + "            <typ:Artikal>\n"
                    + "               <typ:Sifra>" + glId + "</typ:Sifra>\n"
                    + "               <typ:Naziv>" + productObj + "</typ:Naziv>\n"
                    + "               <typ:JM>" + unitOfMeasure + "</typ:JM>\n"
                    + "               <typ:Cijena>" + netAmount + "</typ:Cijena>\n"
                    + "               <typ:Stopa>" + rate + "</typ:Stopa>\n"
                    + "               <typ:Grupa>" + group + "</typ:Grupa>\n"
                    + "               <typ:PLU>" + plu + "</typ:PLU>\n"
                    + "            </typ:Artikal>\n"
                    + "            <typ:Kolicina>1</typ:Kolicina>\n"
                    + "            <typ:Rabat>0</typ:Rabat>\n"
                    + "         </typ:RacunStavka>\n"
                    + "         <typ:BrojRacuna></typ:BrojRacuna>\n"
                    + "         <typ:NacinPlacanja>" + paymentMethod + "</typ:NacinPlacanja>\n"
                    + "      </typ:printInvoiceInputElement>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            logger.severe("The sent xml in order to get the invoice number is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status == " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response == \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "typ:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName(prefix + "InvoiceNumber").item(0).getTextContent());
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName(prefix + "InvoiceStatus").item(0).getTextContent());
                fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName(prefix + "ErrorMessage").item(0).getTextContent());
            }*/
            NodeList errNodes = doc.getElementsByTagName("typ:printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("typ:InvoiceNumber").item(0).getTextContent());
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName("typ:InvoiceStatus").item(0).getTextContent());
                fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("typ:ErrorMessage").item(0).getTextContent());
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service FiscalPrinter", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a response, timeout", new Object[0]);
        } catch (IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        } catch (SAXException ex) {
            logger.severe("FiscalizationWorker, error while calling the web service ", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "printIndividualFiscalizationInvoice");
        return fiscalPrinterResultFields;

    }

    //this method returns the Endpoint for the printer id
    public String getEndpointForPrinterId(String printerId) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "getEndpointForPrinterId");
        String endpoint = null;
        try {
            Properties properties = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            properties.load(ip);
            String url = properties.getProperty("getEndpointForPrinterIdUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:get=\"http://xmlns.oracle.com/pcbpel/adapter/db/sp/GetEndpoint4PrinterId\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <get:InputParameters>\n"
                    + "         <!--Optional:-->\n"
                    + "         <get:P_PRINTER_ID>" + printerId + "</get:P_PRINTER_ID>\n"
                    + "      </get:InputParameters>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the endpoint of the printer is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status == " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response == \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "get:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                endpoint = err.getElementsByTagName(prefix + "FGET_ENDPOINT4PRINTER_ID").item(0).getTextContent(); */
            NodeList errNodes = doc.getElementsByTagName("get:OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                endpoint = err.getElementsByTagName("get:FGET_ENDPOINT4PRINTER_ID").item(0).getTextContent();
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service getEndpointForPrinterId", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getEndpointForPrinterId didn't bring a response, timeout", new Object[0]);
        } catch (SAXException | IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service getEndpointForPrinterId", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getEndpointForPrinterId didn't bring a response", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getEndpointForPrinterId");
        return endpoint;
    }

    public FiscalPrinterResultFields getFiscalNumberForRSBySeqNo(int sequenceNumberAdj) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "getFiscalNumberForRSBySeqNo");
        FiscalPrinterResultFields fiscalPrinterResultFields = new FiscalPrinterResultFields();
        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("getFiscalNumberByRSseqNoUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(40000);
            con.setReadTimeout(40000);
            con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            //con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com/\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <com:GetFiscalNumberByRSseqNo>\n"
                    + "         <!--Optional:-->\n"
                    + "         <rsSeqNo>" + sequenceNumberAdj + "</rsSeqNo>\n"
                    + "      </com:GetFiscalNumberByRSseqNo>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the fiscal number from RS is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service GetFiscalNumberByRSseqNo response is: \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("return");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName("statusCode").item(0).getTextContent());
                if (fiscalPrinterResultFields.getInvoiceStatus().equals("0")) {
                    if (err.getElementsByTagName("numFiscBill").item(0) != null) {
                        fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("numFiscBill").item(0).getTextContent());
                        fiscalPrinterResultFields.setInvoiceStatus("OK");
                    } else {
                        //ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't return a fiscal number", new Object[0]);
                        fiscalPrinterResultFields.setErrorMessage("the web service getFiscalNumberByRSseqNo didn't return a fiscal number");
                    }
                } else if (fiscalPrinterResultFields.getInvoiceStatus().equals("1")) {
                    fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("statusDesc").item(0).getTextContent());
                }
            } else {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't return the return parameters", new Object[0]);
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service getFiscalNumberByRSseqNo", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't bring a response, exception timeout ", new Object[0]);
        } catch (SAXException | IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service getFiscalNumberByRSseqNo", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't bring a valid response ", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getFiscalNumberForRSBySeqNo");
        return fiscalPrinterResultFields;
    }

    //this method builds the flist needed for updating the entry with unissued fiscal invoices
    public FList updateIndividualItemInputFlist(FiscalPrinterResultFields returnedFieldsFromWs, String poid) {
        logger.entering("FiscalizationWorker", "updateIndividualItemsInputFlist");
        FList inputFlist = new FList();

        try {
            if (returnedFieldsFromWs.getInvoiceStatus().equals("OK")) {

                //create the input flist
                Poid poidField = BRMUtility.poidFromRestId(poid);
                inputFlist.set((PoidField) FldPoid.getInst(), poidField);
                inputFlist.set((StrField) BhtFldFiscalNo.getInst(), returnedFieldsFromWs.getInvoiceNumber());
                logger.exiting("FiscalizationWorker", "updateIndividualItemsInputFlist");
                return inputFlist;

            } else {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), returnedFieldsFromWs.getErrorMessage(), new Object[0]);
                return null;
            }
        } catch (NullPointerException ex) {
            logger.severe("FiscalizationWorker, Problem while building the update flist, there is a null value ", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Problem while building the update flist, there is a null value", new Object[0]);
            return null;
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Problem while building the update flist ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            return null;
        }
    }

    //this method builds the flist needed for updating the entry with unissued fiscal invoices
    public FList updateIndividualRSItemInputFlist(FiscalPrinterResultFields returnedFieldsFromWs, String poid) {
        logger.entering("FiscalizationWorker", "updateIndividualRSItemInputFlist");
        FList inputFlist = new FList();

        try {
            if (returnedFieldsFromWs.getInvoiceStatus().equals("OK") && returnedFieldsFromWs.getInvoiceNumber() != null) {

                //create the input flist
                Poid poidField = BRMUtility.poidFromRestId(poid);
                inputFlist.set((PoidField) FldPoid.getInst(), poidField);
                inputFlist.set((StrField) BhtFldFiscalNo.getInst(), returnedFieldsFromWs.getInvoiceNumber());
                logger.exiting("FiscalizationWorker", "updateIndividualRSItemInputFlist");
                return inputFlist;

            } else {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), returnedFieldsFromWs.getErrorMessage(), new Object[0]);
                return null;
            }
        } catch (NullPointerException ex) {
            logger.severe("FiscalizationWorker, Problem while building the update flist, there is a null value ", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Problem while building the update flist, there is a null value", new Object[0]);
            return null;
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Problem while building the update flist ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            return null;
        }
    }

    //this method uses the input flist and calls the neccessary opcode for updatimg the entry with unissued fiscal invoice
    public FiscalUpdateResults updateIndividualItem(FList inputFList) throws EBufException {
        logger.entering("FiscalizationWorker", "updateIndividualItemsOutputFlist");

        FiscalUpdateResults fiscalUpdateResultsList = new FiscalUpdateResults();
        try {

            FList outputFList = opcode(10023, inputFList);
            logger.fine("The output Flist for update is:", outputFList);

            fiscalUpdateResultsList.setFiscalNumber(inputFList.get((StrField) BhtFldFiscalNo.getInst()));
            fiscalUpdateResultsList.setPoid(BRMUtility.restIdFromPoid(outputFList.get(FldPoid.getInst())));
            fiscalUpdateResultsList.setMessage("item was updated, an invoice number has been registered");

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the BHT_OP_FISCAL_UPDATE_FISCAL_NO opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Problem while invoking the BHT_OP_FISCAL_UPDATE_FISCAL_NO opcode", new Object[0]);
        }
        logger.exiting("FiscalizationWorker", "updateIndividualItemsOutputFlist");
        return fiscalUpdateResultsList;
    }

    //this method creates the input flist for step search
    public FList createInputFlistForStepSearch(String accountingPeriod) {
        logger.entering("FiscalizationWorker", "createInputFlistForStepSearch");
        int year = 2000 + Integer.parseInt(accountingPeriod.substring(0, 2));
        int month = Integer.parseInt(accountingPeriod.substring(2));
        Calendar calendar = new GregorianCalendar(year, month, 1);
        FList inputFlistForStepSearch = new FList();
        long dbNumber = getCurrentDB();
        int argsCount = 1;
        int stepSearchResultsCounter = 100;

        inputFlistForStepSearch.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
        inputFlistForStepSearch.set((IntField) FldFlags.getInst(), 0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /bht_fiscal_items_v where F1 != V1 and F2 = V2 and not exists (select 1 from bht_fiscal_item_t where bht_fiscal_item_t.item_obj_id0=bht_fiscal_items_v.item_obj_id0 and bht_fiscal_item_t.status=1) ");
        inputFlistForStepSearch.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        SparseArray resultsSparseArray = new SparseArray();
        FList resultsFlist = new FList();
        resultsFlist.set((Field) BhtFldFiscalNo.getInst());
        resultsFlist.set((Field) BhtFldSalesDirectorate.getInst());
        resultsFlist.set((Field) BhtFldTechnologyCode.getInst());
        resultsFlist.set((Field) FldAccountObj.getInst());
        resultsFlist.set((Field) FldItemObj.getInst());
        resultsFlist.set((Field) BhtFldUserCode.getInst());
        resultsFlist.set((Field) FldAmount.getInst());
        resultsFlist.set((Field) FldTaxCode.getInst());
        resultsFlist.set((Field) BhtFldVatAmount.getInst());
        resultsFlist.set((Field) FldProcessingStage.getInst());
        resultsFlist.set((Field) BhtFldRsSeqNo.getInst());
        resultsSparseArray.add(stepSearchResultsCounter, resultsFlist);
        inputFlistForStepSearch.set((ArrayField) FldResults.getInst(), resultsSparseArray);
        //set Args list
        SparseArray sparseArrayArgs = new SparseArray();
        FList FldStatusFlist = new FList();
        FldStatusFlist.set((EnumField) FldStatus.getInst(), 1);
        sparseArrayArgs.add(argsCount, FldStatusFlist);
        argsCount++;
        FList FLdEndTFlist = new FList();
        FLdEndTFlist.set((TStampField) FldEndT.getInst(), calendar.getTime());
        sparseArrayArgs.add(argsCount, FLdEndTFlist);
        inputFlistForStepSearch.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);

        logger.fine("input flist for step search:" + inputFlistForStepSearch);
        logger.exiting("FiscalizationWorker", "createInputFlistForStepSearch");
        return inputFlistForStepSearch;
    }

    //this method invokes search and the bulk creation of fiscal items 
    public int invokeSearchAndBulkCreate(FList inputFlistForStepSearch, String accountingPeriod) {
        logger.entering("FiscalizationWorker", "invokeSearchAndBulkCreate");
        FList outputFlist = new FList();
        int result = 0;
        do {
            try {
                outputFlist = opcode(7, inputFlistForStepSearch);
                logger.fine("Output FList of search" + outputFlist);
                result += invokeBulkCreateObj(outputFlist, accountingPeriod);
            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem with searching", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            }
        } while (outputFlist.hasField((Field) FldResults.getInst()));
        logger.exiting("FiscalizationWorker", "invokeSearchAndBulkCreate");
        return result;
    }

    public int invokeBulkCreateObj(FList stepSearchResultFlist, String accountingPeriod) throws EBufException {
        logger.entering("FiscalizationWorker", "invokeBulkCreateObj");
        ArrayList<GroupFiscalizationBulkCreationFields> listOfGroupFiscalizationBulkCreationFields = new ArrayList();
        long dbNumber = getCurrentDB();
        int result = 0;

        //parse stepSearchResultFlist in order to store the information into a list of objects
        if (stepSearchResultFlist.hasField((Field) FldResults.getInst())) {
            SparseArray array = stepSearchResultFlist.get(FldResults.getInst());
            GroupFiscalizationBulkCreationFields gfbc = new GroupFiscalizationBulkCreationFields();
            if (array != null) {
                Enumeration<FList> enumeration = array.getValueEnumerator();
                while (enumeration.hasMoreElements()) {
                    FList fList = enumeration.nextElement();
                    gfbc.setAccountObj(fList.get(FldAccountObj.getInst()));
                    gfbc.setFiscalNo(fList.get(BhtFldFiscalNo.getInst()));
                    gfbc.setTechnologyCode(fList.get(BhtFldTechnologyCode.getInst()));
                    gfbc.setSalesDirectorate(fList.get(BhtFldSalesDirectorate.getInst()));
                    gfbc.setItemObj(fList.get(FldItemObj.getInst()));
                    gfbc.setUserCode(fList.get(BhtFldUserCode.getInst()));
                    gfbc.setVatAmount(fList.get(BhtFldVatAmount.getInst()));
                    gfbc.setNetAmount(fList.get(FldAmount.getInst()));
                    gfbc.setTaxCode(fList.get(FldTaxCode.getInst()));
                    gfbc.setProcessingStage(fList.get(FldProcessingStage.getInst()));
                    gfbc.setRsSeqNo(fList.get(BhtFldRsSeqNo.getInst()));

                    listOfGroupFiscalizationBulkCreationFields.add(gfbc);
                    gfbc = new GroupFiscalizationBulkCreationFields();
                }
            }
        }

        FList inputFListForBulkCreate = new FList();
        inputFListForBulkCreate.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/bht_fiscal_item"));
        SparseArray commonValuesSparseArray = new SparseArray();
        FList commonValuesFlist = new FList();
        commonValuesFlist.set((EnumField) BhtFldFscPrintStatus.getInst(), 0);
        commonValuesFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
        commonValuesFlist.set((EnumField) FldStatus.getInst(), 1);
        commonValuesFlist.set((TStampField) BhtFldTransferDate.getInst());
        commonValuesFlist.set((TStampField) BhtFldUpdateDate.getInst());
        commonValuesSparseArray.add(0, commonValuesFlist);
        inputFListForBulkCreate.set((ArrayField) FldCommonValues.getInst(), commonValuesSparseArray);

        int valuesCounter = 1;
        SparseArray ValuesSparseArray = new SparseArray();
        FList valuesFlist = new FList();
        for (GroupFiscalizationBulkCreationFields listOfGroupFiscalizationBulkCreationField : listOfGroupFiscalizationBulkCreationFields) {
            valuesFlist.set(BhtFldDirectorateCode.getInst(), listOfGroupFiscalizationBulkCreationField.getSalesDirectorate());
            valuesFlist.set(BhtFldFiscalNo.getInst(), listOfGroupFiscalizationBulkCreationField.getFiscalNo());
            valuesFlist.set(FldAccountObj.getInst(), listOfGroupFiscalizationBulkCreationField.getAccountObj());
            valuesFlist.set(FldItemObj.getInst(), listOfGroupFiscalizationBulkCreationField.getItemObj());
            valuesFlist.set(BhtFldTechnologyCode.getInst(), listOfGroupFiscalizationBulkCreationField.getTechnologyCode());
            valuesFlist.set(BhtFldUserCode.getInst(), listOfGroupFiscalizationBulkCreationField.getUserCode());
            valuesFlist.set(BhtFldVatAmount.getInst(), listOfGroupFiscalizationBulkCreationField.getVatAmount());
            valuesFlist.set(FldAmount.getInst(), listOfGroupFiscalizationBulkCreationField.getNetAmount());
            valuesFlist.set(FldTaxCode.getInst(), listOfGroupFiscalizationBulkCreationField.getTaxCode());
            valuesFlist.set(FldProcessingStage.getInst(), listOfGroupFiscalizationBulkCreationField.getProcessingStage());
            valuesFlist.set(BhtFldRsSeqNo.getInst(), listOfGroupFiscalizationBulkCreationField.getRsSeqNo());

            ValuesSparseArray.add(valuesCounter, valuesFlist);
            valuesFlist = new FList();
            valuesCounter++;
        }
        inputFListForBulkCreate.set((ArrayField) FldValues.getInst(), ValuesSparseArray);
        result = valuesCounter - 1;
        logger.fine("input Flist for bulk create opcode is: \n" + inputFListForBulkCreate);

        try {
            FList resultOfBulkCreateOpcode = opcode(42, inputFListForBulkCreate);
            logger.fine("result of bulk create opcode is:" + resultOfBulkCreateOpcode);

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the PCM_OP_BULK_CREATE_OBJ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }
        logger.exiting("FiscalizationWorker", "invokeBulkCreateObj");
        return result;
    }

    //this method returns the search parameters for group fiscalization
    public ArrayList<Object> groupedSelectionParameters() {
        logger.entering("FiscalizationWorker", "groupedSelectionParameters");

        ArrayList<Object> arrayListOfGroupedSelectionParameters = new ArrayList();
        HashSet<String> hashsetForDirectorates = new HashSet();
        HashSet<String> hashsetForTechonologies = new HashSet();
        FList flistForBhtFiscalItem = new FList();
        long dbNumber = getCurrentDB();
        int argsCounterForBhtFiscalItem = 1;

        try {

            //construct the search input flist for table bht_fiscal_item
            flistForBhtFiscalItem.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
            flistForBhtFiscalItem.set((IntField) FldFlags.getInst(), 1280);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("select X from /bht_fiscal_item where F1 is null and F2 = V2 and F3 = V3 ");
            flistForBhtFiscalItem.set((StrField) FldTemplate.getInst(), stringBuilder2.toString());
            //set Results array
            SparseArray resultsArrayForBhtFiscalItem = new SparseArray();
            FList directorateCodeFlist = new FList();
            directorateCodeFlist.set((Field) BhtFldDirectorateCode.getInst());
            directorateCodeFlist.set((Field) BhtFldTechnologyCode.getInst());
            resultsArrayForBhtFiscalItem.add(0, directorateCodeFlist);
            flistForBhtFiscalItem.set((ArrayField) FldResults.getInst(), resultsArrayForBhtFiscalItem);
            //set Arguments array
            SparseArray sparseArrayArgs2 = new SparseArray();
            FList FiscalNoFlist2 = new FList();
            FiscalNoFlist2.set((StrField) BhtFldFiscalNo.getInst(), "");
            sparseArrayArgs2.add(argsCounterForBhtFiscalItem, FiscalNoFlist2);
            argsCounterForBhtFiscalItem++;
            FList FldStatusFlist2 = new FList();
            FldStatusFlist2.set((EnumField) FldStatus.getInst(), 1);
            sparseArrayArgs2.add(argsCounterForBhtFiscalItem, FldStatusFlist2);
            argsCounterForBhtFiscalItem++;
            FList FldRsSeqNoFlist2 = new FList();
            FldRsSeqNoFlist2.set((IntField) BhtFldRsSeqNo.getInst(), 0);
            sparseArrayArgs2.add(argsCounterForBhtFiscalItem, FldRsSeqNoFlist2);
            flistForBhtFiscalItem.set((ArrayField) FldArgs.getInst(), sparseArrayArgs2);
            logger.fine("flist in order to get the distinct selection fields from bht_fiscal_item is == " + flistForBhtFiscalItem);

            //invoking the search opcode in order to get the distinct directorate codes
            FList flistWithDistinctFieldsForBhtFiscalItem = opcode(7, flistForBhtFiscalItem);
            logger.fine("the returned flist with the distinct selection fields for bht_fiscal_item codes is == " + flistWithDistinctFieldsForBhtFiscalItem);

            //get the distinct directorate codes and technologies from the flist 
            if (flistWithDistinctFieldsForBhtFiscalItem.hasField((Field) FldResults.getInst())) {
                SparseArray array2 = flistWithDistinctFieldsForBhtFiscalItem.get(FldResults.getInst());
                if (array2 != null) {
                    Enumeration<FList> enumeration2 = array2.getValueEnumerator();
                    while (enumeration2.hasMoreElements()) {
                        FList fList = enumeration2.nextElement();
                        hashsetForDirectorates.add(fList.get((StrField) BhtFldDirectorateCode.getInst()));
                        hashsetForTechonologies.add(fList.get((StrField) BhtFldTechnologyCode.getInst()));
                    }
                }
            }
        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Problem finding grouped selection parameters ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }

        SelectionParameters objectContainingTheDirectoratesInfo = new SelectionParameters();
        for (String hashsetForDirectorate : hashsetForDirectorates) {
            DirectoratesInfo dir = new DirectoratesInfo();
            try {
                dir = getDirectoratesDescription(hashsetForDirectorate);
            } catch (Exception exception) {
                logger.severe("FiscalizationWorker, Couldn't find the directorates:" + hashsetForDirectorate + " name ", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Can not find the name of directorate: " + hashsetForDirectorate, new Object[0]);
            }

            objectContainingTheDirectoratesInfo.setDirectorateName(dir.getDirectorateName());
            objectContainingTheDirectoratesInfo.setDirectorateCode(hashsetForDirectorate);
            arrayListOfGroupedSelectionParameters.add(objectContainingTheDirectoratesInfo);
            objectContainingTheDirectoratesInfo = new SelectionParameters();
        }

        SelectionParameters objectContainingTheTechnologyInfo = new SelectionParameters();
        for (String hashsetForTechonology : hashsetForTechonologies) {
            objectContainingTheTechnologyInfo.setTechnologyCode(hashsetForTechonology);
            arrayListOfGroupedSelectionParameters.add(objectContainingTheTechnologyInfo);
            objectContainingTheTechnologyInfo = new SelectionParameters();
        }

        logger.exiting("FiscalizationWorker", "groupedSelectionParameters");
        return arrayListOfGroupedSelectionParameters;
    }

    //this method takes the group data from bht fiscal item table
    public HashMap<Object, List<BigDecimal>> getGroupedDataFromBhtFiscalItem(String directorateCode, String technologyCode, String accountingPeriod) {
        logger.entering("FiscalizationWorker", "getGroupedDataFromBhtFiscalItem");

        FList searchFlist = new FList();
        FList groupedDataFromBhtFiscalItem;
        HashMap<Object, List<BigDecimal>> hashMap = new HashMap<>();
        ArrayList<Object> listOfKeyValues = new ArrayList();
        ArrayList<BigDecimal> listOfValues = new ArrayList();
        long l = getCurrentDB();
        int resultsCounter = 0;
        byte argsCounter = 1;
        int templateArgsCounter = 3;
        boolean flag = false;

        try {
            //construct the input search flist
            searchFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
            searchFlist.set((IntField) FldFlags.getInst(), 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /bht_fiscal_item where F1 is null and F2 = V2 and F3 = V3 ");
            if (!"null".equals(directorateCode)) {
                templateArgsCounter++;
                stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
            }
            if (!"null".equals(technologyCode)) {
                templateArgsCounter++;
                stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
            }
            if (!"null".equals(accountingPeriod)) {
                templateArgsCounter++;
                stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
            }
            searchFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            //set Results list values
            SparseArray resultsSparseArray = new SparseArray();
            FList resultsFlist = new FList();
            resultsFlist.set((Field) FldItemObj.getInst());
            resultsFlist.set((Field) FldPoid.getInst());
            resultsFlist.set((Field) BhtFldDirectorateCode.getInst());
            resultsFlist.set((Field) BhtFldTechnologyCode.getInst());
            resultsFlist.set((Field) FldAmount.getInst());
            resultsFlist.set((Field) FldTaxCode.getInst());
            resultsSparseArray.add(resultsCounter, resultsFlist);
            searchFlist.set((ArrayField) FldResults.getInst(), resultsSparseArray);
            //set Args list
            SparseArray ArgsSparseArray = new SparseArray();
            FList BhtFldFiscalNoFlist = new FList();
            BhtFldFiscalNoFlist.set((StrField) BhtFldFiscalNo.getInst(), "");
            ArgsSparseArray.add(argsCounter, BhtFldFiscalNoFlist);
            argsCounter++;
            FList FldStatusFlist = new FList();
            FldStatusFlist.set((EnumField) FldStatus.getInst(), 1);
            ArgsSparseArray.add(argsCounter, FldStatusFlist);
            argsCounter++;
            FList BhtFldRsSeqNoFlist = new FList();
            BhtFldRsSeqNoFlist.set((IntField) BhtFldRsSeqNo.getInst(), 0);
            ArgsSparseArray.add(argsCounter, BhtFldRsSeqNoFlist);
            if (!"null".equals(directorateCode)) {
                argsCounter++;
                FList BhtFldDirectorateCodeFlist = new FList();
                BhtFldDirectorateCodeFlist.set((StrField) BhtFldDirectorateCode.getInst(), directorateCode);
                ArgsSparseArray.add(argsCounter, BhtFldDirectorateCodeFlist);
            }
            if (!"null".equals(technologyCode)) {
                argsCounter++;
                FList BhtFldTechnologyCodeFlist = new FList();
                BhtFldTechnologyCodeFlist.set((StrField) BhtFldTechnologyCode.getInst(), technologyCode);
                ArgsSparseArray.add(argsCounter, BhtFldTechnologyCodeFlist);
            }
            if (!"null".equals(accountingPeriod)) {
                argsCounter++;
                FList BhtFldAccountingPeriodFlist = new FList();
                BhtFldAccountingPeriodFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
                ArgsSparseArray.add(argsCounter, BhtFldAccountingPeriodFlist);
            }
            searchFlist.set((ArrayField) FldArgs.getInst(), ArgsSparseArray);

            logger.fine("the input flist for searching the grouped data from bht ficsal item is == " + searchFlist);
            //invoke the search opcode in order to get the grouped data from bht_fiscal_item
            groupedDataFromBhtFiscalItem = opcode(7, searchFlist);
            logger.fine("the returned flist with the grouped data from bht ficsal item is == " + groupedDataFromBhtFiscalItem);

            //parse the groupedDataFromBhtFiscalItem Flist in order to get the grouped fiscalized items information
            if (groupedDataFromBhtFiscalItem.hasField((Field) FldResults.getInst())) {
                SparseArray groupedDataFromBhtFiscalItemArray = groupedDataFromBhtFiscalItem.get(FldResults.getInst());
                GroupedFiscalizedFields groupedFiscalizedFields = new GroupedFiscalizedFields();
                if (groupedDataFromBhtFiscalItemArray != null) {
                    Enumeration<FList> groupedDataFromBhtFiscalItemEnumeration = groupedDataFromBhtFiscalItemArray.getValueEnumerator();
                    while (groupedDataFromBhtFiscalItemEnumeration.hasMoreElements()) {
                        FList groupedDataFromBhtFiscalItemFlist = groupedDataFromBhtFiscalItemEnumeration.nextElement();
                        groupedFiscalizedFields.setDirCode(groupedDataFromBhtFiscalItemFlist.get((StrField) BhtFldDirectorateCode.getInst()));
                        groupedFiscalizedFields.setTechnologyCode(groupedDataFromBhtFiscalItemFlist.get((StrField) BhtFldTechnologyCode.getInst()));
                        groupedFiscalizedFields.setNetAmount(groupedDataFromBhtFiscalItemFlist.get(FldAmount.getInst()));
                        groupedFiscalizedFields.setTaxCode(groupedDataFromBhtFiscalItemFlist.get(FldTaxCode.getInst()));

                        listOfKeyValues.add(groupedFiscalizedFields.getTaxCode());
                        listOfKeyValues.add(groupedFiscalizedFields.getDirCode());
                        listOfKeyValues.add(groupedFiscalizedFields.getTechnologyCode());
                        listOfValues.add(groupedFiscalizedFields.getNetAmount());
                        listOfValues.add(new BigDecimal(1));

                        logger.fine("list of values:" + listOfValues.toString());
                        if (hashMap.isEmpty()) {
                            hashMap.put(listOfKeyValues, listOfValues);
                            listOfValues = new ArrayList();
                        } else {
                            Iterator<Map.Entry<Object, List<BigDecimal>>> iterator = hashMap.entrySet().iterator();
                            // Iterate over the HashMap
                            while (iterator.hasNext()) {

                                // Get the entry at this iteration
                                Map.Entry<Object, List<BigDecimal>> entry = (Map.Entry<Object, List<BigDecimal>>) iterator.next();

                                // Check if this key is the required key
                                if (listOfKeyValues.equals(entry.getKey())) {
                                    listOfValues.set(0, entry.getValue().get(0).add(groupedFiscalizedFields.getNetAmount()));
                                    listOfValues.set(1, entry.getValue().get(1).add(new BigDecimal(1)));
                                    hashMap.put(listOfKeyValues, listOfValues);
                                    listOfValues = new ArrayList();
                                    flag = true;
                                    break;
                                } else {
                                    flag = false;
                                }
                            }
                            if (flag == false) {
                                hashMap.put(listOfKeyValues, listOfValues);
                                listOfValues = new ArrayList();
                                flag = false;
                            }
                        }

                        logger.fine("Map in getGroupedDataFromBhtFiscalItem is ==" + hashMap.toString());
                        listOfKeyValues = new ArrayList();
                        groupedFiscalizedFields = new GroupedFiscalizedFields();
                    }
                }
            }
            logger.exiting("FiscalizationWorker", "getGroupedDataFromBhtFiscalItem");
        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Problem while building the grouped data search flist ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);

        }

        return hashMap;
    }

    //this method returns the printer id
    public List<String> getPrinterIdEndpointFromWs(String directorateCode, String technologyCode) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "getPrinterIdEndpointFromWs");
        List<String> resultsList = new ArrayList();
        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("getPrinterIdEndpoint");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:get=\"http://xmlns.oracle.com/pcbpel/adapter/db/sp/GetPrinterIdEndpoint\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <get:InputParameters>\n"
                    + "         <!--Optional:-->\n"
                    + "         <get:P_LOCATION_ID>" + directorateCode + "</get:P_LOCATION_ID>\n"
                    + "         <!--Optional:-->\n"
                    + "         <get:P_TECHNOLOGY_ID>" + technologyCode + "</get:P_TECHNOLOGY_ID>\n"
                    + "      </get:InputParameters>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the printer id and endpoint is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response: \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "get:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                resultsList.add(err.getElementsByTagName(prefix + "V_PRINTER_ID").item(0).getTextContent());
                resultsList.add(err.getElementsByTagName(prefix + "V_ENDPOINT").item(0).getTextContent()); */
            NodeList errNodes = doc.getElementsByTagName("get:OutputParameters");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                resultsList.add(err.getElementsByTagName("get:V_PRINTER_ID").item(0).getTextContent());
                resultsList.add(err.getElementsByTagName("get:V_ENDPOINT").item(0).getTextContent());
            } else {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getPrinterIdEndpoint didn't return output parameters", new Object[0]);
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.info("FiscalizationWorker, there was a timeout while calling the web service getPrinterIdEndpoint", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getPrinterIdEndpoint didn't bring a response, exception timeout ", new Object[0]);
        } catch (SAXException | IOException e) {
            logger.info("FiscalizationWorker, error while calling the web service getPrinterId", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getPrinterIdEndpoint didn't bring a valid response ", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getPrinterIdEndpointFromWs");
        return resultsList;
    }

    //this method parses the map of the group items and returns a list containing the items information
    public ArrayList<GroupedFiscalizedFields> getGroupFiscalizedItems(HashMap<Object, List<BigDecimal>> mapOfBhtGroupedFiscalItems) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "mergeGroupFiscalizedItems");
        ArrayList<GroupedFiscalizedFields> arrayListOfGroupData = new ArrayList(); //the returned list of merged items 

        //iterate the merged hashMap
        Iterator<Map.Entry<Object, List<BigDecimal>>> iterator = mapOfBhtGroupedFiscalItems.entrySet().iterator();
        // Iterate over the HashMap
        while (iterator.hasNext()) {
            GroupedFiscalizedFields groupedFiscalizedFields = new GroupedFiscalizedFields(); //instatiate a GroupedFiscalizedFields object 
            // Get the entry at this iteration
            Map.Entry<Object, List<BigDecimal>> entry = (Map.Entry<Object, List<BigDecimal>>) iterator.next();

            ArrayList listOfHashMapValues = (ArrayList) entry.getKey();
            groupedFiscalizedFields.setNetAmount(entry.getValue().get(0));
            groupedFiscalizedFields.setItemsCounter(entry.getValue().get(1));
            groupedFiscalizedFields.setTaxCode((String) listOfHashMapValues.get(0));

            groupedFiscalizedFields.setDirCode((String) listOfHashMapValues.get(1));
            DirectoratesInfo dir = getDirectoratesDescription(groupedFiscalizedFields.getDirCode());
            groupedFiscalizedFields.setDirDescription(dir.getDirectorateName());
            groupedFiscalizedFields.setTechnologyCode((String) listOfHashMapValues.get(2));

            //calling the ws to get the endpoint and printer id
            List<String> printerIdAndEndpoint = getPrinterIdEndpointFromWs(groupedFiscalizedFields.getDirCode(), groupedFiscalizedFields.getTechnologyCode());
            groupedFiscalizedFields.setPrinterId(printerIdAndEndpoint.get(0));
            groupedFiscalizedFields.setEndpoint(printerIdAndEndpoint.get(1));

            groupedFiscalizedFields.setDescription("Billing - dir " + groupedFiscalizedFields.getDirCode() + " teh-" + groupedFiscalizedFields.getTechnologyCode());

            arrayListOfGroupData.add(groupedFiscalizedFields); //adding the GroupedFiscalizedFields object into arraylist
        }

        logger.exiting("FiscalizationWorker", "mergeGroupFiscalizedItems");
        return arrayListOfGroupData;
    }

    //this method calls the ws in order to print the grouped data
    public FiscalPrinterResultFields printGroupedFiscalizationInvoice(BigDecimal netAmount, String taxCode, String description, String endpoint) throws IOException, ParserConfigurationException {
        logger.entering("FiscalizationWorker", "printGroupedFiscalizationInvoice");
        FiscalPrinterResultFields fprf = new FiscalPrinterResultFields();

        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("callWsForFiscalPrintersUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String numberOfRequest = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
            int typeOfRequest = 0;
            String unitOfMeasure = "kom";
            int group = 0;
            int plu = 0;
            String code = "123456";
            String paymentMethod = "Virman";
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://www.bhtelecom.ba/FiscalPrinter/types\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <typ:printInvoiceInputElement>\n"
                    + "         <typ:BrojZahtjeva>" + numberOfRequest + "</typ:BrojZahtjeva>\n"
                    + "         <typ:VrstaZahtjeva>" + typeOfRequest + "</typ:VrstaZahtjeva>\n"
                    + "         <typ:PrinterEndpoint>" + endpoint + "</typ:PrinterEndpoint>\n"
                    + "         <typ:Kupac>\n"
                    + "            <typ:IDBroj></typ:IDBroj>\n"
                    + "            <typ:Naziv></typ:Naziv>\n"
                    + "            <typ:Adresa></typ:Adresa>\n"
                    + "            <typ:Grad></typ:Grad>\n"
                    + "            <typ:PostanskiBroj></typ:PostanskiBroj>\n"
                    + "         </typ:Kupac>\n"
                    + "         <typ:RacunStavka>\n"
                    + "            <typ:Artikal>\n"
                    + "               <typ:Sifra>" + code + "</typ:Sifra>\n"
                    + "               <typ:Naziv>" + description + "</typ:Naziv>\n"
                    + "               <typ:JM>" + unitOfMeasure + "</typ:JM>\n"
                    + "               <typ:Cijena>" + netAmount + "</typ:Cijena>\n"
                    + "               <typ:Stopa>" + taxCode + "</typ:Stopa>\n"
                    + "               <typ:Grupa>" + group + "</typ:Grupa>\n"
                    + "               <typ:PLU>" + plu + "</typ:PLU>\n"
                    + "            </typ:Artikal>\n"
                    + "            <typ:Kolicina>1</typ:Kolicina>\n"
                    + "            <typ:Rabat>0</typ:Rabat>\n"
                    + "         </typ:RacunStavka>\n"
                    + "         <typ:BrojRacuna></typ:BrojRacuna>\n"
                    + "         <typ:NacinPlacanja>" + paymentMethod + "</typ:NacinPlacanja>\n"
                    + "      </typ:printInvoiceInputElement>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the invoice number is: \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response: \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "typ:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fprf.setInvoiceNumber(err.getElementsByTagName(prefix + "InvoiceNumber").item(0).getTextContent());
                fprf.setErrorMessage(err.getElementsByTagName(prefix + "ErrorMessage").item(0).getTextContent());
                fprf.setInvoiceStatus(err.getElementsByTagName(prefix + "InvoiceStatus").item(0).getTextContent());
            } */
            NodeList errNodes = doc.getElementsByTagName("typ:printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fprf.setInvoiceNumber(err.getElementsByTagName("typ:InvoiceNumber").item(0).getTextContent());
                fprf.setErrorMessage(err.getElementsByTagName("typ:ErrorMessage").item(0).getTextContent());
                fprf.setInvoiceStatus(err.getElementsByTagName("typ:InvoiceStatus").item(0).getTextContent());
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.info("FiscalizationWorker, there was a timeout while calling the web service FiscalPrinter", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a response, exception timeout ", new Object[0]);
        } catch (IOException | SAXException e) {
            logger.info("FiscalizationWorker, error while calling the web service", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        }
        logger.exiting("FiscalizationWorker", "printGroupedFiscalizationInvoice");
        return fprf;

    }

    //invoke the update opcode
    public FiscalUpdateResults invokeUpdateGroupFiscalNoOpcode(FiscalPrinterResultFields returnedFieldsFromWs, String description, String accountingPeriod, String taxCode, BigDecimal itemsCounter) {
        logger.entering("FiscalizationWorker", "invokeUpdateOpcode");
        String[] splited = description.split(" ");
        String directorate = splited[3];
        String[] splited2 = description.split("-");
        String technology = splited2[2];
        FList inputFlist = new FList();
        long dbNumber = getCurrentDB();
        FiscalUpdateResults fiscalUpdateResultsList = new FiscalUpdateResults();

        if (returnedFieldsFromWs.getInvoiceStatus().equals("OK")) {
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/bht_fiscal_item"));
            inputFlist.set((StrField) BhtFldFiscalNo.getInst(), returnedFieldsFromWs.getInvoiceNumber());
            inputFlist.set(BhtFldDirectorateCode.getInst(), directorate);
            inputFlist.set(BhtFldTechnologyCode.getInst(), technology);
            inputFlist.set(BhtFldAccountingPeriod.getInst(), accountingPeriod);
            inputFlist.set(FldTaxCode.getInst(), taxCode);

            logger.fine("input flist for update group fiscal number is: " + inputFlist);

            try {
                FList outputFList = opcode(10025, inputFlist);
                logger.fine("ouput flist for update group fiscal number is: " + outputFList);

                if (itemsCounter.compareTo(new BigDecimal(outputFList.get(FldCount.getInst()))) == 0) {

                    fiscalUpdateResultsList.setFiscalNumber(returnedFieldsFromWs.getInvoiceNumber());
                    fiscalUpdateResultsList.setPoid(BRMUtility.restIdFromPoid(outputFList.get(FldPoid.getInst())));
                    fiscalUpdateResultsList.setMessage(description + " " + taxCode);
                } else {
                    logger.severe("FiscalizationWorker, The updated items are not equal with the items counter ");
                    ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), " For directorate: " + directorate + ", technology: " + technology + ", accounting period: " + accountingPeriod + " and taxation code:" + taxCode + " the updated items are not equal with the grouped items ", new Object[0]);

                }
            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem while invoking the BHT_OP_FISCAL_UPDATE_GROUP_FISCAL_NO opcode ", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), " Problem while invoking the BHT_OP_FISCAL_UPDATE_GROUP_FISCAL_NO opcode ", new Object[0]);
            }

        } else {
            logger.severe("FiscalizationWorker, error while calling the web service: " + returnedFieldsFromWs.getErrorMessage());
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), returnedFieldsFromWs.getErrorMessage(), new Object[0]);

        }
        logger.exiting("FiscalizationWorker", "invokeUpdateOpcode");
        return fiscalUpdateResultsList;
    }

    //this method returns the search parameters for corrected fiscalization
    public ArrayList<Object> correctionsSelectionParameters(String accountingPeriod) {
        logger.entering("FiscalizationWorker", "correctionsSelectionParameters");

        ArrayList<Object> arrayListOfCorrectionsSelectionParameters = new ArrayList();
        HashSet<String> hashsetForDirectorates = new HashSet();
        HashSet<String> hashsetForTechonologies = new HashSet();
        FList inputFlistForSearchingDistinctSelectionParameters = new FList();
        long dbNumber = getCurrentDB();

        try {

            //construct the search input flist for table bht_fiscal_item
            inputFlistForSearchingDistinctSelectionParameters.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
            inputFlistForSearchingDistinctSelectionParameters.set((IntField) FldFlags.getInst(), 1280);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("select X from /bht_fiscal_item where F1 = V1 ");
            inputFlistForSearchingDistinctSelectionParameters.set((StrField) FldTemplate.getInst(), stringBuilder2.toString());
            //set Results array
            SparseArray resultsArrayForBhtFiscalItem = new SparseArray();
            FList selectionParametersFlist = new FList();
            selectionParametersFlist.set((Field) BhtFldDirectorateCode.getInst());
            selectionParametersFlist.set((Field) BhtFldTechnologyCode.getInst());
            resultsArrayForBhtFiscalItem.add(0, selectionParametersFlist);
            inputFlistForSearchingDistinctSelectionParameters.set((ArrayField) FldResults.getInst(), resultsArrayForBhtFiscalItem);
            //set Arguments array
            SparseArray sparseArrayForArguments = new SparseArray();
            SparseArray sparseArrayAdjustmentInfo = new SparseArray();
            FList BhtFldAdjustmentInfoFlist = new FList();
            FList BhtFldAccountingPeriodFlist = new FList();
            BhtFldAccountingPeriodFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
            sparseArrayAdjustmentInfo.add(0, BhtFldAccountingPeriodFlist);
            BhtFldAdjustmentInfoFlist.set((ArrayField) FldAdjustmentInfo.getInst(), sparseArrayAdjustmentInfo);
            sparseArrayForArguments.add(1, BhtFldAdjustmentInfoFlist);
            inputFlistForSearchingDistinctSelectionParameters.set((ArrayField) FldArgs.getInst(), sparseArrayForArguments);
            logger.fine("flist in order to get the distinct selection fields from bht_fiscal_item is == " + inputFlistForSearchingDistinctSelectionParameters);

            //invoking the search opcode in order to get the distinct selection parameters
            FList flistWithDistinctFieldsForBhtFiscalItem = opcode(7, inputFlistForSearchingDistinctSelectionParameters);
            logger.fine("the returned flist with the distinct selection fields for bht_fiscal_item codes is == " + flistWithDistinctFieldsForBhtFiscalItem);

            //get the distinct technologes and accounting period from the flist 
            if (flistWithDistinctFieldsForBhtFiscalItem.hasField((Field) FldResults.getInst())) {
                SparseArray array2 = flistWithDistinctFieldsForBhtFiscalItem.get(FldResults.getInst());
                if (array2 != null) {
                    Enumeration<FList> enumeration2 = array2.getValueEnumerator();
                    while (enumeration2.hasMoreElements()) {
                        FList fList = enumeration2.nextElement();
                        hashsetForDirectorates.add(fList.get((StrField) BhtFldDirectorateCode.getInst()));
                        hashsetForTechonologies.add(fList.get((StrField) BhtFldTechnologyCode.getInst()));
                    }
                }
            }

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);

        } catch (Exception exception) {
            logger.severe("FiscalizationWorker, Problem while finding the distinct selection parameters ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }

        //add the directorate name/code info into CorrectFiscalizationSelectionParameters object
        SelectionParameters objectContainingTheDirectoratesInfo = new SelectionParameters();
        for (String hashsetForDirectorate : hashsetForDirectorates) {
            DirectoratesInfo dir = new DirectoratesInfo();
            try {
                dir = getDirectoratesDescription(hashsetForDirectorate);
            } catch (Exception exception) {
                logger.severe("FiscalizationWorker, Couldn't find the directorates:" + hashsetForDirectorate + " name ", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Can not find the name of directorate: " + hashsetForDirectorate, new Object[0]);
            }

            objectContainingTheDirectoratesInfo.setDirectorateName(dir.getDirectorateName());
            objectContainingTheDirectoratesInfo.setDirectorateCode(hashsetForDirectorate);
            arrayListOfCorrectionsSelectionParameters.add(objectContainingTheDirectoratesInfo);
            objectContainingTheDirectoratesInfo = new SelectionParameters();
        }

        //add the technology info into CorrectFiscalizationSelectionParameters object
        SelectionParameters objectContainingTheTechnologyInfo = new SelectionParameters();
        for (String hashsetForTechonology : hashsetForTechonologies) {
            objectContainingTheTechnologyInfo.setTechnologyCode(hashsetForTechonology);
            arrayListOfCorrectionsSelectionParameters.add(objectContainingTheTechnologyInfo);
            objectContainingTheTechnologyInfo = new SelectionParameters();
        }

        logger.exiting("FiscalizationWorker", "correctionsSelectionParameters");
        return arrayListOfCorrectionsSelectionParameters;
    }

    //this method creates the flist for corrections search at view bht_fiscal_adjustments_v 
    public FList createInputFlistForCorrectionsSearch(String accountingPeriod) {
        logger.entering("FiscalizationWorker", "createInputFlistForCorrectionsSearch");
        long l = getCurrentDB();
        int searchResultsCounter = 100;
        FList inputFlistForCorrectionsSearch = new FList();
        inputFlistForCorrectionsSearch.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        inputFlistForCorrectionsSearch.set((IntField) FldFlags.getInst(), 0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /bht_fiscal_adjustments_v where F1 = V1 and not exists (select 1 from bht_fiscal_item_adj_t where bht_fiscal_item_adj_t.obj_id0=bht_fiscal_adjustments_v.poid_id0 and bht_fiscal_item_adj_t.accounting_period=bht_fiscal_adjustments_v.accounting_period) ");
        inputFlistForCorrectionsSearch.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        //results
        SparseArray resultsSparseArray = new SparseArray();
        FList resultsFlist = new FList();
        resultsFlist.set((Field) BhtFldFiscalItemObj.getInst());
        resultsFlist.set((Field) BhtFldAccountingPeriod.getInst());
        resultsFlist.set((Field) FldAdjustmentType.getInst());
        resultsFlist.set((Field) FldAmount.getInst());
        resultsFlist.set((Field) BhtFldFiscalNo.getInst());
        resultsSparseArray.add(searchResultsCounter, resultsFlist);
        inputFlistForCorrectionsSearch.set((ArrayField) FldResults.getInst(), resultsSparseArray);
        //arguments
        SparseArray sparseArrayArgs = new SparseArray();
        FList BhtFldAccountingPeriodFlist = new FList();
        BhtFldAccountingPeriodFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
        sparseArrayArgs.add(1, BhtFldAccountingPeriodFlist);
        inputFlistForCorrectionsSearch.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);

        logger.fine("Input FList of Corrections search" + inputFlistForCorrectionsSearch);

        logger.entering("FiscalizationWorker", "createInputFlistForCorrectionsSearch");
        return inputFlistForCorrectionsSearch;
    }

    //this method invokes search and the creation of corrections fiscal items 
    public int invokeSearchAndCreateCorrections(FList inputFlistForCorrectionsSearch, String accountingPeriod) {
        logger.entering("FiscalizationWorker", "invokeSearchAndCreateCorrections");
        FList outputFlistForCorrectionsSearch = new FList();
        int result = 0;
        do {
            try {
                outputFlistForCorrectionsSearch = opcode(7, inputFlistForCorrectionsSearch);
                logger.fine("Output FList of Corrections search" + outputFlistForCorrectionsSearch);
                result += invokeWriteFields(outputFlistForCorrectionsSearch, accountingPeriod);
            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem with searching", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            }
        } while (outputFlistForCorrectionsSearch.hasField((Field) FldResults.getInst()));
        logger.exiting("FiscalizationWorker", "invokeSearchAndCreateCorrections");
        return result;
    }

    //This method parses the Flist of corrections and creates the flist needed for invoking the write fields opcode and invokes the opcode
    public int invokeWriteFields(FList outputFlistForCorrectionsSearch, String accountingPeriod) throws EBufException {
        logger.entering("FiscalizationWorker", "invokeWriteFields");
        ArrayList<CorrectionsFiscalizationWriteFields> listOfCorrectionsFiscalizationWriteFields = new ArrayList();
        int result = 0;

        //parse the output flist with corrections and store the information in a list of objects
        if (outputFlistForCorrectionsSearch.hasField((Field) FldResults.getInst())) {
            SparseArray array = outputFlistForCorrectionsSearch.get(FldResults.getInst());
            CorrectionsFiscalizationWriteFields correctionsFiscalizationWriteFields = new CorrectionsFiscalizationWriteFields();
            if (array != null) {
                Enumeration<FList> enumeration = array.getValueEnumerator();
                while (enumeration.hasMoreElements()) {
                    FList fList = enumeration.nextElement();
                    correctionsFiscalizationWriteFields.setAdjustmentType(fList.get(FldAdjustmentType.getInst()));
                    correctionsFiscalizationWriteFields.setAmount(fList.get(FldAmount.getInst()));
                    correctionsFiscalizationWriteFields.setPoid(fList.get(BhtFldFiscalItemObj.getInst()));
                    correctionsFiscalizationWriteFields.setFiscalNo(fList.get(BhtFldFiscalNo.getInst()));

                    listOfCorrectionsFiscalizationWriteFields.add(correctionsFiscalizationWriteFields);
                    correctionsFiscalizationWriteFields = new CorrectionsFiscalizationWriteFields();
                }
            }
        }

        //create the input flist needed in order to call the write fields opcode for every item in the list of objecrs
        result = listOfCorrectionsFiscalizationWriteFields.size();
        listOfCorrectionsFiscalizationWriteFields.stream().map((correctionsFiscalizationWriteFields) -> {
            FList inputFlistForWriteFields = new FList();
            inputFlistForWriteFields.set(FldPoid.getInst(), correctionsFiscalizationWriteFields.getPoid());
            FList adjustmentInfoSubArray = new FList();
            adjustmentInfoSubArray.set(BhtFldAccountingPeriod.getInst(), accountingPeriod);
            adjustmentInfoSubArray.set(BhtFldFscAdjStatus.getInst(), 0);
            adjustmentInfoSubArray.set(FldAdjustmentType.getInst(), correctionsFiscalizationWriteFields.getAdjustmentType());
            if (correctionsFiscalizationWriteFields.getAdjustmentType() == 1) {
                adjustmentInfoSubArray.set(BhtFldFiscalNo.getInst());
            } else {
                adjustmentInfoSubArray.set(BhtFldFiscalNo.getInst(), correctionsFiscalizationWriteFields.getFiscalNo());
            }
            adjustmentInfoSubArray.set(FldAmount.getInst(), correctionsFiscalizationWriteFields.getAmount());
            inputFlistForWriteFields.setElement(FldAdjustmentInfo.getInst(), ELEMID_ASSIGN, adjustmentInfoSubArray);
            return inputFlistForWriteFields;
        }).map((inputFlistForWriteFields) -> {
            logger.fine("input Flist for write fields is:" + inputFlistForWriteFields);
            return inputFlistForWriteFields;
        }).forEachOrdered((inputFlistForWriteFields) -> { // for each item call the write fields opcode
            try {
                FList outputFlistForWriteFields = opcode(5, 32, inputFlistForWriteFields);
                logger.fine("result of write fields opcode is:" + outputFlistForWriteFields);

            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem while invoking the write fields opcode", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
            }
        });

        logger.exiting("FiscalizationWorker", "invokeWriteFields");
        return result;
    }

    //this method creates the input flist in order to search for corrections in structure bht_fiscal_item
    public FList createInputFlistForCorrectionsSearch(String directorateCode, String technologyCode, String accountingPeriod) {
        logger.entering("FiscalizationWorker", "createInputFlistForCorrectionsSearch");
        long l = getCurrentDB();
        int argumentsCounter = 1;
        int templateArgsCounter = 1;
        FList inputFlistForCorrectionsSearch = new FList();
        inputFlistForCorrectionsSearch.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        inputFlistForCorrectionsSearch.set((IntField) FldFlags.getInst(), 512);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /bht_fiscal_item where F1 = V1 ");
        if (!"null".equals(directorateCode)) {
            templateArgsCounter++;
            stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
        }
        if (!"null".equals(technologyCode)) {
            templateArgsCounter++;
            stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
        }
        inputFlistForCorrectionsSearch.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        //results
        SparseArray resultsSparseArray = new SparseArray();
        FList resultsFlist = new FList();
        resultsFlist.set((Field) FldPoid.getInst());
        resultsFlist.set((Field) BhtFldDirectorateCode.getInst());
        resultsFlist.set((Field) BhtFldTechnologyCode.getInst());
        resultsFlist.set((Field) FldItemObj.getInst());
        resultsFlist.set((Field) FldTaxCode.getInst());
        resultsFlist.set((Field) FldProcessingStage.getInst());
        resultsFlist.set((Field) BhtFldRsSeqNo.getInst());
        resultsFlist.set((Field) BhtFldFiscalNo.getInst());
        FList adjustmentInfoSubArrayFList = new FList();
        adjustmentInfoSubArrayFList.set((Field) BhtFldFscAdjStatus.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldFscAdjNo.getInst());
        adjustmentInfoSubArrayFList.set((Field) FldAdjustmentType.getInst());
        adjustmentInfoSubArrayFList.set((Field) FldAmount.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldFiscalNo.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldRsSeqAdjNo.getInst());
        resultsFlist.setElement(FldAdjustmentInfo.getInst(), ELEMID_ANY, adjustmentInfoSubArrayFList);
        resultsSparseArray.add(0, resultsFlist);
        inputFlistForCorrectionsSearch.set((ArrayField) FldResults.getInst(), resultsSparseArray);
        //arguments
        SparseArray sparseArrayForArguments = new SparseArray();
        SparseArray sparseArrayAdjustmentInfo = new SparseArray();
        FList BhtFldAdjustmentInfoFlist = new FList();
        FList BhtFldAccountingPeriodFlist = new FList();
        BhtFldAccountingPeriodFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
        sparseArrayAdjustmentInfo.add(0, BhtFldAccountingPeriodFlist);
        BhtFldAdjustmentInfoFlist.set((ArrayField) FldAdjustmentInfo.getInst(), sparseArrayAdjustmentInfo);
        sparseArrayForArguments.add(argumentsCounter, BhtFldAdjustmentInfoFlist);
        if (!"null".equals(directorateCode)) {
            argumentsCounter++;
            FList BhtFldDirectorateCodeFlist = new FList();
            BhtFldDirectorateCodeFlist.set((StrField) BhtFldDirectorateCode.getInst(), directorateCode);
            sparseArrayForArguments.add(argumentsCounter, BhtFldDirectorateCodeFlist);
        }
        if (!"null".equals(technologyCode)) {
            argumentsCounter++;
            FList BhtFldTechnologyCodeFlist = new FList();
            BhtFldTechnologyCodeFlist.set((StrField) BhtFldTechnologyCode.getInst(), technologyCode);
            sparseArrayForArguments.add(argumentsCounter, BhtFldTechnologyCodeFlist);
        }
        inputFlistForCorrectionsSearch.set((ArrayField) FldArgs.getInst(), sparseArrayForArguments);

        logger.fine("input Flist for corrections search is:" + inputFlistForCorrectionsSearch);
        logger.exiting("FiscalizationWorker", "createInputFlistForCorrectionsSearch");
        return inputFlistForCorrectionsSearch;
    }

    //this method calls the search opcode
    public FList callSearchOpcodeForCorrections(FList inputFlistForCorrectionsSearch) {
        logger.entering("FiscalizationWorker", "callSearchOpcodeForCorrections");
        FList outputFlistForCorrectionsSearch = new FList();
        try {
            outputFlistForCorrectionsSearch = opcode(7, inputFlistForCorrectionsSearch);
            logger.fine("result of search opcode is:" + outputFlistForCorrectionsSearch);

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "callSearchOpcodeForCorrections");
        return outputFlistForCorrectionsSearch;
    }

    //this method traverses the output flist of search opcode and stores the information in a list of objects 
    public ArrayList<CorrectionsUIFields> getCorrectionItemsUngrouped(FList outputFlistForCorrectionsSearch) throws EBufException {
        logger.entering("FiscalizationWorker", "getCorrectionItemsUngrouped");

        ArrayList<CorrectionsUIFields> listOfCorrectionsItems = new ArrayList();
        //parse the Flist in order to get the corrections items information
        if (outputFlistForCorrectionsSearch.hasField((Field) FldResults.getInst())) {
            SparseArray correctionsDataSparseArray = outputFlistForCorrectionsSearch.get(FldResults.getInst());
            CorrectionsUIFields correctionsUIFields = new CorrectionsUIFields(); //create a new CorrectionsUIFields object to store the information
            if (correctionsDataSparseArray != null) {
                Enumeration<FList> correctionsDataEnumeration = correctionsDataSparseArray.getValueEnumerator();
                while (correctionsDataEnumeration.hasMoreElements()) {
                    FList correctionsDataInformationFlist = correctionsDataEnumeration.nextElement();
                    if (correctionsDataInformationFlist.hasField((Field) FldAdjustmentInfo.getInst())) {
                        SparseArray adjustmentInfoSparseArray = correctionsDataInformationFlist.get(FldAdjustmentInfo.getInst());
                        if (adjustmentInfoSparseArray != null) {
                            Enumeration<FList> enumeration2 = adjustmentInfoSparseArray.getValueEnumerator();
                            while (enumeration2.hasMoreElements()) {
                                FList FldAdjustmentInfoValues = enumeration2.nextElement();
                                correctionsUIFields.setAdjustmentType((FldAdjustmentInfoValues.get(FldAdjustmentType.getInst())).toString());
                                correctionsUIFields.setAdjustmentStatus((FldAdjustmentInfoValues.get(BhtFldFscAdjStatus.getInst())));
                                correctionsUIFields.setAmount(FldAdjustmentInfoValues.get(FldAmount.getInst()));
                                correctionsUIFields.setAdjFiscalNumber(FldAdjustmentInfoValues.get(BhtFldFscAdjNo.getInst()));
                                if (FldAdjustmentInfoValues.get(FldAdjustmentType.getInst()) == 1) {
                                    correctionsUIFields.setFiscalNumber(FldAdjustmentInfoValues.get(BhtFldFiscalNo.getInst()));
                                }
                                //if(correctionsUIFields.getAdjustmentStatus() != 0 && correctionsDataInformationFlist.get((IntField) BhtFldRsSeqNo.getInst()) != 0){
                                correctionsUIFields.setRsSeqAdjNo(FldAdjustmentInfoValues.get((IntField) BhtFldRsSeqAdjNo.getInst()));

                            }
                        }
                    }
                    if (correctionsUIFields.getAdjustmentType().equals("-1")) {
                        correctionsUIFields.setFiscalNumber(correctionsDataInformationFlist.get(BhtFldFiscalNo.getInst()));
                    }
                    correctionsUIFields.setProcessingStage(correctionsDataInformationFlist.get(FldProcessingStage.getInst()));
                    correctionsUIFields.setItemObj(BRMUtility.restIdFromPoid(correctionsDataInformationFlist.get((PoidField) FldItemObj.getInst())));
                    correctionsUIFields.setDirectorateCode(correctionsDataInformationFlist.get((StrField) BhtFldDirectorateCode.getInst()));
                    correctionsUIFields.setTechnologyCode(correctionsDataInformationFlist.get((StrField) BhtFldTechnologyCode.getInst()));
                    correctionsUIFields.setTaxCode(correctionsDataInformationFlist.get(FldTaxCode.getInst()));
                    correctionsUIFields.setRsSeqNo(correctionsDataInformationFlist.get((IntField) BhtFldRsSeqNo.getInst()));

                    listOfCorrectionsItems.add(correctionsUIFields); //add the object in the list
                    correctionsUIFields = new CorrectionsUIFields();
                }
            }
        }
        logger.fine("corrections ungrouped list size is:" + listOfCorrectionsItems.size());

        logger.exiting("FiscalizationWorker", "getCorrectionItemsUngrouped");
        return listOfCorrectionsItems;
    }

    //This method groups the objects by directorate, taxcode, technology, adjustment status, adjustment type and fiscal number into a hashMap
    public HashMap<Object, Object> groupCorrections(ArrayList<CorrectionsUIFields> CorrectionsUngroupedItemsResultList) {
        logger.entering("FiscalizationWorker", "groupCorrections");
        HashMap<Object, Object> mapOfCorrectionsItems = new HashMap();
        ArrayList<Object> listOfKeyValues = new ArrayList();
        ArrayList<Object> listOfValues = new ArrayList();
        boolean flag = false;

        for (CorrectionsUIFields correctionsUngroupedItem : CorrectionsUngroupedItemsResultList) {
            if (correctionsUngroupedItem.getAdjustmentType().equals("-1")) {
                listOfKeyValues.add(correctionsUngroupedItem.getTaxCode());
                listOfKeyValues.add(correctionsUngroupedItem.getDirectorateCode());
                listOfKeyValues.add(correctionsUngroupedItem.getTechnologyCode());
                listOfKeyValues.add(correctionsUngroupedItem.getAdjustmentStatus());
                listOfKeyValues.add(correctionsUngroupedItem.getAdjustmentType());
                listOfKeyValues.add(correctionsUngroupedItem.getFiscalNumber());
                listOfKeyValues.add(correctionsUngroupedItem.getRsSeqNo());

                listOfValues.add(correctionsUngroupedItem.getAdjFiscalNumber());
                listOfValues.add(correctionsUngroupedItem.getAmount());
                listOfValues.add(correctionsUngroupedItem.getProcessingStage());
                listOfValues.add(correctionsUngroupedItem.getItemObj());
                listOfValues.add(correctionsUngroupedItem.getRsSeqAdjNo());

                logger.fine("list of values:" + listOfValues.toString());
                logger.fine("list of keys:" + listOfValues.toString());
                if (mapOfCorrectionsItems.isEmpty()) {
                    mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                    listOfValues = new ArrayList();
                    listOfKeyValues = new ArrayList();
                } else {
                    Iterator<Map.Entry<Object, Object>> iterator = mapOfCorrectionsItems.entrySet().iterator();
                    // Iterate over the HashMap
                    while (iterator.hasNext()) {

                        // Get the entry at this iteration
                        Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();

                        // Check if this key is the required key
                        if (listOfKeyValues.equals(entry.getKey())) {
                            ArrayList listOfHashMapValues = (ArrayList) entry.getValue();
                            BigDecimal amount = (BigDecimal) listOfHashMapValues.get(1);
                            BigDecimal correctionAmount = amount.add(correctionsUngroupedItem.getAmount());
                            listOfValues.set(1, correctionAmount);
                            mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                            listOfValues = new ArrayList();
                            listOfKeyValues = new ArrayList();
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (flag == false) {
                        mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                        listOfValues = new ArrayList();
                        listOfKeyValues = new ArrayList();
                        flag = false;
                    }
                }

                listOfKeyValues = new ArrayList();
                listOfValues = new ArrayList();

            } else if (correctionsUngroupedItem.getAdjustmentType().equals("1")) {
                listOfKeyValues.add(correctionsUngroupedItem.getTaxCode());
                listOfKeyValues.add(correctionsUngroupedItem.getDirectorateCode());
                listOfKeyValues.add(correctionsUngroupedItem.getTechnologyCode());
                listOfKeyValues.add(correctionsUngroupedItem.getAdjustmentStatus());
                listOfKeyValues.add(correctionsUngroupedItem.getAdjustmentType());
                listOfKeyValues.add(correctionsUngroupedItem.getFiscalNumber());
                listOfKeyValues.add(correctionsUngroupedItem.getRsSeqNo());

                listOfValues.add(correctionsUngroupedItem.getAdjFiscalNumber());
                listOfValues.add(correctionsUngroupedItem.getAmount());
                listOfValues.add(correctionsUngroupedItem.getProcessingStage());
                listOfValues.add(correctionsUngroupedItem.getItemObj());
                listOfValues.add(correctionsUngroupedItem.getRsSeqAdjNo());

                logger.fine("list of values:" + listOfValues.toString());
                logger.fine("list of keys:" + listOfValues.toString());

                if (mapOfCorrectionsItems.isEmpty()) {
                    mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                    listOfValues = new ArrayList();
                    listOfKeyValues = new ArrayList();
                } else {
                    Iterator<Map.Entry<Object, Object>> iterator = mapOfCorrectionsItems.entrySet().iterator();
                    // Iterate over the HashMap
                    while (iterator.hasNext()) {

                        // Get the entry at this iteration
                        Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();

                        // Check if this key is the required key
                        if (listOfKeyValues.equals(entry.getKey())) {
                            ArrayList listOfHashMapValues = (ArrayList) entry.getValue();
                            BigDecimal amount = (BigDecimal) listOfHashMapValues.get(1);
                            BigDecimal correctionAmount = amount.add(correctionsUngroupedItem.getAmount());
                            listOfValues.set(1, correctionAmount);
                            mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                            listOfValues = new ArrayList();
                            listOfKeyValues = new ArrayList();
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (flag == false) {
                        mapOfCorrectionsItems.put(listOfKeyValues, listOfValues);
                        listOfValues = new ArrayList();
                        listOfKeyValues = new ArrayList();
                        flag = false;
                    }
                }

                listOfKeyValues = new ArrayList();
                listOfValues = new ArrayList();
            }
        }
        logger.fine("Map of corrections items is ==" + mapOfCorrectionsItems.toString());
        logger.exiting("FiscalizationWorker", "groupCorrections");

        return mapOfCorrectionsItems;
    }

    //This method iteterates the hashmap and stores the grouped corrections into a list of objects
    public ArrayList<CorrectionsUIFields> getCorrectionsItems(HashMap<Object, Object> mapOfGroupedCorrectionsItems) {
        logger.exiting("FiscalizationWorker", "getCorrectionsItems");
        ArrayList<CorrectionsUIFields> arrayListOfCorrectionsData = new ArrayList();

        //iterate the merged hashMap
        Iterator<Map.Entry<Object, Object>> iterator = mapOfGroupedCorrectionsItems.entrySet().iterator();
        // Iterate over the HashMap
        while (iterator.hasNext()) {
            CorrectionsUIFields correctionsUIFields = new CorrectionsUIFields(); //instatiate a GroupedFiscalizedFields object 
            // Get the entry at this iteration
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();

            //iterate the hashMaps values
            ArrayList listOfHashMapValues = (ArrayList) entry.getValue();
            String AdjFiscalNo = (String) listOfHashMapValues.get(0);
            correctionsUIFields.setAdjFiscalNumber(AdjFiscalNo);
            BigDecimal amount = ((BigDecimal) listOfHashMapValues.get(1));
            correctionsUIFields.setAmount(amount);
            int processingStage = (Integer) listOfHashMapValues.get(2);
            correctionsUIFields.setProcessingStage(processingStage);
            String itemObj = (String) listOfHashMapValues.get(3);
            correctionsUIFields.setItemObj(itemObj);
            int rsSeqAdjNo = (Integer) listOfHashMapValues.get(4);
            correctionsUIFields.setRsSeqAdjNo(rsSeqAdjNo);

            //iterate the hashMaps key values
            ArrayList listOfHashMapKeyValues = (ArrayList) entry.getKey();
            correctionsUIFields.setTaxCode((String) listOfHashMapKeyValues.get(0));
            correctionsUIFields.setDirectorateCode((String) listOfHashMapKeyValues.get(1));
            DirectoratesInfo directoratesInfo = getDirectoratesDescription(correctionsUIFields.getDirectorateCode());
            correctionsUIFields.setDirectorateDescription(directoratesInfo.getDirectorateName());
            correctionsUIFields.setTechnologyCode((String) listOfHashMapKeyValues.get(2));
            correctionsUIFields.setAdjustmentStatus((Integer) listOfHashMapKeyValues.get(3));
            if (listOfHashMapKeyValues.get(4).equals("-1")) {
                correctionsUIFields.setAdjustmentType("Storno");
            } else {
                correctionsUIFields.setAdjustmentType("Povećanje");
            }
            correctionsUIFields.setFiscalNumber((String) listOfHashMapKeyValues.get(5));
            int rsSeqNo = (Integer) listOfHashMapKeyValues.get(6);
            correctionsUIFields.setRsSeqNo(rsSeqNo);

            //adding the GroupedFiscalizedFields object into arraylist:
            arrayListOfCorrectionsData.add(correctionsUIFields);
        }

        logger.exiting("FiscalizationWorker", "getCorrectionsItems");
        return arrayListOfCorrectionsData;
    }

    public List<String> getIndividualItemInformationForCorrectionPrint(String itemObj) {
        logger.entering("FiscalizationWorker", "getIndividualItemInformationForCorrectionPrint");
        long l = getCurrentDB();
        Poid itemObjField = BRMUtility.poidFromRestId(itemObj);
        List<String> results = new ArrayList();

        //construct the input flist:
        FList inputSearchFlist = new FList();
        inputSearchFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        inputSearchFlist.set((IntField) FldFlags.getInst(), 1024);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /bht_commission_sale 1, /bht_installment_plan 2 where 1.F1 = 2.F2 and 2.F3 = V3 ");
        inputSearchFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        //set Results array:
        SparseArray resultsArray = new SparseArray();
        FList resultsFlist = new FList();
        resultsFlist.set((Field) BhtFldGlCode.getInst());
        resultsFlist.set((Field) FldProductObj.getInst());
        resultsFlist.set((Field) FldEventObj.getInst());
        resultsFlist.set((Field) BhtFldOrderItemNum.getInst());
        resultsArray.add(0, resultsFlist);
        inputSearchFlist.set((ArrayField) FldResults.getInst(), resultsArray);
        //arguments:
        SparseArray sparseArrayArgs = new SparseArray();
        FList BhtFldInstallmentPlanObjFlist = new FList();
        BhtFldInstallmentPlanObjFlist.set((PoidField) BhtFldInstallmentPlanObj.getInst());
        sparseArrayArgs.add(1, BhtFldInstallmentPlanObjFlist);
        FList FldPoidFlist = new FList();
        FldPoidFlist.set((PoidField) FldPoid.getInst());
        sparseArrayArgs.add(2, FldPoidFlist);
        FList BhtFldDeviceSaleItemObjFlist = new FList();
        BhtFldDeviceSaleItemObjFlist.set((PoidField) BhtFldDeviceSaleItemObj.getInst(), itemObjField);
        sparseArrayArgs.add(3, BhtFldDeviceSaleItemObjFlist);
        inputSearchFlist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);
        logger.fine("input flist in order to get information for individual corrections item is: \n" + inputSearchFlist);

        try {
            FList outputSearchFlist = opcode(7, inputSearchFlist);
            logger.fine("output flist in order to get information for individual corrections item is:" + outputSearchFlist);

            //parse the output flist in order to get the result fields
            if (outputSearchFlist.hasField((Field) FldResults.getInst())) {
                SparseArray array = outputSearchFlist.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fList = enumeration.nextElement();

                        results.add(fList.get(BhtFldGlCode.getInst())); //glId

                        //read product object in order to get description-sifra
                        FList readProductFlist = readObject(fList.get(FldProductObj.getInst()));
                        results.add(readProductFlist.get((StrField) FldDescr.getInst()));

                        //order number
                        results.add(fList.get(BhtFldOrderItemNum.getInst()));

                        //searching for external user/ userCode
                        Poid eventObjectId = fList.get((PoidField) FldEventObj.getInst());
                        results.add(UserActivitySearch(eventObjectId));

                    }
                }
            }

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getIndividualItemInformationForCorrectionPrint");
        return results;
    }

    //this method calls the ws for printing the individual correction record
    public FiscalPrinterResultFields printIndividualFiscalizationInvoiceForCorrections(BigDecimal amount, String taxCode, String glId, String description, String printerId, int typeOfRequest) throws ParserConfigurationException, EBufException, SAXException {
        logger.entering("FiscalizationWorker", "printIndividualFiscalizationInvoice");
        FiscalPrinterResultFields fiscalPrinterResultFields = new FiscalPrinterResultFields();

        //call the ws in order to get the endpoint of the printer id 
        String endpoint = getEndpointForPrinterId(printerId);
        if (endpoint == null) {
            logger.info("FiscalizationWorker, endpoint has null value");
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "endpoint from ws has null value", new Object[0]);
        }
        logger.info("FiscalizationWorker, endpoint:" + endpoint);

        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("callWsForFiscalPrintersUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String numberOfRequest = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
            String unitOfMeasure = "kom";
            int group = 0;
            int plu = 0;
            String paymentMethod = "Virman";
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://www.bhtelecom.ba/FiscalPrinter/types\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <typ:printInvoiceInputElement>\n"
                    + "         <typ:BrojZahtjeva>" + numberOfRequest + "</typ:BrojZahtjeva>\n"
                    + "         <typ:VrstaZahtjeva>" + typeOfRequest + "</typ:VrstaZahtjeva>\n"
                    + "         <typ:PrinterEndpoint>" + endpoint + "</typ:PrinterEndpoint>\n"
                    + "         <typ:Kupac>\n"
                    + "            <typ:IDBroj></typ:IDBroj>\n"
                    + "            <typ:Naziv></typ:Naziv>\n"
                    + "            <typ:Adresa></typ:Adresa>\n"
                    + "            <typ:Grad></typ:Grad>\n"
                    + "            <typ:PostanskiBroj></typ:PostanskiBroj>\n"
                    + "         </typ:Kupac>\n"
                    + "         <typ:RacunStavka>\n"
                    + "            <typ:Artikal>\n"
                    + "               <typ:Sifra>" + glId + "</typ:Sifra>\n"
                    + "               <typ:Naziv>" + description + "</typ:Naziv>\n"
                    + "               <typ:JM>" + unitOfMeasure + "</typ:JM>\n"
                    + "               <typ:Cijena>" + amount + "</typ:Cijena>\n"
                    + "               <typ:Stopa>" + taxCode + "</typ:Stopa>\n"
                    + "               <typ:Grupa>" + group + "</typ:Grupa>\n"
                    + "               <typ:PLU>" + plu + "</typ:PLU>\n"
                    + "            </typ:Artikal>\n"
                    + "            <typ:Kolicina>1</typ:Kolicina>\n"
                    + "            <typ:Rabat>0</typ:Rabat>\n"
                    + "         </typ:RacunStavka>\n"
                    + "         <typ:BrojRacuna></typ:BrojRacuna>\n"
                    + "         <typ:NacinPlacanja>" + paymentMethod + "</typ:NacinPlacanja>\n"
                    + "      </typ:printInvoiceInputElement>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            logger.severe("The sent xml in order to get the invoice number is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status == " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response == \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "typ:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName(prefix + "InvoiceNumber").item(0).getTextContent());
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName(prefix + "InvoiceStatus").item(0).getTextContent());
                fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName(prefix + "ErrorMessage").item(0).getTextContent()); */
            NodeList errNodes = doc.getElementsByTagName("typ:printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("typ:InvoiceNumber").item(0).getTextContent());
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName("typ:InvoiceStatus").item(0).getTextContent());
                fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("typ:ErrorMessage").item(0).getTextContent());
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service FiscalPrinter", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a response, timeout", new Object[0]);
        } catch (IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        } catch (SAXException ex) {
            logger.severe("FiscalizationWorker, error while calling the web service ", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "printIndividualFiscalizationInvoice");
        return fiscalPrinterResultFields;

    }

    //this method calls the ws in order to print the grouped corrections records
    public FiscalPrinterResultFields printGroupedCorrectionsFiscalizationInvoice(BigDecimal netAmount, String taxCode, String description, String endpoint, int typeOfRequest) throws IOException, ParserConfigurationException {
        logger.entering("FiscalizationWorker", "printGroupedFiscalizationInvoice");
        FiscalPrinterResultFields fprf = new FiscalPrinterResultFields();

        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("callWsForFiscalPrintersUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String numberOfRequest = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
            String unitOfMeasure = "kom";
            int group = 0;
            int plu = 0;
            String code = "123456";
            String paymentMethod = "Virman";
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://www.bhtelecom.ba/FiscalPrinter/types\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <typ:printInvoiceInputElement>\n"
                    + "         <typ:BrojZahtjeva>" + numberOfRequest + "</typ:BrojZahtjeva>\n"
                    + "         <typ:VrstaZahtjeva>" + typeOfRequest + "</typ:VrstaZahtjeva>\n"
                    + "         <typ:PrinterEndpoint>" + endpoint + "</typ:PrinterEndpoint>\n"
                    + "         <typ:Kupac>\n"
                    + "            <typ:IDBroj></typ:IDBroj>\n"
                    + "            <typ:Naziv></typ:Naziv>\n"
                    + "            <typ:Adresa></typ:Adresa>\n"
                    + "            <typ:Grad></typ:Grad>\n"
                    + "            <typ:PostanskiBroj></typ:PostanskiBroj>\n"
                    + "         </typ:Kupac>\n"
                    + "         <typ:RacunStavka>\n"
                    + "            <typ:Artikal>\n"
                    + "               <typ:Sifra>" + code + "</typ:Sifra>\n"
                    + "               <typ:Naziv>" + description + "</typ:Naziv>\n"
                    + "               <typ:JM>" + unitOfMeasure + "</typ:JM>\n"
                    + "               <typ:Cijena>" + netAmount + "</typ:Cijena>\n"
                    + "               <typ:Stopa>" + taxCode + "</typ:Stopa>\n"
                    + "               <typ:Grupa>" + group + "</typ:Grupa>\n"
                    + "               <typ:PLU>" + plu + "</typ:PLU>\n"
                    + "            </typ:Artikal>\n"
                    + "            <typ:Kolicina>1</typ:Kolicina>\n"
                    + "            <typ:Rabat>0</typ:Rabat>\n"
                    + "         </typ:RacunStavka>\n"
                    + "         <typ:BrojRacuna></typ:BrojRacuna>\n"
                    + "         <typ:NacinPlacanja>" + paymentMethod + "</typ:NacinPlacanja>\n"
                    + "      </typ:printInvoiceInputElement>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the invoice number is: \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response: \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));

            /*String prefix = "";
            if (BRMUtility.getPropertyValue("ENVIRONMENT").equals("BST") || BRMUtility.getPropertyValue("ENVIRONMENT").equals("BRM2"))
                prefix = "typ:";
            NodeList errNodes = doc.getElementsByTagName(prefix + "printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fprf.setInvoiceNumber(err.getElementsByTagName(prefix + "InvoiceNumber").item(0).getTextContent());
                fprf.setErrorMessage(err.getElementsByTagName(prefix + "ErrorMessage").item(0).getTextContent());
                fprf.setInvoiceStatus(err.getElementsByTagName(prefix + "InvoiceStatus").item(0).getTextContent());
            } */
            NodeList errNodes = doc.getElementsByTagName("typ:printInvoiceOutputElement");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fprf.setInvoiceNumber(err.getElementsByTagName("typ:InvoiceNumber").item(0).getTextContent());
                fprf.setErrorMessage(err.getElementsByTagName("typ:ErrorMessage").item(0).getTextContent());
                fprf.setInvoiceStatus(err.getElementsByTagName("typ:InvoiceStatus").item(0).getTextContent());
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service FiscalPrinter", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a response, exception timeout ", new Object[0]);
        } catch (IOException | SAXException e) {
            logger.severe("FiscalizationWorker, error while calling the web service", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrinter didn't bring a valid response", new Object[0]);
        }
        logger.exiting("FiscalizationWorker", "printGroupedFiscalizationInvoice");
        return fprf;

    }

    //invoke the update opcode
    public FiscalUpdateResults invokeFiscalUpdateAdjFiscalNoOpcode(FiscalPrinterResultFields returnedFieldsFromWs, String directorateCode, String technologyCode, String taxCode, String fiscalNumber, String accountingPeriod, String adjustmentType, int adjustmentStatus, int sequenceNumber, int sequenceNumberAdj) {
        logger.entering("FiscalizationWorker", "invokeFiscalUpdateAdjFiscalNoOpcode");
        FList inputFlist = new FList();
        int adjustmentTypeConverted;
        long dbNumber = getCurrentDB();
        FiscalUpdateResults fiscalUpdateResultsList = new FiscalUpdateResults();

        if (adjustmentType.equals("Storno")) {
            adjustmentTypeConverted = -1;
        } else {
            adjustmentTypeConverted = 1;
        }

        //in case the printer doesn't return a fiscal no we give -1 by default
        if (returnedFieldsFromWs.getInvoiceNumber().equals("-1")) {
            adjustmentStatus = 2;
        }

        boolean flag = false;
        //in case status 3, it means that is processed without fiscal number
        if (adjustmentStatus == 3) {
            flag = true;
            adjustmentStatus = 0;
        }

        if (returnedFieldsFromWs.getInvoiceStatus().equals("OK")) {
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/bht_fiscal_item"));
            inputFlist.set(BhtFldDirectorateCode.getInst(), directorateCode);
            inputFlist.set(BhtFldTechnologyCode.getInst(), technologyCode);
            inputFlist.set(BhtFldAccountingPeriod.getInst(), accountingPeriod);
            inputFlist.set(FldTaxCode.getInst(), taxCode);
            inputFlist.set(FldAdjustmentType.getInst(), adjustmentTypeConverted);
            inputFlist.set(BhtFldRsSeqNo.getInst(), sequenceNumber);

            if (returnedFieldsFromWs.getInvoiceNumber().equals("-1")) {
                inputFlist.set(BhtFldFscAdjNo.getInst(), "");
                inputFlist.set((StrField) BhtFldFiscalNo.getInst(), fiscalNumber);
            } else if (adjustmentTypeConverted == 1) {
                inputFlist.set(BhtFldFscAdjNo.getInst(), "");
                inputFlist.set((StrField) BhtFldFiscalNo.getInst(), returnedFieldsFromWs.getInvoiceNumber());
            } else if (adjustmentTypeConverted == -1) {
                inputFlist.set((StrField) BhtFldFiscalNo.getInst(), fiscalNumber);
                inputFlist.set(BhtFldFscAdjNo.getInst(), returnedFieldsFromWs.getInvoiceNumber());
            }
            inputFlist.set(BhtFldFscAdjStatus.getInst(), adjustmentStatus + 1);
            if (sequenceNumber != 0 && flag == false) {
                inputFlist.set(BhtFldRsSeqAdjNo.getInst(), Integer.parseInt(returnedFieldsFromWs.getNewSequenceNumber()));
            } else {
                inputFlist.set(BhtFldRsSeqAdjNo.getInst(), sequenceNumberAdj);
            }
            logger.fine("input flist for update corrections adj fiscal number is: \n" + inputFlist);

            try {
                FList outputFList = opcode(10026, inputFlist);
                logger.fine("ouput flist for update corrections adj fiscal number is: \n" + outputFList);

                fiscalUpdateResultsList.setFiscalNumber(returnedFieldsFromWs.getInvoiceNumber());
                fiscalUpdateResultsList.setPoid(BRMUtility.restIdFromPoid(outputFList.get(FldPoid.getInst())));
                fiscalUpdateResultsList.setMessage(outputFList.get(FldCount.getInst()).toString());

            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem while invoking the BHT_OP_FISCAL_UPDATE_ADJ_FISCAL_NO opcode: \n", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), " Problem while invoking the BHT_OP_FISCAL_UPDATE_ADJ_FISCAL_NO opcode ", new Object[0]);
            }

        } else {
            logger.severe("FiscalizationWorker, error while calling the web service: " + returnedFieldsFromWs.getErrorMessage());
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), returnedFieldsFromWs.getErrorMessage(), new Object[0]);

        }
        logger.exiting("FiscalizationWorker", "invokeFiscalUpdateAdjFiscalNoOpcode");
        return fiscalUpdateResultsList;
    }

    //invoke the update opcode
    public FiscalUpdateResults invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate(FiscalPrinterResultFields returnedFieldsFromWs, String directorateCode, String technologyCode, String taxCode, String fiscalNumber, String accountingPeriod, String adjustmentType, int adjustmentStatus, String adjFiscalNumber, int sequenceNumber, int sequenceNumberAdj) {
        logger.entering("FiscalizationWorker", "invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate");
        FList inputFlist = new FList();
        int adjustmentTypeConverted;
        long dbNumber = getCurrentDB();
        FiscalUpdateResults fiscalUpdateResultsList = new FiscalUpdateResults();
        if (adjustmentType.equals("Storno")) {
            adjustmentTypeConverted = -1;
        } else {
            adjustmentTypeConverted = 1;
        }

        if (returnedFieldsFromWs.getInvoiceStatus().equals("OK")) {
            inputFlist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/bht_fiscal_item"));
            inputFlist.set((StrField) BhtFldFiscalNo.getInst(), fiscalNumber);
            inputFlist.set(BhtFldDirectorateCode.getInst(), directorateCode);
            inputFlist.set(BhtFldTechnologyCode.getInst(), technologyCode);
            inputFlist.set(BhtFldAccountingPeriod.getInst(), accountingPeriod);
            inputFlist.set(FldTaxCode.getInst(), taxCode);
            inputFlist.set(FldAdjustmentType.getInst(), adjustmentTypeConverted);
            inputFlist.set(BhtFldFscAdjNo.getInst(), adjFiscalNumber);
            inputFlist.set(BhtFldFscAdjStatus.getInst(), adjustmentStatus + 1);
            inputFlist.set(BhtFldRsSeqAdjNo.getInst(), sequenceNumberAdj);
            inputFlist.set(BhtFldRsSeqNo.getInst(), sequenceNumber);

            logger.fine("input flist for update corrections adj fiscal number is: " + inputFlist);

            try {
                FList outputFList = opcode(10026, inputFlist);
                logger.fine("ouput flist for update corrections adj fiscal number is: " + outputFList);

                fiscalUpdateResultsList.setFiscalNumber(returnedFieldsFromWs.getInvoiceNumber());
                fiscalUpdateResultsList.setPoid(BRMUtility.restIdFromPoid(outputFList.get(FldPoid.getInst())));
                fiscalUpdateResultsList.setMessage(outputFList.get(FldCount.getInst()).toString());

            } catch (EBufException exception) {
                logger.severe("FiscalizationWorker, Problem while invoking the BHT_OP_FISCAL_UPDATE_ADJ_FISCAL_NO opcode ", exception);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), " Problem while invoking the BHT_OP_FISCAL_UPDATE_ADJ_FISCAL_NO opcode ", new Object[0]);
            }

        } else {
            logger.severe("FiscalizationWorker, error while calling the web service: " + returnedFieldsFromWs.getErrorMessage());
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), returnedFieldsFromWs.getErrorMessage(), new Object[0]);

        }
        logger.exiting("FiscalizationWorker", "invokeFiscalUpdateAdjFiscalNoOpcodeForCancelDuplicate");
        return fiscalUpdateResultsList;
    }

    //this method creates the input flist in order to search for corrections in structure bht_fiscal_item that we need to batch print
    public FList createInputFlistForCorrectionsBatchPrintSearch(String directorateCode, String technologyCode, String accountingPeriod) {
        logger.entering("FiscalizationWorker", "createInputFlistForCorrectionsBatchPrintSearch");
        long l = getCurrentDB();
        int argumentsCounter = 1;
        int templateArgsCounter = 1;
        FList inputFlistForCorrectionsSearch = new FList();
        inputFlistForCorrectionsSearch.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        inputFlistForCorrectionsSearch.set((IntField) FldFlags.getInst(), 512);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /bht_fiscal_item where F1 = V1 and F2 = V2 ");
        if (!"null".equals(directorateCode)) {
            templateArgsCounter++;
            stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
        }
        if (!"null".equals(technologyCode)) {
            templateArgsCounter++;
            stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
        }
        inputFlistForCorrectionsSearch.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        //results
        SparseArray resultsSparseArray = new SparseArray();
        FList resultsFlist = new FList();
        resultsFlist.set((Field) FldPoid.getInst());
        resultsFlist.set((Field) BhtFldDirectorateCode.getInst());
        resultsFlist.set((Field) BhtFldTechnologyCode.getInst());
        resultsFlist.set((Field) FldItemObj.getInst());
        resultsFlist.set((Field) FldTaxCode.getInst());
        resultsFlist.set((Field) FldProcessingStage.getInst());
        resultsFlist.set((Field) BhtFldRsSeqNo.getInst());
        FList adjustmentInfoSubArrayFList = new FList();
        adjustmentInfoSubArrayFList.set((Field) BhtFldFiscalNo.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldFscAdjStatus.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldFscAdjNo.getInst());
        adjustmentInfoSubArrayFList.set((Field) FldAdjustmentType.getInst());
        adjustmentInfoSubArrayFList.set((Field) FldAmount.getInst());
        adjustmentInfoSubArrayFList.set((Field) BhtFldRsSeqAdjNo.getInst());
        resultsFlist.setElement(FldAdjustmentInfo.getInst(), ELEMID_ANY, adjustmentInfoSubArrayFList);
        resultsSparseArray.add(0, resultsFlist);
        inputFlistForCorrectionsSearch.set((ArrayField) FldResults.getInst(), resultsSparseArray);
        //arguments
        SparseArray sparseArrayForArguments = new SparseArray();
        SparseArray sparseArrayAdjustmentInfo = new SparseArray();
        FList BhtFldAdjustmentInfoArg1Flist = new FList();
        FList BhtFldAccountingPeriodFlist = new FList();
        BhtFldAccountingPeriodFlist.set((StrField) BhtFldAccountingPeriod.getInst(), accountingPeriod);
        sparseArrayAdjustmentInfo.add(0, BhtFldAccountingPeriodFlist);
        BhtFldAdjustmentInfoArg1Flist.set((ArrayField) FldAdjustmentInfo.getInst(), sparseArrayAdjustmentInfo);
        sparseArrayForArguments.add(argumentsCounter, BhtFldAdjustmentInfoArg1Flist);
        argumentsCounter++;
        SparseArray sparseArrayAdjustmentInfo2 = new SparseArray();
        FList BhtFldAdjustmentInfoArg2Flist = new FList();
        FList BhtFldFscAdjStatusFlist = new FList();
        BhtFldFscAdjStatusFlist.set((EnumField) BhtFldFscAdjStatus.getInst(), 0);
        sparseArrayAdjustmentInfo2.add(0, BhtFldFscAdjStatusFlist);
        BhtFldAdjustmentInfoArg2Flist.set((ArrayField) FldAdjustmentInfo.getInst(), sparseArrayAdjustmentInfo2);
        sparseArrayForArguments.add(argumentsCounter, BhtFldAdjustmentInfoArg2Flist);
        if (!"null".equals(directorateCode)) {
            argumentsCounter++;
            FList BhtFldDirectorateCodeFlist = new FList();
            BhtFldDirectorateCodeFlist.set((StrField) BhtFldDirectorateCode.getInst(), directorateCode);
            sparseArrayForArguments.add(argumentsCounter, BhtFldDirectorateCodeFlist);
        }
        if (!"null".equals(technologyCode)) {
            argumentsCounter++;
            FList BhtFldTechnologyCodeFlist = new FList();
            BhtFldTechnologyCodeFlist.set((StrField) BhtFldTechnologyCode.getInst(), technologyCode);
            sparseArrayForArguments.add(argumentsCounter, BhtFldTechnologyCodeFlist);
        }
        inputFlistForCorrectionsSearch.set((ArrayField) FldArgs.getInst(), sparseArrayForArguments);

        logger.fine("input Flist for corrections search is:" + inputFlistForCorrectionsSearch);
        logger.exiting("FiscalizationWorker", "createInputFlistForCorrectionsBatchPrintSearch");
        return inputFlistForCorrectionsSearch;
    }

    public FiscalPrinterResultFields printFiscalBillForRS(BigDecimal amount, String taxCode, String glId, String description, String orderNumber, String cashRegisterId, int typeOfRequest, String fiscalNumber) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "printFiscalBillForRS");
        FiscalPrinterResultFields fiscalPrinterResultFields = new FiscalPrinterResultFields();

        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("printFiscalBillUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int rate = 2;
            if ("E".equals(taxCode)) {
                rate = 1;
            }
            con.setRequestMethod("POST");
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            //con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String isDecreaseCorrection = null;
            String xml2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com/\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <com:PrintFiscalBill>\n"
                    + "         <!--Optional:-->\n"
                    + "         <ordItemNum>" + orderNumber + "</ordItemNum>\n"
                    + "         <!--Optional:-->\n"
                    + "         <cashRegisterId>" + cashRegisterId + "</cashRegisterId>\n"
                    + "         <!--Optional:-->\n"
                    + "         <revenueCode>" + glId + "</revenueCode>\n"
                    + "         <!--Optional:-->\n"
                    + "         <name>" + description + "</name>\n"
                    + "         <!--Optional:-->\n"
                    + "         <price>" + amount + "</price>\n"
                    + "         <!--Optional:-->\n"
                    + "         <taxRate>" + rate + "</taxRate>\n"
                    + "         <!--Optional:-->\n"
                    + "         <quantity>1</quantity>\n"
                    + "         <typeOfRequest>" + typeOfRequest + "</typeOfRequest>\n";
            if (typeOfRequest == 2) {
                isDecreaseCorrection = "<billNumberForCancellation>" + fiscalNumber + "</billNumberForCancellation>\n";
            }
            String xml = xml2 + isDecreaseCorrection
                    + "      </com:PrintFiscalBill>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            logger.severe("The sent xml in order to get the invoice number is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status == " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service response == \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("return");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName("statusCode").item(0).getTextContent());
                if (typeOfRequest == 2) { // case for decrease correction for RS
                    if (fiscalPrinterResultFields.getInvoiceStatus().equals("0")) {
                        if ((err.getElementsByTagName("numFiscBill").item(0) != null) && (err.getElementsByTagName("numFiscBillWhenCancelled").item(0) != null)) {
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                            fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("numFiscBill").item(0).getTextContent());
                            fiscalPrinterResultFields.setNewSequenceNumber(err.getElementsByTagName("seqNumRS").item(0).getTextContent());
                        } else {
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                            fiscalPrinterResultFields.setInvoiceNumber("-1");
                            fiscalPrinterResultFields.setNewSequenceNumber(err.getElementsByTagName("seqNumRS").item(0).getTextContent());
                        }

                    } else {
                        fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("statusDesc").item(0).getTextContent());
                    }

                } else { // case for increase correction for RS

                    if (fiscalPrinterResultFields.getInvoiceStatus().equals("0")) {
                        if (err.getElementsByTagName("numFiscBill").item(0) != null) {
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                            fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("numFiscBill").item(0).getTextContent());
                            fiscalPrinterResultFields.setNewSequenceNumber(err.getElementsByTagName("seqNumRS").item(0).getTextContent());
                        } else {
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                            fiscalPrinterResultFields.setInvoiceNumber("-1");
                            fiscalPrinterResultFields.setNewSequenceNumber(err.getElementsByTagName("seqNumRS").item(0).getTextContent());
                        }
                    } else {
                        fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("statusDesc").item(0).getTextContent());
                    }
                }

            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service PrintFiscalBill", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrintRS didn't bring a response, timeout", new Object[0]);
        } catch (IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service PrintFiscalBill", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrintRS didn't bring a valid response", new Object[0]);
        } catch (SAXException ex) {
            logger.severe("FiscalizationWorker, error while calling the web service PrintFiscalBill", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service FiscalPrintRS didn't bring a valid response", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "printFiscalBillForRS");
        return fiscalPrinterResultFields;
    }

    public FiscalPrinterResultFields getFiscalNumberForRSBySeqNoForCorrections(int sequenceNumberAdj, int typeOfRequest) throws ParserConfigurationException {
        logger.entering("FiscalizationWorker", "getFiscalNumberForRSBySeqNo");
        FiscalPrinterResultFields fiscalPrinterResultFields = new FiscalPrinterResultFields();
        try {
            Properties prop = new Properties();
            InputStream ip = getClass().getClassLoader().getResourceAsStream("custom/conf.properties");
            prop.load(ip);
            String url = prop.getProperty("getFiscalNumberByRSseqNoUrl");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(40000);
            con.setReadTimeout(40000);
            con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            //con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com/\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <com:GetFiscalNumberByRSseqNo>\n"
                    + "         <!--Optional:-->\n"
                    + "         <rsSeqNo>" + sequenceNumberAdj + "</rsSeqNo>\n"
                    + "      </com:GetFiscalNumberByRSseqNo>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            logger.severe("The sent xml in order to get the fiscal number from RS is == \n" + xml);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();
            String responseStatus = con.getResponseMessage();
            logger.severe("Response status: " + responseStatus);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.severe("Web service GetFiscalNumberByRSseqNo response is: \n" + response.toString());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
            NodeList errNodes = doc.getElementsByTagName("return");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                fiscalPrinterResultFields.setInvoiceStatus(err.getElementsByTagName("statusCode").item(0).getTextContent());
                if (typeOfRequest == 2) {
                    if (fiscalPrinterResultFields.getInvoiceStatus().equals("0") && err.getElementsByTagName("numFiscBillWhenCancelled").item(0) != null ) {
                        if (err.getElementsByTagName("numFiscBill").item(0) != null) {
                            fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("numFiscBill").item(0).getTextContent());
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                        } else {
                            //ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't return a fiscal number", new Object[0]);
                            fiscalPrinterResultFields.setErrorMessage("the web service getFiscalNumberByRSseqNo didn't return a fiscal number");
                        }
                    } else if (fiscalPrinterResultFields.getInvoiceStatus().equals("1")) {
                        fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("statusDesc").item(0).getTextContent());
                    }
                } else {
                    if (fiscalPrinterResultFields.getInvoiceStatus().equals("0")) {
                        if (err.getElementsByTagName("numFiscBill").item(0) != null) {
                            fiscalPrinterResultFields.setInvoiceNumber(err.getElementsByTagName("numFiscBill").item(0).getTextContent());
                            fiscalPrinterResultFields.setInvoiceStatus("OK");
                        } else {
                            //ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't return a fiscal number", new Object[0]);
                            fiscalPrinterResultFields.setErrorMessage("the web service getFiscalNumberByRSseqNo didn't return a fiscal number");
                        }
                    } else if (fiscalPrinterResultFields.getInvoiceStatus().equals("1")) {
                        fiscalPrinterResultFields.setErrorMessage(err.getElementsByTagName("statusDesc").item(0).getTextContent());
                    }
                }

            } else {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't return the return parameters", new Object[0]);
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.severe("FiscalizationWorker, there was a timeout while calling the web service getFiscalNumberByRSseqNo", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't bring a response, exception timeout ", new Object[0]);
        } catch (SAXException | IOException e) {
            logger.severe("FiscalizationWorker, error while calling the web service getFiscalNumberByRSseqNo", e);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "the web service getFiscalNumberByRSseqNo didn't bring a valid response ", new Object[0]);
        }

        logger.exiting("FiscalizationWorker", "getFiscalNumberForRSBySeqNo");
        return fiscalPrinterResultFields;
    }

}
