package com.system.mobile.lay_bare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Mark on 12/15/2015.
 */
public class DataHandler {

	/**
	 * This is paramerters for creating tablesubjects
	 */
	//=======================================================
	//               LAY BARE USER DB
	//========================================================
	public static final String TABLE_USER_ACCOUNT        = "userdb";
	public static final String COLUMN_USERID             = "uid";
	public static final String COLUMN_USER_DATA_OBJECT   = "user_object_data";
	private static final String DATABASE_NAME            = "laybare.db";
	private static final int DATABASE_VERSION     		 = 19;
															//	BuildConfig.VERSION_CODE;
	//=======================================================
	//               GCIC IP TABLE DB
	//========================================================
	public static final String TABLE_IPTABLE = "iptable";
	public static final String COLUMN_IP     = "ipaddress";

	//=======================================================
	//              MENU TABLE
	//========================================================
	public static final String TABLE_IMG_MENU   = "img_menu";
	public static final String TABLE_IMG_BUTTON = "img_button";
	public static final String COLUMN_IMG_URL   = "image_url";
	public static final String COLUMN_IMG_NAME  = "image_name";
	public static final String COLUMN_IMG_TITLE = "image_title";
	public static final String COLUMN_IMG_PATH  = "image_path";

	//=======================================================
	//              TOKEN TABLE
	//========================================================
	public static final String TABLE_TOKEN 	      = "token_tbl";
	public static final String COLUMN_TOKEN 	  = "token";
	public static final String COLUMN_TOKEN_DATE  = "token_date";

	//=======================================================
	//               TRANSACTION TABLE
	//========================================================
	public static final String TABLE_APPOINTMENT 				= "tbl_appointment";
	public static final String COLUMN_APPOINTMENT_ID			= "appointment_id";
	public static final String COLUMN_APPOINTMENT_DATE	    = "appointment_datetime";
	public static final String COLUMN_APPOINTMENT_STATUS		= "appointment_status";
	public static final String COLUMN_APPOINTMENT_DETAILS		= "appointment_array_details";




	//=======================================================
	//              PROMOTION TABLE
	//========================================================
	public static final String TABLE_PROMOTIONS   			 = "promotion_tbl";
	public static final String COLUMN_PROMOTION_ID			 = "promotion_id";
	public static final String COLUMN_PROMOTION_TITLE		 = "promotion_title";
	public static final String COLUMN_PROMOTION_DESCRIPTION  = "promotion_desc";
	public static final String COLUMN_PROMOTION_DATE_START   = "promotion_date_start";
	public static final String COLUMN_PROMOTION_DATE_END     = "promotion_date_end";
	public static final String COLUMN_PROMOTION_TYPE     	 = "promotion_type";
	public static final String COLUMN_PROMOTION_IMAGE    	 = "promotion_image";
	public static final String COLUMN_PROMOTION_DATA    	 = "promotion_data";

	//=======================================================
	//              FAQ TABLE
	//========================================================
	public static final String TABLE_FAQ  			 = "faq_tbl";
	public static final String COLUMN_FAQ_ID		 = "faq_id";
	public static final String COLUMN_FAQ_CAT_ID	 = "faq_cat_id";
	public static final String COLUMN_FAQ_QUESTION   = "faq_question";
	public static final String COLUMN_FAQ_ANSWER	 = "faq_answer";


	//=======================================================
	//              FAQ CATEGORY
	//========================================================
	public static final String TABLE_FAQ_CATEGORY  			 = "faq_category_tbl";
	public static final String COLUMN_FAQ_ARRAY		 		 = "faq_cat_array";

	//=======================================================
	//=======================================================
	//             Navigation Buttons And Carousels
	//========================================================
	public static final String TABLE_BANNER  				= "banner_tbl";
	public static final String COLUMN_BANNER_VERSION		= "banner_version";
	public static final String COLUMN_BANNER_ARRAY			= "banner_array";

	//=======================================================
	//  Branches Arrays (display offline except for appointment)
	//========================================================
	public static final String TABLE_BRANCHES  				= "branch_tbl";
	public static final String COLUMN_BRANCH_VERSION		= "branch_version";
	public static final String COLUMN_BRANCH_ARRAY			= "branch_array";

	//=======================================================
	//  Commercial Arrays (display pop up)
	//========================================================
	public static final String TABLE_COMMERCIAL 				= "commercial_tbl";
	public static final String COLUMN_COMMERCIAL_VERSION 		= "commercial_version";
	public static final String COLUMN_COMMERCIAL_ARRAY 			= "commercial_array";

	//=======================================================
	// 	SERVICES TABLE
	//========================================================
	private static final String TABLE_SERVICES              	= "services";
	private static final String COLUMN_SERVICE_VERSION         	= "service_version";
	private static final String COLUMN_SERVICE_ARRAY        	= "service_array";

	//=======================================================
	//  PACKAGE TABLE
	//========================================================
	private static final String TABLE_PACKAGE             		= "package_tbl";
	private static final String COLUMN_PACKAGE_VERSION         	= "package_version";
	private static final String COLUMN_PACKAGE_ARRAY         	= "package_array";

	//=======================================================
	// PRODUCT TABLE
	//========================================================
	private static final String TABLE_PRODUCTS 				= "products";
	private static final String COLUMN_PRODUCT_VERSION 		= "product_version";
	private static final String COLUMN_PRODUCT_ARRAY 		= "product_array";

	//=======================================================
	// CACHE LOGS TABLE
	//========================================================
	private static final String TABLE_LOGS						= "logs_tbl";
	private static final String COLUMN_LOG_DATE_UPDATE_VERSION 	= "log_version_date";
	private static final String COLUMN_LOG_DATE_UPDATE_DATA		= "log_data_date";

	//=======================================================
	// TOTAL TRANSACTION TABLE
	//========================================================
	public static final String TABLE_TOTAL_TRANSACTION        	= "transaction_tbl";
	public static final String COLUMN_TOTAL_TRANSACTION_ARRAY   = "total_transaction_array";
	public static final String COLUMN_TOTAL_TRANSACTION_DATE    = "total_transaction_date";


	//=======================================================
	// TRANSACTION History LOGS
	//========================================================
	public static final String TABLE_TRANSACTION_LOGS        	= "transaction_logs_tbl";
	public static final String COLUMN_TRANSACTION_LOGS_ID   	= "transaction_logs_id";
	public static final String COLUMN_TRANSACTION_LOGS_DB_TYPE  = "transaction_logs_db_type";
	public static final String COLUMN_TRANSACTION_LOGS_DATE     = "transaction_logs_date";
	public static final String COLUMN_TRANSACTION_LOGS_OBJECT   = "transaction_logs_object";



	//=======================================================
	// PLC APPLICATION & REQUEST LOGS TABLE
	//========================================================
	public static final String TABLE_REQUEST_TBL        		= "request_logs_tbl";
	public static final String COLUMN_APPLICATION_LOGS_ARRAY   	= "application_logs_array";
	public static final String COLUMN_REVIEW_LOGS_ARRAY   		= "review_logs_array";

	//=======================================================
	// Branch schedule & technician
	//========================================================
	public static final String TABLE_BRANCH_SCHEDULE      		= "branch_schedule_tbl";
	public static final String COLUMN_BRANCH_ID   	    		= "column_branch_id";
	public static final String COLUMN_BRANCH_SCHEDULE   	    = "column_array_branch_sched";
	public static final String COLUMN_BRANCH_TECH_SCHEDULE   	= "column_array_tech_schedule";
	public static final String COLUMN_BRANCH_SCHEDULE_UPDATED   = "column_branch_updated";


    //=======================================================
    //  Commercial Arrays (display pop up)
    //========================================================
    public static final String TABLE_WAIVER 				    = "waiver_tbl";
    public static final String COLUMN_WAIVER_DATA 		        = "waiver_data";


