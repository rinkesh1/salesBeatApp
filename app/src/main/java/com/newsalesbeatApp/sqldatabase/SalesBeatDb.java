package com.newsalesbeatApp.sqldatabase;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Created by MTC on 08-08-2017.
 */

public class SalesBeatDb extends SQLiteOpenHelper {

    public static String KEY_SERVER_SUBMIT_STATUS = "server_status";
    public static String KEY_RECORD_DATE = "record_date";
    public static String KEY_LAST_UPDATED_AT = "last_updated";
    public static String TRANSACTION_ID = "transactionId";
    public static String KEY_RETAILER_NAME = "retailer_name";
    public static String KEY_STATUS_CODE = "status_code";
    public static String KEY_ERROR_MSG = "error_msg";
    //User detail Table Columns names
    public static String KEY_EMP_ID = "emp_id";
    //User detail Table Columns names
    public static String KEY_SALE_ACH = "sale_ach";
    public static String KEY_SALE_TARGET = "saleTarget";
    public static String KEY_ATTENDANCE_STATUS = "attendance";
    public static String KEY_DATE = "date";
    //Town list table columns name
    public static String KEY_DID = "did";
    public static String KEY_BID = "bid";
    //Town list table columns name
    public static String KEY_SID = "sid";
    //Distributor list table columns name
    public static String KEY_DISTRIBUTOR_ID = "distributor_id";
    public static String KEY_DISTRIBUTOR_GST = "gstin";
    public static String KEY_SKU_STOCK_TYPE = "stock_type";
    public static String KEY_DISTRIBUTOR_ORDE_TYPE = "dis_order_type";
    public static String KEY_OWNER_NAME = "owner_name";
    public static String KEY_BEAT_ID = "beat_id";
    public static String KEY_OWNER_PHONE = "owner_phone";
    public static String KEY_RETAILER_LAT = "retailer_latitude";
    public static String KEY_RETAILER_LONG = "retailer_longtitude";
    public static String KEY_RETAILER_DISTANCE = "retailer_distance";
    public static String KEY_DISTRICT = "district";
    public static String KEY_LOCALITY = "locality";
    public static String KEY_NEW_PREFERRED_RETAILER_TEMP_ID = "tempnrid";
    public static String KEY_NEW_PREFERRED_RETAILER_ID = "prid";
    //Table retailers list columns name
    public static String KEY_NEW_RETAILER_TEMP_IDD = "tempnrid";
    public static String KEY_NEW_PREFERRED_RETAILER_FIRM_NAME = "new_preferred_retailer_firm_name";
    //Table retailers list columns name
    public static String KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME1 = "new_preferred_retailer_firm_contact_name1";
    public static String KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME2 = "new_preferred_retailer_firm_contact_name2";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_NAME = "new_preferred_retailer_distributor_name";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME1 = "new_preferred_retailer_contact_person_name1";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME2 = "new_preferred_retailer_contact_person_name2";
    public static String KEY_NEW_PREFERRED_RETAILER_LATITUDE = "new_preferred_retailer_latitude";
    public static String KEY_NEW_PREFERRED_RETAILER_LONGITUDE = "new_preferred_retailer_longitude";
    public static String KEY_NEW_PREFERRED_RETAILER_BLOCK = "new_preferred_retailer_block";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRICT = "new_preferred_retailer_district";
    public static String KEY_NEW_PREFERRED_RETAILER_PROPOSE_CATEGORY = "new_preferred_retailer_proposed_category";
    public static String KEY_NEW_PREFERRED_RETAILER_MONTHLY_TURNOVER = "new_preferred_retailer_monthly_turnover";
    public static String KEY_NEW_PREFERRED_RETAILER_PER_MONTH_BUSINESS = "new_preferred_retailer_per_month_business";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRIBUTION_BRAND_NAME = "new_preferred_retailer_distribution_brand_name";
    public static String KEY_NEW_PREFERRED_RETAILER_INTERNAL_STAFF = "new_preferred_retailer_internal_staff";
    public static String KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_WALK_IN = "new_preferred_retailer_avg_per_day_walk_in";
    public static String KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_CUSTOMER_INCOME = "new_preferred_retailer_avg_per_day_walk_customer_income";
    public static String KEY_NEW_PREFERRED_RETAILER_OTHER_BUSINESS = "new_preferred_retailer_other_business";
    public static String KEY_NEW_PREFERRED_RETAILER_SIDE_PANEL_SIZE = "new_preferred_retailer_side_panel_size";
    public static String KEY_NEW_PREFERRED_RETAILER_COUNTER_SIZE = "new_preferred_retailer_counter_size";
    public static String KEY_NEW_PREFERRED_RETAILER_FRONT_BOARD_SIZE = "new_preferred_retailer_front_board_size";
    public static String KEY_NEW_PREFERRED_RETAILER_ROOF_HEIGHT = "new_preferred_retailer_roof_height";
    public static String KEY_NEW_PREFERRED_RETAILER_FASCIA_WIDTH = "new_preferred_retailer_fascia_width";
    public static String KEY_NEW_PREFERRED_RETAILER_FIXTURE_TYPE = "new_preferred_retailer_fixture_type";
    public static String KEY_NEW_PREFERRED_RETAILER_FRONT_SPACE = "new_preferred_retailer_front_space";
    public static String KEY_NEW_PREFERRED_RETAILER_TOTAL_SHELF = "new_preferred_retailer_total_shelf";
    public static String KEY_NEW_PREFERRED_RETAILER_SPACE_PROVIDED = "new_preferred_retailer_space_provided";
    public static String KEY_NEW_PREFERRED_RETAILER_FRONT_COUNTER_MEASUREMENT = "new_preferred_retailer_counter_measurement";
    public static String KEY_NEW_PREFERRED_RETAILER_BRAND_POSTING = "new_preferred_retailer_brand_posting";
    public static String KEY_NEW_PREFERRED_RETAILER_ROAD_WIDTH = "new_preferred_retailer_road_width";
    public static String KEY_NEW_PREFERRED_RETAILER_FLOOR_TYPE = "new_preferred_retailer_floor_type";
    public static String KEY_NEW_PREFERRED_RETAILER_SHOP_PATH = "new_preferred_retailer_shop_path";
    public static String KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID = "p_did";
    public static String KEY_NEW_PREFERRED_RETAILER_BEAT_ID = "new_preferred_retailer_beat_id";
    public static String KEY_NEW_PREFERRED_RETAILER_NEW_ORDER_DATE = "new_preferred_retailer_date_date";
    public static String KEY_NEW_PREFERRED_RETAILER_TRANSACTION_ID = "new_preferred_retailer_transaction_id";
    //Table NEW ORDER_PLACED_BY columns name
    public static String KEY_PREFERRED_ORDER_PLACED_BY_RID = "prid";
    public static String KEY_PREFERRED_ORDER_PLACED_BY_DID = "p_did";
    public static String KEY_PREFERRED_ORDER_PLACED_TIME = "p_taken_at";
    public static String KEY_DATE_A = "date";
    public static String KEY_WALK_LAT1 = "walk_lat1";
    public static String KEY_WALK_LONGT1 = "walk_longt1";
    public static String KEY_ACCURACY = "accuracy";
    public static String KEY_PREFERRED_ORDER_TYPE = "p_order_type";
    public static String KEY_PREFERRED_ORDER_CHECK_IN = "p_check_in_time";
    public static String KEY_PREFERRED_ORDER_CHECK_OUT = "p_check_out_time";
    public static String KEY_PREFERRED_ORDER_LAT = "p_order_lat";
    public static String KEY_PREFERRED_ORDER_LONG = "p_order_long";
    public static String KEY_PREFERRED_ORDER_COMMENT = "p_order_comment";
    public static String KEY_PREFERRED_ORDER_PLACED_BY_DATE = "p_order_placed_by_date";
    public static String KEY_PREFERRED_ORDER_STATUS = "p_order_status";
    public static String KEY_OTHER_ACTIVITY_REMARKS = "remarks";
    //columns of chat history table
    public static String KEY_ACTIVITY = "activity";
    public static String KEY_TOWN = "town";
    public static String KEY_DISTRIBUTOR = "distributor";
    public static String KEY_BEAT = "beat";
    public static String KEY_EMP = "emp";
    public static String KEY_TC_PJP = "tc";
    public static String KEY_PC_PJP = "pc";
    public static String KEY_SALE_PJP = "sale";
    public static String KEY_PJP_DATE = "pjp_date";
    //testing
    static String FILE_DIR = "Android";
    // Database Name
    private static String DATABASE_NAME = "SalesBeat";
    // Database Version
    private static int DATABASE_VERSION = 67;
    private static String TABLE_PREFERRED_ORDER_ENTRY_LIST = "preferred_sku_order_list";
    /*--------------Table Preferred Retailers List------------------*/
    private static String TABLE_NEW_PREFERRED_RETAILERS_LIST = "new_preferred_retailers_list";
    /*Table NEW ORDER_PLACED_BY*/
    private static String TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS = "order_placed_by_preferred_retailers";
    private static SalesBeatDb instance;
    private SharedPreferences prefSFA, tempPref;
    /*------------- InApp Notification table name----------------*/
    private String TABLE_INAPPNOTIFICATION = "inappnotifytable";
    //User detail Table Columns names
    private String KEY_INAPPNOT_ID = "inappnoti_id";
    private String KEY_INAPPNOT_TITLE = "inappnoti_name";
    private String KEY_INAPPNOT_BODY = "inappnoti_photo";
    private String KEY_INAPP_PIC = "inappnoti_pic";
    private String KEY_INAPP_DATE = "inappnoti_date";
    private String KEY_STATUS_NOTIF = "read_status";
    /*------------- Offline date table name----------------*/
    private String TABLE_DATE_OFFLINE_DESCRIPT = "offilinetabledesc";
    //User detail Table Columns names
    private String KEY_OFFLINE_ID = "date_offline_id";
    private String KEY_DATES = "date_offline_date";
    private String KEY_OFFLINE_JSON = "date_json";
    private String KEY_EXTRACOLUMN = "date_extra_column";
    /*------------- User detail table name----------------*/
    private String TABLE_LEADERBOARD = "leaderboard";
    private String KEY_EMP_NAME = "emp_name";
    private String KEY_EMP_PIC_URL = "emp_photo";
    private String KEY_TC = "tc";
    private String KEY_PC = "pc";
    private String KEY_SALES = "sales";
    /*------------- User detail table name----------------*/
    private String TABLE_EMP_PRIMARY_SALE = "emp_primary_sale";
    private String TABLE_EMP_SECONDARY_SALE = "emp_secondary_sale";
    /*------------- User detail table name----------------*/
    private String TABLE_EMP_KRA_DETAILS = "emp_kra_details";
    //User detail Table Columns names
    private String KEY_KRA_TC_ACHIEVEMENT = "tcAchievement";
    private String KEY_KRA_PC_ACHIEVEMENT = "pcAchievement";
    private String KEY_KRA_TC_TARGET = "tcTarget";
    private String KEY_KRA_PC_TARGET = "pcTarget";
    /*------------- User detail table name----------------*/
    private String TABLE_CAMPAIGN = "campaign";
    //User detail Table Columns names
    private String KEY_CAMPAIGN_ID = "campaign_id";
    private String KEY_CAMPAIGN_IMG = "campaign_img";
    private String KEY_CAMPAIGN_CONTENT = "campaign_content";
    /*------------- User detail table name----------------*/
    private String TABLE_DOCS = "docs";
    //User detail Table Columns names
    private String KEY_DOCS_ID = "docs_id";
    private String KEY_DOCS_IMG = "docs_img";
    private String KEY_DOCS_CONTENT = "docs_content";
    /*----------- User attendance report table name---------------*/
    private String TABLE_USER_ATTENDANCE = "user_attendance_report";
    // User attendance report Table Columns names
    private String KEY_ID_ATTENDANCE = "id_attendance";
    private String KEY_CHECK_IN_T = "check_in_time";
    private String KEY_CHECK_OUT_T = "check_out_time";
    private String KEY_TOTAL_CALL = "total_call ";
    private String KEY_PRODUCTIVE_CALL = "productive_call ";
    private String KEY_LINE_SOLD = "line_sold ";
    private String KEY_TOTAL_WORKING_TIME = "total_working_time ";
    private String KEY_TOTLA_RETAILING_TIME = "total_retailing_time ";
    private String KEY_REASON = "reason";
    private String KEY_MONTH = "month";
    private String KEY_YEAR = "year";
    /*-----------------Town list table--------------*/
    private String TABLE_DISBEAT_MAP = "disbeatmap";
    /*-----------------Town list table--------------*/
    private String TABLE_DISSKU_MAP = "disskumap";
    /*-----------------Town list table--------------*/
    private String TABLE_TOWN_LIST = "townlist";
    //Town list table columns name
    private String KEY_TOWN_ID = "town_id";
    private String KEY_TOWN_NAME = "town_name";
    /*--------------Distributor list Table--------------*/
    private String TABLE_DISTRIBUTOR_LIST = "distributor_list";
    private String KEY_DISTRIBUTOR_NAME = "distributor_name";
    private String KEY_DISTRIBUTOR_PHONE = "phone";
    private String KEY_DISTRIBUTOR_EMAIL = "email";
    private String KEY_DISTRIBUTOR_TYPE = "type";
    private String KEY_DISTRIBUTOR_ADDRESS = "address";
    private String KEY_DISTRIBUTOR_DISTRICT = "district";
    private String KEY_DISTRIBUTOR_ZONE = "zone";
    private String KEY_DISTRIBUTOR_STATE = "state";
    private String KEY_DISTRIBUTOR_PINCODE = "pincode";
    /*-----------------------Table SKU DETAILS-------------*/
    private String TABLE_SKU_DETAILS = "sku_details";
    //table sku details columns name
    private String KEY_SKU_ID = "sku_id";
    private String KEY_SKU_NAME = "sku_name";
    private String KEY_SKU_BRAND_NAME = "brand_name";
    private String KEY_SKU_BRAND_PRICE = "brand_price";
    private String KEY_SKU_BRAND_WEIGHT = "brand_weight";
    private String KEY_SKU_BRAND_UNIT = "brand_unit";
    private String KEY_SKU_CONVERSION_FACTOR = "conversion_factor";
    private String KEY_SKU_IMAGE = "sku_image";
    /*-----------------------Table SKU ENTRY LIST-------------*/
    private String TABLE_SKU_ENTRY_LIST = "sku_entry_list";
    private String TABLE_SKU_CLOSING_ENTRY_LIST = "sku_closing_entry_list";
    //table sku details columns name
    private String KEY_SKU_ID_L = "sku_id";
    private String KEY_SKU_NAME_L = "sku_name";
    private String KEY_SKU_BRAND_NAME_L = "brand_name";
    private String KEY_SKU_BRAND_PRICE_L = "brand_price";
    private String KEY_SKU_BRAND_QTY_L = "brand_qty";
    private String KEY_SKU_BRAND_UNIT_L = "brand_unit";
    private String KEY_SKU_ENTRY_TIME = "time_at";
    private String KEY_SKU_ENTRY_TYPE = "entry_type";
    private String KEY_SKU_ENTRY_DATE = "sku_entry_date";
    private String KEY_SKU_ENTRY_LAT = "sku_entry_lat";
    private String KEY_SKU_ENTRY_LONGT = "sku_entry_longt";
    /*-----------------------Table SKU ENTRY LIST-------------*/
    private String TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST = "distributor_order_entry_list";
    //table sku details columns name
    private String KEY_DISTRIBUTOR_ORDER_ID_L = "sku_id";
    private String KEY_DISTRIBUTOR_ORDER_NAME_L = "sku_name";
    private String KEY_DISTRIBUTOR_ORDER_BRAND_NAME_L = "brand_name";
    private String KEY_DISTRIBUTOR_ORDER_BRAND_PRICE_L = "brand_price";
    private String KEY_DISTRIBUTOR_ORDER_L = "distributor_order";
    private String KEY_DISTRIBUTOR_ORDER_BRAND_UNIT_L = "brand_unit";
    private String KEY_DISTRIBUTOR_ORDER_ENTRY_TIME = "time_at";
    private String KEY_DISTRIBUTOR_ORDER_ENTRY_DATE = "sku_entry_date";
    private String KEY_DISTRIBUTOR_ORDER_ENTRY_LAT = "sku_entry_lat";
    private String KEY_DISTRIBUTOR_ORDER_ENTRY_LONGT = "sku_entry_longt";
    /*-----------------------Table ORDER ENTRY LIST-------------*/
    private String TABLE_ORDER_ENTRY_LIST = "sku_order_list";
    //table sku details columns name
    private String KEY_ORDER_ID_L = "order_id_l";
    private String KEY_ORDER_SKU_ID_L = "sku_id";
    private String KEY_ORDER_SKU_NAME_L = "sku_name";
    private String KEY_ORDER_SKU_BRAND_NAME_L = "brand_name";
    private String KEY_ORDER_SKU_BRAND_PRICE_L = "brand_price";
    private String KEY_ORDER_SKU_BRAND_QTY_L = "brand_qty";
    public static String KEY_ORDER_SKU_BRAND_UNIT_L = "brand_unit";
    private String KEY_ORDER_SKU_ENTRY_DATE_L = "order_sku_entry_date";
    /*-----------------------Table NEW ORDER ENTRY LIST-------------*/
    private String TABLE_NEW_ORDER_ENTRY_LIST = "new_sku_order_list";
    //table sku details columns name
    private String KEY_NEW_ORDER_SKU_ID_L = "new_sku_id";
    private String KEY_NEW_ORDER_SKU_NAME_L = "new_sku_name";
    private String KEY_NEW_ORDER_SKU_BRAND_NAME_L = "new_brand_name";
    private String KEY_NEW_ORDER_SKU_BRAND_PRICE_L = "new_brand_price";
    private String KEY_NEW_ORDER_SKU_BRAND_QTY_L = "new_brand_qty";
    public static String KEY_NEW_ORDER_SKU_BRAND_UNIT_L = "new_brand_unit";
    private String KEY_NEW_RETAILER_ID = "nrid";
    private String KEY_NEW_ORDER_ENTRY_DATE = "new_order_entry_date";
    /*-----------------------Table SKU ID-----------------*/
    private String TABLE_SKU_ID = "sku_id_table";
    //table sku id columns name
    private String KEY_SKU_ID_S = "sku_id";
    private String KEY_DISTRIBUTOR_ID_S = "d_id";
    /*-----------------Table Beat List-----------------*/
    private String TABLE_BEAT_LIST = "beat_list";
    private String TABLE_BEAT_VISITED_LIST = "beat_visited_list";
    //table beat list columns name
    private String KEY_B_ID = "b_id";
    private String KEY_BEAT_NAME = "beat_name";
    private String KEY_BEAT_VISITED = "beat_visited";
    private String KEY_BEAT_VISITED_LAT = "beat_visited_lat";
    private String KEY_BEAT_VISITED_LONGT = "beat_visited_longt";
    private String KEY_BEAT_VISITED_TIME = "beat_visited_time";
    private String KEY_BEAT_RANGE = "beat_range";
    /*--------------Table Retailers List------------------*/
    private String TABLE_RETAILERS_LIST = "retailers_list";
    //Table retailers list columns name
    private String KEY_RETAILER_ID = "retailer_id";
    private String KEY_BEAT_ID_R = "beat_id_r";
    private String KEY_RETAILER_UNIQUE_ID = "ruid_id";
    private String KEY_RETAILER_ADDRESS = "retailer_address";
    private String KEY_RETAILER_STATE = "retailer_state";
    private String KEY_RETAILER_EMAIL = "retailer_email";
    private String KEY_SHOP_PHONE = "shop_phone";
    private String KEY_WHATSAPP_NO = "whatsapp_no";
    private String KEY_RETAILER_GSTIN = "retailer_gstin";
    private String KEY_RETAILER_PIN = "retailer_pin";
    private String KEY_RETAILER_FSSAI = "retailer_fssai";
    private String KEY_ZONE = "zone";
    private String KEY_TARGET = "target";
    private String KEY_OUTLET_CHANNEL = "outletchannel";
    private String KEY_SHOP_TYPE = "shop_type";
    private String KEY_RETAILER_GRADE = "retailer_grade";
    private String KEY_RETAILER_IMAGE = "retailer_image";
    /*--------------Table Retailers List------------------*/
    private String TABLE_NEW_RETAILERS_LIST = "new_retailers_list";
    private String KEY_NEW_RETAILER_IDD = "nrid";
    private String KEY_NEW_RETAILER_NAME = "new_retailer_name";
    private String KEY_NEW_RETAILER_ADDRESS = "new_retailer_address";
    private String KEY_NEW_RETAILER_STATE = "new_retailer_state";
    private String KEY_NEW_RETAILER_EMAIL = "new_retailer_email";
    private String KEY_NEW_SHOP_PHONE = "new_shop_phone";
    private String KEY_NEW_OWNER_NAME = "new_owner_name";
    private String KEY_NEW_OWNER_PHONE = "new_owner_phone";
    private String KEY_NEW_WHATSAPP_NO = "new_whatsapp_no";
    private String KEY_NEW_RETAILER_LAT = "new_retailer_latitude";
    private String KEY_NEW_RETAILER_LONG = "new_retailer_longtitude";
    private String KEY_NEW_RETAILER_GSTIN = "new_retailer_gstin";
    private String KEY_NEW_RETAILER_PIN = "new_retailer_pin";
    private String KEY_NEW_RETAILER_FSSAI = "new_retailer_fssai";
    private String KEY_NEW_DISTRICT = "new_district";
    private String KEY_NEW_LOCALITY = "new_locality";
    private String KEY_NEW_ZONE = "new_zone";
    private String KEY_NEW_TARGET = "new_target";
    private String KEY_NEW_OUTLET_CHANNEL = "new_outletchannel";
    private String KEY_NEW_SHOP_TYPE = "new_shop_type";
    private String KEY_NEW_RETAILER_GRADE = "new_retailer_grade";
    private String KEY_NEW_OWNER_IMAGE = "new_owner_image";
    private String KEY_IMAGE_TIME_STAMP = "image_time_stamp";
    private String KEY_NEW_SHOP_IMAGE1 = "new_shop_image1";
    private String KEY_NEW_SHOP_IMAGE2 = "new_shop_image2";
    private String KEY_NEW_SHOP_IMAGE3 = "new_shop_image3";
    private String KEY_NEW_SHOP_IMAGE4 = "new_shop_image4";
    private String KEY_NEW_SHOP_IMAGE5 = "new_shop_image5";
    private String KEY_NEW_ORDER_DATE = "new_order_date";
    /*Table Activity Tracking*/
    private String TABLE_ACTIVITY_TRACKING = "activity_tracking";
    //Table activity tracking columns name
    private String KEY_TEMP_ID = "temp_id";
    /*Table Message List*/
    private String TABLE_MESSAGE_LIST = "message_list";
    //Table Message List columns name
    private String KEY_TIME_STAMP = "time_stamp";
    private String KEY_MESSAGE = "message";
    /*Table ORDER_PLACED_BY RETAILERS*/
    private String TABLE_ORDER_PLACED_BY_RETAILERS = "order_placed_by_retailers";
    //Table ORDER_PLACED_BY columns name
    private final String KEY_ORDER_PLACED_BY_RID = "rid";
    private final String KEY_ORDER_PLACED_BY_DID = "did";
    private final String KEY_ORDER_PLACED_TIME = "taken_at";
    private final String KEY_ORDER_TYPE = "order_type";
    private String KEY_ORDER_CHECK_IN = "check_in_time";
    private String KEY_ORDER_CHECK_OUT = "check_out_time";
    private String KEY_BRAND_KG = "brand_kg";
    private String KEY_BRAND_UNIT = "brand_unit";
    private String KEY_ORDER_LAT = "order_lat";
    private String KEY_ORDER_LONG = "order_long";
    private String KEY_ORDER_STATUS = "order_status";
    public static String KEY_ORDER_DATE = "order_date";
    private String KEY_ORDER_COMMENT = "order_comment";
    /*Table NEW ORDER_PLACED_BY*/
    private String TABLE_ORDER_PLACED_BY_NEW_RETAILERS = "order_placed_by_new_retailers";
    //Table NEW ORDER_PLACED_BY columns name
    private String KEY_NEW_ORDER_PLACED_BY_RID = "nrid";
    private String KEY_NEW_ORDER_PLACED_TIME = "new_taken_at";
    private String KEY_NEW_ORDER_TYPE = "new_order_type";
    private String KEY_NEW_ORDER_CHECK_IN = "new_check_in_time";
    private String KEY_NEW_ORDER_CHECK_OUT = "new_check_out_time";
    private String KEY_NEW_ORDER_LAT = "new_order_lat";
    private String KEY_NEW_ORDER_LONG = "new_order_long";
    private String KEY_NEW_ORDER_COMMENT = "new_order_comment";
    private String KEY_NEW_ORDER_PLACED_BY_DATE = "new_order_placed_by_date";
    private String KEY_NEW_ORDER_STATUS = "new_order_status";
    public static String KEY_NEW_ORDER_PLACED_BY_DID = "distributor_id";
    /*Table NEW DISTRIBUTOR*/
    private String TABLE_NEW_DISTRIBUTOR = "new_distributor";
    //Table NEW DISTRIBUTOR columns name
    private String KEY_NEW_DISTRIBUTOR_ID = "new_distributor_id";
    private String KEY_NAME_OF_FIRM = "name_of_firm";
    private String KEY_FIRM_ADDRESS = "firm_address";
    private String KEY_BRAND_NAME = "brand_name";
    private String KEY_OTHER_BRAND_NAME = "other_brand";
    private String KEY_PINCODE = "pincode";
    private String KEY_CITY = "city";
    private String KEY_STATE = "state";
    private String KEY_OWNER_NAME_D = "owner_name";
    private String KEY_OWNER_MOBILE_NO1 = "owner_mobile_no1";
    private String KEY_OWNER_MOBILE_NO2 = "owner_mobile_no2";
    private String KEY_EMAIL_ID = "email_id";
    private String KEY_GSTIN = "gstin";
    private String KEY_FSSAI_NO = "fssai_no";
    private String KEY_PAN_NO = "pan_no";
    private String KEY_MONTHLY_TURNOVER = "monthly_turnover";
    private String KEY_OWNER_IMAGE = "owner_image";
    private String KEY_OWNER_IMAGE_TIME_STAMP = "owner_image_time_stamp";
    private String KEY_OWNER_IMAGE_LATLONG = "owner_image_LatLong";
    private String KEY_TOTAL_NO_EMP = "total_no_of_emp";
    private String KEY_SALES_PERSON = "sales_person";
    private String KEY_ADMIN_PERSON = "admin_person";
    private String KEY_DELIVERY_VEHICLE = "delivery_vehicle";
    private String KEY_NO_OF_VEHICLE = "no_of_vehicle";
    private String KEY_BEAT_NAME_D = "beat_name";
    private String KEY_NO_OF_SHOP_IN_BEAT = "no_of_shop_in_beat";
    private String KEY_NO_OF_SHOP_COVERED = "no_of_shop_covered";
    private String KEY_NO_OF_GUMTI_COVERED = "no_of_gumti_covered";
    private String KEY_TOTAL_NO_OF_SHOP = "total_no_of_shop";
    private String KEY_NO_OF_WHOLESALER = "no_of_wholesaler";
    private String KEY_INVESTMENT_PLAN = "investment_plan";
    private String KEY_PRODUCT_DIVISION = "product_division";
    private String KEY_MONTHLY_SALE_ESTIMATE = "monthly_sale_estimate";
    private String KEY_DISTRIBUTOR_TYPE_D = "distributor_type";
    private String KEY_DISTRIBUTOR_PARENT_NAME = "sub_distributor_parent_name";
    private String KEY_WORKING_BRAND = "working_brand";
    private String KEY_WORKING_SINCE = "working_since";
    private String KEY_OTHER_CONTACT_PERSON_NAME = "other_contact_person_name";
    private String KEY_OTHER_CONTACT_PERSON_PHN = "other_contact_person_phn";
    private String KEY_FIRM_IMAGE = "firm_image";
    private String KEY_FIRM_IMAGE_TIME_STAMP = "firm_image_time_stamp";
    private String KEY_FIRM_IMAGE_LATLONG = "firm_image_LatLong";
    private String KEY_OPINION_ABOUT_DISTRIBUTOR = "opinion_about_distributor";
    private String KEY_COMMENT = "comment";
    private String TABLE_DISTRIBUTOR_ORDER_ENTRY = "distributor_order_entry";
    private String KEY_DISTRIBUTOR_ID_O = "did_o";
    private String KEY_DISTRIBUTOR_ORDER_TAKEN_O = "taken_at_o";
    private String KEY_DISTRIBUTOR_ORDER_STATUS_O = "status_o";
    private String KEY_DISTRIBUTOR_ORDER_DATE_O = "date_o";
    private String KEY_DISTRIBUTOR_ORDER_TYPE = "dis_order_type";
    private String KEY_DISTRIBUTOR_ORDER_LAT_O = "lat_o";
    private String KEY_DISTRIBUTOR_ORDER_LONGT_O = "longt_o";
    private String KEY_DISTRIBUTOR_RECORDING = "recording";
    //Table chat history
    private String TABLE_CHAT_HISTORY = "chat_history";
    //columns of chat history table
    private String KEY_CHAT_ID = "chat_id";
    private String KEY_MESSAGE_CHAT = "message";
    private String KEY_DATE_CHAT = "date";
    private String KEY_CONTACT_CHAT = "contact";
    private String KEY_CHANNEL_CHAT = "channel";
    private String KEY_TIME_STAMP_CHAT = "time_stamp";
    private String KEY_IMAGE_CHAT = "image";
    private String KEY_VIDEO_CHAT = "video";
    private String KEY_AUDIO_CHAT = "audio";
    private String KEY_DOCS_CHAT = "docs";
    private String KEY_LOCATION_CHAT = "loc";
    private String KEY_FEEDBACK_BY = "feedbackby";
    //Table chat history
    private String TABLE_PRIMARY_SALE_HISTORY = "primary_sale_history";
    //columns of chat history table
    private String KEY_DISTRIBUTOR_ID_P = "distributor_id";
    private String KEY_DISTRIBUTOR_SALE_TARGET = "dis_sale_target";
    private String KEY_DISTRIBUTOR_SALE_ACH = "dis_sale_ach";
    private String KEY_DISTRIBUTOR_NAME_P = "dis_name";
    private String KEY_SALE_DATE = "sale_date";
    //Table chat history
    private String TABLE_OTHER_ACTIVITY = "other_activity";
    //columns of chat history table
    private String KEY_OTHER_ACTIVITY_ID = "activity_id";
    private String KEY_OTHER_ACTIVITY = "activity";
    private String KEY_OTHER_ACTIVITY_LAT = "other_lat";
    private String KEY_OTHER_ACTIVITY_LONGT = "other_longt";
    private String KEY_OTHER_ACTIVITY_DATE = "activity_date";
    //Table chat history
    private String TABLE_DISTRIBUTOR_TARGET_ACH = "distarach";
    //columns of chat history table
    private String KEY_DISTRIBUTOR_TARACH_ID = "dis_id";
    private String KEY_DISTRIBUTOR_TARGET = "dis_tar";
    private String KEY_DISTRIBUTOR_ACHIEVEMENT = "dis_ach";
    //Table Cretae Pjp
    private String TABLE_CREATE_PJP = "create_pjp";
    private Context context;

