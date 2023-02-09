package com.oracle.communications.brm.cc.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.communications.brm.cc.common.BRMUtility;
import com.oracle.communications.brm.cc.enums.ErrorConstants;
import com.oracle.communications.brm.cc.exceptions.ApplicationException;
import com.oracle.communications.brm.cc.model.AccountDetails;
import com.oracle.communications.brm.cc.model.ColumnarRecord;
import com.oracle.communications.brm.cc.model.Configurations;
import com.oracle.communications.brm.cc.model.Contacts;
import com.oracle.communications.brm.cc.model.Criteria;
import com.oracle.communications.brm.cc.model.Criterias;
import com.oracle.communications.brm.cc.model.GenericTemplate;
import com.oracle.communications.brm.cc.model.SearchCriterias;
import com.oracle.communications.brm.cc.modules.pcm.workers.ConfigurationWorker;
import com.oracle.communications.brm.cc.modules.pcm.workers.TemplateBaseWorker;
import com.oracle.communications.brm.cc.util.CCLogger;
import com.oracle.communications.brm.cc.util.ExceptionHelper;
import com.portal.pcm.ArrayField;
import com.portal.pcm.DecimalField;
import com.portal.pcm.EBufException;
import com.portal.pcm.EnumField;
import com.portal.pcm.FList;
import com.portal.pcm.Field;
import com.portal.pcm.IntField;
import com.portal.pcm.Poid;
import com.portal.pcm.PoidField;
import com.portal.pcm.SparseArray;
import com.portal.pcm.StrField;
import com.portal.pcm.SubStructField;
import com.portal.pcm.TStampField;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldActionName;
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldBillinfoId;
import com.portal.pcm.fields.FldBillinfoObj;
import com.portal.pcm.fields.FldConfigActionInfo;
import com.portal.pcm.fields.FldConfigActionObj;
import com.portal.pcm.fields.FldConfigScenarioObj;
import com.portal.pcm.fields.FldDueT;
import com.portal.pcm.fields.FldEntryAmount;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldMaxRow;
import com.portal.pcm.fields.FldMinRow;
import com.portal.pcm.fields.FldOverdueAmount;
import com.portal.pcm.fields.FldOverdueT;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldReason;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldScenarioObj;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

public class CustomFullPageWorker extends TemplateBaseWorker {
    
    private static final CCLogger logger = CCLogger.getCCLogger(CustomFullPageWorker.class);
    private int a;
    private int b; 
    private int c; 
    private int d;
    private int e; 
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private SearchCriterias sc;
    private Character cha = Character.MIN_VALUE;
    private boolean bool;
    protected String locationPath = "custom/eventtemplates/";
    private Set<String> seta;
    private Set<String> setb; 
    private Set<String> setc;
    private Set<String> setd;
    private Set<String> setf;
    private Set<Poid>   sete;
    private Map<String, List<String>>   mapa;
    private Map<String, AccountDetails> mapb;
    private Map<String, List<String>>   mapc; 
    private Map<String, String>         mapd;
    private Map<String, Long>           mape;
    private Map<String, List<String>>   mapf;
    private Map<String, BigDecimal>     mapg;

    public CustomFullPageWorker() { 
        this.cha = '\031';
        this.seta = new HashSet();
        this.setb = new HashSet();
        this.setc = new HashSet();
        this.setd = new HashSet();
        this.sete = new HashSet();
        this.setf = new HashSet();
        this.mapa = new HashMap<>();
        this.mapb = new HashMap<>();
        this.mapc = new HashMap<>();
        this.mapd = new HashMap<>();
        this.mape = new HashMap<>();
        this.mapf = new HashMap<>();
        this.mapg = new HashMap<>();
        this.sc = null;
    }
    