	//=======================================================
	//  Rating & review
	//========================================================
	public static final String TABLE_BRANCH_RATING 				= "branch_review_tbl";
	public static final String COLUMN_REVIEW_TRANSACTION_ID 	= "branch_review_transaction_id";



	//=======================================================
	// Chat Message Thread & messaage ( client - Branch supervisor, Branch supervisor - Admin, client - Admin)
	//========================================================

	public static final String TABLE_MESSAGE_THREAD         	= "message_thread_tbl";
	public static final String COLUMN_THREAD_ID     			= "message_thread_id";
	public static final String COLUMN_THREAD_CREATED_BY    		= "message_thread_by";
	public static final String COLUMN_THREAD_CREATED_AT    		= "message_thread_created";
	public static final String COLUMN_THREAD_NAME    			= "message_thread_name";
	public static final String COLUMN_THREAD_PARTICIPANT_ID     = "message_thread_participant";
	public static final String COLUMN_THREAD_IS_SEEN     		= "message_thread_is_seen";

	public static final String TABLE_MESSAGE_CHAT 				= "message_chat_tbl";
	public static final String COLUMN_MESSAGE_ID 				= "message_chat_id";
	public static final String COLUMN_MESSAGE_SENDER_ID 		= "message_chat_sender_id";
	public static final String COLUMN_MESSAGE_RECEIPENT_ID 		= "message_chat_receipent_id";
	public static final String COLUMN_MESSAGE_TITLE				= "message_chat_title";
	public static final String COLUMN_MESSAGE_BODY 				= "message_chat_body";
	public static final String COLUMN_MESSAGE_DATA				= "message_chat_data";
	public static final String COLUMN_MESSAGE_DATETIME			= "message_chat_datetime";
	public static final String COLUMN_MESSAGE_IS_READ			= "message_chat_is_read";
	public static final String COLUMN_MESSAGE_IS_STATUS			= "message_chat_is_status";
	public static final String COLUMN_MESSAGE_THREAD_ID			= "message_chat_thread_id";

	public static final String TABLE_NOTIFICATION 				= "notification_tbl";
	public static final String COLUMN_NOTIFICATION_ID 			= "notification_id";
	public static final String COLUMN_NOTIFICATION_DATETIME 	= "notification_datetime";
	public static final String COLUMN_NOTIFICATION_TYPE 		= "notification_type";
	public static final String COLUMN_NOTIFICATION_IS_POP 		= "notification_if_pop"; //pop or unpop(0,1)
	public static final String COLUMN_NOTIFICATION_IS_SEEN 		= "notification_if_seen"; // seen or unseen(0,1)
	public static final String COLUMN_NOTIFICATION_OBJECT 		= "notification_object";


	public static final String TABLE_USER_ONLINE 				= "user_online_tbl";
	public static final String COLUMN_USER_ONLINE_ID			= "user_online_id";
	public static final String COLUMN_USER_ONLINE_NAME 			= "user_online_name";
	public static final String COLUMN_USER_ONLINE_LAST_ACTIVE	= "user_online_last_active";


	private static final String DATABASE_CREATE = "CREATE TABLE " +
			TABLE_USER_ACCOUNT
			+ "( " +
			COLUMN_USERID + " text not null," +
			COLUMN_USER_DATA_OBJECT + " text" +");";


	private static final String DATABASE_CREATE_IPTABLE = "CREATE TABLE " +
			TABLE_IPTABLE
			+ "( " +
			COLUMN_IP + " text not null"
			+ ");";


	public static final String DATABASE_IMAGE_CAROUSEL = "CREATE TABLE "+
			TABLE_IMG_MENU
			+ "( " +
			COLUMN_IMG_URL + " text, " +
			COLUMN_IMG_NAME + " text," +
			COLUMN_IMG_TITLE + " text"
			+ ");";

	public static final String DATABASE_IMAGE_BUTTON = "CREATE TABLE "+
			TABLE_IMG_BUTTON
			+ "( " +
				COLUMN_IMG_URL + " text, " +
				COLUMN_IMG_NAME + " text," +
				COLUMN_IMG_TITLE + " text,"+
				COLUMN_IMG_PATH + " text"
			+ ");";

	public static final String DATABASE_CREATE_SERVICES = "CREATE TABLE "+
			TABLE_SERVICES
			+ "( " +
				COLUMN_SERVICE_VERSION + " text, " +
				COLUMN_SERVICE_ARRAY + " text"
			+ ");";

	public static final String DATABASE_CREATE_PACKAGE = "CREATE TABLE "+
			TABLE_PACKAGE
			+ "( " +
				COLUMN_PACKAGE_VERSION + " text, " +
				COLUMN_PACKAGE_ARRAY + " text"
			+ ");";

	public static final String DATABASE_CREATE_PRODUCTS = "CREATE TABLE "+
			TABLE_PRODUCTS
			+ "( " +
				COLUMN_PRODUCT_VERSION 		+ " text, " +
				COLUMN_PRODUCT_ARRAY 		+ " text"
			+ ");";

	public static final String DATABASE_CREATE_COMMERCIAL = "CREATE TABLE "+
			TABLE_COMMERCIAL
			+ "( " +
				COLUMN_COMMERCIAL_VERSION + " text not null," +
				COLUMN_COMMERCIAL_ARRAY + " text not null"
			+ ");";

	public static final String DATABASE_CREATE_BRANCHES = "CREATE TABLE "+
			TABLE_BRANCHES
			+ "( " +
				COLUMN_BRANCH_VERSION + " text not null, " +
				COLUMN_BRANCH_ARRAY + " text not null"
			+ ");";

	public static final String DATABASE_CREATE_BANNER = "CREATE TABLE "+
			TABLE_BANNER
			+ "( " +
			COLUMN_BANNER_VERSION	+ " text, " +
			COLUMN_BANNER_ARRAY 	+ " text "
			+ ");";


	private static final String DATABASE_CREATE_TOKEN = "CREATE TABLE " +
			TABLE_TOKEN
			+ "( " +
			COLUMN_TOKEN + " text not null," +
			COLUMN_TOKEN_DATE + " text not null"
			+ ");";



	private static final String DATABASE_CREATE_APPOINTMENT  = "CREATE TABLE " +
			TABLE_APPOINTMENT
			+ "( " +
				COLUMN_APPOINTMENT_ID + " text not null," +
				COLUMN_APPOINTMENT_DATE + " text not null," +
				COLUMN_APPOINTMENT_STATUS + " text not null," +
				COLUMN_APPOINTMENT_DETAILS + " text not null"
			+ ");";

	public static final String DATABASE_PROMOTIONS = "CREATE TABLE "+
			TABLE_PROMOTIONS
			+ "( " +
			COLUMN_PROMOTION_ID 			+ " text, " +
			COLUMN_PROMOTION_TITLE 			+ " text, " +
			COLUMN_PROMOTION_TYPE			+ " text, " +
			COLUMN_PROMOTION_IMAGE			+ " text, " +
			COLUMN_PROMOTION_DESCRIPTION	+ " text, " +
			COLUMN_PROMOTION_DATE_START		+ " text, " +
			COLUMN_PROMOTION_DATA			+ " text, " +
			COLUMN_PROMOTION_DATE_END		+ " text "
			+ ");";

	public static final String DATABASE_FAQ = "CREATE TABLE "+
			TABLE_FAQ
			+ "( " +
			COLUMN_FAQ_ID			+ " text, " +
			COLUMN_FAQ_QUESTION 	+ " text, " +
			COLUMN_FAQ_ANSWER		+ " text, " +
			COLUMN_FAQ_CAT_ID  		+ " text "
			+ ");";

	public static final String DATABASE_FAQ_CATEGORY = "CREATE TABLE "+
			TABLE_FAQ_CATEGORY
			+ "( " +
			COLUMN_FAQ_ARRAY		+ " text "
			+ ");";

