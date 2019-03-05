package com.mycompany.myemailapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {

    private DBHelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c){
        ourcontext=c;
    }
    public SQLController open(){
        dbhelper=new DBHelper(ourcontext);
        database=dbhelper.getWritableDatabase();
        return this;
    }
    public void close(){
        dbhelper.close();
    }


    public void insertemail(String EID,String PASSWORD){
        ContentValues cv=new ContentValues();
        cv.put(dbhelper._EMAILID, EID);
        cv.put(dbhelper._PASSWORD, PASSWORD);
        database.insert(dbhelper.TABLE_2, null, cv);
    }

    public Cursor getemail(){
        String[] allmail=new String[]{dbhelper._EID,dbhelper._EMAILID,dbhelper._PASSWORD};
        Cursor cc=database.query(dbhelper.TABLE_2,allmail,null,null,null,null,null);
        if (cc!=null)
            cc.moveToFirst();
        return cc;
    }

    public String getSingleEntry(String userName) {
        //Cursor cursor = database.query(true,dbhelper.TABLE_2,new String[]
        //      {dbhelper._PASSWORD},dbhelper._EMAILID+"="+userName,null,null,null, null, null);
        //Cursor cursor=database.rawQuery("SELECT * FROM EMAIL WHERE EID= '"+userName,null);
        Cursor cursor = database.query(dbhelper.TABLE_2, null, dbhelper._EMAILID+"=?",new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(dbhelper._PASSWORD));
        cursor.close();
        return password;
    }

    public void insertdraft(String Eto, String CC, String Subject, String Body) {
        // we are using ContentValues to avoid sql format errors
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper._TO, Eto);
        contentValues.put(DBHelper._CC, CC);
        contentValues.put(DBHelper._SUBJ, Subject);
        contentValues.put(DBHelper._BODY, Body);
        database.insert(DBHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData() {
        String[] all=new  String[]{DBHelper._ID,DBHelper._TO,DBHelper._CC,DBHelper._SUBJ,DBHelper._BODY};
        Cursor cursor=database.query(DBHelper.TABLE_NAME,all,null,null,null,null,null);
        if (cursor!=null)
            cursor.moveToFirst();
        return cursor;
    }

    public void deleteData(String memberID) {
        /*String id=String.valueOf(memberID);
         database.execSQL("DELETE FROM "+DBHelper.TABLE_NAME+" WHERE "+DBHelper._ID+" ='"+id+"'");
        //database.delete(DBHelper.TABLE_NAME, DBHelper._ID + "=?" +memberID, null);*/
        database.delete(DBHelper.TABLE_NAME,DBHelper._ID + " =?", new String[] { memberID + ""});
        dbhelper.close();
    }
}