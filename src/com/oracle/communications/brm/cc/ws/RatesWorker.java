package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.enums.ErrorConstants;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseWorker;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.portal.pcm.ArrayField;
import com.portal.pcm.DecimalField;
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
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldCode;
import com.portal.pcm.fields.FldDays;
import com.portal.pcm.fields.FldEffectiveT;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldPercent;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldRateInfo;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldTemplate;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class RatesWorker extends PCMBaseWorker {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.RatesWorker.class);

    public RatesWorker() {
    }

    //create input flist in order to create rate
    public FList convertToInputFListForCreateRate(String maturityPeriod, String effectiveDate, int agingDays, String percent, Poid poid) throws ParseException {
        logger.entering("RatesWorker", "convertToInputFListForCreateRate");
        FList inputFlist = new FList();
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(effectiveDate);
        float percentToInt = Float.parseFloat(percent);
        float percentage = percentToInt / 100.0f;
        BigDecimal convertedToBigDecimal = new BigDecimal(percentage).setScale(5, BigDecimal.ROUND_HALF_UP);
        inputFlist.set((PoidField) FldPoid.getInst(), poid);
        FList rateInfoFList = new FList();
        rateInfoFList.set((StrField) FldCode.getInst(), maturityPeriod);
        rateInfoFList.set((IntField) FldDays.getInst(), agingDays);
        rateInfoFList.set((TStampField) FldEffectiveT.getInst(), date);
        rateInfoFList.set((DecimalField) FldPercent.getInst(), convertedToBigDecimal);
        rateInfoFList.set((EnumField) FldStatus.getInst(), 1);
        inputFlist.setElement(FldRateInfo.getInst(), ELEMID_ASSIGN, rateInfoFList);

        logger.fine("Input flist for create rate: " + inputFlist);
        
        logger.exiting("RatesWorker", "convertToInputFListForCreateRate");
        return inputFlist;
    }

    //create input flist in order to find poid
    public FList createInputFlistForPoidSearch() {
        logger.entering("RatesWorker", "createInputFlistForRatesSearch");
        long l = getCurrentDB();
        FList inputFlist = new FList();
        inputFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        inputFlist.set((IntField) FldFlags.getInst(), 0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select X from /config/bht_ss_rate where F1 = V1 ");
        inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
        inputFlist.set((ArrayField) FldResults.getInst());
        //arguments
        SparseArray sparseArrayArgs = new SparseArray();
        FList FldPoidFlist = new FList();
        FldPoidFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/config/bht_ss_rate"));
        sparseArrayArgs.add(1, FldPoidFlist);
        inputFlist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);

        logger.fine("Input flist for poid search: " + inputFlist);

        logger.exiting("RatesWorker", "createInputFlistForRatesSearch");
        return inputFlist;
    }

    //invoke search opcode
    public FList invokeSearchOpcode(FList inputFlist) {
        logger.entering("RatesWorker", "invokeSearchOpcode");
        FList outputFlist = null;

        try {
            outputFlist = opcode(7, inputFlist);
            logger.fine("Output flist of search: " + outputFlist);

        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem with searching", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "error while calling search opcode", new Object[0]);
        }

        logger.exiting("RatesWorker", "invokeSearchOpcode");
        return outputFlist;
    }

    //traverse flist in order to find poid
    public Poid findPoidOfSsRateObject(FList flist) {
        logger.entering("RatesWorker", "findPoidOfSsRateObject");

        Poid poid = null;
        try {
            if (flist.hasField((Field) FldResults.getInst())) {
                SparseArray array = flist.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList fList = enumeration.nextElement();
                        poid = fList.get(FldPoid.getInst());
                    }
                }
            }
        } catch (EBufException ex) {
            logger.severe("FiscalizationWorker, Problem with finding poid", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), "Couldn't find poid of object in bht_ss_rate structure", new Object[0]);
        }
        logger.fine("poid field: " + poid);

        logger.exiting("RatesWorker", "findPoidOfSsRateObject");
        return poid;
    }

    //invoke write fields opcode
    public void invokeWriteFields(FList inputFlistForCreateRate) {
        logger.entering("RatesWorker", "invokeWriteFields");
        try {
            FList outputFlistForWriteFields = opcode(5, 32, inputFlistForCreateRate);
            logger.fine("Output of write fields opcode: " + outputFlistForWriteFields);

            logger.exiting("RatesWorker", "invokeWriteFields");
        } catch (EBufException exception) {
            logger.severe("FiscalizationWorker, Problem while invoking the write fields opcode", exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), exception.toString(), new Object[0]);
        }
    }

    //create input flist in order to find rates
    public FList createInputFlistForRatesSearch(String effectiveDate, int status) throws ParseException {
        logger.entering("RatesWorker", "createInputFlistForRatesSearch");
        long l = getCurrentDB();
        FList inputFlist = new FList();
        inputFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/search"));
        if (effectiveDate.isEmpty() && status == 0) {
            inputFlist.set((IntField) FldFlags.getInst(), 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /config/bht_ss_rate where F1 = V1 ");
            inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            inputFlist.set((ArrayField) FldResults.getInst());
            //arguments
            SparseArray sparseArrayArgs = new SparseArray();
            FList FldPoidFlist = new FList();
            FldPoidFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/config/bht_ss_rate"));
            sparseArrayArgs.add(1, FldPoidFlist);
            inputFlist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);
        } else {
            int templateArgsCounter = 1;
            int argsCounter = 1;
            inputFlist.set((IntField) FldFlags.getInst(), 768);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select X from /config/bht_ss_rate where F1 = V1 ");
            if (!(effectiveDate.isEmpty())) {
                templateArgsCounter++;
                stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
            }
            if (!(status == 0)) {
                templateArgsCounter++;
                stringBuilder.append("and F").append(templateArgsCounter).append(" = V").append(templateArgsCounter).append(" ");
            }
            inputFlist.set((StrField) FldTemplate.getInst(), stringBuilder.toString());
            //set Results list values
            SparseArray resultsSparseArray = new SparseArray();
            FList resultsFlist = new FList();
            resultsFlist.set((Field) FldPoid.getInst());
            resultsFlist.setElement(FldRateInfo.getInst(), ELEMID_ANY);
            resultsSparseArray.add(0, resultsFlist);
            inputFlist.set((ArrayField) FldResults.getInst(), resultsSparseArray);
            //arguments
            SparseArray sparseArrayArgs = new SparseArray();
            FList FldPoidFlist = new FList();
            FldPoidFlist.set((PoidField) FldPoid.getInst(), new Poid(l, -1L, "/config/bht_ss_rate"));
            sparseArrayArgs.add(1, FldPoidFlist);
            if (!(effectiveDate.isEmpty())) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(effectiveDate);
                argsCounter++;
                FList FldEffectiveTFlist = new FList();
                FldEffectiveTFlist.set((TStampField) FldEffectiveT.getInst(), date);
                FList FldRateInfoFlist = new FList();
                FldRateInfoFlist.setElement(FldRateInfo.getInst(), ELEMID_ANY, FldEffectiveTFlist);
                sparseArrayArgs.add(argsCounter, FldRateInfoFlist);
            }
            if (!(status == 0)) {
                argsCounter++;
                FList StatusFlist = new FList();
                StatusFlist.set((EnumField) FldStatus.getInst(), status);
                FList FldRateInfoFlist = new FList();
                FldRateInfoFlist.setElement(FldRateInfo.getInst(), ELEMID_ANY, StatusFlist);
                sparseArrayArgs.add(argsCounter, FldRateInfoFlist);
            }
            inputFlist.set((ArrayField) FldArgs.getInst(), sparseArrayArgs);

        }

        logger.fine("input flist for rates search: " + inputFlist);
        logger.exiting("RatesWorker", "createInputFlistForRatesSearch");
        return inputFlist;
    }

    //traverse flist and save rates information to list
    public List<RatesInformation> getRates(FList outputFlistOfRatesSearch) {
        logger.entering("RatesWorker", "getRates");
        List<RatesInformation> results = new ArrayList();
        RatesInformation ratesInformation = new RatesInformation();
        try {
            if (outputFlistOfRatesSearch.hasField((Field) FldResults.getInst())) {
                SparseArray array = outputFlistOfRatesSearch.get(FldResults.getInst());
                if (array != null) {
                    Enumeration<FList> enumeration = array.getValueEnumerator();
                    while (enumeration.hasMoreElements()) {
                        FList resultsFList = enumeration.nextElement();
                        Poid poid = resultsFList.get(FldPoid.getInst());
                        if (resultsFList.hasField((Field) FldRateInfo.getInst())) {
                            SparseArray array2 = resultsFList.get(FldRateInfo.getInst());
                            if (array2 != null) {
                                Enumeration<FList> enumeration2 = array2.getValueEnumerator();
                                Enumeration keyEnum = array2.getKeyEnumerator();
                                while (enumeration2.hasMoreElements()) {
                                    FList ratesInfoFlist = enumeration2.nextElement();
                                    ratesInformation.setIndex((Integer) keyEnum.nextElement());
                                    ratesInformation.setMaturityPeriod(ratesInfoFlist.get(FldCode.getInst()));
                                    ratesInformation.setAgingDays(ratesInfoFlist.get(FldDays.getInst()));
                                    ratesInformation.setEffectiveDate(ratesInfoFlist.get(FldEffectiveT.getInst()));
                                    ratesInformation.setPercent(ratesInfoFlist.get(FldPercent.getInst()));
                                    ratesInformation.setStatus(ratesInfoFlist.get(FldStatus.getInst()));
                                    ratesInformation.setPoid(BRMUtility.restIdFromPoid(poid));

                                    results.add(ratesInformation);
                                    ratesInformation = new RatesInformation();
                                }
                            }
                        }
                    }
                }
            }
        } catch (EBufException ex) {
            logger.severe("FiscalizationWorker, Problem with finding rates", ex);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ex.toString(), new Object[0]);
        }

        logger.exiting("RatesWorker", "getRates");
        return results;
    }

    //create input flist in order to edit the rate
    public FList convertToInputFListForEditRate(String maturityPeriod, String effectiveDate, int agingDays, String percent, String poid, int index, String status) throws ParseException {
        logger.entering("RatesWorker", "convertToInputFListForEditRate");
        int intStatus = 1;
        if (status.equals("Approved")) {
            intStatus = 2;
        }
        FList inputFlist = new FList();
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(effectiveDate);
        float keptToInt = Float.parseFloat(percent);
        float percentage = keptToInt / 100.0f;
        BigDecimal convertedToBigDecimal = new BigDecimal(percentage).setScale(5, BigDecimal.ROUND_HALF_UP);
        inputFlist.set((PoidField) FldPoid.getInst(), BRMUtility.poidFromRestId(poid));
        FList rateInfoFList = new FList();
        rateInfoFList.set((StrField) FldCode.getInst(), maturityPeriod);
        rateInfoFList.set((IntField) FldDays.getInst(), agingDays);
        rateInfoFList.set((TStampField) FldEffectiveT.getInst(), date);
        rateInfoFList.set((DecimalField) FldPercent.getInst(), convertedToBigDecimal);
        rateInfoFList.set((EnumField) FldStatus.getInst(), intStatus);
        inputFlist.setElement(FldRateInfo.getInst(), index, rateInfoFList);

        logger.fine("input flist for edit rate: " + inputFlist);
        
        logger.exiting("RatesWorker", "convertToInputFListForEditRate");
        return inputFlist;
    }
}
