package com.texus.shapefileviewer.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.File;

/**
 * Created by sandeep on 21/3/16.
 */
public class JsonMapDataParseTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public JsonMapDataParseTask() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            JsonFactory jfactory = new JsonFactory();

            /*** read from file ***/
            String jsonFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
                    + "jsons" + File.separator + "landparcel_vietnam.json";

            Log.e("JSON Parsing","Field jsonFileName:" + jsonFileName);
            JsonParser jParser = jfactory.createJsonParser(new File(jsonFileName));



//            // loop until token equal to "}"
//            while (jParser.nextToken() != JsonToken.END_OBJECT) {
//                Log.e("JSON Parsing","jParser.nextToken():" + jParser.getCurrentToken());
//                Log.e("JSON Parsing","jParser.getText():" + jParser.getText());
////                Log.e("JSON Parsing","jParser.getText():" + jParser.get);
//                jParser.getText();
//                String fieldname = jParser.getCurrentName();
//                Log.e("JSON Parsing","Field Name:" + fieldname);
//            }


            Log.e("JSON Parsing", "-------------------------------------");
            Log.e("JSON Parsing", "-------------------------------------");
            Log.e("JSON Parsing", "-------------------------------------");
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
//                Log.e("JSON Parsing","jParser.nextToken():" + jParser.getCurrentToken());
//                Log.e("JSON Parsing","jParser.getText():" + jParser.getText());
                if( jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                    String fetchedValue = jParser.getCurrentName();
                    if("type".equals(fetchedValue)) {
                        Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                        jParser.nextToken();
                        if( jParser.getCurrentToken() == JsonToken.VALUE_STRING) {
                            fetchedValue = jParser.getText();
                            Log.e("JSON Parsing","Value:" + fetchedValue);
                        }
                    }

                    if("features".equals(fetchedValue)) {
                        Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                        jParser.nextToken();
                        Log.e("JSON Parsing", "Text:" + jParser.getText());
                        if(jParser.getCurrentToken()== JsonToken.START_ARRAY) {
                            while(jParser.getCurrentToken() != JsonToken.END_ARRAY) {
                                jParser.nextToken();
                                Log.e("JSON Parsing", "Text:" + jParser.getText());
                                if( jParser.getCurrentToken() ==  JsonToken.START_OBJECT) {
                                    jParser.nextToken();
                                    while(jParser.getCurrentToken() != JsonToken.END_OBJECT) {
                                        jParser.nextToken();
                                        Log.e("JSON Parsing", "Text:" + jParser.getText());
                                        if( jParser.getCurrentToken().equals("FIELD_NAME")) {
                                            fetchedValue = jParser.getText();
                                            if("type".equals(fetchedValue)) {
                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                fetchedValue = jParser.getText();
                                                Log.e("JSON Parsing","Value:" + fetchedValue);
                                            }

                                            if("properties".equals(fetchedValue)) {
                                                Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                if( jParser.getCurrentToken().equals("START_OBJECT")) {
                                                    while(!jParser.getCurrentToken().equals("END_OBJECT")) {
                                                        jParser.nextToken();
                                                        if( jParser.getCurrentToken().equals("FIELD_NAME")) {
                                                            fetchedValue = jParser.getText();
                                                            Log.e("JSON Parsing","Attributes:" + fetchedValue);
                                                            fetchedValue = jParser.getText();
                                                            Log.e("JSON Parsing","Value:" + fetchedValue);
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }


        } catch ( Exception e) {
            e.printStackTrace();
        }




//        JsonFactory factory = new JsonFactory();
//        org.codehaus.jackson.JsonFactory



        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}