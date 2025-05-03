package com.newsalesbeatApp.utilityclass;

/*
 * Created by MTC on 21-07-2017.
 */

public class SbAppConstants {

    //private static String API_DOMAIN_V2 = SBApplication.DOMAIN + "api/v2/";
    //private static String API_DOMAIN_V3 = SBApplication.DOMAIN + "api/v3/";
    //public static String API_DOMAIN_V4 = SBApplication.DOMAIN + "api/v4/";


    //@Umesh
    public static String API_DOMAIN_V4 = SBApplication.DOMAIN + "api/Mobile/";
    public static String API_DOMAIN_V4_AUTH = SBApplication.DOMAIN + "api/Authenticate/";
    //public static String API_DOMAIN_V4 = "http://192.168.1.7:80/api/Mobile/";
    //public static String API_DOMAIN_V4_AUTH = "http://192.168.1.7:80/api/Authenticate/";

    public static final String GET_CMNY_INFO = "getLogin?";
    public static final String USER_LOG_IN = "employeeLogin?";
    public static final String GET_BEAT_LIST = "getBeats"; //Not Working 20220916
    public static final String GET_TOWN_LIST = API_DOMAIN_V4 + "getTowns"; //DownloadDataService LineNo 1538
    public static final String GET_PRODUCT_LIST = "getSku";
    public static final String GET_TODAY_SUMMARY = API_DOMAIN_V4 + "getTodaySummary";
    public static final String GET_NEWDISTRIBUTORFORMJSON = "GetNewDistributorFormJson";
    //public static final String GET_EMP_RECORD_BY_DATE = API_DOMAIN_V4+"employee-reports/date/"; // DownloadDataService LineNo 177
    public static final String GET_EMP_RECORD_BY_MONTH = API_DOMAIN_V4 + "getEmpOutputByMonth"; // DownloadDataService LineNo 1400
    public static final String GET_EMP_LEADER_BOARD = API_DOMAIN_V4 + "getEmployeeLeaderboard"; //DownloadDataService LineNo 1209
    // public static final String GET_EMP_KRA_BY_DATE = API_DOMAIN_V4+"getKraByDate"; //DownloadDataService LineNo 1080
    public static final String GET_PROMOTION = API_DOMAIN_V4 + "getCampaigns";
    public static final String GET_SECONDARY_SALE_BY_DATE = API_DOMAIN_V4 + "getMonthlySecondaryKraByDate";
    public static final String GET_PRIMARY_SALE_BY_DATE = API_DOMAIN_V4 + "getMonthlyPrimaryKraByDate"; //DownloadDataService LineNo 593
    public static final String GET_RETAILERS_FEEDBACK = "retailerFeedback";
    public static final String FULL_DAY_ACTIVITY = "fullDayActivity";
    public static final String GET_INCENTIVE = API_DOMAIN_V4 + "getMonthlyIncentive";  //DownloadDataService LineNo 972
    public static final String GET_PRIMARY_SALE_HISTORY = API_DOMAIN_V4 + "getMonthlyPrimaryKraByDateHistory";
    public static final String GET_DISTRIBUTOR_TAR_ACH = API_DOMAIN_V4 + "getMonthlyDistributorTargetAchievement";
    public static final String DISTRIBUTORS_2 = "getDistributorsByTown";
    public static final String SUBMIT_DISTRIBUTOR_CLOSING = "submitClosingStock";
    public static final String SUBMIT_ORDER = API_DOMAIN_V4 + "submitOrders";
    public static final String SUBMIT_ORDER2 = "submitOrders";
    public static final String SUBMIT_DISTRIBUTOR_STOCK_INFO = "submitOpeningStock";
    public static final String ADD_NEW_RETAILER = API_DOMAIN_V4 + "addRetailer";
    public static final String ADD_NEW_RETAILER2 = "addRetailer"; //Not in Use...
    public static final String ADD_NEW_PREFERRED_RETAILER = "preferredRetailer";
    public static final String ADD_NEW_PREFERRED_RETAILER_SHOP = "preferredRetailer/shop-detail";
    public static final String ADD_NEW_DISTRIBUTOR = "newDistributorSearch";
    public static final String SEND_PATH = "getEmployeePath";
    public static final String CANCEL_ORDER = "cancelOrder";
    public static final String EMP_BEAT_VISIT = "employeeBeatVisit";
    public static final String SUBMIT_DISTRIBUTOR_ORDER = "submitDistOrders";
    public static final String CANCEL_DISTRIBUTOR_ORDER = "cancelDistOrder";
    public static final String SUBMIT_ERROR = "submitError";

