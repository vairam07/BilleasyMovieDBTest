package com.billeasy.billeasymoviedb.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME="movies_table";
    private static final String col1="id";
    private static final String col2="title";
    private static final String col3="image_url";
    private static final String col4="overview";
    private static final String col5="duration";
    private static final String col6="genre";

    public DatabaseHelper(@Nullable Context context) {
        super(context,TABLE_NAME,null,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + col1 + " INTEGER, " + col2 + " TEXT, " + col3 + " TEXT, " +
                col4 + " TEXT, " + col5 + " TEXT, " + col6 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int item1,String item2,String item3,String item4,String item5,String item6)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(col1,item1);
        contentValues.put(col2,item2);
        contentValues.put(col3,item3);
        contentValues.put(col4,item4);
        contentValues.put(col5,item5);
        contentValues.put(col6,item6);

        long result=database.insert(TABLE_NAME,null,contentValues);

        if (result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getData()
    {
        SQLiteDatabase database=this.getWritableDatabase();
        String query="SELECT * FROM " + TABLE_NAME;
        Cursor data=database.rawQuery(query,null);
        return data;
    }
}
