package com.rest.sdk;

import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.common.BaseOps;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.modules.pcm.PCMBillModule;
import com.oracle.communications.brm.cc.modules.pcm.workers.BillWorker;
import com.oracle.communications.brm.cc.modules.pcm.workers.PCMBaseOps;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.ws.HotBillingWorker;
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
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import weblogic.security.internal.SerializedSystemIni;
import weblogic.security.internal.encryption.ClearOrEncryptedService;
import weblogic.security.internal.encryption.EncryptionService;

public class HotBillingModule extends PCMBillModule {

    private static CCLogger logger = CCLogger.getCCLogger(com.rest.sdk.HotBillingModule.class);

    public static void getInstallmentPlans(String billInfo, boolean includeCreditInstallment, boolean includeAllInstallments, boolean nextBillingDate) {
        logger.entering("HotBillingModule", "getInstallmentPlans");
        PortalContext ctx = null;
        BaseOps baseOps = null;

        try {
            ArrayList<Poid> installments = new ArrayList<>();
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);

            HotBillingWorker hotBillingWorker = new HotBillingWorker();
            hotBillingWorker.setBaseOps(baseOps);

            if (includeCreditInstallment && includeAllInstallments) {
                installments = hotBillingWorker.getInstallmentPlans(billInfo);
                hotBillingWorker.callOpcodeForIncludeAllInstallments(installments, billInfo, nextBillingDate);
            } else if (includeCreditInstallment) {
                installments = hotBillingWorker.getInstallmentPlans(billInfo);
                hotBillingWorker.callOpcodeforIncludeCreditInstallment(installments);
            } else if (includeAllInstallments) {
                installments = hotBillingWorker.getInstallmentPlans(billInfo);
                hotBillingWorker.callOpcodeForIncludeAllInstallments(installments, billInfo, nextBillingDate);
            }

            logger.exiting("HotBillingModule", "getInstallmentPlans");
        } catch (EBufException ex) {
            throw new ApplicationException(ex);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }

        }
    }

    public String billNow(String billInfo, boolean nextBillingDate, boolean includeCharges) {
        logger.entering("HotBillingModule", "billNow");
        PortalContext ctx = null;
        String str = null;
        BaseOps baseOps = null;
        ArrayList<Poid> items = new ArrayList<>();
        try {
            ctx = BRMUtility.getConnection();
            baseOps = (BaseOps) new PCMBaseOps(ctx);
            ((PCMBaseOps) baseOps).setContext(ctx);
            HotBillingWorker hotBillingWorker = new HotBillingWorker();
            hotBillingWorker.setBaseOps(baseOps);
            BillWorker billWorker = new BillWorker();
            billWorker.setBaseOps(baseOps);
            if (!includeCharges) {
                logger.fine("include charges option is not selected");
                FList InputFlistForGetBillItems = hotBillingWorker.convertToInputFlistForGetBillItems(billInfo);
                FList resultOfGetBillItems = hotBillingWorker.invokeGetBillItems(InputFlistForGetBillItems);
                items = hotBillingWorker.getItemsFromArGetBillItemsFlist(resultOfGetBillItems);
            }
            FList fList1 = hotBillingWorker.convertToInputFListForBillNow(billInfo, nextBillingDate, includeCharges, items);
            FList fList2 = billWorker.invokeBillNow(fList1);
            str = billWorker.convertToOutputForBillNow(fList2);

            logger.fine("calling make invoice with poid : \n" + str);
            hotBillingWorker.callMakeInvoiceOpcode(str);
        } catch (EBufException eBufException) {
            logger.severe("Error while performing bill now.", (Throwable) eBufException);
            throw new ApplicationException(eBufException);
        } finally {
            if (ctx != null) {
                BRMUtility.releaseConnection(ctx);
            }
        }
        logger.exiting("HotBillingModule", "billNow");
        return str;
    }

    public File getCallListingReport(String billInfo) throws FileNotFoundException, IOException, AccessDeniedException_Exception, InvalidParametersException_Exception, OperationFailedException_Exception {
        logger.entering("HotBillingModule", "getCallListingReport");
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
        arrayOfString.getItem().add(billInfo);
        paramNameValue.setName("PARAM_BILLINFO");
        paramNameValue.setValues(arrayOfString);
        ArrayOfParamNameValue arrayOfParamNameValue = new ArrayOfParamNameValue();
        arrayOfParamNameValue.getItem().add(paramNameValue);
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setReportAbsolutePath("BRM_Reports/BHT/DetailCallListing.xdo");
        reportRequest.setSizeOfDataChunkDownload(-1);
        reportRequest.setParameterNameValues(arrayOfParamNameValue);
        reportRequest.setAttributeFormat("pdf");
        ReportResponse reportResponse = publicReportService.runReport(reportRequest, str2, str4);
        byte[] arrayOfByte = reportResponse.getReportBytes();
        File file = new File("BRM_Call_Listing_Report-" + billInfo + ".pdf");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(arrayOfByte);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        logger.exiting("HotBillingModule", "getCallListingReport");
        return file;
    }

    @Override
    public String decryptPassword(String password) {
        EncryptionService encryptionService = SerializedSystemIni.getEncryptionService();
        ClearOrEncryptedService clearOrEncryptedService = new ClearOrEncryptedService(encryptionService);
        return clearOrEncryptedService.decrypt(password);
    }

}