    private final String TABLE_PENDING_ORDERS = "pending_orders";
    private final String TABLE_PENDING_ORDER_PLACED_BY = "pending_orders_placed_by";
    private final String TABLE_EMP_RECORD = "employee_record";
    public static String KEY_EID = "eid";
    public static String KEY_CID = "cid";
    //private String KEY_EMP_NAME = "new_taken_at";
    public static String KEY_USERNAME = "username";
    public static String KEY_PASSWORD = "password";
    public static String KEY_EMP_PHONE = "phone";
    public static String KEY_EMP_MAIL = "mail";
    public static String KEY_HEADQUARTER = "headquarter";
    //private String KEY_ZONE = "zone";
    public static String KEY_ZONE_ID = "zone_id";
    public static String KEY_DESGNATION = "designation";
    public static String KEY_REPORTING_TO = "report_to";
    public static  String KEY_PROFILE_PIC_URL = "profile_pic_url";
    public static String KEY_WORKING_TOWN = "working_town";
    public static String KEY_WORKING_DISTRIBUTOR = "working_distributor";
    public static String KEY_WORKING_BEAT = "working_beat";
    public static String KEY_TOKEN = "token";


    private SalesBeatDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Testing
//        super(context, Environment.getExternalStorageDirectory()
//                        + File.separator + FILE_DIR + File.separator + DATABASE_NAME,
//                null, DATABASE_VERSION);

        this.context = context;
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
    }

    public static synchronized SalesBeatDb getHelper(Context context) {
        if (instance == null)
            instance = new SalesBeatDb(context);

        return instance;
    }

    private static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create table employee record
        String CREATE_TABLE_EMP_RECORD = "CREATE TABLE " + TABLE_EMP_RECORD + "("
                + KEY_EID + " INTEGER PRIMARY KEY," + KEY_CID + " TEXT,"
                + KEY_EMP_NAME + " TEXT," + KEY_EMP_PHONE + " TEXT," + KEY_EMP_MAIL + " TEXT,"
                + KEY_ZONE + " TEXT," + KEY_ZONE_ID+ " TEXT," + KEY_STATE + " TEXT,"
                + KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT," + KEY_HEADQUARTER + " TEXT,"+ KEY_DESGNATION + " TEXT,"
                + KEY_REPORTING_TO + " TEXT," + KEY_PROFILE_PIC_URL + " TEXT," + KEY_WORKING_TOWN + " TEXT,"
                + KEY_WORKING_DISTRIBUTOR + " TEXT," + KEY_WORKING_BEAT + " TEXT," + KEY_TOKEN + " TEXT " + ")";

        //create table user detail
        String CREATE_TABLE_INAPPNOTIFICATION = "CREATE TABLE " + TABLE_INAPPNOTIFICATION + "("
                + KEY_INAPPNOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_INAPPNOT_TITLE + " TEXT,"
                + KEY_INAPPNOT_BODY + " TEXT," + KEY_INAPP_PIC + " TEXT," + KEY_INAPP_DATE + " TEXT," + KEY_STATUS_NOTIF + " TEXT " + ")";

        //create table user detail
        String CREATE_TABLE_OFFLINEDATE = "CREATE TABLE " + TABLE_DATE_OFFLINE_DESCRIPT + "("
                + KEY_OFFLINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATES + " TEXT,"
                + KEY_OFFLINE_JSON + " TEXT," + KEY_EXTRACOLUMN + " TEXT " + ")";

        //create table user detail
        String CREATE_TABLE_LEADERBOARD = "CREATE TABLE " + TABLE_LEADERBOARD + "("
                + KEY_EMP_ID + " TEXT," + KEY_EMP_NAME + " TEXT,"
                + KEY_EMP_PIC_URL + " TEXT," + KEY_TC + " TEXT," + KEY_PC + " TEXT,"
                + KEY_SALES + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user detail
        String CREATE_TABLE_EMP_PRIMARY_SALE = "CREATE TABLE " + TABLE_EMP_PRIMARY_SALE + "("
                + KEY_SALE_ACH + " TEXT," + KEY_SALE_TARGET + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user detail
        String CREATE_TABLE_EMP_SECONDARY_SALE = "CREATE TABLE " + TABLE_EMP_SECONDARY_SALE + "("
                + KEY_SALE_ACH + " TEXT," + KEY_SALE_TARGET + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user detail
        String CREATE_TABLE_EMP_KRA_DETAILS = "CREATE TABLE " + TABLE_EMP_KRA_DETAILS + "("
                + KEY_KRA_TC_ACHIEVEMENT + " TEXT," + KEY_KRA_PC_ACHIEVEMENT + " TEXT,"
                + KEY_KRA_TC_TARGET + " TEXT," + KEY_KRA_PC_TARGET + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user detail
        String CREATE_TABLE_CAMPAIGN = "CREATE TABLE " + TABLE_CAMPAIGN + "("
                + KEY_CAMPAIGN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CAMPAIGN_IMG + " TEXT," + KEY_CAMPAIGN_CONTENT + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user detail
        String CREATE_TABLE_DOCS = "CREATE TABLE " + TABLE_DOCS + "("
                + KEY_DOCS_ID + " INTEGER PRIMARY KEY,"
                + KEY_DOCS_IMG + " TEXT," + KEY_DOCS_CONTENT + " TEXT," + TRANSACTION_ID + " TEXT,"
                + KEY_LAST_UPDATED_AT + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table user attendance
        String CREATE_TABLE_USER_ATTENDANCE = "CREATE TABLE " + TABLE_USER_ATTENDANCE + "("
                + KEY_ID_ATTENDANCE + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATTENDANCE_STATUS + " TEXT,"
                + KEY_CHECK_IN_T + " TEXT," + KEY_CHECK_OUT_T + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_TOTAL_CALL + " TEXT," + KEY_PRODUCTIVE_CALL + " TEXT," + KEY_LINE_SOLD + " TEXT,"
                + KEY_TOTAL_WORKING_TIME + " TEXT," + KEY_TOTLA_RETAILING_TIME + " TEXT,"
                + KEY_REASON + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_MONTH + " TEXT,"
                + KEY_YEAR + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table town list
        String CREATE_TABLE_TOWN_LIST = "CREATE TABLE " + TABLE_TOWN_LIST + "("
                + KEY_TOWN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TOWN_NAME + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table distributor list
        String CREATE_TABLE_DISTRIBUTOR_LIST = "CREATE TABLE " + TABLE_DISTRIBUTOR_LIST + "("
                + KEY_DISTRIBUTOR_ID + " INTEGER PRIMARY KEY," + KEY_DISTRIBUTOR_NAME + " TEXT,"
                + KEY_TOWN_NAME + " TEXT," + KEY_DISTRIBUTOR_PHONE + " TEXT," + KEY_DISTRIBUTOR_EMAIL + " TEXT,"
                + KEY_DISTRIBUTOR_TYPE + " TEXT," + KEY_DISTRIBUTOR_ADDRESS + " TEXT," + KEY_DISTRIBUTOR_DISTRICT + " TEXT,"
                + KEY_DISTRIBUTOR_ZONE + " TEXT," + KEY_DISTRIBUTOR_STATE + " TEXT," + KEY_DISTRIBUTOR_PINCODE + " TEXT,"
                + KEY_RETAILER_LAT + " TEXT," + KEY_RETAILER_LONG + " TEXT," + KEY_RETAILER_DISTANCE + " TEXT," + KEY_DISTRIBUTOR_GST + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT," + KEY_LAST_UPDATED_AT + " TEXT " + ")";


        //create table sku details
        String CREATE_TABLE_SKU_DETAILS = "CREATE TABLE " + TABLE_SKU_DETAILS + "("
                + KEY_SKU_ID + " INTEGER PRIMARY KEY," + KEY_SKU_NAME + " TEXT,"
                + KEY_SKU_BRAND_NAME + " TEXT," + KEY_SKU_BRAND_PRICE + " TEXT,"
                + KEY_SKU_BRAND_WEIGHT + " TEXT,"
                + KEY_SKU_CONVERSION_FACTOR + " TEXT,"+ KEY_SKU_IMAGE + " TEXT," + KEY_SKU_BRAND_UNIT + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table sku entry list
        String CREATE_TABLE_SKU_ENTRY_LIST = "CREATE TABLE " + TABLE_SKU_ENTRY_LIST + "("
                + KEY_SKU_ID_L + " TEXT," + KEY_SKU_NAME_L + " TEXT,"
                + KEY_SKU_BRAND_NAME_L + " TEXT," + KEY_SKU_BRAND_PRICE_L + " TEXT,"
                + KEY_SKU_BRAND_QTY_L + " TEXT," + KEY_SKU_BRAND_UNIT_L + " TEXT,"
                + KEY_SKU_ENTRY_TIME + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_SKU_ENTRY_TYPE + " TEXT,"
                + KEY_SKU_STOCK_TYPE + " TEXT," + KEY_SKU_ENTRY_DATE + " TEXT," + KEY_SKU_ENTRY_LAT + " TEXT,"
                + KEY_SKU_ENTRY_LONGT + " TEXT," + KEY_SKU_CONVERSION_FACTOR + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY(" + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_SKU_ENTRY_TYPE + ")REFERENCES "
                + TABLE_DISTRIBUTOR_ORDER_ENTRY + "(" + KEY_DISTRIBUTOR_ID_O + "," + KEY_DISTRIBUTOR_ORDER_STATUS_O + ","
                + KEY_DISTRIBUTOR_ORDER_TYPE + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")";


        //create table closing entry list
        String CREATE_TABLE_CLOSING_ENTRY_LIST = "CREATE TABLE " + TABLE_SKU_CLOSING_ENTRY_LIST + "("
                + KEY_SKU_ID_L + " TEXT," + KEY_SKU_NAME_L + " TEXT,"
                + KEY_SKU_BRAND_NAME_L + " TEXT," + KEY_SKU_BRAND_PRICE_L + " TEXT,"
                + KEY_SKU_BRAND_QTY_L + " TEXT," + KEY_SKU_BRAND_UNIT_L + " TEXT,"
                + KEY_SKU_ENTRY_TIME + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_SKU_ENTRY_TYPE + " TEXT,"
                + KEY_SKU_STOCK_TYPE + " TEXT," + KEY_SKU_ENTRY_DATE + " TEXT," + KEY_SKU_ENTRY_LAT + " TEXT,"
                + KEY_SKU_ENTRY_LONGT + " TEXT," + KEY_SKU_CONVERSION_FACTOR + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY(" + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_SKU_ENTRY_TYPE + ")REFERENCES "
                + TABLE_DISTRIBUTOR_ORDER_ENTRY + "(" + KEY_DISTRIBUTOR_ID_O + "," + KEY_DISTRIBUTOR_ORDER_STATUS_O + ","
                + KEY_DISTRIBUTOR_ORDER_TYPE + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")";


        //create table sku entry list
        String CREATE_TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST = "CREATE TABLE " + TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST + "("
                + KEY_DISTRIBUTOR_ORDER_ID_L + " TEXT," + KEY_DISTRIBUTOR_ORDER_NAME_L + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_BRAND_NAME_L + " TEXT," + KEY_DISTRIBUTOR_ORDER_BRAND_PRICE_L + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_L + " TEXT," + KEY_DISTRIBUTOR_ORDER_BRAND_UNIT_L + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_ENTRY_TIME + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_ENTRY_DATE + " TEXT," + KEY_DISTRIBUTOR_ORDER_ENTRY_LAT + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_ENTRY_LONGT + " TEXT," + KEY_SKU_CONVERSION_FACTOR + " TEXT," + KEY_SKU_ENTRY_TYPE + " TEXT,"
                + KEY_DISTRIBUTOR_ORDE_TYPE + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY(" + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_SKU_ENTRY_TYPE + ")REFERENCES "
                + TABLE_DISTRIBUTOR_ORDER_ENTRY + "(" + KEY_DISTRIBUTOR_ID_O + "," + KEY_DISTRIBUTOR_ORDER_STATUS_O + ","
                + KEY_DISTRIBUTOR_ORDER_TYPE + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")";


        String CREATE_TABLE_ORDER_ENTRY_LIST = "CREATE TABLE " + TABLE_ORDER_ENTRY_LIST + "("
                + KEY_ORDER_SKU_ID_L + " TEXT," + KEY_ORDER_SKU_NAME_L + " TEXT,"
                + KEY_ORDER_SKU_BRAND_NAME_L + " TEXT," + KEY_ORDER_SKU_BRAND_PRICE_L + " TEXT,"
                + KEY_ORDER_SKU_BRAND_QTY_L + " TEXT," + KEY_ORDER_SKU_BRAND_UNIT_L + " TEXT,"
                + KEY_RETAILER_ID + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT,"
                + KEY_SKU_CONVERSION_FACTOR + " TEXT," + KEY_ORDER_SKU_ENTRY_DATE_L + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY(" + KEY_RETAILER_ID + "," + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + ")REFERENCES "
                + TABLE_ORDER_PLACED_BY_RETAILERS + "(" + KEY_ORDER_PLACED_BY_RID + "," + KEY_ORDER_PLACED_BY_DID + ","
                + KEY_ORDER_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")";

        String CREATE_TABLE_PENDING_ORDERS = "CREATE TABLE " + TABLE_PENDING_ORDERS + "("
                + KEY_ORDER_SKU_ID_L + " TEXT," + KEY_ORDER_SKU_BRAND_QTY_L + " TEXT,"
                + " FOREIGN KEY(" + KEY_RETAILER_ID + "," + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + ")REFERENCES "
                + TABLE_PENDING_ORDER_PLACED_BY + "(" + KEY_ORDER_PLACED_BY_RID + "," + KEY_ORDER_PLACED_BY_DID + ","
                + KEY_SERVER_SUBMIT_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE" + ")";

        String CREATE_TABLE_PENDING_ORDER_PLACED_BY = "CREATE TABLE " + TABLE_PENDING_ORDER_PLACED_BY + "("
                + KEY_ORDER_PLACED_BY_RID + " TEXT," + KEY_ORDER_PLACED_BY_DID + " TEXT,"
                + KEY_ORDER_PLACED_TIME + " TEXT," + KEY_ORDER_TYPE + " TEXT,"
                + KEY_ORDER_CHECK_IN + " TEXT," + KEY_ORDER_CHECK_OUT + " TEXT,"+ KEY_BRAND_KG + " TEXT," + KEY_BRAND_UNIT + " TEXT,"
                + KEY_ORDER_LAT + " TEXT," + KEY_ORDER_LONG + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + KEY_STATUS_CODE + " TEXT," + KEY_ERROR_MSG + " TEXT,"+ KEY_ORDER_DATE + " TEXT,"
                + KEY_ORDER_COMMENT + " TEXT," + TRANSACTION_ID + " TEXT,"
                + KEY_RECORD_DATE + " TEXT," + " PRIMARY KEY (" + KEY_ORDER_PLACED_BY_RID + ","
                + KEY_ORDER_PLACED_BY_DID + "," + KEY_SERVER_SUBMIT_STATUS + ")" + ")";


        String CREATE_TABLE_NEW_ORDER_ENTRY_LIST = "CREATE TABLE " + TABLE_NEW_ORDER_ENTRY_LIST + "("
                + KEY_NEW_ORDER_SKU_ID_L + " TEXT," + KEY_NEW_RETAILER_ID + " INTEGER," + KEY_NEW_RETAILER_TEMP_IDD + " INTEGER,"
                + KEY_NEW_ORDER_SKU_NAME_L + " TEXT," + KEY_NEW_ORDER_SKU_BRAND_NAME_L + " TEXT,"
                + KEY_NEW_ORDER_SKU_BRAND_PRICE_L + " TEXT," + KEY_NEW_ORDER_SKU_BRAND_QTY_L + " TEXT,"
                + KEY_NEW_ORDER_SKU_BRAND_UNIT_L + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT,"
                + KEY_NEW_ORDER_ENTRY_DATE + " TEXT," + KEY_SKU_CONVERSION_FACTOR + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + KEY_NEW_ORDER_STATUS + " TEXT," + " FOREIGN KEY(" + KEY_NEW_RETAILER_ID + ","
                + KEY_NEW_RETAILER_TEMP_IDD + "," + KEY_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + ","
                + KEY_NEW_ORDER_STATUS + ")REFERENCES " + TABLE_NEW_RETAILERS_LIST
                + "(" + KEY_NEW_RETAILER_ID + "," + KEY_NEW_RETAILER_TEMP_IDD + "," + KEY_DISTRIBUTOR_ID
                + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_NEW_ORDER_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE " + ")";


        //create table sku id table
        String CREATE_TABLE_SKU_ID = "CREATE TABLE " + TABLE_SKU_ID + "("
                + KEY_SKU_ID_S + " TEXT," + KEY_DISTRIBUTOR_ID_S + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table beat list
        String CREATE_TABLE_BEAT_LIST = "CREATE TABLE " + TABLE_BEAT_LIST + "("
                + KEY_BEAT_ID + " INTEGER PRIMARY KEY," + KEY_BEAT_NAME + " TEXT,"
                + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_BEAT_VISITED + " TEXT,"
                + KEY_BEAT_VISITED_LAT + " TEXT," + KEY_BEAT_VISITED_LONGT + " TEXT,"+ KEY_BEAT_RANGE + " TEXT,"
                + KEY_BEAT_VISITED_TIME + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT," + KEY_LAST_UPDATED_AT + " TEXT " + ")";
