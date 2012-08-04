package com.linkedin.localin.ININ;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class RecordProvider extends ContentProvider {
	
	
	
	
	//Schema Design
	protected static final String DBNAME = "recorddb";
	public static final String TABLE_RECORD = "record";
	public static final String ID = "_id";
	public static final String BSSID = "bssid";
	public static final String RSSI = "rssi";
	public static final String APID = "_id";
	public static final String ID_RECORD = "record_id";
	public static final String TIMESTAMP = "timestamp";
	public static final String APLIST = "aplist";
	
	
	private static String SQL_CREATE_RECORDS = "CREATE TABLE " +
		    TABLE_RECORD +                       // Table's name
		    " (" +                           // The columns in the table
		    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    TIMESTAMP + " TIMESTAMP," + 
		    APLIST + " text ";
	
	static{
		for(String sensor: SamplingService.SENSORLIST.keySet()){
			SQL_CREATE_RECORDS += ","+sensor+"x real";
			SQL_CREATE_RECORDS += ","+sensor+"y real";
			SQL_CREATE_RECORDS += ","+sensor+"z real";
		}
		SQL_CREATE_RECORDS += ")";
	}
	
	
	//Uri
	private static final String AUTHORITY = "com.linkedin.itdev.orion.provider";
	public static final int RECORDS = 1;
	public static final int RECORD_ID = 2;
	private static final String RECORD_BASE_PATH = "record";
	private static final String APLIST_PATH = "aplist";
	public static final Uri RECORD_URI = Uri.parse("content://"+AUTHORITY+"/"+ RECORD_BASE_PATH);
	public static final Uri AP_URI = Uri.parse("content://"+AUTHORITY+"/"+RECORD_BASE_PATH+"/"+APLIST_PATH);
	
	private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mURIMatcher.addURI(AUTHORITY, RECORD_BASE_PATH, RECORDS);
		mURIMatcher.addURI(AUTHORITY, RECORD_BASE_PATH+"/#", RECORD_ID);
	}
 	
	//Module
	private MainDatabaseHelper mainDatabase;
	
	protected static final class MainDatabaseHelper extends SQLiteOpenHelper{
		MainDatabaseHelper(Context context){
			super(context, DBNAME,null,1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SQL_CREATE_RECORDS);
			Log.v(SamplingService.DEBUG_TAG, "Sql sentent of creating records is "+SQL_CREATE_RECORDS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.w("DEBUG", "Upgrading database. Existing contents will be lost.");
			db.execSQL("drop table if exists "+DBNAME);
			onCreate(db);
		}
	}
	
	//Initialization 
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mainDatabase =  new MainDatabaseHelper(getContext());
		return true;
	}
	
	//Operation 
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private SQLiteDatabase db = null;
	
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		//db = mainDatabase.getWritableDatabase(); //will create the database if not existing.
		int uriType = mURIMatcher.match(uri);
			
		switch(uriType){
		case RECORDS:
			if(db == null){
				db = mainDatabase.getWritableDatabase();
			}
			long rowId = db.insert(TABLE_RECORD, ID, initialValues);
			if(rowId > 0){
				Uri recordUri = ContentUris.withAppendedId(RECORD_URI, rowId);
				getContext().getContentResolver().notifyChange(recordUri, null);
				return recordUri;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		throw new SQLException("Fail to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_RECORD);
		
		int uriType = mURIMatcher.match(uri);
		switch(uriType){
		case RECORDS:
			break;
		case RECORD_ID:
			queryBuilder.appendWhere(RecordProvider.ID + "=" + uri.getLastPathSegment());
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Log.e(SamplingService.DEBUG_TAG, "query triggered");
		Cursor cursor = queryBuilder.query(mainDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
