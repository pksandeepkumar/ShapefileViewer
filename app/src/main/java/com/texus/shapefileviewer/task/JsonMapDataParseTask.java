package com.texus.shapefileviewer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.texus.shapefileviewer.AppConstance;
import com.texus.shapefileviewer.MainActivity;
import com.texus.shapefileviewer.datamodel.ShapeData;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;
import com.texus.shapefileviewer.datamodel.ShapeFileData;
import com.texus.shapefileviewer.datamodel.ShapePoint;
import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.watcher.TimeWatcher;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sandeep on 21/3/16.
 */
public class JsonMapDataParseTask extends AsyncTask<Void, Void, Void> {

    MainActivity mainActivity = null;
    ArrayList<LatLng> latLngs = null;
    Context context;
    int noOfShapes = 0;
    long startTime = 0;
    long endTime = 0;

    public String filePath = AppConstance.FILE_BASE_PATH;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"Started!!!!!!", Toast.LENGTH_LONG).show();
    }

    public JsonMapDataParseTask( MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        latLngs = new ArrayList<LatLng>();
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            TimeWatcher watcher = new TimeWatcher();
            watcher.setStartTime();
            startTime = Calendar.getInstance().getTimeInMillis();
            JsonFactory jfactory = new JsonFactory();

            /*** read from file ***/
            String jsonFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
                    + "jsons" + File.separator + "landparcel_vietnam.json";

//            String jsonFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
//                    + "jsons" + File.separator + "one_parcel.json";


            Log.e("JSON Parsing", "Field jsonFileName:" + jsonFileName);
            JsonParser jParser = jfactory.createJsonParser(new File(jsonFileName));

            Databases db = new Databases(context);

            ShapeFileData.wipeAllData(db);

            ShapeFileData shapeFileData = new ShapeFileData();
            shapeFileData.shapeName = "landparcel_vietnam.json";
            shapeFileData.shapeFilePath = jsonFileName;
            shapeFileData.id = ShapeFileData.insertOperation(db, shapeFileData);


            filePath = filePath + File.separator + shapeFileData.id;
            createFolder(filePath);

            Log.e("JSON Parsing", "-------------------------------------");
            Log.e("JSON Parsing", "-------------------------------------");
            Log.e("JSON Parsing", "-------------------------------------");

            while (jParser.nextToken() != JsonToken.END_OBJECT) {
//                Log.e("JSON Parsing","jParser.nextToken():" + jParser.getCurrentToken());
//                Log.e("JSON Parsing","jParser.getText():" + jParser.getText());
                if( jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                    String fetchedValue = jParser.getCurrentName();

                    if("type".equals(fetchedValue)) {
//                        Log.e("JSON Parsing", "Attribute:" + fetchedValue);
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
                        //Shape array starts here
                        if(jParser.getCurrentToken()== JsonToken.START_ARRAY) {

                            while(jParser.nextToken() != JsonToken.END_ARRAY) {
                                ;
//                                Log.e("JSON Parsing", "Text:" + jParser.getText());
                                if( jParser.getCurrentToken() ==  JsonToken.START_OBJECT) {
//                                    jParser.nextToken();
//                                    Log.e("JSON Parsing", "Text:" + jParser.getText());


                                    ShapeData shapeData = new ShapeData();
                                    shapeData.shapeFileID = shapeFileData.id;
                                    shapeData.id = ShapeData.inseartOperation(db,shapeData);

                                    while(jParser.nextToken() != JsonToken.END_OBJECT) {

//                                        Log.e("JSON Parsing", "Text Type:" + jParser.getCurrentToken());
//                                        Log.e("JSON Parsing", "Text:" + jParser.getText());
                                        if( jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                                            fetchedValue = jParser.getText();

//                                            Log.e("JSON Parsing", "Text:" + fetchedValue);


                                            if("type".equals(fetchedValue)) {
//                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);
                                                jParser.nextToken();
                                                shapeData.shapeType = jParser.getText();
//                                                Log.e("JSON Parsing","Value:" + fetchedValue);
                                                continue;
                                            }
                                            //Field and field values starts from here
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
                                                                data.shapeFileID = shapeFileData.id;
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

                                                            //Shape Points starts here
                                                            if("coordinates".equals(fetchedValue)) {
//                                                                Log.e("JSON Parsing","Attribute:" + fetchedValue);

                                                                ArrayList<ShapePoint> points = new ArrayList<ShapePoint>();
                                                                jParser.nextToken();
                                                                // start 1st [ of  [ [ [] [] [] ] ]
                                                                if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {

                                                                    while(jParser.nextToken() != JsonToken.END_ARRAY) {
                                                                        int onOfParcels = 0;

                                                                        // Start 2nd [ of [ [ [] [] [] ] ]
                                                                        if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {
                                                                            onOfParcels++;
                                                                            ShapePoint point = new ShapePoint();
                                                                            point.shapeId = shapeData.id;
                                                                            StringBuilder builder = new StringBuilder();
//                                                                            latLngs = new ArrayList<LatLng>();
                                                                            while(jParser.nextToken() != JsonToken.END_ARRAY) {
                                                                                // Start 3rd [ of [ [ [] [] [] ] ]
                                                                                if( jParser.getCurrentToken()==  JsonToken.START_ARRAY) {
                                                                                    jParser.nextToken();
                                                                                    double latitiude = jParser.getDoubleValue();
                                                                                    jParser.nextToken();
                                                                                    double longitude = jParser.getDoubleValue();
                                                                                    builder.append(latitiude + "," + longitude + ":");
                                                                                    //This will be end ] of 3rd [
                                                                                    jParser.nextToken();
                                                                                }
                                                                            }
                                                                            writeToFile("" + shapeFileData.id + "_" + shapeData.id + "_" + onOfParcels , filePath, builder);
                                                                            Log.e("Json Parsing", "--------------------Inserted shape point--------------------------");
                                                                        }
                                                                    }
                                                                    ShapePoint.inseartOperation(db,points);
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

            watcher.setEndTime();

            endTime = Calendar.getInstance().getTimeInMillis();

            Log.e("Json Parsing", "--------------------END--------------------------");
            Log.e("Json Parsing", "--------------------END--------------------------");
            Log.e("Json Parsing", "--------------------END--------------------------");
            Log.e("Json Parsing", "TimeTaken:" + (endTime-startTime));

            Log.e("Json Parsing", "Total TimeTaken:" + watcher.getTotalTimeTaken());


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
        Toast.makeText(context,"Finised!!!!!!", Toast.LENGTH_LONG).show();
//        if(mainActivity != null) {
//            mainActivity.plotShape((ArrayList<LatLng>)latLngs.clone());
//            Log.e("PUBLISH", "_______________________________________________");
//            Log.e("PUBLISH","_______________________________________________");
//        }
    }

    public void createFolder( String folderName) {
        File dir =  new File(folderName);
        if (null != dir) {
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public void writeToFile( String fileName, String path, StringBuilder builder) {


        try {
            File file = new File( path , fileName);
            FileOutputStream fileOutput = new FileOutputStream(file);
            fileOutput.write(builder.toString().getBytes());
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        BufferedWriter writer = null;
//        File file = new File( path + File.separator + fileName);
//
//        try {
//            if(!file.exists()) {
//                file.createNewFile();
//            }
//            writer = new BufferedWriter(new FileWriter(file));
//            writer.write(builder.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
////            if (writer != null) writer.close();
//        }

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

//        mainActivity.plotShape(latLngs);
    }
}