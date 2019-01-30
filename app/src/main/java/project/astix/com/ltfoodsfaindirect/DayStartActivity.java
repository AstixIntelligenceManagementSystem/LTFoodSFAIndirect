package project.astix.com.ltfoodsfaindirect;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.astix.Common.CommonInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class DayStartActivity extends BaseActivity implements InterfaceClass,OnMapReadyCallback,CompoundButton.OnCheckedChangeListener
{

    boolean isLateApplicable=false;
    LinkedHashMap<String,String> hmapReasonIdAndDescr_details;
    String reasonId="0";
    String otherReasonForLate="NA";
    static int flgDaySartWorking = 0;
    String crntServerTime;
    String crntAttndncTime="9:30 AM";
    DatabaseAssistant DASFA = new DatabaseAssistant(this);
    public long syncTIMESTAMP;
    ProgressDialog pDialog2;
    String PersonNodeID="NA";
    String PersonNodeType="NA";
    String PersonName="NA";
    String OptionID="NA";
    String OptionDesc="NA";

    ServiceWorker serviceWorker;
    String finalPinCode="NA";
    String finalCity="NA";
    String finalState="NA";
    public static int flgRestart=0;

    String cityID="NA";
    String StateID="NA";
    String MapAddress="NA";
    String MapPincode="NA";
    String MapCity="NA";
    String MapState="NA";

    int refreshCount=0;
    int countSubmitClicked=0;
    public LocationManager locationManager;
    int intentFrom=0;
    LinearLayout ll_map,ll_comment,ll_DBR_Name;

    EditText et_DBR_Name,rsnLatetext;
    public String ReasonId="0";;
    public String ReasonText="NA";
    public int chkFlgForErrorToCloseApp=0;
    String[] reasonNames;
    String[] reasonLate;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);

    TextView txt_DBR_Name;
    public String fDate;
    public SimpleDateFormat sdf;
    SharedPreferences sPrefAttandance;

    FragmentManager manager;
    FragmentTransaction fragTrans;
    MapFragment mapFrag;
    String LattitudeFromLauncher="NA";
    String LongitudeFromLauncher="NA";
    public String AccuracyFromLauncher="NA";
    String AddressFromLauncher="NA";
    LinearLayout ll_start,ll_startAfterDayEndFirst,ll_startAfterDayEndSecond,ll_Working,ll_NoWorking;
    LinearLayout ll_Working_parent,ll_NoWorking_parent,ll_reason;
    Button but_Next;

    TextView txt_DayStarttime;

    LinkedHashMap<String,String>  hmapSelectedCheckBoxData=new LinkedHashMap<String,String>();

    LinkedHashMap<Integer, String> hmapReasonIdAndDescrForWorking_details=new LinkedHashMap<Integer, String>();
    ArrayList<String> listTxtBxToShow;
    LinkedHashMap<Integer, String> hmapReasonIdAndDescrForNotWorking_details=new LinkedHashMap<Integer, String>();

    public CheckBox[] cb;
    public RadioButton[] rb;


    String[] Distribtr_list;
    String DbrNodeId,DbrNodeType,DbrName;
    ArrayList<String> DbrArray=new ArrayList<String>();


    RadioButton rb_workingYes,rb_workingNo;//=null;
    Spinner spinner_for_filter,spnr_late,spinner_location;
    ArrayAdapter<String> adapterLocation;
    ArrayAdapter<String> adapterDBR;

    String[] listLocationWrkng={"Select Working Location","At Distributor Location","Market/Other Location"};
    String DistributorName_Global="Select Distributor";
    String DistributorId_Global="0";
    String DistributorNodeType_Global="0";
    private Button btn_refresh;
    private LinearLayout ll_refresh;
    private RadioGroup rg_yes_no;
    TextView tv_MapLocationCorrectText;

    private RadioButton rb_yes;
    private RadioButton rb_no;


    public void customHeader()
    {


        TextView tv_heading=(TextView) findViewById(R.id.tv_heading);
        tv_heading.setText("Day Start");

        ImageView imgVw_next=(ImageView) findViewById(R.id.imgVw_next);

        ImageView imgVw_back=(ImageView) findViewById(R.id.imgVw_back);
        imgVw_next.setVisibility(View.GONE);
        imgVw_back.setVisibility(View.GONE);
        if(intentFrom==0)
        {
            imgVw_back.setVisibility(View.GONE);
            imgVw_next.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i=new Intent(DayStartActivity.this,AllButtonActivity.class);
                    startActivity(i);
                    finish();

                }
            });
        }
        else
        {
            imgVw_next.setVisibility(View.GONE);
            imgVw_back.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i=new Intent(DayStartActivity.this,StoreSelection.class);

                    startActivity(i);
                    finish();

                }
            });
        }
    }
    private void getDataFromDatabase() throws IOException
    {
        hmapReasonIdAndDescrForWorking_details=dbengine.fetch_Reason_List_for_option();
        hmapReasonIdAndDescrForNotWorking_details=dbengine.fetch_NoWorking_Reason_List();
        listTxtBxToShow=dbengine.fetch_Text_To_Show();
    }


    private void createCheckBoxForWorking()
    {
        cb = new CheckBox[hmapReasonIdAndDescrForWorking_details.size()];

        int i = 0;
        for (Map.Entry<Integer, String> entry : hmapReasonIdAndDescrForWorking_details.entrySet())
        {
            EditText editText = null;
            cb[i] = new CheckBox(this);
            cb[i].setText(entry.getValue());
            cb[i].setTag(entry.getKey().toString().trim());


            if(listTxtBxToShow!=null && listTxtBxToShow.contains(entry.getKey().toString().trim()))
            {
                editText= getEditText(entry.getKey().toString().trim()+"_ed");
            }

            ll_Working.addView(cb[i]);
            if(editText!=null)
            {
                ll_Working.addView(editText);
                editText.setVisibility(View.GONE);
            }
            cb[i].setOnCheckedChangeListener(this);
            i = i + 1;
        }

    }

    public void onCheckedChanged(CompoundButton cb, boolean isChecked){
        String checkedText = cb.getText()+"";
        String checkedID = cb.getTag()+"";
        EditText editText= (EditText) ll_Working.findViewWithTag(checkedID+"_ed");
        if(listTxtBxToShow!=null && listTxtBxToShow.contains(checkedID))
        {
            if(isChecked)
            {
                if(editText!=null)
                {
                    editText.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                if(editText!=null)
                {
                    editText.setVisibility(View.GONE);
                }

            }
        }


      if(isChecked)
           {
              hmapSelectedCheckBoxData.put(checkedID.trim(),checkedText.trim());


                CommonInfo.DayStartClick=1;

           }
           else
           {


              hmapSelectedCheckBoxData.remove(checkedID.trim());
               if(hmapSelectedCheckBoxData.size()==0)
               {
                   CommonInfo.DayStartClick=0;
               }

           }



    }

    private void createRadioButtonForNotWorking()
    {
         rb = new RadioButton[hmapReasonIdAndDescrForNotWorking_details.size()];
         int i = 0;
        for (Map.Entry<Integer, String> entry : hmapReasonIdAndDescrForNotWorking_details.entrySet())
        {
            rb[i] = new RadioButton(this);
            rb[i].setText(entry.getValue());
            rb[i].setTag(entry.getKey().toString().trim());
            rb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean isChecked)
                {
                    String checkedText = cb.getText()+"";
                    String checkedID = cb.getTag()+"";
                    if(isChecked)
                    {
                        ReasonText = hmapReasonIdAndDescrForNotWorking_details.get(Integer.parseInt(checkedID.trim()));
                        ReasonId = "" + checkedID;


                        // for checked Selected the Radio Button in NoT Working
                        int i=0;
                        for (Map.Entry<Integer, String> entry : hmapReasonIdAndDescrForNotWorking_details.entrySet())
                        {
                            RadioButton rb = (RadioButton)ll_NoWorking.getChildAt(i);
                            if(rb.getTag().toString().trim().equals(checkedID.trim()))
                            {
                                rb.setChecked(true);
                            }
                            else
                            {
                                rb.setChecked(false);
                            }

                            i = i + 1;
                        }

                        // for unchecked all the CheckBox in Today Working

                        i=0;

                        for(i=0;i<ll_Working.getChildCount();i++)
                        {
                            View view=ll_Working.getChildAt(i);
                            if(view instanceof CheckBox)
                            {
                                CheckBox chckBox= (CheckBox) view;
                                if(chckBox.isChecked())
                                {
                                    chckBox.setChecked(false);
                                }
                            }
                            else if(view instanceof EditText)
                            {
                                EditText editText= (EditText) view;
                                if(editText!=null)
                                {
                                    editText.setText("");
                                }
                            }
                        }
                       /* i=0;
                        for (Map.Entry<Integer, String> entry : hmapReasonIdAndDescrForWorking_details.entrySet())
                        {
                            cb = (CheckBox) ll_Working.getChildAt(i);
                            cb.setChecked(false);
                            i = i + 1;
                        }*/
                        hmapSelectedCheckBoxData.clear();
                        hmapSelectedCheckBoxData.clear();

                        CommonInfo.DayStartClick=2;
                    }
                    else
                    {

                    }
                }
            });
            ll_NoWorking.addView(rb[i]);
            i = i + 1;
        }

    }
        @Override
    public void onMapReady(GoogleMap googleMap)
    {
        if(!LattitudeFromLauncher.equals("NA") && !LattitudeFromLauncher.equals("0.0"))
        {
            googleMap.clear();
            try {
                googleMap.setMyLocationEnabled(false);
            }
            catch(SecurityException e)
            {

            }
           if(AccuracyFromLauncher.equals("0.0") || AccuracyFromLauncher.equals("NA")|| AccuracyFromLauncher.equals("")|| AccuracyFromLauncher==null){

            }
            else{

                if(Double.parseDouble(AccuracyFromLauncher)>100){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    Marker locationMarker=googleMap.addMarker(marker);
                    locationMarker.showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher)), 15));
                  /*  if(!fetchedFromDb.equals("0") && fetchedFromDb!=null){
                        distanceText.setVisibility(View.VISIBLE);
                    }*/

                }
                else{
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Marker locationMarker=googleMap.addMarker(marker);
                    locationMarker.showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher)), 15));
                   // distanceText.setVisibility(View.GONE);
                }

            }




        }
        else
        {
            if(refreshCount==2)
            {
               // txt_rfrshCmnt.setText(getString(R.string.loc_not_found));
               // btn_refresh.setVisibility(View.GONE);
            }

            try
            {
                googleMap.setMyLocationEnabled(false);
            }
            catch(SecurityException e)
            {

            }
           // googleMap.addMarker(new MarkerOptions().position(new LatLng(22.7253, 75.8655)).title("Indore"));
            googleMap.moveCamera(CameraUpdateFactory.zoomIn());
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                  //  marker.setTitle(StoreName);
                }
            });

        }

    }

    public void fnGetDistributorList()
    {
        dbengine.open();
        Distribtr_list=dbengine.getDistributorData();
        dbengine.close();
        for(int i=0;i<Distribtr_list.length;i++)
        {
            //System.out.println("DISTRIBUTOR........"+Distribtr_list[i]);
            String value=Distribtr_list[i];
            DbrNodeId=value.split(Pattern.quote("^"))[0];
            DbrNodeType=value.split(Pattern.quote("^"))[1];
            DbrName=value.split(Pattern.quote("^"))[2];
            DbrArray.add(DbrName);
        }

        adapterDBR = new ArrayAdapter<String>(DayStartActivity.this,R.layout.initial_spinner_text,DbrArray);
        adapterDBR.setDropDownViewResource(R.layout.spina);

        spinner_for_filter.setAdapter(adapterDBR);

    }
    private void getReasonDetail()
    {



        hmapReasonIdAndDescr_details=dbengine.fetch_Reason_Late();

        int index=0;
        if(hmapReasonIdAndDescr_details!=null)
        {
            reasonLate=new String[hmapReasonIdAndDescr_details.size()];
            LinkedHashMap<String, String> map = new LinkedHashMap<>(hmapReasonIdAndDescr_details);
            Set set2 = map.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext())
            {
                Map.Entry me2 = (Map.Entry)iterator.next();
                reasonLate[index]=me2.getKey().toString();
                index=index+1;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DayStartActivity.this, android.R.layout.simple_spinner_item,reasonLate);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnr_late.setAdapter(adapter);
            spnr_late.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daystart);
        Intent intent=getIntent();
        intentFrom= intent.getIntExtra("IntentFrom", 0);
        sPrefAttandance=getSharedPreferences(CommonInfo.AttandancePreference, MODE_PRIVATE);
        serviceWorker=new ServiceWorker();
        customHeader();
        btn_refresh= (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setVisibility(View.GONE);
        ll_refresh= (LinearLayout) findViewById(R.id.ll_refresh);
        ll_refresh.setVisibility(View.GONE);
        rsnLatetext= (EditText) findViewById(R.id.rsnLatetext);
        rsnLatetext.setVisibility(View.GONE);
        rb_yes= (RadioButton) findViewById(R.id.rb_yes);
        rb_no=(RadioButton)findViewById(R.id.rb_no);

        rg_yes_no= (RadioGroup) findViewById(R.id.rg_yes_no);
        rg_yes_no.setVisibility(View.GONE);

        rg_yes_no.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i!=-1)
                {
                    RadioButton    radioButtonVal = (RadioButton) radioGroup.findViewById(i);
                    if(radioButtonVal.getId()==R.id.rb_yes)
                    {
                        ll_refresh.setVisibility(View.GONE);

                    }
                    else if(radioButtonVal.getId()==R.id.rb_no)
                    {
                        ll_refresh.setVisibility(View.VISIBLE);


                    }
                }

            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                boolean isGPSok = false;
                boolean isNWok=false;
                isGPSok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNWok = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(!isGPSok)
                {
                    isGPSok = false;
                }
                if(!isNWok)
                {
                    isNWok = false;
                }
                if(!isGPSok && !isNWok)
                {
                    try
                    {
                        showSettingsAlert();
                    }
                    catch(Exception e)
                    {

                    }

                    isGPSok = false;
                    isNWok=false;
                }
                else
                {
                    rg_yes_no.clearCheck();
                    ll_refresh.setVisibility(View.GONE);
                    LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(DayStartActivity.this,false,50);
                }


            }
        });



        try
        {
            getDataFromDatabase();
        }
        catch(Exception e)
        {

        }
        locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);
        Date date1=new Date();
        sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        fDate = sdf.format(date1).toString().trim();

        txt_DBR_Name=(TextView) findViewById(R.id.txt_DBR_Name);
        ll_start=(LinearLayout)findViewById(R.id.ll_start);
        ll_start.setVisibility(View.VISIBLE);
        ll_startAfterDayEndFirst=(LinearLayout)findViewById(R.id.ll_startAfterDayEndFirst);
        ll_startAfterDayEndFirst.setVisibility(View.GONE);
        ll_startAfterDayEndSecond=(LinearLayout)findViewById(R.id.ll_startAfterDayEndSecond);
        ll_startAfterDayEndSecond.setVisibility(View.GONE);
        ll_map=(LinearLayout)findViewById(R.id.ll_map);
        ll_map.setVisibility(View.GONE);
        ll_DBR_Name= (LinearLayout) findViewById(R.id.ll_DBR_Name);
        ll_DBR_Name.setVisibility(View.GONE);
        et_DBR_Name= (EditText) findViewById(R.id.et_DBR_Name);
        ll_Working=(LinearLayout)findViewById(R.id.ll_Working);
        ll_Working.setVisibility(View.GONE);
        ll_NoWorking=(LinearLayout)findViewById(R.id.ll_NoWorking);
        ll_NoWorking.setVisibility(View.GONE);



        ll_Working_parent=(LinearLayout)findViewById(R.id.ll_Working_parent);
        ll_Working_parent.setVisibility(View.GONE);

        ll_NoWorking_parent=(LinearLayout)findViewById(R.id.ll_NoWorking_parent);
        ll_NoWorking_parent.setVisibility(View.GONE);

        tv_MapLocationCorrectText=(TextView) findViewById(R.id.tv_MapLocationCorrectText);
        tv_MapLocationCorrectText.setVisibility(View.GONE);



        rb_workingYes=(RadioButton)findViewById(R.id.rb_workingYes);
        rb_workingYes.setVisibility(View.GONE);
        rb_workingNo=(RadioButton)findViewById(R.id.rb_workingNo);
        rb_workingNo.setVisibility(View.GONE);
        spinner_location=(Spinner)findViewById(R.id.spinner_location);
         adapterLocation = new ArrayAdapter<String>(DayStartActivity.this, android.R.layout.simple_spinner_item,listLocationWrkng);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(adapterLocation);
        spinner_location.setVisibility(View.GONE);
        spinner_for_filter=(Spinner)findViewById(R.id.spinner_for_filter);

        spinner_for_filter.setVisibility(View.GONE);
        spnr_late=(Spinner)findViewById(R.id.spnr_late);
        spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spinner_location.getSelectedItem().toString().equals("Select Working Location"))
                {

                    ll_map.setVisibility(View.VISIBLE);
                    ll_Working_parent.setVisibility(View.VISIBLE);
                    ll_NoWorking_parent.setVisibility(View.GONE);
                    ll_map.setVisibility(View.VISIBLE);
                    ll_comment.setVisibility(View.VISIBLE);
                    if(isLateApplicable)
                    {
                       // ll_reason.setVisibility(View.VISIBLE);
                        ll_reason.setVisibility(View.GONE);
                    }

                    but_Next.setVisibility(View.VISIBLE);

                    rg_yes_no.setVisibility(View.VISIBLE);

                    btn_refresh.setVisibility(View.VISIBLE);
                    tv_MapLocationCorrectText.setVisibility(View.VISIBLE);

                    if(spinner_location.getSelectedItem().toString().equals("New Distributor"))
                    {
                        spinner_for_filter.setSelection(adapterDBR.getPosition("Select Distributor"));
                        spinner_for_filter.setVisibility(View.GONE);
                        ll_DBR_Name.setVisibility(View.VISIBLE);
                        txt_DBR_Name.setText(getString(R.string.DistbtrName));
                        CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag("7");
                        if(checkBox!=null)
                        {
                            checkBox.setChecked(true);
                        }

                    }
                    else  if(spinner_location.getSelectedItem().toString().equals("Market/Other Location"))
                    {
                        spinner_for_filter.setSelection(adapterDBR.getPosition("Select Distributor"));
                        spinner_for_filter.setVisibility(View.GONE);
                        ll_DBR_Name.setVisibility(View.VISIBLE);
                        txt_DBR_Name.setText(getString(R.string.other_loc));
                        CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag("7");
                        if(checkBox!=null)
                        {
                            checkBox.setChecked(false);
                        }
                    }
                    else
                    {
                        //DbrArray
                        if(DbrArray.size()>1)
                        {
                            spinner_for_filter.setSelection(adapterDBR.getPosition(DbrArray.get(1)));
                        }

                        spinner_for_filter.setVisibility(View.VISIBLE);
                        ll_DBR_Name.setVisibility(View.GONE);
                        CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag("7");
                        if(checkBox!=null)
                        {
                            checkBox.setChecked(false);
                        }
                    }
                }
                else
                {
                    spinner_for_filter.setSelection(adapterDBR.getPosition("Select Distributor"));
                    spinner_for_filter.setVisibility(View.GONE);
                    ll_map.setVisibility(View.GONE);
                    ll_Working_parent.setVisibility(View.GONE);

                    ll_map.setVisibility(View.GONE);
                    ll_reason.setVisibility(View.GONE);
                    btn_refresh.setVisibility(View.GONE);
                    ll_refresh.setVisibility(View.GONE);
                    rg_yes_no.setVisibility(View.GONE);
                    tv_MapLocationCorrectText.setVisibility(View.GONE);

                    ll_DBR_Name.setVisibility(View.GONE);

                    CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag("7");
                    if(checkBox!=null)
                    {
                        checkBox.setChecked(false);
                    }

                    but_Next.setVisibility(View.GONE);

                    DistributorName_Global="Select Distributor";
                    DistributorId_Global="0";
                    DistributorNodeType_Global="0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnr_late.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((!spnr_late.getSelectedItem().toString().equals("Select Reason")) && (!spnr_late.getSelectedItem().toString().equals("No Reason")))
                {
                    if(listTxtBxToShow.contains(hmapReasonIdAndDescr_details.get(spnr_late.getSelectedItem().toString())))
                    {
                        rsnLatetext.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        rsnLatetext.setVisibility(View.GONE);
                    }
                    otherReasonForLate=spnr_late.getSelectedItem().toString();
                      reasonId=hmapReasonIdAndDescr_details.get(spnr_late.getSelectedItem().toString());
                }
                else
                {
                  reasonId="0";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_for_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id)
            {

                TextView tv =(TextView) view;
                String text=tv.getText().toString();

                if(text.equals("Select Distributor"))
                {
                    DistributorName_Global="Select Distributor";
                    DistributorId_Global="0";
                    DistributorNodeType_Global="0";
                }
                else
                {
                    DistributorName_Global=tv.getText().toString();
                    String   Distribtor_Detail=dbengine.fetchDistributorIdByName(text);
                    DistributorId_Global=Distribtor_Detail.split(Pattern.quote("^"))[0];
                    DistributorNodeType_Global=Distribtor_Detail.split(Pattern.quote("^"))[1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        ll_reason=(LinearLayout) findViewById(R.id.ll_reason);
        ll_reason.setVisibility(View.GONE);
        ll_comment=(LinearLayout) findViewById(R.id.ll_comment);
        ll_comment.setVisibility(View.GONE);

        but_Next=(Button) findViewById(R.id.but_Next);
        but_Next.setVisibility(View.GONE);
        fnGetDistributorList();
        getReasonDetail();

        Button but_DayStart=(Button) findViewById(R.id.but_DayStart);
        but_DayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                boolean isGPSok = false;
                boolean isNWok=false;
                isGPSok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNWok = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(!isGPSok)
                {
                    isGPSok = false;
                }
                if(!isNWok)
                {
                    isNWok = false;
                }
                if(!isGPSok && !isNWok)
                {
                    try
                    {
                        showSettingsAlert();
                    }
                    catch(Exception e)
                    {

                    }

                    isGPSok = false;
                    isNWok=false;
                }
                else
                {

                   ll_Working.setVisibility(View.VISIBLE);
                    ll_NoWorking.setVisibility(View.VISIBLE);
                    createCheckBoxForWorking();
                    createRadioButtonForNotWorking();
                    txt_DayStarttime=(TextView)findViewById(R.id.txt_DayStarttime);
                    txt_DayStarttime.setText(getDateAndTimeInSecond());


                    rb_workingYes.setVisibility(View.VISIBLE);
                    rb_workingNo.setVisibility(View.VISIBLE);

                    manager= getFragmentManager();
                    mapFrag = (MapFragment)manager.findFragmentById(R.id.map);
                    mapFrag.getView().setVisibility(View.VISIBLE);
                   // mapFrag.addMarker(new MarkerOptions().position(new LatLng(22.7253, 75.8655)).title("Indore"));
                   // mapFrag.set

                      mapFrag.getMapAsync(DayStartActivity.this);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.show(mapFrag);

                    ll_start=(LinearLayout)findViewById(R.id.ll_start);
                    ll_start.setVisibility(View.GONE);
                    ll_startAfterDayEndFirst=(LinearLayout)findViewById(R.id.ll_startAfterDayEndFirst);
                    ll_startAfterDayEndFirst.setVisibility(View.VISIBLE);
                    ll_startAfterDayEndSecond=(LinearLayout)findViewById(R.id.ll_startAfterDayEndSecond);
                    ll_startAfterDayEndSecond.setVisibility(View.VISIBLE);



                    LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(DayStartActivity.this,false,50);
                }



            }
        });
        Button but_Exit=(Button) findViewById(R.id.but_Exit);
        but_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finishAffinity();
            }
        });


        but_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                otherReasonForLate="NA";
                if(rb_workingYes.isChecked())
                {
                    if(parseDateTime(crntServerTime).after(crntDateTime(crntAttndncTime)))
                    {
                        // Toast.makeText(DayStartActivity.this,"Time between 9:30 and 10:30",Toast.LENGTH_SHORT).show();
                       /* if(reasonId.equals("0"))
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                            alertDialog.setTitle(getResources().getString(R.string.AlertDialogHeaderErrorMsg));
                            alertDialog.setMessage(getResources().getString(R.string.selectReasonForLate));
                            alertDialog.setIcon(R.drawable.error);
                            alertDialog.setCancelable(false);

                            alertDialog.setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                            return;

                        }
                        else if(spnr_late.getSelectedItem().toString().equals("Others."))
                        {

                            if(TextUtils.isEmpty(rsnLatetext.getText().toString()))
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                                alertDialog.setTitle(getResources().getString(R.string.AlertDialogHeaderErrorMsg));
                                alertDialog.setMessage(getResources().getString(R.string.selectOtherReasonForLate));
                                alertDialog.setIcon(R.drawable.error);
                                alertDialog.setCancelable(false);

                                alertDialog.setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                                return;

                            }
                            else
                            {
                                otherReasonForLate=rsnLatetext.getText().toString();
                            }
                        }
                        else
                        {
                            otherReasonForLate=spnr_late.getSelectedItem().toString();
                        }*/
                    }


                    else
                    {
                        //Toast.makeText(DayStartActivity.this,"Time before 9:30",Toast.LENGTH_SHORT).show();
                    }

                }
                //New Distributor","Other Location"
                if((!spinner_location.getSelectedItem().toString().equals("New Distributor")) && (!spinner_location.getSelectedItem().toString().equals("Market/Other Location")) )

                {
                    if(rb_workingYes.isChecked() && DistributorName_Global.equals("Select Distributor"))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                        alertDialog.setTitle(getResources().getString(R.string.AlertDialogHeaderErrorMsg));
                        alertDialog.setMessage(getResources().getString(R.string.selectDistributorProceeds));
                        alertDialog.setIcon(R.drawable.error);
                        alertDialog.setCancelable(false);

                        alertDialog.setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        return;
                    }
                }

                 if (CommonInfo.DayStartClick==0)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.AlertDialogHeaderErrorMsg));
                    alertDialog.setMessage(getResources().getString(R.string.selectAtleastOneOption));
                    alertDialog.setIcon(R.drawable.error);
                    alertDialog.setCancelable(false);

                    alertDialog.setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }
                else if(validateEditText())
                {

                    return;
                }
               else
                {
                    if(hmapSelectedCheckBoxData.size()>0)
                      {
                        ReasonId="";
                        ReasonText="";
                          if(isLateApplicable)
                          {
                              ReasonId=reasonId;
                              ReasonText=otherReasonForLate;
                          }
                        for(Map.Entry<String, String> entry:hmapSelectedCheckBoxData.entrySet())
                        {
                            String key = entry.getKey().toString().trim();
                            String value = entry.getValue().toString().trim();



                            if(ReasonId.equals(""))
                            {
                                ReasonId= key;
                            }
                            else
                            {
                                ReasonId=ReasonId+"$"+key;
                            }

                            if(ReasonText.equals(""))
                            {
                                ReasonText= value;
                            }
                            else
                            {
                                ReasonText=ReasonText+"$"+value;
                            }
                        }



                    }


                    String commentValue="NA";
                            EditText commenttext=(EditText)findViewById(R.id.commenttext);
                           if(!TextUtils.isEmpty(commenttext.getText().toString().trim()))
                           {
                               commentValue=commenttext.getText().toString().trim();
                           }
                           else
                           {

                           }


                    if(DistributorName_Global.equals("Select Distributor"))
                    {
                        DistributorName_Global="NA";
                        DistributorId_Global="0";
                        DistributorNodeType_Global="0";
                    }

                    dbengine.updatetblAttandanceDetails("33","No Working",ReasonId,ReasonText,commentValue,DistributorId_Global,DistributorNodeType_Global,DistributorName_Global,parseDateRmvAMPMTime(crntServerTime),getDateAndTimeAMPM(),reasonId,otherReasonForLate);
                    syncStartAfterSavindData();
                }


            }
        });

        rb_workingYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(rb_workingYes.isChecked())
                {
                    for(int i=0;i<ll_NoWorking.getChildCount();i++)
                    {
                        View view=ll_NoWorking.getChildAt(i);
                        if(view instanceof RadioButton)
                        {
                            RadioButton rbBtn= (RadioButton) view;
                            if(rbBtn.isChecked())
                            {
                                rbBtn.setChecked(false);
                            }
                        }


                    }

                    rb_workingNo.setChecked(false);
                    ll_NoWorking_parent.setVisibility(View.GONE);
                    ll_comment.setVisibility(View.GONE);
                    spinner_location.setVisibility(View.VISIBLE);
                    spinner_location.setSelection(adapterLocation.getPosition("Select Working Location"));

                    if(listLocationWrkng.length>1)
                    {
                        spinner_location.setSelection(adapterLocation.getPosition("Market/Other Location"));
                    }
                }
            }
        });

        rb_workingNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(rb_workingNo.isChecked())
                {
                    for(int i=0;i<ll_Working.getChildCount();i++)
                    {
                        View view=ll_Working.getChildAt(i);
                        if(view instanceof CheckBox)
                        {
                            CheckBox chckBox= (CheckBox) view;
                            if(chckBox.isChecked())
                            {
                                chckBox.setChecked(false);
                            }
                        }
                        else if(view instanceof EditText)
                        {
                            EditText editText= (EditText) view;
                            if(editText!=null)
                            {
                                editText.setText("");
                            }
                        }
                    }
                    rb_workingYes.setChecked(false);
                    spinner_for_filter.setVisibility(View.GONE);
                    ll_map.setVisibility(View.GONE);
                    ll_Working_parent.setVisibility(View.GONE);
                    ll_NoWorking_parent.setVisibility(View.VISIBLE);
                    ll_map.setVisibility(View.GONE);
                    ll_reason.setVisibility(View.GONE);
                    btn_refresh.setVisibility(View.GONE);
                    ll_refresh.setVisibility(View.GONE);
                    rg_yes_no.setVisibility(View.GONE);
                    tv_MapLocationCorrectText.setVisibility(View.GONE);
                    spinner_location.setSelection(adapterLocation.getPosition("Select Working Location"));
                    spinner_location.setVisibility(View.GONE);

                    ll_comment.setVisibility(View.VISIBLE);

                    but_Next.setText(getText(R.string.txtSubmit));
                    but_Next.setVisibility(View.VISIBLE);

                     DistributorName_Global="Select Distributor";
                     DistributorId_Global="0";
                     DistributorNodeType_Global="0";


                }
            }
        });


    }

    public void syncStartAfterSavindData()
    {
        SharedPreferences.Editor editor=sPrefAttandance.edit();
        editor.clear();
        editor.commit();
        sPrefAttandance.edit().putString("AttandancePref", fDate).commit();
        flgDaySartWorking=1;
        try {

            new bgTasker().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //System.out.println(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            //System.out.println(e);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        boolean isGPSok = false;
        boolean isNWok=false;
        isGPSok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNWok = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSok)
        {
            isGPSok = false;
        }
        if(!isNWok)
        {
            isNWok = false;
        }
        if(!isGPSok && !isNWok)
        {
            try
            {
                showSettingsAlert();
            }
            catch(Exception e)
            {

            }

            isGPSok = false;
            isNWok=false;
        }
        else
        {
            if (isOnline())
            {
                new CurrentDateTime().execute();
            }
            else
            {
                showNoConnAlert(false);
            }
           // LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
           // llaaa.locationRetrievingAndDistanceCalculating(DayStartActivity.this);
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
                                String city, String state) {

        //System.out.println("SHIVA"+fnLati+","+fnLongi+","+finalAccuracy+","+fnAccurateProvider+","+GpsLat+","+GpsLong+","+GpsAccuracy+","+NetwLat+","+NetwLong+","+NetwAccuracy+","+FusedLat+","+FusedLong+","+FusedAccuracy+","+AllProvidersLocation+","+GpsAddress+","+NetwAddress+","+FusedAddress+","+FusedLocationLatitudeWithFirstAttempt+","+FusedLocationLongitudeWithFirstAttempt+","+FusedLocationAccuracyWithFirstAttempt+","+fnLongi+","+flgLocationServicesOnOff+","+flgGPSOnOff+","+flgNetworkOnOff+","+flgFusedOnOff+","+flgInternetOnOffWhileLocationTracking+","+address+","+pincode+","+city+","+state);

        if(!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy)))
        {
            LattitudeFromLauncher=String.valueOf(fnLati);
            LongitudeFromLauncher=String.valueOf(fnLongi);
            AccuracyFromLauncher=String.valueOf(finalAccuracy);

            fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(finalAccuracy));
            manager= getFragmentManager();
            mapFrag = (MapFragment)manager.findFragmentById(R.id.map);
            mapFrag.getView().setVisibility(View.VISIBLE);
            mapFrag.getMapAsync(DayStartActivity.this);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.show(mapFrag);
            dbengine.open();
            dbengine.savetblAttandanceDetails(txt_DayStarttime.getText().toString().trim(),PersonNodeID,PersonNodeType,PersonName,OptionID,OptionDesc,
                    ReasonId,ReasonText,FusedAddress,finalPinCode,finalCity,finalState,fnLati,fnLongi,
                    finalAccuracy,"0",fnAccurateProvider,AllProvidersLocation,FusedAddress,
                    GpsLat,GpsLong,GpsAccuracy,GpsAddress,
                    NetwLat,NetwLong,NetwAccuracy,NetwAddress,
                    FusedLat,FusedLong,FusedAccuracy,FusedAddress,
                    FusedLocationLatitudeWithFirstAttempt,FusedLocationLongitudeWithFirstAttempt,
                    FusedLocationAccuracyWithFirstAttempt,3,flgLocationServicesOnOff,flgGPSOnOff,flgNetworkOnOff,
                    flgFusedOnOff,flgInternetOnOffWhileLocationTracking,flgRestart, cityID, StateID, MapAddress, MapCity, MapPincode, MapState);

            dbengine.close();



        }
        else
        {
            countSubmitClicked++;
            if(countSubmitClicked==1)
            {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DayStartActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle(R.string.AlertDialogHeaderMsg);
                alertDialog.setIcon(R.drawable.error_info_ico);
                alertDialog.setCancelable(false);
                // Setting Dialog Message
                alertDialog.setMessage(getText(R.string.AlertSameLoc));

                // On pressing Settings button
                alertDialog.setPositiveButton(getText(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener() {
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
                    countSubmitClicked++;
                LattitudeFromLauncher=String.valueOf(fnLati);
                LongitudeFromLauncher=String.valueOf(fnLongi);
                AccuracyFromLauncher=String.valueOf(finalAccuracy);
                manager= getFragmentManager();
                mapFrag = (MapFragment)manager.findFragmentById(R.id.map);
                mapFrag.getView().setVisibility(View.VISIBLE);
                mapFrag.getMapAsync(DayStartActivity.this);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(mapFrag);

                dbengine.open();
                dbengine.savetblAttandanceDetails(txt_DayStarttime.getText().toString().trim(),PersonNodeID,PersonNodeType,PersonName,OptionID,OptionDesc,
                        ReasonId,ReasonText,FusedAddress,finalPinCode,finalCity,finalState,fnLati,fnLongi,
                        finalAccuracy,"0",fnAccurateProvider,AllProvidersLocation,FusedAddress,
                        GpsLat,GpsLong,GpsAccuracy,GpsAddress,
                        NetwLat,NetwLong,NetwAccuracy,NetwAddress,
                        FusedLat,FusedLong,FusedAccuracy,FusedAddress,
                        FusedLocationLatitudeWithFirstAttempt,FusedLocationLongitudeWithFirstAttempt,
                        FusedLocationAccuracyWithFirstAttempt,3,flgLocationServicesOnOff,flgGPSOnOff,flgNetworkOnOff,
                        flgFusedOnOff,flgInternetOnOffWhileLocationTracking,flgRestart, cityID, StateID, MapAddress, MapCity, MapPincode, MapState);

                dbengine.close();




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
            String fpath = Environment.getExternalStorageDirectory()+"/"+ CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;


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
    public void showSettingsAlert()
    {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialog.setIcon(R.drawable.error_info_ico);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(getText(R.string.genTermGPSDisablePleaseEnable));

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.AlertDialogOkButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void showNoConnAlert(final boolean isSyncdCalled)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(DayStartActivity.this);
        alertDialogNoConn.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialogNoConn.setMessage(R.string.NoDataConnectionFullMsg);
        alertDialogNoConn.setNeutralButton(R.string.AlertDialogOkButton,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        if(!isSyncdCalled)
                        {
                             finish();
                        }

                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }

    public void errorGettingServerDate()
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(DayStartActivity.this);
        alertDialogNoConn.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialogNoConn.setMessage(R.string.txtErrRetrieving);
        alertDialogNoConn.setNeutralButton(R.string.txtRetry,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                        new CurrentDateTime().execute();
                        // finish();
                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }

    public String displayAlertDialog()
    {
        String str="cb_NoWorking Selected;";
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_nostore, null);
        final EditText et_Reason = (EditText) alertLayout.findViewById(R.id.et_Reason);
        et_Reason.setVisibility(View.INVISIBLE);

        final Spinner spinner_reason=(Spinner) alertLayout.findViewById(R.id.spinner_reason);

        ArrayAdapter adapterCategory=new ArrayAdapter(DayStartActivity.this, android.R.layout.simple_spinner_item,reasonNames);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason.setAdapter(adapterCategory);

        spinner_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                String	spinnerReasonSelected = spinner_reason.getSelectedItem().toString();
                ReasonText=spinnerReasonSelected;
                int check=dbengine.fetchFlgToShowTextBox(spinnerReasonSelected);
                ReasonId=dbengine.fetchReasonIdBasedOnReasonDescr(spinnerReasonSelected);
                if(check==0)
                {
                    et_Reason.setVisibility(View.INVISIBLE);
                }
                else
                {
                    et_Reason.setVisibility(View.VISIBLE);
                }


                //ReasonId,ReasonText
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub

            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.AlertDialogHeaderMsg);
        alert.setView(alertLayout);
        //alert.setIcon(R.drawable.info_ico);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {


                if (ReasonText.equals("")||ReasonText.equals("Select Reason"))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please select the reason first.");
                    alertDialog.setIcon(R.drawable.error);
                    alertDialog.setCancelable(false);

                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int which)
                        {
                            dialog.dismiss();
                            displayAlertDialog();

                        }
                    });
                    alertDialog.show();
                }
                else
                {


                   /* Date pdaDate=new Date();
                    SimpleDateFormat sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    String CurDate = sdfPDaDate.format(pdaDate).toString().trim();

                    if(et_Reason.isShown())
                    {

                        if(TextUtils.isEmpty(et_Reason.getText().toString().trim()))
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Please enter the reason.");
                            alertDialog.setIcon(R.drawable.error);
                            alertDialog.setCancelable(false);

                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    dialog.dismiss();
                                    displayAlertDialog();

                                }
                            });
                            alertDialog.show();
                        }
                        else
                        {
                            ReasonText = et_Reason.getText().toString();
                            if(isOnline())
                            {
                               GetNoStoreVisitForDay task = new GetNoStoreVisitForDay(DayStartActivity.this);
                                task.execute();
                            }
                            else
                            {
                                dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                                dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                                dbengine.updateSstattblNoVisitStoreDetails(3);


                                String aab=dbengine.fetchReasonDescr();

                                showNoConnAlert();

                            }



                        }
                    }
                    else
                    {
                        if(isOnline())
                        {
                           GetNoStoreVisitForDay task = new GetNoStoreVisitForDay(DayStartActivity.this);
                            task.execute();
                        }
                        else
                        {
                            dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                            dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                            dbengine.updateSstattblNoVisitStoreDetails(3);
                            showNoConnAlert();

                        }


                    }*/


                }

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        return str;
    }

    private class GetNoStoreVisitForDay extends AsyncTask<Void, Void, Void>
    {
        ServiceWorker newservice = new ServiceWorker();

        ProgressDialog pDialogGetNoStoreVisitForDay;

        public GetNoStoreVisitForDay(DayStartActivity activity)
        {
            pDialogGetNoStoreVisitForDay = new ProgressDialog(activity);
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


            pDialogGetNoStoreVisitForDay.setTitle(getText(R.string.PleaseWaitMsg));
            pDialogGetNoStoreVisitForDay.setMessage(getText(R.string.RetrivingDataMsg));
            pDialogGetNoStoreVisitForDay.setIndeterminate(false);
            pDialogGetNoStoreVisitForDay.setCancelable(false);
            pDialogGetNoStoreVisitForDay.setCanceledOnTouchOutside(false);
            pDialogGetNoStoreVisitForDay.show();


        }

        @Override
        protected Void doInBackground(Void... params)
        {

            try {


                for(int mm = 1; mm < 2  ; mm++)
                {
                    if(mm==1)
                    {
                        newservice = newservice.getCallspSaveReasonForNoVisit(getApplicationContext(), fDate, getIMEI(), ReasonId,ReasonText);

                        if(!newservice.director.toString().trim().equals("1"))
                        {
                            if(chkFlgForErrorToCloseApp==0)
                            {
                                chkFlgForErrorToCloseApp=1;
                            }

                        }

                    }



                }


            } catch (Exception e)
            {
                Log.i("SvcMgr", "Service Execution Failed!", e);
            }

            finally
            {
                Log.i("SvcMgr", "Service Execution Completed...");
            }

            return null;
        }

        @Override
        protected void onCancelled()
        {
            Log.i("SvcMgr", "Service Execution Cancelled");
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);


            if(pDialogGetNoStoreVisitForDay.isShowing())
            {
                pDialogGetNoStoreVisitForDay.dismiss();
            }

            if(chkFlgForErrorToCloseApp==0)
            {
                dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                dbengine.updateCurDatetblNoVisitStoreDetails(fDate);

                dbengine.updateSstattblNoVisitStoreDetails(3);
                String aab=dbengine.fetchReasonDescr();
                //noVisit_tv.setText("Reason :"+ReasonText);
            }
            else
            {
                dbengine.updateSstattblNoVisitStoreDetailsAfterSync(4);

              /*  if(RowId==0)
                {
                    dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                    dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                    dbengine.updateSstattblNoVisitStoreDetails(3);
                    String aab=dbengine.fetchReasonDescr();
                   // noVisit_tv.setText("Reason :"+ReasonText);
                   // ll_noVisit.setEnabled(false);

                }
                else
                {
                    dbengine.updateSstattblNoVisitStoreDetailsAfterSync(4);
                    String aab=dbengine.fetchReasonDescr();
                  //  noVisit_tv.setText("Reason :"+ReasonText);
                   // ll_noVisit.setEnabled(false);

                }*/
                finishAffinity();
            }
        }
    }

    private class bgTasker extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {

            try {


               /* dbengine.open();
                String rID=dbengine.GetActiveRouteID();
                 dbengine.close();
*/


             //  pDialog2.dismiss();
                   // dbengine.open();

                    //dbengine.updateActiveRoute(rID, 0);
                   // dbengine.close();
                    // sync here


                    SyncNow();



            } catch (Exception e) {}

            finally {}

            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog2 = ProgressDialog.show(DayStartActivity.this,getText(R.string.PleaseWaitMsg),getText(R.string.genTermProcessingRequest), true);
            pDialog2.setIndeterminate(true);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @Override
        protected void onCancelled() {
            Log.i("bgTasker", "bgTasker Execution Cancelled");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.i("bgTasker", "bgTasker Execution cycle completed");
            pDialog2.dismiss();
           // whatTask = 0;

        }
    }

    public void SyncNow()
    {

        syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);


        dbengine.open();
        String presentRoute="0";
        dbengine.close();
        //syncTIMESTAMP = System.currentTimeMillis();
        //Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yyyy.HH.mm.ss",Locale.ENGLISH);
        //fullFileName1 = df.format(dateobj);
        String newfullFileName=getIMEI()+"."+presentRoute+"."+ df.format(dateobj);



        try
        {

            File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

            if (!OrderXMLFolder.exists())
            {
                OrderXMLFolder.mkdirs();
            }

            String routeID="0";

            DASFA.open();
            DASFA.export(dbengine.DATABASE_NAME, newfullFileName,routeID);


            DASFA.close();

            dbengine.savetbl_XMLfiles(newfullFileName, "3","1");

            dbengine.fnSettblAttandanceDetails();


            if(isOnline())
            {
                Intent syncIntent = new Intent(DayStartActivity.this, SyncMaster.class);
                syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + newfullFileName + ".xml");
                syncIntent.putExtra("OrigZipFileName", newfullFileName);
                syncIntent.putExtra("whereTo", "DayStart");
                startActivity(syncIntent);
                finish();
            }
            else
            {
                showNoConnAlert(true);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private class CurrentDateTime extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... params) {
            String serverTime="";
            try {

                 serverTime=serviceWorker.getCurrentDateTime(getIMEI(), CommonInfo.DATABASE_VERSIONID, CommonInfo.Application_TypeID);

            }
                catch (Exception e) {}

            finally {}

            return serverTime;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog2 = ProgressDialog.show(DayStartActivity.this,getText(R.string.PleaseWaitMsg),getText(R.string.genTermFetchingTime), true);
            pDialog2.setIndeterminate(true);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @Override
        protected void onCancelled() {
            Log.i("bgTasker", "bgTasker Execution Cancelled");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            pDialog2.dismiss();
            if((!TextUtils.isEmpty(result)) && (result!=null))
            {

                if(result.contains("^"))
                {
                    crntServerTime=result.split(Pattern.quote("^"))[0];
                    crntAttndncTime=result.split(Pattern.quote("^"))[1];
                }
                else
                {
                    crntServerTime=result;
                }

                if(parseDateTime(crntServerTime).after(crntDateTime(crntAttndncTime)) )
                {
                    isLateApplicable=true;
                    if(rb_workingYes.isChecked())
                    {
                        //ll_reason.setVisibility(View.VISIBLE);
                        ll_reason.setVisibility(View.GONE);
                    }

                    //Toast.makeText(DayStartActivity.this,"Time between 9:30 and 10:30",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    isLateApplicable=false;
                    //Toast.makeText(DayStartActivity.this,"Time before 9:30",Toast.LENGTH_SHORT).show();
                }

                if(countSubmitClicked==2)
                {
                     LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(DayStartActivity.this,false,50);
                }

            }
            else
            {
                errorGettingServerDate();
            }
            // whatTask = 0;

        }
    }

    public String parseDateRmvAMPMTime(String time) {
        String inputPattern = "dd-MMM-yyyy hh:mm:ss aa";
        String outputPattern = "dd-MMM-yyyy hh:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.ENGLISH);
        String dateFnl = "NA";
        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            dateFnl = outputFormat.format(date);
          
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Date fnldate = formatter.parse(dateInString);;
        return dateFnl;
    }
    public Date parseDateTime(String time) {
        String inputPattern = "dd-MMM-yyyy hh:mm:ss aa";
        String outputPattern = "hh:mm aa";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.ENGLISH);

        Date date,dateFnl = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            dateFnl=outputFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       // Date fnldate = formatter.parse(dateInString);;
        return dateFnl;
    }

    public Date crntDateTime(String time) {
        String inputPattern = "hh:mm aa";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.ENGLISH);


        Date date = null;


        try {
            date = inputFormat.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Date fnldate = formatter.parse(dateInString);;
        return date;
    }

    public String getDateAndTimeAMPM()
    {
        long  syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.ENGLISH);
        String curTime = df.format(dateobj);
        return curTime;
    }

    public EditText getEditText(String tagVal)
    {
        EditText editText=new EditText(DayStartActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                ((int) LinearLayout.LayoutParams.WRAP_CONTENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);

        params.leftMargin = 50;
        params.rightMargin = 50;
        editText.setBackground(getResources().getDrawable(R.drawable.et_boundary_retrun));
        editText.setLayoutParams(params);
        editText.setTag(tagVal);
        editText.setEms(10);
        return editText;
    }


    public boolean validateEditText()
    {
        boolean isEdiTextFilled=false;
        if(spinner_location.getSelectedItem().toString().equals("New Distributor"))
        {
           if(TextUtils.isEmpty(et_DBR_Name.getText().toString().trim()))
           {
               alertForValidation(getString(R.string.AlertDialogHeaderErrorMsg),getString(R.string.specifyDBRName));
               isEdiTextFilled=true;
               return isEdiTextFilled;
           }
           else
           {
               CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag("7");
               if(checkBox!=null)
               {

                   hmapSelectedCheckBoxData.put("7",et_DBR_Name.getText().toString().trim());
               }

           }

        }
        else  if(spinner_location.getSelectedItem().toString().equals("Market/Other Location"))
        {
            if(TextUtils.isEmpty(et_DBR_Name.getText().toString().trim()))
            {
               /* alertForValidation(getString(R.string.AlertDialogHeaderErrorMsg),getString(R.string.specifyLocatione));
                isEdiTextFilled=true;
                return isEdiTextFilled;*/
                hmapSelectedCheckBoxData.put("15","Market/Other Location");
            }
            else
            {
                hmapSelectedCheckBoxData.put("15","Market/Other Location"+"^"+et_DBR_Name.getText().toString().trim());
            }

        }
        if(listTxtBxToShow!=null)
        {
           for(int i=0;i<listTxtBxToShow.size();i++)
           {
               CheckBox checkBox= (CheckBox) ll_Working.findViewWithTag(listTxtBxToShow.get(i));
               if(checkBox!=null)
               {
                   if(checkBox.isChecked())
                   {
                       EditText editText= (EditText) ll_Working.findViewWithTag(listTxtBxToShow.get(i)+"_ed");
                       if(editText!=null)
                       {
                           if(TextUtils.isEmpty(editText.getText().toString().trim()))
                           {
                               isEdiTextFilled=true;
                               alertForValidation(getString(R.string.AlertDialogHeaderErrorMsg),getString(R.string.selectAtleastOneOptionwithedittext));
                               break;
                           }
                           else
                           {
                               hmapSelectedCheckBoxData.put(listTxtBxToShow.get(i),editText.getText().toString().trim());
                           }
                       }

                   }

               }

           }
        }
        return isEdiTextFilled;
    }

    public void alertForValidation(String title,String msg)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayStartActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.error);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int which)
            {
                dialog.dismiss();


            }
        });
        alertDialog.show();
    }
}
