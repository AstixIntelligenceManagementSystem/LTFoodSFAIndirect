package project.astix.com.ltfoodsfaindirect.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.astix.com.ltfoodsfaindirect.CustomKeyboard;
import project.astix.com.ltfoodsfaindirect.Models.ActualVisitProductInfo;
import project.astix.com.ltfoodsfaindirect.R;

public class CustomVisitStockAdapter extends RecyclerView.Adapter<CustomVisitStockAdapter.ViewHolder> {

    private CustomKeyboard customKeyboard;
    private Context mContext;
    private List<ActualVisitProductInfo> productInfoList;
    private String storeId;
    private Map<String, ActualVisitProductInfo> hmapFetchPDASavedDataState = new HashMap<>();
    private boolean isDefaultProducts = false;
    private Map<String, ActualVisitProductInfo> hmapFetchPDASavedData;
    private static final String TAG = CustomVisitStockAdapter.class.getName();
    private EditTextClickListener editTextClickListener;

    public CustomVisitStockAdapter(Context mContext, List<ActualVisitProductInfo> productInfoList, String storeId, Map<String, ActualVisitProductInfo> hmapFetchPDASavedData, boolean isDefaultProducts, CustomKeyboard customKeyboard) {
        this.mContext = mContext;
        this.productInfoList = productInfoList;
        this.storeId = storeId;
        this.hmapFetchPDASavedData = hmapFetchPDASavedData;
        this.isDefaultProducts = isDefaultProducts;
        this.customKeyboard = customKeyboard;
        editTextClickListener = (EditTextClickListener) mContext;
        //  customKeyboard = new CustomKeyboard((Activity) mContext,R.id.keyboardviewNum,R.xml.num);
          Log.d(TAG, "productInfoList :" + this.productInfoList.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inflate_row_actual_visit, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        //recyclerView.getLayoutManager().findViewByPosition()
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ActualVisitProductInfo productInfo = productInfoList.get(position);
        hmapFetchPDASavedDataState.put("" + holder.getAdapterPosition(), productInfo);
        holder.productName.setText(productInfo.getProductName());
        holder.stockUnitET.setText(productInfo.getDisplayUnit());


        if (hmapFetchPDASavedDataState.get("" + holder.getAdapterPosition()).getStock() != null) {
            if (hmapFetchPDASavedDataState.get("" + holder.getAdapterPosition()).getStock().equals("-1")) {
                holder.stockValueET.setText("");
            } else {
                holder.stockValueET.setText(hmapFetchPDASavedDataState.get("" + holder.getAdapterPosition()).getStock());
            }
        } else {
            holder.stockValueET.setText("");
        }

        holder.stockValueET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (v instanceof EditText){
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    });

                    editTextClickListener.onClick((EditText) v);
                }
                return false;
            }
        });
        /*holder.stockValueET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    //EditText editText = (EditText) recyclerView.getLayoutManager().findViewByPosition(holder.getAdapterPosition()).findViewById(R.id.et_stckVal);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            editTextClickListener.onClick((EditText) v);

                        }
                    });

                }
            }
        });*//*
        holder.stockValueET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = recyclerView.getLayoutManager().findViewByPosition(holder.getAdapterPosition()).findViewById(R.id.et_stckVal);
                customKeyboard.showCustomKeyboard(view);
            }
        });*/

        holder.stockValueET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ActualVisitProductInfo visitProductInfo = null;
                        if (!TextUtils.isEmpty(holder.stockValueET.getText().toString().trim())) {
                            //String tagProductId = holder.stockValueET.getTag().toString().split(Pattern.quote("_"))[0];
                            String stock = holder.stockValueET.getText().toString().trim();
                            visitProductInfo = hmapFetchPDASavedDataState.get("" + holder.getAdapterPosition());
                            visitProductInfo.setStock(stock);

                            hmapFetchPDASavedData.put(visitProductInfo.getProductId(), visitProductInfo);
                            hmapFetchPDASavedDataState.put("" + holder.getAdapterPosition(), visitProductInfo);
                        } else {
                            visitProductInfo = hmapFetchPDASavedDataState.get("" + holder.getAdapterPosition());
                            visitProductInfo.setStock("");

                            hmapFetchPDASavedDataState.put("" + holder.getAdapterPosition(), visitProductInfo);
                            hmapFetchPDASavedData.remove(visitProductInfo.getProductId());
                        }
                    }
                });
            }
        });
    }

    public void refreshData(ArrayList<ActualVisitProductInfo> updatedActualVisitProductInfos) {
        for (ActualVisitProductInfo visitProductInfo : updatedActualVisitProductInfos) {
            if (hmapFetchPDASavedData.containsKey(visitProductInfo.getProductId())) {
                visitProductInfo.setStock(hmapFetchPDASavedData.get(visitProductInfo.getProductId()).getStock());
            }
        }
        this.productInfoList = updatedActualVisitProductInfos;
        hmapFetchPDASavedDataState.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productInfoList.size();
    }

    public interface EditTextClickListener {
        void onClick(EditText editText);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private EditText stockValueET;
        private TextView stockUnitET;
        private TextView productName;

        public ViewHolder(final View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.prdName);
            stockValueET = (EditText) itemView.findViewById(R.id.et_stckVal);
            stockUnitET = (TextView) itemView.findViewById(R.id.tv_stckUnit);

            stockValueET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
        }
    }
}
