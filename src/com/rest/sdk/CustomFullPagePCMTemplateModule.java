package com.rest.sdk;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.common.BaseOps;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.model.ColumnarRecord;
import com.oracle.communications.brm.cc.model.GenericTemplate;
import com.oracle.communications.brm.cc.model.SearchCriterias;
import com.oracle.communications.brm.cc.modules.TemplateModule;
import com.oracle.communications.brm.cc.modules.pcm.PCMModuleBase;
import com.oracle.communications.brm.cc.modules.pcm.PCMTemplateModule;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseOps;
import com.oracle.communications.brm.cc.modules.pcm.workers.TemplateBaseWorker;
import com.oracle.communications.brm.cc.modules.pcm.workers.TemplateFactory;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.ws.CustomFullPageWorker;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import com.portal.pcm.fields.FldPoid;
import java.util.List;


public class CustomFullPagePCMTemplateModule extends PCMTemplateModule {
  private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.modules.pcm.PCMTemplateModule.class);
   
  @Override
  public List<ColumnarRecord> getRecordsForTemplate(String templateType, String id, String secondaryId, int offset, int limit, SearchCriterias searchCriteria, List<GenericTemplate.SortbyFields> sortByFields) {
    PortalContext ctx = null;
    logger.info(" --- Entering CustomFullPagePCMTemplateModule, getRecordsForTemplate method ---");
        try {
            BaseOps baseOps = getBaseOps();
            if (baseOps instanceof PCMBaseOps) {
                ctx = BRMUtility.getConnection();
                ((PCMBaseOps) baseOps).setContext(ctx);
            }
            List<ColumnarRecord> records = null;
            TemplateBaseWorker templateWorker = null;           
            if (templateType.equalsIgnoreCase("customFullPageTemplate")) {
                templateWorker = new CustomFullPageWorker();
                logger.info(" --- Instansiating the CustomFullPageWorker ---");
            } 
            else {
                logger.info(" --- call the super class implementation of getRecordsForTemplate ---");
                //if the template is not our custom template call the super class implementation of getRecordsForTemplate
                return super.getRecordsForTemplate(templateType, id, secondaryId, offset, limit, searchCriteria, sortByFields);

            }
            if (templateWorker != null) {              
                templateWorker.setBaseOps(baseOps);
                AddDependantSortingParameterForPhoneNumber(sortByFields);
                logger.info(" --- Calling the convertToInputFListForGetTemplateRecords ---");
                FList inputFList = templateWorker.convertToInputFListForGetTemplateRecords(id, secondaryId, offset, limit, searchCriteria, sortByFields);
                logger.info(" --- Result of the convertToInputFListForGetTemplateRecords: --- \n" +inputFList.toString());
                FList outputFList = new FList();
                if (inputFList == null ) {
                 
                    outputFList.set(FldPoid.getInst(), new Poid(1, -1, "/search"));
                } else {
                    logger.info(" --- Calling the invokeGetTemplateRecords ---");
                    outputFList = templateWorker.invokeGetTemplateRecords(inputFList);
                    logger.info(" --- Called the invokeGetTemplateRecords ---");
                }
                logger.info(" --- Calling the convertToTemplateRecordsForGetTemplateRecords ---");
                records = templateWorker.convertToTemplateRecordsForGetTemplateRecords(outputFList, id, secondaryId, searchCriteria);
                logger.info("--- Exiting CustomFullPagePCMTemplateModule, getRecordsForTemplate ---");
                return records;
            } else { // if the worker class cannot be located then throw exception
                logger.severe("Unable to locate the CustomFullPageWorker for the templateType: " +templateType);
                throw new ApplicationException("Unable to locate the CustomFullPageWorker for the templateType");
            }
        } catch (EBufException ex) {
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
  
   }
  
}
