package project.astix.com.ltfoodsfaindirect;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class CompetitorPrdctPriceActivity extends AppCompatActivity implements InterfaceClass{

    Button btn_submit;
    ImageView btn_bck;

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
    public Double fnAccuracy=0.0;


    DatabaseAssistant DA = new DatabaseAssistant(this);
    public String storeID;
    public String strGlobalOrderID="0";
    public String newfullFileName;
    public String imei;
    public String date;
    public String pickerDate;
    public String SN="";
    DBAdapterKenya dbengine=new DBAdapterKenya(CompetitorPrdctPriceActivity.this);
    public AppLocationService appLocationService;
    LinearLayout ll_CompetitorPrdct;
    public ProgressDialog pDialog2STANDBY;
    LocationRequest mLocationRequest;
    public LocationManager locationManager;
    LinkedHashMap<String,ArrayList<String>> hmapCmpttrChkdPrdct;
    LinkedHashMap<String,String> hmapCmpttrPrdctPTR=new LinkedHashMap<String,String>();
    LinkedHashMap<String,String> hmapCmpttrPrdctPTC=new LinkedHashMap<String,String>();

    String surveyDate;

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
        inflatePrdctCompetitor();
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
                int isFlgPrdctPriceFld=validatePriceFilled();
                if(isFlgPrdctPriceFld==2)
                {


                            savingPrdctCompetitor();
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
               else if(isFlgPrdctPriceFld==0) {
                    showAlertForEveryOne("Please fill atleast one Competitor Product's price to retailer and price to consumer");
                }

            }
        });
    }

    public int validatePriceFilled()
    {

        int isflgPriceFilled=0;

        //dbengine.deleteLastCompetitrPrdctPTRPTC(storeID);
        for(Map.Entry<String,ArrayList<String>> entry:hmapCmpttrChkdPrdct.entrySet())
        {

            String businessUnitId=entry.getKey().toString().split(Pattern.quote("^"))[0];
            String businessUnit=entry.getKey().toString().split(Pattern.quote("^"))[1];

            ArrayList<String> listCmpttrPrdct=entry.getValue();
            if(listCmpttrPrdct!=null && listCmpttrPrdct.size()>0)
            {

                for(String cmpttrPrdctDesc:listCmpttrPrdct)
                {
                    String cmpttrPrdctId=cmpttrPrdctDesc.split(Pattern.quote("^"))[0];
                    String cmpttrPrdct=cmpttrPrdctDesc.split(Pattern.quote("^"))[1];
                    String cmpttrCategory=cmpttrPrdctDesc.split(Pattern.quote("^"))[2];
                    String prdctPTR="";
                    String prdctPTC="";
                    if(hmapCmpttrPrdctPTR.containsKey(cmpttrPrdctId+"_PTR"))
                    {
                        prdctPTR=hmapCmpttrPrdctPTR.get(cmpttrPrdctId+"_PTR");
                    }
                    if(hmapCmpttrPrdctPTC.containsKey(cmpttrPrdctId+"_PTC"))
                    {
                        prdctPTC=hmapCmpttrPrdctPTC.get(cmpttrPrdctId+"_PTC");
                    }

                    if(!TextUtils.isEmpty(prdctPTC) && !TextUtils.isEmpty(prdctPTR))
                    {
                        if(Double.parseDouble(prdctPTR)<=Double.parseDouble(prdctPTC))
                    {

                        isflgPriceFilled=2;
                    }
                    else
                        {
                            isflgPriceFilled=1;
                            EditText ed_CmpttrPrdct= (EditText) ll_CompetitorPrdct.findViewWithTag(cmpttrPrdctId+"_PTC");
                            if(ed_CmpttrPrdct!=null)
                            {
                                ed_CmpttrPrdct.requestFocus();
                                ed_CmpttrPrdct.setText("");
                                startStopEditing(false,ed_CmpttrPrdct);

                            }
                            showAlertForEveryOne("PTC cannot be less than PTR for "+cmpttrPrdct);
                            break;
                        }



                    }
                    else if(TextUtils.isEmpty(prdctPTR) && (!TextUtils.isEmpty(prdctPTC)))
                    {
                        isflgPriceFilled=1;
                        EditText ed_CmpttrPrdct= (EditText) ll_CompetitorPrdct.findViewWithTag(cmpttrPrdctId+"_PTR");
                        if(ed_CmpttrPrdct!=null)
                        {
                            ed_CmpttrPrdct.requestFocus();

                        }
                        showAlertForEveryOne("Please fill PTR also of "+cmpttrPrdct);
                        break;
                    }
                    else if(TextUtils.isEmpty(prdctPTC) && (!TextUtils.isEmpty(prdctPTR)))
                    {
                        isflgPriceFilled=1;
                        EditText ed_CmpttrPrdct= (EditText) ll_CompetitorPrdct.findViewWithTag(cmpttrPrdctId+"_PTC");
                        if(ed_CmpttrPrdct!=null)
                        {
                            ed_CmpttrPrdct.requestFocus();

                        }
                        showAlertForEveryOne("Please fill PTC also of "+cmpttrPrdct);
                        break;

                    }


                    //ed_priceToRtlr.setTag(cmpttrPrdctId+"_PTR");

                    //ed_priceToConsumer.setTag(cmpttrPrdctId+"_PTC");


                }


            }
            if(isflgPriceFilled==1)
            {
                break;
            }

        }

        return isflgPriceFilled;
    }

    public void inflatePrdctCompetitor()
    {

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(Map.Entry<String,ArrayList<String>> entry:hmapCmpttrChkdPrdct.entrySet())
        {

            String businessUnitId=entry.getKey().toString().split(Pattern.quote("^"))[0];
            String businessUnit=entry.getKey().toString().split(Pattern.quote("^"))[1];
            TextView txtBusinessDesc=getTextView(businessUnit,businessUnitId);
            LinearLayout ll_BusinessSegment=getLinearLayout();

            ll_BusinessSegment.addView(txtBusinessDesc);
            ArrayList<String> listCmpttrPrdct=entry.getValue();
            if(listCmpttrPrdct!=null && listCmpttrPrdct.size()>0)
            {
                int index=1;
                for(String cmpttrPrdctDesc:listCmpttrPrdct)
                {
                    String cmpttrPrdctId=cmpttrPrdctDesc.split(Pattern.quote("^"))[0];
                    String cmpttrPrdct=cmpttrPrdctDesc.split(Pattern.quote("^"))[1];
                    String cmpttrCategory=cmpttrPrdctDesc.split(Pattern.quote("^"))[2];
                    final View viewCompttrProduct=inflater.inflate(R.layout.list_product_competitor,null);
                    TextView tvCtgryName= (TextView) viewCompttrProduct.findViewById(R.id.tvCtgryName);
                    TextView tvProdctName= (TextView) viewCompttrProduct.findViewById(R.id.tvProdctName);
                    tvCtgryName.setText(cmpttrCategory);
                    tvProdctName.setText(cmpttrPrdct);
                    final EditText ed_priceToRtlr= (EditText) viewCompttrProduct.findViewById(R.id.ed_priceToRtlr);
                    ed_priceToRtlr.setTag(cmpttrPrdctId+"_PTR");
                    final EditText ed_priceToConsumer= (EditText) viewCompttrProduct.findViewById(R.id.ed_priceToConsumer);
                    ed_priceToConsumer.setTag(cmpttrPrdctId+"_PTC");
             if(index%2==0)
             {
                 viewCompttrProduct.setBackgroundResource(R.drawable.card_background_even);
             }
				    else
             {

                 viewCompttrProduct.setBackgroundResource(R.drawable.card_background_odd);

             }
                    ed_priceToRtlr.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!ed_priceToRtlr.getText().toString().trim().equalsIgnoreCase("")) {


                                String editTextString = ed_priceToRtlr.getText().toString().trim();
                                if(editTextString.length()>2)
                                {
                                    int decimalIndexOf = editTextString.indexOf(".");

                                    if (decimalIndexOf >= 0) {
                                        startStopEditing(false,ed_priceToRtlr);
                                        if (editTextString.substring(decimalIndexOf).length() > 2) {

                                            startStopEditing(true,ed_priceToRtlr);

                                        }
                                    }
                                    else
                                    {
                                        startStopEditing(true,ed_priceToRtlr);
                                    }


                                }
                                else
                                {
                                    startStopEditing(false,ed_priceToRtlr);
                                }

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            String tagCmpttrPrdct=ed_priceToRtlr.getTag().toString();
                            if(!TextUtils.isEmpty(ed_priceToRtlr.getText().toString().trim()))
                            {
                                hmapCmpttrPrdctPTR.put(tagCmpttrPrdct,ed_priceToRtlr.getText().toString().trim());
                            }
                            else
                            {
                                hmapCmpttrPrdctPTR.remove(tagCmpttrPrdct);
                            }

                        }
                    });
                    ed_priceToConsumer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if (!ed_priceToConsumer.getText().toString().trim().equalsIgnoreCase("")) {


                                String editTextString = ed_priceToConsumer.getText().toString().trim();
                                if(editTextString.length()>2)
                                {
                                    int decimalIndexOf = editTextString.indexOf(".");

                                    if (decimalIndexOf >= 0) {
                                        startStopEditing(false,ed_priceToConsumer);
                                        if (editTextString.substring(decimalIndexOf).length() > 2) {

                                            startStopEditing(true,ed_priceToConsumer);

                                        }
                                    }
                                    else
                                    {
                                        startStopEditing(true,ed_priceToConsumer);
                                    }


                                }
                                else
                                {
                                    startStopEditing(false,ed_priceToConsumer);
                                }

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String tagCmpttrPrdct=ed_priceToConsumer.getTag().toString();
                            if(!TextUtils.isEmpty(ed_priceToConsumer.getText().toString().trim()))
                            {
                                hmapCmpttrPrdctPTC.put(tagCmpttrPrdct,ed_priceToConsumer.getText().toString().trim());
                            }
                            else
                            {
                                hmapCmpttrPrdctPTC.remove(tagCmpttrPrdct);
                            }
                        }
                    });
                    ll_BusinessSegment.addView(viewCompttrProduct);


                    index++;
                }
                ll_CompetitorPrdct.addView(ll_BusinessSegment);

            }

        }

    }

    public void savingPrdctCompetitor()
    {


        dbengine.deleteLastCompetitrPrdctPTRPTC(storeID);
        for(Map.Entry<String,ArrayList<String>> entry:hmapCmpttrChkdPrdct.entrySet())
        {

            String businessUnitId=entry.getKey().toString().split(Pattern.quote("^"))[0];
            String businessUnit=entry.getKey().toString().split(Pattern.quote("^"))[1];

            ArrayList<String> listCmpttrPrdct=entry.getValue();
            if(listCmpttrPrdct!=null && listCmpttrPrdct.size()>0)
            {
                int index=1;
                for(String cmpttrPrdctDesc:listCmpttrPrdct)
                {
                    String cmpttrPrdctId=cmpttrPrdctDesc.split(Pattern.quote("^"))[0];
                    String cmpttrPrdct=cmpttrPrdctDesc.split(Pattern.quote("^"))[1];
                    String cmpttrCategory=cmpttrPrdctDesc.split(Pattern.quote("^"))[2];
                    String prdctPTR="";
                    String prdctPTC="";
                    if(hmapCmpttrPrdctPTR.containsKey(cmpttrPrdctId+"_PTR"))
                    {
                        prdctPTR=hmapCmpttrPrdctPTR.get(cmpttrPrdctId+"_PTR");
                    }
                    if(hmapCmpttrPrdctPTC.containsKey(cmpttrPrdctId+"_PTC"))
                    {
                        prdctPTC=hmapCmpttrPrdctPTC.get(cmpttrPrdctId+"_PTC");
                    }

                    if(!TextUtils.isEmpty(prdctPTC) && !TextUtils.isEmpty(prdctPTR))
                    {

                        dbengine.insertCompetitrPrdctPTRPTC(storeID,cmpttrPrdctId,cmpttrPrdct,prdctPTR,prdctPTC,businessUnitId,businessUnit,1,surveyDate);
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

        hmapCmpttrChkdPrdct=dbengine.getCmpttrChkdPrdct(storeID);


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



                        pDialog2STANDBY= ProgressDialog.show(CompetitorPrdctPriceActivity.this,getText(R.string.genTermPleaseWaitNew) ,getText(R.string.genTermRetrivingLocation), true);
                        pDialog2STANDBY.setIndeterminate(true);

                        pDialog2STANDBY.setCancelable(false);
                        pDialog2STANDBY.show();


                        LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                        llaaa.locationRetrievingAndDistanceCalculating(CompetitorPrdctPriceActivity.this);


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
            fnAccuracy=Double.parseDouble(finalAccuracy);

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
                fnAccuracy=Double.parseDouble(finalAccuracy);

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

    public void startStopEditing(boolean isLock, EditText editText) {

        if (isLock) {

            editText.setFilters(new InputFilter[] { new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                   if((source.length() < 1))
                   {
                       return "";
                   }

                   else
                   {
                       if(source.equals("."))
                       {
                           return null;
                       }
                       else
                       {
                           return dest.subSequence(dstart, dend);
                       }

                   }
                  //  return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
                }
            } });

        } else {

            editText.setFilters(new InputFilter[] { new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return null;
                }
            } });
        }
    }


    public LinearLayout getLinearLayout()
    {
        LinearLayout lay = new LinearLayout(CompetitorPrdctPriceActivity.this);

        lay.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setBackgroundResource(R.drawable.card_background_gray);
        lay.removeAllViews();
        return lay;
    }
    public TextView getTextView(String catgryDes, String tagVal)
    {


        TextView txtVw_ques=new TextView(CompetitorPrdctPriceActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 1f);
        txtVw_ques.setLayoutParams(layoutParams1);
        txtVw_ques.setTag(tagVal);
        txtVw_ques.setGravity(Gravity.CENTER);
        txtVw_ques.setPadding(0,3,0,3);
        txtVw_ques.setTextColor(getResources().getColor(R.color.primaryColorDark));
        txtVw_ques.setText(catgryDes);






        return txtVw_ques;
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
