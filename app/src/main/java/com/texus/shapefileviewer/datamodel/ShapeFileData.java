package com.texus.shapefileviewer.datamodel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * Created by sandeep on 8/2/16.
 *
 * This class is use tomanage multpple shape files
 *
 * ShapeFile has
 * --->ShapeData
 *      --->ShapeFieldData
 *      --->ShapePoints
 * --->ShapeField
 */
public class ShapeFileData extends BaseDataModel  {

    public static final String TABLE_NAME = "TableShapeFileData";

    public static final String ID = "id";
    public static final String SHAPE_FILE_NAME = "ShapeFileName";
    public static final String SHAPE_FILE_PATH = "ShapeFilePath";
    public static final String NO_OF_SHAPES = "NoOfShapes";


    public String shapeName;
    public String shapeFilePath;
    public int noOfShapes;



    ArrayList<ShapePoint> points  = new ArrayList<ShapePoint>();

    public ShapeFileData() {
        super();
    }

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + SHAPE_FILE_PATH + " TEXT,"
            + NO_OF_SHAPES + " INTEGER,"
            + SHAPE_FILE_NAME + " TEXT );";


    public static long insertOperation( Databases db , ShapeFileData shapeData) {
        if(shapeData.id != INVALID_VALUE) {
           ShapeFileData.updateOperation(db,shapeData);
        }
        SQLiteDatabase sql = db.getWritableDatabase();
        long id = insertOperation(sql, shapeData);
        sql.close();
        return id;
    }

    public static long insertOperation( SQLiteDatabase sql,ShapeFileData shapeData ) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(SHAPE_FILE_PATH, shapeData.shapeFilePath);
        insertValues.put(SHAPE_FILE_NAME, shapeData.shapeName);
        insertValues.put(NO_OF_SHAPES, shapeData.noOfShapes);
        long id = INVALID_VALUE;
        id = sql.insert(TABLE_NAME, null, insertValues);
        return id;
    }


    public static int insertOperation( Databases db , ArrayList<ShapeFileData> objects) {

        for(ShapeFileData object: objects) {
            if(object.id != INVALID_VALUE) {
                updateOperation(db,object);
            }
        }
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for(ShapeFileData object: objects) {
            long id = insertOperation(sql, object);
        }
        sql.close();
        return getID(db);
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

    public static ArrayList<ShapeFileData> getAllshapeFileData(Databases db) {
        ArrayList<ShapeFileData> items = new ArrayList<ShapeFileData>();
        ShapeFileData instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME ;
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                instance = new ShapeFileData();
                instance.id = c.getInt(c.getColumnIndex(ID));
                instance.shapeFilePath = c.getString(c.getColumnIndex(SHAPE_FILE_PATH));
                instance.shapeName = c.getString(c.getColumnIndex(SHAPE_FILE_NAME));
                instance.noOfShapes = c.getInt(c.getColumnIndex(NO_OF_SHAPES));
                items.add(instance);
            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return items;
    }

    public static boolean updateOperation(Databases db, ShapeFileData shapeFileData) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "update " + TABLE_NAME + " SET "
                + " "   + SHAPE_FILE_NAME + " = '" + shapeFileData.shapeName + "' "
                + " , " + SHAPE_FILE_PATH + " = '" + shapeFileData.shapeFilePath + "' "
                + " , " + NO_OF_SHAPES + " = " + shapeFileData.noOfShapes + " "
                + " WHERE " + ID + " = " + shapeFileData.id ;
        LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);

        sql.close();
        return true;
    }

    public static int getID(Databases db) {
        try {
            final String MY_QUERY = "SELECT MAX(" + ID + ") FROM " + TABLE_NAME;
            SQLiteDatabase dbRead = db.getReadableDatabase();
            Cursor cur = dbRead.rawQuery(MY_QUERY, null);
            int id = 0;
            if(cur.moveToFirst()) {
                id = cur.getInt(0);
            }
            cur.close();
            dbRead.close();
            return id;
        } catch( Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteShapeFile(Databases db, long shapeFileID) {
        ShapeFileData.deleteTable(db);
        ShapeField.deleteTable(db);
        ShapeFieldData.deleteTable(db);
        ShapePoint.deleteTable(db);

    }

    public static void wipeAllData(Databases db) {
        ShapeFileData.deleteTable(db);
        ShapeFileData.deleteTable(db);
        ShapeField.deleteTable(db);
        ShapeFieldData.deleteTable(db);
        ShapePoint.deleteTable(db);

    }

}