    @Override
    public FList convertToInputFListForGetTemplateRecords(String id, String secondaryId, int offset, int limit, SearchCriterias searchCriteria, List<GenericTemplate.SortbyFields> sortByFields) {
        logger.info("--- CustomFullPageWorker: convertToInputFListForGetTemplateRecords ---");
        if (offset > 0 && limit > 0) {
            if (offset > limit) {
                ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_START_GREATER_THAN_END.errorCode(), ErrorConstants.ERROR_START_GREATER_THAN_END
                        .errorMessage(), new Object[0]);
            }
        } else if (offset < 0 || limit < 0) {
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_START_END_NEGATIVE.errorCode(), ErrorConstants.ERROR_START_END_NEGATIVE
                    .errorMessage(), new Object[0]);
        }
        this.b = limit - offset;
        logger.info("--- b equals: ---" + b);
        if (this.b >=0) {
            ConfigurationWorker configurationWorker = new ConfigurationWorker();
            Configurations configurations = configurationWorker.getXMLConfigurations();
            List<Configurations.Thresholds> list = configurations.getThresholds();
            for (Configurations.Thresholds thresholds : list) {
                if (thresholds.getKey().equalsIgnoreCase("collections.pagination.size")) {
                    this.a = thresholds.getValue();
                    logger.info("CustomFullPageWorker/convertToInputFListForGetTemplateRecords: pagination size is set to: " + this.a);
                    break;
                }
            }
        } else {
            this.a = this.b + 1;
        }
        this.sc = searchCriteria;
        if (searchCriteria.getSearchTemplateName() != null && !searchCriteria.getSearchTemplateName().isEmpty()) {
            this.template = getCollectionSearchTemplate(searchCriteria.getSearchTemplateName());
        } else {
            this.template = getCollectionSearchTemplate("customFullPageTemplate");
        }
        SearchCriterias searchCriterias = new SearchCriterias();
        if (!searchCriteria.getCriterias().isEmpty()) {
            searchCriterias.getCriterias().addAll(searchCriteria.getCriterias());
            logger.info("--- CustomFullPageWorker: searchCriteria.getCriterias() is not empty ---");
        }
        searchCriterias.setSearchTemplateName(searchCriteria.getSearchTemplateName());
        FList fList = constructFilterForInputFlist(this.template.getFilter(), searchCriterias, sortByFields);
        fList.set((IntField) FldMinRow.getInst(), offset);
        fList.set((IntField) FldMaxRow.getInst(), limit);
        logger.info("--- CustomFullPageWorker: exiting convertToInputFListForGetTemplateRecords ---");
        return fList;
    }

    @Override
    public FList invokeGetTemplateRecords(FList inputFList) {
        logger.info("--- CustomFullPageWorker: ---");
        FList fList = null;
        try {
            fList = search(inputFList);
            
            if (fList != null && fList.hasField((Field) FldResults.getInst())) {
                Poid poid = fList.get((PoidField) FldPoid.getInst());
                setTargetDB(poid.getDb());
                SparseArray sparseArray = fList.get(FldResults.getInst()); //(ArrayField)
                getRecordsForTemplate(sparseArray, this.template);
                if (sparseArray.size() == this.cha) {
                    this.cha = '\001';
                }
                resetTargetDB();
            }
        } catch (Exception exception) {
            Logger.getLogger(com.oracle.communications.brm.cc.modules.pcm.workers.TemplateCollectionsWorker.class.getName()).log(Level.SEVERE, (String) null, exception);
            ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ErrorConstants.ERROR_PROCESSING_TEMPLATE
                    .errorMessage(), new Object[0]);
        }
        logger.info("--- CustomFullPageWorker: exiting invokeGetTemplateRecords ---");
        return fList;
    }

    @Override
    public List<ColumnarRecord> convertToTemplateRecordsForGetTemplateRecords(FList outputFList, String id, String secondaryId, SearchCriterias searchCriteria) throws EBufException {
        logger.info("--- CustomFullPageWorker: convertToTemplateRecordsForGetTemplateRecords ---");
        createColumnHeader(this.toReturn, this.template); //ColumnarRecord toReturn; from TemplateBaseWorker 
        this.toReturn.setXmlTemplateName("customFullPageTemplate");
        this.toReturn.setTemplateName("customFullPageTemplate");
        this.toReturn.setExtension(Boolean.valueOf(this.bool));
        boolean bool = (this.template.isSaveResults() != null && this.template.isSaveResults().booleanValue()) ? true : false;
        this.toReturn.setSaveResults(Boolean.valueOf(bool));
        ArrayList<ColumnarRecord> arrayList = new ArrayList();
        arrayList.add(this.toReturn);
        logger.info("--- CustomFullPageWorker: exiting convertToTemplateRecordsForGetTemplateRecords ---");
        return arrayList;
    }

    @Override
    public FList constructFilterForInputFlist(GenericTemplate.Filter filterTemplate, SearchCriterias searchCriteria, List<GenericTemplate.SortbyFields> sortByFields) {
        logger.info("--- CustomFullPageWorker: entering constructFilterForInputFlist ---");
        logger.info("--- CustomFullPageWorker: calling buildCollectionInputFList ---");
        FList fList = buildCollectionInputFList(searchCriteria, this.template, sortByFields);
        if (fList == null) {
            throw new ApplicationException("Problem occurred while preparing collections search input");
        }
        logger.info("--- CustomFullPageWorker exiting constructFilterForInputFlist ---");
        return fList;
    }

    @Override
    public GenericTemplate.Filter getFiltersForTemplate(String primaryId, String secondaryId, SearchCriterias searchCriteria, boolean isNonCurrency) {
        logger.info(" --- entering CustomFullPageWorker : getFiltersForTemplate ---");
        if (this.template == null) {
            this.template = getCollectionSearchTemplate(searchCriteria.getSearchTemplateName());
        }
        GenericTemplate.Filter filter = this.template.getFilter();
        logger.info(" --- exiting CustomFullPageWorker : getFiltersForTemplate ---");
        return filter;
    }
    
    protected GenericTemplate getCollectionSearchTemplate(String searchTemplateName) {
    logger.info("--- CustomFullPageWorker: entering getCollectionSearchTemplate ---");
    if (this.template == null) {
      String str = getCustomTemplateName(searchTemplateName + ".xml");
      this.template = getTemplate(str, true);
      if (this.template == null)
        this.template = getTemplate(attach(searchTemplateName), false); 
    } 
    logger.info("--- CustomFullPageWorker: exiting getCollectionSearchTemplate with value: ---" +this.template);
    return this.template;    
  }
    
   private String attach(String paramString) {
    logger.info("--- CustomFullPageWorker: attach method returning value:---" +this.locationPath + paramString + ".xml");
    return this.locationPath + paramString + ".xml";
    
  }
  
    
    protected FList search(FList inputFList) throws EBufException {
    logger.info("--- CustomFullPageWorker: search ---");
    byte by = 7;
    Poid poid = inputFList.get((PoidField)FldPoid.getInst());
    if (0L == poid.getDb())
      by = 25; 
    FList fList = opcode(by, inputFList);
    logger.info("--- CustomFullPageWorker: search --- \n" + fList.toString());
    return fList;
  }
    
    protected FList buildCollectionInputFList(SearchCriterias searchCriterias, GenericTemplate template, List<GenericTemplate.SortbyFields> sortByFields) {
    logger.info("--- CustomFullPageWorker: entering buildCollectionInputFList ---");
    FList fList = new FList();
    long l = getCurrentDB();
    try {
      HashMap<String, Criteria> hashMap = getAllCriteriaFromTemplate(template);
      String str = template.getStorableClass().getName();
      ArrayList<String> arrayList1 = new ArrayList();
      SparseArray sparseArray = new SparseArray();
      byte b1 = 1;
      byte b2 = 0;
      LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
      ArrayList<String> arrayList2 = new ArrayList();
      byte b3 = 1;
      HashMap<Object, Object> hashMap1 = new HashMap<>();
      HashMap<Object, Object> hashMap2 = new HashMap<>();
      hashMap1.put(str, Integer.toString(b3));
      StringBuilder stringBuilder = new StringBuilder("select X from ");
      stringBuilder.append(str).append(" ").append(b3);
      b3++;
      hashMap1.put("/billinfo", Integer.toString(b3));
      stringBuilder.append(", ").append("/billinfo").append(" ").append(b3);
      FList fList1 = new FList();
      fList1.set((PoidField)FldPoid.getInst(), new Poid(getCurrentDB(), -1L, "/collections_scenario"));
      sparseArray.add(b1, fList1);
      b1++;
      FList fList2 = new FList();
      fList2.set((Field)FldPoid.getInst());
      sparseArray.add(b1, fList2);
      b1++;
      FList fList3 = new FList();
      fList3.set((Field)FldScenarioObj.getInst());
      sparseArray.add(b1, fList3);
      b1++;
      if (searchCriterias != null && !searchCriterias.getCriterias().isEmpty()) {
        ArrayList<Criterias> arrayList = getUserCriteria(searchCriterias.getCriterias());
        for (byte b = 0; b < arrayList.size(); b++) {
          Criterias criterias = arrayList.get(b);
          Criteria criteria = (Criteria)hashMap.get(criterias.getField());
          if (criteria == null) {
            logger.info("CustomFullPageWorker, Passed search criteria is not configured in the corresponding search template. Exiting..");
            return null;
          } 
          if (criteria.getName().equalsIgnoreCase("schema")) {
            l = Long.parseLong(criterias.getValue());
          } else {
            String str1 = criteria.getStorableClass();
            arrayList2.add(str1);
            String str2 = criteria.getJoin();
            linkedHashMap.put(str1, str2);
            String str3 = criteria.getFieldKey();
            String str4 = criterias.getValue();
            if (str4 != null) {
              String str5 = str4;
              FList fList4 = buildArgsFList(str1, str3, str5.toLowerCase());
              String str6 = str1;
              hashMap2.put(criterias, Integer.valueOf(b2));
              sparseArray.add(b1, fList4);
              b1++;
              b2++;
              if (!str6.equals(str))
                if (!arrayList1.contains(str6) && !str6.equals("/billinfo")) {
                  arrayList1.add(str6);
                  stringBuilder.append(',');
                  stringBuilder.append(" ").append(str6).append(" ");
                  stringBuilder.append(++b3);
                  hashMap1.put(str6, Integer.toString(b3));
                }  
            } 
          } 
        } 
      } 
      stringBuilder.append(" where ").append(" ").append("1.F1 like V1").append(" and ").append("1.F2 = 2.F3").append(" ");
      int i = setSearchPhraseCustom(template, searchCriterias, stringBuilder, arrayList2, hashMap1, (HashMap)hashMap2);
      stringBuilder.append(" ");
      if (arrayList1.size() > 0) {
        stringBuilder.append(" ").append(AND).append(" ");
        byte b;
        int j;
        for (b = 0, j = arrayList1.size(); b < j; b++) {
          int k = b + 3;
          stringBuilder.append("1.F").append(++i).append(" = ").append(k).append(".F").append(++i);
          if (b != j - 1)
            stringBuilder.append(" ").append(AND).append(" "); 
          FList fList4 = new FList();
          String str1 = (String)linkedHashMap.get(str);
          String str2 = arrayList1.get(b);
          if (str.equals("/collections_scenario") && str2.equals("/billinfo")) {
            fList4.set((Field)FldBillinfoObj.getInst());
            sparseArray.add(b1++, fList4);
          } else if (str.equals("/collections_scenario") && !str2.equals("/config/collections/scenario")) {
            fList4.set((Field)FldAccountObj.getInst());
            sparseArray.add(b1++, fList4);
          } else if (str.equals("/collections_scenario") && str2.equals("/config/collections/scenario")) {
            fList4.set((Field)FldConfigScenarioObj.getInst());
            sparseArray.add(b1++, fList4);
          } else if (str1 == null || str1.trim().length() == 0) {
            fList4.set((Field)FldPoid.getInst());
            sparseArray.add(b1++, fList4);
          } else {
            fList4 = buildArgsFList(str, str1, null);
            sparseArray.add(b1++, fList4);
          } 
          String str3 = arrayList1.get(b);
          str1 = (String)linkedHashMap.get(str3);
          fList4 = new FList();
          if (str1 == null || str1.trim().length() == 0) {
            str1 = str.trim();
            if (this.converter.checkFwdSlash(str1).booleanValue()) {
              str1 = str1.substring(1, str1.length());
            } else {
              logger.info("CustomFullPageWorker, Name of the storable class is not in proper format");
            } 
            str1 = str1.substring(0, 1).toUpperCase() + str1.substring(1);
            str1 = "Fld" + str1 + "Obj";
            Field field = Field.fromName(str1);
            fList4.set(field);
            sparseArray.add(b1++, fList4);
          } else {
            fList4 = buildArgsFList(str3, str1, null);
            sparseArray.add(b1++, fList4);
          } 
        } 
      } 
      setSortingParameters(template, sortByFields, stringBuilder, i, str, sparseArray, b1);
      stringBuilder.append(" ");
      fList.set((PoidField)FldPoid.getInst(), new Poid(l, -1L, "/search"));
      fList.set((IntField)FldFlags.getInst(), 0);
      fList.set((ArrayField)FldArgs.getInst(), sparseArray);
      fList.set((StrField)FldTemplate.getInst(), stringBuilder.toString());
      fList.setElement((ArrayField)FldResults.getInst(), 0, null);
    } catch (Exception exception) {
      logger.info("CustomFullPageWorker, Problem while building the collection search template ", exception);
      return null;
    } 
    logger.info("--- CustomFullPageWorker: exiting buildCollectionInputFList ---");
    return fList;
  }
     
    

 @Override
 public Map<String, List<String>> getBillUnitsInCollectionsOfAccountInBatch(Collection<String> accountIds) throws EBufException {
    HashMap<Object, Object> hashMap = new HashMap<>();
    FList fList1 = new FList();
    fList1.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), -1L, "/search"));
    fList1.set((IntField)FldFlags.getInst(), 256);
    fList1.set((StrField)FldTemplate.getInst(), "select X from /billinfo where F1 in (V1, " + toCommaSeparatedString(accountIds) + " ) and F2.type like V2");
    FList fList2 = new FList();
    fList2.set((Field)FldPoid.getInst());
    fList2.set((Field)FldScenarioObj.getInst());
    fList2.set((Field)FldAccountObj.getInst());
    fList2.set((Field)FldBillinfoId.getInst());
    fList1.setElement((ArrayField)FldResults.getInst(), -1, fList2);
    FList fList3 = new FList();
    fList3.set((PoidField)FldAccountObj.getInst(), new Poid(getTargetDB(), 0L, "/account"));
    fList1.setElement((ArrayField)FldArgs.getInst(), 1, fList3);
    FList fList4 = new FList();
    fList4.set((PoidField)FldScenarioObj.getInst(), new Poid(getTargetDB(), 0L, "/collections_scenario"));
    fList1.setElement((ArrayField)FldArgs.getInst(), 2, fList4);
    FList fList5 = opcode(7, fList1);
    logger.info("-------------flist5--------------- \n" +fList5.toString());
    if (fList5.hasField((Field)FldResults.getInst())) {
      SparseArray sparseArray = fList5.get((ArrayField)FldResults.getInst());
      Enumeration<FList> enumeration = sparseArray.getValueEnumerator();
      while (enumeration.hasMoreElements()) {
        FList fList = enumeration.nextElement();
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(0, BRMUtility.restIdFromPoid(fList.get((PoidField)FldAccountObj.getInst())));
        arrayList.add(1, String.valueOf(fList.get((PoidField)FldScenarioObj.getInst()).getId()));
        arrayList.add(2, fList.get((StrField)FldBillinfoId.getInst()));
        hashMap.put(BRMUtility.restIdFromPoid(fList.get((PoidField)FldPoid.getInst())), arrayList); /// Map<poid,{AccountObj,fldScenarioId,billinfoId}>
      } 
    } 
    return (Map)hashMap;
  }

    @Override
    public List<ColumnarRecord> getRecordsForTemplate(SparseArray results, GenericTemplate template) {
    logger.info("--- CustomFullPageWorker: entering getRecordsForTemplate ---");
    String str = "";
    try {
      GenericTemplate.StorableClass storableClass = template.getStorableClass();
      String str1 = storableClass.getName(); // /collections_scenario
      String str2 = str1;
      HashMap<Object, Object> hashMap = new HashMap<>(); //the final hashMap that the entries will get their values
      SparseArray sparseArray = results; //the method input parameter results
      byte b = 0;
      if (sparseArray != null) {
        Enumeration<FList> enumeration = sparseArray.getValueEnumerator();
        while (enumeration.hasMoreElements()) { //get the information from sparseArray and store it to sets
          FList fList = enumeration.nextElement();
          Poid poid1 = fList.get(FldBillinfoObj.getInst()); 
          Poid poid2 = fList.get((PoidField)FldAccountObj.getInst()); // 0.0.0.1 /account 4456826 0
          Poid poid3 = fList.get((PoidField)FldPoid.getInst());
          Poid poid4 = fList.get((PoidField)FldConfigScenarioObj.getInst());
          this.setb.add(String.valueOf(poid1.getId())); // BIll info 
          this.seta.add(String.valueOf(poid2.getId())); // account obj
          this.setc.add(String.valueOf(poid3.getId())); // poid 
          this.setd.add(String.valueOf(poid4.getId())); // config scenario
        } 
        this.mapa = getBillUnitsInCollectionsOfAccountInBatch((Collection<String>)this.seta); //Get the all bill units in collections for list of account ids and put it to map
        for (Map.Entry entry : this.mapa.entrySet()) {
          List<String> list = (List)entry.getValue(); //get the value of map and put it in a list
          String str3 = String.valueOf(BRMUtility.poidFromRestId((String)entry.getKey()).getId()); //e.g 6531613
          String str4 = list.get(1);
          if (!this.setb.contains(str3)) {
            this.setb.add(str3);
            this.setc.add(str4);
            this.mapf.put((String)entry.getKey(), list);  // e.g 0.0.0.1+-billinfo+6531613
            this.setf.add(str4);
          } 
        }        
        
       if (this.seta != null && this.seta.size() > 0)
          this.mapb = getBasicAccountDetails((Collection)this.seta); //Retrieves all account basic details for the given account Ids
        if (this.setc != null && this.setc.size() > 0)
          this.mapd = getCurrentActionNameInBatches((Collection)this.setc); //get current pending action name for list of scenarios object in batches
        if (this.setf != null && this.setf.size() > 0)
          this.mapc = getCollectionsScenarioDetailsInBatch(this.setf); //Get the collection scenario details for list of scenarios ID
      } 
      if (this.mapc != null && !this.mapc.isEmpty())
        for (List<String> list : this.mapc.values())
          this.setd.add(list.get(3));  
      this.mapg = getExitAmount((Collection)this.setd); //get exit amount for scenario in batch
      if (sparseArray != null) {
        Enumeration<FList> enumeration = sparseArray.getValueEnumerator();
        while (enumeration.hasMoreElements()) {
          FList fList = enumeration.nextElement();
          String str3 = str2;
          Poid poid1 = fList.get((PoidField)FldAccountObj.getInst()); // 0.0.0.1 /account 4456826 0 ,1st loop
          Poid poid2 = fList.get((PoidField)FldBillinfoObj.getInst());
          Poid poid3 = fList.get((PoidField)FldPoid.getInst());
          Poid poid4 = fList.get((PoidField)FldConfigScenarioObj.getInst());
          if (this.sete.contains(poid3))
            continue; 
          this.sete.add(poid3);
          BigDecimal bigDecimal1 = fList.get((DecimalField)FldOverdueAmount.getInst());
          Date date1 = fList.get((TStampField)FldOverdueT.getInst());
          Date date2 = getDateInBRMTimezone();
          long l = date2.getTime() - getTimeInBRMTimezone(date1.getTime());
          int i = Math.round((float)(l / 86400000L)) + 1;
          BigDecimal bigDecimal2 = this.mapg.get(restIdFromPoid(poid4));
          BigDecimal bigDecimal3 = bigDecimal1.subtract(bigDecimal2);
          if (hashMap.get(poid1) != null) {
            ColumnarRecord.Entries entries1 = this.toReturn.getEntries().get(((Integer)hashMap.get(poid1)));
            ColumnarRecord.Entries.Cells cells1 = entries1.getCells().get(this.f);
            HashMap<Object, Object> hashMap1 = new HashMap<>();
            hashMap1.put("minimumPayment", bigDecimal3);
            hashMap1.put("billinfoId", BRMUtility.restIdFromPoid(poid2));
            hashMap1.put("overdueAmount", bigDecimal1);
            hashMap1.put("overdueDays", i);
            hashMap1.put("billUnitName", ((List)this.mapa.get(BRMUtility.restIdFromPoid(poid2))).get(2));
            hashMap1.put("currentActionName", this.mapd.get(String.valueOf(poid3.getId())));
            hashMap1.put("currentActionDueDate", this.mape.get(String.valueOf(poid3.getId())));
            cells1.setName("billUnitDetails");
            cells1.getArgs().add((new ObjectMapper()).writeValueAsString(hashMap1));
            ColumnarRecord.Entries.Cells cells2 = entries1.getCells().get(this.e);
            String str4 = cells2.getArgs().get(0);
            int j = Integer.parseInt(str4);
            int k = (j > i) ? j : i;
            cells2.getArgs().remove(0);
            cells2.getArgs().add(Integer.toString(k));
            ColumnarRecord.Entries.Cells cells3 = entries1.getCells().get(this.g);
            BigDecimal bigDecimal = bigDecimal1.add(new BigDecimal(cells3.getArgs().get(0)));
            cells3.getArgs().remove(0);
            cells3.getArgs().add(bigDecimal.toString());
            ColumnarRecord.Entries.Cells cells4 = entries1.getCells().get(this.h);
            cells4.getArgs().remove(0);
            cells4.getArgs().add("true");
            continue;
          } 
          hashMap.put(poid1, Integer.valueOf(b));
          b++;
          ColumnarRecord.Entries entries = new ColumnarRecord.Entries();
          this.toReturn.getEntries().add(entries);
          byte b1 = 0;
          for (GenericTemplate.StorableClass.Column column : storableClass.getColumn()) {
            if (column.getRefFieldId() != null)
              continue; 
            if (column.getName().equals("billUnitDetails")) {
              this.f = b1;
              ColumnarRecord.Entries.Cells cells1 = new ColumnarRecord.Entries.Cells();
              cells1.setType(column.getType());
              HashMap<Object, Object> hashMap1 = new HashMap<>();
              hashMap1.put("billinfoId", BRMUtility.restIdFromPoid(poid2));
              hashMap1.put("minimumPayment", bigDecimal3);
              hashMap1.put("overdueAmount", bigDecimal1);
              hashMap1.put("overdueDays", i);
              hashMap1.put("billUnitName", ((List)this.mapa.get(BRMUtility.restIdFromPoid(poid2))).get(2));
              hashMap1.put("currentActionName", this.mapd.get(String.valueOf(poid3.getId())));
              hashMap1.put("currentActionDueDate", this.mape.get(String.valueOf(poid3.getId())));
              cells1.setName("billUnitDetails");
              cells1.getArgs().add((new ObjectMapper()).writeValueAsString(hashMap1));
              entries.getCells().add(cells1);
              continue;
            } 
            ColumnarRecord.Entries.Cells cells = new ColumnarRecord.Entries.Cells();
            cells.getFormat().addAll(column.getFormat());
            cells.getStyles().addAll(column.getStyles());
            cells.setType(column.getType());
            cells.getTypes().addAll(column.getTypes());
            cells.setName(column.getName());
            AccountDetails accountDetails = this.mapb.get(String.valueOf(poid1.getId())); 
            String str4 = accountDetails.getAccountNumber(); // 0.0.0.1-4456826
            String str5 = String.valueOf(accountDetails.getCurrency());
            Contacts contacts = accountDetails.getContacts().get(0);
            String str6 = contacts.getFirstName();
            String str7 = contacts.getLastName();
            if (column.getName().equals("firstName")) {
              cells.getArgs().add(str6);
              this.i = b1;
            } else if (column.getName().equals("lastName")) {
              this.j = b1;
              cells.getArgs().add(str7);
            } else if (column.getName().equals("accountNumber")) {
              cells.getArgs().add(str4);
            } else if (column.getName().equals("oldestBillUnitOverdueDays")) {
              cells.getArgs().add(Integer.toString(i));
              this.e = b1;
            } else if (column.getName().equals("totalOverdueAmount")) {
              this.g = b1;
              processField(cells, str3, fList, column, entries);
            } else if (column.getName().equals("isMultipleBillUnit")) {
              this.h = b1;
              cells.getArgs().add("false");
            } else if (column.getName().equals("accountCurrency")) {
              cells.getArgs().add(str5);
            } else if (column.getName().equals("accountId")) {
              this.k = b1;
              processField(cells, str3, fList, column, entries);
            } else {
              processField(cells, str3, fList, column, entries);
            } 
            entries.getCells().add(cells);
            b1++;
          } 
        } 
      } 
     /*for (Map.Entry<String, ?> entry : this.mapf.entrySet()) {
        Poid poid = poidFromRestId((String)entry.getValue());
        if (hashMap.get(poid) == null)
          continue; 
        ColumnarRecord.Entries entries = this.toReturn.getEntries().get(((Integer)hashMap.get(poid)));
        ColumnarRecord.Entries.Cells cells1 = entries.getCells().get(this.f);
        HashMap<Object, Object> hashMap1 = new HashMap<>();
        hashMap1.put("billinfoId", entry.getKey());
        List<String> list = this.mapc.get(entry.getKey());
        int i = Integer.parseInt(list.get(2));
        BigDecimal bigDecimal1 = new BigDecimal(list.get(1));
        String str3 = list.get(3);
        BigDecimal bigDecimal2 = this.mapg.get(str3);
        BigDecimal bigDecimal3 = bigDecimal1.subtract(bigDecimal2);
        hashMap1.put("overdueAmount", bigDecimal1);
        hashMap1.put("overdueDays", i);
        hashMap1.put("minimumPayment", bigDecimal3);
        String str4 = ((List<String>)this.mapa.get(entry.getKey())).get(2);
        hashMap1.put("billUnitName", str4);
        hashMap1.put("currentActionName", this.mapd.get(String.valueOf(BRMUtility.poidFromRestId(list.get(0)).getId())));
        hashMap1.put("currentActionDueDate", this.mape.get(String.valueOf(BRMUtility.poidFromRestId(list.get(0)).getId())));
        cells1.setName("billUnitDetails");
        cells1.getArgs().add((new ObjectMapper()).writeValueAsString(hashMap1)); //put the hashmap1 
        ColumnarRecord.Entries.Cells cells2 = entries.getCells().get(this.e);
        String str5 = cells2.getArgs().get(0);
        int j = Integer.parseInt(str5);
        int k = (j > i) ? j : i;
        cells2.getArgs().remove(0);
        cells2.getArgs().add(Integer.toString(k));
        ColumnarRecord.Entries.Cells cells3 = entries.getCells().get(this.g);
        BigDecimal bigDecimal4 = bigDecimal1.add(new BigDecimal(cells3.getArgs().get(0)));
        cells3.getArgs().remove(0);
        cells3.getArgs().add(bigDecimal4.toString());
        ColumnarRecord.Entries.Cells cells4 = entries.getCells().get(this.h);
        cells4.getArgs().remove(0);
        cells4.getArgs().add("true"); 
      } */
    } catch (Exception exception) {
      logger.severe("Unable to retrieve the records : error while retrieving the field :" + str, exception);
      ExceptionHelper.buildErrorInfo(ErrorConstants.ERROR_PROCESSING_TEMPLATE.errorCode(), ErrorConstants.ERROR_PROCESSING_TEMPLATE
          .errorMessage(), new Object[0]);
    } 
    ArrayList<ColumnarRecord> arrayList = new ArrayList();
    arrayList.add(this.toReturn);
    logger.info("--- CustomFullPageWorker exiting getRecordsForTemplate ---");
    return arrayList;
  }
    
    
    @Override
    protected void processField(ColumnarRecord.Entries.Cells viCol, String storableClassType, FList storableClassFList, GenericTemplate.StorableClass.Column column, ColumnarRecord.Entries row) throws Exception {
        logger.info("--- CustomFullPageWorker entering processField ---");
        for (String str : column.getFields()) {
         Object object = getFieldFromFList(storableClassType, str, storableClassFList);
         if (object instanceof Date) {
            long l = ((Date)object).getTime();
            viCol.getArgs().add(String.valueOf(l));
            continue;
         } 
         //logger.info("--- storableClassType ------" +storableClassType);
         processFieldForColumnName(viCol, storableClassType, storableClassFList, row, str, object);
        } 
        logger.info("--- CustomFullPageWorker exiting processField ---");   
  }
    
   @Override
   protected Object getFieldFromFList(String storableClassType, String field, FList flist) throws Exception {
    logger.info("--- CustomFullPageWorker entering getFieldFromFList ---"); 
    String str = this.converter.convertFieldForFilter(storableClassType, field);
     //logger.info("Value strrr: --->" +str);
    int i = str.indexOf(".");
    if (i == -1) {
      Field field2 = fieldInstanceFromFList(str, flist); 
      return flist.get(field2);
    } 
    str = str.replaceAll("\\[any\\]", "");
    int j = 0;
    int k = str.indexOf(".");
    Field field1 = null;
    while (k != -1) {
      String str1 = str.substring(j, k);
      j = k + 1;
      k = str.indexOf(".", j);
      field1 = fieldInstanceFromFList(str1, flist);
      if (field1 instanceof ArrayField) {
        SparseArray sparseArray = flist.get((ArrayField)field1);
        flist = sparseArray.getValueEnumerator().nextElement();
        if (k == -1)
          k = str.length(); 
        continue;
      } 
      if (field1 instanceof SubStructField) {
        flist = (FList)flist.get(field1);
        if (k == -1)
          k = str.length(); 
      } 
    } 
    Object object = "";
    if (field1 != null && flist.hasField(field1))
      object = flist.get(field1); 
    logger.info("--- CustomFullPageWorker exiting getFieldFromFList ---"); 
    return object;
  }  
    
    
  protected Map<String, String> getCurrentActionNameInBatches(Collection<String> scenariosIds) throws EBufException {
    logger.info("CustomFullPageWorker entering getCurrentActionNameInBatches");
    int i = getCharactersLength(scenariosIds);
    Map<String, String> map = new HashMap<>();
    if (scenariosIds.size() > 0) {
      ArrayList<String> arrayList = new ArrayList<>(scenariosIds);
      if (i > 1250) {
        int j = scenariosIds.size();
        int k = j / (i / 1250 + 1);
        int n = 0;
        for (int m = 0; n <= j / k * k + k; ) {
          List<String> list = null;
          if (n >= j) {
            n = j;
            list = arrayList.subList(m, n);
            map.putAll(getCurrentActionNameList(list));
            break;
          } 
          list = arrayList.subList(m, n);
          map.putAll(getCurrentActionNameList(list));
          m = n;
          n += k;
        } 
      } else {
        map.putAll(getCurrentActionNameList(scenariosIds));
      } 
    } else {
      map = Collections.emptyMap();
    } 
    logger.info("--- CustomFullPageWorker exiting getCurrentActionNameInBatches ---");
    return (Map)map;
  }
  
  
  @Override
  protected void processFieldForColumnName(ColumnarRecord.Entries.Cells viCol, String storableClassType, FList flist, ColumnarRecord.Entries row, String field, Object value) throws Exception {
    logger.info("--- CustomFullPageWorker entering processFieldForColumnName ---");
   
    String ss = value.toString();
    if(ss.contains(" /")){
      ss = ss.replace(' ','+');     
      ss = ss.replace("/","-");
      String substring = ss.substring(0, ss.length() - 2);
      //logger.info("Value toString: --->" +substring);
      viCol.getArgs().add(substring);
      logger.info("--- CustomFullPageWorker exiting processFieldForColumnName ---");
    }else{
    //logger.info("Value toString: --->" +ss);
    viCol.getArgs().add(ss);
    logger.info("--- CustomFullPageWorker exiting processFieldForColumnName ---");
    }
  }
  
   private Map<String, String> getCurrentActionNameList(Collection<String> paramCollection) throws EBufException {
    logger.info("--- CustomFullPageWorker entering getCurrentActionNameList ---");
    HashSet<String> hashSet = new HashSet();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    FList fList1 = new FList();
    fList1.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), -1L, "/search"));
    fList1.set((IntField)FldFlags.getInst(), 0);
    fList1.set((StrField)FldTemplate.getInst(), "select X from /collections_action where ( F1 in (V1, " + toCommaSeparatedString(paramCollection) + " ) and F2 = V2 ) order by F3 desc ");
    byte v = 1;
    SparseArray sparseArray = new SparseArray();
    FList fList2 = new FList();
    fList2.set((PoidField)FldScenarioObj.getInst(), new Poid(getTargetDB(), 0L, "/collection_scenario"));
    sparseArray.add(v++, fList2);
    FList fList3 = new FList();
    fList3.set((EnumField)FldStatus.getInst(), 0);
    sparseArray.add(v++, fList3);
    FList fList4 = new FList();
    fList4.set((Field)FldDueT.getInst());
    sparseArray.add(v++, fList4);
    fList1.set((ArrayField)FldArgs.getInst(), sparseArray);
    FList fList5 = new FList();
    fList5.set((Field)FldConfigActionObj.getInst());
    fList5.set((Field)FldScenarioObj.getInst());
    fList5.set((Field)FldDueT.getInst());
    fList1.setElement((ArrayField)FldResults.getInst(), 0, fList5);
    FList fList6 = opcode(7, fList1);
    if (fList6 != null && fList6.hasField((Field)FldResults.getInst())) {
      SparseArray sparseArray1 = fList6.get((ArrayField)FldResults.getInst());
      Enumeration<FList> enumeration = sparseArray1.elements();
      while (enumeration.hasMoreElements()) {
        FList fList = enumeration.nextElement();
        Poid poid = fList.get((PoidField)FldConfigActionObj.getInst());
        hashSet.add(String.valueOf(poid.getId()));
        hashMap1.put(String.valueOf(fList.get((PoidField)FldScenarioObj.getInst()).getId()), String.valueOf(poid.getId()));
        if (fList.hasField((Field)FldDueT.getInst()) && fList.get((TStampField)FldDueT.getInst()) != null) {
          String str = String.valueOf(fList.get((PoidField)FldScenarioObj.getInst()).getId());
          Long long_ = fList.get((TStampField)FldDueT.getInst()).getTime();
          this.mape.put(str, long_);
          continue;
        } 
        this.mape.put(String.valueOf(fList.get((PoidField)FldScenarioObj.getInst()).getId()), null);
      } 
    } 
    if (!hashSet.isEmpty()) {
      FList fList7 = new FList();
      fList7.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), -1L, "/search"));
      fList7.set((IntField)FldFlags.getInst(), 0);
      fList7.set((StrField)FldTemplate.getInst(), "select X from /config/collections/action where F1 in (V1, " + toCommaSeparatedString(hashSet) + " ) ");
      byte b1 = 1;
      SparseArray sparseArray1 = new SparseArray();
      FList fList8 = new FList();
      fList8.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), 0L, "/config/collections/action"));
      sparseArray1.add(b1++, fList8);
      fList7.set((ArrayField)FldArgs.getInst(), sparseArray1);
      FList fList9 = new FList();
      FList fList10 = new FList();
      fList10.set((Field)FldActionName.getInst());
      fList9.set((SubStructField)FldConfigActionInfo.getInst(), fList10);
      fList7.setElement((ArrayField)FldResults.getInst(), 0, fList9);
      FList fList11 = opcode(7, fList7);
      if (fList11 != null && fList11.hasField((Field)FldResults.getInst())) {
        SparseArray sparseArray2 = fList11.get((ArrayField)FldResults.getInst());
        Enumeration<FList> enumeration = sparseArray2.elements();
        while (enumeration.hasMoreElements()) {
          FList fList12 = enumeration.nextElement();
          FList fList13 = fList12.get((SubStructField)FldConfigActionInfo.getInst());
          String str = fList13.get((StrField)FldActionName.getInst());
          hashMap2.put(String.valueOf(fList12.get((PoidField)FldPoid.getInst()).getId()), str);
        } 
      } 
      for (Map.Entry<Object, Object> entry : hashMap1.entrySet()) {
        if (hashMap2.get(entry.getValue()) != null) {
          entry.setValue(hashMap2.get(entry.getValue()));
          continue;
        } 
        entry.setValue(this.a);
      } 
    } else {
      for (Map.Entry<Object, Object> entry : hashMap1.entrySet())
        entry.setValue(this.a); 
    } 
    logger.info("--- CustomFullPageWorker exiting getCurrentActionNameList--- ");
    return (Map)hashMap1;
  }
 
  public Map<String, List<String>> getCollectionsScenarioDetailsInBatch(Collection<String> scenarioIds) throws EBufException {
    logger.info("--- CustomFullPageWorker entering getCollectionsScenarioDetailsInBatch ---");
    HashMap<Object, Object> hashMap = new HashMap<>();
    FList fList1 = new FList();
    fList1.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), -1L, "/search"));
    fList1.set((IntField)FldFlags.getInst(), 256);
    fList1.set((StrField)FldTemplate.getInst(), "select X from /collections_scenario where F1 in (V1, " + toCommaSeparatedString(scenarioIds) + " )");
    FList fList2 = new FList();
    fList2.set((Field)FldPoid.getInst());
    fList2.set((Field)FldOverdueT.getInst());
    fList2.set((Field)FldOverdueAmount.getInst());
    fList2.set((Field)FldBillinfoObj.getInst());
    fList2.set((Field)FldConfigScenarioObj.getInst());
    fList1.setElement((ArrayField)FldResults.getInst(), -1, fList2);
    FList fList3 = new FList();
    fList3.set((PoidField)FldPoid.getInst(), new Poid(getTargetDB(), 0L, "/collections_scenario"));
    fList1.setElement((ArrayField)FldArgs.getInst(), 1, fList3);
    FList fList4 = opcode(7, fList1);
    if (fList4.hasField((Field)FldResults.getInst())) {
      SparseArray sparseArray = fList4.get((ArrayField)FldResults.getInst());
      Enumeration<FList> enumeration = sparseArray.getValueEnumerator();
      while (enumeration.hasMoreElements()) {
        FList fList = enumeration.nextElement();
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(0, BRMUtility.restIdFromPoid(fList.get((PoidField)FldPoid.getInst())));
        arrayList.add(1, String.valueOf(fList.get((DecimalField)FldOverdueAmount.getInst())));
        Date date1 = fList.get((TStampField)FldOverdueT.getInst());
        Date date2 = new Date();
        long l = date2.getTime() - date1.getTime();
        int i = Math.round((float)(l / 86400000L)) + 1;
        arrayList.add(2, Integer.toString(i));
        arrayList.add(3, BRMUtility.restIdFromPoid(fList.get((PoidField)FldConfigScenarioObj.getInst())));
        String str = BRMUtility.restIdFromPoid(fList.get((PoidField)FldBillinfoObj.getInst()));
        hashMap.put(str, arrayList);
      } 
    } 
    logger.info("--- CustomFullPageWorker exiting getCollectionsScenarioDetailsInBatch ---");
    return (Map)hashMap;
  }
  
  
    @Override
    protected ArrayList<Criterias> getUserCriteria(List<Criterias> criterias) {
    logger.info("--- CustomFullPageWorker entering getUserCriteria ---");
    ArrayList<Criterias> arrayList = new ArrayList();
    for (Criterias criterias1 : criterias) {
      List<Criterias.Groups> list = criterias1.getGroups();
      if (list != null && !list.isEmpty()) {
        for (Criterias.Groups groups : list) {
          List<Criterias> list1 = groups.getCriterias();
          for (Criterias criterias2 : list1) {
            if (criterias2.getField().equals("overdueT") && criterias2.getOperator().equals("=")) {
              int i = Integer.parseInt(criterias2.getValue());
              Calendar calendar1 = getCalendarInBRMTimezone();
              calendar1.set(11, 0);
              calendar1.set(12, 0);
              calendar1.set(13, 0);
              calendar1.set(14, 0);
              Date date1 = calendar1.getTime();
              Date date2 = (new DateTime(date1)).minusDays(i).toDate();
              String str1 = Long.toString(convertTimeToBRMTimezone(date2.getTime()) / 1000L);
              criterias2.setValue(str1);
              criterias2.setOperator(">=");
              criterias2.setName("equalCriteria");
              arrayList.add(criterias2);
              Calendar calendar2 = getCalendarInBRMTimezone();
              calendar2.set(11, 23);
              calendar2.set(12, 59);
              calendar2.set(13, 59);
              calendar2.set(14, 999);
              Date date3 = calendar2.getTime();
              Date date4 = (new DateTime(date3)).minusDays(i).toDate();
              String str2 = Long.toString(convertTimeToBRMTimezone(date4.getTime()) / 1000L);
              Criterias criterias3 = new Criterias();
              criterias3.setField("overdueT");
              criterias3.setValue(str2);
              criterias3.setOperator("<=");
              arrayList.add(criterias3);
              continue;
            } 
            if (criterias2.getField().equals("overdueT") && (criterias2.getOperator().equals("<") || criterias2.getOperator().equals("<=") || criterias2.getOperator().equals(">") || criterias2.getOperator().equals(">="))) {
              int i = Integer.parseInt(criterias2.getValue());
              String str1 = criterias2.getOperator();
              String str2 = null;
              if (str1.equals("<")) {
                str2 = ">";
              } else if (str1.equals(">")) {
                str2 = "<";
              } else if (str1.equals("<=")) {
                str2 = ">=";
              } else if (str1.equals(">=")) {
                str2 = "<=";
              } 
              Calendar calendar = getCalendarInBRMTimezone();
              if (str2 != null)
                if (str2.equals(">=") || str2.equals(">")) {
                  calendar.set(11, 0);
                  calendar.set(12, 0);
                  calendar.set(13, 0);
                  calendar.set(14, 0);
                } else if (str2.equals("<=") || str2.equals("<")) {
                  calendar.set(11, 23);
                  calendar.set(12, 59);
                  calendar.set(13, 59);
                  calendar.set(14, 999);
                }  
              Date date1 = calendar.getTime();
              Date date2 = (new DateTime(date1)).minusDays(i).toDate();
              String str3 = Long.toString(convertTimeToBRMTimezone(date2.getTime()) / 1000L);
              criterias2.setValue(str3);
              if (str2 != null)
                criterias2.setOperator(str2); 
              arrayList.add(criterias2);
              continue;
            } 
            arrayList.add(criterias2);
          } 
        } 
        continue;
      } 
      arrayList.add(criterias1);
    } 
    logger.info("--- CustomFullPageWorker exiting getUserCriteria ---");
    return arrayList;
  }
    
    
   protected int setSearchPhraseCustom(GenericTemplate template, SearchCriterias searchCriterias, StringBuilder sbSearchQuery, ArrayList<String> listAssociatedClasses, HashMap mapStorableClassandID, HashMap<Criterias, Integer> criterias) {
    logger.info("--- CustomFullPageWorker entering setSearchPhraseCustom ---");
    byte b = 3;
    if (searchCriterias != null && !searchCriterias.getCriterias().isEmpty()) {
      List<Criterias> list = searchCriterias.getCriterias();
      for (byte b1 = 0; b1 < list.size(); b1++) {
        Criterias criterias1 = list.get(b1);
        if (!"schema".equalsIgnoreCase(criterias1.getName())) {
          sbSearchQuery.append(" ").append(AND).append(" ");
          List<Criterias.Groups> list1 = criterias1.getGroups();
          if (list1 != null && !list1.isEmpty()) {
            for (Criterias.Groups groups : list1) {
              List<Criterias> list2 = groups.getCriterias();
              for (byte b2 = 0; b2 < list2.size(); b2++) {
                Criterias criterias2 = list2.get(b2);
                Integer integer = criterias.get(criterias2);
                if (integer != null) {
                  int i = integer;
                  String str1 = listAssociatedClasses.get(i);
                  String str2 = (String)mapStorableClassandID.get(str1);
                  String str3 = this.converter.convertFieldForFilter(str1, criterias2.getField());
                  StringTokenizer stringTokenizer = new StringTokenizer(str3, ".");
                  String str4 = "";
                  while (stringTokenizer.hasMoreTokens())
                    str4 = stringTokenizer.nextToken(); 
                  Field field = Field.fromName(str4);
                  boolean bool = true;
                  if (field != null && (
                    field.getTypeID() == 14 || field.getTypeID() == 7))
                    bool = false; 
                  b++;
                  String str5 = getJoinOperatorForCriteria(template, criterias2);
                  if (str5.equalsIgnoreCase(OR) && 
                    b2 == 0 && list2.size() > 1)
                    sbSearchQuery.append(" ").append(openBrace).append(" "); 
                  String str6, str7;
                  String str8 = criterias2.getOperator();
                  String str9 = criterias2.getValue().replace("*", "%");
                  String str10 = evaluateOperator(str9, str8);
                  if (bool && criterias2.getValue().trim().isEmpty()) {
                    sbSearchQuery.append(str2).append(".F").append(b);
                    sbSearchQuery.append(" is null");
                  } else {
                    str6 = "lower( ";
                    str7 = " )";
                    sbSearchQuery = bool ? sbSearchQuery.append(str6) : sbSearchQuery;
                    sbSearchQuery.append(str2).append(".F").append(b);
                    sbSearchQuery = bool ? sbSearchQuery.append(str7) : sbSearchQuery;
                    sbSearchQuery.append(str10).append("V").append(b);
                  } 
                  if (b2 != list2.size() - 1)
                    sbSearchQuery.append(" ").append(str5).append(" "); 
                  if (str5.equalsIgnoreCase(OR) && 
                    b2 == list2.size() - 1 && list2.size() > 1)
                    sbSearchQuery.append(" ").append(closeBrace).append(" "); 
                } 
                if (criterias2.getField().equals("overdueT") && "equalCriteria".equals(criterias2.getName())) {
                  b++;
                  sbSearchQuery.append(" ").append(AND).append(" F").append(b).append(" <= ").append("V").append(b).append(" ");
                } 
              } 
            } 
          } else {
            Integer integer = criterias.get(criterias1);
            if (integer != null) {
              int ii = integer;
              String str1 = listAssociatedClasses.get(ii);
              String str2 = (String)mapStorableClassandID.get(str1);
              b++;
              String str3 = "", str4 = "";
              String str5 = criterias1.getOperator();
              String str6 = evaluateOperator(criterias1.getValue(), str5);
              if (str6.trim().equals(LIKE)) {
                str3 = "lower( ";
                str4 = " )";
              } 
              sbSearchQuery.append(str3).append(str2).append(".F").append(b).append(str4).append(str6).append("V").append(b);
            } 
          } 
        } 
      } 
    } 
    logger.info("--- CustomFullPageWorker exiting setSearchPhraseCustom ---");
    return b;
  }
  
}
