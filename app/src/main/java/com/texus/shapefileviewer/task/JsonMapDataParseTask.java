package com.texus.shapefileviewer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.texus.shapefileviewer.MainActivity;
import com.texus.shapefileviewer.datamodel.ShapeData;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;
import com.texus.shapefileviewer.db.Databases;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sandeep on 21/3/16.
 */
public class JsonMapDataParseTask extends AsyncTask<Void, Void, Void> {

    MainActivity mainActivity = null;
    ArrayList<LatLng> latLngs = null;
    Context context;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public JsonMapDataParseTask( MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        latLngs = new ArrayList<LatLng>();
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            JsonFactory jfactory = new JsonFactory();

            /*** read from file ***/
            String jsonFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
                    + "jsons" + File.separator + "landparcel_vietnam.json";

//            String jsonFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
//                    + "jsons" + File.separator + "one_parcel.json";


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
            ShapeData shapeData = new ShapeData();
            Databases db = new Databases(context);
            shapeData.id = ShapeData.getID(db);
            ShapeData.inseartOperation(db,shapeData);

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
                            shapeData.shapeType = fetchedValue;
                            Log.e("JSON Parsing","Value:" + fetchedValue);
                        }
                    }

                    if("features".equals(fetchedValue)) {
                        Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                        jParser.nextToken();
                        Log.e("JSON Parsing", "Text:" + jParser.getText());
                        if(jParser.getCurrentToken()== JsonToken.START_ARRAY) {
                            while(jParser.nextToken() != JsonToken.END_ARRAY) {
                                ;
//                                Log.e("JSON Parsing", "Text:" + jParser.getText());
                                if( jParser.getCurrentToken() ==  JsonToken.START_OBJECT) {
//                                    jParser.nextToken();
//                                    Log.e("JSON Parsing", "Text:" + jParser.getText());



                                    while(jParser.nextToken() != JsonToken.END_OBJECT) {

//                                        Log.e("JSON Parsing", "Text Type:" + jParser.getCurrentToken());
//                                        Log.e("JSON Parsing", "Text:" + jParser.getText());
                                        if( jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                                            fetchedValue = jParser.getText();

//                                            Log.e("JSON Parsing", "Text:" + fetchedValue);


                                            if("type".equals(fetchedValue)) {
//                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                fetchedValue = jParser.getText();
//                                                Log.e("JSON Parsing","Value:" + fetchedValue);
                                                continue;
                                            }

                                            if("properties".equals(fetchedValue)) {
//                                                Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                if( jParser.getCurrentToken() ==  JsonToken.START_OBJECT) {

                                                    ArrayList<ShapeFieldData> shapeFieldDatas = new ArrayList<ShapeFieldData>();
                                                    while(jParser.nextToken() != JsonToken.END_OBJECT) {
                                                        if( jParser.getCurrentToken()== JsonToken.FIELD_NAME) {
                                                            ShapeFieldData fieldData = new ShapeFieldData();
                                                            fieldData.shapeID = shapeData.id;
                                                            String fieldName = jParser.getText();
                                                            jParser.nextToken();
                                                            String fieldValue = jParser.getText();
                                                            if(fieldValue.trim().length() != 0 || true) {
                                                                ShapeFieldData data = new ShapeFieldData();
                                                                data.shapeID = shapeData.id;
                                                                data.fieldData = fieldValue;
                                                                data.fieldName = fieldName;
                                                                shapeFieldDatas.add(data);
                                                            }
                                                        }
                                                    }
                                                    ShapeFieldData.insertOperation(db, shapeFieldDatas);


//                                                    Log.e("JSON Parsing", "While End:" + jParser.getText());
                                                }
                                                continue;
                                            }

//                                            Log.e("JSON Parsing", "Value End:" + jParser.getText());
//                                            jParser.nextToken();

                                            if("geometry".equals(fetchedValue)) {
//                                                Log.e("JSON Parsing", "Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                if( jParser.getCurrentToken()==  JsonToken.START_OBJECT) {
                                                    while(jParser.nextToken()!= JsonToken.END_OBJECT) {
//                                                        Log.e("JSON Parsing", "Geometry:" + jParser.getText());
//                                                        Log.e("JSON Parsing", "Geometry:" + jParser.getCurrentToken());
                                                        if( jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                                                            fetchedValue = jParser.getText();
//                                                            Log.e("JSON Parsing", "Geometry:" + jParser.getText());
                                                            if("type".equals(fetchedValue)) {
//                                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);
                                                                jParser.nextToken();
                                                                fetchedValue = jParser.getText();
//                                                                Log.e("JSON Parsing","Value:" + fetchedValue);
                                                            } else

                                                            if("coordinates".equals(fetchedValue)) {
//                                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);
                                                                jParser.nextToken();
                                                                if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {
                                                                    while(jParser.nextToken() != JsonToken.END_ARRAY) {

                                                                        if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {
                                                                            latLngs = new ArrayList<LatLng>();
                                                                            while(jParser.nextToken() != JsonToken.END_ARRAY) {

                                                                                if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {
                                                                                    jParser.nextToken();

                                                                                    double latitiude = jParser.getDoubleValue();
//                                                                                    Log.e("JSON Parsing", "Latitude:" + latitiude);
                                                                                    jParser.nextToken();
                                                                                    double longitude = jParser.getDoubleValue();
//                                                                                    Log.e("JSON Parsing", "Longitude:" + longitude);
//                                                                                    latLngs.add(new LatLng(latitiude,longitude));
                                                                                    latLngs.add(new LatLng(longitude,latitiude));
                                                                                    jParser.nextToken();

                                                                                }
                                                                            }
                                                                            publishProgress();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                continue;
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
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
//        if(mainActivity != null) {
//            mainActivity.plotShape((ArrayList<LatLng>)latLngs.clone());
//            Log.e("PUBLISH", "_______________________________________________");
//            Log.e("PUBLISH","_______________________________________________");
//        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

//        mainActivity.plotShape(latLngs);
    }
}