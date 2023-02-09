package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.enums.ErrorConstants;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseWorker;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.portal.pcm.ArrayField;
import com.portal.pcm.EBufException;
import static com.portal.pcm.Element.ELEMID_ANY;
import com.portal.pcm.EnumField;
import com.portal.pcm.FList;
import com.portal.pcm.Field;
import com.portal.pcm.IntField;
import com.portal.pcm.Poid;
import com.portal.pcm.PoidField;
import com.portal.pcm.SparseArray;
import com.portal.pcm.StrField;
import com.portal.pcm.TStampField;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldArBillinfoObj;
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldBalImpacts;
import com.portal.pcm.fields.FldBillObj;
import com.portal.pcm.fields.FldBillinfoObj;
import com.portal.pcm.fields.FldEndT;
import com.portal.pcm.fields.FldEvents;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldIncludeChildren;
import com.portal.pcm.fields.FldItemObj;
import com.portal.pcm.fields.FldItems;
import com.portal.pcm.fields.FldLastBillT;
import com.portal.pcm.fields.FldNextBillT;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldProgramName;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldTemplate;
import com.portal.pcm.fields.FldValidFrom;
import customfields.BhtFldInstallments;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class HotBillingWorker extends PCMBaseWorker {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.HotBillingWorker.class);

    public HotBillingWorker() {

    }

    //This method finds the installments of an account and stores them in a list
    public ArrayList<Poid> getInstallmentPlans(String billInfo) {
        logger.entering("HotBillingWorker", "getInstallmentPlans");
        FList flist = new FList();
        long dbNumber = getCurrentDB();
        int argsCount = 1;
        ArrayList<Poid> installments = new ArrayList<>();

        try {
            //construct the search input flist
            flist.set((PoidField) FldPoid.getInst(), new Poid(dbNumber, -1L, "/search"));
            flist.set((IntField) FldFlags.getInst(), 256);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /bht_installment_plan 1, /billinfo 2 where 2.F1 = V1 and ( 1.F2 = V2 or 1.F3 = V3 ) and (1.F4 < 2.F6 or ( 1.F4 < 2.F7 and 1.F5 >= 2.F6 and 1.F5 < 2.F7 ) ) and 1.F8 = 2.F9 and not exists ( select 1 from bht_installment_t where bht_installment_t.status = 3 and bht_installment_plan_t.poid_id0 = bht_installment_t.obj_id0 )");
            flist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            //set results list
            SparseArray resultsArray = new SparseArray();
            FList PoidFlist = new FList();
            PoidFlist.set((PoidField) FldPoid.getInst());
            resultsArray.add(0, PoidFlist);
            flist.set((ArrayField) FldResults.getInst(), resultsArray);
            //set Args list
            SparseArray sparseArrayArgs = new SparseArray();
            FList ArBillInfoFlist = new FList();
            ArBillInfoFlist.set((PoidField) FldArBillinfoObj.getInst(), BRMUtility.poidFromRestId(billInfo));
            sparseArrayArgs.add(argsCount, ArBillInfoFlist);
            argsCount++;
            FList FldStatusFlist = new FList();
            FldStatusFlist.set((EnumField) FldStatus.getInst(), 1);
            sparseArrayArgs.add(argsCount, FldStatusFlist);
            argsCount++;
            FList FldStatus2Flist = new FList();
            FldStatus2Flist.set((EnumField) FldStatus.getInst(), 2);
            sparseArrayArgs.add(argsCount, FldStatus2Flist);
            argsCount++;
            FList FldValidFromFlist = new FList();
            FldValidFromFlist.set((TStampField) FldValidFrom.getInst());
            sparseArrayArgs.add(argsCount, FldValidFromFlist);
            argsCount++;
            SparseArray sparseArrayInstallments = new SparseArray();
            FList FldValidFrom2Flist = new FList();
            FldValidFrom2Flist.set((TStampField) FldValidFrom.getInst());
            sparseArrayInstallments.add(FldValidFrom2Flist);
            FList FldInstallments = new FList();
            FldInstallments.set((ArrayField) BhtFldInstallments.getInst(), sparseArrayInstallments);
            sparseArrayArgs.add(argsCount, FldInstallments);
            argsCount++;
            FList FldLastBillFlist = new FList();
            FldLastBillFlist.set((TStampField) FldLastBillT.getInst());
            sparseArrayArgs.add(argsCount, FldLastBillFlist);
            argsCount++;
            FList FldNextBillFlist = new FList();
            FldNextBillFlist.set((TStampField) FldNextBillT.getInst());
            sparseArrayArgs.add(argsCount, FldNextBillFlist);
            argsCount++;
            FList BillInfo1Flist = new FList();
            BillInfo1Flist.set((PoidField) FldBillinfoObj.getInst());
            sparseArrayArgs.add(argsCount, BillInfo1Flist);
            argsCount++;
            FList PoidArgsFlist = new FList();
            PoidArgsFlist.set((PoidField) FldPoid.getInst());
            sparseArrayArgs.add(argsCount, PoidArgsFlist);
            flist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);
            logger.fine("search input flist in order to get installment plans: \n" + flist);

            //invoking the search opcode in order to get the installment plans
            FList installmentPlansFlist = opcode(7, flist);
            logger.fine("the returned flist with the installment plans: \n" + installmentPlansFlist);

            //get the poids of installment plans from the flist and store them into Arraylist
            if (installmentPlansFlist.hasField((Field) FldResults.getInst())) {
                SparseArray array = installmentPlansFlist.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fList = enumeration.nextElement();
                        installments.add(fList.get((PoidField) FldPoid.getInst()));
                    }
                }
            }

        } catch (EBufException exception) {
            logger.severe("HotBillingWorker, Problem while invoking the search opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        } catch (Exception exception) {
            logger.severe("HotBillingWorker, Problem while finding the installment plans ", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);

        }
        logger.exiting("HotBillingWorker", "getInstallmentPlans");
        return installments;
    }

    //This method invokes the InstallmentChargeInstallment opcode for every installment
    public void callOpcodeforIncludeCreditInstallment(ArrayList<Poid> installmentPlans) {
        logger.entering("HotBillingWorker", "callOpcodeforIncludeCreditInstallment");
        //for every installment invoke the opcode
        for (Poid installmentPlan : installmentPlans) {
            FList flist = new FList();
            flist.set((PoidField) FldPoid.getInst(), installmentPlan);

            FList InstallmentChargeInstallment = null;
            try {
                InstallmentChargeInstallment = opcode(10006, flist);
                logger.fine("InstallmentChargeInstallment opcode result:", InstallmentChargeInstallment);

            } catch (EBufException ex) {
                logger.severe("HotBillingWorker, Problem while invoking the Installment Charge Installment opcode", ex);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
            }

        }
        logger.exiting("HotBillingWorker", "callOpcodeforIncludeCreditInstallment");
    }

    //This method invokes the InstallmentChargeInstallment opcode for every installment
    public void callOpcodeForIncludeAllInstallments(ArrayList<Poid> installmentPlans, String id, boolean nextBillingDate) throws EBufException {
        logger.entering("HotBillingWorker", "callOpcodeForIncludeAllInstallments");
        
        Date nextBillDate;
        
        //search for field FldEndT
        if(nextBillingDate){
        
            Poid poid1 = poidFromRestId(id);
            FList fList1 = new FList();
            fList1.set((PoidField) FldPoid.getInst(), poid1);
            fList1.set((Field) FldAccountObj.getInst());
            fList1.set((TStampField) FldNextBillT.getInst());
            logger.fine("Input Flist for READ_FLDS : ", fList1);
            FList fList2 = new FList();
            try {
                fList2 = opcode(4, fList1);
            } catch (EBufException ex) {
            logger.severe("HotBillingWorker, Problem while invoking the READ_FLDS opcode", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
            }
            logger.fine("Output Flist of READ_FLDS : ", fList2);
        
            nextBillDate = fList2.get((TStampField) FldNextBillT.getInst());
        } else{
            
           nextBillDate = new Date(System.currentTimeMillis()); 
        }

        
        //for every installment invoke the opcodes
        for (Poid installmentPlan : installmentPlans) {
            FList flistForClosePlan = new FList();
            flistForClosePlan.set((PoidField) FldPoid.getInst(), installmentPlan);
            flistForClosePlan.set((TStampField) FldEndT.getInst(),nextBillDate);
            
            FList flistFOrInstallmentChargeInstallment = new FList();
            flistFOrInstallmentChargeInstallment.set((PoidField) FldPoid.getInst(), installmentPlan);
            
            try {
                FList InstallmentClosePlanFlist = opcode(10005, flistForClosePlan);
                logger.fine("InstallmentClosePlanFlist opcode result:", InstallmentClosePlanFlist);

                FList InstallmentChargeInstallment = opcode(10006, flistFOrInstallmentChargeInstallment);
                logger.fine("InstallmentChargeInstallment opcode result:", InstallmentChargeInstallment);
            } catch (EBufException ex) {
                logger.severe("HotBillingWorker, Problem while invoking the opcodes", ex);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
            }
        }
        logger.exiting("InstallmentPlansWorker", "callOpcodeForIncludeAllInstallments");
    }

    //This method builds the flist needed in order to invoke the PCM_OP_AR_GET_BILL_ITEMS opcode
    public FList convertToInputFlistForGetBillItems(String billInfo) throws EBufException {
        logger.entering("HotBillingWorker", "convertToInputFlistForGetBillItems");

        Poid poid1 = poidFromRestId(billInfo);
        FList inputFlistForReadFlds = new FList();
        inputFlistForReadFlds.set((PoidField) FldPoid.getInst(), poid1);
        inputFlistForReadFlds.set((Field) FldArBillinfoObj.getInst());
        inputFlistForReadFlds.set((Field) FldAccountObj.getInst());
        inputFlistForReadFlds.set((Field) FldBillObj.getInst());
        logger.fine("Input Flist for READ_FLDS : ", inputFlistForReadFlds);
        FList outputFlistForReadFlds = new FList();
        try {
            outputFlistForReadFlds = opcode(4, inputFlistForReadFlds);
        } catch (EBufException ex) {
            logger.severe("HotBillingWorker, Problem while invoking the READ_FLDS opcode", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
        }
        logger.fine("Output Flist of READ_FLDS : " + outputFlistForReadFlds);
        Poid poid2 = outputFlistForReadFlds.get((PoidField) FldAccountObj.getInst());
        Poid poid3 = outputFlistForReadFlds.get((PoidField) FldArBillinfoObj.getInst());
        Poid poid4 = outputFlistForReadFlds.get((PoidField) FldBillObj.getInst());

        FList inputFlistForGetBillItems = new FList();
        inputFlistForGetBillItems.set((PoidField) FldPoid.getInst(), poid2);
        inputFlistForGetBillItems.set((PoidField) FldBillObj.getInst(), poid4);
        inputFlistForGetBillItems.set((PoidField) FldArBillinfoObj.getInst(), poid3);
        inputFlistForGetBillItems.set((PoidField) FldBillinfoObj.getInst(), poid1);
        inputFlistForGetBillItems.set((EnumField) FldStatus.getInst(), 1);
        inputFlistForGetBillItems.set((IntField) FldIncludeChildren.getInst(), 3);
        logger.fine("Input Flist for AR_GET_BILL_ITEMS : ", inputFlistForGetBillItems);

        logger.exiting("HotBillingWorker", "convertToInputFlistForGetBillItems");
        return inputFlistForGetBillItems;
    }

    //This method invokes the PCM_OP_AR_GET_BILL_ITEMS opcode
    public FList invokeGetBillItems(FList inputFlist) {
        logger.entering("HotBillingWorker", "invokeGetBillItems");
        FList outputfList = new FList();
        try {
            outputfList = opcode(1318, inputFlist);
        } catch (EBufException ex) {
            logger.severe("HotBillingWorker, Problem while invoking the PCM_OP_AR_GET_BILL_ITEMS opcode", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
        }
        logger.fine("Output FList of AR_GET_BILL_ITEMS : ", outputfList);
        logger.exiting("HotBillingWorker", "invokeGetBillItems");
        return outputfList;
    }

    //This method traverses the Flist and saves the bill Items in a list
    public ArrayList<Poid> getItemsFromArGetBillItemsFlist(FList GetBillItemsFlist) throws EBufException {
        logger.entering("HotBillingWorker", "getItemsFromArGetBillItemsFlist");
        ArrayList<Poid> items = new ArrayList<>();

        if (GetBillItemsFlist.hasField((Field) FldResults.getInst())) {
            SparseArray array = GetBillItemsFlist.get(FldResults.getInst());
            if (array != null) {
                Enumeration<FList> enumeration = array.getValueEnumerator();
                while (enumeration.hasMoreElements()) {
                    FList fList = enumeration.nextElement();
                    items.add(fList.get((PoidField) FldPoid.getInst()));
                }
            }
        }

        //TODO: we need to check which items will be excluded from the list.
        ArrayList<Poid> excludedPoids = new ArrayList<>();
        for (Poid item : items) {
            if (item.toString().contains("/item/cycle")) {
                excludedPoids.add(item);
            } else if (item.toString().contains("/one_off")) {
                excludedPoids.add(item);
            }
        }

        ArrayList<Poid> excluded = new ArrayList<>();
        for (Poid excludedPoid : excludedPoids) {

            //create input flist of PCM_OP_BILL_ITEM_EVENT_SEARCH 
            FList flist = new FList();
            flist.set(FldPoid.getInst(), excludedPoid);
            Poid p = flist.get(FldPoid.getInst());
            FList FldItemObjFlist = new FList();
            SparseArray sparseArrayArgs = new SparseArray();
            FldItemObjFlist.set((PoidField) FldItemObj.getInst(), excludedPoid);
            sparseArrayArgs.add(0, FldItemObjFlist);
            flist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);
            FList FldItemObjFlist2 = new FList();
            FldItemObjFlist2.set(FldItemObj.getInst());
            FList FldBalImpactsFList = new FList();
            FldBalImpactsFList.setElement(FldBalImpacts.getInst(), ELEMID_ANY, FldItemObjFlist2);
            SparseArray sparseArrayResults = new SparseArray();
            sparseArrayResults.add(ELEMID_ANY, FldBalImpactsFList);
            flist.set((ArrayField) FldResults.getInst(), sparseArrayResults);
            logger.fine("Input FList of PCM_OP_BILL_ITEM_EVENT_SEARCH : ", flist);

            FList outputfList = new FList();
            try {
                //invoke PCM_OP_BILL_ITEM_EVENT_SEARCH opcode
                outputfList = opcode(146, flist);
                logger.fine("Output FList of PCM_OP_BILL_ITEM_EVENT_SEARCH : ", outputfList);
            } catch (EBufException ex) {
                logger.severe("HotBillingWorker, Problem while invoking the PCM_OP_BILL_ITEM_EVENT_SEARCH opcode", ex);
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
            }

            excluded.add(outputfList.get(FldPoid.getInst()));
            if (outputfList.hasField((Field) FldEvents.getInst())) {
                SparseArray array = outputfList.get(FldEvents.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fldResultsFlist = enumeration.nextElement();
                        if (fldResultsFlist.hasField((Field) FldResults.getInst())) {
                            SparseArray array2 = fldResultsFlist.get(FldResults.getInst());
                            if (array2 != null) {
                                Enumeration<FList> enumeration2 = array2.getValueEnumerator();
                                while (enumeration2.hasMoreElements()) {
                                    FList fldBalImpactsFList = enumeration2.nextElement();
                                    if (fldBalImpactsFList.hasField((Field) FldBalImpacts.getInst())) {
                                        SparseArray array3 = fldBalImpactsFList.get(FldBalImpacts.getInst());
                                        if (array3 != null) {
                                            Enumeration<FList> enumeration3 = array3.getValueEnumerator();
                                            while (enumeration3.hasMoreElements()) {
                                                FList fldItemObjFList = enumeration3.nextElement();

                                                excluded.add(fldItemObjFList.get(FldItemObj.getInst()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        items.removeAll(excluded);

        logger.exiting("HotBillingWorker", "getItemsFromArGetBillItemsFlist");

        return items;
    }

    //This method builds the Flist needed in order to invoke the opcode bill now
    public FList convertToInputFListForBillNow(String id, boolean nextBillingDate, boolean includeCharges, ArrayList<Poid> items) throws EBufException {
        logger.entering("HotBillingWorker", "convertToInputFListForBillNow");
        Poid poid1 = poidFromRestId(id);
        FList fList1 = new FList();
        fList1.set((PoidField) FldPoid.getInst(), poid1);
        fList1.set((Field) FldAccountObj.getInst());
        fList1.set((TStampField) FldNextBillT.getInst());
        logger.fine("Input Flist for READ_FLDS : ", fList1);
        FList fList2 = new FList();
        try {
            fList2 = opcode(4, fList1);
        } catch (EBufException ex) {
            logger.severe("HotBillingWorker, Problem while invoking the READ_FLDS opcode", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
        }
        logger.fine("Output Flist of READ_FLDS : ", fList2);
        Poid poid2 = fList2.get((PoidField) FldAccountObj.getInst());
        fList1 = new FList();
        fList1.set(FldFlags.getInst(), 32);
        fList1.set((PoidField) FldPoid.getInst(), poid2);
        fList1.set((PoidField) FldBillinfoObj.getInst(), poid1);
        fList1.set((StrField) FldProgramName.getInst(), "Billing Care");
        if (nextBillingDate) {
            Date nextBillDate = fList2.get((TStampField) FldNextBillT.getInst());
            fList1.set((TStampField) FldEndT.getInst(),nextBillDate);
        }
        if (!includeCharges) {
            SparseArray itemsArrays = new SparseArray();
            for (Poid item : items) {
                FList itemFlist = new FList();
                itemFlist.set(FldItemObj.getInst(), item);
                itemsArrays.add(itemFlist);
            }
            fList1.set(FldItems.getInst(), itemsArrays);
            fList1.set(FldFlags.getInst(), 0);
        }
        logger.exiting("HotBillingWorker", "convertToInputFListForBillNow");
        return fList1;
    }

    //This method invokes the makeInvoice opcode
    public void callMakeInvoiceOpcode(String billNowPoid) {
        logger.entering("HotBillingWorker", "callMakeInvoiceOpcode");
        FList flist = new FList();
        flist.set((PoidField) FldPoid.getInst(), BRMUtility.poidFromRestId(billNowPoid));
        try {
            FList makeInvoiceFlist = opcode(953, flist);
            logger.fine("MakeInvoiceFlist opcode result:", makeInvoiceFlist);

        } catch (EBufException ex) {
            logger.severe("HotBillingWorker, Problem while invoking the Make Invoice opcode", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
        }
        logger.exiting("HotBillingWorker", "callMakeInvoiceOpcode");
    }

}
