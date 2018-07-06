package project.astix.com.ltfoodsfaindirect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FeedbackCompetitorActivity extends BaseActivity implements CheckedUnchekedCmpttrProduct
{
    LinearLayout ll_parent;

    LinkedHashMap<String,ArrayList<String>> hmapCatIdAndCmpttrDtl=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String,ArrayList<String>> hmapCmpttrIddAndPrdctDtl=new LinkedHashMap<String,ArrayList<String>>();
    public ProgressDialog pDialog2STANDBYabhi;
    Thread myThread;
    boolean isRetailerAllowedToDo=true;
    LinkedHashMap<String,String> hmapSavedCompetitrData=new LinkedHashMap<>();
    LinkedHashMap<String,String> hmapPrdctImgPath;
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
    int isStockAvlbl=0;
    int isCmpttrAvlbl=0;
    CheckBox chkBox_Rtailer;

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
        isStockAvlbl=intent.getIntExtra("isStockAvlbl",0);
        isCmpttrAvlbl=intent.getIntExtra("isCmpttrAvlbl",0);
        ll_parent=(LinearLayout) findViewById(R.id.ll_parent);
        btn_save= (Button) findViewById(R.id.btn_save);
        chkBox_Rtailer= (CheckBox) findViewById(R.id.chkBox_Rtailer);
        chkBox_Rtailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(chkBox_Rtailer.isChecked())
               {
                   isRetailerAllowedToDo=false;
                   hmapSavedCompetitrData.clear();
                   disableAllCheckBox("Disabling Product");

               }
               else
               {
                   isRetailerAllowedToDo=true;
                   disableAllCheckBox("Enabling Product");
               }
            }
        });
        //putDataInHashmap();


        saveBtnWorking();
        backBtnWorking();

        getDataFromDatabase();
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
        ArrayList<LinkedHashMap<String,ArrayList<String>>> list_fns=dbengine.getFeedbckCompMstrDetails();
        hmapCatIdAndCmpttrDtl=list_fns.get(0); //ctgryid^ctgryDesc cmpttrId^cmpttrDesc
        hmapCmpttrIddAndPrdctDtl=list_fns.get(1); //cmpttrId^cmpttrDesc prdctId^prdctDesc

        hmapSavedCompetitrData=dbengine.fetchCompetitorData(StoreID);
        hmapPrdctImgPath=dbengine.getPrdctImgPath();
        if(dbengine.getCmpttrRetailerAllowed(StoreID)==0)
        {
            chkBox_Rtailer.setChecked(true);
            isRetailerAllowedToDo=false;

            disableAllCheckBox("Disabling Product");

        }
        else
        {
            if((hmapSavedCompetitrData!=null) && (hmapSavedCompetitrData.size()>0))
            {
                chkBox_Rtailer.setEnabled(false);
            }
            disableAllCheckBox("Loading Product");
        }
    }

    void saveBtnWorking()
    {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isRetailerAllowedToDo)
                {
                    dbengine.updateCmpttrRetailerAllowed(StoreID,1);
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

                                    String prdctDesc=entry.getKey().split(Pattern.quote("~"))[0];
                                    String compttrDesc=entry.getKey().split(Pattern.quote("~"))[1];
                                    String catId=entry.getKey().split(Pattern.quote("~"))[2];
                                    String prdctID=prdctDesc.split(Pattern.quote("^"))[0];
                                    String prdctName=prdctDesc.split(Pattern.quote("^"))[1];
                                    String compttrId=compttrDesc.split(Pattern.quote("^"))[0];
                                    String compttrName=compttrDesc.split(Pattern.quote("^"))[1];
                                    int flgAvilable=Integer.parseInt(entry.getValue());
                                    dbengine.savetblFeedbackCompetitr(StoreID,compttrId,compttrName,catId,prdctID,prdctName,flgAvilable,"1");//1 here is Sstat

                                }

                            }
                            dbengine.close();
                            dialog.dismiss();
                            // VideoPageOpenOrProductOrderPageOpen();

                            intentToPicSctn2();



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
                else
                {
                    dbengine.updateCmpttrRetailerAllowed(StoreID,0);
                    intentToPicSctn2();
                }

            }
        });
    }

    public void intentToPicSctn2()
    {
        if((isStockAvlbl==1) && (dbengine.getStockRetailerAllowed(StoreID)==1))
        {
            Intent nxtP4 = new Intent(FeedbackCompetitorActivity.this,PicClkdAfterStock.class);
            nxtP4.putExtra("storeID",StoreID );
            nxtP4.putExtra("SN", SN);
            nxtP4.putExtra("imei", imei);
            nxtP4.putExtra("userdate", date);
            nxtP4.putExtra("pickerDate", pickerDate);
            nxtP4.putExtra("flgOrderType", 1);
            nxtP4.putExtra("isStockAvlbl", isStockAvlbl);
            nxtP4.putExtra("isCmpttrAvlbl", isCmpttrAvlbl);

            startActivity(nxtP4);
            finish();
        }

        else
        {
            VideoPageOpenOrProductOrderPageOpen();
        }

    }

    void createViews(boolean isRetailerAllowed)
    {
        ll_parent.removeAllViews();
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

                final CheckBox cb_CompBox= (CheckBox) view_row.findViewById(R.id.cb_CompBox);
                cb_CompBox.setTag(cmpptr+"~"+catId);//tag- catID_prdctID_cb

                TextView txt_CompName= (TextView) view_row.findViewById(R.id.txt_CompName);
               //tag- catID_prdctID_text
                txt_CompName.setText(cmpptr.split(Pattern.quote("^"))[1]);

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
                                ImageProductAdapter  adapterImage = new ImageProductAdapter(this,listPrdct,hmapPrdctImgPath,cmpptr+"~"+catId,hmapSavedCompetitrData,isRetailerAllowed);

                                expandableHeightGridView.setAdapter(adapterImage);

                                ll_prdctDetail.addView(view_PrdctGrid);
                            }
                            else
                            {

                                cb_CompBox.setVisibility(View.VISIBLE);
                                if(isRetailerAllowed)
                                {
                                    cb_CompBox.setEnabled(true);
                                }
                                else
                                {
                                    cb_CompBox.setChecked(false);
                                    cb_CompBox.setEnabled(false);
                                }
                                if(hmapSavedCompetitrData.containsKey("0^NA~"+cb_CompBox.getTag().toString()))
                                {
                                    cb_CompBox.setChecked(true);
                                }
                                cb_CompBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CheckBox checkBox=(CheckBox)v;
                                        String tag="0^NA~"+checkBox.getTag().toString();
                                        if(cb_CompBox.isChecked())
                                        {
                                             chkBox_Rtailer.setEnabled(false);

                                            hmapSavedCompetitrData.put(tag,"1");
                                        }
                                        else
                                        {
                                            if(hmapSavedCompetitrData.containsKey(tag))
                                            {
                                                hmapSavedCompetitrData.remove(tag);
                                                if(hmapSavedCompetitrData.size()<1)
                                                {
                                                    chkBox_Rtailer.setEnabled(true);
                                                }
                                            }
                                        }
                                    }
                                });
                            }


                    }
                }


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

        if((isStockAvlbl==1) && (dbengine.getStockRetailerAllowed(StoreID)==1))
        {
            Intent intent=new Intent(FeedbackCompetitorActivity.this,ActualVisitStock.class);
            intent.putExtra("storeID", StoreID);
            intent.putExtra("imei", imei);
            intent.putExtra("userdate", date);
            intent.putExtra("pickerDate", pickerDate);
            intent.putExtra("SN", SN);
            intent.putExtra("isStockAvlbl", isStockAvlbl);
            intent.putExtra("isCmpttrAvlbl", isCmpttrAvlbl);

            startActivity(intent);
            finish();
        }
        else if((isStockAvlbl==1) && (dbengine.getStockRetailerAllowed(StoreID)==0))
        {
            Intent nxtP4 = new Intent(FeedbackCompetitorActivity.this,PicClkBfrStock.class);
            nxtP4.putExtra("storeID", StoreID);
            nxtP4.putExtra("SN", SN);
            nxtP4.putExtra("imei", imei);
            nxtP4.putExtra("userdate", date);
            nxtP4.putExtra("pickerDate", pickerDate);
            nxtP4.putExtra("flgOrderType", 1);
            nxtP4.putExtra("isStockAvlbl", isStockAvlbl);
            nxtP4.putExtra("isCmpttrAvlbl", isCmpttrAvlbl);
            startActivity(nxtP4);
            finish();
        }

        else
        {
            Intent ready4GetLoc = new Intent(FeedbackCompetitorActivity.this,StockCheckAndCmpttrAvilable.class);
            //enableGPSifNot();

            ready4GetLoc.putExtra("storeID", StoreID);
            ready4GetLoc.putExtra("selStoreName", SN);
            ready4GetLoc.putExtra("imei", imei);
            ready4GetLoc.putExtra("userDate", date);
            ready4GetLoc.putExtra("pickerDate", pickerDate);


            startActivity(ready4GetLoc);
            finish();

        }





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


    @Override
    public void checkedUncheckedPrdct(boolean isChecked,String tagChkdPrdct) {
        if(isChecked)
        {
            chkBox_Rtailer.setEnabled(false);
            hmapSavedCompetitrData.put(tagChkdPrdct,"1");
        }
        else
        {
            if( hmapSavedCompetitrData.containsKey(tagChkdPrdct))
            {
                hmapSavedCompetitrData.remove(tagChkdPrdct);
                if(hmapSavedCompetitrData.size()<1)
                {
                    chkBox_Rtailer.setEnabled(true);
                }
            }
        }

    }


    public void disableAllCheckBox(String message)
    {

            pDialog2STANDBYabhi=ProgressDialog.show(FeedbackCompetitorActivity.this,getText(R.string.genTermPleaseWaitNew) ,message, false,true);
            myThread = new Thread(myRunnable);
            myThread.setPriority(Thread.MAX_PRIORITY);
            myThread.start();


    }
    Runnable myRunnable = new Runnable(){

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {


                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            if(pDialog2STANDBYabhi != null && pDialog2STANDBYabhi.isShowing())
                            {
                                pDialog2STANDBYabhi.setCancelable(false);

                            }
                        }

                        public void onFinish() {

                            new IAmABackgroundTask().execute();

                        }
                    }.start();







                }

            });

        }
    };

    class IAmABackgroundTask extends
            AsyncTask<String, Integer, Boolean> {


        @SuppressWarnings("static-access")
        @Override
        protected void onPreExecute() {
            createViews(isRetailerAllowedToDo);

        }

        @Override
        protected void onPostExecute(Boolean result) {


            fnAbhinav(1000);
        }

        @Override
        protected Boolean doInBackground(String... params) {


            return true;

        }

    }

    public void countUp(int start)
    {

        if(pDialog2STANDBYabhi != null && pDialog2STANDBYabhi.isShowing())
        {

            pDialog2STANDBYabhi.setTitle("My Name Abhinav");
            pDialog2STANDBYabhi.setCancelable(true);
            pDialog2STANDBYabhi.cancel();
            pDialog2STANDBYabhi.dismiss();

        }

        else
        {
            countUp(start + 1);
        }
    }


    public void fnAbhinav(int mytimeval)
    {
        countUp(1);
    }

}
