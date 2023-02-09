package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.common.BRMCommunicationContext;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.model.ColumnarRecord;
import com.oracle.communications.brm.cc.model.GenericTemplate;
import com.oracle.communications.brm.cc.model.SearchCriterias;
import com.oracle.communications.brm.cc.modules.TemplateModule;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("private/customTemplates")
public class CustomFullPageResource {
  private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.template.TemplateResource.class);
  
  BRMCommunicationContext ctx = new BRMCommunicationContext();
  
  @Path("/{templatetype}")
  @POST
  @Produces({"application/xml", "application/json"})
  @Consumes({"application/xml", "application/json"})
  public List<ColumnarRecord> getRecordsForTemplate(@PathParam("templatetype") String templatetype, @QueryParam("id") String id, @QueryParam("secondaryid") String secondaryId, @QueryParam("offset") int offset, @QueryParam("limit") int limit, SearchCriterias searchCriteria, @QueryParam("sortbyField") String sortbyField, @QueryParam("sortOrder") String sortOrder) {
    List<ColumnarRecord> list = new ArrayList();
    logger.info("--- Entering the CustomFullPageResource, calling the method getRecordsForTemplate --- ");
    logger.info("--- Method getRecordsForTemplate: get template for templateType " + templatetype + " and for id " + id + "and secondary id" +secondaryId);
    TemplateModule templateModule = this.ctx.getCommunicationFactory().getTemplateModule();
    ArrayList<GenericTemplate.SortbyFields> arrayList = new ArrayList();
    try {
      if (sortbyField != null) {
        GenericTemplate.SortbyFields sortbyFields = new GenericTemplate.SortbyFields();
        sortbyFields.setField(sortbyField);
        sortbyFields.setSortOrder((sortOrder != null) ? sortOrder : "desc");
        sortbyFields.setSortingPriority(1);
        logger.info("-- adding sortbyFields ---");
        arrayList.add(sortbyFields);
      } 
      logger.info("-- calling method getRecordsForTemplate from CustomFullPageResource ---");
      list = templateModule.getRecordsForTemplate(templatetype, id, secondaryId, offset, limit, searchCriteria, arrayList);
      logger.info("--- CustomFullPageResource, getRecordsForTemplate: records retrieved successfully for templateType " + templatetype + " and for id " + id + "---");
    } catch (ApplicationException applicationException) {
      logger.severe("--- CustomFullPageResource: Failed to get Records for template type " + templatetype + " and for id " + id + "---");
      ExceptionHelper.handleException(applicationException);
    }    

    return list;
  }
}
