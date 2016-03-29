package com.texus.shapefileviewer;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.texus.shapefileviewer.adapters.FieldDataAdapter;
import com.texus.shapefileviewer.component.ComponentInfo;
import com.texus.shapefileviewer.datamodel.ShapeData;
import com.texus.shapefileviewer.datamodel.ShapeField;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;
import com.texus.shapefileviewer.datamodel.ShapePoint;
import com.texus.shapefileviewer.db.Databases;
import com.texus.shapefileviewer.dialogs.FileChooser;
import com.texus.shapefileviewer.dialogs.progressbars.TexProgressDialog;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.dbf.DBF_Field;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.dbf.DBF_File;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.shp.SHP_File;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.shp.shapeTypes.ShpShape;
import com.texus.shapefileviewer.shape.diewald_shapeFile.files.shx.SHX_File;
import com.texus.shapefileviewer.shape.diewald_shapeFile.shapeFile.ShapeFile;
import com.texus.shapefileviewer.task.JsonMapDataParseTask;
import com.texus.shapefileviewer.utility.LOG;
import com.texus.shapefileviewer.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements FileChooser.FileSelectedListener, OnMapReadyCallback {

    private static boolean drawing = false;

    private MapView         mapView;
    private GoogleMap       map;
    private Context         mContext;
    private static LinearLayout    llPopupLayout;
    private LinearLayout    searchLayout;
    private EditText        etSearch;
    private ListView        llSearchList;
    private ImageButton     imSearchBack;

    private Polygon selectedPolygon;
    Marker marker = null;

    private int previousShape = -1;

    private FieldDataAdapter          adapter;
    private ArrayList<ShapeFieldData> shapeFieldDatas;

    private  DisplayShapesTask displayShapesTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        llPopupLayout = (LinearLayout) this.findViewById(R.id.llPopupLayout);
        searchLayout = (LinearLayout) this.findViewById(R.id.searchLayout);
        imSearchBack = (ImageButton) this.findViewById(R.id.imSearchBack);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooserDialog();
            }
        });

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpSearchLayout();

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                LOG.log("Polygon CLick", "GET ID:" + polygon.getId());
                setSelected(polygon);
            }
        });

        hideSoftKeyboard();

