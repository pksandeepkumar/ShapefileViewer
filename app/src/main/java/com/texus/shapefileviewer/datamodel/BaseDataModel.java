package com.texus.shapefileviewer.datamodel;

/**
 * Created by sandeep on 29/3/16.
 */
public class BaseDataModel {

    /*
    Clear all illegal characters for query
     */
    public String makeItValidForSQLQuery( String data) {
        if( data == null) return null;
        data.replaceAll("'","\'");
        return data;
    }
}
