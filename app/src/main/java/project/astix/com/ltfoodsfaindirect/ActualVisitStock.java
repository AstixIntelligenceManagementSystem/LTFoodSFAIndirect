package project.astix.com.ltfoodsfaindirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import project.astix.com.ltfoodsfaindirect.Adapters.CustomVisitStockAdapter;
import project.astix.com.ltfoodsfaindirect.Models.ActualVisitProductInfo;

public class ActualVisitStock extends Activity implements CategoryCommunicator, CustomVisitStockAdapter.EditTextClickListener {

    private static final String TAG = ActualVisitStock.class.getSimpleName();
    LinearLayout lLayout_main, ll_SelfOtherProducts, ll_CompleteSelfOtherProductSection, ll_CompanyPrdHeader;
    DBAdapterKenya dbengine;
    Button btnNext;
    CustomKeyboard customKeyboard;
    CustomKeyboard customKeyboard2;

    private View divider;
    private RecyclerView defaultProductRV, filteredProductRV;
    public EditText ed_search, ed_searchSelfOtherProducts;
    public ImageView btn_go, btn_goSelfOtherProduct;
    public int flgCompanyCompetitorProducts = 0;
    public String storeID;
    public String imei;
    public String date;
    public String pickerDate;
    public String selStoreName;
    int isStockAvlbl = 0;
    int isCmpttrAvlbl = 0;
    List<String> categoryNames;
    int progressBarStatus = 0;
    public Dialog dialog = null;
    LinkedHashMap<String, String> hmapctgry_details = new LinkedHashMap<String, String>();
    ImageView img_ctgry, img_ctgrySelfOtherProducts;
    public int StoreCurrentStoreType = 0;
    String previousSlctdCtgry = "";
    LinkedHashMap<String, String> hmapFilterProductListCompany = new LinkedHashMap<String, String>();
    LinkedHashMap<String, String> hmapFilterProductListCompetitor = new LinkedHashMap<String, String>();
    LinkedHashMap<String, ActualVisitProductInfo> hmapFetchPDASavedDataForDefaultData = new LinkedHashMap<>();
    LinkedHashMap<String, ActualVisitProductInfo> hmapFetchPDASavedDataForFilteredData = new LinkedHashMap<>();

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            customKeyboard.hideCustomKeyboard();
            return false;

        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {

        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_visit_stock);
        dbengine = new DBAdapterKenya(ActualVisitStock.this);
        getDataFromIntent();
        initializeallViews();
        customKeyboard = new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num);
        customKeyboard2 = new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num);
        fetchDataFromDatabase();
    }

    public void initializeallViews() {
        divider = findViewById(R.id.divider);
        defaultProductRV = (RecyclerView) findViewById(R.id.default_products_recycler_view);
        RecyclerView.LayoutManager layoutManagerForDefaultRV = new LinearLayoutManager(this);
        defaultProductRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        defaultProductRV.setLayoutManager(layoutManagerForDefaultRV);

        filteredProductRV = (RecyclerView) findViewById(R.id.filtered_products_recycler_view);
        RecyclerView.LayoutManager layoutManagerForFilteredRV = new LinearLayoutManager(this);
        filteredProductRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        filteredProductRV.setLayoutManager(layoutManagerForFilteredRV);

        lLayout_main = (LinearLayout) findViewById(R.id.lLayout_main);
        // ll_SelfOtherProducts = (LinearLayout) findViewById(R.id.ll_SelfOtherProducts);
        ImageView img_back_Btn = (ImageView) findViewById(R.id.img_back_Btn);
        btnNext = (Button) findViewById(R.id.btnNext);
        ll_CompleteSelfOtherProductSection = (LinearLayout) findViewById(R.id.ll_CompleteSelfOtherProductSection);
        ll_CompanyPrdHeader = (LinearLayout) findViewById(R.id.ll_CompanyPrdHeader);
        img_ctgry = (ImageView) findViewById(R.id.img_ctgry);
        ed_search = (EditText) findViewById(R.id.ed_search);
        btn_go = (ImageView) findViewById(R.id.btn_go);


        img_ctgrySelfOtherProducts = (ImageView) findViewById(R.id.img_ctgrySelfOtherProducts);
        ed_searchSelfOtherProducts = (EditText) findViewById(R.id.ed_searchSelfOtherProducts);
        ed_searchSelfOtherProducts.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    customKeyboard.hideCustomKeyboard();
                }
            }
        });

        btn_goSelfOtherProduct = (ImageView) findViewById(R.id.btn_goSelfOtherProduct);

        img_ctgry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_ctgry.setEnabled(false);
                // flgCompanyCompetitorProducts = 0;
                customAlertStoreList(categoryNames, "Select Category");
            }
        });


        img_ctgrySelfOtherProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //img_ctgrySelfOtherProducts.setEnabled(false);
                flgCompanyCompetitorProducts = 1;
                customAlertStoreList(categoryNames, "Select Competitor Category");
            }
        });
        btn_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(ed_search.getText().toString().trim())) {

                    if (!ed_search.getText().toString().trim().equals("")) {
                        searchProduct(ed_search.getText().toString().trim(), "", 0, false);
                    }


                } else {
                    searchProduct("All", "", 0, false);
                }

            }


        });


        btn_goSelfOtherProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(ed_searchSelfOtherProducts.getText().toString().trim())) {

                    if (!ed_searchSelfOtherProducts.getText().toString().trim().equals("")) {
                        searchProduct(ed_searchSelfOtherProducts.getText().toString().trim(), "", 1, false);

                    }


                } else {
                    searchProduct("All", "", 1, false);
                }

            }


        });
        img_back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent fireBackDetPg=new Intent(ActualVisitStock.this,LastVisitDetails.class);
                fireBackDetPg.putExtra("storeID", storeID);
                fireBackDetPg.putExtra("SN", selStoreName);
                fireBackDetPg.putExtra("bck", 1);
                fireBackDetPg.putExtra("imei", imei);
                fireBackDetPg.putExtra("userdate", date);
                fireBackDetPg.putExtra("pickerDate", pickerDate);
                fireBackDetPg.putExtra("flgOrderType", 1);
                //fireBackDetPg.putExtra("rID", routeID);
                startActivity(fireBackDetPg);
                finish();*/
                //aa
                Intent nxtP4 = new Intent(ActualVisitStock.this, PicClkBfrStock.class);
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
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStockFilledProperly()) {
                    dbengine.deleteActualVisitData(storeID);

                    Log.d(TAG, "UpdatedDefaultData : " + hmapFetchPDASavedDataForDefaultData.toString());
                    if (hmapFetchPDASavedDataForDefaultData != null && hmapFetchPDASavedDataForDefaultData.size() > 0) {
                        for (Map.Entry<String, ActualVisitProductInfo> entry : hmapFetchPDASavedDataForDefaultData.entrySet()) {

                            ActualVisitProductInfo visitProductInfo = entry.getValue();
                            dbengine.saveTblActualVisitStock(storeID, visitProductInfo.getProductId(), visitProductInfo.getStock(), 1, 1, visitProductInfo.getDisplayUnit());
                        }
                    }

                    Log.d(TAG, "UpdatedFilteredData : " + hmapFetchPDASavedDataForFilteredData.toString());
                    if (hmapFetchPDASavedDataForFilteredData != null && hmapFetchPDASavedDataForFilteredData.size() > 0) {
                        for (Map.Entry<String, ActualVisitProductInfo> entry : hmapFetchPDASavedDataForFilteredData.entrySet()) {

                            ActualVisitProductInfo visitProductInfo = entry.getValue();
                            dbengine.saveTblActualVisitStock(storeID, visitProductInfo.getProductId(), visitProductInfo.getStock(), 1, 0, visitProductInfo.getDisplayUnit());
                        }
                    }

                    passIntentToProductOrderFilter();
                } else {
                    //showAlertForEveryOne("It's compulsory to fill atleast one stock as you have mentioned Ltfoods Stock available.");
                    showAlertForEveryOne("It's compulsory to fill  stock as you have mentioned Ltfoods Stock available.");
                }
            }
        });

    }

    public void showAlertForEveryOne(String msg) {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(ActualVisitStock.this);
        alertDialogNoConn.setTitle("Information");
        alertDialogNoConn.setMessage(msg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(R.string.txtOk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();
    }

    public boolean isStockFilledProperly() {
        boolean stockFilledPrprly = true;
       /* if (hmapFetchPDASavedDataForDefaultData != null && hmapFetchPDASavedDataForDefaultData.size() > 0) {
            for (Map.Entry<String, ActualVisitProductInfo> entry : hmapFetchPDASavedDataForDefaultData.entrySet()) {
                ActualVisitProductInfo visitProductInfo = entry.getValue();
                if (!TextUtils.isEmpty(visitProductInfo.getStock())) {
                    if (Integer.parseInt(visitProductInfo.getStock()) < 0) {
                        stockFilledPrprly = false;
                        return stockFilledPrprly;
                    }
                }
            }
        }*/
        if (defaultProductInfoArrayList != null && !defaultProductInfoArrayList.isEmpty()) {
            for (ActualVisitProductInfo visitProductInfo : defaultProductInfoArrayList) {
                if (hmapFetchPDASavedDataForDefaultData.containsKey(visitProductInfo.getProductId())) {
                    if (!TextUtils.isEmpty(visitProductInfo.getStock() ) && !visitProductInfo.getStock().equals("")) {
                        if (Integer.parseInt(visitProductInfo.getStock()) < 0) {
                            stockFilledPrprly = false;
                            return stockFilledPrprly;
                        }
                    }
                    else {
                        stockFilledPrprly = false;
                        return stockFilledPrprly;
                    }
                }
                else {
                    stockFilledPrprly = false;
                    return stockFilledPrprly;
                }
            }
        }

        if (hmapFetchPDASavedDataForFilteredData != null && hmapFetchPDASavedDataForFilteredData.size() > 0) {
            for (Map.Entry<String, ActualVisitProductInfo> entry : hmapFetchPDASavedDataForFilteredData.entrySet()) {
                ActualVisitProductInfo visitProductInfo = entry.getValue();
                if (!TextUtils.isEmpty(visitProductInfo.getStock())) {
                    if (Integer.parseInt(visitProductInfo.getStock()) < 0) {
                        stockFilledPrprly = false;
                        return stockFilledPrprly;
                    }
                }
            }
        }

        return stockFilledPrprly;
    }

    public void passIntentToProductOrderFilter() {
        if (isCmpttrAvlbl == 1) {
            Intent nxtP4 = new Intent(ActualVisitStock.this, FeedbackCompetitorActivity.class);
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
        } else {
            Intent nxtP4 = new Intent(ActualVisitStock.this, PicClkdAfterStock.class);
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

    }

    public void inflatePrdctStockData(int flgCompanyCompetitorProducts) {

        /*if (flgCompanyCompetitorProducts == 0) {
            if (hmapFilterProductListCompany != null && hmapFilterProductListCompany.size() > 0) {
                for (Map.Entry<String, String> entry : hmapFilterProductListCompany.entrySet()) {

                    String prdId = entry.getKey();
                    String value = entry.getValue().toString().split(Pattern.quote("^"))[0];
                    String unit = entry.getValue().toString().split(Pattern.quote("^"))[1];


                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewProduct = inflater.inflate(R.layout.inflate_row_actual_visit, null);
                    LinearLayout ll_inflate = (LinearLayout) viewProduct.findViewById(R.id.ll_inflate);

                    TextView prdName = (TextView) viewProduct.findViewById(R.id.prdName);
                    final EditText et_stckVal = (EditText) viewProduct.findViewById(R.id.et_stckVal);
                    TextView tv_stckUnit = (TextView) viewProduct.findViewById(R.id.tv_stckUnit);
                    prdName.setText(value);
                    prdName.setTag(prdId);
                    tv_stckUnit.setText(unit);
                    tv_stckUnit.setTag(prdId + "_Unit");

                    et_stckVal.setTag(prdId + "_Stock");

                    if (hmapFetchPDASavedDataForDefaultData != null && hmapFetchPDASavedDataForDefaultData.containsKey(prdId)) {
                        et_stckVal.setText(hmapFetchPDASavedDataForDefaultData.get(prdId));
                    } else {
                        hmapFetchPDASavedDataForDefaultData.put(prdId, "-1");
                    }
                    et_stckVal.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            ActualVisitProductInfo visitProductInfo;
                            if (!TextUtils.isEmpty(et_stckVal.getText().toString().trim())) {
                                String tagProductId = et_stckVal.getTag().toString().split(Pattern.quote("_"))[0];
                                String stock = et_stckVal.getText().toString().trim();

                                if (hmapFetchPDASavedDataForDefaultData.containsKey(tagProductId)) {
                                    visitProductInfo = hmapFetchPDASavedDataForDefaultData.get(tagProductId);
                                    visitProductInfo.setStock(stock);
                                } else {
                                    visitProductInfo = new ActualVisitProductInfo(storeID,
                                            tagProductId,
                                            1,
                                            stock,
                                            1,
                                            ""
                                    );
                                }
                                hmapFetchPDASavedDataForDefaultData.put(tagProductId, visitProductInfo);
                            } else {
                                String tagProductId = et_stckVal.getTag().toString().split(Pattern.quote("_"))[0];
                                if (hmapFetchPDASavedDataForDefaultData.containsKey(tagProductId)) {
                                    // hmapFetchPDASavedDataForDefaultData.remove(tagProductId);
                                    visitProductInfo = new ActualVisitProductInfo(storeID,
                                            tagProductId,
                                            1,
                                            "-1",
                                            1,
                                            ""
                                    );
                                    hmapFetchPDASavedDataForDefaultData.put(tagProductId, visitProductInfo);
                                }
                            }
                        }
                    });
                    lLayout_main.addView(viewProduct);

                    // btnNextClick(storeID,prdId,et_stckVal);


                }
            }
        }
        if (flgCompanyCompetitorProducts == 1) {
            if (hmapFilterProductListCompetitor != null && hmapFilterProductListCompetitor.size() > 0) {
                for (Map.Entry<String, String> entry : hmapFilterProductListCompetitor.entrySet()) {

                    String prdId = entry.getKey();
                    String value = entry.getValue().toString().split(Pattern.quote("^"))[0];
                    String unit = entry.getValue().toString().split(Pattern.quote("^"))[1];


                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewProduct = inflater.inflate(R.layout.inflate_row_actual_visit, null);
                    LinearLayout ll_inflate = (LinearLayout) viewProduct.findViewById(R.id.ll_inflate);

                    TextView prdName = (TextView) viewProduct.findViewById(R.id.prdName);
                    final EditText et_stckVal = (EditText) viewProduct.findViewById(R.id.et_stckVal);
                    TextView tv_stckUnit = (TextView) viewProduct.findViewById(R.id.tv_stckUnit);
                    prdName.setText(value);
                    prdName.setTag(prdId);
                    tv_stckUnit.setText(unit);
                    tv_stckUnit.setTag(prdId + "_Unit");

                    et_stckVal.setTag(prdId + "_Stock");

                    if (hmapFetchPDASavedDataForDefaultData != null && hmapFetchPDASavedDataForDefaultData.containsKey(prdId)) {
                        et_stckVal.setText(hmapFetchPDASavedDataForDefaultData.get(prdId));
                    } else {
                        hmapFetchPDASavedDataForDefaultData.put(prdId, "-1");
                    }
                    et_stckVal.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!TextUtils.isEmpty(et_stckVal.getText().toString().trim())) {
                                String tagProductId = et_stckVal.getTag().toString().split(Pattern.quote("_"))[0];
                                hmapFetchPDASavedDataForDefaultData.put(tagProductId, et_stckVal.getText().toString().trim());
                            } else {
                                String tagProductId = et_stckVal.getTag().toString().split(Pattern.quote("_"))[0];
                                if (hmapFetchPDASavedDataForDefaultData.containsKey(tagProductId)) {
                                    // hmapFetchPDASavedDataForDefaultData.remove(tagProductId);
                                    hmapFetchPDASavedDataForDefaultData.put(tagProductId, "-1");
                                }
                            }
                        }
                    });
                    ll_SelfOtherProducts.addView(viewProduct);

                    // btnNextClick(storeID,prdId,et_stckVal);


                }
            }
        }*/
    }


    public void fetchDataFromDatabase() {
        dbengine.open();
        /*hmapPrdctData = dbengine.fetchProductDataForActualVisit();
        hmapFetchPDASavedDataForDefaultData = dbengine.fetchActualVisitData(storeID);
        hmapProductStockFromPurchaseTable = dbengine.fetchProductStockFromPurchaseTable(storeID);
        Iterator it11new = hmapProductStockFromPurchaseTable.entrySet().iterator();
        String crntPID = "0";
        int cntPsize = 0;
        while (it11new.hasNext()) {
            Map.Entry pair = (Map.Entry) it11new.next();
            hmapFetchPDASavedDataForDefaultData.put(pair.getKey().toString(), pair.getValue().toString());
        }
*/
        //  img_ctgry.setText("All");
        //searchProduct("All","");
        List<ActualVisitProductInfo> savedActualVisitProductInfos = dbengine.fetchActualVisitDataForActualVisitStock(storeID);
        if (savedActualVisitProductInfos != null) {
            for (ActualVisitProductInfo visitProductInfo : savedActualVisitProductInfos) {
                if (visitProductInfo.getIsDefaultProduct() == 1) {
                    hmapFetchPDASavedDataForDefaultData.put(visitProductInfo.getProductId(), visitProductInfo);
                } else if (visitProductInfo.getIsDefaultProduct() == 0) {
                    hmapFetchPDASavedDataForFilteredData.put(visitProductInfo.getProductId(), visitProductInfo);
                }
            }
        }

        StoreCurrentStoreType = Integer.parseInt(dbengine.fnGetStoreTypeOnStoreIdBasis(storeID));
        dbengine.close();
        getCategoryDetail();

        searchLoadDefaultProduct("All", "", 0);//Company Product Loading//********WE load defualt product on Oncreate
        searchProduct("All", "", 1, true);//Competitor Product Loading
            /* if(hmapFetchPDASavedDataForDefaultData!=null && hmapFetchPDASavedDataForDefaultData.size()>0) {


            for (Map.Entry<String, String> entry : hmapFetchPDASavedDataForDefaultData.entrySet()) {

                String prdId=entry.getKey();
                String stckVal=entry.getValue();


                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewProduct = inflater.inflate(R.layout.inflate_row_actual_visit, null);
                LinearLayout ll_inflate= (LinearLayout) viewProduct.findViewById(R.id.ll_inflate);

                TextView prdName= (TextView) viewProduct.findViewById(R.id.prdName);
                EditText et_stckVal= (EditText) findViewById(R.id.et_stckVal);

                lLayout_main.addView(viewProduct);

            }
        }*/
    }


    private void getDataFromIntent() {


        Intent passedvals = getIntent();

        if (passedvals != null) {

            storeID = passedvals.getStringExtra("storeID");
            imei = passedvals.getStringExtra("imei");
            date = passedvals.getStringExtra("userdate");
            pickerDate = passedvals.getStringExtra("pickerDate");
            selStoreName = passedvals.getStringExtra("SN");
            isStockAvlbl = passedvals.getIntExtra("isStockAvlbl", 0);
            isCmpttrAvlbl = passedvals.getIntExtra("isCmpttrAvlbl", 0);

        }

    }

    public void customAlertStoreList(final List<String> listOption, String sectionHeader) {

        final Dialog listDialog = new Dialog(ActualVisitStock.this);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        listDialog.setContentView(R.layout.search_list);
        listDialog.setCanceledOnTouchOutside(false);
        listDialog.setCancelable(false);
        WindowManager.LayoutParams parms = listDialog.getWindow().getAttributes();
        parms.gravity = Gravity.CENTER;
        //there are a lot of settings, for dialog, check them all out!
        parms.dimAmount = (float) 0.5;


        TextView txt_section = (TextView) listDialog.findViewById(R.id.txt_section);
        txt_section.setText(sectionHeader);
        TextView txtVwCncl = (TextView) listDialog.findViewById(R.id.txtVwCncl);
        //    TextView txtVwSubmit=(TextView) listDialog.findViewById(R.id.txtVwSubmit);


        final ListView list_store = (ListView) listDialog.findViewById(R.id.list_store);
        final CardArrayAdapterCategory cardArrayAdapter = new CardArrayAdapterCategory(ActualVisitStock.this, listOption, listDialog, previousSlctdCtgry, flgCompanyCompetitorProducts);

        //img_ctgry.setText(previousSlctdCtgry);


        list_store.setAdapter(cardArrayAdapter);
        //	editText.setBackgroundResource(R.drawable.et_boundary);
        img_ctgry.setEnabled(true);


        txtVwCncl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listDialog.dismiss();
                img_ctgry.setEnabled(true);


            }
        });


        //now that the dialog is set up, it's time to show it
        listDialog.show();

    }


    @Override
    public void selectedOption(String selectedCategory, Dialog dialog, int flgCompanyCompetitorProducts) {
        dialog.dismiss();
        previousSlctdCtgry = selectedCategory;

        //  img_ctgry.setText(selectedCategory);

        if (hmapctgry_details.containsKey(selectedCategory)) {
            searchProduct(selectedCategory, hmapctgry_details.get(selectedCategory), flgCompanyCompetitorProducts, false);
        } else {
            searchProduct(selectedCategory, "", flgCompanyCompetitorProducts, false);
        }


    }

    private ArrayList<ActualVisitProductInfo> filteredProductList = new ArrayList<>();

    public void searchProduct(String filterSearchText, String ctgryId, int flgCompanyCompetitorProducts, boolean isFirstTime) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarStatus = 0;

        /*if (flgCompanyCompetitorProducts == 0) {
            hmapFilterProductListCompany.clear();
            hmapFilterProductListCompany = dbengine.getFileredProductListMapStoreCheck(filterSearchText.trim(),
                    StoreCurrentStoreType,
                    ctgryId,
                    flgCompanyCompetitorProducts);

            lLayout_main.removeAllViews();
            if (hmapFilterProductListCompany.size() > 0) {
                inflatePrdctStockData(flgCompanyCompetitorProducts);
            } else {
                allMessageAlert(ActualVisitStock.this.getResources().getString(R.string.AlertFilter));
            }
        }
        */
        if (flgCompanyCompetitorProducts == 1) {
            filteredProductList.clear();
            /*hmapFilterProductListCompetitor.clear();
            hmapFilterProductListCompetitor = dbengine.getFileredProductListMapStoreCheck(filterSearchText.trim(), StoreCurrentStoreType, ctgryId, flgCompanyCompetitorProducts);
            */
            filteredProductList = dbengine.getFileredProductListMapStoreCheck(filterSearchText.trim(), StoreCurrentStoreType, ctgryId, flgCompanyCompetitorProducts, storeID);
            if (filteredProductList.size() > 0) {
                for (ActualVisitProductInfo visitProductInfo : filteredProductList) {
                    if (hmapFetchPDASavedDataForFilteredData.containsKey(visitProductInfo.getProductId())) {
                        visitProductInfo.setStock(hmapFetchPDASavedDataForFilteredData.get(visitProductInfo.getProductId()).getStock());
                    }
                }
                displayFilteredProducts(isFirstTime);
            } else {
                allMessageAlert(ActualVisitStock.this.getResources().getString(R.string.AlertFilter));
            }
            Log.d("ActualVisit", filteredProductList.toString());

            /*ll_SelfOtherProducts.removeAllViews();
            if (hmapFilterProductListCompetitor.size() > 0) {
                inflatePrdctStockData(flgCompanyCompetitorProducts);
            }*/
        }



		/*if(hmapFilterProductList.size()<250)
		{*/


		/*}

		else
		{
			allMessageAlert("Please put some extra filter on Search-Box to fetch related product");
		}*/


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void allMessageAlert(String message) {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(ActualVisitStock.this);
        alertDialogNoConn.setTitle(ActualVisitStock.this.getResources().getString(R.string.genTermInformation));
        alertDialogNoConn.setMessage(message);
        //alertDialogNoConn.setMessage(getText(R.string.connAlertErrMsg));
        alertDialogNoConn.setNeutralButton(ActualVisitStock.this.getResources().getString(R.string.AlertDialogOkButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }

    private void getCategoryDetail() {

        hmapctgry_details = dbengine.fetch_Category_List();

        int index = 0;
        if (hmapctgry_details != null) {
            categoryNames = new ArrayList<String>();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(hmapctgry_details);
            Set set2 = map.entrySet();
            Iterator iterator = set2.iterator();
            while (iterator.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator.next();
                categoryNames.add(me2.getKey().toString());
                index = index + 1;
            }
        }


    }

    public void openVideoPlayerDialog(String VIDEO_PATH) {
        dialog = new Dialog(ActualVisitStock.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.video_player_layout_for_store);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();
        // final Button cancleCamera= (Button)dialog.  findViewById(R.id.cancleCamera);

        parms.height = parms.MATCH_PARENT;
        parms.width = parms.MATCH_PARENT;
        VideoView videoView = (VideoView) dialog.findViewById(R.id.videoView1);


        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        Uri uri = Uri.parse(VIDEO_PATH);
        //  Uri uri=new Uri(STRINGPATH);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        Button btnNextDialog = (Button) dialog.findViewById(R.id.btnNextDialog);
        btnNextDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                passIntentToProductOrderFilter();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private ArrayList<ActualVisitProductInfo> defaultProductInfoArrayList = new ArrayList<>();

    public void searchLoadDefaultProduct(String filterSearchText, String ctgryId, int flgCompanyCompetitorProductsDefault) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarStatus = 0;

        if (flgCompanyCompetitorProductsDefault == 0) {
            defaultProductInfoArrayList.clear();
            defaultProductInfoArrayList = dbengine.fetchProductListLastvisitAndOrderBasis(storeID);
            if (defaultProductInfoArrayList != null && !defaultProductInfoArrayList.isEmpty()) {
                for (ActualVisitProductInfo visitProductInfo : defaultProductInfoArrayList) {
                    if (hmapFetchPDASavedDataForDefaultData.containsKey(visitProductInfo.getProductId())) {
                        visitProductInfo.setStock(hmapFetchPDASavedDataForDefaultData.get(visitProductInfo.getProductId()).getStock());
                    }
                }
            }
            displayDefaultProducts();
        }

        //System.out.println("hmapFilterProductListCount :-"+ hmapFilterProductList.size());


		/*if(hmapFilterProductList.size()<250)

		else
		{
			allMessageAlert("Please put some extra filter on Search-Box to fetch related product");
		}*/


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void displayDefaultProducts() {
        //defaultProductInfoArrayList.clear();
        if (defaultProductInfoArrayList != null && defaultProductInfoArrayList.size() > 6) {
            LinearLayout.LayoutParams layoutParamsForDefault = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.4f);
            ll_CompanyPrdHeader.setLayoutParams(layoutParamsForDefault);
            LinearLayout.LayoutParams layoutParamsForFiltered = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.6f);
            ll_CompleteSelfOtherProductSection.setLayoutParams(layoutParamsForFiltered);
            divider.setVisibility(View.VISIBLE);
            CustomVisitStockAdapter visitStockAdapter = new CustomVisitStockAdapter(this, defaultProductInfoArrayList, storeID, hmapFetchPDASavedDataForDefaultData, true, customKeyboard);
            defaultProductRV.setAdapter(visitStockAdapter);
        } else if (defaultProductInfoArrayList != null && !defaultProductInfoArrayList.isEmpty()) {
            ll_CompanyPrdHeader.setVisibility(View.VISIBLE);
            /*LinearLayout.LayoutParams layoutParamsForDefault = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.4f);
            ll_CompanyPrdHeader.setLayoutParams(layoutParamsForDefault);
            LinearLayout.LayoutParams layoutParamsForFiltered = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.6f);
            ll_CompleteSelfOtherProductSection.setLayoutParams(layoutParamsForFiltered);
            */
            divider.setVisibility(View.VISIBLE);
            CustomVisitStockAdapter visitStockAdapter = new CustomVisitStockAdapter(this, defaultProductInfoArrayList, storeID, hmapFetchPDASavedDataForDefaultData, true, customKeyboard);
            defaultProductRV.setAdapter(visitStockAdapter);
        } else {
            ll_CompanyPrdHeader.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParamsForFiltered = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 3.0f);
            ll_CompleteSelfOtherProductSection.setLayoutParams(layoutParamsForFiltered);
        }
    }

    private CustomVisitStockAdapter visitStockAdapter;

    private void displayFilteredProducts(boolean isFirstTime) {
        if (isFirstTime) {
            visitStockAdapter = new CustomVisitStockAdapter(this, filteredProductList, storeID, hmapFetchPDASavedDataForFilteredData, false, customKeyboard);
            filteredProductRV.setAdapter(visitStockAdapter);
        } else {
            visitStockAdapter.refreshData(filteredProductList);
        }

    }

    @Override
    public void onClick(EditText editText) {

        customKeyboard.registerEditText(editText);
        customKeyboard.showCustomKeyboard(editText);
    }
}