//        if(displayShapesTask == null) {
//            displayShapesTask = new DisplayShapesTask();
//            displayShapesTask.execute();
//        }

        JsonMapDataParseTask task = new JsonMapDataParseTask(this);
        task.execute();

    }



    public int getPolygonID(Polygon polygon) {
        return Utility.parseInt(polygon.getId().replaceAll("pg","").trim());
    }

    public void setSelected(Polygon polygon) {
        selectedPolygon = polygon;
        int shapeID = getPolygonID(polygon);
        addMark(getCentroid(shapeID));
        addInfoWindow(shapeID);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(displayShapesTask != null) displayShapesTask.cancel(true);
        Toast.makeText(this,"On Detached Window called", Toast.LENGTH_LONG).show();
        LOG.log("onDetachedFromWindow", "onDetachedFromWindow");
    }

    public LatLng getCentroid(int shapeId) {
        ShapePoint point = getAShapePoint(shapeId);
        ArrayList<LatLng> latLngs = getLatLangs(point.filename);
        double x = 0.;
        double y = 0.;
        int pointCount = latLngs.size();
        for (LatLng latLang: latLngs){
            x += latLang.latitude;
            y += latLang.longitude;
        }
        x = x/pointCount;
        y = y/pointCount;
        return new LatLng(x,y);
    }

    public void addMark(LatLng latLng) {
        if(marker != null) { marker.remove(); }
        marker = map.addMarker(new MarkerOptions() .position(latLng) .title("Selected Area"));
    }

    public ShapePoint getAShapePoint(int shapeID) {
        Databases db = new Databases(mContext);
        ShapePoint point = getAShapePoint(db, shapeID);
        db.close();
        return  point;
    }

    public ShapePoint getAShapePoint(Databases db, int shapeID) {
        return ShapePoint.getAllPointsOfAShape(db, shapeID);
    }
    public void addListLayout() {
        if(llPopupLayout.getChildCount() != 0) return;
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.element_list, llPopupLayout);
        llSearchList = (ListView) child.findViewById(R.id.llSearchList);
        searchLayout.setBackgroundColor(Color.WHITE);
        imSearchBack.setImageResource(R.drawable.ic_back);
        imSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchListLayout();
            }
        });

        shapeFieldDatas = new ArrayList<ShapeFieldData>();
        adapter = new FieldDataAdapter(mContext, shapeFieldDatas);
        llSearchList.setAdapter(adapter);
    }

    public void addInfoWindow(int shapeId) {
        if(llPopupLayout.getChildCount() != 0) return;
        llPopupLayout.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        ComponentInfo componentInfo = new ComponentInfo(mContext,llPopupLayout.getHeight());
        llPopupLayout.addView(componentInfo);
        componentInfo.setValues(shapeId);
    }

    public static void removeInfoWindow() {
        llPopupLayout.removeAllViews();
        llPopupLayout.setGravity(Gravity.CENTER | Gravity.TOP);
    }

    public void refreshList( ArrayList<ShapeFieldData> datas) {
        shapeFieldDatas = datas;
        adapter = new FieldDataAdapter(mContext, datas);
        if(llSearchList != null) {
            llSearchList.setAdapter(adapter);
            llSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clearSearchListLayout();
                    ShapeFieldData data = shapeFieldDatas.get(position);
                    LatLng latLng = getCentroid(data.shapeID);
                    addMark(latLng);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
                            AppConstance.zoomLevel);
                    map.animateCamera(cameraUpdate);
                    addInfoWindow(data.shapeID);
                    hideSoftKeyboard();
                }
            });
        }
    }

    public void clearSearchListLayout() {
        llPopupLayout.removeAllViews();
        imSearchBack.setOnClickListener(null);
        searchLayout.setBackgroundColor(Color.TRANSPARENT);
        imSearchBack.setImageResource(R.drawable.ic_search);
    }

    public void setUpSearchLayout() {
        etSearch = (EditText) this.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (drawing) {
                    Toast.makeText(mContext, "Shape is drawing... please wait..", Toast.LENGTH_LONG).show();
                    return;
                }
                addListLayout();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (drawing) return;
                String keyWord = s.toString();
                if (keyWord.length() > 0) {
                    Databases db = new Databases(mContext);
                    ArrayList<ShapeFieldData> fieldDatas = ShapeFieldData.searchFieldData(db, keyWord);
                    db.close();
                    refreshList(fieldDatas);
                } else {
                    refreshList(new ArrayList<ShapeFieldData>());
                }
            }
        });
    }

    public ArrayList<LatLng> getLatLangs(String latLangString) {
        ArrayList<LatLng> latLangs = new ArrayList<LatLng>();
        if(latLangString == null) return latLangs;
        String[] spiltMain = latLangString.split(":");
        for(String sub : spiltMain) {
            String[] splitSub = sub.split(",");
            double lat = Utility.parseDouble(splitSub[0]);
            double lon = Utility.parseDouble(splitSub[1]);
            latLangs.add(new LatLng(lat,lon));
        }
        return latLangs;
    }



    public synchronized void plotShape( ArrayList<LatLng> latLngs) {
        PolygonOptions rectOptions = new PolygonOptions();

//        Iterator<LatLng> iter = latLngs.iterator();

//        while (iter.hasNext()) {
//
//            LatLng str = iter.next();
//            rectOptions.add(str);
//
//        }

        try {
            for( LatLng latLng : latLngs) {

                rectOptions.add(latLng);
            }
        } catch ( Exception e) {
            e.printStackTrace();;
        }


        drawPolygon(rectOptions, Color.RED, true);

    }


    public LatLng plotSHape(ShapePoint shapePoint, int color, boolean needFill){
        if(shapePoint == null) return null;
        try{

            PolygonOptions rectOptions = new PolygonOptions();
            double lat = 0;
            double lon = 0;
            String[] spiltMain = shapePoint.filename.split(":");
            for(String sub : spiltMain) {
                String[] splitSub = sub.split(",");
                lat = Utility.parseDouble(splitSub[0]);
                lon = Utility.parseDouble(splitSub[1]);
                rectOptions.add(new LatLng(lat, lon));
//                LOG.log("plotSHape", "Added lat lon:" + lat + ":" + lon);
            }
            LatLng point = new LatLng(lat,lon );
            drawPolygon(rectOptions, color, needFill);
            return  point;
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void plotSHapes() {
        double pointerLat = 0;
        double pointerLon = 0;
        map.clear();
        Databases db = new Databases(mContext);
        PolygonOptions rectOptions = new PolygonOptions();
        ArrayList<ShapePoint> points = ShapePoint.getAllPointsOfAShape(db);
        LatLng latLng = null;
        for(ShapePoint point: points) {
            LatLng tempLatLng = plotSHape(point,AppConstance.COLOR_DEFAULT, false);
            if(tempLatLng != null) latLng = tempLatLng;
        }
        if(latLng != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            map.animateCamera(cameraUpdate);
        }
    }

    public void drawPolygon(PolygonOptions rectOptions, int color, boolean needFill) {
        rectOptions.strokeColor(color).strokeWidth(5);
//        if( needFill ) rectOptions.fillColor(color);
        Polygon polygon = map.addPolygon(rectOptions);

        polygon.setClickable(true);
    }

    public void openFileChooserDialog() {
        if (drawing) {
            Toast.makeText(mContext, "Shape is drawing... please wait..", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(mContext, "Choose a dbf file.", Toast.LENGTH_LONG).show();
        FileChooser fileChooser = new FileChooser(this);
        fileChooser.showDialog();
        fileChooser.setFileListener(this);

    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onBackPressed() {
        if(llPopupLayout.getChildCount() >= 1) {
            removeInfoWindow();
            clearSearchListLayout();return;
        }
        finish();
    }

    public void fileSelected(File file) {
        if(file != null) {
//            Toast.makeText(this,"Choosed File:" + file.getParent(),Toast.LENGTH_LONG).show();
            file.getParent();
            String folderName = file.getParent();
            String fileName = file.getName();
            if(!fileName.endsWith(".dbf")) {
                Toast.makeText(this,"Please choose a dbf file",Toast.LENGTH_LONG).show();
                return;
            }
            fileName = fileName.replaceFirst(".dbf", "");
            ParseTask task = new ParseTask(folderName,fileName);
            task.execute();
        }
    }

    public class DisplayShapesTask extends AsyncTask<Void, ShapePoint, Void> {

        boolean flagDrawn = false;
        boolean flagGotoLoc = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public DisplayShapesTask() {
            map.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            drawing = true;
//            Databases db = new Databases(mContext);
//            //Read shape id only, because reading all point may cause outof memmory erros, to avoid
//            // this we read each point string and plot
//            ArrayList<ShapePoint> shapePoints = ShapePoint.getAllPointsShapeID(db);
//            LOG.log("doInBackground","shapePoints Size:" + shapePoints.size() );
//            for( ShapePoint point : shapePoints) {
//                LOG.log("doInBackground","Reading a point:" + point.shapeId );
//                LOG.log("doInBackground","Reading a point POINTS:" + point.filename );
//                //Read all points
//                point = ShapePoint.getAllPointsOfAShape(db,point.shapeId);
//                LOG.log("doInBackground","Reading a point POINTS:" + point.filename );
//                if(point ==  null) continue;
//                flagDrawn = false;
//                publishProgress(point);
//                while (!flagDrawn) {}
//            }
//            db.close();


            drawing = true;
            Databases db = new Databases(mContext);
            //Read shape id only, because reading all point may cause outof memmory erros, to avoid
            // this we read each point string and plot
            ArrayList<ShapePoint> shapePoints = ShapePoint.getAllPointsOfAShape(db);
            LOG.log("doInBackground","shapePoints Size:" + shapePoints.size() );
            for( ShapePoint point : shapePoints) {
                LOG.log("doInBackground","Reading a point:" + point.shapeId );
                LOG.log("doInBackground","Reading a point POINTS:" + point.filename );
                //Read all points
//                point = ShapePoint.getAllPointsOfAShape(db,point.shapeId);
                LOG.log("doInBackground","Reading a point POINTS:" + point.filename );
                if(point ==  null) continue;
                flagDrawn = false;
                publishProgress(point);
                while (!flagDrawn) {}
            }
            db.close();



            return null;
        }

        @Override
        protected void onProgressUpdate(ShapePoint... values) {
            try{
                ShapePoint point = values[0];
                flagDrawn = true;
                LatLng latLng = plotSHape(point,AppConstance.COLOR_DEFAULT,false);
                if(flagGotoLoc ==  true) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                    map.animateCamera(cameraUpdate);
                    flagGotoLoc = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            drawing = false;
        }
    }

    public class ParseTask extends AsyncTask<Void, Void, Void> {
        private String folderName;
        private String fileName;
        private TexProgressDialog dialog;
        PolygonOptions rectOptions = new PolygonOptions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new TexProgressDialog(mContext);
            dialog.show();
        }

        public ParseTask(String folderName, String fileName) {
            this.folderName = folderName;
            this.fileName = fileName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            parseAndInsertShapeFile(folderName, fileName);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try{
                rectOptions.strokeColor(AppConstance.COLOR_DEFAULT).strokeWidth(5);
                Polygon polygon = map.addPolygon(rectOptions);
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            exportDatabse();

            if(displayShapesTask != null) {
                displayShapesTask = new DisplayShapesTask();
                displayShapesTask.execute();
            }

        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            exportDatabse();
//            plotSHapes();
//            DisplayShapesTask task = new DisplayShapesTask();
//            task.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public HashMap<Integer,String> fieldNameMap = new HashMap<>();
    HashMap hm = new HashMap();
    public int getFiledNameId(String fieldName) {
        Integer val=(Integer)hm.get(fieldName);
        if( val == null) { return -1; }
        return val;
    }

    public void insertFieldName(String fieldName, int fieldID) {
       if(hm == null) return;
        hm.put(fieldName, fieldID);
    }



    public void parseAndInsertShapeFile(String folderName, String fileName) {
        DBF_File.LOG_INFO           = !false;
        DBF_File.LOG_ONLOAD_HEADER  = false;
        DBF_File.LOG_ONLOAD_CONTENT = false;
        SHX_File.LOG_INFO           = !false;
        SHX_File.LOG_ONLOAD_HEADER  = false;
        SHX_File.LOG_ONLOAD_CONTENT = false;
        SHP_File.LOG_INFO           = !false;
        SHP_File.LOG_ONLOAD_HEADER  = false;
        SHP_File.LOG_ONLOAD_CONTENT = false;
        System.out.printf("  ---------------------Parsing--------------------------");
        try {
            String folder = folderName;
            System.out.printf("  ------------- Folder " + folder );
            // LOAD SHAPE FILE (.shp, .shx, .dbf)
            ShapeFile shapefile = new ShapeFile(folder, fileName).READ();
            System.out.println("----------------------------Shape Count:" + shapefile.getSHP_shapeCount());
            ShpShape.Type shape_type = shapefile.getSHP_shapeType();
            System.out.println("\nshape_type = " +shape_type);
            int number_of_shapes = shapefile.getSHP_shapeCount();
            int number_of_fields = shapefile.getDBF_fieldCount();



            Databases db = new Databases(mContext);
            ShapeData.wipeData(db);
            ArrayList<ShapeData> shapeDatas = new ArrayList<ShapeData>();
            ArrayList<ShapePoint> shapePoints = new ArrayList<ShapePoint>();
            ArrayList<ShapeFieldData> shapeFieldDatas = new ArrayList<ShapeFieldData>();
            int primaryKeyID = 0;
            for(int i = 0; i < 1 /*number_of_shapes */; i++){
                ShpPolygon shape    = shapefile.getSHP_shape(i);
                int shapeID = primaryKeyID++;
                double[][] points =  shape.getPoints();
                int length = points.length;
                String shapePointText = "";
                for(int i2 = 0 ; i2 < length ; i2++) {
                    double [] pointsSingle = points[i2];
                    shapePointText  = shapePointText + pointsSingle[1] + ","
                            + pointsSingle[0] + ":";
                }
                ShapePoint shapePoint = new ShapePoint();
                shapePoint.shapeId = shapeID;
                shapePoint.filename = shapePointText;
                shapePoints.add(shapePoint);
                if(shapePoints.size() >= 100) {
                    ShapePoint.inseartOperation(db,shapePoints);
                    shapePoints = new ArrayList<ShapePoint>();
                    System.gc();
                }
                String[] shape_info = shapefile.getDBF_record(i);

                ShpShape.Type type     = shape.getShapeType();
                int number_of_vertices = shape.getNumberOfPoints();
                int number_of_polygons = shape.getNumberOfParts();
                int record_number      = shape.getRecordNumber();

                System.out.printf("\nSHAPE[%2d] - %s\n", i, type);
                System.out.printf("  (shape-info) record_number = %3d; vertices = %6d; polygons = %2d\n",
                        record_number, number_of_vertices, number_of_polygons);

                for(int j = 0; j < number_of_fields; j++){
                    String data = shape_info[j].trim();
                    DBF_Field field = shapefile.getDBF_field(j);
                    String field_name = field.getName();
                    int fieldID = getFiledNameId(field_name);
                    if(fieldID == -1) {
                        ShapeField shapeField = new ShapeField();
                        shapeField.fieldName = field_name;
                        fieldID = ShapeField.inseartOperation(db,shapeField);
                        insertFieldName(field_name,fieldID);
                    }

                    ShapeFieldData fieldData = new ShapeFieldData();
                    fieldData.shapeID = shapeID;
                    fieldData.fieldID = fieldID;
                    fieldData.fieldData = data;
                    shapeFieldDatas.add(fieldData);
                    if(shapeFieldDatas.size() >= 100) {
                        ShapeFieldData.inseartOperation(db, shapeFieldDatas);
                        shapeFieldDatas = new ArrayList<ShapeFieldData>();
                    }
//                    ShapeFieldData.inseartOperation(db, fieldData);

                    System.out.printf("  (dbase-info) [%d] %s = %s", j, field_name, data);
                }
                System.out.printf("\n");
            }
//            ShapeData.inseartOperation(db,shapeDatas);
            ShapePoint.inseartOperation(db,shapePoints);
            ShapeFieldData.inseartOperation(db, shapeFieldDatas);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void exportDatabse() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+ Databases.DATABASE_NAME+"";
                String backupDBPath = "ShapefileViewerDB.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            Toast.makeText(this, "A copy of DB is saved on SD card!!!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