//                + " CONSTRAINT " + KEY_B_ID + " UNIQUE " + "(" + KEY_BEAT_ID + "," + KEY_DISTRIBUTOR_ID + ")" + ")";

        //create table sku id table
        String CREATE_TABLE_BEAT_VISITED_LIST = "CREATE TABLE " + TABLE_BEAT_VISITED_LIST + "("
                + KEY_BEAT_ID + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_BEAT_VISITED + " TEXT,"
                + KEY_BEAT_VISITED_LAT + " TEXT," + KEY_BEAT_VISITED_LONGT + " TEXT," + KEY_BEAT_VISITED_TIME + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + " CONSTRAINT " + KEY_B_ID + " UNIQUE " + "(" + KEY_BEAT_ID + "," + KEY_DISTRIBUTOR_ID + ")" + ")";


        //create table retailer list
        String CREATE_TABLE_RETAIERS_LIST = "CREATE TABLE " + TABLE_RETAILERS_LIST + "("
                + KEY_RETAILER_ID + " INTEGER PRIMARY KEY," + KEY_BEAT_ID_R + " TEXT,"
                + KEY_RETAILER_UNIQUE_ID + " TEXT," + KEY_RETAILER_NAME + " TEXT,"
                + KEY_RETAILER_ADDRESS + " TEXT," + KEY_RETAILER_STATE + " TEXT," + KEY_RETAILER_EMAIL + " TEXT,"
                + KEY_SHOP_PHONE + " TEXT," + KEY_OWNER_NAME + " TEXT," + KEY_OWNER_PHONE + " TEXT,"
                + KEY_WHATSAPP_NO + " TEXT," + KEY_RETAILER_LAT + " TEXT," + KEY_RETAILER_LONG + " TEXT," + KEY_RETAILER_DISTANCE + " TEXT,"
                + KEY_RETAILER_GSTIN + " TEXT," + KEY_RETAILER_PIN + " TEXT," + KEY_RETAILER_FSSAI + " TEXT,"
                + KEY_DISTRICT + " TEXT," + KEY_LOCALITY + " TEXT," + KEY_ZONE + " TEXT," + KEY_TARGET + " TEXT,"
                + KEY_OUTLET_CHANNEL + " TEXT," + KEY_SHOP_TYPE + " TEXT," + KEY_RETAILER_GRADE + " TEXT,"
                + KEY_RETAILER_IMAGE + " TEXT," + KEY_BEAT_ID + " TEXT," + TRANSACTION_ID + " TEXT,"
                + KEY_LAST_UPDATED_AT + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table retailer list
        String CREATE_TABLE_NEW_RETAIERS_LIST = "CREATE TABLE " + TABLE_NEW_RETAILERS_LIST + "("
                + KEY_NEW_RETAILER_IDD + " INTEGER," + KEY_NEW_RETAILER_TEMP_IDD + " INTEGER," + KEY_NEW_RETAILER_NAME + " TEXT,"
                + KEY_NEW_RETAILER_ADDRESS + " TEXT," + KEY_NEW_RETAILER_STATE + " TEXT,"
                + KEY_NEW_RETAILER_EMAIL + " TEXT," + KEY_NEW_SHOP_PHONE + " TEXT,"
                +  KEY_STATUS_CODE + " TEXT,"+KEY_ERROR_MSG + " TEXT,"
                + KEY_NEW_OWNER_NAME + " TEXT," + KEY_NEW_OWNER_PHONE + " TEXT," + KEY_NEW_WHATSAPP_NO + " TEXT,"
                + KEY_NEW_RETAILER_LAT + " TEXT," + KEY_NEW_RETAILER_LONG + " TEXT," + KEY_NEW_RETAILER_GSTIN + " TEXT,"
                + KEY_NEW_RETAILER_PIN + " TEXT," + KEY_NEW_RETAILER_FSSAI + " TEXT," + KEY_NEW_DISTRICT + " TEXT,"
                + KEY_NEW_LOCALITY + " TEXT," + KEY_NEW_ZONE + " TEXT," + KEY_NEW_TARGET + " TEXT,"
                + KEY_NEW_OUTLET_CHANNEL + " TEXT," + KEY_NEW_SHOP_TYPE + " TEXT," + KEY_NEW_RETAILER_GRADE + " TEXT,"
                + KEY_NEW_OWNER_IMAGE + " TEXT," + KEY_IMAGE_TIME_STAMP + " TEXT," + KEY_NEW_SHOP_IMAGE1 + " TEXT,"
                + KEY_NEW_SHOP_IMAGE2 + " TEXT," + KEY_NEW_SHOP_IMAGE3 + " TEXT," + KEY_NEW_SHOP_IMAGE4 + " TEXT,"
                + KEY_NEW_SHOP_IMAGE5 + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_BEAT_ID + " TEXT,"
                + KEY_NEW_ORDER_DATE + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID
                + " TEXT," + KEY_RECORD_DATE + " TEXT," + KEY_NEW_ORDER_STATUS + " TEXT,"
                + "PRIMARY KEY ( " + KEY_NEW_RETAILER_IDD + "," + KEY_NEW_RETAILER_TEMP_IDD + "," + KEY_DISTRIBUTOR_ID + ","
                + KEY_SERVER_SUBMIT_STATUS + "," + KEY_NEW_ORDER_STATUS + ")" + ")";


        //create table ACTIVITY TRACKING
        String CREATE_TABLE_ACTIVITY_TRACKING = "CREATE TABLE " + TABLE_ACTIVITY_TRACKING + "("
                + KEY_TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE_A + " TEXT," + KEY_WALK_LAT1
                + " TEXT," + KEY_WALK_LONGT1 + " TEXT," + KEY_ACCURACY + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table message list
        String CREATE_TABLE_MESSAGE_LIST = "CREATE TABLE " + TABLE_MESSAGE_LIST + "("
                + KEY_TIME_STAMP + " TEXT," + KEY_MESSAGE + " TEXT," + TRANSACTION_ID + " TEXT,"
                + KEY_RECORD_DATE + " TEXT " + ")";


        //create table order placed by
        String CREATE_TABLE_ORDER_PLACED_BY = "CREATE TABLE " + TABLE_ORDER_PLACED_BY_RETAILERS + "("
                + KEY_ORDER_PLACED_BY_RID + " TEXT," + KEY_ORDER_PLACED_BY_DID + " TEXT,"
                + KEY_ORDER_PLACED_TIME + " TEXT," + KEY_ORDER_TYPE + " TEXT,"
                +  KEY_STATUS_CODE + " TEXT,"+KEY_ERROR_MSG + " TEXT,"
                + KEY_ORDER_CHECK_IN + " TEXT," + KEY_ORDER_CHECK_OUT + " TEXT," + KEY_BRAND_KG + " TEXT," + KEY_BRAND_UNIT + " TEXT,"
                + KEY_ORDER_LAT + " TEXT," + KEY_ORDER_LONG + " TEXT," + KEY_ORDER_STATUS + " TEXT,"
                + KEY_ORDER_DATE + " TEXT," + KEY_ORDER_COMMENT + " TEXT," + TRANSACTION_ID + " TEXT,"
                + KEY_RECORD_DATE + " TEXT," + " PRIMARY KEY (" + KEY_ORDER_PLACED_BY_RID + ","
                + KEY_ORDER_PLACED_BY_DID + "," + KEY_ORDER_STATUS + ")" + ")";


        String CREATE_TABLE_NEW_ORDER_PLACED_BY = "CREATE TABLE " + TABLE_ORDER_PLACED_BY_NEW_RETAILERS + "("
                + KEY_NEW_ORDER_PLACED_BY_RID + " INTEGER," + KEY_NEW_RETAILER_TEMP_IDD + " INTEGER,"
                + KEY_NEW_ORDER_PLACED_BY_DID + " TEXT," + KEY_NEW_ORDER_PLACED_TIME + " TEXT," + KEY_NEW_ORDER_TYPE + " TEXT,"
                + KEY_NEW_ORDER_CHECK_IN + " TEXT," + KEY_NEW_ORDER_CHECK_OUT + " TEXT,"
                + KEY_NEW_ORDER_LAT + " TEXT," + KEY_NEW_ORDER_LONG + " TEXT,"
                +  KEY_STATUS_CODE + " TEXT,"+KEY_ERROR_MSG + " TEXT,"
                + KEY_NEW_ORDER_COMMENT + " TEXT," + KEY_NEW_ORDER_PLACED_BY_DATE + " TEXT,"
                + KEY_NEW_ORDER_STATUS + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY (" + KEY_NEW_ORDER_PLACED_BY_RID + "," + KEY_NEW_RETAILER_TEMP_IDD + ","
                + KEY_NEW_ORDER_PLACED_BY_DID + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_NEW_ORDER_STATUS + ") REFERENCES "
                + TABLE_NEW_RETAILERS_LIST + "(" + KEY_NEW_RETAILER_IDD + "," + KEY_NEW_RETAILER_TEMP_IDD + "," + KEY_DISTRIBUTOR_ID + ","
                + KEY_SERVER_SUBMIT_STATUS + "," + KEY_NEW_ORDER_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE " + ")";


        String CREATE_TABLE_NEW_PREFERRED_RETAILER_LIST = "CREATE TABLE " + TABLE_NEW_PREFERRED_RETAILERS_LIST + "("
                + KEY_NEW_PREFERRED_RETAILER_ID + " INTEGER," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + " INTEGER,"
                + KEY_NEW_PREFERRED_RETAILER_FIRM_NAME + " TEXT," + KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME1 + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME2 + " TEXT," + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_NAME + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME1 + " TEXT," + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME2 + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_LATITUDE + " TEXT," + KEY_NEW_PREFERRED_RETAILER_LONGITUDE + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_BLOCK + " TEXT," + KEY_NEW_PREFERRED_RETAILER_DISTRICT + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_PROPOSE_CATEGORY + " TEXT," + KEY_NEW_PREFERRED_RETAILER_PER_MONTH_BUSINESS + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTION_BRAND_NAME + " TEXT," + KEY_MONTHLY_TURNOVER + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_INTERNAL_STAFF + " TEXT," + KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_WALK_IN + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_CUSTOMER_INCOME + " TEXT," + KEY_NEW_PREFERRED_RETAILER_OTHER_BUSINESS + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_SHOP_PATH + " TEXT," + KEY_NEW_PREFERRED_RETAILER_ROOF_HEIGHT + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_FASCIA_WIDTH + " TEXT," + KEY_NEW_PREFERRED_RETAILER_FIXTURE_TYPE + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_FRONT_SPACE + " TEXT," + KEY_NEW_PREFERRED_RETAILER_TOTAL_SHELF + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_SPACE_PROVIDED + " TEXT," + KEY_NEW_PREFERRED_RETAILER_FRONT_COUNTER_MEASUREMENT + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_ROAD_WIDTH + " TEXT," + KEY_NEW_PREFERRED_RETAILER_FLOOR_TYPE + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_BRAND_POSTING + " TEXT," + KEY_NEW_PREFERRED_RETAILER_SIDE_PANEL_SIZE + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_COUNTER_SIZE + " TEXT," + KEY_NEW_PREFERRED_RETAILER_FRONT_BOARD_SIZE + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + " TEXT," + KEY_NEW_PREFERRED_RETAILER_BEAT_ID + " TEXT,"
                + KEY_NEW_PREFERRED_RETAILER_NEW_ORDER_DATE + " TEXT," + KEY_NEW_PREFERRED_RETAILER_TRANSACTION_ID + " TEXT,"
                + KEY_RECORD_DATE + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT," + KEY_PREFERRED_ORDER_STATUS + " TEXT,"
                + "PRIMARY KEY (" + KEY_NEW_PREFERRED_RETAILER_ID + "," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + ","
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + ","
                + KEY_PREFERRED_ORDER_STATUS + ")" + ")";


        String CREATE_TABLE_PREFERRED_ORDER_PLACED_BY = "CREATE TABLE " + TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS + "("
                + KEY_PREFERRED_ORDER_PLACED_BY_RID + " INTEGER," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + " INTEGER,"
                + KEY_PREFERRED_ORDER_PLACED_BY_DID + " TEXT," + KEY_PREFERRED_ORDER_PLACED_TIME + " TEXT,"
                + KEY_PREFERRED_ORDER_TYPE + " TEXT," + KEY_PREFERRED_ORDER_CHECK_IN + " TEXT,"
                + KEY_PREFERRED_ORDER_CHECK_OUT + " TEXT," + KEY_PREFERRED_ORDER_LAT + " TEXT,"
                + KEY_PREFERRED_ORDER_LONG + " TEXT," + KEY_PREFERRED_ORDER_COMMENT + " TEXT,"
                + KEY_PREFERRED_ORDER_PLACED_BY_DATE + " TEXT," + KEY_PREFERRED_ORDER_STATUS
                + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " FOREIGN KEY (" + KEY_PREFERRED_ORDER_PLACED_BY_RID + "," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + ","
                + KEY_PREFERRED_ORDER_PLACED_BY_DID + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_PREFERRED_ORDER_STATUS + ") REFERENCES "
                + TABLE_NEW_PREFERRED_RETAILERS_LIST + "(" + KEY_NEW_PREFERRED_RETAILER_ID + "," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + ","
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "," + KEY_SERVER_SUBMIT_STATUS + ","
                + KEY_PREFERRED_ORDER_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE " + ")";


        String CREATE_TABLE_PREFERRED_ORDER_ENTRY_LIST = "CREATE TABLE " + TABLE_PREFERRED_ORDER_ENTRY_LIST + "("
                + KEY_NEW_ORDER_SKU_ID_L + " TEXT," + KEY_NEW_PREFERRED_RETAILER_ID + " INTEGER,"
                + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + " INTEGER,"
                + KEY_NEW_ORDER_SKU_NAME_L + " TEXT," + KEY_NEW_ORDER_SKU_BRAND_NAME_L + " TEXT,"
                + KEY_NEW_ORDER_SKU_BRAND_PRICE_L + " TEXT," + KEY_NEW_ORDER_SKU_BRAND_QTY_L + " TEXT,"
                + KEY_NEW_ORDER_SKU_BRAND_UNIT_L + " TEXT," + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + " TEXT,"
                + KEY_NEW_ORDER_ENTRY_DATE + " TEXT," + KEY_SKU_CONVERSION_FACTOR + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + KEY_PREFERRED_ORDER_STATUS + " TEXT," + " FOREIGN KEY(" + KEY_NEW_PREFERRED_RETAILER_ID + ","
                + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + "," + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + ","
                + KEY_SERVER_SUBMIT_STATUS + "," + KEY_PREFERRED_ORDER_STATUS + ")REFERENCES " + TABLE_NEW_PREFERRED_RETAILERS_LIST
                + "(" + KEY_NEW_PREFERRED_RETAILER_ID + "," + KEY_NEW_PREFERRED_RETAILER_TEMP_ID + "," + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID
                + "," + KEY_SERVER_SUBMIT_STATUS + "," + KEY_PREFERRED_ORDER_STATUS + ") ON UPDATE CASCADE ON DELETE CASCADE " + ")";


        //create table NEW DISTRIBUTOR
        String CREATE_TABLE_NEW_DISTRIBUTOR = "CREATE TABLE " + TABLE_NEW_DISTRIBUTOR + "("
                + KEY_NEW_DISTRIBUTOR_ID + " INTEGER PRIMARY KEY," + KEY_NAME_OF_FIRM + " TEXT,"
                + KEY_FIRM_ADDRESS + " TEXT," + KEY_BRAND_NAME + " TEXT," + KEY_OTHER_BRAND_NAME + " TEXT," + KEY_PINCODE + " TEXT," + KEY_CITY + " TEXT," + KEY_STATE + " TEXT,"
                + KEY_OWNER_NAME_D + " TEXT," + KEY_OWNER_MOBILE_NO1 + " TEXT," + KEY_OWNER_MOBILE_NO2 + " TEXT,"
                + KEY_EMAIL_ID + " TEXT," + KEY_GSTIN + " TEXT," + KEY_FSSAI_NO + " TEXT," + KEY_PAN_NO + " TEXT,"
                + KEY_MONTHLY_TURNOVER + " TEXT," + KEY_OWNER_IMAGE + " TEXT," + KEY_OWNER_IMAGE_TIME_STAMP + " TEXT,"
                + KEY_OWNER_IMAGE_LATLONG + " TEXT," + KEY_TOTAL_NO_EMP + " TEXT," + KEY_SALES_PERSON + " TEXT,"
                + KEY_ADMIN_PERSON + " TEXT," + KEY_DELIVERY_VEHICLE + " TEXT," + KEY_NO_OF_VEHICLE + " TEXT,"
                + KEY_BEAT_NAME_D + " TEXT," + KEY_NO_OF_SHOP_IN_BEAT + " TEXT," + KEY_NO_OF_SHOP_COVERED + " TEXT,"
                + KEY_NO_OF_GUMTI_COVERED + " TEXT," + KEY_TOTAL_NO_OF_SHOP + " TEXT," + KEY_NO_OF_WHOLESALER + " TEXT,"
                + KEY_INVESTMENT_PLAN + " TEXT," + KEY_PRODUCT_DIVISION + " TEXT," + KEY_MONTHLY_SALE_ESTIMATE + " TEXT,"
                + KEY_DISTRIBUTOR_TYPE_D + " TEXT," + KEY_DISTRIBUTOR_PARENT_NAME + " TEXT," + KEY_WORKING_BRAND + " TEXT,"
                + KEY_WORKING_SINCE + " TEXT," + KEY_OTHER_CONTACT_PERSON_NAME + " TEXT," + KEY_OTHER_CONTACT_PERSON_PHN + " TEXT,"
                + KEY_FIRM_IMAGE + " TEXT," + KEY_FIRM_IMAGE_TIME_STAMP + " TEXT," + KEY_FIRM_IMAGE_LATLONG + " TEXT,"+ KEY_DISTRIBUTOR_RECORDING + " TEXT,"
                + KEY_OPINION_ABOUT_DISTRIBUTOR + " TEXT," + KEY_COMMENT + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table distributor order entry table
        String CREATE_TABLE_DISTRIBUTOR_ORDER_ENTRY = "CREATE TABLE " + TABLE_DISTRIBUTOR_ORDER_ENTRY + "("
                + KEY_DISTRIBUTOR_ID_O + " TEXT," + KEY_DISTRIBUTOR_ORDER_TAKEN_O + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_STATUS_O + " TEXT," + KEY_DISTRIBUTOR_ORDER_DATE_O + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_LAT_O + " TEXT," + KEY_DISTRIBUTOR_ORDER_LONGT_O + " TEXT,"
                + KEY_DISTRIBUTOR_ORDER_TYPE + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT,"
                + " PRIMARY KEY (" + KEY_DISTRIBUTOR_ID_O + "," + KEY_DISTRIBUTOR_ORDER_STATUS_O + "," + KEY_DISTRIBUTOR_ORDER_TYPE + ")" + ")";


        //create table chat history table
        String CREATE_TABLE_CHAT_HISTORY = "CREATE TABLE " + TABLE_CHAT_HISTORY + "("
                + KEY_CHAT_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_CHAT + " TEXT," + KEY_DATE_CHAT + " TEXT,"
                + KEY_CONTACT_CHAT + " TEXT," + KEY_CHANNEL_CHAT + " TEXT,"
                + KEY_TIME_STAMP_CHAT + " TEXT," + KEY_IMAGE_CHAT + " TEXT,"
                + KEY_VIDEO_CHAT + " TEXT," + KEY_AUDIO_CHAT + " TEXT," + KEY_DOCS_CHAT + " TEXT,"
                + KEY_LOCATION_CHAT + " TEXT," + KEY_RETAILER_ID + " TEXT,"
                + KEY_FEEDBACK_BY + " TEXT," + KEY_SERVER_SUBMIT_STATUS + " TEXT,"
                + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table chat history table
        String CREATE_TABLE_PRIMARY_SALE_HISTORY = "CREATE TABLE " + TABLE_PRIMARY_SALE_HISTORY + "("
                + KEY_DISTRIBUTOR_ID_P + " INTEGER PRIMARY KEY," + KEY_DISTRIBUTOR_NAME_P + " TEXT,"
                + KEY_DISTRIBUTOR_SALE_TARGET + " TEXT," + KEY_DISTRIBUTOR_SALE_ACH + " TEXT,"
                + KEY_SALE_DATE + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table chat history table
        String CREATE_TABLE_OTHER_ACTIVITY = "CREATE TABLE " + TABLE_OTHER_ACTIVITY + "("
                + KEY_OTHER_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_OTHER_ACTIVITY + " TEXT,"
                + KEY_OTHER_ACTIVITY_REMARKS + " TEXT," + KEY_OTHER_ACTIVITY_DATE + " TEXT,"
                + KEY_OTHER_ACTIVITY_LAT + " TEXT," + KEY_OTHER_ACTIVITY_LONGT + " TEXT,"
                + KEY_SERVER_SUBMIT_STATUS + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table chat history table
        String CREATE_TABLE_DISTRIBUTOR_TARGET_ACH = "CREATE TABLE " + TABLE_DISTRIBUTOR_TARGET_ACH + "("
                + KEY_DISTRIBUTOR_TARACH_ID + " INTEGER PRIMARY KEY," + KEY_DISTRIBUTOR_TARGET + " TEXT,"
                + KEY_DISTRIBUTOR_ACHIEVEMENT + " TEXT," + TRANSACTION_ID + " TEXT," + KEY_RECORD_DATE + " TEXT " + ")";


        //create table chat history table
        String CREATE_TABLE_DISBEAT_MAP = "CREATE TABLE " + TABLE_DISBEAT_MAP + "("
                + KEY_TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DID + " TEXT," + KEY_BID + " TEXT " + ")";


        //create table chat history table
        String CREATE_TABLE_DISSKU_MAP = "CREATE TABLE " + TABLE_DISSKU_MAP + "("
                + KEY_TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DID + " TEXT," + KEY_SID + " TEXT " + ")";


        //create table create pjp
        String CREATE_TABLE_CREATE_PJP = "CREATE TABLE " + TABLE_CREATE_PJP + "("
                + KEY_TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ACTIVITY + " TEXT,"
                + KEY_TOWN + " TEXT," + KEY_DISTRIBUTOR_ID + " TEXT," + KEY_DISTRIBUTOR + " TEXT,"
                + KEY_BEAT_ID + " TEXT," + KEY_BEAT + " TEXT," + KEY_EMP_ID + " TEXT,"
                + KEY_EMP + " TEXT," + KEY_TC_PJP + " TEXT," + KEY_PC_PJP + " TEXT,"
                + KEY_SALE_PJP + " TEXT," + KEY_PJP_DATE + " TEXT," + KEY_OTHER_ACTIVITY_REMARKS + " TEXT " + ")";


        db.execSQL(CREATE_TABLE_USER_ATTENDANCE);
        db.execSQL(CREATE_TABLE_TOWN_LIST);
        db.execSQL(CREATE_TABLE_DISTRIBUTOR_LIST);
        db.execSQL(CREATE_TABLE_SKU_DETAILS);
        db.execSQL(CREATE_TABLE_SKU_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_ORDER_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_NEW_ORDER_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_SKU_ID);
        db.execSQL(CREATE_TABLE_BEAT_LIST);
        db.execSQL(CREATE_TABLE_BEAT_VISITED_LIST);
        db.execSQL(CREATE_TABLE_RETAIERS_LIST);
        db.execSQL(CREATE_TABLE_NEW_RETAIERS_LIST);
        db.execSQL(CREATE_TABLE_ACTIVITY_TRACKING);
        db.execSQL(CREATE_TABLE_MESSAGE_LIST);
        db.execSQL(CREATE_TABLE_ORDER_PLACED_BY);
        db.execSQL(CREATE_TABLE_NEW_ORDER_PLACED_BY);
        db.execSQL(CREATE_TABLE_NEW_DISTRIBUTOR);
        db.execSQL(CREATE_TABLE_DISTRIBUTOR_ORDER_ENTRY);
        db.execSQL(CREATE_TABLE_CHAT_HISTORY);
        db.execSQL(CREATE_TABLE_LEADERBOARD);
        db.execSQL(CREATE_TABLE_EMP_PRIMARY_SALE);
        db.execSQL(CREATE_TABLE_EMP_SECONDARY_SALE);
        db.execSQL(CREATE_TABLE_EMP_KRA_DETAILS);
        db.execSQL(CREATE_TABLE_CAMPAIGN);
        db.execSQL(CREATE_TABLE_DOCS);
        db.execSQL(CREATE_TABLE_PRIMARY_SALE_HISTORY);
        db.execSQL(CREATE_TABLE_OTHER_ACTIVITY);
        db.execSQL(CREATE_TABLE_DISTRIBUTOR_TARGET_ACH);
        db.execSQL(CREATE_TABLE_DISBEAT_MAP);
        db.execSQL(CREATE_TABLE_DISSKU_MAP);
        db.execSQL(CREATE_TABLE_CREATE_PJP);
        db.execSQL(CREATE_TABLE_CLOSING_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_INAPPNOTIFICATION);
        db.execSQL(CREATE_TABLE_OFFLINEDATE);
        db.execSQL(CREATE_TABLE_NEW_PREFERRED_RETAILER_LIST);
        db.execSQL(CREATE_TABLE_PREFERRED_ORDER_PLACED_BY);
        db.execSQL(CREATE_TABLE_PREFERRED_ORDER_ENTRY_LIST);
        db.execSQL(CREATE_TABLE_EMP_RECORD);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys='ON';");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (newVersion > oldVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ATTENDANCE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOWN_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTOR_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKU_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKU_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKU_CLOSING_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_ORDER_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERRED_ORDER_ENTRY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKU_ID);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEAT_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEAT_VISITED_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETAILERS_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_RETAILERS_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_PREFERRED_RETAILERS_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_TRACKING);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_PLACED_BY_RETAILERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_PLACED_BY_NEW_RETAILERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_DISTRIBUTOR);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTOR_ORDER_ENTRY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP_PRIMARY_SALE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP_SECONDARY_SALE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP_KRA_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIMARY_SALE_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTHER_ACTIVITY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTOR_TARGET_ACH);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISBEAT_MAP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISSKU_MAP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INAPPNOTIFICATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_PJP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATE_OFFLINE_DESCRIPT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP_RECORD);

            SharedPreferences.Editor editor = tempPref.edit();
            editor.remove(context.getString(R.string.town_name_key));
            editor.remove(context.getString(R.string.is_on_retailer_page));
            editor.remove(context.getString(R.string.beat_id_key));
            editor.remove(context.getString(R.string.beat_name_key));
            editor.remove(context.getString(R.string.dis_id_key));
            editor.remove(context.getString(R.string.dis_name_key));
            editor.apply();
            // Create tables again
            onCreate(db);
