package com.texus.shapefileviewer.datamodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeFieldData {

    public static final String TABLE_NAME = "TableShapeFieldData";

    public static final String SHAPE_ID = "ShapeID";
    public static final String FIELD_ID = "FieldID";
    public static final String FIELD_DATA = "FieldData";

    public int shapeID;
    public int fieldID;
    public String fieldName;
    public String fieldData;

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + SHAPE_ID + " INTEGER , "
            + FIELD_ID + " INTEGER , "
            + FIELD_DATA + " TEXT );";

    public static ShapeFieldData getAnObjectFromCursor(Cursor c) {
        ShapeFieldData instance = null;
        if( c != null) {
            instance = new ShapeFieldData();
            instance.shapeID = c.getInt(c.getColumnIndex(SHAPE_ID));
            instance.fieldID = c.getInt(c.getColumnIndex(FIELD_ID));
            instance.fieldData = c.getString(c.getColumnIndex(FIELD_DATA));
        } else {
            LOG.log("getAnObjectFromCursor:", "getAnObjectFromCursor Cursor is null");
        }
        return instance;
    }

    public static boolean inseartOperation( Databases db , ShapeFieldData instance) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "insert into " + TABLE_NAME + " ("
                + FIELD_ID + ","
                + FIELD_DATA + ","
                + SHAPE_ID + " ) values ( "
                + "" + instance.fieldID + ","
                + "'" + instance.fieldData + "',"
                + "" + instance.shapeID + ");";
        LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);
        return true;
    }

    public static boolean inseartOperation( Databases db , ArrayList<ShapeFieldData> objects) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for( ShapeFieldData object : objects) {
            query = "insert into " + TABLE_NAME + " ("
                    + FIELD_ID + ","
                    + FIELD_DATA + ","
                    + SHAPE_ID + " ) values ( "
                    + "" + object.fieldID + ","
                    + "'" + object.fieldData + "',"
                    + "" + object.shapeID + ");";
            LOG.log("Query:", "Query:" + query);
            sql.execSQL(query);
        }
        sql.close();
        return true;
    }

    public static ArrayList<ShapeFieldData> searchFieldData(Databases db, String searchKey) {
        ArrayList<ShapeFieldData> objects = new ArrayList<ShapeFieldData>();
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + "  WHERE "
                + FIELD_DATA + " Like '%" + searchKey + "%'";
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                ShapeFieldData instance = new ShapeFieldData();
                instance.shapeID = c.getInt(c.getColumnIndex(SHAPE_ID));
                instance.fieldData = c.getString(c.getColumnIndex(FIELD_DATA));
                objects.add(instance);
            } while (c.moveToNext());
        }
        c.close();
        dbRead.close();
        return objects;
    }


    public static ArrayList<ShapeFieldData> getAllFieldDataForAShape(Databases db, int shapeID) {
        ArrayList<ShapeFieldData> objects = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + "," + ShapeField.TABLE_NAME + "  WHERE "
                + SHAPE_ID + " = '" + shapeID + "'";
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            objects = new ArrayList<ShapeFieldData>();
            do {
                ShapeFieldData instance = new ShapeFieldData();
                instance.shapeID = c.getInt(c.getColumnIndex(SHAPE_ID));
                instance.fieldID = c.getInt(c.getColumnIndex(FIELD_ID));
                instance.fieldName = c.getString(c.getColumnIndex(ShapeField.FIELD_NAME));
                instance.fieldData = c.getString(c.getColumnIndex(FIELD_DATA));
                objects.add(instance);
            } while (c.moveToNext());
        }
        c.close();
        dbRead.close();
        return objects;
    }

    public static boolean deleteTable(Databases db) {
        try {
            SQLiteDatabase sql = db.getWritableDatabase();
            String query = "DELETE from " +  TABLE_NAME;
            LOG.log("Query:", "Query:" + query);
            sql.execSQL(query);
            sql.close();
            return true;
        } catch ( Exception e) {
            return false;
        }
    }





}
