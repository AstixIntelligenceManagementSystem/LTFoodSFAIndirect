package project.astix.com.ltfoodsfaindirect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class StockCheckAndCmpttrAvilable extends AppCompatActivity {

    Button btn_Next;
    RadioGroup rg_compttr,rg_stock;
    public String storeID;
    public String imei;
    public String date;
    public String pickerDate;
        ImageView img_back_Btn;
    public String selStoreName;
    DBAdapterKenya dbengine=new DBAdapterKenya(StockCheckAndCmpttrAvilable.this);

    ArrayList<Integer> listStkCmpttr;


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
        setContentView(R.layout.activity_stock_check_and_cmpttr_avilable);

        btn_Next= (Button) findViewById(R.id.btn_Next);
        rg_compttr= (RadioGroup) findViewById(R.id.rg_compttr);
        rg_stock= (RadioGroup) findViewById(R.id.rg_stock);
        img_back_Btn=(ImageView) findViewById(R.id.img_back_Btn);
        getDataFromIntent();
        img_back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fireBackDetPg=new Intent(StockCheckAndCmpttrAvilable.this,LastVisitDetails.class);
                fireBackDetPg.putExtra("storeID", storeID);
                fireBackDetPg.putExtra("SN", selStoreName);
                fireBackDetPg.putExtra("bck", 1);
                fireBackDetPg.putExtra("imei", imei);
                fireBackDetPg.putExtra("userdate", date);
                fireBackDetPg.putExtra("pickerDate", pickerDate);
                fireBackDetPg.putExtra("flgOrderType", 1);
                //fireBackDetPg.putExtra("rID", routeID);
                startActivity(fireBackDetPg);
                finish();
            }
        });
        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateRadioGroups())
                {
                    int isStockAvlbl=0;
                    int isCmpttrAvlbl=0;
                    int selectedId = rg_stock.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    if(selectedId==R.id.rb_stckAvlbl)
                    {
                        isStockAvlbl=1;
                    }

                    int selectedCmpttr = rg_compttr.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    if(selectedCmpttr==R.id.rb_cmpttrAvlbl)
                    {
                        isCmpttrAvlbl=1;
                    }
                    dbengine.insertStoreStockCmpttrAvlbl(storeID,isStockAvlbl,isCmpttrAvlbl,1,1,1);

                    if(isStockAvlbl==1)
                    {
                        Intent nxtP4 = new Intent(StockCheckAndCmpttrAvilable.this,PicClkBfrStock.class);
                        nxtP4.putExtra("storeID", storeID);
                        nxtP4.putExtra("SN", selStoreName);
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
                        if(isCmpttrAvlbl==1)
                        {
                            Intent nxtP4 = new Intent(StockCheckAndCmpttrAvilable.this,FeedbackCompetitorActivity.class);
                            //Intent nxtP4 = new Intent(LastVisitDetails.this,ProductOrderFilterSearch_RecycleView.class);
                            nxtP4.putExtra("storeID", storeID);
                            nxtP4.putExtra("SN", selStoreName);
                            nxtP4.putExtra("imei", imei);
                            nxtP4.putExtra("userdate", date);
                            nxtP4.putExtra("pickerDate", pickerDate);
                            nxtP4.putExtra("flgOrderType", 1);
                            nxtP4.putExtra("isStockAvlbl", isStockAvlbl);
                            nxtP4.putExtra("isCmpttrAvlbl", isCmpttrAvlbl);
                            startActivity(nxtP4);
                            finish();
                        }
                        else {
                            Intent nxtP4 = new Intent(StockCheckAndCmpttrAvilable.this,ProductOrderFilterSearch.class);
                            //Intent nxtP4 = new Intent(LastVisitDetails.this,ProductOrderFilterSearch_RecycleView.class);
                            nxtP4.putExtra("storeID", storeID);
                            nxtP4.putExtra("SN", selStoreName);
                            nxtP4.putExtra("imei", imei);
                            nxtP4.putExtra("userdate", date);
                            nxtP4.putExtra("pickerDate", pickerDate);
                            nxtP4.putExtra("flgOrderType", 1);
                            startActivity(nxtP4);
                            finish();
                        }
                    }

                }

            }
        });
    }

    public boolean validateRadioGroups()
    {
        boolean validate=false;
        if(rg_stock.getCheckedRadioButtonId()==-1)
        {
            validate=false;
            showAlertForEveryOne("Please Choose Stock Avilable or Vot Avilable to proceed");
        }
        else if(rg_compttr.getCheckedRadioButtonId()==-1)
        {
            validate=false;
            showAlertForEveryOne("Please Choose Competitor Avilable or Vot Avilable to proceed");
        }
        else
        {
            validate= true;
        }
        return validate;
    }

    public void showAlertForEveryOne(String msg)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(StockCheckAndCmpttrAvilable.this);
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

    void getDataFromIntent()
    {
        Intent passedvals = getIntent();
        storeID = passedvals.getStringExtra("storeID");
        imei = passedvals.getStringExtra("imei");
        date = passedvals.getStringExtra("userDate");
        pickerDate= passedvals.getStringExtra("pickerDate");
        selStoreName = passedvals.getStringExtra("selStoreName");


        listStkCmpttr=dbengine.getLTfoodStockCmpttr(storeID);
        if((listStkCmpttr!=null) && (listStkCmpttr.size()>0))
        {
            int isStockAvlbl=listStkCmpttr.get(0);
            if(isStockAvlbl==1)
            {
                rg_stock.check(R.id.rb_stckAvlbl);
            }
            else if(isStockAvlbl==0)
            {
                rg_stock.check(R.id.rb_stckNotAvlbl);
            }

            int isCmpttrAvlbl=listStkCmpttr.get(1);
            if(isCmpttrAvlbl==1)
            {
                rg_compttr.check(R.id.rb_cmpttrAvlbl);
            }
            else if(isCmpttrAvlbl==0)
            {
                rg_compttr.check(R.id.rb_cmpttrNotAvlbl);
            }


        }
    }
}
