package com.texus.shapefileviewer.datamodel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeData extends BaseDataModel {

    public static final String TABLE_NAME = "TableShapeData";

    public static final String ID = "id";
    public static final String SHAPE_NAME = "ShapeName";
    public static final String SHAPE_TYPE = "ShapeType";
    public static final String SHAPE_FILE_ID = "ShapeFileID";
    public static final String NO_OF_PARCELS = "NoOfParcels";

    public static final int DEFAULT_NO_OF_SHAPES = 1;

    public long shapeFileID;
    public int noOfParcels = DEFAULT_NO_OF_SHAPES;
    public String shapeName;
    public String shapeType;

    ArrayList<ShapePoint> points  = new ArrayList<ShapePoint>();


    public ShapeData() {
        super();
    }

    /**
     * No of parcel default value is 1, most commonly a shape has one parcel, so chances of occuring
     * multiple parcel is very less, By setting default value to 1 we can reduce serveral update db
     * operation
     */
    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + SHAPE_TYPE + " VARCHAR(100),"
            + SHAPE_FILE_ID + " INTEGER,"
            + NO_OF_PARCELS + " INTEGER DEFAULT " + DEFAULT_NO_OF_SHAPES + " ,"
            + SHAPE_NAME + " TEXT );";


    public static long inseartOperation( Databases db , ShapeData shapeData) {
        SQLiteDatabase sql = db.getWritableDatabase();
        long id = insertOperation(sql, shapeData);
        sql.close();
        return id;
    }

    public static long insertOperation(SQLiteDatabase sql, ShapeData shapeData) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(SHAPE_TYPE, shapeData.shapeType);
        insertValues.put(SHAPE_NAME, shapeData.shapeName);
        insertValues.put(SHAPE_FILE_ID, shapeData.shapeFileID);
        return sql.insert(TABLE_NAME, null, insertValues);

    }

    public static boolean updateOperation(Databases db, ShapeData shapeData) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "update " + TABLE_NAME + " SET "
                + " "   + SHAPE_TYPE + " = '" + shapeData.shapeType + "' "
                + " , " + SHAPE_NAME + " = '" + shapeData.shapeName + "' "
                + " , " + SHAPE_FILE_ID + " = " + shapeData.shapeFileID + " "
                + " , " + NO_OF_PARCELS + " = " + shapeData.noOfParcels + " "
                + " WHERE " + ID + " = " + shapeData.id ;
        LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);

        sql.close();
        return true;
    }

    public static int inseartOperation( Databases db , ArrayList<ShapeData> objects) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for(ShapeData object: objects) {
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

    public static ArrayList<ShapeData> getAllShapes(Databases db) {
        ArrayList<ShapeData> items = new ArrayList<ShapeData>();
        ShapeData instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME ;
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                instance = new ShapeData();
                instance.id = c.getInt(c.getColumnIndex(ID));
                instance.shapeType = c.getString(c.getColumnIndex(SHAPE_TYPE));
                instance.shapeName = c.getString(c.getColumnIndex(SHAPE_NAME));
                items.add(instance);
            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return items;
    }

    public static ArrayList<ShapeData> getAllShapesForASHapeFile(Databases db, long shapeFileID) {
        ArrayList<ShapeData> items = new ArrayList<ShapeData>();
        ShapeData instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " WHERE " + SHAPE_FILE_ID + " = " +
                shapeFileID;
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                instance = new ShapeData();
                instance.id = c.getInt(c.getColumnIndex(ID));
                instance.shapeType = c.getString(c.getColumnIndex(SHAPE_TYPE));
                instance.shapeName = c.getString(c.getColumnIndex(SHAPE_NAME));
                items.add(instance);
            } while ( c.moveToNext()) ;
        }
        c.close();
        dbRead.close();
        return items;
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

    public static void wipeData(Databases db) {
        ShapeData.deleteTable(db);
        ShapeField.deleteTable(db);
        ShapeFieldData.deleteTable(db);
        ShapePoint.deleteTable(db);
    }

}
