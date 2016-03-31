package com.texus.shapefileviewer.datamodel;

/**
 * Created by sandeep on 29/3/16.
 */
public class BaseDataModel {

    /*
    Invalid value returns by method if that method didnt get sufficient parameter
     */
    public static final long INVALID_VALUE = -1;

    public long id;

    public BaseDataModel() {
        id = INVALID_VALUE;
    }

    /*
    Clear all illegal characters for query
     */
    public String makeItValidForSQLQuery( String data) {
        if( data == null) return null;
        data.replaceAll("'","\'");
        return data;
    }
}