//            }
        }
    }

    public void getMyDB() {

        @SuppressLint("SdCardPath") File Db = new File("/data/data/com.newsalesbeat/databases/SalesBeat");

        //String PATH = Environment.getExternalStorageDirectory() + "/CMNY/sb.db";

        try {
            //@Umesh 26-06-2022
            File dir = new File(Environment.getExternalStorageDirectory(), "NewSalesBeat");
            if(!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(Environment.getExternalStorageDirectory(), "/NewSalesBeat/sb.db");
            if(!file.exists()) {
                file.createNewFile();
            }
            file.setWritable(true);
            copyFile(new FileInputStream(Db), new FileOutputStream(file));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //insert inapp notification details in InappNotification table
    public long insertInappNotification(String attendance_delay, String s, String s1, String dayOfMonth) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INAPPNOT_TITLE, attendance_delay);
        contentValues.put(KEY_INAPPNOT_BODY, s);
        contentValues.put(KEY_INAPP_PIC, s1);
        contentValues.put(KEY_INAPP_DATE, dayOfMonth);
        contentValues.put(KEY_STATUS_NOTIF, "unread");

        closeDb(db);
        return db.insertOrThrow(TABLE_INAPPNOTIFICATION, null, contentValues);

    }


    //insert insertDateDescription details in DATEDESCOFFLINE table
    public long insertDateDescription(String date, String jsonString, String extraToBeUsed) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DATES, date);
        contentValues.put(KEY_OFFLINE_JSON, jsonString);
        contentValues.put(KEY_EXTRACOLUMN, extraToBeUsed);
        closeDb(db);
        return db.insertOrThrow(TABLE_DATE_OFFLINE_DESCRIPT, null, contentValues);

    }

    //function to get all record from user_attendance_table
    public Cursor getJSONRecordFROMTABLE_DATE_OFFLINE_DESCRIPT(String date) {

        SQLiteDatabase db = getReadableDb();

        return db.rawQuery("SELECT " + KEY_OFFLINE_JSON + "  FROM " + TABLE_DATE_OFFLINE_DESCRIPT + " WHERE " + KEY_DATES
                + " = " + "'" + date + "'", null);
    }

    public void updateReadStatusNotif(String status) {
        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_STATUS_NOTIF, status);
        db.update(TABLE_INAPPNOTIFICATION, cv, null, null);
        closeDb(db);
    }

    public void deleteNotifItem(int position) {
        SQLiteDatabase db = getWritableDb();
        Log.e("DELETE", "deleteNotifItem: " + position);
        db.delete(TABLE_INAPPNOTIFICATION, KEY_INAPPNOT_ID + "=?", new String[]{String.valueOf(position)});
        closeDb(db);
    }


    //insert user details in user_detail table
    public void insertLeaderboardDetail(String emp_id, String emp_name, String empPic, String tc,
                                        String pc, String sales, String date) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_EMP_ID, emp_id);
        contentValues.put(KEY_EMP_NAME, emp_name);
        contentValues.put(KEY_EMP_PIC_URL, empPic);
        contentValues.put(KEY_TC, tc);
        contentValues.put(KEY_PC, pc);
        contentValues.put(KEY_SALES, sales);
        contentValues.put(KEY_RECORD_DATE, date);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_LEADERBOARD, null, contentValues);

        closeDb(db);
    }

    public boolean deleteLeaderboardDetail() {

        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_LEADERBOARD, null, null) > 0;

        closeDb(db);
        return flag;
    }

    public void deleteLeaderboardDetail2(String date) {

        SQLiteDatabase db = getWritableDb();
        /*boolean flag =*/
        db.delete(TABLE_LEADERBOARD, KEY_RECORD_DATE + "<>?", new String[]{date}) /*> 0*/;

        closeDb(db);
        //return flag;
    }

    public Cursor getLeaderboardDetails() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_LEADERBOARD, new String[]{}, null, null,
                null, null, null);
    }

    //insert user details in user_detail table
    public void insertPrimarySale(String saleAch, String saleT) {

        SQLiteDatabase db = getWritableDb();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SALE_ACH, saleAch);
        contentValues.put(KEY_SALE_TARGET, saleT);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_EMP_PRIMARY_SALE, null, contentValues);

        closeDb(db);

    }


    //insert user details in user_detail table
    public void insertSecondarySale(String saleAch, String saleT) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SALE_ACH, saleAch);
        contentValues.put(KEY_SALE_TARGET, saleT);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_EMP_SECONDARY_SALE, null, contentValues);

        closeDb(db);

    }


    public boolean deletePrimarySale() {
        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_EMP_PRIMARY_SALE, null, null) > 0;

        closeDb(db);
        return flag;
    }

    public boolean deleteSecondarySale() {

        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_EMP_SECONDARY_SALE, null, null) > 0;

        closeDb(db);
        return flag;
    }

    public Cursor getEmpPrimarySale() {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_EMP_PRIMARY_SALE, new String[]{}, null, null,
                null, null, null);
    }

    public Cursor getEmpSecondarySale() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_EMP_SECONDARY_SALE, new String[]{}, null, null,
                null, null, null);
    }

    //insert user details in user_detail table
    public void insertEmpKraDetail(String tcA, String pcA, String tcT, String pcT,
                                   String date) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_KRA_TC_ACHIEVEMENT, tcA);
        contentValues.put(KEY_KRA_PC_ACHIEVEMENT, pcA);
        contentValues.put(KEY_KRA_TC_TARGET, tcT);
        contentValues.put(KEY_KRA_PC_TARGET, pcT);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());
        contentValues.put(KEY_RECORD_DATE, date);

        db.insertOrThrow(TABLE_EMP_KRA_DETAILS, null, contentValues);

        closeDb(db);

    }

    public void deleteEmpKraDetails() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_EMP_KRA_DETAILS, null, null);

        closeDb(db);
    }

    public void deleteEmpKraDetails2(String date) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_EMP_KRA_DETAILS, KEY_RECORD_DATE + "=?", new String[]{date});

        closeDb(db);
    }

    public Cursor getEmpKraDetails() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_EMP_KRA_DETAILS, new String[]{}, null, null,
                null, null, null);
    }


    //insert user details in user_detail table
    public void insertCampaignDetail(String img, String content) {


        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CAMPAIGN_IMG, img);
        contentValues.put(KEY_CAMPAIGN_CONTENT, content);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_CAMPAIGN, null, contentValues);

        closeDb(db);
    }

    //insert user details in user_detail table
    public void insertDocsDetail(String id, String img, String content, String lastupdatedAt) {


        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DOCS_ID, id);
        contentValues.put(KEY_DOCS_IMG, img);
        contentValues.put(KEY_DOCS_CONTENT, content);
        contentValues.put(KEY_LAST_UPDATED_AT, lastupdatedAt);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_DOCS, null, contentValues);

        closeDb(db);
    }

    //insert user details in user_detail table
    public void updateDocsDetail(String id, String img, String content, String lastupdatedAt) {


        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DOCS_IMG, img);
        contentValues.put(KEY_DOCS_CONTENT, content);
        contentValues.put(KEY_LAST_UPDATED_AT, lastupdatedAt);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.update(TABLE_DOCS, contentValues, KEY_DOCS_ID + "=?", new String[]{id});

        closeDb(db);
    }

    public void deletetCampaign() {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_CAMPAIGN, null, null);
        closeDb(db);
    }


    public void deletetDocs() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DOCS, null, null);
        closeDb(db);
    }

    public void deletetSpecificDocs(String id) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DOCS, KEY_DOCS_ID + "=?", new String[]{id});
        closeDb(db);

    }

    public Cursor gettCampaignDetails() {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_CAMPAIGN, new String[]{}, null, null,
                null, null, null);

    }

    public Cursor gettDocs() {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DOCS, new String[]{}, null, null,
                null, null, null);
    }

    //insert user attendance status and more...in user_attendance_table
    public void insertUserAttendance(String attendance_status, String check_in_time,
                                     String check_out_time, String date, String totalCall, String productiveCall,
                                     String lineSold, String totalWorkingTime, String totalRetailingTime,
                                     String reason, String month, String year) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ATTENDANCE_STATUS, attendance_status);
        contentValues.put(KEY_CHECK_IN_T, check_in_time);
        contentValues.put(KEY_CHECK_OUT_T, check_out_time);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_TOTAL_CALL, totalCall);
        contentValues.put(KEY_PRODUCTIVE_CALL, productiveCall);
        contentValues.put(KEY_LINE_SOLD, lineSold);
        contentValues.put(KEY_TOTAL_WORKING_TIME, totalWorkingTime);
        contentValues.put(KEY_TOTLA_RETAILING_TIME, totalRetailingTime);
        contentValues.put(KEY_REASON, reason);
        contentValues.put(KEY_MONTH, month);
        contentValues.put(KEY_YEAR, year);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_USER_ATTENDANCE, null, contentValues);

        closeDb(db);


    }

    //function to get all record from user_attendance_table
    public Cursor getAllRecordFromUserAttendanceTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_USER_ATTENDANCE, new String[]{KEY_ID_ATTENDANCE, KEY_ATTENDANCE_STATUS,
                        KEY_CHECK_IN_T, KEY_CHECK_OUT_T, KEY_DATE}, null, null, null,
                null, null, null);
    }

    public Cursor getAllRecordFromUserAttendanceTable2(String month, String year) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_USER_ATTENDANCE, new String[]{KEY_ID_ATTENDANCE, KEY_ATTENDANCE_STATUS,
                        KEY_CHECK_IN_T, KEY_CHECK_OUT_T, KEY_DATE}, KEY_MONTH + "=? AND " + KEY_YEAR + "=?",
                new String[]{month, year}, null,
                null, null, null);
    }

    //function to get all record from user_attendance_table
    public Cursor getAllRecordFromUserAttendanceTable2(String date) {

        SQLiteDatabase db = getReadableDb();

        return db.rawQuery("SELECT * FROM " + TABLE_USER_ATTENDANCE + " WHERE " + KEY_DATE
                + " = " + "'" + date + "'", null);
    }

    public Cursor getBeatIdFromRetailer(String rId) {

        SQLiteDatabase db = getReadableDb();

        return db.rawQuery("SELECT * FROM " + TABLE_RETAILERS_LIST + " WHERE " + KEY_RETAILER_ID
                + " = " + "'" + rId + "'", null);
    }

    public Cursor getBeatIdFromBeat(String bId) {

        SQLiteDatabase db = getReadableDb();

        return db.rawQuery("SELECT * FROM " + TABLE_BEAT_LIST + " WHERE " + KEY_BEAT_ID
                + " = " + "'" + bId + "'", null);
    }

    public void deleteUserAttendance() {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_USER_ATTENDANCE, null, null);
        closeDb(db);

    }

    public void deleteUserMonthAttendance(String month, String year) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_USER_ATTENDANCE, KEY_MONTH + "=? AND " + KEY_YEAR + "=?", new String[]{month, year});
        closeDb(db);

    }

    //insert record in town list table
    public void insertTownList(String town_name) {
        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TOWN_NAME, town_name);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_TOWN_LIST, null, contentValues);
        closeDb(db);

    }

    ////delete all record from town list table
    public void deleteAllFromTownList() {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_TOWN_LIST, null, null);
        closeDb(db);

    }

    //search into town list table
    public Cursor searchIntoTownListTable(String searchTerm) {
        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_TOWN_LIST, new String[]{}, KEY_TOWN_NAME + " LIKE ?",
                new String[]{"%" + searchTerm + "%"}, null, null, null);

    }

    //get all data from town list table
    public Cursor getAllRecordFromTownListTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_TOWN_LIST, new String[]{KEY_TOWN_ID, KEY_TOWN_NAME},
                null, null, null, null, null);
    }


    //get all data from inappnotifytable table
    public Cursor getAllRecordFromNotificationTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_INAPPNOTIFICATION, new String[]{},
                null, null, null, null, null);
    }

    //insert record distributor list table
    public void insertDistributorList(String distributor_id, String distributor_name, String town_name,
                                      String phone1, String email1, String type, String address, String district,
                                      String zone, String state, String pincode, String lat, String longt, String gstn) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DISTRIBUTOR_ID, distributor_id);
        contentValues.put(KEY_DISTRIBUTOR_NAME, distributor_name);
        contentValues.put(KEY_TOWN_NAME, town_name);
        contentValues.put(KEY_DISTRIBUTOR_PHONE, phone1);
        contentValues.put(KEY_DISTRIBUTOR_EMAIL, email1);
        contentValues.put(KEY_DISTRIBUTOR_TYPE, type);
        contentValues.put(KEY_DISTRIBUTOR_ADDRESS, address);
        contentValues.put(KEY_DISTRIBUTOR_DISTRICT, district);
        contentValues.put(KEY_DISTRIBUTOR_ZONE, zone);
        contentValues.put(KEY_DISTRIBUTOR_STATE, state);
        contentValues.put(KEY_DISTRIBUTOR_PINCODE, pincode);
        contentValues.put(KEY_RETAILER_LAT, lat);
        contentValues.put(KEY_RETAILER_LONG, longt);
        contentValues.put(KEY_RETAILER_DISTANCE, "0.0");
        contentValues.put(KEY_DISTRIBUTOR_GST, gstn);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_DISTRIBUTOR_LIST, null, contentValues);

        closeDb(db);


    }

    //insert record distributor list table
    public void insertDistributorList2(String distributor_id, String distributor_name, String town_name,
                                       String phone1, String email1, String type, String address, String district,
                                       String zone, String state, String pincode, String lat, String longt,
                                       String gstn, String lastUpdatedAt) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DISTRIBUTOR_ID, distributor_id);
        contentValues.put(KEY_DISTRIBUTOR_NAME, distributor_name);
        contentValues.put(KEY_TOWN_NAME, town_name);
        contentValues.put(KEY_DISTRIBUTOR_PHONE, phone1);
        contentValues.put(KEY_DISTRIBUTOR_EMAIL, email1);
        contentValues.put(KEY_DISTRIBUTOR_TYPE, type);
        contentValues.put(KEY_DISTRIBUTOR_ADDRESS, address);
        contentValues.put(KEY_DISTRIBUTOR_DISTRICT, district);
        contentValues.put(KEY_DISTRIBUTOR_ZONE, zone);
        contentValues.put(KEY_DISTRIBUTOR_STATE, state);
        contentValues.put(KEY_DISTRIBUTOR_PINCODE, pincode);
        contentValues.put(KEY_RETAILER_LAT, lat);
        contentValues.put(KEY_RETAILER_LONG, longt);
        contentValues.put(KEY_RETAILER_DISTANCE, "0.0");
        contentValues.put(KEY_DISTRIBUTOR_GST, gstn);
        contentValues.put(KEY_LAST_UPDATED_AT, lastUpdatedAt);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_DISTRIBUTOR_LIST, null, contentValues);


        closeDb(db);

    }

    public Cursor getDistributorName(String did) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DISTRIBUTOR_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);
    }

    //update in distributor list table
    public boolean updateDistributorTable(String did, String mobilenumber) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();

        cv.put(KEY_DISTRIBUTOR_PHONE, mobilenumber);

        return db.update(TABLE_DISTRIBUTOR_LIST, cv, KEY_DISTRIBUTOR_ID + "= ?", new String[]{did}) > 0;
    }

    //get all data from distributor list table
    public Cursor getAllDataFromDistributorListTable(String town_name) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DISTRIBUTOR_LIST, new String[]{}, null/*KEY_TOWN_NAME + "=?"*/,
                null /*new String[]{town_name}*/, null, null, null);
    }

//    //get all data from distributor list table
//    public Cursor getAllDataFromDistributorListTable2(String rid) {
//        SQLiteDatabase db = getReadableDb();
//        return db.query(TABLE_DISTRIBUTOR_LIST, new String[]{KEY_DISTRIBUTOR_ID, KEY_DISTRIBUTOR_NAME},
//                KEY_RETAILER_ID + "=?", new String[]{rid}, null, null, null);
//    }

    //get all data from distributor list table
    public void deleteAllDataFromDistributorListTable() {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DISTRIBUTOR_LIST, null, null);

        closeDb(db);

    }

    //get all data from distributor list table
    public void deleteDistributor(String did) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DISTRIBUTOR_LIST, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did});

        closeDb(db);

    }

    //get all data from distributor list table
    public Cursor getDistributor(String did) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DISTRIBUTOR_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);
    }


    //search into town list table
    public Cursor searchIntoDistributorListTable(String searchTerm) {
        SQLiteDatabase db = getReadableDb();

        String query = "Select * from " + TABLE_DISTRIBUTOR_LIST + " where " + KEY_DISTRIBUTOR_NAME + " like " + "'%" + searchTerm + "%'";

        return db.rawQuery(query, null);
    }

    //insert record sku details table
    public void insertSkuDetailsTable(String sku_id, String sku_name, String brand_name, String brand_price,String brand_weight,
                                      String brand_unit, String cFactor,String imgStr) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SKU_ID, sku_id);
        contentValues.put(KEY_SKU_NAME, sku_name);
        contentValues.put(KEY_SKU_BRAND_NAME, brand_name);
        contentValues.put(KEY_SKU_BRAND_PRICE, brand_price);
        contentValues.put(KEY_SKU_BRAND_WEIGHT, brand_weight);
        contentValues.put(KEY_SKU_BRAND_UNIT, brand_unit);
        contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
        contentValues.put(KEY_SKU_IMAGE, imgStr);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_SKU_DETAILS, null, contentValues);

        closeDb(db);

    }

    //get all data from sku details table
    public Cursor getAllDataFromSkuDetailsTable() {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_DETAILS, new String[]{}, null, null,
                null, null, "brand_name");
//                null, null, "brand_unit");
//                null, null, "brand_name,brand_price"); //@Rinkesh
    }

    //delete all data from sku details table
    public void deleteAllDataFromSkuDetailsTable() {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_SKU_DETAILS, null, null);

        closeDb(db);

    }

    //get all data from sku details table
    public Cursor getDataByUnitFromSkuDetailsTable(String unit) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_DETAILS, new String[]{}, KEY_SKU_BRAND_UNIT + "=?",
                new String[]{unit}, null, null, null);
    }

    //get all data from sku details table
    public Cursor getDataBrandFromSkuDetailsTable(String brandId) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_DETAILS, new String[]{}, KEY_SKU_BRAND_NAME + "=?",
                new String[]{brandId}, null, null, null);
    }

    //get all data from sku details table
    public Cursor getDataPriceFromSkuDetailsTable(String op, String price) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_DETAILS, new String[]{}, KEY_SKU_BRAND_PRICE + op + "?",
                new String[]{price}, null, null, null);
    }


    //insert record sku entry list table
    public void insertSkuEntryListTable(String sku_id, String sku_name, String brand_name, String brand_price,
                                        String brand_qty, String brand_unit, String timeAt, String did,
                                        String date, String serverStatus, String from, String lat, String longt, String type) {

        SQLiteDatabase db = getWritableDb();

        if (from.equalsIgnoreCase("stock")) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SKU_ID_L, sku_id);
            contentValues.put(KEY_SKU_NAME_L, sku_name);
            contentValues.put(KEY_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_ENTRY_TIME, timeAt);
            contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_SKU_ENTRY_TYPE, from);
            contentValues.put(KEY_SKU_STOCK_TYPE, type);
            contentValues.put(KEY_SKU_ENTRY_DATE, date);
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            contentValues.put(KEY_SKU_ENTRY_LAT, lat);
            contentValues.put(KEY_SKU_ENTRY_LONGT, longt);
            contentValues.put(KEY_RECORD_DATE, date);
            contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

            db.insertOrThrow(TABLE_SKU_ENTRY_LIST, null, contentValues);
            closeDb(db);

        } else if (from.equalsIgnoreCase("closing")) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SKU_ID_L, sku_id);
            contentValues.put(KEY_SKU_NAME_L, sku_name);
            contentValues.put(KEY_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_ENTRY_TIME, timeAt);
            contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_SKU_ENTRY_TYPE, from);
            contentValues.put(KEY_SKU_STOCK_TYPE, type);
            contentValues.put(KEY_SKU_ENTRY_DATE, date);
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            contentValues.put(KEY_SKU_ENTRY_LAT, lat);
            contentValues.put(KEY_SKU_ENTRY_LONGT, longt);
            contentValues.put(KEY_RECORD_DATE, date);
            contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

            db.insertOrThrow(TABLE_SKU_CLOSING_ENTRY_LIST, null, contentValues);
            closeDb(db);


        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_DISTRIBUTOR_ORDER_ID_L, sku_id);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_NAME_L, sku_name);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_L, brand_qty);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_ENTRY_TIME, timeAt);
            contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_SKU_ENTRY_TYPE, from);
            contentValues.put(KEY_DISTRIBUTOR_ORDE_TYPE, type);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_ENTRY_DATE, date);
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_ENTRY_LAT, lat);
            contentValues.put(KEY_DISTRIBUTOR_ORDER_ENTRY_LONGT, longt);
            contentValues.put(KEY_RECORD_DATE, date);
            contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

            db.insertOrThrow(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, null, contentValues);

            closeDb(db);
        }
    }

    //get all data from sku entry list table
    public Cursor getAllDataFromSkuEntryListTable(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_SKU_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=? AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{did, "fail"}, null, null, null);
    }

    //get all data from Closing entry list table
    public Cursor getAllDataFromClosingEntryListTable(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_SKU_CLOSING_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=? AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{did, "fail"}, null, null, null);
    }


    //get all data from sku entry list table
    public Cursor getAllDataFromSkuEntryListTable2(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_SKU_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);
    }


    //get all data from Closing entry list table
    public Cursor getAllDataFromClosingEntryListTable2(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_SKU_CLOSING_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);
    }


    //get all data from sku entry list table
    public Cursor getSpecificDataFromSkuEntryListTable(String did, String status) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, new String[]{},
                KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{did, status}, null, null, null);
    }

    //get all data from sku entry list table
    public Cursor getSpecificDataFromSkuEntryListTable2(String did) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);

    }

    //get all data from Closing entry list table
    public Cursor getSpecificDataFromClosingEntryListTable2(String did) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_SKU_CLOSING_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);

    }


    //get all data from sku entry list table
    public Cursor getAllDataFromSkuEntryListTable1(String did, String skuID, String from) {

        SQLiteDatabase db = getReadableDb();
        Cursor cursor;
        if (from.equalsIgnoreCase("stock")) {

            cursor = db.query(TABLE_SKU_ENTRY_LIST, new String[]{},
                    KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_SKU_ID_L + "=?", new String[]{did, skuID},
                    null, null, null);

        } else if (from.equalsIgnoreCase("closing")) {

            cursor = db.query(TABLE_SKU_CLOSING_ENTRY_LIST, new String[]{},
                    KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_SKU_ID_L + "=?", new String[]{did, skuID},
                    null, null, null);

        } else {

            cursor = db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, new String[]{},
                    KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ORDER_ID_L + "=?", new String[]{did, skuID},
                    null, null, null);
        }

        return cursor;
    }

    public Cursor getSpecificDataFromDisOrderEntryListTable2(String did) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}, null, null, null);
    }

    public void deleteSpecificDataFromSkuEntryListTable(String did, String from) {

        SQLiteDatabase db = getWritableDb();

        if (from.equalsIgnoreCase("stock"))
            db.delete(TABLE_SKU_ENTRY_LIST, KEY_DISTRIBUTOR_ID + "=?", new String[]{did});
        else
            db.delete(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, KEY_DISTRIBUTOR_ID + "=?", new String[]{did});

        closeDb(db);
    }

    public void deleteAllDataFromSkuEntryListTable() {

        SQLiteDatabase db = getWritableDb();
        /*boolean flag = */
        db.delete(TABLE_SKU_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"})/* > 0*/;

        closeDb(db);
        //return flag;
    }

    public void deleteAllDataFromClosingEntryListTable() {

        SQLiteDatabase db = getWritableDb();
        /*boolean flag = */
        db.delete(TABLE_SKU_CLOSING_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"})/* > 0*/;

        closeDb(db);
        //return flag;
    }

    public void deleteAllDataFromSkuEntryListTable2() {
        SQLiteDatabase db = getWritableDb();
        db.execSQL("delete from " + TABLE_SKU_ENTRY_LIST);
        closeDb(db);
    }

    public void deleteAllDataFromSkuEntryListTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_SKU_ENTRY_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteAllDataFromClosingEntryListTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_SKU_CLOSING_ENTRY_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteAllDataFromSkuEntryListTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_SKU_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});

        Log.e("Database", "===>Called");

        closeDb(db);
    }


    public void deleteAllDataFromClosingEntryListTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_SKU_CLOSING_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});

        Log.e("Database", "===>Called");

        closeDb(db);
    }

    public boolean updateDataInSkuEntryListTable(String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SERVER_SUBMIT_STATUS, status);

        boolean val = db.update(TABLE_SKU_ENTRY_LIST, cv, KEY_DISTRIBUTOR_ID + "=?", new String[]{did}) > 0;

        closeDb(db);
        return val;
    }


    public boolean updateDataInClosingEntryListTable(String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SERVER_SUBMIT_STATUS, status);

        boolean val = db.update(TABLE_SKU_CLOSING_ENTRY_LIST, cv, KEY_DISTRIBUTOR_ID + "=?", new String[]{did}) > 0;

        closeDb(db);
        return val;
    }

    public boolean updateDataInOrderEntryListTable(String did, String status) {
        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SERVER_SUBMIT_STATUS, status);

        boolean val = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY_LIST, cv, KEY_DISTRIBUTOR_ID + "=?",
                new String[]{did}) > 0;

        closeDb(db);
        return val;

    }

    //insert record order entry list table
    public void insertOrderEntryListTable(String sku_id, String sku_name, String brand_name, String brand_price,
                                          String brand_qty, String brand_unit, String cFactor, String rid, String did,
                                          String date, String transId, String recordDate) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ORDER_SKU_ID_L, sku_id);
        contentValues.put(KEY_ORDER_SKU_NAME_L, sku_name);
        contentValues.put(KEY_ORDER_SKU_BRAND_NAME_L, brand_name);
        contentValues.put(KEY_ORDER_SKU_BRAND_PRICE_L, brand_price);
        contentValues.put(KEY_ORDER_SKU_BRAND_QTY_L, brand_qty);
        contentValues.put(KEY_ORDER_SKU_BRAND_UNIT_L, brand_unit);
        contentValues.put(KEY_RETAILER_ID, rid);
        contentValues.put(KEY_DISTRIBUTOR_ID, did);
        contentValues.put(KEY_ORDER_SKU_ENTRY_DATE_L, date);
        contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(TRANSACTION_ID, transId);
        contentValues.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_ORDER_ENTRY_LIST, null, contentValues);
        closeDb(db);

    }

    //insert record order entry list table
    public boolean updateOrderEntryListTable(String rid, String did, String status) {

        Calendar cal = Calendar.getInstance();
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        SQLiteDatabase db = getWritableDb();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ORDER_SKU_ID_L, "-");
        contentValues.put(KEY_ORDER_SKU_NAME_L, "-");
        contentValues.put(KEY_ORDER_SKU_BRAND_NAME_L, "-");
        contentValues.put(KEY_ORDER_SKU_BRAND_PRICE_L, "-");
        contentValues.put(KEY_ORDER_SKU_BRAND_QTY_L, "-");
        contentValues.put(KEY_ORDER_SKU_BRAND_UNIT_L, "-");
        contentValues.put(KEY_ORDER_SKU_ENTRY_DATE_L, date);
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, status);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_ORDER_ENTRY_LIST, contentValues,
                KEY_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID + "=?" /*+ " AND " + KEY_ORDER_SKU_ID_L + "=?"*/,
                new String[]{rid, did}) > 0;

        closeDb(db);
        return val;
    }


    public void deleteSpecificRecordFromOrderEntryListTable(String rid, String did/*, String skuId*/) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_ORDER_ENTRY_LIST,
                KEY_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID + "=?" /*+ " AND " + KEY_ORDER_SKU_ID_L + "=?"*/,
                new String[]{rid, did/*, skuId*/});
        closeDb(db);
    }

    //get Specific data from order entry list table
    public Cursor getSpecificDataFromOrderEntryListTable22(String rid, String did) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_ENTRY_LIST, new String[]{}, KEY_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID + "=?",
                new String[]{rid, did}, null, null, null);
    }


    //get Specific data from order entry list table
    public Cursor getSpecificDataFromOrderEntryListTable(String rid, String did/*, String status*/) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_ENTRY_LIST, new String[]{}, KEY_RETAILER_ID + "=?"
                        + " AND " + KEY_DISTRIBUTOR_ID + "=?"/* + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?"*/,
                new String[]{rid, did/*, status*/}, null, null, null);
    }

