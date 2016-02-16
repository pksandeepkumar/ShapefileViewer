package com.texus.shapefileviewer.datamodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.AppConstance;
import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapePoint {

    public static final String TABLE_NAME = "TableShapePoints";

    public static final String ID_SHAPE = "ShapeId";
    public static final String LATITUDE = "Latitiude";
    public static final String LONGITUDE = "Longitude";
    public static final String FILE_NAME = "FileName";

    public int shapeId;
    public double latitude;
    public double longitude;
    public String filename;

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID_SHAPE + " INTEGER , "
            + FILE_NAME + " TEXT , "
            + LATITUDE + " REAL , "
            + LONGITUDE + " REAL );";

    public static ShapePoint getAnObjectFromCursor(Cursor c) {
        ShapePoint instance = null;
        if( c != null) {
            instance = new ShapePoint();
            instance.shapeId = c.getInt(c.getColumnIndex(ID_SHAPE));
            instance.filename = c.getString(c.getColumnIndex(FILE_NAME));
            instance.latitude = c.getDouble(c.getColumnIndex(LATITUDE));
            instance.longitude = c.getDouble(c.getColumnIndex(LONGITUDE));
        } else {
            if(AppConstance.D)  LOG.log("getAnObjectFromCursor:", "getAnObjectFromCursor Cursor is null");
        }
        return instance;
    }

    public static boolean inseartOperation( Databases db , ShapePoint instance) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "insert into " + TABLE_NAME + " ("
                + ID_SHAPE + ","
                + FILE_NAME + ","
                + LATITUDE + ","
                + LONGITUDE + " ) values ( "
                + "" + instance.shapeId + ","
                + "'" + instance.filename + "',"
                + "" + instance.latitude + ","
                + "" + instance.longitude + ");";
//        if(AppConstance.D) LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);
        return true;
    }

    public static boolean inseartOperation( Databases db , ArrayList<ShapePoint> objects) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for(ShapePoint instance: objects) {
            query = "insert into " + TABLE_NAME + " ("
                    + ID_SHAPE + ","
                    + FILE_NAME + ","
                    + LATITUDE + ","
                    + LONGITUDE + " ) values ( "
                    + "" + instance.shapeId + ","
                    + "'" + instance.filename + "',"
                    + "" + instance.latitude + ","
                    + "" + instance.longitude + ");";
//            if(AppConstance.D) LOG.log("Query:", "Query:" + query);
            sql.execSQL(query);
        }


        return true;
    }



    public static ShapePoint getAllPointsOfAShape(Databases db, int shapeId) {
        ArrayList<ShapePoint> items = new ArrayList<ShapePoint>();
        ShapePoint instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " WHERE " + ID_SHAPE + " = " + shapeId ;
        if(AppConstance.D) LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            instance = getAnObjectFromCursor(c);
//            do {
//
//                items.add(instance);
//            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return instance;
    }



    public static ArrayList<ShapePoint> getAllPointsOfAShape(Databases db) {
        ArrayList<ShapePoint> items = new ArrayList<ShapePoint>();
        ShapePoint instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME ;
        if(AppConstance.D) LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                instance = getAnObjectFromCursor(c);
                items.add(instance);
            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return items;
    }

    public static ArrayList<ShapePoint> getAllPointsShapeID(Databases db) {
        ArrayList<ShapePoint> items = new ArrayList<ShapePoint>();
        ShapePoint instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select " + ID_SHAPE + " from " + TABLE_NAME ;
        if(AppConstance.D) LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                instance = new ShapePoint();
                instance.shapeId = c.getInt(c.getColumnIndex(ID_SHAPE));
                items.add(instance);
            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return items;
    }

    public static boolean deleteTable(Databases db) {
        try {
            SQLiteDatabase sql = db.getWritableDatabase();
            String query = "DELETE from " +  TABLE_NAME;
            if(AppConstance.D) LOG.log("Query:", "Query:" + query);
            sql.execSQL(query);
            sql.close();
            return true;
        } catch ( Exception e) {
            return false;
        }
    }


}
