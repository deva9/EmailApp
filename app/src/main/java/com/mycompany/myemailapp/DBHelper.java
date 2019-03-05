package com.mycompany.myemailapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;// Table name
    public static final String DATABASE_NAME = "mydb";
    //public static final String COLUMN_FROM = "From";
    public static final String TABLE_NAME = "drafts";
    public static final String TABLE_2= "emailid";

    public static final String _TO = "ETo";
    public static final String _ID = "_id";
    public static final String _SUBJ = "Subject";
    public static final String _CC = "CC";
    public static final String _BODY = "Body";

    public static final String _EMAILID="EMAILID";
    public static final String _PASSWORD="PASS";
    public static final String _EID="_id";
    String buildSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _TO + " TEXT, " + _CC + " TEXT, " + _SUBJ + " TEXT, " + _BODY + " TEXT)";

    String buildSQL1= "CREATE TABLE IF NOT EXISTS " + TABLE_2 + "( " + _EID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _EMAILID + " TEXT, " + _PASSWORD + " TEXT)";

    /*private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;
    private Context con;
    private DBHelper dbh;*/
    private static final String TAG = DBHelper.class.getSimpleName();


    // this is a wrapper class. that means, from outside world, anyone will communicate with PersonDatabaseHelper,
    // but under the hood actually DatabaseOpenHelper class will perform database CRUD operations
    public DBHelper(Context aContext) {
        super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create your tables here

        Log.d(TAG, "onCreate SQL: " + buildSQL);
        Log.d(TAG, "onCreate SQL: " + buildSQL1);
        sqLiteDatabase.execSQL(buildSQL);
        sqLiteDatabase.execSQL(buildSQL1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Database schema upgrade code goes here
        String buildS = "DROP TABLE IF EXISTS " + TABLE_NAME;
        String buildS1 = "DROP TABLE IF EXISTS " + TABLE_2;
        Log.d(TAG, "onUpgrade SQL: " + buildS);
        Log.d(TAG, "onUpgrade SQL: " + buildS1);
        sqLiteDatabase.execSQL(buildS);       // drop previous table
        sqLiteDatabase.execSQL(buildS1);
        onCreate(sqLiteDatabase);               // create the table from the beginning
    }
}/*
    public DBHelper open(){
        openHelper = new DatabaseOpenHelper(con);
        database = openHelper.getWritableDatabase();
        return this;
    }

    public void insertdraft(String Eto, String CC, String Subject, String Body) {
        // we are using ContentValues to avoid sql format errors
        ContentValues contentValues = new ContentValues();
        contentValues.put(_TO, Eto);
        contentValues.put(_CC, CC);
        contentValues.put(_SUBJ, Subject);
        contentValues.put(_BODY, Body);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData() {
        String[] all=new  String[]{dbh._ID,dbh._TO,dbh._CC,dbh._SUBJ,dbh._BODY};
        Cursor cursor=database.query(TABLE_NAME,all,null,null,null,null,null);
        //String buildSQL = "SELECT * FROM " + TABLE_NAME;
        //Log.d(TAG, "getAllData SQL: " + buildSQL);
        if (cursor!=null)
            cursor.moveToFirst();
//        return database.rawQuery(buildSQL, null);
        return cursor;
    }

    // this DatabaseOpenHelper class will actually be used to perform database related operation

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Create your tables here

            String buildSQL = "CREATE TABLE " + TABLE_NAME + "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    _TO + " TEXT, " + _CC + " TEXT, " + _SUBJ + " TEXT, " + _BODY + " TEXT)";
            Log.d(TAG, "onCreate SQL: " + buildSQL);
            sqLiteDatabase.execSQL(buildSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // Database schema upgrade code goes here
            String buildSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            Log.d(TAG, "onUpgrade SQL: " + buildSQL);
            sqLiteDatabase.execSQL(buildSQL);       // drop previous table
            onCreate(sqLiteDatabase);               // create the table from the beginning
        }

    }
    public void deleteData(long memberID) {
        database.delete(dbh.TABLE_NAME, dbh._ID + "="
                + memberID, null);
    }
    }
    /*public Cursor getData(int id){
       // SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  database.rawQuery( "select * from drafts where id="+id+"", null );
        if (res!=null)
            res.moveToFirst();
        return res;
    }


    public Integer deletedraft (Integer id)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        int dd=database.delete("drafts", "id = ? ",
                new String[] { Integer.toString(id) });
        database.close();
        return dd;
    }*/

    /*public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table drafts " +
                        "(id integer primary key, To text,CC text,Subject text,Body text)"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS drafts");
        onCreate(db);
    }*/


//ArrayList<String> array_list = new ArrayList<String>();
//hp = new HashMap();
//SQLiteDatabase db = this.getWritableDatabase();
     /*res.moveToFirst();
        if (res.moveToFirst()){
            do {
                Draft d= new Draft();
                d.setID(Integer.parseInt(res.getString(0)));
                d.setTo(res.getString(1));
                d.setcc(res.getString(2));
                d.setsub(res.getString(3));
                d.setbody(res.getString(4));
                String id=res.getString(0) +"\n" + res.getString(1);
                MainActivity.ArrayDraft.add(id);
                // Adding contact to list
                array_list.add(d);
            }while (res.moveToNext());
        }
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_TO)));
            array_list.add(res.getString(res.getColumnIndex(COLUMN_SUBJECT)));
            res.moveToNext();
        }*/