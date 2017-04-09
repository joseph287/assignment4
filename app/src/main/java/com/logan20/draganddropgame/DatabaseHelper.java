package com.logan20.draganddropgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/** Outside codes were used in this project. jstjoy creation
 * This class abstracts the information as well as provides an interface for database operations
 */

class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyDatabase.db";

    DatabaseHelper(Context c){
        super(c,DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + DatabaseUserEntry.TABLE_NAME +
                "(" + DatabaseUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + DatabaseUserEntry.FIRST_NAME + " TEXT, "
                + DatabaseUserEntry.LAST_NAME + " TEXT, "
                + DatabaseUserEntry.EMAIL + " TEXT, "
                + DatabaseUserEntry.PASSWORD + " TEXT, "
                + DatabaseUserEntry.TYPE + " TEXT)"; //SQL string representing table

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES); //this will create the table if it doesn't exist
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseUserEntry.TABLE_NAME; //SQL string for table deletion
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES); //delete table
        onCreate(sqLiteDatabase);//re-create the table
    }

    static class DatabaseUserEntry implements BaseColumns { //static class representing the columns the database has
        static final String TABLE_NAME = "USERS";
        static final String FIRST_NAME = "FNAME";
        static final String LAST_NAME = "LNAME";
        static final String EMAIL = "EMAIL";
        static final String PASSWORD = "PASSWORD";
        static final String TYPE = "USERTYPE";

    }
}
