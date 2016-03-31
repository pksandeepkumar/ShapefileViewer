package com.texus.shapefileviewer.datamodel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sandeep on 8/2/16.
 */
public class ShapeFieldData extends BaseDataModel {

    public static final String TABLE_NAME = "TableShapeFieldData";

    public static final String ID = "field_data_ID";
    public static final String SHAPE_ID = "ShapeID";
    public static final String FIELD_ID = "FieldID";
    public static final String FIELD_DATA = "FieldData";
    public static final String SHAPE_FILE_ID = "ShapeFileID";

    public static HashMap<String,Long> fieldIdMap = null;

    public long shapeID;
    public long fieldID;
    public long shapeFileID;
    public String fieldName;
    public String fieldData;

    public ShapeFieldData() {
        super();
    }


    public static void clearFieldNameIdHashMap() {
        fieldIdMap = new HashMap<>();
    }

    public static void insertFieldNamesOnlyOrGetFieldIDs(Databases db, ArrayList<ShapeFieldData> objects) {

        if(fieldIdMap == null) clearFieldNameIdHashMap();

        if( fieldIdMap.size() > 0) {
            for( ShapeFieldData object : objects) {
                object.fieldID = fieldIdMap.get( object.fieldName);
            }
            return;
        }
        for( ShapeFieldData object : objects) {
            ShapeField field = new ShapeField();
            field.fieldName = object.fieldName;
            field.shapeFileID = object.shapeFileID;
            object.fieldID  = ShapeField.insertOperation(db, field);
            fieldIdMap.put(object.fieldName, object.fieldID);
        }
    }


    /**
     * Keep a hashmap for getting id of field of this shape id
     * @param fieldName
     * @return
     */
    public static long getIdOfFieldName(Databases db, String fieldName, long shapeID) {
        if(fieldIdMap == null) {
            fieldIdMap = new HashMap<>();
        }
        Long id = fieldIdMap.get(fieldName);
        if(id != null) {
           return id;
        } else {
            ShapeField field = new ShapeField();
            field.fieldName = fieldName;
            field.shapeFileID = shapeID;
            id = ShapeField.insertOperation(db,field);
        }
        return id;
    }

    public static final String CREATE_TABE_QUERY = "CREATE TABLE  " + TABLE_NAME
            + " ( " + ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + SHAPE_ID + " INTEGER , "
            + FIELD_ID + " INTEGER , "
            + SHAPE_FILE_ID + " INTEGER , "
            + FIELD_DATA + " TEXT );";

    public static ShapeFieldData getAnObjectFromCursor(Cursor c) {
        ShapeFieldData instance = null;
        if( c != null) {
            instance = new ShapeFieldData();
            instance.shapeID = c.getInt(c.getColumnIndex(SHAPE_ID));
            instance.fieldID = c.getInt(c.getColumnIndex(FIELD_ID));
            instance.shapeFileID = c.getInt(c.getColumnIndex(SHAPE_FILE_ID));
            instance.fieldData = c.getString(c.getColumnIndex(FIELD_DATA));
        } else {
            LOG.log("getAnObjectFromCursor:", "getAnObjectFromCursor Cursor is null");
        }
        return instance;
    }

    public static long insertOperation(SQLiteDatabase sql, ShapeFieldData instance) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(FIELD_ID, instance.fieldID);
        insertValues.put(FIELD_DATA, instance.fieldData);
        insertValues.put(SHAPE_ID, instance.shapeID);
        insertValues.put(SHAPE_FILE_ID, instance.shapeFileID);
        return sql.insert(TABLE_NAME, null, insertValues);

    }

    public static long insertOperation(Databases db, ShapeFieldData instance) {
        long insertedID = INVALID_VALUE;
        instance.fieldID = getIdOfFieldName(db,instance.fieldName,instance.shapeID);
        SQLiteDatabase sql = db.getWritableDatabase();
        return insertOperation(sql, instance);
    }

    public static boolean insertOperation(Databases db, ArrayList<ShapeFieldData> objects) {
        insertFieldNamesOnlyOrGetFieldIDs(db,objects);
        SQLiteDatabase sql = db.getWritableDatabase();
        String query = "";
        for( ShapeFieldData object : objects) {
            //If no data, we can skip this object
            if(object.fieldData.trim().length() ==  0) continue;
            insertOperation(sql, object);
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


    public static ArrayList<ShapeFieldData> getAllFieldDataForAShape(Databases db, long shapeID) {
        ArrayList<ShapeFieldData> objects = null;
        SQLiteDatabase dbRead = db.getReadableDatabase();
//        String query = "select DISTINCT  from " + TABLE_NAME + "," + ShapeField.TABLE_NAME + "  WHERE "
//                + SHAPE_FILE_ID + " = '" + shapeFileID + "' " ;
        String query = "select *  from " + TABLE_NAME  + "  WHERE "
                + SHAPE_ID + " = '" + shapeID + "' " ;
        LOG.log("Query:", "Query:" + query);
        Cursor c = dbRead.rawQuery(query, null);
        if (c.moveToFirst()) {
            objects = new ArrayList<ShapeFieldData>();
            do {
                ShapeFieldData instance = new ShapeFieldData();
                instance.shapeID = c.getInt(c.getColumnIndex(SHAPE_ID));
                instance.fieldID = c.getInt(c.getColumnIndex(FIELD_ID));
//                instance.fieldName = c.getString(c.getColumnIndex(ShapeField.FIELD_NAME));
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
