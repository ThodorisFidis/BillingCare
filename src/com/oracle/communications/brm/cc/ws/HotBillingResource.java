package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.authorization.EnforcementError;
import com.oracle.communications.brm.cc.authorization.EnforcementUtil;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.model.ObjectFactory;
import com.oracle.communications.brm.cc.model.Resource;
import com.oracle.communications.brm.cc.model.ResourceRef;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.oracle.communications.brm.cc.util.TokenValidatorUtil;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.AccessDeniedException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.InvalidParametersException_Exception;
import com.oracle.xmlns.oxp.service.v11.publicreportservice.OperationFailedException_Exception;
import com.rest.sdk.HotBillingModule;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;

@Path("hot-billing")
public class HotBillingResource {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.HotBillingResource.class);

    @Context
    HttpServletRequest servletRequest;

    @Context
    private UriInfo context;

    @Path("/installment-plans/get-installment-plans")
    @POST
    @Consumes({"application/xml", "application/json"})
    @ApiOperation(value = "createBillNow", notes = "Creates a bill")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Creates a bill and a bill ID is returned as part of response object on success")
        , @ApiResponse(code = 400, message = "Bad Request on passing a null Bill Unit ID")
        , @ApiResponse(code = 500, message = "Exception containing errors will be raised on failure")})
    public Response billNow(@QueryParam("billInfo") String billInfo, @QueryParam("nextBillingDate") boolean nextBillingDate, @QueryParam("includeCharges") boolean includeCharges, @QueryParam("includeCreditInstallment") boolean includeCreditInstallment, @QueryParam("includeAllInstallments") boolean includeAllInstallments) throws ParserConfigurationException {
        logger.entering("HotBillingResource", "billNow");
        Response response = null;
        try {
            Subject subject = TokenValidatorUtil.getSubject(this.servletRequest);
            EnforcementError enforcementError = EnforcementError.ERROR_NO_GRANT_FOR_BILL_NOW;
            if (EnforcementUtil.isResourceGranted(this.servletRequest, subject, "BillingCare", "ReadOnlyResource")) {
                ExceptionHelper.buildErrorInfo(enforcementError.getErrorCode(), enforcementError.getErrorMessage(), new Object[]{Response.Status.UNAUTHORIZED});
            }
            if (!EnforcementUtil.isResourceGranted(this.servletRequest, subject, "BillingCare", "SuperUserResource")) {
                EnforcementUtil.checkAccess(subject, "BillingCare", "BillNow", "BillResourceType", "BillResource", enforcementError, new com.oracle.communications.brm.cc.authorization.UIRequestValue[0]);
            }
            if (billInfo == null || billInfo.isEmpty()) {
                response = Response.status(Response.Status.BAD_REQUEST).build();
                return response;
            }

            logger.fine("billNow: initializing HotBilling Module");
            HotBillingModule hotBillingModule = new HotBillingModule();
            HotBillingModule.getInstallmentPlans(billInfo, includeCreditInstallment, includeAllInstallments, nextBillingDate);

            String str = hotBillingModule.billNow(billInfo, nextBillingDate, includeCharges);
            logger.fine("billNow result: \n" + str);
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            ObjectFactory objectFactory = new ObjectFactory();
            ResourceRef resourceRef = objectFactory.createResourceRef();
            UriBuilder uriBuilder = this.context.getBaseUriBuilder().path(com.oracle.communications.brm.cc.ws.HotBillingResource.class).path(str);
            resourceRef.setUri(uriBuilder.build(new Object[]{str}).toString());
            resourceRef.setId(str);
            Resource resource = objectFactory.createResource();
            resource.setReference(resourceRef);
            responseBuilder.entity(resource);
            response = responseBuilder.build();

        } catch (ApplicationException applicationException) {
            logger.severe("billNow: Failed to create bill and generate invoice");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("HotBillingResource", "billNow");
        return response;
    }
    
    @Path("/call-listing-report")
    @GET
    @Produces({"application/pdf"})
    public Response getCallListingReport(@QueryParam("billInfo") String billInfo) throws IOException, FileNotFoundException, AccessDeniedException_Exception, AccessDeniedException_Exception, AccessDeniedException_Exception, OperationFailedException_Exception, InvalidParametersException_Exception, AccessDeniedException_Exception, AccessDeniedException_Exception, InvalidParametersException_Exception {
        if (billInfo == null || billInfo.isEmpty()) {
            Response.ResponseBuilder responseBuilder1 = Response.status(Response.Status.BAD_REQUEST);
            return responseBuilder1.build();
        }
        File file = null;
        String str = "";
        logger.entering("HotBillingResource", "getCallListingReport");
        HotBillingModule hotBillingModule = new HotBillingModule();
        file = hotBillingModule.getCallListingReport(billInfo);
        if (file != null && file.getName() != null) {
            str = file.getName();
        }
        logger.exiting("HotBillingResource", "getCallListingReport");
        Response.ResponseBuilder responseBuilder = Response.ok(file);
        responseBuilder.header("Content-Disposition", "inline; filename=\"" + str + "\"");
        responseBuilder.header("Content-type", "application/pdf");
        return responseBuilder.build();
    }
}
