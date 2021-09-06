package com.example.plantpalplz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

//class DatabaseManipulator
public class DatabaseManipulator {
    //create private static final string DATABASE_NAME equal to "savedcitites.db"
    private static final String DATABASE_NAME = "savedsettings.db";
    //create private static int DATABASE_VERSION equal to 4 - I have messed up more than once so we're on version 4
    private static int DATABASE_VERSION = 2;
    //create static final String TABLE_NAME equal to "newtable"
    static final String TABLE_NAME = "newtable";
    //create private static Context object context
    private static Context context;
    //create static SQLiteDatabase object db
    static SQLiteDatabase db;
    //create private SQLiteStatement object insetStmt
    private SQLiteStatement insertStmt;

    //create private static final String INSERT equal to makeup of SQL statement with TABLE_NAME variable and others in too
    private static final String INSERT = "insert into " + TABLE_NAME
            + " (name,value) values (?,?)";

    //constructor method
    public DatabaseManipulator(Context context)
    {
        //set DatabaseManipulator context equal to context from MainActivity
        DatabaseManipulator.context = context;
        //create OpenHelper object openHelper
        OpenHelper openHelper = new OpenHelper(this.context);
        //set DatabaseManipulator.db equal to value from openHelper.getWritableDatabase()
        DatabaseManipulator.db = openHelper.getWritableDatabase();
        //set insertStmt equal to DatabaseManipulator.db.compileStatment() method with INSET as parameter
        this.insertStmt = DatabaseManipulator.db.compileStatement(INSERT);
    }

    //method insert, takes String returns long
    public long insert(String name, String value)
    {
        //call insertStmt.bindString() with parameters 1 and name
        this.insertStmt.bindString(1, name);
        this.insertStmt.bindString(2, value);
        //return value from insertStmt.executeInsert()
        return this.insertStmt.executeInsert();
    }

    //method deletecity() with String parameter
    public void deletecity() {
        //call db.delete() sending TABLE_NAME and makeup of where clause of SQL statement as parameters, as well as null for whereArgument
        //db.delete(TABLE_NAME, "name = " + "'" + city + "'", null);
        db.delete(TABLE_NAME, null, null);
    }

    //method selectAll() returns list of string arrays
    public List<String[]> selectAll()
    {
        //create list of string arrays list
        List<String[]> list = new ArrayList<String[]>();
        //create cursor object equal to value returned from db.query()
        Cursor cursor = db.query(TABLE_NAME, new String[]{"id", "name", "value"}, null, null, null, null, "name asc");
        //create int x equal to 0
        int x = 0;
        //if cursor.moveToFirst() returns true
        if (cursor.moveToFirst())
        {
            //do while cursor.movetoNext() returns true
            do {
                //create array of Strings bl fill with database entry at index 0 and index 1
                String[] bl = new String[]{cursor.getString(0),
                        cursor.getString(1), cursor.getString(2)};
                //add current array to list
                list.add(bl);
                //increment int x
                x++;
            }while (cursor.moveToNext());
        }
        //if statement to determine if cursor is not null and if cursor.isClosed() returns false
        if (cursor != null && !cursor.isClosed())
        {
            //call cursor.close()
            cursor.close();
        }
        //call cursor.close()
        cursor.close();
        //return list
        return list;
    }

    //private static class OpenHelper extending functionality of SQLiteOpenHelper
    private static class OpenHelper extends SQLiteOpenHelper
    {
        //constructor method, taking context, DATABASE_NAME, null and DATABASE_VERSIOn as parameters
        OpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //onCreate method
        public void onCreate(SQLiteDatabase db)
        {
            //call db.execSQL() sending SQL like statement with variables in place of text values
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY, name TEXT, value TEXT)");
        }

        //onUpgrade method runs when DATABASE_VERSION does not match oldVersion int
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //set DATABASE_VERSION equal to newVersion int
            DATABASE_VERSION = newVersion;
            //call db.execSQL() sending sql like statement to delete table with name equal to TABLE_NAME
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            //call onCreate() method
            onCreate(db);
        }
    }
}
