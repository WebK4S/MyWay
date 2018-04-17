package com.example.kamil.myway.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kamil.myway.Map.Position;

/**
 * Created by Kamil on 17.03.2018.
 */

public class DBConnection {

    public static final String DEBUG_TAG = "SqlitePosistionManager";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "dbsql.db";
    public static final String DB_POSITIONS_TABLE = "positions";

    public static final String KEY_ID = "id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_LATITUDE = "latitude";
    public static final String LATITUDE_OPTIONS = "REAL NOT NULL";
    public static final int LATITUDE_COLUMN = 1;

    public static final String KEY_LONGITUDE = "longitude";
    public static final String LONGITUDE_OPTIONS = "REAL NOT NULL";
    public static final int LONGITUDE_COLUMN = 2;

    public static final String KEY_DATE = "date";
    public static final String DATE_OPTIONS = "TEXT NOT NULL";
    public static final int DATE_COLUMN = 3;

    private Position position;


    public static final String DB_CREATE_POSITIONS = "CREATE TABLE IF NOT EXISTS " + DB_POSITIONS_TABLE +
            "(" + KEY_ID + " " + ID_OPTIONS + ", " +
            KEY_LATITUDE + " " + LATITUDE_OPTIONS + ", " +
            KEY_LONGITUDE + " " + LONGITUDE_OPTIONS + ", " +
            KEY_DATE + " " + DATE_OPTIONS + ");";

    public static final String DB_DROP_POSITIONS = "DROP TABLE IF EXISTS " + DB_POSITIONS_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DBHelper dbHelper;



    public static class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_POSITIONS);
            Log.d(DEBUG_TAG, "Database creating");
            Log.d(DEBUG_TAG, "Table" + DB_POSITIONS_TABLE + "ver. " + DB_VERSION + " created");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL(DB_DROP_POSITIONS);

            Log.d(DEBUG_TAG, "Database updating");
            Log.d(DEBUG_TAG, "Table" + DB_POSITIONS_TABLE + "updated from ver "+ i + "to ver." + i1 );
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }


    public DBConnection(Context context){
        this.context = context;
    }


    public DBConnection open(){
        dbHelper = new DBHelper(context,DB_NAME,null, DB_VERSION);
        try{
            db = dbHelper.getWritableDatabase();

        }catch (SQLException e){
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public long insertPosition(Double latitude, Double longitude, Integer date){
        ContentValues newPosition = new ContentValues();
        newPosition.put(KEY_LATITUDE, latitude);
        newPosition.put(KEY_LONGITUDE, longitude);
        newPosition.put(KEY_DATE, date);

        return db.insert(DB_POSITIONS_TABLE, null,newPosition);
    }

    public long insertPosition(Position position){
        ContentValues newPosition = new ContentValues();
        newPosition.put(KEY_LATITUDE, position.getLatitude());
        newPosition.put(KEY_LONGITUDE, position.getLongitude());
        newPosition.put(KEY_DATE, position.getDate());

        return db.insert(DB_POSITIONS_TABLE, null, newPosition);
    }

    public boolean updatePosition(Position position){
        long id = position.getId();
        Double latitude = position.getLatitude();
        Double longitude = position.getLongitude();
        String date = position.getDate();

        return updatePosition(id, latitude,longitude,date);
    }

    public boolean updatePosition(long id, Double latitude, Double longitude, String date){
        String where = KEY_ID + "=" + id;
        ContentValues updatePosition = new ContentValues();
        updatePosition.put(KEY_LATITUDE, latitude);
        updatePosition.put(KEY_LONGITUDE, longitude);
        updatePosition.put(KEY_LONGITUDE, longitude);
        updatePosition.put(KEY_DATE, date);

        return db.update(DB_POSITIONS_TABLE, updatePosition, where, null) > 0;
    }

    public boolean deletePosition(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_POSITIONS_TABLE, where, null) >0;
    }

    public Cursor getAllPositions(){
        String[] columns = {KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_DATE};
        return db.query(DB_POSITIONS_TABLE,columns,null,null,null,null,null);
    }

    public Position getPosition(long id){
        String[] columns = {KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_DATE};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_POSITIONS_TABLE, columns, where, null,null,null,null);
        Position position = null;
        if (cursor!=null && cursor.moveToFirst()){
            Double latitude = cursor.getDouble(LATITUDE_COLUMN);
            Double longitude = cursor.getDouble(LONGITUDE_COLUMN);
            String date = cursor.getString(DATE_COLUMN);
            position = new Position(id, latitude,longitude,date);
        }
        return position;
    }

}
