package com.oracle.communications.brm.cc.ws;

import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.rest.sdk.RatesModule;
import java.text.ParseException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.parsers.ParserConfigurationException;

@Path("ifrs9")
public class RatesResource {

    private static CCLogger logger = CCLogger.getCCLogger(com.oracle.communications.brm.cc.ws.RatesResource.class);

    @Path("/create-rate")
    @POST
    @Produces({"application/xml", "application/json"})
    public void createRate(@QueryParam("maturityPeriod") String maturityPeriod, @QueryParam("effectiveDate") String effectiveDate, @QueryParam("agingDays") int agingDays, @QueryParam("percent") String percent) throws ParserConfigurationException, ParseException {
        logger.entering("RatesResource", "createRate");
        try {
            logger.fine("createRates: initializing RatesModule");
            RatesModule ratesModule = new RatesModule();
            ratesModule.createRate(maturityPeriod, effectiveDate, agingDays, percent);

        } catch (ApplicationException applicationException) {
            logger.severe("RatesResource: Failed to create rate");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("RatesResource", "createRate");
    }

    @Path("/search-rates")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<RatesInformation> searchRates(@QueryParam("effectiveDate") String effectiveDate, @QueryParam("status") int status) throws ParserConfigurationException, ParseException {
        logger.entering("RatesResource", "searchRates");
        List<RatesInformation> result = null;
        try {
            logger.fine("searchRates: initializing RatesModule");
            RatesModule ratesModule = new RatesModule();
            result = ratesModule.searchRates(effectiveDate, status);

        } catch (ApplicationException applicationException) {
            logger.severe("RatesResource: Failed to find rate");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("RatesResource", "searchRates");
        return result;
    }

    @Path("/edit-rate")
    @POST
    @Produces({"application/xml", "application/json"})
    public void editRate(@QueryParam("maturityPeriod") String maturityPeriod, @QueryParam("effectiveDate") String effectiveDate, @QueryParam("agingDays") int agingDays, @QueryParam("percent") String percent, @QueryParam("index") int index, @QueryParam("poid") String poid, @QueryParam("status") String status) throws ParserConfigurationException, ParseException {
        logger.entering("RatesResource", "editRate");
        try {
            logger.fine("editRate: initializing RatesModule");
            RatesModule ratesModule = new RatesModule();
            ratesModule.editRate(maturityPeriod, effectiveDate, agingDays, percent, index, poid, status);

        } catch (ApplicationException applicationException) {
            logger.severe("RatesResource: Failed to edit rate");
            ExceptionHelper.handleException(applicationException);
        }
        logger.exiting("RatesResource", "editRate");
    }

}
