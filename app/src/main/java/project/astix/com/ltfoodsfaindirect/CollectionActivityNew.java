package project.astix.com.ltfoodsfaindirect;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import project.astix.com.ltfoodsfaindirect.BaseActivity;
import project.astix.com.ltfoodsfaindirect.DBAdapterKenya;

public class CollectionActivityNew extends BaseActivity implements DatePickerDialog.OnDateSetListener
{


    public String strGlobalInvoiceNumber="NA";
    public String TmpInvoiceCodePDA="NA";
    public int chkflgInvoiceAlreadyGenerated=0;


    TextView totaltextview,dateTextViewFirst,dateTextViewSecond,dateTextViewThird,pymtModeTextView,AmountTextview,chequeNoTextview,DateLabelTextview,BankLabelTextview;

    TextView BankSpinnerSecond,BankSpinnerThird;
    Double OverAllAmountCollected=0.0;
    TextView paymentModeSpinnerFirst,paymentModeSpinnerSecond,paymentModeSpinnerThird;
    AlertDialog.Builder alertDialog;
    AlertDialog ad;
    View convertView;
    ListView listviewPaymentModeFirst,listviewPaymentModeSecond,listviewBankFirst,listviewBankSecond,listviewBankThird;
    ArrayAdapter<String> adapterPaymentModeFirst,adapterPaymentModeSecond,adapterBankFirst,adapterBankSecond,adapterBankThird;
    String[] pymtModeFirstList,pymtModeSecondList,bankfirstList,bankSecondList;
    String[] pymtModeThirdList,bankThirdList;
    LinkedHashMap<String, String> hashmapPymtMdFirst,hashmapPymtMdSecond,hashmapBank,linkedHmapBankID,linkedHmapPaymentModeID;
    EditText inputSearch;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    int Year, Month, Day;
    boolean calFirst=false;
    boolean calSecond=false;
    boolean calThird=false;
    EditText amountEdittextFirst,amountEdittextSecond,amountEdittextThird,checqueNoEdittextSecond,checqueNoEdittextThird;


    Button Done_btn;
    String storeIDGlobal;
    String flagNew_orallDataFromDatabase,PaymentModeUpdate,PaymentModeSecondUpdate,AmountUpdate,ChequeNoUpdte,DateUpdate;
    String BankNameUpdate,BankNameSecondUpdate="0",BankNameThirdUpdate="0";
    public String storeID;
    public String imei;

    public String date;
    public String pickerDate;
    public String SN;
    public String strGlobalOrderID="0";
    int flgOrderType=0;
    TextView tv_outstandingvalue,tv_MinCollectionvalue,tv_cntInvoceValue,tv_totOutstandingValue;

