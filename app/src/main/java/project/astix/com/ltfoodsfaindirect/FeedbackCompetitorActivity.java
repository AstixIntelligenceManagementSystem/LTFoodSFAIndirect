package project.astix.com.ltfoodsfaindirect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FeedbackCompetitorActivity extends BaseActivity
{
    LinearLayout ll_parent;
    LinkedHashMap<String,String> hmapPrdctIdAndName=new LinkedHashMap<>();
    LinkedHashMap<String,ArrayList<String>> hmapCatIdAndPrdctId=new LinkedHashMap<>();
    LinkedHashMap<String,String> hmapCatIdAndDesc=new LinkedHashMap<>();

    LinkedHashMap<String,String> hmapSavedCompetitrData=new LinkedHashMap<>();
    String[] prdctIDArray={"13","83","98","99","55"};
    String[] prdctNameArray={"Indiagate","Kohinoor","Rajdhani","Aahaar","Other"};
    String[] catIDArray={"1","1","1","2","2"};
    String[] catDescArray={"Rice","Oil"};
    
    Button btn_save;
    DBAdapterKenya dbengine=new DBAdapterKenya(FeedbackCompetitorActivity.this);
    public String StoreID;
    public String imei,date,pickerDate;
    int bck=0;
    ImageView img_back;
    String  flgOrderType;
    String  SN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_competitor);
        
        Intent intent=getIntent();
        StoreID=intent.getStringExtra("storeID");
        imei = intent.getStringExtra("imei");
        date = intent.getStringExtra("userdate");
        pickerDate= intent.getStringExtra("pickerDate");
        SN= intent.getStringExtra("SN");

        ll_parent=(LinearLayout) findViewById(R.id.ll_parent);
        btn_save= (Button) findViewById(R.id.btn_save);

        //putDataInHashmap();
        getDataFromDatabase();
        createViews();
        saveBtnWorking();
        backBtnWorking();
    }

   /* void putDataInHashmap()
    {
        for(int i=0;i<prdctIDArray.length;i++)
        {
            hmapPrdctIdAndName.put(prdctIDArray[i],prdctNameArray[i]);
        }

        String[] prdCatORiceId={"13","83","98"};
        String[] prdCatOilId={"99","55"};

        hmapCatIdAndPrdctId.put("1",prdCatORiceId);
        hmapCatIdAndPrdctId.put("2",prdCatOilId);

        hmapCatIdAndDesc.put("1","Rice");
        hmapCatIdAndDesc.put("2","Oil");
    }*/

    void getDataFromDatabase()
    {
        ArrayList<LinkedHashMap> list_fns=dbengine.getFeedbckCompMstrDetails();
        hmapCatIdAndPrdctId=list_fns.get(0); //catID and ProdID mapping
        hmapPrdctIdAndName=list_fns.get(1); //compID and compDesc
        hmapCatIdAndDesc=list_fns.get(2); //catID and catDesc

        hmapSavedCompetitrData=dbengine.fetchCompetitorData(StoreID);
    }

    void saveBtnWorking()
    {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alert=new AlertDialog.Builder(FeedbackCompetitorActivity.this);
                alert.setTitle("Alert");
                alert.setMessage("Do you want to save changes?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dbengine.deletetblFeedbackCompetitrData(StoreID);
                        dbengine.open();
                        if(hmapSavedCompetitrData != null && hmapSavedCompetitrData.size()>0)
                        {

                            for(Map.Entry<String,String> entry:hmapSavedCompetitrData.entrySet())
                            {
                                String prdctID=entry.getKey();
                                String catID=entry.getValue().split(Pattern.quote("^"))[0];
                                String prdctDesc=entry.getValue().split(Pattern.quote("^"))[1];

                                //key=compID, value=compName
                                if(prdctID.equals("13"))
                                {

                                }
                                else
                                {
                                    dbengine.savetblFeedbackCompetitr(StoreID,prdctID,prdctDesc,catID,"1");//1 here is Sstat
                                }

                                System.out.println("SAVING.."+StoreID+"--"+prdctID+"--"+prdctDesc+"--"+catID);
                            }

                        }

                        dbengine.savetblFeedbackCompetitr(StoreID,"13","Other Banded Rice","1","1");//1 here is Sstat
                        dbengine.close();
                        dialog.dismiss();
                        VideoPageOpenOrProductOrderPageOpen();


                           /* Intent intent = new Intent(FeedbackCompetitorActivity.this, LastVisitDetails.class);
                            intent.putExtra("storeID", StoreID);
                            intent.putExtra("imei", imei);
                            intent.putExtra("userdate", date);
                            intent.putExtra("pickerDate", pickerDate);
                            intent.putExtra("SN", SN);
                            startActivity(intent);
                            finish();*/


                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog=alert.create();
                dialog.show();
            }
        });
    }

    void createViews()
    {
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for(Map.Entry<String,String> entry:hmapCatIdAndDesc.entrySet())
        {
            View view_template = inflater1.inflate(R.layout.template_feedbckcmp, null);

            TextView txt_catName= (TextView) view_template.findViewById(R.id.txt_catName);
            txt_catName.setText(entry.getValue());
            
            final LinearLayout ll_CatTempltParent= (LinearLayout) view_template.findViewById(R.id.ll_CatTempltParent);
            ll_CatTempltParent.setTag(entry.getKey()+"_ll"); //tag- catID_ll

            ArrayList<String> list_prdctID=hmapCatIdAndPrdctId.get(entry.getKey());
            for(int i=0;i<list_prdctID.size();i++)
            {
                String prdctDesc=hmapPrdctIdAndName.get(list_prdctID.get(i));

                View view_row= inflater1.inflate(R.layout.custom_row, null);

                CheckBox cb_CompBox= (CheckBox) view_row.findViewById(R.id.cb_CompBox);
                cb_CompBox.setTag(entry.getKey()+"_"+list_prdctID.get(i)+"_cb");//tag- catID_prdctID_cb

                TextView txt_CompName= (TextView) view_row.findViewById(R.id.txt_CompName);
                txt_CompName.setTag(entry.getKey()+"_"+list_prdctID.get(i)+"_text"); //tag- catID_prdctID_text
                txt_CompName.setText(prdctDesc);

                if(hmapSavedCompetitrData != null && hmapSavedCompetitrData.size()>0)
                {
                    if(hmapSavedCompetitrData.containsKey(list_prdctID.get(i)))
                    {
                        cb_CompBox.setChecked(true);
                    }
                    else
                    {
                        cb_CompBox.setChecked(false);
                    }
                }

                cb_CompBox.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String catID=v.getTag().toString().trim().split(Pattern.quote("_"))[0];
                        String prdctID=v.getTag().toString().trim().split(Pattern.quote("_"))[1];

                        String prdctName="";

                        TextView compText= (TextView) ll_parent.findViewWithTag(catID+"_"+prdctID+"_text");
                        if(compText != null)
                        {
                            prdctName=compText.getText().toString().trim();
                        }

                        CheckBox cb= (CheckBox) v;
                        if(cb.isChecked())
                        {
                            hmapSavedCompetitrData.put(prdctID,catID+"^"+prdctName);
                        }
                        else
                        {
                            hmapSavedCompetitrData.remove(prdctID);
                        }
                    }
                });

                ll_CatTempltParent.addView(view_row);
            }

            ll_parent.addView(view_template);
        }
    }

    void backBtnWorking()
    {
        img_back= (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


  Intent intent=new Intent(FeedbackCompetitorActivity.this,ActualVisitStock.class);
            intent.putExtra("storeID", StoreID);
            intent.putExtra("imei", imei);
            intent.putExtra("userdate", date);
            intent.putExtra("pickerDate", pickerDate);
           intent.putExtra("SN", SN);


    startActivity(intent);
    finish();




            }
        });
    }

    public void VideoPageOpenOrProductOrderPageOpen(){
        dbengine.open();
        String VideoData=      dbengine.getVideoNameByStoreID(StoreID,"2");
        dbengine.close();
        int flagPlayVideoForStore=0;
        String Video_Name="0";
        String VIDEO_PATH="0";
        String VideoViewed="0";
        String Contentype="0";
        if(!VideoData.equals("0") && VideoData.contains("^")){
            Video_Name=   VideoData.toString().split(Pattern.quote("^"))[0];
            flagPlayVideoForStore=   Integer.parseInt( VideoData.toString().split(Pattern.quote("^"))[1]);
            VideoViewed=    VideoData.toString().split(Pattern.quote("^"))[2];
            Contentype=    VideoData.toString().split(Pattern.quote("^"))[3];
        }

                /*  VIDEO_PATH= "/sdcard/WhatsApp/Media/WhatsApp Video/VID-20180303-WA0030.mp4";
                VIDEO_PATH= "/sdcard/VideoLTFOODS/SampleVideo5mb.mp4";*/
        VIDEO_PATH=   Environment.getExternalStorageDirectory() + "/" + CommonInfo.VideoFolder + "/"+Video_Name;
        Uri intentUri;
        //if videoShown check
        if(flagPlayVideoForStore==1 && !(VIDEO_PATH.equals("0")) && VideoViewed.equals("0")&& Contentype.equals("2")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                File file = new File(VIDEO_PATH);
                intentUri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", file);
            }
            else{
                intentUri = Uri.parse(VIDEO_PATH);
            }


            if(intentUri!=null) {
                Intent intent = new Intent(FeedbackCompetitorActivity.this,VideoPlayerActivityForStore.class);
                intent.putExtra("FROM","FeedbackCompetitorActivity");
                intent.putExtra("STRINGPATH",VIDEO_PATH);
                intent.putExtra("storeID", StoreID);
                intent.putExtra("SN", SN);
                intent.putExtra("imei", imei);
                intent.putExtra("userdate", date);
                intent.putExtra("pickerDate", pickerDate);
                intent.putExtra("flgOrderType", 1);
                startActivity(intent);
                finish();
                // openVideoPlayerDialog(VIDEO_PATH);

            }
            else{
                Toast.makeText(FeedbackCompetitorActivity.this, "No video Found", Toast.LENGTH_LONG).show();
                passIntentToProductOrderFilter();
            }

        }
        else{

            passIntentToProductOrderFilter();
        }
    }
    public void passIntentToProductOrderFilter(){
        Intent nxtP4 = new Intent(FeedbackCompetitorActivity.this,ProductOrderFilterSearch.class);
        //Intent nxtP4 = new Intent(LastVisitDetails.this,ProductOrderFilterSearch_RecycleView.class);
        nxtP4.putExtra("storeID", StoreID);
        nxtP4.putExtra("SN", SN);
        nxtP4.putExtra("imei", imei);
        nxtP4.putExtra("userdate", date);
        nxtP4.putExtra("pickerDate", pickerDate);
        nxtP4.putExtra("flgOrderType", 1);
        startActivity(nxtP4);
        finish();
    }
}
