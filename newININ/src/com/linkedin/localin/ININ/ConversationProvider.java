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

public class ConversationProvider extends ContentProvider {
	
	
	
	
	//Schema Design
	protected static final String DBNAME = "localdb";
	public static final String TABLE_CONVERSATION = "conversation";
	public static final String OTHERID = "otherid";
	public static final String TIMESTAMP = "timestamp";
	public static final String LASTSENTENCE = "lastsentance";
	
	
	private static String SQL_CREATE_RECORDS = "CREATE TABLE " +
		    TABLE_CONVERSATION +                       // Table's name
		    " (" +                           // The columns in the table
		    OTHERID + " INTEGER," +
		    TIMESTAMP + " TIMESTAMP," + 
		    LASTSENTENCE + " text) ";
	
	//Uri
	private static final String AUTHORITY = "com.linkedin.localin.ININ";
	public static final int RECORDS = 1;
	public static final int RECORD_ID = 2;
	private static final String RECORD_BASE_PATH = "record";
	
	public static final Uri RECORD_URI = Uri.parse("content://"+AUTHORITY+"/"+ RECORD_BASE_PATH);
	
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
			Log.v("DEBUG", "Sql sentent of creating records is "+SQL_CREATE_RECORDS);
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
	public int delete(Uri uri, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		int uriType = mURIMatcher.match(uri);
		
		switch(uriType){
			case RECORD_ID:
				SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(TABLE_CONVERSATION);
				queryBuilder.appendWhere(ConversationProvider.OTHERID + "=" + uri.getLastPathSegment());
				if(db == null){
					db = mainDatabase.getWritableDatabase();
				}
				int rowNum = db.delete(TABLE_CONVERSATION, arg1, arg2);
				return rowNum;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
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
			long rowId = db.insert(TABLE_CONVERSATION, OTHERID, initialValues);
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
		queryBuilder.setTables(TABLE_CONVERSATION);
		
		int uriType = mURIMatcher.match(uri);
		switch(uriType){
		case RECORDS:
			break;
		case RECORD_ID:
			queryBuilder.appendWhere(ConversationProvider.OTHERID + "=" + uri.getLastPathSegment());
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Log.e("DEBUG", "query triggered");
		Cursor cursor = queryBuilder.query(mainDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	
	@Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;
        int uriType = mURIMatcher.match(uri);
        switch (uriType) {
       
        case RECORD_ID:
            
            long personid = ContentUris.parseId(uri);
            String where = this.OTHERID + "=" + personid;// 
           
            count = db.update(this.TABLE_CONVERSATION, values, where, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }
}