//    public Cursor getSpecificDataFromOrderEntryListTableIf(String rid, String did) {
//
//        SQLiteDatabase db = getReadableDb();
//        return db.query(TABLE_ORDER_ENTRY_LIST, new String[]{}, KEY_RETAILER_ID + "=?"
//                        + " AND " + KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
//                new String[]{rid, did}, null, null, null);
//    }

    //get Specific data from order entry list table
    public Cursor getSpecificDataFromOrderEntryListTable2(String did, String rid, String skuId) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_ENTRY_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_RETAILER_ID + "=?" + " AND " + KEY_ORDER_SKU_ID_L + "=?",
                new String[]{did, rid, skuId}, null, null, null);
    }

    public boolean deleteSpecificDataFromOrderEntryListTable() {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_ORDER_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public void deleteSpecificDataFromOrderEntryListTable2() {
//        SQLiteDatabase db = getWritableDb();
//
//        db.execSQL("delete from " + TABLE_ORDER_ENTRY_LIST);
//        closeDb(db);
//    }

    public void deleteSpecificDataFromOrderEntryListTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_ENTRY_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteSpecificDataFromOrderEntryListTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});

        closeDb(db);
    }

    public boolean updateSpecificDataInOrderEntryListTable(String rid, String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SERVER_SUBMIT_STATUS, status);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_ORDER_ENTRY_LIST, cv, KEY_RETAILER_ID + "=? AND " + KEY_DISTRIBUTOR_ID + "=?",
                new String[]{rid, did}) > 0;

        closeDb(db);
        return val;
    }

    //insert record new order entry list table
    public void insertNewOrderEntryListTable(String sku_id, String sku_name, String brand_name, String brand_price,
                                             String brand_qty, String brand_unit, String cFactor, String nrid, String did,
                                             String date, String transId, String recordDate) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
        contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
        contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
        contentValues.put(KEY_NEW_RETAILER_ID, nrid);
        contentValues.put(KEY_NEW_RETAILER_TEMP_IDD, nrid);
        contentValues.put(KEY_DISTRIBUTOR_ID, did);
        contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
        contentValues.put(KEY_NEW_ORDER_STATUS, "fail");
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(TRANSACTION_ID, transId);
        contentValues.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_NEW_ORDER_ENTRY_LIST, null, contentValues);
        closeDb(db);

    }

    //insert record new order entry list table
    public void insertPreferredOrderEntryListTable(String sku_id, String sku_name, String brand_name, String brand_price,
                                                   String brand_qty, String brand_unit, String cFactor, String nrid, String did,
                                                   String date, String transId, String recordDate) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
        contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
        contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
        contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_ID, nrid);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_TEMP_ID, nrid);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID, did);
        contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
        contentValues.put(KEY_PREFERRED_ORDER_STATUS, "fail");
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(TRANSACTION_ID, transId);
        contentValues.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_PREFERRED_ORDER_ENTRY_LIST, null, contentValues);
        closeDb(db);

    }

    //insert record new order entry list table
    public void insertNewOrderEntryListTable2(String sku_id, String sku_name, String brand_name, String brand_price,
                                              String brand_qty, String brand_unit, String cFactor, String nrid, String did,
                                              String date, String transId, String recordDate) {

        Cursor cursor = getSpecificDataFromNewOrderEntryListTable2(nrid, did, sku_id);
        SQLiteDatabase db = getWritableDb();

        if (cursor != null && cursor.getCount() > 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
            contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
//        contentValues.put(KEY_NEW_RETAILER_ID, nrid);
//        contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
            contentValues.put(KEY_NEW_ORDER_STATUS, "fail");
            // contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
            contentValues.put(TRANSACTION_ID, transId);
            contentValues.put(KEY_RECORD_DATE, recordDate);

            db.update(TABLE_NEW_ORDER_ENTRY_LIST, contentValues, KEY_NEW_RETAILER_ID + "=?" + " AND "
                    + KEY_DISTRIBUTOR_ID + "=?" + " AND " + KEY_NEW_ORDER_SKU_ID_L + "=?", new String[]{nrid, did, sku_id});

        } else {

            SQLiteDatabase dbP = getReadableDb();
            @SuppressLint("Recycle") Cursor cursorPreRet = dbP.query(TABLE_NEW_RETAILERS_LIST, new String[]{}, KEY_NEW_RETAILER_IDD + "=?" + " AND "
                    + KEY_DISTRIBUTOR_ID + "=?", new String[]{nrid, did}, null, null, null);

            String tempId = "", serverStatus = "";
            if (cursorPreRet != null && cursorPreRet.getCount() > 0 && cursorPreRet.moveToFirst()) {
                tempId = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_NEW_RETAILER_TEMP_IDD));
                serverStatus = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_SERVER_SUBMIT_STATUS));
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
            contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
            contentValues.put(KEY_NEW_RETAILER_ID, nrid);
            contentValues.put(KEY_NEW_RETAILER_TEMP_IDD, tempId);
            contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
            contentValues.put(KEY_NEW_ORDER_STATUS, "fail");
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            contentValues.put(TRANSACTION_ID, transId);
            contentValues.put(KEY_RECORD_DATE, recordDate);
            db.insertOrThrow(TABLE_NEW_ORDER_ENTRY_LIST, null, contentValues);
        }

        closeDb(db);

    }

    //insert record new order entry list table
    public void insertPreferredOrderEntryListTable2(String sku_id, String sku_name, String brand_name, String brand_price,
                                                    String brand_qty, String brand_unit, String cFactor, String nrid, String did,
                                                    String date, String transId, String recordDate) {

        Cursor cursor = getSpecificDataFromPreferredOrderEntryListTable2(nrid, did, sku_id);
        SQLiteDatabase db = getWritableDb();

        SQLiteDatabase dbP = getReadableDb();
        @SuppressLint("Recycle") Cursor cursorPreRet = dbP.query(TABLE_NEW_PREFERRED_RETAILERS_LIST, new String[]{}, KEY_NEW_PREFERRED_RETAILER_ID + "=?" + " AND "
                + KEY_PREFERRED_ORDER_PLACED_BY_DID + "=?", new String[]{nrid, did}, null, null, null);

        String tempId = "", serverStatus = "";
        Log.e("SalesBeatDb", " --->" + cursorPreRet.getCount());
        if (cursorPreRet != null && cursorPreRet.getCount() > 0 && cursorPreRet.moveToFirst()) {
            tempId = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_NEW_PREFERRED_RETAILER_TEMP_ID));
            serverStatus = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_SERVER_SUBMIT_STATUS));
            Log.e("SalesBeatDb", " --->" + serverStatus + " " + tempId + " " + nrid);
        }

        Log.e("SalesBeatDb", " --->" + serverStatus + " " + tempId + " " + nrid);


        if (cursor != null && cursor.getCount() > 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
            contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
//        contentValues.put(KEY_NEW_RETAILER_ID, nrid);
//        contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
            contentValues.put(KEY_PREFERRED_ORDER_STATUS, "fail");
//        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
            contentValues.put(TRANSACTION_ID, transId);
            contentValues.put(KEY_RECORD_DATE, recordDate);

            db.update(TABLE_PREFERRED_ORDER_ENTRY_LIST, contentValues, KEY_NEW_PREFERRED_RETAILER_ID + "=?" + " AND "
                    + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "=?" + " AND " + KEY_NEW_ORDER_SKU_ID_L + "=?", new String[]{nrid, did, sku_id});

        } else {

            Log.e("SalesBeatDb", " --->" + serverStatus + " " + tempId + " " + nrid);

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NEW_ORDER_SKU_ID_L, sku_id);
            contentValues.put(KEY_NEW_ORDER_SKU_NAME_L, sku_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_NAME_L, brand_name);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_PRICE_L, brand_price);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_QTY_L, brand_qty);
            contentValues.put(KEY_NEW_ORDER_SKU_BRAND_UNIT_L, brand_unit);
            contentValues.put(KEY_SKU_CONVERSION_FACTOR, cFactor);
            contentValues.put(KEY_NEW_PREFERRED_RETAILER_ID, nrid);
            contentValues.put(KEY_NEW_PREFERRED_RETAILER_TEMP_ID, tempId);
            contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_NEW_ORDER_ENTRY_DATE, date);
            contentValues.put(KEY_PREFERRED_ORDER_STATUS, "fail");
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            contentValues.put(TRANSACTION_ID, transId);
            contentValues.put(KEY_RECORD_DATE, recordDate);
            db.insertOrThrow(TABLE_PREFERRED_ORDER_ENTRY_LIST, null, contentValues);
        }

        closeDb(db);

    }


    public void deleteSpecificRecordFromNewOrderEntryListTable(String nrid, String did) {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_NEW_ORDER_ENTRY_LIST,
                KEY_NEW_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID + "=?" /*+ " AND " + KEY_ORDER_SKU_ID_L + "=?"*/,
                new String[]{nrid, did/*, skuId*/});
        closeDb(db);

    }

    //get data from new order entry list table
    public Cursor getSpecificDataFromNewOrderEntryListTable22(String nrid, String new_did) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_ORDER_ENTRY_LIST, new String[]{}, KEY_NEW_RETAILER_ID + "=?"
                + " AND " + KEY_DISTRIBUTOR_ID + "=?", new String[]{nrid, new_did}, null, null, null);

    }

    //get data from new order entry list table
    public Cursor getSpecificDataFromNewOrderEntryListTable2(String nrid, String did, String skuId) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_ORDER_ENTRY_LIST, new String[]{}, KEY_NEW_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID +
                "=?" + " AND " + KEY_NEW_ORDER_SKU_ID_L + "=?", new String[]{nrid, did, skuId}, null, null, null);
    }

    //get data from new order entry list table
    public Cursor getSpecificDataFromPreferredOrderEntryListTable2(String nrid, String did, String skuId) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_PREFERRED_ORDER_ENTRY_LIST, new String[]{}, KEY_NEW_PREFERRED_RETAILER_ID
                + "=?" + " AND " + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "=?" + " AND " + KEY_NEW_ORDER_SKU_ID_L
                + "=?", new String[]{nrid, did, skuId}, null, null, null);
    }

    //get data from new order entry list table
    public Cursor getSpecificDataFromNewOrderEntryListTable(String nrid, String did
                                                           /* ,String serverStatus, String orderStatus*/) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_ORDER_ENTRY_LIST, new String[]{},
                KEY_NEW_RETAILER_ID + "=?" + " AND " + KEY_DISTRIBUTOR_ID + "=?",
                new String[]{nrid, did}, null, null, null);
    }

    //get data from new order entry list table
    public Cursor getSpecificDataFromPreferredOrderEntryListTable(String nrid, String did, String serverStatus, String orderStatus) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_PREFERRED_ORDER_ENTRY_LIST, new String[]{}, KEY_NEW_PREFERRED_RETAILER_ID
                        + "=?" + " AND " + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "=?",
                new String[]{nrid, did}, null, null, null);
    }

    public boolean deleteSpecificDataFromNewOrderEntryListTable() {
        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_NEW_ORDER_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public void deleteSpecificDataFromNewOrderEntryListTable2() {
//        SQLiteDatabase db = getWritableDb();
//        db.execSQL("delete from " + TABLE_NEW_ORDER_ENTRY_LIST);
//        closeDb(db);
//    }

    public void deleteSpecificDataFromNewOrderEntryListTable3(String date) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_ORDER_ENTRY_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteSpecificDataFromNewOrderEntryListTable4(String date) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_ORDER_ENTRY_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});
        closeDb(db);
    }
    public void deleteSpecificDataFromNewOrderEntryListTable5(String nrid,String did) { //@Umesh 20221122
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_NEW_ORDER_ENTRY_LIST,KEY_NEW_RETAILER_ID + "=?" + " AND "
                + KEY_DISTRIBUTOR_ID + "=?", new String[]{nrid, did});
        closeDb(db);
    }

    public boolean updateIdDataInNewOrderEntryListTable(String tempID, String nrid) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NEW_RETAILER_ID, nrid);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_NEW_ORDER_ENTRY_LIST, cv, KEY_NEW_RETAILER_ID + "=?", new String[]{tempID}) > 0;

        closeDb(db);
        return val;
    }

    public boolean updateSpecificDataInNewOrderEntryListTable(String nrid, String did, String orderStatus) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NEW_ORDER_STATUS, orderStatus);
        //cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_NEW_ORDER_ENTRY_LIST, cv, KEY_NEW_RETAILER_ID + "=? AND "
                + KEY_DISTRIBUTOR_ID + "=?", new String[]{nrid, did}) > 0;

        closeDb(db);
        return flag;
    }

    //insert record sku id table
    public void insertSkuIdTable(String sku_id, String distributor_id) {
        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SKU_ID_S, sku_id);
        contentValues.put(KEY_DISTRIBUTOR_ID_S, distributor_id);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_SKU_ID, null, contentValues);
        closeDb(db);
    }

    //get all data from sku id table
//    public Cursor getAllDataFromSkuIdTable(String distributor_id) {
//
//        SQLiteDatabase db = getReadableDb();
//        return db.query(TABLE_SKU_ID, new String[]{}, KEY_DISTRIBUTOR_ID_S + "=?",
//                new String[]{distributor_id}, null, null, null);
//    }

    //delete all data from sku id table
    public void deleteAllDataFromSkuIdTable(String distributor_id) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_SKU_ID, KEY_DISTRIBUTOR_ID_S + "=?", new String[]{distributor_id});
        closeDb(db);
    }

    //insert record beat list table
    public void insertBeatList(String beat_id, String beat_name, String distributor_id,String isRange) {
        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_BEAT_ID, beat_id);
        contentValues.put(KEY_BEAT_NAME, beat_name);
        contentValues.put(KEY_BEAT_RANGE, isRange);
        contentValues.put(KEY_BEAT_VISITED, "no");
        contentValues.put(KEY_BEAT_RANGE, "no");
        contentValues.put(KEY_BEAT_VISITED_TIME, "00:00");
        contentValues.put(KEY_DISTRIBUTOR_ID, distributor_id);
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_BEAT_LIST, null, contentValues);
        closeDb(db);
    }

    //insert record beat list table
    public void insertBeatList2(String beat_id, String beat_name, String distributor_id, String updatedAt,String isRange) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = getWritableDb();
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_BEAT_ID, beat_id);
                contentValues.put(KEY_BEAT_NAME, beat_name);
                contentValues.put(KEY_BEAT_VISITED, "no");
                contentValues.put(KEY_BEAT_VISITED_TIME, "00:00");
                contentValues.put(KEY_DISTRIBUTOR_ID, distributor_id);
                contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
                contentValues.put(KEY_LAST_UPDATED_AT, updatedAt);
                contentValues.put(KEY_BEAT_RANGE, isRange);
                contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

                db.insertOrThrow(TABLE_BEAT_LIST, null, contentValues);
            } catch (SQLiteException e) {
                Log.e("DatabaseError", "Error inserting data: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (db != null && db.isOpen()) {
                    closeDb(db);
                }
            }
        });
    }

    //insert record beat list table
//    public void updateBeatList(String beat_id, String distributor_id, String checkInTimeStamp, String lat, String longt) {
//        SQLiteDatabase db = getWritableDb();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(KEY_BEAT_VISITED, "yes");
//        contentValues.put(KEY_BEAT_VISITED_LAT, lat);
//        contentValues.put(KEY_BEAT_VISITED_LONGT, longt);
//        contentValues.put(KEY_BEAT_VISITED_TIME, checkInTimeStamp);
//        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());
//
//        db.update(TABLE_BEAT_LIST, contentValues, KEY_BEAT_ID + " =? AND " + KEY_DISTRIBUTOR_ID + " =?",
//                new String[]{beat_id, distributor_id});
//        closeDb(db);
//
//    }

    public void insertBeatVisited(String beat_id, String distributor_id, String checkInTimeStamp, String lat, String longt)
            throws SQLiteConstraintException {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_BEAT_ID, beat_id);
        contentValues.put(KEY_DISTRIBUTOR_ID, distributor_id);
        contentValues.put(KEY_BEAT_VISITED, "yes");
        contentValues.put(KEY_BEAT_VISITED_LAT, lat);
        contentValues.put(KEY_BEAT_VISITED_LONGT, longt);
        contentValues.put(KEY_BEAT_VISITED_TIME, checkInTimeStamp);
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") +
                "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_BEAT_VISITED_LIST, null, contentValues);
        closeDb(db);

    }

    //insert record beat list table
    public boolean updateBeatVisited(String beat_id, String distributor_id, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "success");

        boolean val = db.update(TABLE_BEAT_VISITED_LIST, contentValues, KEY_BEAT_ID +
                " =? AND " + KEY_DISTRIBUTOR_ID + " =?", new String[]{beat_id, distributor_id}) > 0;

        closeDb(db);
        return val;
    }

    public Cursor getAllVisitedBeat() {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_BEAT_VISITED_LIST, new String[]{}, KEY_BEAT_VISITED +
                        "=? AND " + KEY_SERVER_SUBMIT_STATUS + "=?", new String[]{"yes", "fail"},
                null, null, null);
    }

    public void deleteAllVisitedBeat(String date) {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_BEAT_VISITED_LIST, /*KEY_RECORD_DATE + "<>? AND " +*/ KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{/*date,*/ "success"});

        closeDb(db);
    }

    public Cursor getBeatList(String beat_id, String distributor_id) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_BEAT_LIST, new String[]{}, KEY_BEAT_ID + " =? AND " +
                        KEY_BEAT_VISITED + " =? AND " + KEY_DISTRIBUTOR_ID + " =?",
                new String[]{beat_id, "yes", distributor_id}, null, null, null);
    }
//
//    //insert record beat list table
//    public boolean updateBeatList2(String beat_id, String distributor_id, String status) {
//        SQLiteDatabase db = getWritableDb();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "success");
//        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());
//
//        boolean val = db.update(TABLE_BEAT_LIST, contentValues, KEY_BEAT_ID + " =? AND " + KEY_DISTRIBUTOR_ID + " =?",
//                new String[]{beat_id, distributor_id}) > 0;
//
//        closeDb(db);
//        return val;
//    }
//
//    //get all data from beat list table
//    public Cursor getAllDataFromBeatListTable22() {
//        SQLiteDatabase db = getReadableDb();
//        return db.query(TABLE_BEAT_LIST, new String[]{}, KEY_BEAT_VISITED + "=? AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
//                new String[]{"yes", "fail"}, null, null, null);
//    }
//
//    //get all data from beat list table
//    public Cursor getAllDataFromBeatListTable(String distributor_id) {
//        SQLiteDatabase db = getReadableDb();
//        return db.query(TABLE_BEAT_LIST, new String[]{}, KEY_DISTRIBUTOR_ID + "=?",
//                new String[]{distributor_id}, null, null, null);
//    }

    //get all data from beat list table
    public Cursor getAllDataFromBeatListTable2(String beat_id) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_BEAT_LIST, new String[]{}, KEY_BEAT_ID + "=?", new String[]{beat_id},
                null, null, null);
    }

    //delete all data from beat list table
    public void deleteDataFromBeatListTable() {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_BEAT_LIST, null, null);
        closeDb(db);
    }

    //delete all data from beat list table
    public void deleteDataFromBeatListTable(String beatId) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_BEAT_LIST, KEY_BEAT_ID + "=?", new String[]{beatId});
        closeDb(db);
    }


    //search into town list table
    public Cursor searchIntoBeatListTable(String searchTerm, String did) {

        SQLiteDatabase db = getReadableDb();
        String query = "Select * from " + TABLE_BEAT_LIST + " where " + KEY_BEAT_NAME + " like " + "'%" + searchTerm + "%'"
                + " AND " + KEY_DISTRIBUTOR_ID + " = " + did + "";

        return db.rawQuery(query, null);
    }

    //insert record retailer list table
    public void insertRetailerList(String retailer_id, String bid, String ruid, String retailer_name,
                                   String retailer_address, String state, String email, String shopPhone,
                                   String ownerName, String owner_phone1, String whatsAppNo, String retailer_lat,
                                   String retailer_long,float distance,String gstin, String pin, String fssai, String district,
                                   String locality, String zone, String target, String outlethannel, String shopType,
                                   String grade, String imageLink, String beat_id) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RETAILER_ID, retailer_id);
        contentValues.put(KEY_BEAT_ID_R, bid);
        contentValues.put(KEY_RETAILER_UNIQUE_ID, ruid);
        contentValues.put(KEY_RETAILER_NAME, retailer_name);
        contentValues.put(KEY_RETAILER_ADDRESS, retailer_address);
        contentValues.put(KEY_RETAILER_STATE, state);
        contentValues.put(KEY_RETAILER_EMAIL, email);
        contentValues.put(KEY_SHOP_PHONE, shopPhone);
        contentValues.put(KEY_OWNER_NAME, ownerName);
        contentValues.put(KEY_OWNER_PHONE, owner_phone1);
        contentValues.put(KEY_WHATSAPP_NO, whatsAppNo);
        contentValues.put(KEY_RETAILER_LAT, retailer_lat);
        contentValues.put(KEY_RETAILER_LONG, retailer_long);
        contentValues.put(KEY_RETAILER_DISTANCE, distance);
        contentValues.put(KEY_RETAILER_GSTIN, gstin);
        contentValues.put(KEY_RETAILER_PIN, pin);
        contentValues.put(KEY_RETAILER_FSSAI, fssai);
        contentValues.put(KEY_DISTRICT, district);
        contentValues.put(KEY_LOCALITY, locality);
        contentValues.put(KEY_ZONE, zone);
        contentValues.put(KEY_TARGET, target);
        contentValues.put(KEY_OUTLET_CHANNEL, outlethannel);
        contentValues.put(KEY_SHOP_TYPE, shopType);
        contentValues.put(KEY_RETAILER_GRADE, grade);
        contentValues.put(KEY_RETAILER_IMAGE, imageLink);
        contentValues.put(KEY_BEAT_ID, beat_id);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_RETAILERS_LIST, null, contentValues);
        closeDb(db);
    }


    //insert record retailer list table
    public void insertRetailerList2(String retailer_id, String bid, String ruid, String retailer_name,
                                    String retailer_address, String state, String email, String shopPhone,
                                    String ownerName, String owner_phone1, String whatsAppNo, String retailer_lat,
                                    String retailer_long,float distance, String gstin, String pin, String fssai, String district,
                                    String locality, String zone, String target, String outlethannel, String shopType,
                                    String grade, String imageLink, String beat_id, String updatedAt) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RETAILER_ID, retailer_id);
        contentValues.put(KEY_BEAT_ID_R, bid);
        contentValues.put(KEY_RETAILER_UNIQUE_ID, ruid);
        contentValues.put(KEY_RETAILER_NAME, retailer_name);
        contentValues.put(KEY_RETAILER_ADDRESS, retailer_address);
        contentValues.put(KEY_RETAILER_STATE, state);
        contentValues.put(KEY_RETAILER_EMAIL, email);
        contentValues.put(KEY_SHOP_PHONE, shopPhone);
        contentValues.put(KEY_OWNER_NAME, ownerName);
        contentValues.put(KEY_OWNER_PHONE, owner_phone1);
        contentValues.put(KEY_WHATSAPP_NO, whatsAppNo);
        contentValues.put(KEY_RETAILER_LAT, retailer_lat);
        contentValues.put(KEY_RETAILER_LONG, retailer_long);
        contentValues.put(KEY_RETAILER_DISTANCE, distance);
        contentValues.put(KEY_RETAILER_GSTIN, gstin);
        contentValues.put(KEY_RETAILER_PIN, pin);
        contentValues.put(KEY_RETAILER_FSSAI, fssai);
        contentValues.put(KEY_DISTRICT, district);
        contentValues.put(KEY_LOCALITY, locality);
        contentValues.put(KEY_ZONE, zone);
        contentValues.put(KEY_TARGET, target);
        contentValues.put(KEY_OUTLET_CHANNEL, outlethannel);
        contentValues.put(KEY_SHOP_TYPE, shopType);
        contentValues.put(KEY_RETAILER_GRADE, grade);
        contentValues.put(KEY_RETAILER_IMAGE, imageLink);
        contentValues.put(KEY_BEAT_ID, beat_id);
        contentValues.put(KEY_LAST_UPDATED_AT, updatedAt);
        contentValues.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_RETAILERS_LIST, null, contentValues);
        closeDb(db);
    }

    //insert record retailer list table
    public  boolean updateLocation(String retailerId,String lat,String longT){
        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RETAILER_LAT, lat);
        contentValues.put(KEY_RETAILER_LONG, longT);

        boolean val = db.update(TABLE_RETAILERS_LIST, contentValues, KEY_RETAILER_ID + "=?", new String[]{retailerId}) > 0;
        closeDb(db);

        return val;
    }
    public boolean updateRetailerList2(String retailerId, String email, String shopPhone, String whatsppno,
                                       String gstin, String fssaino, String pin_no, String grade, String target) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(KEY_RETAILER_NAME, retailer_name);
