package com.texus.shapefileviewer.datamodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeField {

    public static final String TABLE_NAME = "TableShapeField";

    public static final String ID = "id";
    public static final String FIELD_NAME = "FieldName";

    public int id;
    public String fieldName;

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + FIELD_NAME + " TEXT );";

    public static ShapeField getAnObjectFromCursor(Cursor c) {
        ShapeField instance = null;
        if( c != null) {
            instance = new ShapeField();
            instance.id = c.getInt(c.getColumnIndex(ID));
            instance.fieldName = c.getString(c.getColumnIndex(FIELD_NAME));
        } else {
            LOG.log("getAnObjectFromCursor:", "getAnObjectFromCursor Cursor is null");
        }
        return instance;
    }

    public static int inseartOperation( Databases db , ShapeField instance) {
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        query = "insert into " + TABLE_NAME + " ("
                + FIELD_NAME + " ) values ( "
                + "'" + instance.fieldName.replaceAll("'","\'") + "');";
        LOG.log("Query:", "Query:" + query);
        sql.execSQL(query);
        return getID(db);
    }

    public static int getID(Databases db) {
        try {
            final String MY_QUERY = "SELECT MAX(" + ID + ") FROM " + TABLE_NAME;
            SQLiteDatabase dbRead = db.getReadableDatabase();
            Cursor cur = dbRead.rawQuery(MY_QUERY, null);
            cur.moveToFirst();
            int id = cur.getInt(0);
            cur.close();
            return id;
        } catch( Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ShapeField getAnObject(Databases db, String fieldName) {
        ShapeField instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " WHERE "
                + FIELD_NAME + " = '" + fieldName.replaceAll("'","\'") + "'";
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            instance = getAnObjectFromCursor(c);
        }
        c.close();
        dbRead.close();
        return instance;
    }



    public static ShapeField getAnObject(Databases db, int id) {
        ShapeField instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " WHERE "
                + ID + " = " + id + "";
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            instance = getAnObjectFromCursor(c);
        }
        c.close();
        dbRead.close();
        return instance;
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
