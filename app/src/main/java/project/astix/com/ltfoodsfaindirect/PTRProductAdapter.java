package project.astix.com.ltfoodsfaindirect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;


class PTRProductAdapter extends BaseAdapter
 {
	 private Context context;
	 private List<Bitmap> itemsName;
	 int imgLocation;
	 int picPostion;
	 List<String> productIdTag;
	 LayoutInflater inflater ;
     LinkedHashMap<String,String> hmapPrdctImgPath;
	 MinMaxValidationCompttr chkdUnchkdPrdct;
	 LinkedHashMap<String,String> hmapCmpttrPrdctPTR;

	public static class ViewHolder
	{
        public ImageView img_prdct;
        public TextView tv_prdctName;
		public EditText ed_priceToRtlr;

    }


	public PTRProductAdapter(Context context, ArrayList<String> productIdTag, LinkedHashMap<String,String> hmapPrdctImgPath, LinkedHashMap<String,String> hmapCmpttrPrdctPTR)
	{
		this.context = context;
		itemsName=new ArrayList<Bitmap>();
		this.productIdTag=productIdTag;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hmapPrdctImgPath=hmapPrdctImgPath;
        this.hmapCmpttrPrdctPTR=hmapCmpttrPrdctPTR;
		
	}
	
	 public void add(int location,Bitmap imageValues,String imagename)
	 {
	        itemsName.add(location,imageValues);
	        productIdTag.add(location,imagename);
	        picPostion=location;
	        notifyDataSetChanged();
	 }
	 
	 public void remove(Bitmap bmp)
	    {
	      
	        itemsName.remove(bmp);
	        notifyDataSetChanged();
	    }


	public View getView(final int position, View convertView, ViewGroup parent) 
	{

	    final ViewHolder holder;
		

		if (convertView == null) 
		{

		    // get layout from mobile.xml
			convertView = inflater.inflate(R.layout.list_product_competitor, null);
			holder=new ViewHolder();
			holder.img_prdct = (ImageView) convertView.findViewById(R.id.img_prdct);
			holder.tv_prdctName = (TextView) convertView.findViewById(R.id.tvProdctName);
			holder.ed_priceToRtlr= (EditText) convertView.findViewById(R.id.ed_priceToRtlr);
			holder.tv_prdctName.setTag(picPostion);


            chkdUnchkdPrdct=(MinMaxValidationCompttr) context;
		    convertView.setTag(holder);
		       
		} 
		else
		{
			 holder = (ViewHolder) convertView.getTag();
		}
		
		// holder.img_prdct.setImageBitmap(itemsName.get(position));
		// final Bitmap info = itemsName.get(position);
		// final String productIdDelPhotoDetail=productIdTag.get(position);
		// holder.img_prdct.setTag(productIdTag.get(position));
		holder.tv_prdctName.setText(productIdTag.get(position).split(Pattern.quote("^"))[1]);
       // holder.tv_prdctName.setTag(productIdTag.get(position)+"~"+tagToSet);
		holder.ed_priceToRtlr.setTag(productIdTag.get(position));
        if(hmapPrdctImgPath.containsKey(productIdTag.get(position).split(Pattern.quote("^"))[0]))
        {
            Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0])),120,120);
            if(bitmap!=null)
            {
                holder.img_prdct.setImageBitmap(bitmap);
				holder.img_prdct.setTag(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0]));
            }
        }
        if((hmapCmpttrPrdctPTR!=null) && (hmapCmpttrPrdctPTR.containsKey((productIdTag.get(position).split(Pattern.quote("^"))[0]+"_PTR"))))
		{
			holder.ed_priceToRtlr.setText(hmapCmpttrPrdctPTR.get((productIdTag.get(position).split(Pattern.quote("^"))[0]+"_PTR")));
		}
		holder.img_prdct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				String filePathName=v.getTag().toString();
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					if(filePathName.contains("file:")){
						filePathName=filePathName.replace("file:","");
					}

					File file = new File(filePathName);
					Uri intentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
					intent.setDataAndType(intentUri, "image/*");
					context.startActivity(intent);

				}
				else{

					//	Uri intentUri = Uri.parse(filePathName);

					Uri intentUri = Uri.fromFile(new  File(filePathName));
					intent.setDataAndType(intentUri, "image/*");
					context.startActivity(intent);
				}

			}
		});
		holder.ed_priceToRtlr.addTextChangedListener(new MyTextWatcher(holder.ed_priceToRtlr));
		holder.ed_priceToRtlr.setOnFocusChangeListener(new MyFocusChangeListener());

		 System.out.println("Picture Bitmap "+position);


	

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

	 public class MyFocusChangeListener implements View.OnFocusChangeListener
	 {

		 @Override
		 public void onFocusChange(View v, boolean hasFocus) {
			 if(!hasFocus)
			 {
				 if(v instanceof EditText)
				 {
					 EditText ed_priceToRtlr= (EditText) v;
					 if(ed_priceToRtlr!=null)
					 {
						 if(!TextUtils.isEmpty(ed_priceToRtlr.getText().toString().trim()))
						 {
							 chkdUnchkdPrdct.edittextValPTR(ed_priceToRtlr.getText().toString().trim(),ed_priceToRtlr.getTag().toString());
						 }
					 }
				 }



			 }
			 else
			 {
				 if(v instanceof EditText)
				 {
					 EditText ed_priceToRtlr= (EditText) v;
					 chkdUnchkdPrdct.crntFocusEditext(ed_priceToRtlr,ed_priceToRtlr.getTag().toString());
				 }

			 }
		 }
	 }


	 public class MyTextWatcher implements TextWatcher {

		 private EditText ed_priceToRtlr;

		 public MyTextWatcher(EditText editText) {
			 ed_priceToRtlr = editText;
		 }

		 @Override
		 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		 }

		 @Override
		 public void onTextChanged(CharSequence s, int start, int before, int count) {
			 if (!ed_priceToRtlr.getText().toString().trim().equalsIgnoreCase("")) {


				 String editTextString = ed_priceToRtlr.getText().toString().trim();
				 if(editTextString.length()>3)
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


		 }

	 }


}