//        contentValues.put(KEY_RETAILER_STATE, state);
        contentValues.put(KEY_RETAILER_EMAIL, email);
//        contentValues.put(KEY_SHOP_PHONE, shopPhone);
        contentValues.put(KEY_OWNER_NAME, whatsppno);
        contentValues.put(KEY_OWNER_PHONE, shopPhone);
        contentValues.put(KEY_WHATSAPP_NO, whatsppno);
//        contentValues.put(KEY_RETAILER_LAT, retailer_lat);
//        contentValues.put(KEY_RETAILER_LONG, retailer_long);
        contentValues.put(KEY_RETAILER_GSTIN, gstin);
        contentValues.put(KEY_RETAILER_PIN, pin_no);
        contentValues.put(KEY_RETAILER_FSSAI, fssaino);
//        contentValues.put(KEY_LOCALITY, city);
        contentValues.put(KEY_TARGET, target);
        contentValues.put(KEY_RETAILER_GRADE, grade);

        boolean val = db.update(TABLE_RETAILERS_LIST, contentValues, KEY_RETAILER_ID + "=?", new String[]{retailerId}) > 0;
        closeDb(db);

        return val;
    }

    //get all data from retailer list table
    public Cursor getAllDataFromRetailerListTable(String beat_id) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_RETAILERS_LIST, null, KEY_BEAT_ID_R + "=?",
                new String[]{beat_id}, null, null, null);
    }

    public Cursor getAllDataFromRetailerListTableNew(String beat_id) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_RETAILERS_LIST,
                null,
                KEY_BEAT_ID_R + "=?",
                new String[]{beat_id},
                null,
                null,
                KEY_RETAILER_DISTANCE + " ASC");
    }


    //get all data from retailer list table
    public Cursor getAllDataFromRetailerListTable2(String beat_id, String rid) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_RETAILERS_LIST, null, KEY_BEAT_ID_R + "=?" + " AND " + KEY_RETAILER_ID + "=?",
                new String[]{beat_id, rid}, null, null, null);
    }

    //delete all data from retailer list table
    public void deleteDataFromRetailerListTable() {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_RETAILERS_LIST, null, null);
        closeDb(db);
    }

    //delete all data from retailer list table
    public void deleteDataFromRetailerListTable(String rid) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_RETAILERS_LIST, KEY_RETAILER_ID + "=?", new String[]{rid});
        closeDb(db);
    }

    //get all data from retailer list table
    public Cursor getRetailer(String rid) {

        SQLiteDatabase db = getWritableDb();
        return db.query(TABLE_RETAILERS_LIST, new String[]{}, KEY_RETAILER_ID + "=?",
                new String[]{rid}, null, null, null);
    }

    //insert record new retailer list table
    public void insertNewRetailerList(String tempRid, String retailer_name,
                                      String retailer_address, String shopPhone, String ownerName,
                                      String owner_phone1, String whatsAppNo, String retailer_lat,
                                      String retailer_long, String state, String zone, String locality,
                                      String district, String pin, String email, String gstin, String target,
                                      String fssai, String grade, String outlethannel, String shopType,
                                      String ownerImageLink, String imageTimeStamp, String shopImageLink1,
                                      String shopImageLink2, String shopImageLink3, String shopImageLink4,
                                      String shopImageLink5, String _did, String beat_id, String date,
                                      String transId, String recordDate) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NEW_RETAILER_IDD, tempRid);
        contentValues.put(KEY_NEW_RETAILER_TEMP_IDD, tempRid);
        contentValues.put(KEY_NEW_RETAILER_NAME, retailer_name);
        contentValues.put(KEY_NEW_RETAILER_ADDRESS, retailer_address);
        contentValues.put(KEY_NEW_RETAILER_STATE, state);
        contentValues.put(KEY_NEW_RETAILER_EMAIL, email);
        contentValues.put(KEY_NEW_SHOP_PHONE, shopPhone);
        contentValues.put(KEY_NEW_OWNER_NAME, ownerName);
        contentValues.put(KEY_NEW_OWNER_PHONE, owner_phone1);
        contentValues.put(KEY_NEW_WHATSAPP_NO, whatsAppNo);
        contentValues.put(KEY_NEW_RETAILER_LAT, retailer_lat);
        contentValues.put(KEY_NEW_RETAILER_LONG, retailer_long);
        contentValues.put(KEY_NEW_RETAILER_GSTIN, gstin);
        contentValues.put(KEY_NEW_RETAILER_PIN, pin);
        contentValues.put(KEY_NEW_RETAILER_FSSAI, fssai);
        contentValues.put(KEY_NEW_DISTRICT, district);
        contentValues.put(KEY_NEW_LOCALITY, locality);
        contentValues.put(KEY_NEW_ZONE, zone);
        contentValues.put(KEY_NEW_TARGET, target);
        contentValues.put(KEY_STATUS_CODE, "0");
        contentValues.put(KEY_ERROR_MSG, "N/A");
        contentValues.put(KEY_NEW_OUTLET_CHANNEL, outlethannel);
        contentValues.put(KEY_NEW_SHOP_TYPE, shopType);
        contentValues.put(KEY_NEW_RETAILER_GRADE, grade);
        contentValues.put(KEY_NEW_OWNER_IMAGE, ownerImageLink);
        contentValues.put(KEY_IMAGE_TIME_STAMP, imageTimeStamp);
        contentValues.put(KEY_NEW_SHOP_IMAGE1, shopImageLink1);
        contentValues.put(KEY_NEW_SHOP_IMAGE2, shopImageLink2);
        contentValues.put(KEY_NEW_SHOP_IMAGE3, shopImageLink3);
        contentValues.put(KEY_NEW_SHOP_IMAGE4, shopImageLink4);
        contentValues.put(KEY_NEW_SHOP_IMAGE5, shopImageLink5);
        contentValues.put(KEY_NEW_ORDER_DATE, date);
        contentValues.put(KEY_NEW_ORDER_STATUS, "fail");
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(KEY_DISTRIBUTOR_ID, _did);
        contentValues.put(KEY_BEAT_ID, beat_id);
        contentValues.put(TRANSACTION_ID, transId);
        contentValues.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_NEW_RETAILERS_LIST, null, contentValues);
        closeDb(db);
    }


    // Insert record New Preferred Retailer


    //get all data from retailer list table
    public Cursor  getAllDataFromNewRetailerListTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_RETAILERS_LIST, new String[]{}, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"fail"}, null, null, null);
    }

    public Cursor getSpecificDataFromNewRetailerListTable(String rid) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_RETAILERS_LIST, new String[]{},
                KEY_NEW_RETAILER_IDD + "=?", new String[]{rid},
                null, null, null);
    }

    //get all data from retailer list table
    public Cursor getAllDataFromNewRetailerListTable2(String bid) {
        SQLiteDatabase db = getReadableDb();
        //return db.rawQuery("select * from "+TABLE_NEW_RETAIERS_LIST+"",null);
        return db.query(TABLE_NEW_RETAILERS_LIST, new String[]{}, KEY_BEAT_ID + "=?",
                new String[]{bid}, null, null, null);
    }

    //DELETE all data from new retailer list table
    public boolean deleteSpecificDataFromNewRetailerListTable() {

        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_NEW_RETAILERS_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);

        return flag;
    }

    //DELETE all data from new retailer list table
