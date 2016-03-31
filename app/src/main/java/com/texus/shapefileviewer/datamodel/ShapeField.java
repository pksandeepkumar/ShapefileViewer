package com.texus.shapefileviewer.datamodel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeField extends BaseDataModel  {

    public static final String TABLE_NAME = "TableShapeField";

    public static final String ID = "id";
    public static final String SHAPE_FILE_ID = "ShapeFileID";
    public static final String FIELD_NAME = "FieldName";

    public long shapeFileID;
    public String fieldName;

    public ShapeField() {
        super();
    }

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + SHAPE_FILE_ID + " INTEGER , "
            + FIELD_NAME + " TEXT );";


    /**
     * This method insert and returns the ID of the row, if the object is already exist
     * the ID of that object will return
     * @param db
     * @param shapeField
     * @return
     */
    public static long insertOperation( Databases db , ShapeField shapeField) {

        if(shapeField == null) return INVALID_VALUE;

        ShapeField tempField = getAnObject(db, shapeField.fieldName, shapeField.shapeFileID);

        if(tempField != null) {
            return tempField.shapeFileID;
        }

        SQLiteDatabase sql = db.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put(SHAPE_FILE_ID, shapeField.shapeFileID);
        insertValues.put(FIELD_NAME, shapeField.fieldName);
        long id = sql.insert(TABLE_NAME, null, insertValues);
        sql.close();
        return id;
    }

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

//    public static int insertOperation( Databases db , ShapeField instance) {
//        SQLiteDatabase sql = db.getWritableDatabase();
//        String query = "";
//        query = "insert into " + TABLE_NAME + " ("
//                + FIELD_NAME + " ) values ( "
//                + "'" + instance.fieldName.replaceAll("'","\'") + "');";
//        LOG.log("Query:", "Query:" + query);
//        sql.execSQL(query);
//        return getID(db);
//    }

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

    public static ShapeField getAnObject(Databases db, String fieldName, long shapeID) {
        ShapeField instance = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
        String query = "select * from " + TABLE_NAME + " WHERE "
                + FIELD_NAME + " = '" + fieldName.replaceAll("'","\'") + "' AND "
                + SHAPE_FILE_ID + " = " + shapeID;
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            instance = getAnObjectFromCursor(c);
        }
        c.close();
//        dbRead.close();
        return instance;
    }



    public static ShapeField getAnObject(Databases db, long id) {
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
