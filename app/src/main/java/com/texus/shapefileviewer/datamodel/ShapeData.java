package com.texus.shapefileviewer.datamodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeData {
    public static final String TABLE_NAME = "TableShapeData";

    public static final String ID = "id";
    public static final String SHAPE_NAME = "ShapeName";

    public int id;
    public String shapeName;

    ArrayList<ShapePoint> points  = new ArrayList<ShapePoint>();

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + SHAPE_NAME + " TEXT );";


    public static int inseartOperation( Databases db , ShapeData shapeData) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "insert into " + TABLE_NAME + " ("
                + SHAPE_NAME + " ) values ( "
                + "'" + shapeData.shapeName + "');";
        LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);
        sql.close();
        return getID(db);
    }

    public static int inseartOperation( Databases db , ArrayList<ShapeData> objects) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for(ShapeData object: objects) {
            query = "insert into " + TABLE_NAME + " ("
                    + ID + ","
                    + SHAPE_NAME + " ) values ( "
                    + "" + object.id + ","
                    + "'" + object.shapeName + "');";
            LOG.log("Query:", "Query:" + query);
            sql.execSQL(query);
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