	public static final String DATABASE_LOGS = "CREATE TABLE "+
			TABLE_LOGS
			+ "( " +
			COLUMN_LOG_DATE_UPDATE_VERSION		+ " text, "+
			COLUMN_LOG_DATE_UPDATE_DATA			+ " text "
			+ ");";

	public static final String DATABASE_TRANSACTION = "CREATE TABLE "+
			TABLE_TOTAL_TRANSACTION
			+ "( " +
				COLUMN_TOTAL_TRANSACTION_ARRAY			+ " text, "+
				COLUMN_TOTAL_TRANSACTION_DATE			+ " text  "
			+ ");";

	public static final String DATABASE_TRANSACTION_LOGS = "CREATE TABLE "+
			TABLE_TRANSACTION_LOGS
			+ "( " +
			COLUMN_TRANSACTION_LOGS_ID			+ " text, "+
			COLUMN_TRANSACTION_LOGS_DB_TYPE		+ " text, "+
			COLUMN_TRANSACTION_LOGS_DATE		+ " text, "+
			COLUMN_TRANSACTION_LOGS_OBJECT		+ " text "
			+ ");";

	public static final String DATABASE_APPLICATION_LOGS = "CREATE TABLE "+
			TABLE_REQUEST_TBL
			+ "( " +
			COLUMN_APPLICATION_LOGS_ARRAY			+ " text, "+
			COLUMN_REVIEW_LOGS_ARRAY				+ " text "
			+ ");";

	public static final String DATABASE_BRANCH_SCHEDULE = "CREATE TABLE "+
			TABLE_BRANCH_SCHEDULE
			+ "( " +
			COLUMN_BRANCH_ID				+ " text, "+
			COLUMN_BRANCH_SCHEDULE			+ " text, "+
			COLUMN_BRANCH_TECH_SCHEDULE		+ " text , "+
			COLUMN_BRANCH_SCHEDULE_UPDATED	+ " text  "
			+ ");";

    public static final String DATABASE_WAIVER = "CREATE TABLE "+
            TABLE_WAIVER
            + "( " +
            COLUMN_WAIVER_DATA			+ " text "
            + ");";

	public static final String DATABASE_BRANCH_RATING = "CREATE TABLE "+
			TABLE_BRANCH_RATING
			+ "( " +
			COLUMN_REVIEW_TRANSACTION_ID				+ " text "
			+ ");";

	public static final String DATABASE_CHAT_THREAD = "CREATE TABLE "+
			TABLE_MESSAGE_THREAD
			+ "( " +
				COLUMN_THREAD_ID 				+ " text, " +
				COLUMN_THREAD_CREATED_BY		+ " text, " +
				COLUMN_THREAD_CREATED_AT 		+ " text, " +
				COLUMN_THREAD_NAME				+ " text, "+
				COLUMN_THREAD_IS_SEEN			+ " text, "+
				COLUMN_THREAD_PARTICIPANT_ID 	+ " text "
			+ ");";

	public static final String DATABASE_CHAT = "CREATE TABLE "+
			TABLE_MESSAGE_CHAT
			+ "( " +
			COLUMN_MESSAGE_ID 			+ " text, " +
			COLUMN_MESSAGE_SENDER_ID 	+ " text, " +
			COLUMN_MESSAGE_RECEIPENT_ID	+ " text, " +
			COLUMN_MESSAGE_TITLE		+ " text, " +
			COLUMN_MESSAGE_BODY			+ " text, " +
			COLUMN_MESSAGE_DATA  		+ " text, " +
			COLUMN_MESSAGE_DATETIME		+ " text, " +
			COLUMN_MESSAGE_IS_READ		+ " text, " +
			COLUMN_MESSAGE_IS_STATUS	+ " text, " +
			COLUMN_MESSAGE_THREAD_ID	+ " text "
			+ ");";



	public static final String DATABASE_NOTIFICATION = "CREATE TABLE "+
			TABLE_NOTIFICATION
			+ "( " +
			COLUMN_NOTIFICATION_ID 			+ " text, " +
			COLUMN_NOTIFICATION_DATETIME 	+ " text, " +
			COLUMN_NOTIFICATION_TYPE		+ " text, " +
			COLUMN_NOTIFICATION_OBJECT		+ " text, " +
			COLUMN_NOTIFICATION_IS_POP		+ " text ," +
			COLUMN_NOTIFICATION_IS_SEEN  	+ " text  "
			+ ");";

	private static final String DATABASE_CREATE_USER_ONLINE  = "CREATE TABLE " +
			TABLE_USER_ONLINE
			+ "( " +
			COLUMN_USER_ONLINE_ID 			+ " text not null, " +
			COLUMN_USER_ONLINE_NAME 		+ " text not null, " +
			COLUMN_USER_ONLINE_LAST_ACTIVE 	+ " text not null "
			+ ");";

	DataBaseHelper dbhelper;
	Context ctx;
	SQLiteDatabase db;

	public DataHandler(Context ctx){

		this.ctx=ctx;
		dbhelper=new DataBaseHelper(ctx);
	}

	private static class DataBaseHelper extends SQLiteOpenHelper{