    //API FOR GETTING DATA FROM SERVER
    public static String API_GET_CMNY_INFO = API_DOMAIN_V4 + "getLogin"; // SplashScreen LineNo 536
    public static String API_USER_LOG_IN = API_DOMAIN_V4 + "employeeLogin"; //LoginScreen LineNo 231  //DownloadDataService LineNo 1830 //ServerCall 252
    public static String API_AuthencateUser = API_DOMAIN_V4_AUTH + "AuthencateUser";
    //public static String API_GET_BEAT_LIST = API_DOMAIN_V4 + "getBeats?";
    public static String GET_TOWN_LIST_BY_SEARCH = API_DOMAIN_V4 + "getTownsBySearch?";
    public static String API_GET_TOWN_LIST = API_DOMAIN_V4 + "getTowns?";
    public static String API_GET_PRODUCT_LIST = API_DOMAIN_V4 + "getSku?";
    public static String API_GET_PJPS = API_DOMAIN_V4 + "getPjps";
    public static String API_GET_BEAT_PLAN = API_DOMAIN_V4 + "beatPlan";
    public static String API_GET_BEAT_PLAN_BY_DATE = API_DOMAIN_V4 + "beatPlanByDate"; //@Umesh 14-03-2022
    public static String API_TO_GET_CATALOG = API_DOMAIN_V4 + "getCatalogue?";
    public static String API_GET_EMP_RECORD_BY_DATE = API_DOMAIN_V4 + "employee_reports"; //DownloadDataService LineNo 178
    public static String API_GET_EMP_RECORD_BY_MONTH = API_DOMAIN_V4 + "getEmpOutputByMonth";
    public static String API_GET_EMP_LEADER_BOARD = API_DOMAIN_V4 + "getEmployeeLeaderboard"; //DownloadDataService LineNo 1209 //DownloadDataService LineNo 1426
    public static String API_GET_EMP_KRA_BY_DATE = API_DOMAIN_V4 + "getKraByDate";  //DownloadDataService LineNo 1080
    public static String API_GET_PROMOTION = API_DOMAIN_V4 + "getCampaigns";
    public static String API_GET_VISIT_HISTORY = API_DOMAIN_V4 + "getRetailerOrderHistory";
    public static String API_GET_DIS_OPENINGSTOCK_HISTORY = API_DOMAIN_V4 + "getOpeningStockHistory";
    public static String API_GET_DIS_VISIT_HISTORY = API_DOMAIN_V4 + "getDistributorOrderHistory";
    public static String API_GET_SECONDARY_SALE_BY_DATE = API_DOMAIN_V4 + "getMonthlySecondaryKraByDate"; //DownloadDataService LineNo 693
    public static String API_GET_PRIMARY_SALE_BY_DATE = API_DOMAIN_V4 + "getMonthlyPrimaryKraByDate"; //DownloadDataService LineNo 593
    public static String API_GET_CLAIM_HISTORY = API_DOMAIN_V4 + "getClaimHistory";
    //public static String API_GET_EMP_LIST = API_DOMAIN_V4 + "zones/"; @Umesh 23-03-2022
    public static String API_GET_EMP_LIST = API_DOMAIN_V4 + "EmployeeByZones"; //DashboardFragment 3118
    public static String API_GET_DAILY_SUMMARY = API_DOMAIN_V4 + "employeeSummary";
    public static String API_GET_RETAILERS_FEEDBACK = API_DOMAIN_V4 + "retailerFeedback";
    //    public static String API_GET_ORDER_HISTORY = API_DOMAIN_V4 + "employee-pc-report/date/";
    public static String API_GET_ORDER_HISTORY = API_DOMAIN_V4 + "employee_pc_report"; //@Umesh
    public static String API_FULL_DAY_ACTIVITY = API_DOMAIN_V4 + "fullDayActivity";
    public static String API_NEW_DISTRIBUTOR_HISTORY = API_DOMAIN_V4 + "newDistributerHistory";
    public static String API_GET_INCENTIVE = API_DOMAIN_V4 + "getMonthlyIncentive";
    public static String API_GET_INCENTIVE_HISTORY = API_DOMAIN_V4 + "getMonthlyIncentiveInfo";
    public static String API_GET_PRIMARY_SALE_HISTORY = API_DOMAIN_V4 + "getMonthlyPrimaryKraByDateHistory"; //DownloadDataService LineNo 1738
    public static String API_GET_DISTRIBUTOR_TAR_ACH = API_DOMAIN_V4 + "getMonthlyDistributorTargetAchievement"; //DownloadDataService LineNo 461
    public static String API_GET_PARTY_OUTSTANDING = API_DOMAIN_V4 + "getOutstanding";
    public static String API_GET_RECAP = API_DOMAIN_V4 + "getRecap";
    public static String API_GET_MAPPING_DETAILS = API_DOMAIN_V4 + "getTownData?"; // Not Working Replaced By Below
    public static String API_GET_MAPPING_DistributorsAndBeats = API_DOMAIN_V4 + "getTownDistributorsAndBeats?";
    public static String API_GET_SETTING = "http://testsalesbeat.rungtatea.in/api/Master/GetSettings";
    public static String API_GET_MAPPING_RetailerAndSkus = API_DOMAIN_V4 + "getTownRetailerAndSkus?";
    public static String API_GET_DISTRIBUTORS = API_DOMAIN_V4 + "getDistributors";
    public static String API_GET_DISTRIBUTORS_2 = API_DOMAIN_V4 + "getDistributorsByTown"; //DashboardFragment
    public static String API_GET_BEATS = API_DOMAIN_V4 + "getBeats";
    public static String API_GET_BEATS_2 = API_DOMAIN_V4 + "getBeatsByDist?";
    public static String API_GET_RETAILERS = API_DOMAIN_V4 + "getRetailers";
    public static String API_GET_TARGETDISTRIBUTORS = API_DOMAIN_V4 + "distributorListforTarget/";
    public static String API_SUBMIT_DISTRIBUTOR_CLOSING = API_DOMAIN_V4 + "submitClosingStock";
    public static String API_GET_DIS_CLOSING_HISTORY = API_DOMAIN_V4 + "getClosingStockHistory";
    //API FOR SENDING DATA TO SERVER
    public static String API_TO_MARK_ATTENDANCE = API_DOMAIN_V4 + "markEmpAttendance";
    public static String API_TO_MARK_ATTENDANCE_NEW = API_DOMAIN_V4 + "markEmpAttendanceNew";
    public static String API_SUBMIT_ORDER = API_DOMAIN_V4 + "submitOrders";
    public static String API_SUBMIT_DISTRIBUTOR_STOCK_INFO = API_DOMAIN_V4 + "submitOpeningStock";
    public static String API_TO_ADD_NEW_RETAILER = API_DOMAIN_V4 + "addRetailer";
    public static String API_TO_ADD_NEW_DISTRIBUTOR = API_DOMAIN_V4 + "newDistributorSearch";
    public static String API_TO_SEND_PATH = API_DOMAIN_V4 + "getEmployeePath";
    public static String API_UPDATE_EMP_INFO = API_DOMAIN_V4 + "updateEmployeeInfo";
    public static String API_UPDATE_RETAILER_INFO = API_DOMAIN_V4 + "updateRetailerInfo";
    public static String API_TO_CANCEL_ORDER = API_DOMAIN_V4 + "cancelOrder";
    public static String API_TO_SUBMIT_FEEDBACK_D = API_DOMAIN_V4 + "distributorFeedback";
    public static String API_UPLOAD_CLAIM_EXP = API_DOMAIN_V4 + "expenseClaim";
    public static String API_JOINT_WORKING_REQUEST = API_DOMAIN_V4 + "jointWorkingCreate";
    public static String API_JOINT_WORKING_UPDATE = API_DOMAIN_V4 + "jointWorkingUpdate";
    public static String API_EMP_BEAT_VISIT = API_DOMAIN_V4 + "employeeBeatVisit";
    public static String API_SUBMIT_DISTRIBUTOR_ORDER = API_DOMAIN_V4 + "submitDistOrders";
    public static String API_CANCEL_DISTRIBUTOR_ORDER = API_DOMAIN_V4 + "cancelDistOrder";
    public static String API_SUBMIT_DB = API_DOMAIN_V4 + "submitDb";
    public static String API_SUBMIT_ERROR = API_DOMAIN_V4 + "submitError";
    public static String API_SUBMIT_OUTSTANDING_FEEDBACK = API_DOMAIN_V4 + "outstanding";
    public static String API_CREATE_PJP = API_DOMAIN_V4 + "beatPlancreate"; //"beatPlan/create" By Umesh 13-March-2022;
    public static String API_SUBMIT_TOMORROWS_PLAN = API_DOMAIN_V4 + "tomorrowPlans";