//    public void deleteSpecificDataFromNewRetailerListTable2() {
//
//        SQLiteDatabase db = getWritableDb();
//        db.execSQL("delete from " + TABLE_NEW_RETAILERS_LIST);
//        closeDb(db);
//    }

    //DELETE all data from new retailer list table
    public void deleteSpecificDataFromNewRetailerListTable3(String date) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_RETAILERS_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});

        closeDb(db);
    }

    //DELETE all data from new retailer list table
    public void deleteSpecificDataFromNewRetailerListTable4(String date) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_RETAILERS_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});

        closeDb(db);
    }

    //update in new  retailer list table
    public boolean updateNewRetailerListTable(String tempRid, String nrid, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SERVER_SUBMIT_STATUS, status);
        cv.put(KEY_NEW_RETAILER_IDD, nrid);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_NEW_RETAILERS_LIST, cv,
                KEY_NEW_RETAILER_IDD + "=?", new String[]{tempRid}) > 0;

        Log.e("New Testing", " New Retailer updated rid is:" + nrid);

        closeDb(db);
        return flag;
    }

    //update in new  retailer list table if error
    public boolean updateNewRetailerListTableIfError(String nrid, String did,
                                                     String status,String statusCode,String errorMsg ) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SERVER_SUBMIT_STATUS, status);
        cv.put(KEY_STATUS_CODE, statusCode);
        cv.put(KEY_ERROR_MSG, errorMsg);

        boolean flag = db.update(TABLE_NEW_RETAILERS_LIST, cv,
                KEY_NEW_RETAILER_IDD + "=? AND "+KEY_DISTRIBUTOR_ID + "=?",
                new String[]{nrid,did}) > 0;

        Log.e("New Testing", " New Retailer updated rid is:" + nrid);

        closeDb(db);
        return flag;
    }


    public boolean updateNewRetailerListTable2(String nrid, String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_NEW_ORDER_STATUS, status);

        boolean flag = db.update(TABLE_NEW_RETAILERS_LIST, cv,
                KEY_NEW_RETAILER_IDD + "=? AND " + KEY_DISTRIBUTOR_ID + "=? AND "
                        + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{nrid, did,"success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public boolean updateNewPreferredRetailerListTable2(String nrid, String did, String status) {
//
//        SQLiteDatabase db = getWritableDb();
//        ContentValues cv = new ContentValues();
//
//        cv.put(KEY_NEW_ORDER_STATUS, status);
//
//        boolean flag = db.update(TABLE_NEW_RETAILERS_LIST, cv, KEY_NEW_RETAILER_IDD + "=? AND "
//                + KEY_DISTRIBUTOR_ID + "=?", new String[]{nrid, did}) > 0;
//
//        closeDb(db);
//        return flag;
//    }

    public void insertNewPreferredRetailer(String tempRId, String firmName, String firmContact1, String firmContact2, String didSelected,
                                           String distributorContactName1, String distributorContactName2, String latitude, String longitude, String block, String district,
                                           String proposedCategory, String perMonthBusiness, String retailerDistributionBrandName,
                                           String monthlyTurnover, String internalStaffCount,
                                           String avgPerDayWalk, String avgCustomerIncome, String otherBusiness, String shopImagePath,
                                           String roofHeight, String openFasciaWidth, String fixtureType, String frontSpace, String totalShelf,
                                           String spaceProvided, String frontCounterMeasurement, String widthOfRoad,
                                           String floorType, String brandPosting, String sidePaneSize, String counterSize, String frontBoardSize,
                                           String _did, String beat_id, String date, String transId, String recordDate) {

        SQLiteDatabase db = getWritableDb();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NEW_PREFERRED_RETAILER_TEMP_ID, tempRId);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_ID, tempRId);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FIRM_NAME, firmName);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME1, firmContact1);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME2, firmContact2);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_NAME, didSelected);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME1, distributorContactName1);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME2, distributorContactName2);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_LATITUDE, latitude);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_LONGITUDE, longitude);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_BLOCK, block);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRICT, district);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_PROPOSE_CATEGORY, proposedCategory);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_PER_MONTH_BUSINESS, perMonthBusiness);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTION_BRAND_NAME, retailerDistributionBrandName);
        contentValues.put(KEY_MONTHLY_TURNOVER, monthlyTurnover);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_INTERNAL_STAFF, internalStaffCount);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_WALK_IN, avgPerDayWalk);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_AVG_PER_DAY_CUSTOMER_INCOME, avgCustomerIncome);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_OTHER_BUSINESS, otherBusiness);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_SHOP_PATH, shopImagePath);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_ROOF_HEIGHT, roofHeight);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FASCIA_WIDTH, openFasciaWidth);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FIXTURE_TYPE, fixtureType);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FRONT_SPACE, frontSpace);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_TOTAL_SHELF, totalShelf);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_SPACE_PROVIDED, spaceProvided);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FRONT_COUNTER_MEASUREMENT, frontCounterMeasurement);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_ROAD_WIDTH, widthOfRoad);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FLOOR_TYPE, floorType);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_BRAND_POSTING, brandPosting);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_SIDE_PANEL_SIZE, sidePaneSize);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_COUNTER_SIZE, counterSize);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_FRONT_BOARD_SIZE, frontBoardSize);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID, _did);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_BEAT_ID, beat_id);
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_NEW_ORDER_DATE, date);
        contentValues.put(KEY_PREFERRED_ORDER_STATUS, "fail");
        contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        contentValues.put(KEY_NEW_PREFERRED_RETAILER_TRANSACTION_ID, transId);
        contentValues.put(KEY_RECORD_DATE, recordDate);


        db.insertOrThrow(TABLE_NEW_PREFERRED_RETAILERS_LIST, null, contentValues);
        closeDb(db);

    }

    // get all data from new preferred retailer list

    public Cursor getAllDataFromNewPreferredRetailerListTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_NEW_PREFERRED_RETAILERS_LIST, new String[]{}, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"fail"}, null, null, null);
    }

    //get all data from preferred retailer list table

    public Cursor getAllDataFromNewPreferredRetailerListTable2(String bid) {
        SQLiteDatabase db = getReadableDb();
        //return db.rawQuery("select * from "+TABLE_NEW_RETAIERS_LIST+"",null);
        return db.query(TABLE_NEW_PREFERRED_RETAILERS_LIST, new String[]{}, KEY_NEW_PREFERRED_RETAILER_BEAT_ID + "=?",
                new String[]{bid}, null, null, null);
    }

    //DELETE all data from new preferred retailer list table

    public boolean deleteSpecificDataFromNewPreferredRetailerListTable() {

        SQLiteDatabase db = getWritableDb();
        boolean flag = db.delete(TABLE_NEW_PREFERRED_RETAILERS_LIST, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);

        return flag;
    }

    //DELETE all data from new preferred retailer list table

    public void deleteSpecificDataFromNewPreferredRetailerListTable3(String date) {
        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_PREFERRED_RETAILERS_LIST, KEY_RECORD_DATE + "<>?", new String[]{date});

        closeDb(db);
    }

    //DELETE all data from new preferred retailer list table

    public void deleteSpecificDataFromNewPreferredRetailerListTable4(String date) {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_NEW_PREFERRED_RETAILERS_LIST, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});

        closeDb(db);
    }

    //update in new   preferred retailer list table
    public boolean updateNewPreferredRetailerListTable(String tempRid, String nrid, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        // TODO CHECK FOR STATUS KEYS DONT OVERLAP
        cv.put(KEY_SERVER_SUBMIT_STATUS, status);
        cv.put(KEY_NEW_PREFERRED_RETAILER_ID, nrid);
        cv.put(KEY_NEW_PREFERRED_RETAILER_TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_NEW_PREFERRED_RETAILERS_LIST, cv, KEY_NEW_PREFERRED_RETAILER_ID + "=?", new String[]{tempRid}) > 0;

        Log.e("New Testing", " New Retailer updated rid is:" + nrid);

        closeDb(db);
        return flag;
    }


    public boolean updateNewPreferredRetailerListTable2(String nrid, String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        // TODO CHECK FOR STATUS KEYS DONT OVERLAP
        cv.put(KEY_PREFERRED_ORDER_STATUS, status);

        boolean flag = db.update(TABLE_NEW_PREFERRED_RETAILERS_LIST, cv, KEY_NEW_PREFERRED_RETAILER_ID + "=? AND "
                + KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_ID + "=?", new String[]{nrid, did}) > 0;

        closeDb(db);
        return flag;
    }

    //search into town list table
    //Intelegains Technologies Code
    //Modification
    public Cursor searchIntoRetailerListTable(String searchTerm, String beatId) {
        SQLiteDatabase db = getReadableDb();

        try {

            String query = "Select * from " + TABLE_RETAILERS_LIST + " where " + KEY_RETAILER_NAME + " like " + "'%" + searchTerm + "%'" + " and " +
                    KEY_BEAT_ID_R + " = " + "'" + beatId + "'";

/*
            String query = "Select * from " + TABLE_RETAILERS_LIST + " where " + KEY_RETAILER_NAME + " like " + "'%" + searchTerm + "%'" + " and " +
                    KEY_BEAT_ID_R + " = " + "'" + beatId + "'";
*/


            return db.rawQuery(query, null);

        } catch (Exception e) {
            e.getMessage();
        }

        return null;
    }


    //update in activity tracking table
    public boolean insertActivityTrackingTableWalkLatLong(String date, String
            walkLat2, String walkLongt2, String accuracy) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_DATE_A, date);
        cv.put(KEY_WALK_LAT1, walkLat2);
        cv.put(KEY_WALK_LONGT1, walkLongt2);
        cv.put(KEY_ACCURACY, accuracy);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.insertOrThrow(TABLE_ACTIVITY_TRACKING, null, cv) > 0;

        closeDb(db);
        return val;
    }

    //get all data from activity tracking table
    public Cursor getAllDataFromActivityTrackingTable() {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ACTIVITY_TRACKING, new String[]{}, null, null,
                null, null, null);
    }

    public boolean deleteAllDataFromActivityTrackingTable() {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_ACTIVITY_TRACKING, null, null) > 0;

        closeDb(db);
        return flag;
    }

    //Inserting data in Message List Table
    public void insertIntoMessageListTable(String date, String message) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TIME_STAMP, date);
        cv.put(KEY_MESSAGE, message);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_MESSAGE_LIST, null, cv);
        closeDb(db);
    }

    //get all data from Message List Table
    public Cursor getAllDataFromMessageListTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_MESSAGE_LIST, new String[]{}, null, null, null, null, null);
    }

    //Inserting data in OderPlacedByTable
    public void entryInOderPlacedByRetailersTable(String rid, String did, String time, String orderType, String checkIn,
                                                  String lat, String longt, String comment, String transId
            , String recordDate, String qty, String qtyPC) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = dateFormat.format(Calendar.getInstance().getTime());

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER_PLACED_BY_RID, rid);
        cv.put(KEY_ORDER_PLACED_BY_DID, did);
        cv.put(KEY_ORDER_PLACED_TIME, time);
        cv.put(KEY_ORDER_TYPE, orderType);
        cv.put(KEY_ORDER_CHECK_IN, checkIn);
        cv.put(KEY_ORDER_CHECK_OUT, time);
        cv.put(KEY_BRAND_KG, qty);
        cv.put(KEY_BRAND_UNIT, qtyPC);
        cv.put(KEY_ORDER_LAT, lat);
        cv.put(KEY_ORDER_LONG, longt);
        cv.put(KEY_ORDER_STATUS, "fail");
        cv.put(KEY_ORDER_DATE, date);
        cv.put(KEY_STATUS_CODE, "0");
        cv.put(KEY_ERROR_MSG, "N/A");
        cv.put(KEY_ORDER_COMMENT, comment);
        cv.put(TRANSACTION_ID, transId);
        cv.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_ORDER_PLACED_BY_RETAILERS, null, cv);
        closeDb(db);
    }

    //update in in OderPlacedByTable
    public void updateInOderPlacedByRetailersTable(String rid, String did, String time2, String orderType,
                                                   String checkInTime, String latitudeStr, String longitudeStr,
                                                   String comnt, String transactionId, String recordDate) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = dateFormat.format(Calendar.getInstance().getTime());

        SQLiteDatabase db1 = getReadableDb();
        SQLiteDatabase db = getWritableDb();

        Cursor cursor = db1.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{}, KEY_ORDER_PLACED_BY_RID + "=?" + " AND "
                + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did}, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {

            ContentValues cv = new ContentValues();
            cv.put(KEY_ORDER_TYPE, orderType);
            cv.put(KEY_ORDER_STATUS, "fail");
            cv.put(KEY_ORDER_DATE, date);
            cv.put(KEY_ORDER_COMMENT, comnt);
            cv.put(TRANSACTION_ID, transactionId);
            cv.put(KEY_RECORD_DATE, recordDate);

            db.update(TABLE_ORDER_PLACED_BY_RETAILERS, cv, KEY_ORDER_PLACED_BY_RID + "=?" + " AND "
                    + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did});

            cursor.close();
            closeDb(db);
            return;

        } else {

            ContentValues cv = new ContentValues();
            cv.put(KEY_ORDER_PLACED_BY_RID, rid);
            cv.put(KEY_ORDER_PLACED_BY_DID, did);
            cv.put(KEY_ORDER_PLACED_TIME, time2);
            cv.put(KEY_ORDER_TYPE, orderType);
            cv.put(KEY_ORDER_CHECK_IN, checkInTime);
            cv.put(KEY_ORDER_CHECK_OUT, time2);
            cv.put(KEY_ORDER_LAT, latitudeStr);
            cv.put(KEY_ORDER_LONG, longitudeStr);
            cv.put(KEY_ORDER_STATUS, "fail");
            cv.put(KEY_ORDER_DATE, date);
            cv.put(KEY_ORDER_COMMENT, comnt);
            cv.put(TRANSACTION_ID, transactionId);
            cv.put(KEY_RECORD_DATE, recordDate);

            db.insertOrThrow(TABLE_ORDER_PLACED_BY_RETAILERS, null, cv);

        }

        closeDb(db);

    }

    public void updateBrandQtyTable(String rid, String did, String transactionId, String recordDate,String qty,String qtyPC) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = dateFormat.format(Calendar.getInstance().getTime());

        SQLiteDatabase db1 = getReadableDb();
        SQLiteDatabase db = getWritableDb();

        Cursor cursor = db1.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{}, KEY_ORDER_PLACED_BY_RID + "=?" + " AND "
                + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did}, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {

            ContentValues cv = new ContentValues();
            cv.put(KEY_BRAND_KG, qty);
            cv.put(KEY_BRAND_UNIT, qtyPC);

            db.update(TABLE_ORDER_PLACED_BY_RETAILERS, cv, KEY_ORDER_PLACED_BY_RID + "=?" + " AND "
                    + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did});

            cursor.close();
            closeDb(db);
            return;

        } else {

            ContentValues cv = new ContentValues();
            cv.put(KEY_BRAND_KG, qty);
            cv.put(KEY_BRAND_UNIT, qtyPC);

            db.insertOrThrow(TABLE_ORDER_PLACED_BY_RETAILERS, null, cv);

        }

        closeDb(db);

    }

    //update in in OderPlacedByTable
    public boolean updateInOderPlacedByRetailersTable2(String rid, String orderStatus, String did) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER_STATUS, orderStatus);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_ORDER_PLACED_BY_RETAILERS, cv,
                KEY_ORDER_PLACED_BY_RID + "=? AND "
                + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did}) > 0;

        closeDb(db);
        return val;
    }

    public boolean updateInOderPlacedByRetailersTable22(String rid, String did ,String orderStatus,
                                                        String statusCode,String errorMsg) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER_STATUS, orderStatus);
        cv.put(KEY_STATUS_CODE, statusCode);
        cv.put(KEY_ERROR_MSG, errorMsg);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_ORDER_PLACED_BY_RETAILERS, cv, KEY_ORDER_PLACED_BY_RID + "=? AND "
                + KEY_ORDER_PLACED_BY_DID + "=?", new String[]{rid, did}) > 0;

        closeDb(db);
        return val;
    }

    //get all data from OderPlacedByTable
    public Cursor getRetailersFromOderPlacedByRetailersTable() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{}, null, null,
                null, null, null);
    }


    //get all data from OderPlacedByTable
    public Cursor getRetailersFromOderPlacedByRetailersTable2(String serverStatus) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{},
                KEY_SERVER_SUBMIT_STATUS+"=?", new String[]{serverStatus},
                null, null, null);
    }

    //get order type from OderPlacedByTable
    public Cursor getOrderTypeFromOderPlacedByRetailersTable(String rid) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{}, KEY_ORDER_PLACED_BY_RID + "=?",
                new String[]{rid}, null, null, null);

    }

    //get all data from OderPlacedByTable
    public Cursor getProductiveOrdersFromOderPlacedByRetailersTable(String orderType) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{},
                KEY_ORDER_TYPE + "!=? AND "+KEY_ORDER_STATUS+"=?",
                new String[]{orderType,"fail"}, null, null, null);
    }

    public Cursor getNonProductiveOrdersFromOderPlacedByRetailersTable(String orderType) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_RETAILERS, new String[]{},
                KEY_ORDER_TYPE + "=? AND "+KEY_ORDER_STATUS+"=?",
                new String[]{orderType,"fail"}, null, null, null);
    }

    //get all data from OderPlacedByTable
    public Cursor getRetailersFromOderPlacedByRetailersTable3(String orderType, String status) {
        SQLiteDatabase db = getReadableDb();
        String query = "SELECT * FROM " + TABLE_ORDER_PLACED_BY_RETAILERS + " WHERE " + KEY_ORDER_TYPE + "='" + orderType + "' AND " + KEY_ORDER_STATUS + "='" + status + "'";
        return db.rawQuery(query, null);
    }

    //delete specific data from Order Placed by Table
    public boolean deleteAllDataFromOderPlacedByRetailersTable(/*String orderStatus, String date*/) {

        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_ORDER_PLACED_BY_RETAILERS, KEY_ORDER_STATUS + "=?", new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

    //delete specific data from Order Placed by Table
    public void deleteAllDataFromOderPlacedByRetailersTable3(String date) {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_PLACED_BY_RETAILERS, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    //delete specific data from Order Placed by Table
    public void deleteAllDataFromOderPlacedByRetailersTable4(String date) {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_PLACED_BY_RETAILERS, KEY_ORDER_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});
        closeDb(db);
    }

    //Inserting data in NewOderPlacedByTable
    public void entryInOderPlacedByNewRetailersTable(String tempRid, String did, String time, String orderType,
                                                     String checkIn, String lat, String longt, String comment,
                                                     String date, String transId, String recordDate) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NEW_ORDER_PLACED_BY_RID, tempRid);
        cv.put(KEY_NEW_RETAILER_TEMP_IDD, tempRid);
        cv.put(KEY_NEW_ORDER_PLACED_BY_DID, did);
        cv.put(KEY_NEW_ORDER_PLACED_TIME, time);
        cv.put(KEY_NEW_ORDER_TYPE, orderType);
        cv.put(KEY_NEW_ORDER_CHECK_IN, checkIn);
        cv.put(KEY_NEW_ORDER_CHECK_OUT, time);
        cv.put(KEY_NEW_ORDER_LAT, lat);
        cv.put(KEY_NEW_ORDER_LONG, longt);
        cv.put(KEY_NEW_ORDER_COMMENT, comment);
        cv.put(KEY_NEW_ORDER_PLACED_BY_DATE, date);
        cv.put(KEY_NEW_ORDER_STATUS, "fail");
        cv.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        cv.put(KEY_STATUS_CODE, "0");
        cv.put(KEY_ERROR_MSG, "N/A");
        cv.put(TRANSACTION_ID, transId);
        cv.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, null, cv);
        closeDb(db);

    }

    //Inserting data in NewOderPlacedByTable
    public void entryInOderPlacedByPreferredRetailersTable(String tempRid, String did, String time, String orderType,
                                                           String checkIn, String lat, String longt, String comment,
                                                           String date, String transId, String recordDate) {
        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_PREFERRED_ORDER_PLACED_BY_RID, tempRid);
        cv.put(KEY_NEW_PREFERRED_RETAILER_TEMP_ID, tempRid);
        cv.put(KEY_PREFERRED_ORDER_PLACED_BY_DID, did);
        cv.put(KEY_PREFERRED_ORDER_PLACED_TIME, time);
        cv.put(KEY_PREFERRED_ORDER_TYPE, orderType);
        cv.put(KEY_PREFERRED_ORDER_CHECK_IN, checkIn);
        cv.put(KEY_PREFERRED_ORDER_CHECK_OUT, time);
        cv.put(KEY_PREFERRED_ORDER_LAT, lat);
        cv.put(KEY_PREFERRED_ORDER_LONG, longt);
        cv.put(KEY_PREFERRED_ORDER_COMMENT, comment);
        cv.put(KEY_PREFERRED_ORDER_PLACED_BY_DATE, date);
        cv.put(KEY_PREFERRED_ORDER_STATUS, "fail");
        cv.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        cv.put(TRANSACTION_ID, transId);
        cv.put(KEY_RECORD_DATE, recordDate);

        db.insertOrThrow(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, null, cv);
        closeDb(db);

    }

    public void updateOderPlacedByNewRetailersTable(String nRid, String did, String time, String orderType,
                                                    String checkIn, String lat, String longt, String comment,
                                                    String date, String transId, String recordDate) {


        SQLiteDatabase db1 = getReadableDb();

        Cursor cursor = db1.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{}, KEY_NEW_ORDER_PLACED_BY_RID + "=?" + " AND "
                + KEY_NEW_ORDER_PLACED_BY_DID + "=?", new String[]{nRid, did}, null, null, null);

        Log.e("SalesDatabase", " nrid:" + nRid + " row count:" + cursor.getCount());


        if (cursor != null && cursor.getCount() > 0) {

            SQLiteDatabase db2 = getWritableDb();

            ContentValues conVal = new ContentValues();

            conVal.put(KEY_NEW_ORDER_STATUS, "fail");

            db2.update(TABLE_NEW_RETAILERS_LIST, conVal, KEY_NEW_RETAILER_IDD + "=? AND "
                    + KEY_DISTRIBUTOR_ID + "=?", new String[]{nRid, did});


            SQLiteDatabase db = getWritableDb();

            ContentValues cv = new ContentValues();
            cv.put(KEY_NEW_ORDER_TYPE, orderType);
            cv.put(KEY_NEW_ORDER_PLACED_TIME, time);
            cv.put(KEY_NEW_ORDER_LAT, lat);
            cv.put(KEY_NEW_ORDER_LONG, longt);
            cv.put(KEY_NEW_ORDER_STATUS, "fail");
            cv.put(KEY_NEW_ORDER_PLACED_BY_DATE, date);
            cv.put(KEY_NEW_ORDER_COMMENT, comment);
            cv.put(TRANSACTION_ID, transId);
            cv.put(KEY_RECORD_DATE, recordDate);

            try {


                db.update(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, cv, KEY_NEW_ORDER_PLACED_BY_RID + "=?" + " AND "
                        + KEY_NEW_ORDER_PLACED_BY_DID + "=?", new String[]{nRid, did});
            }
            catch (Exception ex)
            {

            }
            cursor.close();
            closeDb(db);

        } else {

            SQLiteDatabase dbN = getReadableDb();
            @SuppressLint("Recycle") Cursor cursorNewRet = dbN.query(TABLE_NEW_RETAILERS_LIST, new String[]{}, KEY_NEW_RETAILER_IDD + "=?" + " AND "
                    + KEY_DISTRIBUTOR_ID + "=?", new String[]{nRid, did}, null, null, null);

            String tempId = "", serverStatus = "";
            if (cursorNewRet != null && cursorNewRet.getCount() > 0 && cursorNewRet.moveToFirst()) {
                tempId = cursorNewRet.getString(cursorNewRet.getColumnIndex(KEY_NEW_RETAILER_TEMP_IDD));
                serverStatus = cursorNewRet.getString(cursorNewRet.getColumnIndex(KEY_SERVER_SUBMIT_STATUS));
            }

            SQLiteDatabase db = getWritableDb();

            ContentValues cv = new ContentValues();
            cv.put(KEY_NEW_ORDER_PLACED_BY_RID, nRid);
            cv.put(KEY_NEW_RETAILER_TEMP_IDD, tempId);
            cv.put(KEY_NEW_ORDER_PLACED_BY_DID, did);
            cv.put(KEY_NEW_ORDER_PLACED_TIME, time);
            cv.put(KEY_NEW_ORDER_TYPE, orderType);
            cv.put(KEY_NEW_ORDER_CHECK_IN, checkIn);
            cv.put(KEY_NEW_ORDER_CHECK_OUT, time);
            cv.put(KEY_NEW_ORDER_LAT, lat);
            cv.put(KEY_NEW_ORDER_LONG, longt);
            cv.put(KEY_NEW_ORDER_COMMENT, comment);
            cv.put(KEY_NEW_ORDER_PLACED_BY_DATE, date);
            cv.put(KEY_NEW_ORDER_STATUS, "fail");
            cv.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            cv.put(TRANSACTION_ID, transId);
            cv.put(KEY_RECORD_DATE, recordDate);

            db.insertOrThrow(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, null, cv);
            closeDb(db);
        }


    }

    public void updateOderPlacedByPreferredRetailersTable(String pRid, String did, String time, String orderType,
                                                          String checkIn, String lat, String longt, String comment,
                                                          String date, String transId, String recordDate) {

        SQLiteDatabase db1 = getReadableDb();

        Cursor cursor = db1.query(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, new String[]{}, KEY_PREFERRED_ORDER_PLACED_BY_RID + "=?" + " AND "
                + KEY_PREFERRED_ORDER_PLACED_BY_DID + "=?", new String[]{pRid, did}, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {

            SQLiteDatabase db2 = getWritableDb();

            ContentValues conVal = new ContentValues();

            conVal.put(KEY_PREFERRED_ORDER_STATUS, "fail");

            db2.update(TABLE_NEW_PREFERRED_RETAILERS_LIST, conVal, KEY_NEW_PREFERRED_RETAILER_ID + "=? AND "
                    + KEY_PREFERRED_ORDER_PLACED_BY_DID + "=?", new String[]{pRid, did});


            SQLiteDatabase db = getWritableDb();

            ContentValues cv = new ContentValues();
            cv.put(KEY_PREFERRED_ORDER_TYPE, orderType);
            cv.put(KEY_PREFERRED_ORDER_PLACED_TIME, time);
            cv.put(KEY_PREFERRED_ORDER_LAT, lat);
            cv.put(KEY_PREFERRED_ORDER_LONG, longt);
            cv.put(KEY_PREFERRED_ORDER_STATUS, "fail");
            cv.put(KEY_PREFERRED_ORDER_PLACED_BY_DATE, date);
            cv.put(KEY_PREFERRED_ORDER_COMMENT, comment);
            cv.put(TRANSACTION_ID, transId);
            cv.put(KEY_RECORD_DATE, recordDate);

            db.update(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, cv, KEY_PREFERRED_ORDER_PLACED_BY_RID + "=?" + " AND "
                    + KEY_PREFERRED_ORDER_PLACED_BY_DID + "=?", new String[]{pRid, did});

            cursor.close();
            closeDb(db);

        } else {

            SQLiteDatabase dbP = getReadableDb();

            @SuppressLint("Recycle") Cursor cursorPreRet = dbP.query(TABLE_NEW_PREFERRED_RETAILERS_LIST, new String[]{}, KEY_NEW_PREFERRED_RETAILER_ID + "=?" + " AND "
                    + KEY_PREFERRED_ORDER_PLACED_BY_DID + "=?", new String[]{pRid, did}, null, null, null);

            String tempId = "", serverStatus = "";
            if (cursorPreRet != null && cursorPreRet.getCount() > 0 && cursorPreRet.moveToFirst()) {
                tempId = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_NEW_PREFERRED_RETAILER_TEMP_ID));
                serverStatus = cursorPreRet.getString(cursorPreRet.getColumnIndex(KEY_SERVER_SUBMIT_STATUS));
            }


            SQLiteDatabase db = getWritableDb();

            ContentValues cv = new ContentValues();
            cv.put(KEY_PREFERRED_ORDER_PLACED_BY_RID, pRid);
            cv.put(KEY_NEW_PREFERRED_RETAILER_TEMP_ID, tempId);
            cv.put(KEY_PREFERRED_ORDER_PLACED_BY_DID, did);
            cv.put(KEY_PREFERRED_ORDER_PLACED_TIME, time);
            cv.put(KEY_PREFERRED_ORDER_TYPE, orderType);
            cv.put(KEY_PREFERRED_ORDER_CHECK_IN, checkIn);
            cv.put(KEY_PREFERRED_ORDER_CHECK_OUT, time);
            cv.put(KEY_PREFERRED_ORDER_LAT, lat);
            cv.put(KEY_PREFERRED_ORDER_LONG, longt);
            cv.put(KEY_PREFERRED_ORDER_COMMENT, comment);
            cv.put(KEY_PREFERRED_ORDER_PLACED_BY_DATE, date);
            cv.put(KEY_PREFERRED_ORDER_STATUS, "fail");
            cv.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
            cv.put(TRANSACTION_ID, transId);
            cv.put(KEY_RECORD_DATE, recordDate);

            db.insertOrThrow(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, null, cv);
            closeDb(db);
        }


    }

    //get all data from NewOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTable2(/*String nrid*/) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                KEY_NEW_ORDER_STATUS + "=?" + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"fail", "success"/*, nrid*/}, null, null, null);
    }

    //get all data from NewOderPlacedByTable
    public Cursor getSpecificPreferredRetailersFromOderPlacedByPreferredRetailersTable2(/*String nrid*/) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, new String[]{},
                KEY_PREFERRED_ORDER_STATUS + "=?" + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"fail", "success"/*, nrid*/}, null, null, null);
    }


    //get all data from NewOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTable222(String nrid) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                KEY_NEW_ORDER_STATUS + "=?" + " AND " + KEY_SERVER_SUBMIT_STATUS + "=?" + " AND " +
                        KEY_NEW_ORDER_PLACED_BY_RID + "=?", new String[]{"fail", "success", nrid},
                null, null, null);
    }

    //get specific data from OderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTable22(String nrid) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                KEY_NEW_ORDER_COMMENT + "!=?" + " AND " + KEY_NEW_ORDER_STATUS + "=?" + " AND " +
                        KEY_SERVER_SUBMIT_STATUS + "=?" + " AND " + KEY_NEW_ORDER_PLACED_BY_RID + "=?",
                new String[]{"new productive", "fail", "success", nrid}, null, null, null);
    }


    //get specific data from OderPlacedByTable
    public Cursor getSpecificNewPreferredRetailersFromOderPlacedByNewRetailersTable22(String nrid) {

        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, new String[]{},
                KEY_PREFERRED_ORDER_COMMENT + "!=?" + " AND " + KEY_PREFERRED_ORDER_STATUS + "=?" + " AND " +
                        KEY_SERVER_SUBMIT_STATUS + "=?" + " AND " + KEY_PREFERRED_ORDER_PLACED_BY_RID + "=?",
                new String[]{"p productive", "fail", "success", nrid}, null, null, null);
    }


    //update in new  retailer list table
    public boolean updateInOderPlacedByNewRetailersTable(String tempRid, String newRid, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_NEW_ORDER_PLACED_BY_RID, newRid);
        cv.put(KEY_SERVER_SUBMIT_STATUS, status);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean val = db.update(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, cv, KEY_NEW_ORDER_PLACED_BY_RID + "=?",
                new String[]{tempRid}) > 0;

        closeDb(db);
        return val;
    }

    //update in new  retailer list table
    public boolean updateStatusInOderPlacedByNewRetailersTable(String newRid, String did, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();

        cv.put(KEY_NEW_ORDER_STATUS, status);
        //cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, cv, KEY_NEW_ORDER_PLACED_BY_RID + "=? AND "
                + KEY_NEW_ORDER_PLACED_BY_DID + "=?", new String[]{newRid, did}) > 0;

        closeDb(db);
        return flag;

    }

    //delete specific data from Order Placed by Table
    public boolean deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable() {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

    //delete specific data from Order Placed by Table
//    public void deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable2() {
//        SQLiteDatabase db = getWritableDb();
//
//        db.execSQL("delete from " + TABLE_ORDER_PLACED_BY_NEW_RETAILERS);
//        closeDb(db);
//    }

    //delete specific data from Order Placed by Table
    public void deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    //delete specific data from Order Placed by Table
    public void deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});
        closeDb(db);
    }

    //get all data from NewOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTable(String nrid) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                KEY_NEW_RETAILER_IDD + "=?", new String[]{nrid}, null, null, null);
    }

    //get specific data from PreferredRetailerOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewPreferredRetailersTable(String nrid) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_PREFERRED_RETAILERS, new String[]{},
                KEY_PREFERRED_ORDER_PLACED_BY_RID + "=?", new String[]{nrid}, null, null, null);
    }

    //get all data from NewOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTable222() {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                null, null, null, null, null);
    }

    //get all data from NewOderPlacedByTable
    public Cursor getSpecificNewRetailersFromOderPlacedByNewRetailersTablePend(String serverStatus) {
        SQLiteDatabase db = getReadableDb();
        return db.query(TABLE_ORDER_PLACED_BY_NEW_RETAILERS, new String[]{},
                KEY_SERVER_SUBMIT_STATUS+"=?", new String[]{serverStatus},
                null, null, null);
    }

    //insert new distributor table
    public void insertInNewDistributorTable(String tempDid, ArrayList<String> newDistributorDetails,
            /*List<String> listBeatName*/String beatName, List<String> listProductDivision,
                                            List<Double> ownerImageLatLong, List<Double> firmImageLatLong,String brandName,String rec) {
        try{
            Log.e("TAG", "insertInNewDistributor Image: "+rec);
            Log.e("TAG", "Model Image: "+new Gson().toJson(newDistributorDetails));
            SQLiteDatabase db = getWritableDb();

            ContentValues cv = new ContentValues();
            cv.put(KEY_NEW_DISTRIBUTOR_ID, tempDid);
            cv.put(KEY_NAME_OF_FIRM, newDistributorDetails.get(0));
            cv.put(KEY_FIRM_ADDRESS, newDistributorDetails.get(1));
            cv.put(KEY_BRAND_NAME, brandName);
            cv.put(KEY_OTHER_BRAND_NAME, brandName);
            cv.put(KEY_PINCODE, newDistributorDetails.get(2));
            cv.put(KEY_CITY, newDistributorDetails.get(3));
            cv.put(KEY_STATE, newDistributorDetails.get(4));
            cv.put(KEY_OWNER_NAME_D, newDistributorDetails.get(5));
            cv.put(KEY_OWNER_MOBILE_NO1, newDistributorDetails.get(6));
            cv.put(KEY_EMAIL_ID, newDistributorDetails.get(7));
            cv.put(KEY_OWNER_MOBILE_NO2, newDistributorDetails.get(8));
            cv.put(KEY_GSTIN, newDistributorDetails.get(9));
            cv.put(KEY_FSSAI_NO, newDistributorDetails.get(10));
            cv.put(KEY_PAN_NO, newDistributorDetails.get(11));
            cv.put(KEY_MONTHLY_TURNOVER, newDistributorDetails.get(12));
            cv.put(KEY_OWNER_IMAGE, newDistributorDetails.get(13));
            cv.put(KEY_OWNER_IMAGE_TIME_STAMP, newDistributorDetails.get(14));
            cv.put(KEY_DISTRIBUTOR_RECORDING, rec);

            if (ownerImageLatLong.size() > 0) {

                String oLatLong = "";
                for (int i = 0; i < ownerImageLatLong.size(); i++) {

                    oLatLong = oLatLong.concat(String.valueOf(ownerImageLatLong.get(i)));
                    if (ownerImageLatLong.size() > 1 && i < 1)
                        oLatLong = oLatLong.concat(",");
                }

                cv.put(KEY_OWNER_IMAGE_LATLONG, oLatLong);
            }

            cv.put(KEY_TOTAL_NO_EMP, "");
            cv.put(KEY_SALES_PERSON, "");
            cv.put(KEY_ADMIN_PERSON, "");
            cv.put(KEY_DELIVERY_VEHICLE, "");
            cv.put(KEY_NO_OF_VEHICLE, "");
            cv.put(KEY_BEAT_NAME_D, beatName);
            cv.put(KEY_NO_OF_SHOP_IN_BEAT, newDistributorDetails.get(15));
            cv.put(KEY_NO_OF_SHOP_COVERED, "");
            cv.put(KEY_NO_OF_GUMTI_COVERED, "");
            cv.put(KEY_TOTAL_NO_OF_SHOP, "");
            cv.put(KEY_NO_OF_WHOLESALER, "");
            cv.put(KEY_INVESTMENT_PLAN, newDistributorDetails.get(16));

            if (listProductDivision.size() > 0) {

                String strProduct = "";

                for (int i = 0; i < listProductDivision.size(); i++) {
                    strProduct = strProduct.concat(listProductDivision.get(i));

                    if ((listProductDivision.size() > 1) && (i < (listProductDivision.size() - 1)))
                        strProduct = strProduct.concat(",");
                }

                cv.put(KEY_PRODUCT_DIVISION, strProduct);

            } else {
                cv.put(KEY_PRODUCT_DIVISION, "");
            }

            cv.put(KEY_MONTHLY_SALE_ESTIMATE, "");
            cv.put(KEY_DISTRIBUTOR_TYPE_D, "");
            cv.put(KEY_DISTRIBUTOR_PARENT_NAME, "");
            cv.put(KEY_WORKING_BRAND, "");
            cv.put(KEY_WORKING_SINCE, newDistributorDetails.get(17));
            cv.put(KEY_OTHER_CONTACT_PERSON_NAME, newDistributorDetails.get(18));
            cv.put(KEY_OTHER_CONTACT_PERSON_PHN, newDistributorDetails.get(19));
            cv.put(KEY_FIRM_IMAGE, newDistributorDetails.get(20));
            cv.put(KEY_FIRM_IMAGE_TIME_STAMP, newDistributorDetails.get(21));

            if (firmImageLatLong.size() > 0) {

                String oLatLong = "";
                for (int i = 0; i < firmImageLatLong.size(); i++) {

                    oLatLong = oLatLong.concat(String.valueOf(firmImageLatLong.get(i)));
                    if (firmImageLatLong.size() > 1 && i < 1)
                        oLatLong = oLatLong.concat(",");
                }

                cv.put(KEY_FIRM_IMAGE_LATLONG, oLatLong);
            }
            cv.put(KEY_OPINION_ABOUT_DISTRIBUTOR, newDistributorDetails.get(22));
            cv.put(KEY_COMMENT, newDistributorDetails.get(23));
            cv.put(KEY_SERVER_SUBMIT_STATUS, "fail");
            cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" +
                    Calendar.getInstance().getTimeInMillis());

            db.insertOrThrow(TABLE_NEW_DISTRIBUTOR, null, cv);

            closeDb(db);
        }
        catch (Exception e){
            Log.d("TAG", "insertInNewDistributorTable error: "+e.getMessage());
        }


    }


    //insert  uploaded distributor table
    public void insertInNewDistributorTable2(String tempDid, ArrayList<String> newDistributorDetails,
                                             List<String> listBeatName, List<String> listProductDivision,
                                             List<Double> ownerImageLatLong, List<Double> firmImageLatLong) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NEW_DISTRIBUTOR_ID, tempDid);
        cv.put(KEY_NAME_OF_FIRM, newDistributorDetails.get(0));
        cv.put(KEY_FIRM_ADDRESS, newDistributorDetails.get(1));
        cv.put(KEY_PINCODE, newDistributorDetails.get(2));
        cv.put(KEY_CITY, newDistributorDetails.get(3));
        cv.put(KEY_STATE, newDistributorDetails.get(4));
        cv.put(KEY_OWNER_NAME_D, newDistributorDetails.get(5));
        cv.put(KEY_OWNER_MOBILE_NO1, newDistributorDetails.get(6));
        cv.put(KEY_EMAIL_ID, newDistributorDetails.get(7));
        cv.put(KEY_OWNER_MOBILE_NO2, newDistributorDetails.get(8));
        cv.put(KEY_GSTIN, newDistributorDetails.get(9));
        cv.put(KEY_FSSAI_NO, newDistributorDetails.get(10));
        cv.put(KEY_PAN_NO, newDistributorDetails.get(11));
        cv.put(KEY_MONTHLY_TURNOVER, newDistributorDetails.get(12));
        cv.put(KEY_OWNER_IMAGE, newDistributorDetails.get(13));
        cv.put(KEY_OWNER_IMAGE_TIME_STAMP, newDistributorDetails.get(14));
        cv.put(KEY_OWNER_IMAGE_LATLONG, ownerImageLatLong.toString());
        cv.put(KEY_TOTAL_NO_EMP, "");
        cv.put(KEY_SALES_PERSON, "");
        cv.put(KEY_ADMIN_PERSON, "");
        cv.put(KEY_DELIVERY_VEHICLE, "");
        cv.put(KEY_NO_OF_VEHICLE, "");
        cv.put(KEY_BEAT_NAME_D, String.valueOf(listBeatName));
        cv.put(KEY_NO_OF_SHOP_IN_BEAT, newDistributorDetails.get(15));
        cv.put(KEY_NO_OF_SHOP_COVERED, "");
        cv.put(KEY_NO_OF_GUMTI_COVERED, "");
        cv.put(KEY_TOTAL_NO_OF_SHOP, "");
        cv.put(KEY_NO_OF_WHOLESALER, "");
        cv.put(KEY_INVESTMENT_PLAN, newDistributorDetails.get(16));

        if (listProductDivision.size() > 0) {
            cv.put(KEY_PRODUCT_DIVISION, listProductDivision.toString());
        } else {
            cv.put(KEY_PRODUCT_DIVISION, "");
        }

        cv.put(KEY_MONTHLY_SALE_ESTIMATE, "");
        cv.put(KEY_DISTRIBUTOR_TYPE_D, "");
        cv.put(KEY_DISTRIBUTOR_PARENT_NAME, "");
        cv.put(KEY_WORKING_BRAND, "");
        cv.put(KEY_WORKING_SINCE, newDistributorDetails.get(17));
        cv.put(KEY_OTHER_CONTACT_PERSON_NAME, newDistributorDetails.get(18));
        cv.put(KEY_OTHER_CONTACT_PERSON_PHN, newDistributorDetails.get(19));
        cv.put(KEY_FIRM_IMAGE, newDistributorDetails.get(20));
        cv.put(KEY_FIRM_IMAGE_TIME_STAMP, newDistributorDetails.get(21));
        cv.put(KEY_FIRM_IMAGE_LATLONG, firmImageLatLong.toString());
        cv.put(KEY_OPINION_ABOUT_DISTRIBUTOR, newDistributorDetails.get(22));
        cv.put(KEY_COMMENT, newDistributorDetails.get(23));
        cv.put(KEY_OTHER_BRAND_NAME, newDistributorDetails.get(24));
        cv.put(KEY_DISTRIBUTOR_RECORDING, "rec");
        cv.put(KEY_SERVER_SUBMIT_STATUS, "success");
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" +
                Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_NEW_DISTRIBUTOR, null, cv);
        closeDb(db);
    }

    public Cursor getAllDataFromNewDistributorTable() {
        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_NEW_DISTRIBUTOR, new String[]{},
                KEY_SERVER_SUBMIT_STATUS + "=?", new String[]{"fail"},
                null, null, null);
    }

   /* public Cursor getAllDataFromNewDistributorTable2() {
        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_NEW_DISTRIBUTOR, new String[]{},
                null, null, null, null, null);
    }*/

    public Cursor getAllDataFromNewDistributorTable2() {
        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_NEW_DISTRIBUTOR,
                new String[]{
                        "name_of_firm",
                        "firm_address",
                        "pincode",
                        "city",
                        "state",
                        "owner_name",
                        "owner_mobile_no1",
                        "owner_mobile_no2",
                        "email_id",
                        "gstin",
                        "fssai_no",
                        "pan_no",
                        "monthly_turnover",
                        "beat_name",
                        "no_of_shop_in_beat",
                        "investment_plan",
                        "product_division",
                        "working_since",
                        "other_contact_person_name",
                        "other_contact_person_phn",
                        "opinion_about_distributor",
                        "owner_image_time_stamp",
                        "comment",
                        "other_brand"
                },
                null, null, null, null, null);
    }


    public boolean deleteSpecificDataFromNewDistributorTable(String tempDid) {
        SQLiteDatabase db = getWritableDb();

        boolean val = db.delete(TABLE_NEW_DISTRIBUTOR, KEY_NEW_DISTRIBUTOR_ID + "=?",
                new String[]{tempDid}) > 0;

        closeDb(db);
        return val;
    }

    public boolean deleteAllDataFromNewDistributorTable() {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_NEW_DISTRIBUTOR, KEY_SERVER_SUBMIT_STATUS + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public void deleteAllDataFromNewDistributorTable2() {
//        SQLiteDatabase db = getWritableDb();
//
//        db.execSQL("delete from " + TABLE_NEW_DISTRIBUTOR);
//        closeDb(db);
//    }

    public void deleteAllDataFromNewDistributorTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_NEW_DISTRIBUTOR, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteAllDataFromNewDistributorTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_NEW_DISTRIBUTOR, KEY_SERVER_SUBMIT_STATUS
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});
        closeDb(db);
    }

    public void insertInDistributorOrderTable(String did, String takenAt, String status, String date,
                                              String from, String latitude, String longitude) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ID_O, did);
        cv.put(KEY_DISTRIBUTOR_ORDER_TAKEN_O, takenAt);
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, status);
        cv.put(KEY_DISTRIBUTOR_ORDER_DATE_O, date);
        cv.put(KEY_DISTRIBUTOR_ORDER_LAT_O, latitude);
        cv.put(KEY_DISTRIBUTOR_ORDER_LONGT_O, longitude);
        cv.put(KEY_DISTRIBUTOR_ORDER_TYPE, from);
        cv.put(KEY_RECORD_DATE, date);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_DISTRIBUTOR_ORDER_ENTRY, null, cv);
        closeDb(db);
    }

    public boolean updateInDistributorOrder2(String did, String status) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, status);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY, cv,
                KEY_DISTRIBUTOR_ID_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{did, "cancelled"}) > 0;

        closeDb(db);
        return flag;
    }

    public boolean updateInDistributorOrder(String did, String status) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, status);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        boolean flag = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY, cv,
                KEY_DISTRIBUTOR_ID_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?", new String[]{did, "order"}) > 0;

        closeDb(db);
        return flag;
    }

    public boolean updateInDistributorStock(String did, String status) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, status);

        boolean flag = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY, cv, KEY_DISTRIBUTOR_ID_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{did, "stock"}) > 0;

        closeDb(db);
        return flag;
    }

    public boolean updateInDistributorClosing(String did, String status) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, status);

        boolean flag = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY, cv, KEY_DISTRIBUTOR_ID_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{did, "closing"}) > 0;

        closeDb(db);
        return flag;
    }

    public int updateInDistributorOrderTable2(String did) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ORDER_STATUS_O, "cancelled");
        cv.put(KEY_DISTRIBUTOR_ORDER_TYPE, "cancelled");
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        int flag = db.update(TABLE_DISTRIBUTOR_ORDER_ENTRY, cv, KEY_DISTRIBUTOR_ID_O + "=?", new String[]{did});

        closeDb(db);
        return flag;
    }

    public Cursor getStockFromDistributorOrderTable(String status) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY, new String[]{},
                KEY_DISTRIBUTOR_ORDER_STATUS_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{status, "stock"}, null, null, null);
    }

    public Cursor getClosingFromDistributorOrderTable(String status) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY, new String[]{},
                KEY_DISTRIBUTOR_ORDER_STATUS_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{status, "closing"}, null, null, null);
    }

    public Cursor getOrdersFromDistributorOrderTable(String status) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY, new String[]{},
                KEY_DISTRIBUTOR_ORDER_STATUS_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{status, "order"}, null, null, null);
    }


    public Cursor getAllFromDistributorOrderTable2(String status, String type) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY, new String[]{},
                KEY_DISTRIBUTOR_ORDER_STATUS_O + "=? AND " + KEY_DISTRIBUTOR_ORDER_TYPE + "=?",
                new String[]{status, type}, null, null, null);

    }

    public Cursor getSpecificFromDistributorOrderTable(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_ORDER_ENTRY, new String[]{},
                KEY_DISTRIBUTOR_ID_O + "=?", new String[]{did}, null, null, null);
    }

    public boolean deleteAllFromDistributorOrderTable(String did, String type) {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_DISTRIBUTOR_ORDER_ENTRY, KEY_DISTRIBUTOR_ID_O + "=? AND "
                + KEY_DISTRIBUTOR_ORDER_TYPE + "=?", new String[]{did, type}) > 0;

        closeDb(db);
        return flag;
    }

    public boolean deleteAllFromDistributorOrderTable() {
        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_DISTRIBUTOR_ORDER_ENTRY, KEY_DISTRIBUTOR_ORDER_STATUS_O + "=?",
                new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public void deleteAllFromDistributorOrderTable2() {
//        SQLiteDatabase db = getWritableDb();
//        db.execSQL("delete from " + TABLE_DISTRIBUTOR_ORDER_ENTRY);
//        closeDb(db);
//    }

    public void deleteAllFromDistributorOrderTable3(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_DISTRIBUTOR_ORDER_ENTRY, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void deleteAllFromDistributorOrderTable4(String date) {
        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_DISTRIBUTOR_ORDER_ENTRY, KEY_DISTRIBUTOR_ORDER_STATUS_O
                + "=? AND " + KEY_RECORD_DATE + "<>?", new String[]{"success", date});
        closeDb(db);
    }

    public void insertInTableChatHistory(String chatId, String msg, String date, String contact, String channel,
                                         String timeStamp, String image, String video, String audio, String docs,
                                         String loc, String rid, String feedbackBy, String serverStatus) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_CHAT_ID, chatId);
        cv.put(KEY_MESSAGE_CHAT, msg);
        cv.put(KEY_DATE_CHAT, date);
        cv.put(KEY_CONTACT_CHAT, contact);
        cv.put(KEY_CHANNEL_CHAT, channel);
        cv.put(KEY_TIME_STAMP_CHAT, timeStamp);
        cv.put(KEY_IMAGE_CHAT, image);
        cv.put(KEY_VIDEO_CHAT, video);
        cv.put(KEY_AUDIO_CHAT, audio);
        cv.put(KEY_DOCS_CHAT, docs);
        cv.put(KEY_LOCATION_CHAT, loc);
        cv.put(KEY_RETAILER_ID, rid);
        cv.put(KEY_FEEDBACK_BY, feedbackBy);
        cv.put(KEY_SERVER_SUBMIT_STATUS, serverStatus);
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_CHAT_HISTORY, null, cv);
        closeDb(db);

    }

    public void updateInTableChatHistory(String chatId) {
        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SERVER_SUBMIT_STATUS, "success");
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.update(TABLE_CHAT_HISTORY, cv, KEY_CHAT_ID + "=?", new String[]{chatId});
        closeDb(db);

    }

    public Cursor getDataFromTableChatHistory(String rid) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_CHAT_HISTORY, new String[]{}, KEY_RETAILER_ID + "=?", new String[]{rid},
                null, null, null);

    }

    public Cursor getDataFromTableChatHistory2(String rid) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_CHAT_HISTORY, new String[]{}, KEY_SERVER_SUBMIT_STATUS + "=?" + " AND " +
                KEY_RETAILER_ID + "=?", new String[]{"fail", rid}, null, null, null);
    }

    public boolean deleteAllChat(String id) {
        SQLiteDatabase db = getWritableDb();

        return db.delete(TABLE_CHAT_HISTORY, KEY_RETAILER_ID + "=?", new String[]{id}) > 0;
    }

    public void insertInPrimarySaleHistory(Item item/*String did, String name, String target, String ach, String date*/) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_ID_P, item.getItem1());
        cv.put(KEY_DISTRIBUTOR_NAME_P, item.getItem2());
        cv.put(KEY_DISTRIBUTOR_SALE_TARGET, item.getItem3());
        cv.put(KEY_DISTRIBUTOR_SALE_ACH, item.getItem4());
        cv.put(KEY_SALE_DATE, item.getItem5());
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_PRIMARY_SALE_HISTORY, null, cv);
        closeDb(db);
    }

