package project.astix.com.ltfoodsfaindirect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;


class ImageProductAdapter extends BaseAdapter
 {
	 private Context context;
	 private List<Bitmap> itemsName;
	 int imgLocation;
	 int picPostion;
	 List<String> productIdTag;
	 LayoutInflater inflater ;
     LinkedHashMap<String,String> hmapPrdctImgPath;
     CheckedUnchekedCmpttrProduct chkdUnchkdPrdct;
	 LinkedHashMap<String,String> hmapSavedCompetitrData;
    String tagToSet;
	 boolean isRetailerAllowed;
	public static class ViewHolder
	{
        public ImageView img_prdct;
        public CheckBox tv_prdctName;


    }


	public ImageProductAdapter(Context context,ArrayList<String> productIdTag,LinkedHashMap<String,String> hmapPrdctImgPath,String tagToSet,LinkedHashMap<String,String> hmapSavedCompetitrData,boolean isRetailerAllowed)
	{
		this.context = context;
		itemsName=new ArrayList<Bitmap>();
		this.productIdTag=productIdTag;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hmapPrdctImgPath=hmapPrdctImgPath;
        this.tagToSet=tagToSet;
		this.hmapSavedCompetitrData=hmapSavedCompetitrData;
		this.isRetailerAllowed=isRetailerAllowed;
		
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
			convertView = inflater.inflate(R.layout.prdct_feedback_row, null);
			holder=new ViewHolder();
			holder.img_prdct = (ImageView) convertView.findViewById(R.id.img_prdct);
			holder.tv_prdctName = (CheckBox) convertView.findViewById(R.id.tv_prdctName);
			holder.tv_prdctName.setTag(picPostion);


            chkdUnchkdPrdct=(CheckedUnchekedCmpttrProduct) context;
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
        holder.tv_prdctName.setTag(productIdTag.get(position)+"~"+tagToSet);
        if(hmapPrdctImgPath.containsKey(productIdTag.get(position).split(Pattern.quote("^"))[0]))
        {
			holder.img_prdct.setTag(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0]));
			Uri uri = Uri.fromFile(new File(hmapPrdctImgPath.get(productIdTag.get(position).split(Pattern.quote("^"))[0])));

			Picasso.get().load(uri).resize(96,96).error(R.drawable.image_not_found).centerCrop().into(holder.img_prdct, new Callback() {
				@Override
				public void onSuccess() {
					if(isRetailerAllowed)
					{
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
		 System.out.println("Picture Bitmap "+position);


		if(isRetailerAllowed)
		{

			if((hmapSavedCompetitrData!=null) && (hmapSavedCompetitrData.containsKey(productIdTag.get(position)+"~"+tagToSet)))
			{
				holder.tv_prdctName.setChecked(true);
			}
			holder.tv_prdctName.setEnabled(true);
			holder.tv_prdctName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckBox checkBox=(CheckBox)v;
					if(checkBox!=null)
						if(checkBox.isChecked())
						{
							chkdUnchkdPrdct.checkedUncheckedPrdct(true,checkBox.getTag().toString());
						}
						else
						{
							chkdUnchkdPrdct.checkedUncheckedPrdct(false,checkBox.getTag().toString());
						}

				}
			});
		}
		else
		{

			holder.tv_prdctName.setEnabled(false);
			holder.tv_prdctName.setOnClickListener(null);
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