    //GOOGLE API FOR RECEIVING STATE CITY BY PIN
    //public static String API_TO_GET_CITY_STATE_BY_PINCODE = "http://maps.googleapis.com/maps/api/geocode/json?";
    //public static String API_GET_CITY_STATE_BY_LTLG = "http://maps.googleapis.com/maps/api/geocode/json?";
    public static String API_DISTRIBUTOR_MOBILE_NUMBER_UPDATE = API_DOMAIN_V4 + "updateDistributorInfo";
    public static String API_POST_SAVETARGETDISTRIBUTORS = API_DOMAIN_V4 + "employeeDistMonthlyTarget/create";
    public static String PLACEHOLDER_URL = "https://i2.wp.com/karolinskatrialalliance.se/wp-content/uploads/2017/02/staff-member-2.jpg?ssl=1";
    //Simple Link
    public static String URL_HELP = SBApplication.DOMAIN + "help";
    //image prefix url
    //private static String IMAGE_PATH_DOMAIN = "https://dgeqtkz13qm3j.cloudfront.net/";
    private static String IMAGE_PATH_DOMAIN = "https://rtplstorageaccount.blob.core.windows.net/sfaadmin/"; //Umesh
    public static String IMAGE_PREFIX = IMAGE_PATH_DOMAIN + "employees/thumb/";
    public static String IMAGE_PREFIX2 = IMAGE_PATH_DOMAIN + "employees/orig/";
    public static String IMAGE_PREFIX_RETAILER_THUMB = IMAGE_PATH_DOMAIN + "retailers/thumb/";
    public static String IMAGE_PREFIX_RETAILER_ORIGINAL = IMAGE_PATH_DOMAIN + "retailers/orig/";
    public static String IMAGE_PREFIX_SHOPIMAGES_THUMB = IMAGE_PATH_DOMAIN + "shopImages/thumb/";
    public static String IMAGE_PREFIX_SHOPIMAGES_ORIGINAL = IMAGE_PATH_DOMAIN + "shopImages/orig/";