		public DataBaseHelper(Context ctx){
			super(ctx,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE_IPTABLE);
			db.execSQL(DATABASE_IMAGE_CAROUSEL);
			db.execSQL(DATABASE_IMAGE_BUTTON);
			db.execSQL(DATABASE_CREATE_TOKEN);
			db.execSQL(DATABASE_CREATE_APPOINTMENT);
			db.execSQL(DATABASE_PROMOTIONS);
			db.execSQL(DATABASE_FAQ);
			db.execSQL(DATABASE_FAQ_CATEGORY);
			db.execSQL(DATABASE_CREATE_BANNER);
			db.execSQL(DATABASE_CREATE_SERVICES);
			db.execSQL(DATABASE_CREATE_PRODUCTS);
			db.execSQL(DATABASE_CREATE_PACKAGE);
			db.execSQL(DATABASE_CREATE_BRANCHES);
			db.execSQL(DATABASE_CREATE_COMMERCIAL);
			db.execSQL(DATABASE_LOGS);
			db.execSQL(DATABASE_TRANSACTION);
			db.execSQL(DATABASE_APPLICATION_LOGS);
			db.execSQL(DATABASE_BRANCH_SCHEDULE);
            db.execSQL(DATABASE_WAIVER);
			db.execSQL(DATABASE_BRANCH_RATING);
			db.execSQL(DATABASE_CHAT);
			db.execSQL(DATABASE_CHAT_THREAD);
			db.execSQL(DATABASE_NOTIFICATION);
			db.execSQL(DATABASE_CREATE_USER_ONLINE);
			db.execSQL(DATABASE_TRANSACTION_LOGS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.e(DataHandler.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ACCOUNT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_IPTABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMG_MENU);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMG_BUTTON);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAQ);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAQ_CATEGORY);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANNER);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTIONS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMERCIAL);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOTAL_TRANSACTION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST_TBL);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH_SCHEDULE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAIVER);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH_RATING);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_CHAT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_THREAD);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ONLINE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_LOGS);
			onCreate(db);
		}



	}
	public DataHandler open(){
		db=dbhelper.getWritableDatabase();
		return this;
	}

	public void close(){
		dbhelper.close();
	}


	//==================================================================
	//  					PROFILE
	//==================================================================

	public long insertUserAccount(String uid,String objectData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_USERID, uid);
		values.put(COLUMN_USER_DATA_OBJECT, objectData);
		return db.insertOrThrow(TABLE_USER_ACCOUNT, null, values);
	}

	public long updateUserAccount(String uid,String objectData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_USERID, uid);
		values.put(COLUMN_USER_DATA_OBJECT, objectData);
		return db.update(TABLE_USER_ACCOUNT, values,null,null );
	}

	public Cursor returnUserAccount() {
		Cursor cursor = db.query(TABLE_USER_ACCOUNT, new String[] { COLUMN_USERID,COLUMN_USER_DATA_OBJECT}, null, null, null, null, null);
		return cursor;
	}

	public int deleteUserAccount(){
		return db.delete(TABLE_USER_ACCOUNT, null, null);
	}


	//==================================================================
	//  					TOKEN
	//==================================================================

	public int deleteToken(){
		return db.delete(TABLE_TOKEN, null, null);
	}

	public long insertToken(String token,String date){
		ContentValues values = new ContentValues();
		values.put(COLUMN_TOKEN, token);
		values.put(COLUMN_TOKEN_DATE, date);
		return db.insertOrThrow(TABLE_TOKEN, null, values);
	}

	public Cursor returnToken(){
		Cursor cursor = db.query(TABLE_TOKEN, new String[] { COLUMN_TOKEN}, null, null, null, null, null);
		return cursor;
	}

	///==================================================================
	// 						TRANSACTIONS
	//==================================================================

	public Cursor returnTotalTransactions() {
		Cursor cursor = db.query(TABLE_TOTAL_TRANSACTION, new String[] {
				COLUMN_TOTAL_TRANSACTION_ARRAY,COLUMN_TOTAL_TRANSACTION_DATE
		}, null, null, null, null, null);
		return cursor;
	}

	public long insertTotalTransactions(String arrayData,String dateOnly) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TOTAL_TRANSACTION_ARRAY,arrayData);
		values.put(COLUMN_TOTAL_TRANSACTION_DATE,dateOnly);
		return db.insertOrThrow(TABLE_TOTAL_TRANSACTION,null,values);
	}

	public long updateTotalTransactions(String arrayData,String dateOnly) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TOTAL_TRANSACTION_ARRAY,arrayData);
		values.put(COLUMN_TOTAL_TRANSACTION_DATE,dateOnly);
		return db.update(TABLE_TOTAL_TRANSACTION,values,null, null);
	}

	public int deleteTotalTransactions() {
		return db.delete(TABLE_TOTAL_TRANSACTION,null,null);
	}


	///==================================================================
	// 		TRANSACTION LOGS
	//==================================================================

	public Cursor returnTransactionLogs(int offset) {

		int limit = 20;
		String orderBy =  "DATE("+COLUMN_TRANSACTION_LOGS_DATE+") DESC LIMIT "+limit+" OFFSET "+offset;
//		String whereClause = COLUMN_TRANSACTION_LOGS_DB_TYPE +" > "+ transaction_type_id;
		Log.e("orderBy",orderBy);
		Cursor cursor = db.query(TABLE_TRANSACTION_LOGS, new String[] {
				COLUMN_TRANSACTION_LOGS_ID,
				COLUMN_TRANSACTION_LOGS_DB_TYPE,
				COLUMN_TRANSACTION_LOGS_DATE,
				COLUMN_TRANSACTION_LOGS_OBJECT,
		}, null, null, null, null, orderBy);
		return cursor;
	}

	public Cursor returnTransactionLogsID(int transaction_id) {

		String myWhereClause = COLUMN_TRANSACTION_LOGS_ID+" = "+transaction_id;
		Cursor cursor = db.query(TABLE_TRANSACTION_LOGS, new String[] {
				COLUMN_TRANSACTION_LOGS_ID,
				COLUMN_TRANSACTION_LOGS_DB_TYPE,
				COLUMN_TRANSACTION_LOGS_DATE,
				COLUMN_TRANSACTION_LOGS_OBJECT,
		}, myWhereClause, null, null, null, null);
		return cursor;
	}


	public long insertTransactionLogs(int transaction_id,int dbType,String dateTime,JSONObject objectLogs)  {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TRANSACTION_LOGS_ID,transaction_id);
		values.put(COLUMN_TRANSACTION_LOGS_DB_TYPE,dbType);
		values.put(COLUMN_TRANSACTION_LOGS_DATE,dateTime);
		values.put(COLUMN_TRANSACTION_LOGS_OBJECT,objectLogs.toString());
		return db.insertOrThrow(TABLE_TRANSACTION_LOGS,null,values);
	}

	public long updateTransactionLogs(int transaction_id,int dbType,String dateTime,JSONObject objectLogs) {

		String myWhereClause = COLUMN_TRANSACTION_LOGS_ID+" = "+transaction_id;
		ContentValues values = new ContentValues();
		values.put(COLUMN_TRANSACTION_LOGS_ID,transaction_id);
		values.put(COLUMN_TRANSACTION_LOGS_DB_TYPE,dbType);
		values.put(COLUMN_TRANSACTION_LOGS_DATE,dateTime);
		values.put(COLUMN_TRANSACTION_LOGS_OBJECT,objectLogs.toString());
		return db.update(TABLE_TRANSACTION_LOGS,values,myWhereClause, null);
	}

	public int deleteTransactionLogs() {
		return db.delete(TABLE_TRANSACTION_LOGS,null,null);
	}




	//==================================================================
	//             PROMOTIONS
	//==================================================================

	public long insertPromotions(int id,String title,String description,String date_start,String date_end,String type,String image,String promotions_data) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_ID, id);
		values.put(COLUMN_PROMOTION_TITLE, title);
		values.put(COLUMN_PROMOTION_DESCRIPTION, description);
		values.put(COLUMN_PROMOTION_DATE_START, date_start);
		values.put(COLUMN_PROMOTION_DATE_END, date_end);
		values.put(COLUMN_PROMOTION_TYPE, type);
		values.put(COLUMN_PROMOTION_IMAGE, image);
		values.put(COLUMN_PROMOTION_DATA,promotions_data);
		return db.insertOrThrow(TABLE_PROMOTIONS, null, values);
	}

	public long updatePromotions(int id,String title,String description,String date_start,String date_end,String type,String image,String promotions_data) {

		String whereClause = COLUMN_PROMOTION_ID+" = "+id;
		ContentValues values = new ContentValues();
		values.put(COLUMN_PROMOTION_ID, id);
		values.put(COLUMN_PROMOTION_TITLE, title);
		values.put(COLUMN_PROMOTION_DESCRIPTION, description);
		values.put(COLUMN_PROMOTION_DATE_START, date_start);
		values.put(COLUMN_PROMOTION_DATE_END, date_end);
		values.put(COLUMN_PROMOTION_TYPE, type);
		values.put(COLUMN_PROMOTION_IMAGE, image);
		values.put(COLUMN_PROMOTION_DATA,promotions_data);
		return db.update(TABLE_PROMOTIONS,values,whereClause,null);
	}

	public Cursor returnSpecificPromotion(int id){

		String whereClause = COLUMN_PROMOTION_ID+" = "+id;
		Cursor c = db.query(TABLE_PROMOTIONS, new String[] {
				COLUMN_PROMOTION_ID,
				COLUMN_PROMOTION_TITLE,
				COLUMN_PROMOTION_DESCRIPTION,
				COLUMN_PROMOTION_DATE_START,
				COLUMN_PROMOTION_DATE_END,
				COLUMN_PROMOTION_TYPE,
				COLUMN_PROMOTION_IMAGE,
				COLUMN_PROMOTION_DATA
		}, whereClause, null, null, null, null);
		return c;
	}

	public Cursor returnPromotion(){
		Cursor c= db.query(TABLE_PROMOTIONS, new String[] {
				COLUMN_PROMOTION_ID,
				COLUMN_PROMOTION_TITLE,
				COLUMN_PROMOTION_DESCRIPTION,
				COLUMN_PROMOTION_DATE_START,
				COLUMN_PROMOTION_DATE_END,
				COLUMN_PROMOTION_TYPE,
				COLUMN_PROMOTION_IMAGE,
				COLUMN_PROMOTION_DATA
		}, null, null, null, null, null);
		return c;
	}

	public int deletePromotion(){
		return db.delete(TABLE_PROMOTIONS, null, null);
	}



	//==================================================================
	//              FAQ & CATREGORU
	//==================================================================

	public long insertFAQ(String id,String question,String answer,String category_id) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_FAQ_ID, id);
		values.put(COLUMN_FAQ_QUESTION, question);
		values.put(COLUMN_FAQ_ANSWER, answer);
		values.put(COLUMN_FAQ_CAT_ID, category_id);
		return db.insertOrThrow(TABLE_FAQ, null, values);
	}

	public Cursor returnFAQ(String id){
		String whereClause = COLUMN_FAQ_CAT_ID +" = '"+id+"'";
		Cursor c= db.query(TABLE_FAQ, new String[] {
				COLUMN_FAQ_ID,
				COLUMN_FAQ_QUESTION,
				COLUMN_FAQ_ANSWER
		}, whereClause, null, null, null, null);

		return c;
	}

	public int deleteFAQ(){
		return db.delete(TABLE_FAQ, null, null);
	}

	public long insertFAQCategory(String arrayCat) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_FAQ_ARRAY, arrayCat);
		return db.insertOrThrow(TABLE_FAQ_CATEGORY, null, values);
	}

	public Cursor returnFAQCategory(){
		Cursor c= db.query(TABLE_FAQ_CATEGORY, new String[] {
				COLUMN_FAQ_ARRAY
		}, null, null, null, null, null);
		return c;
	}

	public int deleteFAQCategory(){
		return db.delete(TABLE_FAQ_CATEGORY, null, null);
	}



	//==================================================================
	//            IP address CRUD
	//==================================================================

	public long insertIPAddress(String ipaddress) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_IP, ipaddress);
		return db.insertOrThrow(TABLE_IPTABLE, null, values);
	}

	public Cursor returnIPAddress(){
		Cursor c= db.query(TABLE_IPTABLE, new String[] {
				// _ID,
				COLUMN_IP
		}, null, null, null, null, null);
		if(c!=null){
			c.moveToFirst();
		}
		return c;
	}

	public int deleteIPAddress(){
		return db.delete(TABLE_IPTABLE, null, null);
	}


	//==================================================================
	//          BANNER
	//==================================================================


	public long insertBanner(String banner_version,String banner_array){
		ContentValues values = new ContentValues();
		values.put(COLUMN_BANNER_VERSION,banner_version);
		values.put(COLUMN_BANNER_ARRAY,banner_array);
		return db.insertOrThrow(TABLE_BANNER,null,values);
	}

	public int deleteBanner() {
		return db.delete(TABLE_BANNER, null, null);
	}

	public Cursor returnBanner() {
		Cursor cursor = db.query(TABLE_BANNER,
				new String[] {
						COLUMN_BANNER_VERSION,
						COLUMN_BANNER_ARRAY
				},null , null, null, null, null);
		return cursor;
	}



	///==================================================================
	//            PACKAGES
	//==================================================================


	public Cursor returnPackage() {
		Cursor cursor = db.query(TABLE_PACKAGE, new String[] {
				COLUMN_PACKAGE_VERSION,
				COLUMN_PACKAGE_ARRAY
		}, null, null, null, null, null);
		return cursor;
	}

	public long insertPackages(String version, String arrayData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PACKAGE_VERSION,version);
		values.put(COLUMN_PACKAGE_ARRAY,arrayData);
		return db.insertOrThrow(TABLE_PACKAGE,null,values);
	}

	public long updatePackages(String id, String version, String arrayData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PACKAGE_VERSION,version);
		values.put(COLUMN_PACKAGE_ARRAY,arrayData);
		return db.update(TABLE_PACKAGE,values,null, null);
	}

	public int deletePackage() {
		return db.delete(TABLE_PACKAGE,null,null);
	}



	///==================================================================
	//            SERVICES
	//==================================================================

	public long insertServices(String version, String arrayData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SERVICE_VERSION,version);
		values.put(COLUMN_SERVICE_ARRAY,arrayData);
		return db.insertOrThrow(TABLE_SERVICES,null,values);
	}

	public Cursor returnServices() {
		Cursor cursor = db.query(TABLE_SERVICES, new String[] {
				COLUMN_SERVICE_VERSION,
				COLUMN_SERVICE_ARRAY
		}, null, null, null, null, null);
		return cursor;
	}

	public boolean deleteService()
	{
		return db.delete(TABLE_SERVICES,null,null) > 0;
	}


	///==================================================================
	//            PRODUCTS CRUD
	//==================================================================

	public long insertProducts(String version, String arrayData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PRODUCT_VERSION,version);
		values.put(COLUMN_PRODUCT_ARRAY,arrayData);
		return db.insertOrThrow(TABLE_PRODUCTS,null,values);
	}
	public Cursor returnProducts() {
		Cursor cursor = db.query(TABLE_PRODUCTS, new String[] {
				COLUMN_PRODUCT_VERSION,
				COLUMN_PRODUCT_ARRAY,
		}, null, null, null, null, null);
		return cursor;
	}
	public boolean deleteProducts()
	{
		return db.delete(TABLE_PRODUCTS,null,null) > 0;
	}



	///==================================================================
	//           COMMERCIALS
	//==================================================================

	public long insertCommercial(String version,String array){
		ContentValues values = new ContentValues();
		values.put(COLUMN_COMMERCIAL_VERSION,version);
		values.put(COLUMN_COMMERCIAL_ARRAY,array);
		return db.insertOrThrow(TABLE_COMMERCIAL,null,values);
	}

	public Cursor returnCommercial() {
		Cursor cursor = db.query(TABLE_COMMERCIAL, new String[] {COLUMN_COMMERCIAL_VERSION,COLUMN_COMMERCIAL_ARRAY}, null, null, null, null, null);
		return cursor;
	}

	public int deleteCommercial() {
		return db.delete(TABLE_COMMERCIAL, null, null);
	}

	///==================================================================
			//           FOR APPOINTMENTS
	//==================================================================


	public long insertAppointments(String transaction_id,String dateTime,String transaction_status,String objectTransaction,String arrayTransactionItems){
		ContentValues values = new ContentValues();
		values.put(COLUMN_APPOINTMENT_ID,transaction_id);
		values.put(COLUMN_APPOINTMENT_DATE,dateTime);
		values.put(COLUMN_APPOINTMENT_STATUS,transaction_status);
		values.put(COLUMN_APPOINTMENT_DETAILS,objectTransaction);
		return db.insertOrThrow(TABLE_APPOINTMENT,null,values);

	}

	public long updateAppointments(String transaction_id,String dateTime,String transaction_status,String objectTransaction,String arrayTransactionItems){

		String where_clause = COLUMN_APPOINTMENT_ID +" = '"+transaction_id+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_APPOINTMENT_DATE,dateTime);
		values.put(COLUMN_APPOINTMENT_STATUS,transaction_status);
		values.put(COLUMN_APPOINTMENT_DETAILS,objectTransaction);
		return db.update(TABLE_APPOINTMENT,values,where_clause, null);
	}

	public Cursor returnAllAppointments(){
		String orderBy = "DATETIME("+COLUMN_APPOINTMENT_DATE+") DESC";
		Cursor cursor = db.query(TABLE_APPOINTMENT,
				new String[]{
						COLUMN_APPOINTMENT_ID,
						COLUMN_APPOINTMENT_DATE,
						COLUMN_APPOINTMENT_STATUS,
						COLUMN_APPOINTMENT_DETAILS
				}, null, null, null, null,orderBy);
		return cursor;
	}


	public Cursor returnSpecificAppointments(String transaction_id){
		String orderBy = "DATETIME("+COLUMN_APPOINTMENT_DATE+") DESC";
		String whereClause = COLUMN_APPOINTMENT_ID+" = '"+transaction_id+"'";
		Cursor cursor = db.query(TABLE_APPOINTMENT,
				new String[]{
						COLUMN_APPOINTMENT_ID,
						COLUMN_APPOINTMENT_DATE,
						COLUMN_APPOINTMENT_STATUS,
						COLUMN_APPOINTMENT_DETAILS
				}, whereClause, null, null, null, orderBy);
		return cursor;
	}

	public Cursor returnDateAppointments(String eventDate){

		String whereClause = "DATE(" + COLUMN_APPOINTMENT_DATE + ") = DATE('" + eventDate + "')";

		Cursor cursor = db.query(TABLE_APPOINTMENT,
				new String[]{
						COLUMN_APPOINTMENT_ID,
						COLUMN_APPOINTMENT_DATE,
						COLUMN_APPOINTMENT_STATUS,
						COLUMN_APPOINTMENT_DETAILS
				}, whereClause, null, null, null, null);
		return cursor;
	}

	public Cursor returnStatusAppointments(String transaction_status, boolean b){
		String whereClause = "";
		if(b == true){
			whereClause = COLUMN_APPOINTMENT_STATUS+" = '"+transaction_status+"'";
		}
		else{
			whereClause = COLUMN_APPOINTMENT_STATUS+" <> '"+transaction_status+"'";
		}

		Cursor cursor = db.query(TABLE_APPOINTMENT,
				new String[]{
						COLUMN_APPOINTMENT_ID,
						COLUMN_APPOINTMENT_DATE,
						COLUMN_APPOINTMENT_STATUS,
						COLUMN_APPOINTMENT_DETAILS
				}, whereClause, null, null, null, null);
		return cursor;
	}

	public long setTransactionAsCancelled(String id){
		String where_clause = COLUMN_APPOINTMENT_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(COLUMN_APPOINTMENT_STATUS,"cancelled");
		return db.update(TABLE_APPOINTMENT,values,where_clause, null);
	}

	public int deleteAppointment(){
		return db.delete(TABLE_APPOINTMENT,null,null);
	}




	///==================================================================
			//            FOR PLC APPLICATION
	//==================================================================

	public Cursor returnPLCApplicationAndReviewLogs() {
		Cursor cursor = db.query(TABLE_REQUEST_TBL, new String[] {
				COLUMN_APPLICATION_LOGS_ARRAY,
				COLUMN_REVIEW_LOGS_ARRAY
		}, null, null, null, null, null);
		return cursor;
	}

	public long insertPLCApplicationAndReviewLogs(String arrayApplication,String arrayReview) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_APPLICATION_LOGS_ARRAY,arrayApplication);
		values.put(COLUMN_REVIEW_LOGS_ARRAY,arrayReview);
		return db.insertOrThrow(TABLE_REQUEST_TBL,null,values);
	}

	public long updatePLCApplicationAndReviewLogs(String arrayApplication,String arrayReview) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_APPLICATION_LOGS_ARRAY,arrayApplication);
		values.put(COLUMN_REVIEW_LOGS_ARRAY,arrayReview);
		return db.update(TABLE_REQUEST_TBL,values,null, null);
	}

	public int deleteApplicationAndRequest(){
		return db.delete(TABLE_REQUEST_TBL,null,null);
	}


	///==================================================================
			//           BRANCH & SCHEDULES
	//==================================================================

	public Cursor returnBranchSchedule(String branch_id) {

		String whereClause = COLUMN_BRANCH_ID+" = '"+branch_id+"'";
		Cursor cursor = db.query(TABLE_BRANCH_SCHEDULE, new String[] {
                COLUMN_BRANCH_ID,
				COLUMN_BRANCH_SCHEDULE,
				COLUMN_BRANCH_TECH_SCHEDULE,
				COLUMN_BRANCH_SCHEDULE_UPDATED
		}, whereClause, null, null, null, null);
		return cursor;
	}

	public long insertBranchSchedule(String branch_id,String arrayBranchSchedule,String arrayTechnicianSchedule,String scheduleDateUpdated) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_BRANCH_ID,branch_id);
		values.put(COLUMN_BRANCH_SCHEDULE,arrayBranchSchedule);
		values.put(COLUMN_BRANCH_TECH_SCHEDULE,arrayTechnicianSchedule);
		values.put(COLUMN_BRANCH_SCHEDULE_UPDATED,scheduleDateUpdated);
		return db.insertOrThrow(TABLE_BRANCH_SCHEDULE,null,values);
	}

	public long updateBranchSchedule(String branch_id,String arrayBranchSchedule,String arrayTechnicianSchedule,String scheduleDateUpdated) {

		String whereClause = COLUMN_BRANCH_ID+" = '"+branch_id+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_BRANCH_ID,branch_id);
		values.put(COLUMN_BRANCH_SCHEDULE,arrayBranchSchedule);
		values.put(COLUMN_BRANCH_TECH_SCHEDULE,arrayTechnicianSchedule);
		values.put(COLUMN_BRANCH_SCHEDULE_UPDATED,scheduleDateUpdated);
		return db.update(TABLE_BRANCH_SCHEDULE,values,whereClause, null);

	}

	public long insertBranches(String branch_version,String branch_array){
		ContentValues values = new ContentValues();
		values.put(COLUMN_BRANCH_VERSION,branch_version);
		values.put(COLUMN_BRANCH_ARRAY,branch_array);
		return db.insertOrThrow(TABLE_BRANCHES,null,values);
	}

	public long updateBranches(String version, String arrayData) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_BRANCH_VERSION,version);
		values.put(COLUMN_BRANCH_ARRAY,arrayData);
		return db.update(TABLE_BRANCHES,values,null, null);
	}

	public int deleteBranches() {
		return db.delete(TABLE_BRANCHES, null, null);
	}

	public Cursor returnBranches() {
		Cursor cursor = db.query(TABLE_BRANCHES,
				new String[] {
						COLUMN_BRANCH_VERSION,
						COLUMN_BRANCH_ARRAY
				},null , null, null, null, null);
		return cursor;
	}

	public int deleteBranchSchedule(String branch_id){
		String whereClause = COLUMN_BRANCH_ID+" = '"+branch_id+"'";
		return db.delete(TABLE_REQUEST_TBL,whereClause,null);
	}




	///==================================================================
			//           WAIVER DATA
	//==================================================================

    public long insertWaiver(String waiverData) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_WAIVER_DATA,waiverData);
        return db.insertOrThrow(TABLE_WAIVER,null,values);
    }
	public Cursor returnWaiver() {

		Cursor cursor = db.query(TABLE_WAIVER, new String[] {
				COLUMN_WAIVER_DATA
		}, null, null, null, null, null);
		return cursor;

	}
    public int deleteWaiver(){
        return db.delete(TABLE_WAIVER,null,null);
    }



	///==================================================================
	//          BRanch Rating
	//==================================================================

	public long insertBranchRating(String transaction_id) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_REVIEW_TRANSACTION_ID,transaction_id);
		return db.insertOrThrow(TABLE_BRANCH_RATING,null,values);
	}

	public Cursor returnBranchRating(String transactionID) {
		String whereClause = COLUMN_REVIEW_TRANSACTION_ID+" = '"+transactionID+"'";
		Cursor cursor = db.query(TABLE_BRANCH_RATING, new String[] {
				COLUMN_REVIEW_TRANSACTION_ID
		}, whereClause, null, null, null, null);
		return cursor;
	}

	public int deleteBranchRating(){
		return db.delete(TABLE_BRANCH_RATING,null,null);
	}


	///==================================================================
	//         Message Chat
	//==================================================================


	public long insertChatThread(int thread_id, String name, String created_at, int created_by_id, String participant_id){

		ContentValues values = new ContentValues();
		values.put(COLUMN_THREAD_ID,thread_id);
		values.put(COLUMN_THREAD_NAME,name);
		values.put(COLUMN_THREAD_PARTICIPANT_ID,participant_id);
		values.put(COLUMN_THREAD_CREATED_BY,created_by_id);
		values.put(COLUMN_THREAD_CREATED_AT,created_at);
		values.put(COLUMN_THREAD_IS_SEEN,"");
		return db.insertOrThrow(TABLE_MESSAGE_THREAD,null,values);
	}

	public long updateChatThread(int thread_id, String name, String created_at, int created_by_id, String participant_id){
		String whereClause = COLUMN_THREAD_ID+" = "+thread_id;
		ContentValues values = new ContentValues();
		values.put(COLUMN_THREAD_ID,thread_id);
		values.put(COLUMN_THREAD_NAME,name);
		values.put(COLUMN_THREAD_PARTICIPANT_ID,participant_id);
		values.put(COLUMN_THREAD_CREATED_BY,created_by_id);
		values.put(COLUMN_THREAD_CREATED_AT,created_at);
		return db.update(TABLE_MESSAGE_THREAD,values,whereClause, null);
	}


	public Cursor returnChatThread(int thread_id) {
		String whereClause = COLUMN_THREAD_ID+" = '"+thread_id+"'";
		Cursor cursor = db.query(TABLE_MESSAGE_THREAD,
				new String[] {
						COLUMN_THREAD_ID,
						COLUMN_THREAD_PARTICIPANT_ID,
						COLUMN_THREAD_NAME,
						COLUMN_THREAD_CREATED_BY,
						COLUMN_THREAD_CREATED_AT,
						COLUMN_THREAD_IS_SEEN
				},whereClause , null, null, null, null);
		return cursor;
	}

	public Cursor returnAllThread() {
		Cursor cursor = db.query(TABLE_MESSAGE_THREAD,
				new String[] {
						COLUMN_THREAD_ID,
						COLUMN_THREAD_PARTICIPANT_ID,
						COLUMN_THREAD_NAME,
						COLUMN_THREAD_CREATED_BY,
						COLUMN_THREAD_CREATED_AT,
						COLUMN_THREAD_IS_SEEN
				},null , null, null, null, COLUMN_THREAD_CREATED_AT+" DESC");
		return cursor;
	}

	public int countUnreadMessage(){
		int countMessage = 0;
		String whereClause = COLUMN_THREAD_IS_SEEN+" <> 'seen'";
		Cursor cursor = db.query(TABLE_MESSAGE_THREAD,
				new String[] {
						COLUMN_THREAD_ID,
						COLUMN_THREAD_PARTICIPANT_ID,
						COLUMN_THREAD_NAME,
						COLUMN_THREAD_CREATED_BY,
						COLUMN_THREAD_CREATED_AT,
						COLUMN_THREAD_IS_SEEN
				},whereClause , null, null, null, null);
		countMessage = cursor.getCount();
		Log.e("COUNT DB THREAD: ",String.valueOf(countMessage));
		return countMessage;
	}

	public long insertChatMessage(int chatID, int chatSenderID, int chatReceipentID,int thread_id, String title, String body, String messageData, String dateTime, String isRead, String isStatus){

		ContentValues values = new ContentValues();
		values.put(COLUMN_MESSAGE_ID,chatID);
		values.put(COLUMN_MESSAGE_SENDER_ID,chatSenderID);
		values.put(COLUMN_MESSAGE_RECEIPENT_ID,chatReceipentID);
		values.put(COLUMN_MESSAGE_THREAD_ID,thread_id);
		if(title != null || !title.equals("") || !title.isEmpty() || !title.equals(null)){
			values.put(COLUMN_MESSAGE_TITLE,title);
		}
		values.put(COLUMN_MESSAGE_BODY,body);
		values.put(COLUMN_MESSAGE_DATA,messageData);
		values.put(COLUMN_MESSAGE_DATETIME,dateTime);
		values.put(COLUMN_MESSAGE_IS_READ,isRead);
		values.put(COLUMN_MESSAGE_IS_STATUS,isStatus);
		return db.insertOrThrow(TABLE_MESSAGE_CHAT,null,values);
	}

	public long updateThreadTime(int thread_id,String status, String message_created){

		String whereClause 		= COLUMN_THREAD_ID+" = "+thread_id;
		ContentValues values 	= new ContentValues();
		values.put(COLUMN_THREAD_CREATED_AT,message_created);
		values.put(COLUMN_THREAD_IS_SEEN,status);
		return db.update(TABLE_MESSAGE_THREAD,values,whereClause, null);
	}


	public long updateChatMessage(int chatID, int chatSenderID, int chatReceipentID,int thread_id, String title, String body, String messageData, String dateTime, String isRead,String isStatus) {

		String whereClause = COLUMN_MESSAGE_ID+" = '"+chatID+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_MESSAGE_SENDER_ID,chatSenderID);
		values.put(COLUMN_MESSAGE_RECEIPENT_ID,chatReceipentID);
		values.put(COLUMN_MESSAGE_THREAD_ID,thread_id);
		if(title != null || !title.equals("") || !title.isEmpty() || !title.equals(null)){
			values.put(COLUMN_MESSAGE_TITLE,title);
		}
		values.put(COLUMN_MESSAGE_BODY,body);
		values.put(COLUMN_MESSAGE_DATA,messageData);
		values.put(COLUMN_MESSAGE_DATETIME,dateTime);
		values.put(COLUMN_MESSAGE_IS_READ,isRead);
		values.put(COLUMN_MESSAGE_IS_STATUS,isStatus);
		return db.update(TABLE_MESSAGE_CHAT,values,whereClause, null);
	}

	public Cursor returnChatMessageByID(int chat_id) {

		String whereClause = COLUMN_MESSAGE_ID+" = "+chat_id+" AND "+COLUMN_MESSAGE_TITLE+" = 'null'";
		Cursor cursor = db.query(TABLE_MESSAGE_CHAT,
				new String[] {
						COLUMN_MESSAGE_ID,
						COLUMN_MESSAGE_SENDER_ID,
						COLUMN_MESSAGE_RECEIPENT_ID,
						COLUMN_MESSAGE_THREAD_ID,
						COLUMN_MESSAGE_TITLE,
						COLUMN_MESSAGE_BODY,
						COLUMN_MESSAGE_DATA,
						COLUMN_MESSAGE_DATETIME,
						COLUMN_MESSAGE_IS_READ,
						COLUMN_MESSAGE_IS_STATUS
				},whereClause , null, null, null, null);
		return cursor;
	}

	public int returnLastChatMessage(int thread_id) {
		int latestMessageID = 0;
		String whereClause = COLUMN_MESSAGE_THREAD_ID+" = "+thread_id;
		Cursor cursor = db.query(TABLE_MESSAGE_CHAT,
				new String[] {
						COLUMN_MESSAGE_ID
				},whereClause , null, null, null, COLUMN_MESSAGE_DATETIME+" DESC ");
		cursor.moveToFirst();
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			latestMessageID = Integer.parseInt(cursor.getString(0));
		}
		return latestMessageID;
	}

	public Cursor returnLastChatMessageByThreadID(int thread_id) {

		String limit	    = COLUMN_MESSAGE_DATETIME+" DESC  LIMIT 1";
		String whereClause = COLUMN_MESSAGE_THREAD_ID+" = "+thread_id+" AND "+COLUMN_MESSAGE_TITLE+" = 'null'";
		Cursor cursor = db.query(TABLE_MESSAGE_CHAT, new String[] {
				COLUMN_MESSAGE_ID,
				COLUMN_MESSAGE_SENDER_ID,
				COLUMN_MESSAGE_RECEIPENT_ID,
				COLUMN_MESSAGE_THREAD_ID,
				COLUMN_MESSAGE_TITLE,
				COLUMN_MESSAGE_BODY,
				COLUMN_MESSAGE_DATA,
				COLUMN_MESSAGE_DATETIME,
				COLUMN_MESSAGE_IS_READ,
				COLUMN_MESSAGE_IS_STATUS
		},whereClause , null, null, null, limit);
		return cursor;
	}


	public Cursor returnChatMessageWithOffset(int thread_id,int offset) {

		String limit	   = COLUMN_MESSAGE_DATETIME+" DESC LIMIT 20 OFFSET "+offset;
		String whereClause = "";
		whereClause =  COLUMN_MESSAGE_THREAD_ID+" = "+thread_id+" AND "+COLUMN_MESSAGE_TITLE+" = 'null'";
		Cursor cursor = db.query(TABLE_MESSAGE_CHAT, new String[] {
						COLUMN_MESSAGE_ID,
						COLUMN_MESSAGE_SENDER_ID,
						COLUMN_MESSAGE_RECEIPENT_ID,
						COLUMN_MESSAGE_THREAD_ID,
						COLUMN_MESSAGE_TITLE,
						COLUMN_MESSAGE_BODY,
						COLUMN_MESSAGE_DATA,
						COLUMN_MESSAGE_DATETIME,
						COLUMN_MESSAGE_IS_READ,
						COLUMN_MESSAGE_IS_STATUS
				},whereClause , null, null, null, limit);
		return cursor;
	}

	public Cursor returnLatestChatMessage(int thread_id) {

		String limit	   = COLUMN_MESSAGE_DATETIME+" DESC";
		String whereClause = COLUMN_MESSAGE_THREAD_ID+" = "+thread_id +" AND "+COLUMN_MESSAGE_TITLE+" = 'null'";
		Cursor cursor = db.query(TABLE_MESSAGE_CHAT, new String[] {
				COLUMN_MESSAGE_ID,
				COLUMN_MESSAGE_SENDER_ID,
				COLUMN_MESSAGE_RECEIPENT_ID,
				COLUMN_MESSAGE_THREAD_ID,
				COLUMN_MESSAGE_TITLE,
				COLUMN_MESSAGE_BODY,
				COLUMN_MESSAGE_DATA,
				COLUMN_MESSAGE_DATETIME,
				COLUMN_MESSAGE_IS_READ,
				COLUMN_MESSAGE_IS_STATUS
		},whereClause , null, null, null, limit);
		return cursor;
	}

	public long setChatAsRead(int thread_id,String datetime) {

		String whereClause = COLUMN_MESSAGE_THREAD_ID+" = "+thread_id;
		ContentValues values = new ContentValues();
		values.put(COLUMN_MESSAGE_IS_READ,datetime);
		values.put(COLUMN_MESSAGE_IS_STATUS,"seen");
		return db.update(TABLE_MESSAGE_CHAT,values,whereClause, null);
	}

	public long setThreadAsSeen(int threadID){
		String whereClause 		= COLUMN_THREAD_ID+" = "+threadID;
		ContentValues values 	= new ContentValues();
		values.put(COLUMN_THREAD_IS_SEEN,"seen");
		return db.update(TABLE_MESSAGE_THREAD,values,whereClause, null);
	}


	public int deleteSpecificChatMessage(int chatID) {
		String whereClause = COLUMN_MESSAGE_ID+" = "+chatID;
		return db.delete(TABLE_MESSAGE_CHAT, whereClause, null);
	}


	public int deleteAllChatMessage() {
		return db.delete(TABLE_MESSAGE_CHAT, null, null);
	}

	public int deleteAllChatThread() {
		return db.delete(TABLE_MESSAGE_THREAD, null, null);
	}




	///==================================================================
	//        Notifications
	//==================================================================


	public long insertNotification(int notifID, String dateTime,  String type, JSONObject objectNotification,int isPop, int isRead){

		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTIFICATION_ID,notifID);
		values.put(COLUMN_NOTIFICATION_DATETIME,dateTime);
		values.put(COLUMN_NOTIFICATION_TYPE,type);
		values.put(COLUMN_NOTIFICATION_OBJECT ,objectNotification.toString());
		values.put(COLUMN_NOTIFICATION_IS_POP,isPop);
		values.put(COLUMN_NOTIFICATION_IS_SEEN,isRead);
		return db.insertOrThrow(TABLE_NOTIFICATION,null,values);
	}

	public long updateNotification(int notifID, String dateTime,  String type, JSONObject objectNotification,int isPop, int isRead){
		String whereClause = COLUMN_NOTIFICATION_ID+" = '"+notifID+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTIFICATION_ID,notifID);
		values.put(COLUMN_NOTIFICATION_DATETIME,dateTime);
		values.put(COLUMN_NOTIFICATION_TYPE,type);
		values.put(COLUMN_NOTIFICATION_OBJECT ,objectNotification.toString());
		values.put(COLUMN_NOTIFICATION_IS_POP,isPop);
		values.put(COLUMN_NOTIFICATION_IS_SEEN,isRead);
		return db.update(TABLE_NOTIFICATION,values,whereClause, null);
	}


	public Cursor returnNotification(int notifID) {
		String 	whereClause = COLUMN_NOTIFICATION_ID+" = '"+notifID+"'";
		Cursor cursor = db.query(TABLE_NOTIFICATION,
				new String[] {
						COLUMN_NOTIFICATION_ID,
						COLUMN_NOTIFICATION_DATETIME,
						COLUMN_NOTIFICATION_TYPE,
						COLUMN_NOTIFICATION_OBJECT,
						COLUMN_NOTIFICATION_IS_POP,
						COLUMN_NOTIFICATION_IS_SEEN
				},whereClause , null, null, null, COLUMN_NOTIFICATION_DATETIME+" DESC");
		return cursor;
	}

	public Cursor returnAllNotification() {

		Cursor cursor = db.query(TABLE_NOTIFICATION,
				new String[] {
						COLUMN_NOTIFICATION_ID,
						COLUMN_NOTIFICATION_DATETIME,
						COLUMN_NOTIFICATION_TYPE,
						COLUMN_NOTIFICATION_OBJECT,
						COLUMN_NOTIFICATION_IS_POP,
						COLUMN_NOTIFICATION_IS_SEEN
				},null , null, null, null, COLUMN_NOTIFICATION_DATETIME+" DESC");
		return cursor;
	}

	public int deleteAllNotification() {
		return db.delete(TABLE_NOTIFICATION, null, null);
	}

	public int deleteNotification(int notifID) {
		String whereClause =  COLUMN_NOTIFICATION_ID + " = '"+notifID+"'";
		return db.delete(TABLE_NOTIFICATION, whereClause, null);
	}

	public long setNotificationAsRead(int notifID){
		String whereClause = COLUMN_NOTIFICATION_ID+" = "+notifID;
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOTIFICATION_IS_POP,1);
		values.put(COLUMN_NOTIFICATION_IS_SEEN,1);
		return db.update(TABLE_NOTIFICATION,values,whereClause, null);
	}

	///==================================================================
	//        User's last activity
	//==================================================================

	public long insertUserOnline(int userID,String user_name,String lastActivity) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_USER_ONLINE_ID,userID);
		values.put(COLUMN_USER_ONLINE_NAME,user_name);
		values.put(COLUMN_USER_ONLINE_LAST_ACTIVE,lastActivity);
		return db.insertOrThrow(TABLE_USER_ONLINE,null,values);
	}

	public long updateUserOnlineLastActivity(int userID,String lastActivity) {

		String whereClause = COLUMN_USER_ONLINE_ID+" = "+userID;
		ContentValues values = new ContentValues();
		values.put(COLUMN_USER_ONLINE_LAST_ACTIVE,lastActivity);
		return db.update(TABLE_USER_ONLINE,values,whereClause, null);
	}

	public Cursor returnUserOnlineLastActivity(int userID) {
		String whereClause = COLUMN_USER_ONLINE_ID+" = "+userID;
		Cursor cursor = db.query(TABLE_USER_ONLINE,
				new String[] {
						COLUMN_USER_ONLINE_ID,
						COLUMN_USER_ONLINE_NAME,
						COLUMN_USER_ONLINE_LAST_ACTIVE
				},whereClause , null, null, null, null);
		return cursor;
	}

}
