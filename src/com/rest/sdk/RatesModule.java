package com.rest.sdk;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.common.BaseOps;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseOps;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.ws.RatesInformation;
import com.oracle.communications.brm.cc.ws.RatesWorker;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import java.text.ParseException;
import java.util.List;


public class RatesModule {

    private static CCLogger logger = CCLogger.getCCLogger(com.rest.sdk.RatesModule.class);

    public void createRate(String maturityPeriod, String effectiveDate, int agingDays, String percent) throws ParseException {
        logger.entering("RatesModule", "createRate");
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            RatesWorker ratesWorker = new RatesWorker();
            ratesWorker.setBaseOps(baseOps);
            FList inputFlistForPoidSearch = ratesWorker.createInputFlistForPoidSearch();
            FList outputFlistForPoidSearch = ratesWorker.invokeSearchOpcode(inputFlistForPoidSearch);
            Poid poid = ratesWorker.findPoidOfSsRateObject(outputFlistForPoidSearch);
            FList inputFlistForCreateRate = ratesWorker.convertToInputFListForCreateRate(maturityPeriod, effectiveDate, agingDays, percent, poid);
            ratesWorker.invokeWriteFields(inputFlistForCreateRate);
            
            logger.exiting("RatesModule", "createRate");
        } catch (EBufException ex) {
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }
    
    public List<RatesInformation> searchRates(String effectiveDate, int status) throws ParseException{
        logger.entering("RatesModule", "searchRates");
        PortalContext ctx = null;
        BaseOps baseOps = null;
        List<RatesInformation> result = null;
        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            RatesWorker ratesWorker = new RatesWorker();
            ratesWorker.setBaseOps(baseOps);
            FList inputFlistForPoidSearch = ratesWorker.createInputFlistForRatesSearch(effectiveDate, status);
            FList outputFlistOfRatesSearch = ratesWorker.invokeSearchOpcode(inputFlistForPoidSearch);
            result = ratesWorker.getRates(outputFlistOfRatesSearch);
            
            logger.exiting("RatesModule", "searchRates");
            return result;
        } catch (EBufException ex) {
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }
    
        public void editRate(String maturityPeriod, String effectiveDate, int agingDays, String percent, int index, String poid, String status) throws ParseException {
        logger.entering("RatesModule", "editRate");
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            RatesWorker ratesWorker = new RatesWorker();
            ratesWorker.setBaseOps(baseOps);
            FList inputFlistForEditRate = ratesWorker.convertToInputFListForEditRate(maturityPeriod, effectiveDate, agingDays, percent, poid, index, status);
            ratesWorker.invokeWriteFields(inputFlistForEditRate);
            
            logger.exiting("RatesModule", "editRate");
        } catch (EBufException ex) {
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
    }

}