//    public Cursor getDataFromTablePrimarySaleHistory(String did/*, String date*/) {
//
//        SQLiteDatabase db = getReadableDb();
//
//        return db.query(TABLE_PRIMARY_SALE_HISTORY, new String[]{},
//                KEY_DISTRIBUTOR_ID_P + "=?" /*+ " AND " + KEY_SALE_DATE + "=?"*/,
//                new String[]{did/*, date*/}, null, null, null);
//
//    }


    public void deleteAllFromTablePrimarySaleHistory() {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_PRIMARY_SALE_HISTORY, null, null);

        closeDb(db);
    }

    public void insertOtherActivity(String activity, String remarks, String date, String latitude, String longitude, String date2) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_OTHER_ACTIVITY, activity);
        cv.put(KEY_OTHER_ACTIVITY_REMARKS, remarks);
        cv.put(KEY_OTHER_ACTIVITY_DATE, date);
        cv.put(KEY_OTHER_ACTIVITY_LAT, latitude);
        cv.put(KEY_OTHER_ACTIVITY_LONGT, longitude);
        cv.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());
        cv.put(KEY_RECORD_DATE, date2);

        db.insertOrThrow(TABLE_OTHER_ACTIVITY, null, cv);
        closeDb(db);
    }

    public boolean updateOtherActivity(String traId, String status) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SERVER_SUBMIT_STATUS, status);


        boolean flag = db.update(TABLE_OTHER_ACTIVITY, cv, TRANSACTION_ID + "=?", new String[]{traId}) > 0;

        closeDb(db);
        return flag;
    }

    public Cursor getOtherActivity(String status) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_OTHER_ACTIVITY, new String[]{},
                KEY_SERVER_SUBMIT_STATUS + "=?", new String[]{status}, null, null, null);

    }


    public boolean deleteOtherActivity() {

        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_OTHER_ACTIVITY, KEY_SERVER_SUBMIT_STATUS + "=?", new String[]{"success"}) > 0;

        closeDb(db);
        return flag;
    }

    public boolean deleteOtherActivity2(String date) {

        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_OTHER_ACTIVITY, KEY_RECORD_DATE
                + "<>? AND " + KEY_SERVER_SUBMIT_STATUS + "=?", new String[]{date, "success"}) > 0;

        closeDb(db);
        return flag;
    }

//    public void deleteOtherActivity2() {
//
//        SQLiteDatabase db = getWritableDb();
//
//        db.execSQL("delete from " + TABLE_OTHER_ACTIVITY);
//        closeDb(db);
//    }

    public void deleteOtherActivity3(String date) {

        SQLiteDatabase db = getWritableDb();

        db.delete(TABLE_OTHER_ACTIVITY, KEY_RECORD_DATE + "<>?", new String[]{date});
        closeDb(db);
    }

    public void insertIntoDisTarAch(String did, String target, String ach) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DISTRIBUTOR_TARACH_ID, did);
        cv.put(KEY_DISTRIBUTOR_TARGET, target);
        cv.put(KEY_DISTRIBUTOR_ACHIEVEMENT, ach);
        //cv.put(TRANSACTION_ID, prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + Calendar.getInstance().getTimeInMillis());

        db.insertOrThrow(TABLE_DISTRIBUTOR_TARGET_ACH, null, cv);
        closeDb(db);
    }

    public Cursor getDistributorTarAch(String did) {
        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISTRIBUTOR_TARGET_ACH, new String[]{}, KEY_DISTRIBUTOR_TARACH_ID + "=?",
                new String[]{did}, null, null, null);

    }

    public boolean deleteDisTarAch() {

        SQLiteDatabase db = getWritableDb();

        boolean flag = db.delete(TABLE_DISTRIBUTOR_TARGET_ACH, null, null) > 0;

        closeDb(db);
        return flag;
    }


    public void insertIntoDisBeatMap(String did, String bid) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DID, did);
        cv.put(KEY_BID, bid);

        db.insertOrThrow(TABLE_DISBEAT_MAP, null, cv);
        closeDb(db);
    }

    public Cursor getDisBeatMap(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISBEAT_MAP, new String[]{}, KEY_DID + "=?",
                new String[]{did}, null, null, null);
    }

    public Cursor getDisBeatMap2(String beatId) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISBEAT_MAP, new String[]{}, KEY_BID + "=?",
                new String[]{beatId}, null, null, null);

    }

    public void deleteDisBeatMap() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DISBEAT_MAP, null, null);
        closeDb(db);
    }


    /*public void insertIntoDisSkuMap(String did, String skuId) {

        SQLiteDatabase db = getWritableDb();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DID, did);
        cv.put(KEY_SID, skuId);

        db.insertOrThrow(TABLE_DISSKU_MAP, null, cv);
        closeDb(db);

    }*/

    public void insertIntoDisSkuMap(String did, String skuId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = getWritableDb();
                ContentValues cv = new ContentValues();
                cv.put(KEY_DID, did);
                cv.put(KEY_SID, skuId);

                db.insertOrThrow(TABLE_DISSKU_MAP, null, cv);
            } catch (SQLiteException e) {
                Log.e("DatabaseError", "Error inserting data: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (db != null && db.isOpen()) {
                    closeDb(db);
                }
            }
        });
    }

    public Cursor getDisSkuMap(String did) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_DISSKU_MAP, new String[]{}, KEY_DID + "=?",
                new String[]{did}, null, null, null);

    }

    public void deleteDisSkuMap() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_DISSKU_MAP, null, null);
        closeDb(db);
    }


    public void insertPjp(String activity, String town, String did, String dis, String beatId,
                          String beat, String empId, String emp, String tc, String pc, String sale,
                          String date, String remarks) {

        SQLiteDatabase db = getWritableDb();
        Cursor cursor = getPjp(date);

        if (cursor != null && cursor.getCount() > 0) {

            ContentValues cv = new ContentValues();
            cv.put(KEY_ACTIVITY, activity);
            cv.put(KEY_TOWN, town);
            cv.put(KEY_DISTRIBUTOR_ID, did);
            cv.put(KEY_DISTRIBUTOR, dis);
            cv.put(KEY_BEAT_ID, beatId);
            cv.put(KEY_BEAT, beat);
            cv.put(KEY_EMP_ID, empId);
            cv.put(KEY_EMP, emp);
            cv.put(KEY_TC_PJP, tc);
            cv.put(KEY_PC_PJP, pc);
            cv.put(KEY_SALE_PJP, sale);
            cv.put(KEY_OTHER_ACTIVITY_REMARKS, remarks);

            db.update(TABLE_CREATE_PJP, cv, KEY_PJP_DATE + "=?", new String[]{date});

        } else {

            ContentValues cv = new ContentValues();
            cv.put(KEY_ACTIVITY, activity);
            cv.put(KEY_TOWN, town);
            cv.put(KEY_DISTRIBUTOR_ID, did);
            cv.put(KEY_DISTRIBUTOR, dis);
            cv.put(KEY_BEAT_ID, beatId);
            cv.put(KEY_BEAT, beat);
            cv.put(KEY_EMP_ID, empId);
            cv.put(KEY_EMP, emp);
            cv.put(KEY_TC_PJP, tc);
            cv.put(KEY_PC_PJP, pc);
            cv.put(KEY_SALE_PJP, sale);
            cv.put(KEY_PJP_DATE, date);
            cv.put(KEY_OTHER_ACTIVITY_REMARKS, remarks);

            db.insertOrThrow(TABLE_CREATE_PJP, null, cv);
        }


        closeDb(db);
    }


    public void insertInPendingOrders(HashMap<String, Object> params, int code, String message){
        List<HashMap> retailerCalls = (List<HashMap>) params.get("retailerCalls");
        String transactionId = (String) params.get("transactionId");
        List<HashMap> orders = (List<HashMap>) params.get("orders");

        HashMap<String, Object> retailerCallsArrayItem = retailerCalls.get(0);
        HashMap<String, Object> orderArrayItem = orders.get(0);

        List<HashMap> catalog = (List<HashMap>) orderArrayItem.get("catalogue");

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        String rid = (String) retailerCallsArrayItem.get("rid");
        String did = (String) retailerCallsArrayItem.get("did");
        cv.put(KEY_ORDER_PLACED_BY_RID, rid);
        cv.put(KEY_ORDER_PLACED_BY_DID, did);
        cv.put(KEY_ORDER_PLACED_TIME, (String) retailerCallsArrayItem.get("checkIn"));
        cv.put(KEY_ORDER_TYPE, (String) orderArrayItem.get("orderType"));
        cv.put(KEY_ORDER_CHECK_IN, (String) retailerCallsArrayItem.get("checkIn"));
        cv.put(KEY_ORDER_CHECK_OUT, (String) retailerCallsArrayItem.get("checkOut"));
        cv.put(KEY_ORDER_LAT, (String) retailerCallsArrayItem.get("latitude"));
        cv.put(KEY_ORDER_LONG, (String) retailerCallsArrayItem.get("longitude"));
        cv.put(KEY_SERVER_SUBMIT_STATUS, "fail");
        cv.put(KEY_STATUS_CODE, String.valueOf(code));
        cv.put(KEY_ERROR_MSG, message);
        cv.put(KEY_ORDER_DATE, (String) orderArrayItem.get("takenAt"));
        cv.put(KEY_ORDER_COMMENT, "comment");
        cv.put(TRANSACTION_ID, transactionId);
        cv.put(KEY_RECORD_DATE, (String) orderArrayItem.get("takenAt"));

        db.insertOrThrow(TABLE_PENDING_ORDER_PLACED_BY, null, cv);
        closeDb(db);

        for(int index = 0;index < catalog.size();index++){
            HashMap<String, Object> item = catalog.get(index);

            SQLiteDatabase db2 = getWritableDb();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ORDER_SKU_ID_L, (String) item.get("skuid"));
            contentValues.put(KEY_ORDER_SKU_BRAND_QTY_L,(String) item.get("qty"));
            contentValues.put(KEY_RETAILER_ID, rid);
            contentValues.put(KEY_DISTRIBUTOR_ID, did);
            contentValues.put(KEY_SERVER_SUBMIT_STATUS, "fail");

            db2.insertOrThrow(TABLE_PENDING_ORDERS, null, contentValues);
            closeDb(db2);
        }
    }

    public Cursor getPjp(String date) {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_CREATE_PJP, new String[]{}, KEY_PJP_DATE + "=?",
                new String[]{date}, null, null, null);

    }

    public Cursor getAllPjp() {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_CREATE_PJP, new String[]{}, null,
                null, null, null, null);

    }

    public void deleteAllPjp() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_CREATE_PJP, null, null);
        closeDb(db);
    }

    /*----------------------*******--------------------------*/

    private SQLiteDatabase getWritableDb() {

        return this.getWritableDatabase();

    }

    private SQLiteDatabase getReadableDb() {

        return this.getWritableDatabase();
    }

    private void closeDb(SQLiteDatabase db) {
        //db.close();
    }

    public void insertEmployeeRecord(String emp_id, String cid, String emp_name, String emp_ph_no,
                                     String emp_email, String username, String password,
                                     String zone, String zoneid, String state, String headquarter,
                                     String report_to, String designation, String filepath, String token) {

        SQLiteDatabase db = getWritableDb();

        ContentValues cv = new ContentValues();
        cv.put(KEY_EID, emp_id);
        cv.put(KEY_CID, cid);
        cv.put(KEY_EMP_NAME, emp_name);
        cv.put(KEY_EMP_PHONE, emp_ph_no);
        cv.put(KEY_EMP_MAIL, emp_email);
        cv.put(KEY_USERNAME, username);
        cv.put(KEY_PASSWORD, password);
        cv.put(KEY_ZONE, zone);
        cv.put(KEY_ZONE_ID, zoneid);
        cv.put(KEY_STATE, state);
        cv.put(KEY_HEADQUARTER, headquarter);
        cv.put(KEY_REPORTING_TO, report_to);
        cv.put(KEY_DESGNATION, designation);
        cv.put(KEY_PROFILE_PIC_URL, filepath);
        cv.put(KEY_TOKEN,token);

        db.delete(TABLE_EMP_RECORD, null, null);

        db.insertOrThrow(TABLE_EMP_RECORD, null, cv);
        closeDb(db);
    }

    public Cursor getEmpRocord() {

        SQLiteDatabase db = getReadableDb();

        return db.query(TABLE_EMP_RECORD, new String[]{}, null,
                null, null, null, null);

    }

    public void deleteEmp() {

        SQLiteDatabase db = getWritableDb();
        db.delete(TABLE_EMP_RECORD, null, null);
        closeDb(db);
    }


}
