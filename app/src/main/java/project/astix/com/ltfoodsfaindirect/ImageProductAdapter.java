package project.astix.com.ltfoodsfaindirect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;


class ImageProductAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> itemsName;
    int imgLocation;
    int picPostion;
    List<String> productIdTag;
    LayoutInflater inflater;
    LinkedHashMap<String, String> hmapPrdctImgPath;
    CheckedUnchekedCmpttrProduct chkdUnchkdPrdct;
    LinkedHashMap<String, String> hmapSavedCompetitrData;
    String tagToSet;
    boolean isRetailerAllowed;
    private CustomKeyboard mCustomKeyboard;

    public static class ViewHolder {
        public ImageView img_prdct;
        public CheckBox tv_prdctName;
        public EditText et_stock;
        public TextView tvProductName;
    }

    private static final String TAG = ImageProductAdapter.class.getSimpleName();

    public ImageProductAdapter(Context context, ArrayList<String> productIdTag, LinkedHashMap<String, String> hmapPrdctImgPath, String tagToSet, LinkedHashMap<String, String> hmapSavedCompetitrData, boolean isRetailerAllowed, CustomKeyboard customKeyboard) {
        this.context = context;
        itemsName = new ArrayList<Bitmap>();
        this.productIdTag = productIdTag;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hmapPrdctImgPath = hmapPrdctImgPath;
        this.tagToSet = tagToSet;
        this.hmapSavedCompetitrData = hmapSavedCompetitrData;
        this.isRetailerAllowed = isRetailerAllowed;
        this.mCustomKeyboard = customKeyboard;
        Log.d(TAG, "hmapSavedCompetitrData  :" + hmapSavedCompetitrData.toString());
        Log.d(TAG, "productIdTag :" + productIdTag.toString());

    }

    public void add(int location, Bitmap imageValues, String imagename) {
        itemsName.add(location, imageValues);
        productIdTag.add(location, imagename);
        picPostion = location;
        notifyDataSetChanged();
    }

    public void remove(Bitmap bmp) {

        itemsName.remove(bmp);
        notifyDataSetChanged();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            // get layout from mobile.xml
            convertView = inflater.inflate(R.layout.prdct_feedback_row, null);
            holder = new ViewHolder();
            holder.img_prdct = (ImageView) convertView.findViewById(R.id.img_prdct);
            holder.tv_prdctName = (CheckBox) convertView.findViewById(R.id.tv_prdctName);
            holder.et_stock = (EditText) convertView.findViewById(R.id.et_Compi_Stock);
            holder.tvProductName = (TextView) convertView.findViewById(R.id.tv_product_name);
         //   holder.tvProductName.setMovementMethod(new ScrollingMovementMethod());
            holder.tv_prdctName.setTag(picPostion);

            chkdUnchkdPrdct = (CheckedUnchekedCmpttrProduct) context;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // holder.img_prdct.setImageBitmap(itemsName.get(position));
        // final Bitmap info = itemsName.get(position);
        // final String productIdDelPhotoDetail=productIdTag.get(position);
        // holder.img_prdct.setTag(productIdTag.get(position));
        holder.tv_prdctName.setText(productIdTag.get(position).split(Pattern.quote("^"))[1]);
        holder.tv_prdctName.setTag(productIdTag.get(position) + "~" + tagToSet);
        holder.tvProductName.setText(productIdTag.get(position).split(Pattern.quote("^"))[1]);
        holder.et_stock.setTag(productIdTag.get(position) + "~" + tagToSet);

       /* holder.et_stock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCustomKeyboard.registerEditText((EditText) v);
                mCustomKeyboard.showCustomKeyboard(v);
                return true;
            }
        });*/

        holder.et_stock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCustomKeyboard.registerEditText((EditText) v);
                    mCustomKeyboard.showCustomKeyboard(v);
                } else if (!hasFocus) {
                    mCustomKeyboard.hideCustomKeyboard();
                }
            }
        });

        holder.et_stock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomKeyboard.registerEditText((EditText) v);
                mCustomKeyboard.showCustomKeyboard(v);
            }
        });

        if (hmapSavedCompetitrData.containsKey(holder.et_stock.getTag())) {
            holder.et_stock.setText(hmapSavedCompetitrData.get(holder.et_stock.getTag()).split(Pattern.quote("^"))[1]);
        }

        if (hmapPrdctImgPath.containsKey(productIdTag.get(position).split(Pattern.quote("^"))[0])) {
            holder.img_prdct.setTag(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0]));
            Uri uri = Uri.fromFile(new File(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0])));

            Picasso.get().load(uri).resize(96, 96).error(R.drawable.image_not_found).centerCrop().into(holder.img_prdct, new Callback() {
                @Override
                public void onSuccess() {
                    if (isRetailerAllowed) {
                        holder.img_prdct.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                String filePathName = v.getTag().toString();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    if (filePathName.contains("file:")) {
                                        filePathName = filePathName.replace("file:", "");
                                    }

                                    File file = new File(filePathName);
                                    Uri intentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                                    intent.setDataAndType(intentUri, "image/*");
                                    context.startActivity(intent);

                                } else {

                                    //	Uri intentUri = Uri.parse(filePathName);

                                    Uri intentUri = Uri.fromFile(new File(filePathName));
                                    intent.setDataAndType(intentUri, "image/*");
                                    context.startActivity(intent);
                                }

                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    holder.img_prdct.setOnClickListener(null);

                }
            });
			/*Picasso.with(context).load(uri)
					.resize(96, 96).centerCrop().into(holder.img_prdct);
			holder.img_prdct.setTag(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0]));*/
           /* Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0])),120,120);
            if(bitmap!=null)
            {
                holder.img_prdct.setImageBitmap(bitmap);

            }*/
        }
        System.out.println("Picture Bitmap " + position);


        if (isRetailerAllowed) {

            if ((hmapSavedCompetitrData != null) && (hmapSavedCompetitrData.containsKey(productIdTag.get(position) + "~" + tagToSet))) {
                holder.tv_prdctName.setChecked(true);
            }
            holder.tv_prdctName.setEnabled(true);
            holder.tv_prdctName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    if (checkBox != null)
                        if (checkBox.isChecked()) {
                            //chkdUnchkdPrdct.checkedUncheckedPrdct(true, checkBox.getTag().toString());
                        } else {
                            //chkdUnchkdPrdct.checkedUncheckedPrdct(false, checkBox.getTag().toString());
                        }

                }
            });
            holder.et_stock.setEnabled(true);
            holder.et_stock.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!s.toString().trim().equals("")) {
                        chkdUnchkdPrdct.checkedUncheckedPrdct(true, holder.et_stock.getTag().toString(), s.toString());
                        // String[] arr = (holder.et_stock.getTag().toString() + "^" + holder.et_stock.getText()).split(Pattern.quote("^"));
                    } else {
                        chkdUnchkdPrdct.checkedUncheckedPrdct(false, holder.et_stock.getTag().toString(), s.toString());
                    }
                }
            });

        } else {

            holder.tv_prdctName.setEnabled(false);
            holder.tv_prdctName.setOnClickListener(null);
            holder.et_stock.setEnabled(false);
            holder.img_prdct.setOnClickListener(null);
        }


        return convertView;
    }


    @Override
    public int getCount() {
        return productIdTag.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}