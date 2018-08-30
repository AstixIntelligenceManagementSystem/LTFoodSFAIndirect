package project.astix.com.ltfoodsfaindirect;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astix.Common.CommonInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class CompetitorPrdctPriceActivity extends AppCompatActivity implements InterfaceClass,MinMaxValidationCompttr{

    LinkedHashMap<String,String> hmapPrdctunitInGram;
    LinkedHashMap<String,String> hmapPrdctMinMax;
    Button btn_submit;
    ImageView btn_bck;
    LinkedHashMap<String,String> hmapPrdctImgPath;
    LinkedHashMap<String,ArrayList<String>> hmapCatIdAndCmpttrDtl;
    LinkedHashMap<String,ArrayList<String>> hmapCmpttrIddAndPrdctDtl;
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
    public String storeID;
    public String strGlobalOrderID="0";
    public String newfullFileName;
    public String imei;
    public String date;
    public String pickerDate;
    public String SN="";
    LinearLayout ll_CompetitorPrdct;
    DBAdapterKenya dbengine=new DBAdapterKenya(CompetitorPrdctPriceActivity.this);
    public AppLocationService appLocationService;

    public ProgressDialog pDialog2STANDBY;
    LocationRequest mLocationRequest;
    public LocationManager locationManager;
    LinkedHashMap<String,ArrayList<String>> hmapCmpttrChkdPrdct;
    LinkedHashMap<String,String> hmapCmpttrPrdctPTR=new LinkedHashMap<String,String>();


    String surveyDate;
    EditText edLastFocus=null;
    String crntEditTextTag;

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            return true;

        }
        if(keyCode==KeyEvent.KEYCODE_HOME)
        {

        }
        if(keyCode==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        if(keyCode== KeyEvent.KEYCODE_SEARCH)
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_prdct_price);

        long syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
         surveyDate= df.format(dateobj);
        ll_CompetitorPrdct= (LinearLayout) findViewById(R.id.ll_CompetitorPrdct);
        locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);
        btn_submit= (Button) findViewById(R.id.btn_submit);
        btn_bck= (ImageView) findViewById(R.id.btn_bck);
        getDataFromIntent();
        createViews();
        //inflatePrdctCompetitor();
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storeOrderReviewIntent = new Intent(CompetitorPrdctPriceActivity.this, OrderReview.class);
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
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isFlgPrdctPriceFld=2;//=validatePriceFilled();
                if(validateMinMax())
                {

                    if((hmapCmpttrPrdctPTR!=null) && (hmapCmpttrPrdctPTR.size()>0))
                    {
                        savingPrdctCompetitor();
                        Intent storeOrderReviewIntent=new Intent(CompetitorPrdctPriceActivity.this,DisplayItemPics.class);
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
                        showAlertForEveryOne("Please fill atleast one product PTR");
                    }

                        /*    boolean isGPSEnabled2 = false;
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
                            }*/




                }
               else if(isFlgPrdctPriceFld==0) {
                    showAlertForEveryOne("Please fill atleast one Competitor Product's price to retailer and price to consumer");
                }

            }
        });
    }


   public void alertValidation(String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompetitorPrdctPriceActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Information");

        // Setting Dialog Message
        alertDialog.setMessage(message);


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                savingPrdctCompetitor();
                Intent storeOrderReviewIntent=new Intent(CompetitorPrdctPriceActivity.this,DisplayItemPics.class);
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
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    void createViews()
    {
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(Map.Entry<String,ArrayList<String>> entry:hmapCatIdAndCmpttrDtl.entrySet())
        {
            View view_template = inflater1.inflate(R.layout.template_feedbckcmp, null);

            TextView txt_catName= (TextView) view_template.findViewById(R.id.txt_catName);
            txt_catName.setText(entry.getKey().split(Pattern.quote("^"))[1]);
            final String catId=entry.getKey().split(Pattern.quote("^"))[0];
            final LinearLayout ll_CatTempltParent= (LinearLayout) view_template.findViewById(R.id.ll_CatTempltParent);
            ll_CatTempltParent.setTag(entry.getKey()+"_ll"); //tag- catID_ll

            ArrayList<String> list_compttrDtl=entry.getValue();
            for(final String cmpptr:list_compttrDtl)
            {
                View view_row= inflater1.inflate(R.layout.custom_row, null);

                CheckBox cb_CompBox= (CheckBox) view_row.findViewById(R.id.cb_CompBox);
                cb_CompBox.setTag(cmpptr);//tag- catID_prdctID_cb

                TextView txt_CompName= (TextView) view_row.findViewById(R.id.txt_CompName);
                //tag- catID_prdctID_text
                txt_CompName.setText(cmpptr);

                LinearLayout ll_prdctDetail= (LinearLayout) view_row.findViewById(R.id.ll_prdctDetail);



                if((hmapCmpttrIddAndPrdctDtl!=null) && (hmapCmpttrIddAndPrdctDtl.size()>0))
                {
                    if(hmapCmpttrIddAndPrdctDtl.containsKey(cmpptr))
                    {

                        ArrayList<String> listPrdct=hmapCmpttrIddAndPrdctDtl.get(cmpptr);

                        if((listPrdct!=null) && (listPrdct.size()>0) && (listPrdct.get(0)!=null))
                        {
                            View view_PrdctGrid= inflater1.inflate(R.layout.prdctdetail_grid, null);
                            ExpandableHeightGridView expandableHeightGridView= (ExpandableHeightGridView) view_PrdctGrid.findViewById(R.id.expandable_gridview);
                            expandableHeightGridView.setExpanded(true);
                            PTRProductAdapter  adapterImage = new PTRProductAdapter(this,listPrdct,hmapPrdctImgPath,hmapCmpttrPrdctPTR);

                            expandableHeightGridView.setAdapter(adapterImage);

                            ll_prdctDetail.addView(view_PrdctGrid);
                        }


                    }
                }


                ll_CatTempltParent.addView(view_row);
            }

            ll_CompetitorPrdct.addView(view_template);
        }
    }


    public void savingPrdctCompetitor()
    {


        dbengine.deleteLastCompetitrPrdctPTRPTC(storeID);
        for(Map.Entry<String,ArrayList<String>> entry:hmapCatIdAndCmpttrDtl.entrySet())
        {

            String businessUnitId=entry.getKey().toString().split(Pattern.quote("^"))[0];
            String businessUnit=entry.getKey().toString().split(Pattern.quote("^"))[1];

            ArrayList<String> listCmpttrBrand=entry.getValue();
            if(listCmpttrBrand!=null && listCmpttrBrand.size()>0)
            {
                int index=1;
                for(String cmpttrDesc:listCmpttrBrand)
                {
                    if(hmapCmpttrIddAndPrdctDtl.containsKey(cmpttrDesc)) {

                        ArrayList<String> listPrdct = hmapCmpttrIddAndPrdctDtl.get(cmpttrDesc);

                        if ((listPrdct != null) && (listPrdct.size() > 0) && (listPrdct.get(0) != null)) {
                            for(String prdctDesc:listPrdct)
                            {
                                String cmpttrPrdctId=prdctDesc.split(Pattern.quote("^"))[0];
                                String cmpttrPrdct=prdctDesc.split(Pattern.quote("^"))[1];

                                String prdctPTR="";

                                if(hmapCmpttrPrdctPTR.containsKey(cmpttrPrdctId+"_PTR"))
                                {
                                    prdctPTR=hmapCmpttrPrdctPTR.get(cmpttrPrdctId+"_PTR");
                                }


                                if(!TextUtils.isEmpty(prdctPTR))
                                {

                                    dbengine.insertCompetitrPrdctPTRPTC(storeID,cmpttrPrdctId,cmpttrPrdct,prdctPTR,businessUnitId,businessUnit,1,surveyDate);
                                }
                            }

                        }
                    }

                    //ed_priceToRtlr.setTag(cmpttrPrdctId+"_PTR");

                    //ed_priceToConsumer.setTag(cmpttrPrdctId+"_PTC");


                }


            }

        }


    }

    private void getDataFromIntent() {


        Intent passedvals = getIntent();

        storeID = passedvals.getStringExtra("storeID");
        imei = passedvals.getStringExtra("imei");
        date = passedvals.getStringExtra("userdate");
        pickerDate = passedvals.getStringExtra("pickerDate");
        SN = passedvals.getStringExtra("SN");
        ArrayList<LinkedHashMap<String,ArrayList<String>>> listMstrDetails=dbengine.getPTRMstrDetails(storeID);
        hmapCatIdAndCmpttrDtl=listMstrDetails.get(0);
        hmapCmpttrIddAndPrdctDtl=listMstrDetails.get(1);
        hmapPrdctMinMax=dbengine.getMinMaxCmpttrPrdct();
        hmapPrdctunitInGram=dbengine.getGrmUnitPrdct();
       // hmapCmpttrChkdPrdct=dbengine.getCmpttrChkdPrdct(storeID);
        hmapPrdctImgPath=dbengine.getPrdctImgPath();
        strGlobalOrderID=dbengine.fngetOrderIDAganistStore(storeID);
        hmapCmpttrPrdctPTR=dbengine.getSavedPTRPTC(storeID);
    }



    public LinearLayout getLinearLayout( int flgPaddingApplicable)
    {
        LinearLayout lay = new LinearLayout(CompetitorPrdctPriceActivity.this);

        LinearLayout.LayoutParams llParams=new LinearLayout.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        lay.setOrientation(LinearLayout.VERTICAL);
        llParams.setMargins(0,0,0,5);
        if(flgPaddingApplicable==2)
        {
            lay.setBackgroundResource(R.drawable.card_background_gray);
            lay.setPadding(16,5,16,5);


        }
        else if(flgPaddingApplicable==1)
        {
            lay.setBackgroundResource(R.drawable.card_background_even);
            lay.setPadding(0,5,0,5);

        }
        else
        {
            lay.setBackgroundResource(R.drawable.card_background_odd);
            lay.setPadding(0,5,0,5);
        }
        lay.setLayoutParams(llParams);
        lay.removeAllViews();
        return lay;
    }
    public TextView getTextView(String catgryDes, String tagVal,boolean isHeader)
    {


        TextView txtVw_ques=new TextView(CompetitorPrdctPriceActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, 1f);
        txtVw_ques.setLayoutParams(layoutParams1);
        txtVw_ques.setTag(tagVal);

        txtVw_ques.setPadding(0,3,0,3);
        if(isHeader)
        {
            txtVw_ques.setGravity(Gravity.CENTER);
            txtVw_ques.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            txtVw_ques.setGravity(Gravity.LEFT);
        }
        txtVw_ques.setTextColor(getResources().getColor(R.color.primaryColorDark));
        txtVw_ques.setText(catgryDes);






        return txtVw_ques;
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


    public void fnSaveFilledDataToDatabase(int valBtnClickedFrom)
    {

        //valBtnClickedFrom=Save/Save And Exit/Submit

        //Declare  Outstat=0;  // Outstat=1 (Save,SaveExit) , Outstat=3(Submit)



        if(valBtnClickedFrom==3)//Clicked By Btn Submitt
        {
            //Send Data for Sync

            // Changes By Sunil
            AlertDialog.Builder alertDialogSubmitConfirm = new AlertDialog.Builder(CompetitorPrdctPriceActivity.this);
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
                            FullSyncDataNow task = new FullSyncDataNow(CompetitorPrdctPriceActivity.this);
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

                        appLocationService=new AppLocationService();






                        LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                        llaaa.locationRetrievingAndDistanceCalculating(CompetitorPrdctPriceActivity.this,false,50);


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
        if(!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy)))
        {

            fnLati=String.valueOf(fnLati);
            fnLongi=String.valueOf(fnLongi);
            fnAccuracy=""+finalAccuracy;

            fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy));
            UpdateLocationAndProductAllData();


        }
        else
        {
            countSubmitClicked++;
            if(countSubmitClicked==1)
            {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CompetitorPrdctPriceActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Information");
                alertDialog.setIcon(R.drawable.error_info_ico);
                alertDialog.setCancelable(false);
                // Setting Dialog Message
                alertDialog.setMessage("Your current location is same as previous, so please turn off your location services then turn on, it back again.");

                // On pressing Settings button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        countSubmitClicked++;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();



            }
            else
            {

                fnLati=String.valueOf(fnLati);
                fnLongi=String.valueOf(fnLongi);
                fnAccuracy=""+finalAccuracy;

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



    public boolean validateMinMax()
    {
        boolean validated=true;






        if(edLastFocus!=null)
        {
            String prdctId=crntEditTextTag.split(Pattern.quote("^"))[0];
            String prdctName=crntEditTextTag.split(Pattern.quote("^"))[1];
            String value=edLastFocus.getText().toString().trim();
            if(!TextUtils.isEmpty(value))
            {

                hmapCmpttrPrdctPTR.put(prdctId+"_PTR",value);
                if((hmapPrdctunitInGram.containsKey(prdctId)) && (hmapPrdctMinMax.containsKey(prdctId)))
                {
                    String grmPerUnit=hmapPrdctunitInGram.get(prdctId);
                    String minVal=(hmapPrdctMinMax.get(prdctId)).split(Pattern.quote("^"))[0];
                    String maxVal=(hmapPrdctMinMax.get(prdctId)).split(Pattern.quote("^"))[1];
                    Double netPtrPerKG=(Double.parseDouble(value))/(Double.parseDouble(grmPerUnit));
                    if(netPtrPerKG<Double.parseDouble(minVal))
                    {
                 //       Price of <<>> is out of range. Please check you have price of unit and not per kg. Update price if incorrect or leave it if unit price is OK
                        alertValidation("Price of "+prdctName+" is out of range. Please check you have price of unit and not per kg. Update price if incorrect or leave it if unit price is OK");
                        validated=false;
                    }
                    else if(netPtrPerKG>Double.parseDouble(maxVal))
                    {
                        alertValidation("Price of "+prdctName+" is out of range. Please check you have price of unit and not per kg. Update price if incorrect or leave it if unit price is OK");
                        validated=false;
                    }
                }
            }
            else
            {
                if(hmapCmpttrPrdctPTR.containsKey(prdctId+"_PTR"))
                {
                    hmapCmpttrPrdctPTR.remove(prdctId+"_PTR");
                }
            }
        }
        return validated;
    }

    @Override
    public void edittextValPTR(String value,String tagPrdct) {
        String prdctId=tagPrdct.split(Pattern.quote("^"))[0];
        String prdctName=tagPrdct.split(Pattern.quote("^"))[1];
        if(!TextUtils.isEmpty(value))
        {
            hmapCmpttrPrdctPTR.put(prdctId+"_PTR",value);
            if((hmapPrdctunitInGram.containsKey(prdctId)) && (hmapPrdctMinMax.containsKey(prdctId)))
            {
                String grmPerUnit=hmapPrdctunitInGram.get(prdctId);
                String minVal=(hmapPrdctMinMax.get(prdctId)).split(Pattern.quote("^"))[0];
                String maxVal=(hmapPrdctMinMax.get(prdctId)).split(Pattern.quote("^"))[1];
                Double netPtrPerKG=(Double.parseDouble(value))/(Double.parseDouble(grmPerUnit));
                if(netPtrPerKG<Double.parseDouble(minVal))
                {
                    showAlertForEveryOne("Price of "+prdctName+" is out of range. Please check you have price of unit and not per kg. Update price if incorrect or leave it if unit price is OK");
                }
                else if(netPtrPerKG>Double.parseDouble(maxVal))
                {
                    showAlertForEveryOne("Price of "+prdctName+" is out of range. Please check you have price of unit and not per kg. Update price if incorrect or leave it if unit price is OK");
                }
            }
        }
        else
        {
            if(hmapCmpttrPrdctPTR.containsKey(prdctId+"_PTR"))
            {
                hmapCmpttrPrdctPTR.remove(prdctId+"_PTR");
            }
        }


    }

    @Override
    public void crntFocusEditext(EditText edText, String tag) {
        edLastFocus=edText;
        crntEditTextTag=tag;
    }


    private class FullSyncDataNow extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;
        public FullSyncDataNow(CompetitorPrdctPriceActivity activity)
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
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
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
                Intent syncIntent = new Intent(CompetitorPrdctPriceActivity.this, SyncMaster.class);
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
        checkHighAccuracyLocationMode(CompetitorPrdctPriceActivity.this);
        dbengine.open();
        dbengine.UpdateStoreActualLatLongi(storeID, String.valueOf(fnLati), String.valueOf(fnLongi), "" + fnAccuracy, fnAccurateProvider, flgLocationServicesOnOffOrderReview, flgGPSOnOffOrderReview, flgNetworkOnOffOrderReview, flgFusedOnOffOrderReview, flgInternetOnOffWhileLocationTrackingOrderReview, flgRestartOrderReview, flgStoreOrderOrderReview);


        dbengine.close();

        FullSyncDataNow task = new FullSyncDataNow(CompetitorPrdctPriceActivity.this);
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
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(CompetitorPrdctPriceActivity.this);
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
}