    HashMap<String,Integer> hmapDistPrdctStockCount =new HashMap<String,Integer>();
    HashMap<String,String> hmapPrdctIdOutofStock=new HashMap<String,String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_activity);
        hashmapBank = dbengine.fnGettblBankMaster();

        getDataFromIntent();

        TextviewInitialization();

        EdittextInitialization();

        DropDownInitialization();

        CalendarInitialization();

        ButtonInialization();




        tv_outstandingvalue=(TextView) findViewById(R.id.tv_outstandingvalue);
        Double outstandingvalue=dbengine.fnGetStoretblLastOutstanding(storeID);
        outstandingvalue=Double.parseDouble(new DecimalFormat("##.##").format(outstandingvalue));
        //String.format("%.2f", outstandingvalue)
        tv_outstandingvalue.setText(""+String.format("%.2f", outstandingvalue));

        tv_MinCollectionvalue=(TextView) findViewById(R.id.tv_MinCollectionvalue);
        Double MinCollectionvalue=dbengine.fnGetStoretblLastOutstanding(storeID);
        MinCollectionvalue=Double.parseDouble(new DecimalFormat("##.##").format(MinCollectionvalue));
        tv_MinCollectionvalue.setText(""+String.format("%.2f", MinCollectionvalue));

        tv_cntInvoceValue=(TextView) findViewById(R.id.tv_cntInvoceValue);
        Double cntInvoceValue=dbengine.fetch_Store_InvValAmount(storeID,strGlobalOrderID);
        cntInvoceValue=Double.parseDouble(new DecimalFormat("##.##").format(cntInvoceValue));
        tv_cntInvoceValue.setText(""+String.format("%.2f", cntInvoceValue));

        tv_totOutstandingValue=(TextView) findViewById(R.id.tv_totOutstandingValue);
        Double totOutstandingValue=outstandingvalue+cntInvoceValue;
        totOutstandingValue=Double.parseDouble(new DecimalFormat("##.##").format(totOutstandingValue));
        tv_totOutstandingValue.setText(""+String.format("%.2f", totOutstandingValue));

        linkedHmapPaymentModeID=	dbengine.fnGetPaymentMode();
        linkedHmapBankID=	dbengine.fnGetBankIdData();
        fetchData();
    }

    public void fetchData()
    {
        try
        {
            String  DataFromDatabase=	dbengine.fnRetrieveCollectionDataBasedOnStoreID(storeIDGlobal,strGlobalOrderID);
            if(!DataFromDatabase.equals("0"))
            {
                if(DataFromDatabase.contains("$")){
                    String[] collectionData=DataFromDatabase.split(Pattern.quote("$"));
                    for(int i=0;i<=collectionData.length;i++){
                        String collectionDataString=  collectionData[i];
                        SetDataToLayout(collectionDataString);
                    }
                }
                else{
                    SetDataToLayout(DataFromDatabase);
                }

               /* if(!DataFromDatabase.split(Pattern.quote("^"))[0].equals("0"))
                {
                    amountEdittextFirst.setText(DataFromDatabase.split(Pattern.quote("^"))[0]);
                }
                else
                {
                    amountEdittextFirst.setText("");
                }
                if(!DataFromDatabase.split(Pattern.quote("^"))[1].equals("0")) {
                    amountEdittextSecond.setText(DataFromDatabase.split(Pattern.quote("^"))[1]);
                }
                else
                {
                    amountEdittextSecond.setText("");
                }

                if(!DataFromDatabase.split(Pattern.quote("^"))[2].equals("0")) {
                    checqueNoEdittextSecond.setText(DataFromDatabase.split(Pattern.quote("^"))[2]);
                }
                else
                {
                    checqueNoEdittextSecond.setText("");
                }
                dateTextViewSecond.setText(DataFromDatabase.split(Pattern.quote("^"))[3]);
                BankNameUpdate = DataFromDatabase.split(Pattern.quote("^"))[4];
                if (BankNameUpdate.equals("0"))
                {
                    BankSpinnerSecond.setText(getResources().getString(R.string.txtSelect));
                } else
                {
                    BankSpinnerSecond.setText(linkedHmapBankID.get(BankNameUpdate));

                }

                if(!DataFromDatabase.split(Pattern.quote("^"))[5].equals("0")) {
                    amountEdittextThird.setText(DataFromDatabase.split(Pattern.quote("^"))[5]);
                }
                else
                {
                    amountEdittextThird.setText("");
                }

                if(!DataFromDatabase.split(Pattern.quote("^"))[6].equals("0")) {
                    checqueNoEdittextThird.setText(DataFromDatabase.split(Pattern.quote("^"))[6]);
                }
                else
                {
                    checqueNoEdittextThird.setText("");
                }
                dateTextViewThird.setText(DataFromDatabase.split(Pattern.quote("^"))[7]);
                BankNameUpdate = DataFromDatabase.split(Pattern.quote("^"))[8];
                if (BankNameUpdate.equals("0")) {
                    BankSpinnerThird.setText(getResources().getString(R.string.txtSelect));
                } else {
                    BankSpinnerThird.setText(linkedHmapBankID.get(BankNameUpdate));

                }*/

            }


        }
        catch(Exception e)
        {

        }
    }

    private void getDataFromIntent()
    {


        Intent passedvals = getIntent();

        storeID = passedvals.getStringExtra("storeID");
        imei = passedvals.getStringExtra("imei");
        date = passedvals.getStringExtra("userdate");
        pickerDate = passedvals.getStringExtra("pickerDate");
        SN = passedvals.getStringExtra("SN");
        flgOrderType = passedvals.getIntExtra("flgOrderType",0);
        strGlobalOrderID=passedvals.getStringExtra("OrderPDAID");

        storeIDGlobal=storeID;

    }
    public void EdittextInitialization()
    {

        amountEdittextFirst=(EditText) findViewById(R.id.amountEdittextFirst);
        amountEdittextSecond=(EditText) findViewById(R.id.amountEdittextSecond);
        amountEdittextThird=(EditText) findViewById(R.id.amountEdittextThird);

        checqueNoEdittextSecond=(EditText) findViewById(R.id.checqueNoEdittextSecond);
        checqueNoEdittextThird=(EditText) findViewById(R.id.checqueNoEdittextThird);
        amountEdittextFirst.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                double amntFirst=0.00;
                double amntSecond=0.00;
                double amntThird=0.00;
                NumberFormat nf = NumberFormat.getInstance();
                if(!amountEdittextFirst.getText().toString().trim().equals(""))
                {
                    // amntFirst= Double.parseDouble(amountEdittextFirst.getText().toString().trim());
                    try
                    {
                        amntFirst = nf.parse(amountEdittextFirst.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }

                }
                if(!amountEdittextSecond.getText().toString().trim().equals(""))
                {
                    //amntSecond= Double.parseDouble(amountEdittextSecond.getText().toString().trim());
                    try
                    {
                        amntSecond = nf.parse(amountEdittextSecond.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }
                if(!amountEdittextThird.getText().toString().trim().equals(""))
                {
                    //amntThird= Double.parseDouble(amountEdittextThird.getText().toString().trim());
                    try
                    {
                        amntThird = nf.parse(amountEdittextThird.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }


                //String Total=String.valueOf(amntFirst+amntSecond);
                totaltextview.setText(String.format("%.2f", amntFirst+amntSecond+amntThird));
                OverAllAmountCollected= amntFirst+amntSecond+amntThird;

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        amountEdittextSecond.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                double amntFirst=0.00;
                double amntSecond=0.00;
                double amntThird=0.00;
                NumberFormat nf = NumberFormat.getInstance();
                if(!amountEdittextSecond.getText().toString().trim().equals(""))
                {
                    // amntSecond= Double.parseDouble(amountEdittextSecond.getText().toString().trim());
                    try
                    {
                        amntSecond = nf.parse(amountEdittextSecond.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }
                if(!amountEdittextFirst.getText().toString().trim().equals(""))
                {
                    // amntFirst= Double.parseDouble(amountEdittextFirst.getText().toString().trim());
                    try
                    {
                        amntFirst = nf.parse(amountEdittextFirst.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }

                }

                if(!amountEdittextThird.getText().toString().trim().equals(""))
                {
                    // amntThird= Double.parseDouble(amountEdittextThird.getText().toString().trim());
                    try
                    {
                        amntThird = nf.parse(amountEdittextThird.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }


                //String Total=String.valueOf(amntFirst+amntSecond);
                totaltextview.setText(String.format("%.2f", amntFirst+amntSecond+amntThird));
                OverAllAmountCollected= amntFirst+amntSecond+amntThird;
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });


        amountEdittextThird.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                double amntFirst=0.00;
                double amntSecond=0.00;
                double amntThird=0.00;
                NumberFormat nf = NumberFormat.getInstance();
                if(!amountEdittextSecond.getText().toString().trim().equals(""))
                {
                    //amntSecond= Double.parseDouble(amountEdittextSecond.getText().toString().trim());
                    try
                    {
                        amntSecond = nf.parse(amountEdittextSecond.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }
                if(!amountEdittextFirst.getText().toString().trim().equals(""))
                {
                    //amntFirst= Double.parseDouble(amountEdittextFirst.getText().toString().trim());
                    try
                    {
                        amntFirst = nf.parse(amountEdittextFirst.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }

                }

                if(!amountEdittextThird.getText().toString().trim().equals(""))
                {
                    //amntThird= Double.parseDouble(amountEdittextThird.getText().toString().trim());
                    try
                    {
                        amntThird = nf.parse(amountEdittextThird.getText().toString().trim()).doubleValue();
                    }
                    catch(ParseException e)
                    {
                        System.out.print("i m in errro");
                    }
                }


                //String Total=String.valueOf(amntFirst+amntSecond);
                totaltextview.setText(String.format("%.2f", amntFirst+amntSecond+amntThird));
                OverAllAmountCollected= amntFirst+amntSecond+amntThird;
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

    }




    public void DropDownInitialization()
    {
        paymentModeSpinnerFirst=	(TextView) findViewById(R.id.paymentModeSpinnerFirst);
        paymentModeSpinnerSecond=	(TextView) findViewById(R.id.paymentModeSpinnerSecond);
        paymentModeSpinnerThird=	(TextView) findViewById(R.id.paymentModeSpinnerThird);

        BankSpinnerSecond=	(TextView) findViewById(R.id.BankSpinnerSecond);
        BankSpinnerThird=	(TextView) findViewById(R.id.BankSpinnerThird);



        paymentModeSpinnerFirst.setText("Cash");
        amountEdittextFirst.setEnabled(true);

        paymentModeSpinnerSecond.setText("Cheque/DD");
        amountEdittextSecond.setEnabled(true);

        // paymentModeSpinnerThird.setText("Electronic");
        paymentModeSpinnerThird.setText("Mobile");
        amountEdittextThird.setEnabled(true);


        if(paymentModeSpinnerFirst.equals("Cash"))
        {
            amountEdittextFirst.setEnabled(true);
            dateTextViewFirst.setBackgroundResource(R.drawable.outside_boundry_gray);
            BankNameUpdate="0";
            dateTextViewFirst.setEnabled(false);
            dateTextViewFirst.setText("");
        }

        if(paymentModeSpinnerSecond.equals("Cheque/DD"))
        {
            dateTextViewSecond.setBackgroundResource(R.drawable.outside_boundry);
            checqueNoEdittextSecond.setBackgroundResource(R.drawable.edittex_with_white_background);
            BankSpinnerSecond.setBackgroundResource(R.drawable.spinner_background_with_rectangle);
            amountEdittextSecond.setEnabled(true);
            checqueNoEdittextSecond.setEnabled(true);
            dateTextViewSecond.setEnabled(true);
            BankSpinnerSecond.setEnabled(true);
        }

        /*if(paymentModeSpinnerThird.equals("Electronic"))
        {
            paymentModeSpinnerThird.setText("Electronic");
            dateTextViewThird.setBackgroundResource(R.drawable.outside_boundry);
            checqueNoEdittextThird.setBackgroundResource(R.drawable.edittex_with_white_background);
            BankSpinnerThird.setBackgroundResource(R.drawable.spinner_background_with_rectangle);
           amountEdittextThird.setEnabled(true);
            checqueNoEdittextThird.setEnabled(true);
            dateTextViewThird.setEnabled(true);
            BankSpinnerThird.setEnabled(true);
        }*/

        if(paymentModeSpinnerThird.equals("Mobile"))
        {
            paymentModeSpinnerThird.setText("Mobile");
            dateTextViewThird.setBackgroundResource(R.drawable.outside_boundry_gray);
            checqueNoEdittextThird.setBackgroundResource(R.drawable.edittext_with_gray_background);//.edittex_with_white_background);
            BankSpinnerThird.setBackgroundResource(R.drawable.spinner_background_with_rectangle);
            amountEdittextThird.setEnabled(true);
            checqueNoEdittextThird.setEnabled(false);
            dateTextViewThird.setEnabled(false);
            BankSpinnerThird.setEnabled(false);
            dateTextViewThird.setText("");



        }







        BankSpinnerSecond.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                if (!amountEdittextSecond.getText().toString().trim().equals("") && !checqueNoEdittextSecond.getText().toString().trim().equals("") && !dateTextViewSecond.getText().toString().trim().equals("21-mar-16"))
                {
                    BankLabelTextview.setError(null);
                    BankLabelTextview.clearFocus();


                    alertDialog = new AlertDialog.Builder(CollectionActivityNew.this);
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = (View) inflater.inflate(R.layout.activity_list,
                            null);

                    listviewBankSecond = (ListView) convertView.findViewById(R.id.list_view);

                    int index = 0;
                    if (hashmapBank != null) {
                        bankSecondList = new String[hashmapBank.size() + 1];
                        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(
                                hashmapBank);
                        Set set2 = map.entrySet();
                        Iterator iterator = set2.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry me2 = (Map.Entry) iterator.next();
                            if (index == 0) {
                                bankSecondList[index] = "Select";

                                index = index + 1;

                                bankSecondList[index] = me2.getKey().toString().trim();

                                index = index + 1;
                            } else {
                                bankSecondList[index] = me2.getKey().toString().trim();

                                index = index + 1;
                            }

                        }

                    }
                    adapterBankSecond = new ArrayAdapter<String>(CollectionActivityNew.this,
                            R.layout.list_item, R.id.product_name, bankSecondList);

                    listviewBankSecond.setAdapter(adapterBankSecond);
                    inputSearch = (EditText) convertView
                            .findViewById(R.id.inputSearch);
                    inputSearch.setVisibility(View.VISIBLE);
                    alertDialog.setView(convertView);
                    alertDialog.setTitle("Bank");
                    inputSearch.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                            // TODO Auto-generated method stub
                            adapterBankSecond.getFilter().filter(arg0.toString().trim());

                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1,
                                                      int arg2, int arg3) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                    listviewBankSecond.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            String abc = listviewBankSecond.getItemAtPosition(arg2)
                                    .toString().trim();
                            BankSpinnerSecond.setText(abc);
                            inputSearch.setText("");

                            ad.dismiss();

                            if (abc.equals("Select")) {


                            } else {
                            }
                        }
                    });
                    ad = alertDialog.show();


                }
                else
                {
                    if(amountEdittextSecond.getText().toString().trim().equals(""))
                    {
                        // allMessageAlert("Please Enter the Amount.");
                        showAlertSingleButtonError("Please Enter the Amount.");
                    }
                    else if(checqueNoEdittextSecond.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter ChequeNo/RefNo.");
                    }
                    else if(dateTextViewSecond.getText().toString().trim().equals("21-mar-16"))
                    {
                        showAlertSingleButtonError("Please Select the Date.");
                    }
                    else
                    {
                        showAlertSingleButtonError("Please Enter the Amount or ChequeNo/RefNo. or Select Date");
                    }


                }
            }

        });



        BankSpinnerThird.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if (!amountEdittextThird.getText().toString().trim().equals("") && !checqueNoEdittextThird.getText().toString().trim().equals("") && !dateTextViewThird.getText().toString().trim().equals("21-mar-16"))
                {
                    BankLabelTextview.setError(null);
                    BankLabelTextview.clearFocus();


                    alertDialog = new AlertDialog.Builder(CollectionActivityNew.this);
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = (View) inflater.inflate(R.layout.activity_list,
                            null);

                    listviewBankThird = (ListView) convertView.findViewById(R.id.list_view);

                    int index = 0;
                    if (hashmapBank != null) {
                        bankThirdList = new String[hashmapBank.size() + 1];
                        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(
                                hashmapBank);
                        Set set2 = map.entrySet();
                        Iterator iterator = set2.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry me2 = (Map.Entry) iterator.next();
                            if (index == 0) {
                                bankThirdList[index] = "Select";

                                index = index + 1;

                                bankThirdList[index] = me2.getKey().toString().trim();

                                index = index + 1;
                            } else {
                                bankThirdList[index] = me2.getKey().toString().trim();

                                index = index + 1;
                            }

                        }

                    }
                    adapterBankThird = new ArrayAdapter<String>(CollectionActivityNew.this,
                            R.layout.list_item, R.id.product_name, bankThirdList);

                    listviewBankThird.setAdapter(adapterBankThird);
                    inputSearch = (EditText) convertView
                            .findViewById(R.id.inputSearch);
                    inputSearch.setVisibility(View.VISIBLE);
                    alertDialog.setView(convertView);
                    alertDialog.setTitle("Bank");
                    inputSearch.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                            // TODO Auto-generated method stub
                            adapterBankThird.getFilter().filter(arg0.toString().trim());

                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1,
                                                      int arg2, int arg3) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                    listviewBankThird.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            String abc = listviewBankThird.getItemAtPosition(arg2).toString().trim();
                            BankSpinnerThird.setText(abc);
                            inputSearch.setText("");

                            ad.dismiss();

                            if (abc.equals("Select")) {


                            } else {
                            }
                        }
                    });
                    ad = alertDialog.show();

                }
                else
                {
                    if(amountEdittextThird.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter the Amount.");
                    }
                    else if(checqueNoEdittextThird.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter ChequeNo/RefNo.");
                    }
                    else if(dateTextViewThird.getText().toString().trim().equals("21-mar-16"))
                    {
                        showAlertSingleButtonError("Please Select the Date.");
                    }
                    else
                    {
                        showAlertSingleButtonError("Please Enter the Amount or ChequeNo/RefNo. or Select Date");
                    }

                }


            }
        });

    }
    public void CalendarInitialization()
    {

        dateTextViewSecond=(TextView) findViewById(R.id.dateTextViewSecond);
        dateTextViewSecond.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if (!amountEdittextSecond.getText().toString().trim().equals("") && !checqueNoEdittextSecond.getText().toString().trim().equals("")) {
                    DateLabelTextview.setError(null);
                    DateLabelTextview.clearFocus();

                    calSecond = true;
                    calendar = Calendar.getInstance();

                    Year = calendar.get(Calendar.YEAR);
                    Month = calendar.get(Calendar.MONTH);
                    Day = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = DatePickerDialog.newInstance(
                            CollectionActivityNew.this, Year, Month, Day);

                    datePickerDialog.setThemeDark(false);

                    datePickerDialog.showYearPickerFirst(false);

                    Calendar calendarForSetDate = Calendar.getInstance();
                    calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

                    // calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
                    // YOU can set min or max date using this code
                    // datePickerDialog.setMaxDate(Calendar.getInstance());
                    // datePickerDialog.setMinDate(calendar);

                    datePickerDialog.setAccentColor(Color.parseColor("#5c5696"));

                    datePickerDialog.setTitle("SELECT DATE");
                    datePickerDialog.setMinDate(calendarForSetDate);
                    /*
                     * Calendar calendar = Calendar.getInstance();
                     * calendar.setTimeInMillis
                     * (System.currentTimeMillis()+24*60*60*1000);
                     */
                    // YOU can set min or max date using this code
                    // datePickerDialog.setMaxDate(Calendar.getInstance());
                    // datePickerDialog.setMinDate(calendar);

                    datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

                }
                else
                {
                    if(amountEdittextSecond.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter the Amount.");
                    }
                    else if(checqueNoEdittextSecond.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter ChequeNo/RefNo.");
                    }
                    else
                    {
                        showAlertSingleButtonError("Please Enter the Amount or ChequeNo/RefNo.");
                    }
                }

            }
        });


        dateTextViewThird=(TextView) findViewById(R.id.dateTextViewThird);
        dateTextViewThird.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if (!amountEdittextThird.getText().toString().trim().equals("") && !checqueNoEdittextThird.getText().toString().trim().equals(""))
                {
                    DateLabelTextview.setError(null);
                    DateLabelTextview.clearFocus();

                    calThird = true;
                    calendar = Calendar.getInstance();

                    Year = calendar.get(Calendar.YEAR);
                    Month = calendar.get(Calendar.MONTH);
                    Day = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = DatePickerDialog.newInstance(
                            CollectionActivityNew.this, Year, Month, Day);

                    datePickerDialog.setThemeDark(false);

                    datePickerDialog.showYearPickerFirst(false);

                    Calendar calendarForSetDate = Calendar.getInstance();
                    calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

                    // calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
                    // YOU can set min or max date using this code
                    // datePickerDialog.setMaxDate(Calendar.getInstance());
                    // datePickerDialog.setMinDate(calendar);

                    datePickerDialog.setAccentColor(Color.parseColor("#5c5696"));

                    datePickerDialog.setTitle("SELECT DATE");
                    datePickerDialog.setMinDate(calendarForSetDate);
                    /*
                     * Calendar calendar = Calendar.getInstance();
                     * calendar.setTimeInMillis
                     * (System.currentTimeMillis()+24*60*60*1000);
                     */
                    // YOU can set min or max date using this code
                    // datePickerDialog.setMaxDate(Calendar.getInstance());
                    // datePickerDialog.setMinDate(calendar);

                    datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

                }
                else
                {
                    if(amountEdittextThird.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter the Amount.");
                    }
                    else if(checqueNoEdittextThird.getText().toString().trim().equals(""))
                    {
                        showAlertSingleButtonError("Please Enter ChequeNo/RefNo.");
                    }
                    else
                    {
                        showAlertSingleButtonError("Please Enter the Amount or ChequeNo/RefNo.");
                    }

                }


            }
        });





    }



    public void TextviewInitialization()
    {
        pymtModeTextView=(TextView) findViewById(R.id.pymtModeTextView);
        AmountTextview=(TextView) findViewById(R.id.AmountTextview);
        chequeNoTextview=(TextView) findViewById(R.id.chequeNoTextview);
        DateLabelTextview=(TextView) findViewById(R.id.DateLabelTextview);
        BankLabelTextview=(TextView) findViewById(R.id.BankLabelTextview);
        totaltextview=(TextView) findViewById(R.id.totaltextview);

    }


    public void ButtonInialization() {
        ImageView btn_bck=(ImageView) findViewById(R.id.btn_bck);
        btn_bck.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(CollectionActivityNew.this);
                alertDialog.setTitle("Information");

                alertDialog.setCancelable(false);
                alertDialog.setMessage("Have you saved your data before going back ");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                        Intent ide=new Intent(CollectionActivityNew.this,OrderReview.class);
                        ide.putExtra("SN", SN);
                        ide.putExtra("storeID", storeID);
                        ide.putExtra("imei", imei);
                        ide.putExtra("userdate", date);
                        ide.putExtra("pickerDate", pickerDate);
                        ide.putExtra("flgOrderType", flgOrderType);
                        startActivity(ide);
                        finish();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();


                    }
                });

                // Showing Alert Message
                alertDialog.show();*/
                Intent ide=new Intent(CollectionActivityNew.this,OrderReview.class);
                ide.putExtra("SN", SN);
                ide.putExtra("storeID", storeID);
                ide.putExtra("imei", imei);
                ide.putExtra("userdate", date);
                ide.putExtra("pickerDate", pickerDate);
                ide.putExtra("flgOrderType", flgOrderType);

                startActivity(ide);
                finish();
            }
        });

        Done_btn=(Button) findViewById(R.id.Done_btn);
        Done_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
               // if(validate() && validateCollectionAmt())
                if(validate())
               {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            CollectionActivityNew.this);
                    alertDialog.setTitle("Information");

                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Do you want to save data ");
                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                    saveDataToDatabase();


                                }
                            });
                    alertDialog.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    // Showing Alert Message
                    alertDialog.show();



                }

            }
        });


    }

    public void saveDataToDatabase()
    {
        String paymentModeFirstString="Cash";
        String AmountFirstString="0";
        String ChequeNoFirstString="0";
        String DateFirstString="0";
        String BankFirstString="0";

        String paymentModeSecondString="Cheque/DD";
        String AmountSecondString="0";
        String ChequeNoSecondString="0";
        String DateSecondString="0";
        String BankSecondString="0";

        String paymentModeThirdString="Electronic";
        String AmountThirdString="0";
        String ChequeNoThirdString="0";
        String DateThirdString="0";
        String BankThirdString="0";


        // First row data
        if(!TextUtils.isEmpty(amountEdittextFirst.getText().toString()))
        {
            AmountFirstString =amountEdittextFirst.getText().toString().trim();
        }


        // Second row data
        if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()))
        {
            AmountSecondString =amountEdittextSecond.getText().toString().trim();
        }
        if(!checqueNoEdittextSecond.getText().toString().trim().equals("")){
            ChequeNoSecondString =checqueNoEdittextSecond	.getText().toString().trim();
        }
        if(!dateTextViewSecond.getText().toString().trim().equals("21-mar-2016"))
        {
            DateSecondString =dateTextViewSecond.getText().toString().trim();
        }

        if(dateTextViewSecond.getText().toString().trim().equals("21-mar-2016"))
        {
            DateSecondString ="0";
        }
        if (hashmapBank != null)
        {
            if(hashmapBank.containsKey(BankSpinnerSecond.getText().toString().trim()))
            {
                BankSecondString = hashmapBank.get(BankSpinnerSecond.getText().toString().trim());
            }
            else
            {
                BankSecondString="0";
            }


        }
        else
        {
            BankSecondString = BankNameSecondUpdate;
        }



        // Third row data
        if(!TextUtils.isEmpty(amountEdittextThird.getText().toString()))
        {
            AmountThirdString =amountEdittextThird.getText().toString().trim();
        }

      /*  if(!checqueNoEdittextThird.getText().toString().trim().equals("")){
            ChequeNoThirdString =checqueNoEdittextThird.getText().toString().trim();
        }
        if(!dateTextViewThird.getText().toString().trim().equals("21-mar-2016")){
            DateThirdString =dateTextViewThird.getText().toString().trim();
        }

        if(dateTextViewThird.getText().toString().trim().equals("21-mar-2016")){
            DateThirdString ="0";
        }

        if (hashmapBank != null)
        {
            if(hashmapBank.containsKey(BankSpinnerThird.getText().toString().trim()))
            {
                BankThirdString = hashmapBank.get(BankSpinnerThird.getText().toString().trim());
            }
            else
            {
                BankThirdString="0";
            }


        }
        else
        {
            BankThirdString = BankNameThirdUpdate;
        }*/


        if(!TextUtils.isEmpty(amountEdittextFirst.getText().toString()) || !TextUtils.isEmpty(amountEdittextSecond.getText().toString()) || !TextUtils.isEmpty(amountEdittextThird.getText().toString()))
        {
            dbengine.open();
            dbengine.deleteWhereStoreId(storeIDGlobal,strGlobalOrderID);
            if(!AmountFirstString.equals("0")){
                dbengine.savetblAllCollectionData(strGlobalOrderID,storeIDGlobal, paymentModeFirstString,"1", AmountFirstString,ChequeNoFirstString, "0", BankFirstString,TmpInvoiceCodePDA);

            }
            if(!AmountSecondString.equals("0")){
                dbengine.savetblAllCollectionData(strGlobalOrderID,storeIDGlobal, paymentModeSecondString,"2", AmountSecondString,ChequeNoSecondString, DateSecondString, BankSecondString,TmpInvoiceCodePDA);

            }
            if(!AmountThirdString.equals("0")){
                dbengine.savetblAllCollectionData(strGlobalOrderID,storeIDGlobal, paymentModeThirdString,"4", AmountThirdString,ChequeNoThirdString, DateThirdString, BankThirdString,TmpInvoiceCodePDA);
            }
           /* if(!AmountFirstString.equals("0"))
            {
                dbengine.savetblAllCollectionData(storeIDGlobal, paymentModeFirstString, AmountFirstString,
                        ChequeNoFirstString, DateFirstString, BankFirstString,paymentModeSecondString, AmountSecondString,
                        ChequeNoSecondString, DateSecondString, BankSecondString,paymentModeThirdString, AmountThirdString,
                        ChequeNoThirdString, DateThirdString, BankThirdString,strGlobalOrderID);

            }
            if(!AmountSecondString.equals("0"))
            {
               // dbengine.savetblAllCollectionData(storeIDGlobal, paymentModeSecondString,"2", AmountSecondString,ChequeNoSecondString, DateSecondString, BankSecondString,TmpInvoiceCodePDA);
                dbengine.savetblAllCollectionData(storeIDGlobal, paymentModeFirstString, AmountFirstString,
                        ChequeNoFirstString, DateFirstString, BankFirstString,paymentModeSecondString, AmountSecondString,
                        ChequeNoSecondString, DateSecondString, BankSecondString,paymentModeThirdString, AmountThirdString,
                        ChequeNoThirdString, DateThirdString, BankThirdString,strGlobalOrderID);
            }
            if(!AmountThirdString.equals("0"))
            {
               // dbengine.savetblAllCollectionData(storeIDGlobal, paymentModeThirdString,"4", AmountThirdString,ChequeNoThirdString, DateThirdString, BankThirdString,TmpInvoiceCodePDA);
                dbengine.savetblAllCollectionData(storeIDGlobal, paymentModeFirstString, AmountFirstString,
                        ChequeNoFirstString, DateFirstString, BankFirstString,paymentModeSecondString, AmountSecondString,
                        ChequeNoSecondString, DateSecondString, BankSecondString,paymentModeThirdString, AmountThirdString,
                        ChequeNoThirdString, DateThirdString, BankThirdString,strGlobalOrderID);
            }*/



            dbengine.close();
        }
        else
        {

        }


        Intent ide=new Intent(CollectionActivityNew.this,OrderReview.class);
        ide.putExtra("SN", SN);
        ide.putExtra("storeID", storeID);
        ide.putExtra("imei", imei);
        ide.putExtra("userdate", date);
        ide.putExtra("pickerDate", pickerDate);
        ide.putExtra("flgOrderType", flgOrderType);
        ide.putExtra("OrderPDAID", strGlobalOrderID);


        startActivity(ide);
        finish();



    }


   /* private void allMessageAlert(String message) {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(CollectionActivityNew.this);
        alertDialogNoConn.setTitle("Information");
        alertDialogNoConn.setMessage(message);
        alertDialogNoConn.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();

                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }*/


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear,
                          int dayOfMonth) {
        String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec" };
        String mon = MONTHS[monthOfYear];
        if(calFirst){
            dateTextViewFirst.setText(dayOfMonth + "-" + mon + "-" + year);
            calFirst=false;
            calSecond=false;

        }

        if(calSecond){
            dateTextViewSecond.setText(dayOfMonth + "-" + mon + "-" + year);
            calFirst=false;
            calSecond=false;
            calThird=false;

        }
        if(calThird){
            dateTextViewThird.setText(dayOfMonth + "-" + mon + "-" + year);
            calFirst=false;
            calSecond=false;
            calThird=false;

        }


    }
    private boolean validateCollectionAmt()
    {
        Double  OverAllAmountCollectedLimit=dbengine.fetch_Store_MaxCollectionAmount(storeID,strGlobalOrderID);
        OverAllAmountCollectedLimit=Double.parseDouble(new DecimalFormat("##.##").format(OverAllAmountCollectedLimit));
        OverAllAmountCollected=Double.parseDouble(new DecimalFormat("##.##").format(OverAllAmountCollected));

        Double MinCollectionvalue=dbengine.fnGetStoretblLastOutstanding(storeID);
        MinCollectionvalue=Double.parseDouble(new DecimalFormat("##.##").format(MinCollectionvalue));

        if(Math.ceil(OverAllAmountCollected) < Math.ceil(MinCollectionvalue))
        {
            // showAlertSingleButtonError("Collection Amount can not be less then "+MinCollectionvalue);
            showAlertSingleAfterCostumValidationForAmountCollection("Collected amount is less than the minimum collection amount , current invoice cannot be made.\nClick CANCEL & EXIT to close Invoice and exit current visit. \nClick on UPDATE PAYMENT to update Collection amount.");
            return false;
        }
        else if(Math.ceil(OverAllAmountCollected)>=Math.ceil(MinCollectionvalue) && Math.ceil(OverAllAmountCollected)<=Math.ceil(OverAllAmountCollectedLimit))
        {
            return true;
        }
        else
        {
            // showAlertSingleButtonError("Collection Amount can not be greater then "+OverAllAmountCollectedLimit);
            // showAlertSingleAfterCostumValidationForAmountCollection("Collection amount exceeds then required and current Invoice can not be made, Click Cancel & Exit to close Invoice and Exit current visit, Click on Update Payment to update Collection amount.");

            showAlertSingleAfterCostumValidationForAmountCollection("Collection amount can not be greater then total outstandings, current invoice cannot be made.\nClick CANCEL & EXIT to close Invoice and exit current visit. \nClick on UPDATE PAYMENT to update Collection amount.");

            //Collected amount is less than the minimum collection amount , current invoice cannot be made.Click CANCEL & EXIT to close Invoice and exit current visit, Click on UPDATE PAYMENT to update Collection amount

            return false;
        }


    }
    private boolean validate()
    {

        // Start Second Row
        if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && checqueNoEdittextSecond.getText().toString().trim().equals(""))
        {
            checqueNoEdittextSecond.clearFocus();
            checqueNoEdittextSecond.requestFocus();
            String estring = "RefNo/chequeNo/TrnNo is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            checqueNoEdittextSecond.setError(ssbuilder);

            return false;
        }
        else if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }
        else if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && amountEdittextSecond.getText().toString().trim().equals(""))
        {
            amountEdittextSecond.clearFocus();
            amountEdittextSecond.requestFocus();

            String estring = "Amount is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            amountEdittextSecond.setError(ssbuilder);

            return false;
        }
        else if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && BankSpinnerSecond.getText().toString().trim().equals("Select"))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }
        // Second row end
        // Start Second Row
        if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && checqueNoEdittextSecond.getText().toString().trim().equals(""))
        {
            checqueNoEdittextSecond.clearFocus();
            checqueNoEdittextSecond.requestFocus();
            String estring = "RefNo/chequeNo/TrnNo is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            checqueNoEdittextSecond.setError(ssbuilder);

            return false;
        }
        else if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(amountEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }
        else if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && amountEdittextSecond.getText().toString().trim().equals(""))
        {
            amountEdittextSecond.clearFocus();
            amountEdittextSecond.requestFocus();

            String estring = "Amount is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            amountEdittextSecond.setError(ssbuilder);

            return false;
        }
        else if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && dateTextViewSecond.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(checqueNoEdittextSecond.getText().toString()) && BankSpinnerSecond.getText().toString().trim().equals("Select"))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }
        // Second row end

        // Third Second Row
       /* else if(!TextUtils.isEmpty(amountEdittextThird.getText().toString()) && checqueNoEdittextThird.getText().toString().trim().equals(""))
        {
            checqueNoEdittextThird.clearFocus();
            checqueNoEdittextThird.requestFocus();
            String estring = "RefNo/chequeNo/TrnNo is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            checqueNoEdittextThird.setError(ssbuilder);

            return false;
        }
        else if(!TextUtils.isEmpty(amountEdittextThird.getText().toString()) && dateTextViewThird.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(amountEdittextThird.getText().toString()) && dateTextViewThird.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }
        else if(!TextUtils.isEmpty(checqueNoEdittextThird.getText().toString()) && amountEdittextThird.getText().toString().trim().equals(""))//
        {
            amountEdittextThird.clearFocus();
            amountEdittextThird.requestFocus();

            String estring = "Amount is Empty";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#ffffff"));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            amountEdittextThird.setError(ssbuilder);

            return false;
        }*/
        /*else if(!TextUtils.isEmpty(checqueNoEdittextThird.getText().toString()) && dateTextViewThird.getText().toString().trim().equals(""))
        {
            showAlertSingleButtonError("Date is Empty");
            return false;
        }
        else  if(!TextUtils.isEmpty(checqueNoEdittextThird.getText().toString()) && BankSpinnerThird.getText().toString().trim().equals("Select"))
        {
            showAlertSingleButtonError("Please Select Bank.");
            return false;
        }*/
        else
        {
            return true;
        }


    }

    public void showAlertSingleAfterCostumValidationForAmountCollection(String msg)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CollectionActivityNew.this);
        alertDialog.setTitle("Validation");
        alertDialog.setIcon(R.drawable.error_ico);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Update Payment", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel & Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
                Intent ide=new Intent(CollectionActivityNew.this,OrderReview.class);
                ide.putExtra("SN", SN);
                ide.putExtra("storeID", storeID);
                ide.putExtra("imei", imei);
                ide.putExtra("userdate", date);
                ide.putExtra("pickerDate", pickerDate);
                ide.putExtra("flgOrderType", flgOrderType);
                ide.putExtra("OrderPDAID", strGlobalOrderID);

                startActivity(ide);
                finish();
            }
        });

        alertDialog.show();

    }

    public void SetDataToLayout(String collectionDataString){
        String paymentMode=collectionDataString.split(Pattern.quote("^"))[0];
        String PaymentModeID=collectionDataString.split(Pattern.quote("^"))[1];
        String Amount=collectionDataString.split(Pattern.quote("^"))[2];
        String RefNoChequeNoTrnNo=collectionDataString.split(Pattern.quote("^"))[3];
        String Date=collectionDataString.split(Pattern.quote("^"))[4];
        String BankID=collectionDataString.split(Pattern.quote("^"))[5];
        //paymentMode 1 means Cash
        if(PaymentModeID.equals("1")){
            amountEdittextFirst.setText(Amount);
        }
        //paymentMode 2 means Check/DD
        if(PaymentModeID.equals("2")){
            amountEdittextSecond.setText(Amount);
            checqueNoEdittextSecond.setText(RefNoChequeNoTrnNo);
            dateTextViewSecond.setText(Date);
            if (BankID.equals("0"))
            {
                BankSpinnerSecond.setText(getResources().getString(R.string.txtSelect));
            } else
            {
                BankSpinnerSecond.setText(linkedHmapBankID.get(BankID));

            }
        }
        //paymentMode 4 means Electronics
        if(PaymentModeID.equals("4")){
            amountEdittextThird.setText(Amount);
           /* checqueNoEdittextThird.setText(RefNoChequeNoTrnNo);
            dateTextViewThird.setText(Date);
            if (BankID.equals("0"))
            {
                BankSpinnerThird.setText(getResources().getString(R.string.txtSelect));
            } else
            {
                BankSpinnerThird.setText(linkedHmapBankID.get(BankID));

            }*/
            checqueNoEdittextThird.setText("");
            dateTextViewThird.setText("");
            BankID="0";
            if (BankID.equals("0"))
            {
                BankSpinnerThird.setText(getResources().getString(R.string.txtSelect));
            } else
            {
                BankSpinnerThird.setText(linkedHmapBankID.get(BankID));

            }
        }

    }
}