    public final static String ACTIVITY1 = "Retailing";
    public final static String ACTIVITY2 = "Joint working";
    public final static String ACTIVITY3 = "Meeting";
    public final static String ACTIVITY4 = "New distributor search";
    public final static String ACTIVITY5 = "Travelling";
    public final static String ACTIVITY6 = "Payment collection";
    public final static String ACTIVITY7 = "Market/Promotion";
    public final static String ACTIVITY8 = "Van sales";
    public final static String ACTIVITY9 = "Others";
    public final static String ACTIVITY12 = "Retailing & Joint working";
    public final static String ACTIVITY13 = "Retailing & Meeting";
    public final static String ACTIVITY14 = "Retailing & New distributor search";
    public final static String ACTIVITY15 = "Retailing & Travelling";
    public final static String ACTIVITY16 = "Retailing & Payment collection";
    public final static String ACTIVITY17 = "Retailing & Market/Promotion";
    public final static String ACTIVITY18 = "Retailing & Van sales";
    public final static String ACTIVITY19 = "Retailing & Others";
    public final static String ACTIVITY21 = "Joint working & Retailing";
    public final static String ACTIVITY23 = "Joint working & Meeting";
    public final static String ACTIVITY24 = "Joint working & New distributor search";
    public final static String ACTIVITY25 = "Joint working & Travelling";
    public final static String ACTIVITY26 = "Joint working & Payment collection";
    public final static String ACTIVITY27 = "Joint working & Market/Promotion";
    public final static String ACTIVITY28 = "Joint working & Van sales";
    public final static String ACTIVITY29 = "Joint working & Others";
    public final static String ACTIVITY31 = "Meeting & Retailing";
    public final static String ACTIVITY32 = "Meeting & Joint working";
    public final static String ACTIVITY34 = "Meeting & New distributor search";
    public final static String ACTIVITY35 = "Meeting & Travelling";
    public final static String ACTIVITY36 = "Meeting & Payment collection";
    public final static String ACTIVITY37 = "Meeting & Market/Promotion";
    public final static String ACTIVITY38 = "Meeting & Van sales";
    public final static String ACTIVITY39 = "Meeting & Others";
    public final static String ACTIVITY41 = "New distributor search & Retailing";
    public final static String ACTIVITY42 = "New distributor search & Joint working";
    public final static String ACTIVITY43 = "New distributor search & Meeting";
    public final static String ACTIVITY45 = "New distributor search & Travelling";
    public final static String ACTIVITY46 = "New distributor search & Payment collection";
    public final static String ACTIVITY47 = "New distributor search & Market/Promotion";
    public final static String ACTIVITY48 = "New distributor search & Van sales";
    public final static String ACTIVITY49 = "New distributor search & Others";
    public final static String ACTIVITY51 = "Travelling & Retailing";
    public final static String ACTIVITY52 = "Travelling & Joint working";
    public final static String ACTIVITY53 = "Travelling & Meeting";
    public final static String ACTIVITY54 = "Travelling & New distributor search";
    public final static String ACTIVITY56 = "Travelling & Payment collection";
    public final static String ACTIVITY57 = "Travelling & Market/Promotion";
    public final static String ACTIVITY58 = "Travelling & Van sales";
    public final static String ACTIVITY59 = "Travelling & Others";
    public final static String ACTIVITY61 = "Payment collection & Retailing";
    public final static String ACTIVITY62 = "Payment collection & Joint working";
    public final static String ACTIVITY63 = "Payment collection & Meeting";
    public final static String ACTIVITY64 = "Payment collection & New distributor search";
    public final static String ACTIVITY65 = "Payment collection & Travelling";
    public final static String ACTIVITY67 = "Payment collection & Market/Promotion";
    public final static String ACTIVITY68 = "Payment collection & Van sales";
    public final static String ACTIVITY69 = "Payment collection & Others";
    public final static String ACTIVITY71 = "Market/Promotion & Retailing";
    public final static String ACTIVITY72 = "Market/Promotion & Joint working";
    public final static String ACTIVITY73 = "Market/Promotion & Meeting";
    public final static String ACTIVITY74 = "Market/Promotion & New distributor search";
    public final static String ACTIVITY75 = "Market/Promotion & Travelling";
    public final static String ACTIVITY76 = "Market/Promotion & Payment collection";
    public final static String ACTIVITY78 = "Market/Promotion & Van sales";
    public final static String ACTIVITY79 = "Market/Promotion & Others";
    public final static String ACTIVITY81 = "Van sales & Retailing";
    public final static String ACTIVITY82 = "Van sales & Joint working";
    public final static String ACTIVITY83 = "Van sales & Meeting";
    public final static String ACTIVITY84 = "Van sales & New distributor search";
    public final static String ACTIVITY85 = "Van sales & Travelling";
    public final static String ACTIVITY86 = "Van sales & Payment collection";
    public final static String ACTIVITY87 = "Van sales & Market/Promotion";
    public final static String ACTIVITY89 = "Van sales & Others";
    public final static String ACTIVITY91 = "Others & Retailing";
    public final static String ACTIVITY92 = "Others & Joint working";
    public final static String ACTIVITY93 = "Others & Meeting";
    public final static String ACTIVITY94 = "Others & New distributor search";
    public final static String ACTIVITY95 = "Others & Travelling";
    public final static String ACTIVITY96 = "Others & Payment collection";
    public final static String ACTIVITY97 = "Others & Market/Promotion";
    public final static String ACTIVITY98 = "Others & Van sales";
    public static boolean STOP_SYNC = false;
    //    public final static String ACTIVITY1 = "Retailing";
//    public final static String ACTIVITY2 = "Joint working";
//    public final static String ACTIVITY3 = "Meeting";
//    public final static String ACTIVITY4 = "New distributor search";
//    public final static String ACTIVITY5 = "Travelling";
//    public final static String ACTIVITY6 = "Payment collection";
//    public final static String ACTIVITY7 = "Market/Promotion";
//    public final static String ACTIVITY8 = "Van sales";
//    public final static String ACTIVITY9 = "Others";
    public static boolean isAppAlive = false;
    public static String API_GET_MAPPING_DETAILS2 = API_DOMAIN_V4 + "getTownDataByZone?";
    public static String API_GET_DISTRIBUTORS_3 = API_DOMAIN_V4 + "getDistributorsByZone?";


}

