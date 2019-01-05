package project.astix.com.ltfoodsfaindirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonFunction;
import com.astix.Common.CommonInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class DisplayItemPics extends AppCompatActivity implements InterfaceClass{

    Button btn_submit;
    int isStockAvlbl=0;
    int isCmpttrAvlbl=0;
    String imageName;
    File imageF;
    String clickedTagPhoto;
    Dialog dialog;
    Uri uriSavedImage;
    ImageView flashImage;
    float mDist=0;
    private boolean isLighOn = false;
    ArrayList<Object> arrImageData=new ArrayList<Object>();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture,cancelCam, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static int flgRestart=0;
    public static int flgStoreOrder=0;
    DBAdapterKenya dbengine=new DBAdapterKenya(DisplayItemPics.this);
    public AppLocationService appLocationService;
    int countSubmitClicked=0;
    public  int flgLocationServicesOnOffOrderReview=0;
    public  int flgGPSOnOffOrderReview=0;
    public  int flgNetworkOnOffOrderReview=0;
    public  int flgFusedOnOffOrderReview=0;
    public  int flgInternetOnOffWhileLocationTrackingOrderReview=0;
    public  int flgRestartOrderReview=0;
    public  int flgStoreOrderOrderReview=0;

    public String fnAccurateProvider="";
    public String fnLati="0";
    public String fnLongi="0";
    public String fnAccuracy="0";


    DatabaseAssistant DA = new DatabaseAssistant(this);
    public ProgressDialog pDialog2STANDBY;
    LocationRequest mLocationRequest;
    public LocationManager locationManager;

    public String storeID;
    public String strGlobalOrderID="0";
    public String newfullFileName;
    public String imei;
    public String date;
    public String pickerDate;
    public String SN="";

    LinearLayout ll_LTFoodsItem,ll_OtherItem;
    TextView txt_LtFoodsItem,txt_OtherItem;
    LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<String ,String>();
    ImageView imageback;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_HOME)
        {
            // finish();
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_SEARCH)
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item_pics);
        getDataFromIntent();
        imageback= (ImageView) findViewById(R.id.img_back_Btn);
        btn_submit= (Button) findViewById(R.id.btn_submit);
        txt_LtFoodsItem= (TextView) findViewById(R.id.txt_LtFoodsItem);
        txt_OtherItem= (TextView) findViewById(R.id.txt_OtherItem);
        ll_OtherItem= (LinearLayout) findViewById(R.id.ll_OtherItem);
        ll_LTFoodsItem= (LinearLayout) findViewById(R.id.ll_LTFoodsItem);
        locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((dbengine.isDataExistCompetitor(storeID)) && (dbengine.isDataForCompetitorCmplsry(storeID))  && (dbengine.getCmpttrRetailerAllowed(storeID)==1) &&(isCmpttrAvlbl==1))

                {
                    Intent storeOrderReviewIntent=new Intent(DisplayItemPics.this,CompetitorPrdctPriceActivity.class);
                    storeOrderReviewIntent.putExtra("storeID", storeID);
                    storeOrderReviewIntent.putExtra("SN", SN);
                    storeOrderReviewIntent.putExtra("bck", 1);
                    storeOrderReviewIntent.putExtra("imei", imei);
                    storeOrderReviewIntent.putExtra("userdate", date);
                    storeOrderReviewIntent.putExtra("pickerDate", pickerDate);



                    //fireBackDetPg.putExtra("rID", routeID);
                    startActivity(storeOrderReviewIntent);
                    finish();

                }
                else
                {
                    Intent storeOrderReviewIntent = new Intent(DisplayItemPics.this, OrderReview.class);
                    storeOrderReviewIntent.putExtra("storeID", storeID);
                    storeOrderReviewIntent.putExtra("SN", SN);
                    storeOrderReviewIntent.putExtra("bck", 1);
                    storeOrderReviewIntent.putExtra("imei", imei);
                    storeOrderReviewIntent.putExtra("userdate", date);
                    storeOrderReviewIntent.putExtra("pickerDate", pickerDate);
                    storeOrderReviewIntent.putExtra("flgOrderType", 1);

                    //fireBackDetPg.putExtra("rID", routeID);
                    startActivity(storeOrderReviewIntent);
                    finish();
                }
            }
        });
        txt_OtherItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickedTagPhoto="4";
                openCustomCamara();
            }
        });
        txt_LtFoodsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedTagPhoto="3";
                openCustomCamara();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((hmapPhotoDetailsForSaving!=null) && (hmapPhotoDetailsForSaving.size()>0))
                {
                    if(saveImageSection())
                    {
                        submitData();
                    }
                    else
                    {
                        submitData();
                    }

                }
                else
                {
                    submitData();
                }


            }
        });
        setSavedImage();
        getStockCompttrAvilable();
    }

    public void submitData()
    {
        boolean isGPSEnabled2 = false;
        boolean isNetworkEnabled2=false;
        isGPSEnabled2 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled2)
        {
            isGPSEnabled2 = false;
        }
        if(!isNetworkEnabled2)
        {
            isNetworkEnabled2 = false;
        }
        if(!isGPSEnabled2 && !isNetworkEnabled2)
        {
            try
            {
                showSettingsAlert();
            }
            catch(Exception e)
            {

            }

            isGPSEnabled2 = false;
            isNetworkEnabled2=false;
        }
        else{
            fnSaveFilledDataToDatabase(3);
        }
    }

    private void getDataFromIntent() {


        Intent passedvals = getIntent();

        storeID = passedvals.getStringExtra("storeID");
        imei = passedvals.getStringExtra("imei");
        date = passedvals.getStringExtra("userdate");
        pickerDate = passedvals.getStringExtra("pickerDate");
        SN = passedvals.getStringExtra("SN");


        hmapPhotoDetailsForSaving=dbengine.getImageData(storeID,"3^4");
        strGlobalOrderID=dbengine.fngetOrderIDAganistStore(storeID);

    }

    public void setSavedImage()
    {
        if((hmapPhotoDetailsForSaving!=null) && (hmapPhotoDetailsForSaving.size()>0))
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(entry.getValue().split(Pattern.quote("^"))[0]),180,180);
                String valueOfKey=entry.getValue().split(Pattern.quote("^"))[0]+"~"+entry.getValue().split(Pattern.quote("^"))[1];
                String clkTagPic=entry.getValue().split(Pattern.quote("^"))[2];
                if(clkTagPic.equals("3"))
                {
                    setSavedImageToScrollView(bitmap,entry.getKey(),valueOfKey,clkTagPic,ll_LTFoodsItem,false);
                }
                else
                {
                    setSavedImageToScrollView(bitmap,entry.getKey(),valueOfKey,clkTagPic,ll_OtherItem,false);
                }

            }
        }
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Information");
        alertDialog.setIcon(R.drawable.error_info_ico);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. \nPlease select all settings on the next page!");

        // On pressing Settings button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public boolean saveImageSection()
    {
        boolean isPicSaved=false;
        dbengine.deletePicSectionImage(storeID,"3");
        dbengine.deletePicSectionImage(storeID,"4");
        if((hmapPhotoDetailsForSaving!=null) && (hmapPhotoDetailsForSaving.size()>0))
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                String imageNameToSave=entry.getKey();
                String imagePath=entry.getValue().split(Pattern.quote("^"))[0];
                String dateTime=entry.getValue().split(Pattern.quote("^"))[1];
                String clkdTagPic=entry.getValue().split(Pattern.quote("^"))[2];
                //(String StoreID,String imageName,String imagePath,String ImageClicktime,String flgSctnPic,int Sstat)
                dbengine.savePicSectionImage(storeID,imageNameToSave,imagePath,dateTime,clkdTagPic,1);
                isPicSaved=true;

            }
        }
        return isPicSaved;
    }

    public void fnSaveFilledDataToDatabase(int valBtnClickedFrom)
    {

        //valBtnClickedFrom=Save/Save And Exit/Submit

        //Declare  Outstat=0;  // Outstat=1 (Save,SaveExit) , Outstat=3(Submit)



        if(valBtnClickedFrom==3)//Clicked By Btn Submitt
        {
            //Send Data for Sync

            // Changes By Sunil
            AlertDialog.Builder alertDialogSubmitConfirm = new AlertDialog.Builder(DisplayItemPics.this);
            alertDialogSubmitConfirm.setTitle("Information");
            alertDialogSubmitConfirm.setMessage(getText(R.string.submitConfirmAlert));
            alertDialogSubmitConfirm.setCancelable(false);

            alertDialogSubmitConfirm.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which)
                {

                    dbengine.open();
                    if ((dbengine.PrevLocChk(storeID.trim())) )
                    {
                        dbengine.close();
                        try
                        {
                            FullSyncDataNow task = new FullSyncDataNow(DisplayItemPics.this);
                            task.execute();
                        }
                        catch (Exception e) {
                            // TODO Autouuid-generated catch block
                            e.printStackTrace();
                            //System.out.println("onGetStoresForDayCLICK: Exec(). EX: "+e);
                        }
                    }
                    else
                    {

                        //appLocationService=new AppLocationService();



                      /*  pDialog2STANDBY= ProgressDialog.show(DisplayItemPics.this,getText(R.string.genTermPleaseWaitNew) ,getText(R.string.genTermRetrivingLocation), true);
                        pDialog2STANDBY.setIndeterminate(true);

                        pDialog2STANDBY.setCancelable(false);
                        pDialog2STANDBY.show();*/


                        LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                        llaaa.locationRetrievingAndDistanceCalculating(DisplayItemPics.this,false,50);


                    }



                }
            });

            alertDialogSubmitConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            });

            alertDialogSubmitConfirm.setIcon(R.drawable.info_ico);

            AlertDialog alert = alertDialogSubmitConfirm.create();

            alert.show();




        }



    }

    @Override
    public void testFunctionOne(String fnLati, String fnLongi, String finalAccuracy, String fnAccurateProvider,
                                String GpsLat, String GpsLong, String GpsAccuracy, String NetwLat, String NetwLong,
                                String NetwAccuracy, String FusedLat, String FusedLong, String FusedAccuracy,
                                String AllProvidersLocation, String GpsAddress, String NetwAddress, String FusedAddress,
                                String FusedLocationLatitudeWithFirstAttempt,
                                String FusedLocationLongitudeWithFirstAttempt,
                                String FusedLocationAccuracyWithFirstAttempt, int flgLocationServicesOnOff,
                                int flgGPSOnOff, int flgNetworkOnOff, int flgFusedOnOff,
                                int flgInternetOnOffWhileLocationTracking, String address, String pincode,
                                String city, String state)
    {

        //System.out.println("SHIVA"+fnLati+","+fnLongi+","+finalAccuracy+","+fnAccurateProvider+","+GpsLat+","+GpsLong+","+GpsAccuracy+","+NetwLat+","+NetwLong+","+NetwAccuracy+","+FusedLat+","+FusedLong+","+FusedAccuracy+","+AllProvidersLocation+","+GpsAddress+","+NetwAddress+","+FusedAddress+","+FusedLocationLatitudeWithFirstAttempt+","+FusedLocationLongitudeWithFirstAttempt+","+FusedLocationAccuracyWithFirstAttempt+","+fnLongi+","+flgLocationServicesOnOff+","+flgGPSOnOff+","+flgNetworkOnOff+","+flgFusedOnOff+","+flgInternetOnOffWhileLocationTracking+","+address+","+pincode+","+city+","+state);
        dbengine.fndeleteOldAddressDetailsofVisitedStore(storeID);
        dbengine.saveLatLngToTxtFile(storeID,fnLati, fnLongi,finalAccuracy,fnAccurateProvider,GpsLat,GpsLong,GpsAccuracy,NetwLat,NetwLong,NetwAccuracy,FusedLat,FusedLong,FusedAccuracy,3,"0",
                FusedAddress,AllProvidersLocation,GpsAddress,NetwAddress,FusedAddress,FusedLocationLatitudeWithFirstAttempt
                ,FusedLocationLongitudeWithFirstAttempt,FusedLocationAccuracyWithFirstAttempt);
        dbengine.open();
        dbengine.UpdateStoreActualLatLongi(storeID,String.valueOf(fnLati), String.valueOf(fnLongi), "" + finalAccuracy,fnAccurateProvider,flgLocationServicesOnOff,flgGPSOnOff,flgNetworkOnOff,flgFusedOnOff,flgInternetOnOffWhileLocationTracking,flgRestart,flgStoreOrder);

        dbengine.close();

        if(!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy)))
        {

           /* fnLati=String.valueOf(fnLati);
            fnLongi=String.valueOf(fnLongi);
            fnAccuracy=""+finalAccuracy;*/

            fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy));
            UpdateLocationAndProductAllData();


        }
        else
        {
            countSubmitClicked++;
            if(countSubmitClicked==1)
            {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DisplayItemPics.this);

                // Setting Dialog Title
                alertDialog.setTitle("Information");
                alertDialog.setIcon(R.drawable.error_info_ico);
                alertDialog.setCancelable(false);
                // Setting Dialog Message
                alertDialog.setMessage("Your current location is same as previous, so please turn off your location services then turn on, it back again.");

                // On pressing Settings button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //countSubmitClicked++;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();



            }
            else
            {

               /* fnLati=String.valueOf(fnLati);
                fnLongi=String.valueOf(fnLongi);
                fnAccuracy=""+finalAccuracy;*/

                UpdateLocationAndProductAllData();

            }


        }





    }

    public void checkHighAccuracyLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        flgLocationServicesOnOffOrderReview=0;
        flgGPSOnOffOrderReview=0;
        flgNetworkOnOffOrderReview=0;
        flgFusedOnOffOrderReview=0;
        flgInternetOnOffWhileLocationTrackingOrderReview=0;

        if(isGooglePlayServicesAvailable())
        {
            flgFusedOnOffOrderReview=1;
        }
        if(isOnline())
        {
            flgInternetOnOffWhileLocationTrackingOrderReview=1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //Equal or higher than API 19/KitKat
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=1;
                    //flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=0;
                    flgNetworkOnOffOrderReview=1;
                    // flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=0;
                    //flgFusedOnOff=0;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            //Lower than API 19
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);


            if (TextUtils.isEmpty(locationProviders)) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;

                flgLocationServicesOnOffOrderReview = 0;
                flgGPSOnOffOrderReview = 0;
                flgNetworkOnOffOrderReview = 0;
                // flgFusedOnOff = 0;
            }
            if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                flgLocationServicesOnOffOrderReview = 1;
                flgGPSOnOffOrderReview = 1;
                flgNetworkOnOffOrderReview = 1;
                //flgFusedOnOff = 0;
            } else {
                if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 1;
                    flgNetworkOnOffOrderReview = 0;
                    // flgFusedOnOff = 0;
                }
                if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 0;
                    flgNetworkOnOffOrderReview = 1;
                    //flgFusedOnOff = 0;
                }
            }
        }

    }

    public boolean checkLastFinalLoctionIsRepeated(String currentLat,String currentLong,String currentAccuracy){
        boolean repeatedLoction=false;

        try {

            String chekLastGPSLat="0";
            String chekLastGPSLong="0";
            String chekLastGpsAccuracy="0";
            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+ CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;

            // If file does not exists, then create it
            if (file.exists()) {
                StringBuffer buffer=new StringBuffer();
                String myjson_stampiGPSLastLocation="";
                StringBuffer sb = new StringBuffer();
                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader(file));

                    String temp;
                    while ((temp = br.readLine()) != null)
                        sb.append(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close(); // stop reading
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                myjson_stampiGPSLastLocation=sb.toString();

                JSONObject jsonObjGPSLast = new JSONObject(myjson_stampiGPSLastLocation);
                JSONArray jsonObjGPSLastInneralues = jsonObjGPSLast.getJSONArray("GPSLastLocationDetils");

                String StringjsonGPSLastnew = jsonObjGPSLastInneralues.getString(0);
                JSONObject jsonObjGPSLastnewwewe = new JSONObject(StringjsonGPSLastnew);

                chekLastGPSLat=jsonObjGPSLastnewwewe.getString("chekLastGPSLat");
                chekLastGPSLong=jsonObjGPSLastnewwewe.getString("chekLastGPSLong");
                chekLastGpsAccuracy=jsonObjGPSLastnewwewe.getString("chekLastGpsAccuracy");

                if(currentLat!=null )
                {
                    if(currentLat.equals(chekLastGPSLat) && currentLong.equals(chekLastGPSLong) && currentAccuracy.equals(chekLastGpsAccuracy))
                    {
                        repeatedLoction=true;
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return repeatedLoction;

    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    private class FullSyncDataNow extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;
        public FullSyncDataNow(DisplayItemPics activity)
        {
            pDialogGetStores = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
            pDialogGetStores.setMessage("Submitting Order Details...");
            pDialogGetStores.setIndeterminate(false);
            pDialogGetStores.setCancelable(false);
            pDialogGetStores.setCanceledOnTouchOutside(false);
            pDialogGetStores.show();


        }

        @Override

        protected Void doInBackground(Void... params) {

            int Outstat=3;

            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);


            dbengine.open();
            dbengine.UpdateStoreEndVisit(storeID, StampEndsTime);
            dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(),"3",strGlobalOrderID);
            dbengine.UpdateStoreStoreReturnDetail(storeID.trim(),"3",strGlobalOrderID);
            dbengine.UpdateStoreFlag(storeID.trim(), 3);
            dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 3,strGlobalOrderID);

            //dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);

            dbengine.close();
            dbengine.updateStoreQuoteSubmitFlgInStoreMstr(storeID.trim(),0);
            if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
            {
                String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                if(!strDefaultPaymentStageForStore.equals(""))
                {
                    dbengine.open();
                    dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"3");
                    dbengine.close();
                }
            }
            dbengine.open();
            String presentRoute=dbengine.GetActiveRouteID();
            dbengine.close();


			/*long syncTIMESTAMP = System.currentTimeMillis();
			Date dateobj = new Date(syncTIMESTAMP);*/
            SimpleDateFormat df1 = new SimpleDateFormat("dd.MMM.yyyy.HH.mm.ss",Locale.ENGLISH);

            newfullFileName=imei+"."+presentRoute+"."+ df1.format(dateobj);




            try {


                File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

                if (!OrderXMLFolder.exists())
                {
                    OrderXMLFolder.mkdirs();

                }
                String routeID=dbengine.GetActiveRouteIDSunil();

                DA.open();
                DA.export(CommonInfo.DATABASE_NAME, newfullFileName,routeID);
                DA.close();


                //dbengine.deleteAllXmlDataTable( "4");
                dbengine.savetbl_XMLfiles(newfullFileName, "3","0");
                dbengine.open();

                dbengine.UpdateStoreFlag(storeID.trim(), 5);
                dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 5,strGlobalOrderID);
                dbengine.UpdateStoreMaterialphotoFlag(storeID.trim(), 5);
                dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);
                dbengine.UpdateNewAddedStorephotoFlag(storeID.trim(), 5);
                dbengine.UpdatetblContentStoreWiseSstat(storeID.trim(), 5,"3","1");

                dbengine.close();
                if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
                {
                    String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                    if(!strDefaultPaymentStageForStore.equals(""))
                    {
                        dbengine.open();
                        dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"4");
                        dbengine.close();
                    }
                }


            } catch (Exception e) {

                e.printStackTrace();
                if(pDialogGetStores.isShowing())
                {
                    pDialogGetStores.dismiss();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(pDialogGetStores.isShowing())
            {
                pDialogGetStores.dismiss();
            }

            try
            {
                StoreSelection.flgChangeRouteOrDayEnd=0;
                Intent syncIntent = new Intent(DisplayItemPics.this, SyncMaster.class);
                //syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/RSPLSFAXml/" + newfullFileName + ".xml");
                syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + newfullFileName + ".xml");

                syncIntent.putExtra("OrigZipFileName", newfullFileName);
                syncIntent.putExtra("whereTo", "Regular");
                startActivity(syncIntent);
                finish();


            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    }


    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void UpdateLocationAndProductAllData() {
        checkHighAccuracyLocationMode(DisplayItemPics.this);
      /*  dbengine.open();
        dbengine.UpdateStoreActualLatLongi(storeID, String.valueOf(fnLati), String.valueOf(fnLongi), "" + fnAccuracy, fnAccurateProvider, flgLocationServicesOnOffOrderReview, flgGPSOnOffOrderReview, flgNetworkOnOffOrderReview, flgFusedOnOffOrderReview, flgInternetOnOffWhileLocationTrackingOrderReview, flgRestartOrderReview, flgStoreOrderOrderReview);


        dbengine.close();
*/
        FullSyncDataNow task = new FullSyncDataNow(DisplayItemPics.this);
        task.execute();
    }

    public void fnCreateLastKnownFinalLocation(String chekLastGPSLat,String chekLastGPSLong,String chekLastGpsAccuracy)
    {

        try {

            JSONArray jArray=new JSONArray();
            JSONObject jsonObjMain=new JSONObject();


            JSONObject jOnew = new JSONObject();
            jOnew.put( "chekLastGPSLat",chekLastGPSLat);
            jOnew.put( "chekLastGPSLong",chekLastGPSLong);
            jOnew.put( "chekLastGpsAccuracy", chekLastGpsAccuracy);


            jArray.put(jOnew);
            jsonObjMain.put("GPSLastLocationDetils", jArray);

            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;


            // If file does not exists, then create it
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            FileWriter fw;
            try {
                fw = new FileWriter(file.getAbsoluteFile());

                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(jsonObjMain.toString());

                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
				 /*  file=contextcopy.getFilesDir();
				//fileOutputStream=contextcopy.openFileOutput("FinalGPSLastLocation.txt", Context.MODE_PRIVATE);
				fileOutputStream.write(jsonObjMain.toString().getBytes());
				fileOutputStream.close();*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{

        }
    }


    public void showAlertForEveryOne(String msg)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(DisplayItemPics.this);
        alertDialogNoConn.setTitle("Information");
        alertDialogNoConn.setMessage(msg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(R.string.txtOk,new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();


            }
        });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();
    }


    public void openCustomCamara()
    {
        if(dialog!=null)
        {
            if(!dialog.isShowing())
            {
                openCamera();
            }
        }
        else
        {
            openCamera();
        }
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params)
    {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            // zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            // zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
            if(dialog!=null){
                if(dialog.isShowing()){
                    dialog.dismiss();

                }
            }
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;

                if (pictureFile == null) {
                    return;
                }
                try
                {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    arrImageData.add(0,pictureFile);
                    arrImageData.add(1,pictureFile.getName());
                    dialog.dismiss();
                    if(pictureFile!=null)
                    {
                        File file=pictureFile;
                        System.out.println("File +++"+pictureFile);
                        imageName=pictureFile.getName();
                        CommonFunction.normalizeImageForUri(DisplayItemPics.this,Uri.fromFile(pictureFile));
                        Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()),120,120);

                        long syncTIMESTAMP = System.currentTimeMillis();
                        Date dateobj = new Date(syncTIMESTAMP);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
                        String clkdTime = df.format(dateobj);
                        String valueOfKey=file.getAbsolutePath()+"~"+clkdTime;
                        if(clickedTagPhoto.equals("3"))
                        {
                            setSavedImageToScrollView(bitmap,imageName,valueOfKey,clickedTagPhoto,ll_LTFoodsItem,true);
                        }
                        else
                        {
                            setSavedImageToScrollView(bitmap,imageName,valueOfKey,clickedTagPhoto,ll_OtherItem,true);
                        }


                    }
//Show dialog here
//...
//Hide dialog here

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview--------------------------------------------------------------
                //	mPreview.refreshCamera(mCamera);
                //if want to release camera
                if(mCamera!=null){
                    mCamera.release();
                    mCamera=null;
                }
            }
        };
        return picture;
    }

    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            cancelCam.setEnabled(false);
            flashImage.setEnabled(false);
            if(cameraPreview!=null)
            {
                cameraPreview.setEnabled(false);
            }

            if(mCamera!=null)
            {
                mCamera.takePicture(null, null, mPicture);
            }
            else
            {
                dialog.dismiss();
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    };

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private static File getOutputMediaFile()
    {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", CommonInfo.ImagesFolder);

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss.SSS",Locale.ENGLISH).format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +CommonInfo.imei+ "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public void openCamera()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        arrImageData.clear();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(DisplayItemPics.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(DisplayItemPics.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(DisplayItemPics.this)) {
            Toast toast = Toast.makeText(DisplayItemPics.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(DisplayItemPics.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }

            //mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
            mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			/*if(mCamera==null){
				mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}*/

            boolean isParameterSet=false;
            try {
                Camera.Parameters params= mCamera.getParameters();


                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                //Camera.Size size1 = sizes.get(0);
                for(int i=0;i<sizes.size();i++)
                {

                    if(sizes.get(i).width > size.width)
                        size = sizes.get(i);
                }

                //System.out.println(size.width + "mm" + size.height);

                params.setPictureSize(size.width, size.height);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                //	params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

                //	params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                isLighOn = false;
                int minExpCom=params.getMinExposureCompensation();
                int maxExpCom=params.getMaxExposureCompensation();

                if( maxExpCom > 4 && minExpCom < 4)
                {
                    params.setExposureCompensation(4);
                }
                else
                {
                    params.setExposureCompensation(0);
                }

                params.setAutoExposureLock(false);
                params.setAutoWhiteBalanceLock(false);
                //String supportedIsoValues = params.get("iso-values");
                // String newVAlue = params.get("iso");
                //  params.set("iso","1600");
                params.setColorEffect("none");
                params.set("scene-mode","auto");
                params.setPictureFormat(ImageFormat.JPEG);
                params.setJpegQuality(70);
                params.setRotation(90);

                mCamera.setParameters(params);
                isParameterSet=true;
            }
            catch (Exception e)
            {

            }
            if(!isParameterSet)
            {
                Camera.Parameters params2 = mCamera.getParameters();
                params2.setPictureFormat(ImageFormat.JPEG);
                params2.setJpegQuality(70);
                params2.setRotation(90);

                mCamera.setParameters(params2);
            }

            setCameraDisplayOrientation(DisplayItemPics.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }

        capture = (Button)dialog.  findViewById(R.id.button_capture);

        flashImage= (ImageView)dialog.  findViewById(R.id.flashImage);
        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLighOn)
                {
                    // turn off flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    flashImage.setImageResource(R.drawable.flash_off);
                    isLighOn=false;
                }
                else
                {
                    // turn on flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    flashImage.setImageResource(R.drawable.flash_on);
                    mCamera.setParameters(params);

                    isLighOn=true;
                }
            }
        });

        final Button cancleCamera= (Button)dialog.  findViewById(R.id.cancleCamera);
        cancelCam=cancleCamera;
        cancleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                v.setEnabled(false);
                capture.setEnabled(false);
                cameraPreview.setEnabled(false);
                flashImage.setEnabled(false);

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;
                dialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        capture.setOnClickListener(captrureListener);

        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Get the pointer ID
                Camera.Parameters params = mCamera.getParameters();
                int action = event.getAction();

                if (event.getPointerCount() > 1) {
                    // handle multi-touch events
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE
                            && params.isZoomSupported()) {
                        mCamera.cancelAutoFocus();
                        handleZoom(event, params);
                    }
                } else {
                    // handle single touch events
                    if (action == MotionEvent.ACTION_UP) {
                        handleFocus(event, params);
                    }
                }
                return true;
            }
        });

        dialog.show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    void setSavedImageToScrollView(Bitmap bitmap, String imageName, String valueOfKey, final String clickedTagPhoto, final LinearLayout ll_imgToSet,boolean isClkdPic)
    {
        LayoutInflater inflate= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View convertView = inflate.inflate(R.layout.images_return_grid, null);

        //tagVal= catId+"_"+prodID+"_RemarkImage"



        ImageView img_thumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
        img_thumbnail.setImageBitmap(bitmap);

        ImageView imgCncl = (ImageView) convertView.findViewById(R.id.imgCncl);
        imgCncl.setTag(imageName);

        if(isClkdPic)
        {



            String photoPath=valueOfKey.split(Pattern.quote("~"))[0];
            String clickedDataTime=valueOfKey.split(Pattern.quote("~"))[1];




            //key- imagName
            //value- businessId^CatID^TypeID^templateID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        /*    savetbWareHousePhotoDetails(String StoreID,String PhotoName,
                    String PhotoPath,String ClickedDateTime,
                    String ClickTagPhoto,String Sstat)*/
            //  tagVal+"_edReason";

            hmapPhotoDetailsForSaving.put(imageName,photoPath+"^"+clickedDataTime+"^"+clickedTagPhoto);


        }

        ll_imgToSet.addView(convertView);

        imgCncl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String imageNameToDelVal=v.getTag().toString();

                ll_imgToSet.removeView(convertView);

                if(hmapPhotoDetailsForSaving.containsKey(imageNameToDelVal))
                {
                    String clkTagPic=hmapPhotoDetailsForSaving.get(imageNameToDelVal).split(Pattern.quote("^"))[2];
                    dbengine.deleteSinglePicSectionImage(storeID,clkTagPic,imageNameToDelVal);
                    hmapPhotoDetailsForSaving.remove(imageNameToDelVal);
                }




                //  String file_dj_path = Environment.getExternalStorageDirectory() + "/RSPLSFAImages/"+imageNameToDel;
                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imageNameToDelVal;

                File fdelete = new File(file_dj_path);
                if (fdelete.exists())
                {
                    if (fdelete.delete())
                    {
                        // Log.e("-->", "file Deleted :" + file_dj_path);
                        callBroadCast();
                    }
                    else
                    {
                        // Log.e("-->", "file not Deleted :" + file_dj_path);
                    }
                }
            }
        });
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public void getStockCompttrAvilable()
    {
        ArrayList<Integer> listStkCmpttr=dbengine.getLTfoodStockCmpttr(storeID);
        if((listStkCmpttr!=null) && (listStkCmpttr.size()>0))
        {
            isStockAvlbl=listStkCmpttr.get(0);
            isCmpttrAvlbl=listStkCmpttr.get(1);
        }
    }


    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();


        if(locationManager!=null)
        {
            boolean isGPSokCheckInResume = false;
            boolean isNWokCheckInResume=false;
            isGPSokCheckInResume = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNWokCheckInResume = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSokCheckInResume && !isNWokCheckInResume)
            {
                try
                {
                    showSettingsAlert();
                }
                catch(Exception e)
                {

                }
                isGPSokCheckInResume = false;
                isNWokCheckInResume=false;
            }
            else
            {


                if(countSubmitClicked==1)
                {

                    LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(DisplayItemPics.this,false,50);

                    countSubmitClicked++;


                }




            }
        }
        //new GetData().execute();
    }

}
