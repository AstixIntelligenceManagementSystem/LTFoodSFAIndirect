package project.astix.com.ltfoodsfaindirect;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.astix.Common.CommonInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

public class InvoiceProductList  extends Activity implements OnItemSelectedListener, DatePickerDialog.OnDateSetListener,InterfaceClass
{
LinearLayout ll_cancleremakrs;
	LinkedHashMap<String,String> hmapRsnForCncl=new LinkedHashMap<String,String>();
	ArrayList<String> listRsnForCncl=new ArrayList<String>();
	String idSelectedRsn="0";
	public EditText et_Reason;
	Spinner spnrCnclRsn;
	View alertLayout;
	public String imei;
	public String StoreName;
	public String currSysDate;
	public String pickerDate; 
	public TableLayout tl2;
	LinearLayout ll_parentOfAllProduct;
	//public ProgressDialog pDialogSync;
	public String[] CATID;
	public String[] CATIDFomProduct;
	 public String[] CATDesc;
	 public Spinner category_spinner;
	 int spinnerDistSlctd;
	int spinnerRouteSelected;
	 public String fullFileName1;
	 public long syncTIMESTAMP;
	 
	 public String[] PID;
		public String[] PName;
	Button But_Conform_Select_Invoice;
		//image
		Button But_Cancel_Select,btn_clickImage;
	float mDist=0;
	String uriStringPath="";
	ImageView flashImage;
	private boolean cameraFront = false;
	private boolean isLighOn = false;
	private Button capture,cancelCam, switchCamera;
	private Camera mCamera;
	private CameraPreview mPreview;
	private Camera.PictureCallback mPicture;
	Dialog dialog;
	private LinearLayout cameraPreview,recycler_view_images;
	String globalImageName=   "";
	String globalImagePath=      "" ;
	String userName,imageName,imagButtonTag,onlyDate;
	Uri uriSavedImage;
	String clickedTagPhoto;
	View viewStoreLocDetail;
	LinearLayout ll_ParentOfImages;
	EditText Et_invoiceNo;
	TextView tv_invoiceDate,tv_Date;
	Calendar calendar ;
	DatePickerDialog datePickerDialog ;
	int Year, Month, Day ;
	LinkedHashMap<String,String> hashMapImages=new LinkedHashMap<String,String>();
		//end camera
		public String[] rt;
		 public String[] Oqty;
		
		 public String[] delvQty;
		 public String[] delvfreeQty;
		 public String[] delvDiscount;
		 
		 
		public String[] ProductID;
		public String[] ProductName;
		public String[] rate;
		public String[] OrderQty;
		public String[] ProdpSampleQty;
		public String[] PDisplayUnit;
		public String[] pDisplayUnit;
		
		
		
		public String[] DeliverQty;
		public String[] FreeQty;
		public String[] DiscountValue;
		
		
		public String selected_Competitor_id="0";
		public CheckBox chkIos;
		
	 DBAdapterKenya dbengine = new DBAdapterKenya(this); 
	 
	 public String[] pName;
		
		public String[] LODQTY;
		public Double[] rte;
		public int[] stk;
		public int[] oQty;
		public int[] DelQty;
		public int[] fQty;
		public Double[] dVal;
	public String[] LineValuearray;
		
		ImageView storeBackDet;
		//Button storeSubmit;
		//Button storeSaveOnly;//Only Save
		public int syncClick = 0;
		String StoreID="0";
		String activityFrom="";
		public String SelectStoreTag;
		
		
		public String TagStoreID;
		public String TagOrderID;
		public String TagDistID;
		public String TagRouteID;
		public String TagDate;
		
		public TableRow dataRow;
	public View dataRowNew;
		
		InvoiceDatabaseAssistant DA = new InvoiceDatabaseAssistant(this); 
		
	 public String Storename;
	 public String Noti_text="Null";
		public int MsgServerID=0;
		
		public EditText etadditionalDiscountValue;
	 
	 
	 
	 @Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			try
			{

			dbengine.open();
			String Noti_textWithMsgServerID=dbengine.fetchNoti_textFromtblNotificationMstr();
			dbengine.close();
			System.out.println("Sunil Tty Noti_textWithMsgServerID :"+Noti_textWithMsgServerID);
			if(!Noti_textWithMsgServerID.equals("Null"))
			{
			StringTokenizer token = new StringTokenizer(String.valueOf(Noti_textWithMsgServerID), "_");
			
			MsgServerID= Integer.parseInt(token.nextToken().trim());
			Noti_text= token.nextToken().trim();
			
			
			if(Noti_text.equals("") || Noti_text.equals("Null"))
			{
				
			}
			else
			{
				
				

				 final AlertDialog builder = new AlertDialog.Builder(InvoiceProductList.this).create();
			       

					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        View openDialog = inflater.inflate(R.layout.custom_dialog, null);
			        openDialog.setBackgroundColor(Color.parseColor("#ffffff"));
			        
			        builder.setCancelable(false);
			     	TextView header_text=(TextView)openDialog. findViewById(R.id.txt_header);
			     	final TextView msg=(TextView)openDialog. findViewById(R.id.msg);
			     	
					final Button ok_but=(Button)openDialog. findViewById(R.id.but_yes);
					final Button cancel=(Button)openDialog. findViewById(R.id.but_no);
					
					cancel.setVisibility(View.GONE);
				    header_text.setText("Alert");
				     msg.setText(Noti_text);
				     	
				     	ok_but.setText("OK");
				     	
						builder.setView(openDialog,0,0,0,0);

				        ok_but.setOnClickListener(new OnClickListener() 
				        {
							
							@Override
							public void onClick(View arg0) 
							{
								// TODO Auto-generated method stub

								long syncTIMESTAMP = System.currentTimeMillis();
								Date dateobj = new Date(syncTIMESTAMP);
								SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
								String Noti_ReadDateTime = df.format(dateobj);
					    	
								dbengine.open();
								dbengine.updatetblNotificationMstr(MsgServerID,Noti_text,0,Noti_ReadDateTime,3);
								dbengine.close();
								
								try
								{
									dbengine.open();
								    int checkleftNoti=dbengine.countNumberOFNotificationtblNotificationMstr();
								    if(checkleftNoti>0)
								    {
									    	String Noti_textWithMsgServerID=dbengine.fetchNoti_textFromtblNotificationMstr();
											System.out.println("Sunil Tty Noti_textWithMsgServerID :"+Noti_textWithMsgServerID);
											if(!Noti_textWithMsgServerID.equals("Null"))
											{
												StringTokenizer token = new StringTokenizer(String.valueOf(Noti_textWithMsgServerID), "_");
												
												MsgServerID= Integer.parseInt(token.nextToken().trim());
												Noti_text= token.nextToken().trim();
												
												dbengine.close();
												if(Noti_text.equals("") || Noti_text.equals("Null"))
												{
													
												}
												else
												{
													  msg.setText(Noti_text);
												}
											}
								    	
								    }
									else
									{
										builder.dismiss();
									}
								
								}
								catch(Exception e)
								{
									
								}
					            finally
					            {
					            	dbengine.close();
								   
					            }
				            
							
							}
						});
				        
				       
				      
				 
				     	builder.show();
					
					
					

			
				 
			}
			}
			}
			catch(Exception e)
			{
				
			}
			
		}
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_productlist);

		Intent passedvals = getIntent();
		imei = passedvals.getStringExtra("imei");
		StoreName = passedvals.getStringExtra("StoreName");
		currSysDate = passedvals.getStringExtra("currSysDate");
		pickerDate = passedvals.getStringExtra("pickerDate");
		StoreID = passedvals.getStringExtra("StoreID");
		activityFrom = passedvals.getStringExtra("activityFrom");
		SelectStoreTag = passedvals.getStringExtra("SelectStoreTag");
		spinnerRouteSelected=passedvals.getIntExtra("spinnerSlctd", 0);
		spinnerDistSlctd=passedvals.getIntExtra("spnrDistSlctd", 0);

		
		System.out.println("pickerDate in InvoiceProductList :"+pickerDate);
		System.out.println("Hari singh imei recevie oncreate :"+imei);
		ll_ParentOfImages = (LinearLayout) findViewById(R.id.ll_ParentOfImages);
		btn_clickImage=(Button) findViewById(R.id.btn_clickImage);
		btn_clickImage.setTag("Camera");
		btn_clickImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickedTagPhoto=v.getTag().toString();
				openCustomCamara();
			}
		});

		tv_invoiceDate=(TextView) findViewById(R.id.tv_invoiceDate);
		tv_Date=(TextView) findViewById(R.id.tv_Date);
		tv_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calanderAllCode();
			}
		});
		Et_invoiceNo=(EditText) findViewById(R.id.Et_invoiceNo);
		
		StringTokenizer ad = new StringTokenizer(String.valueOf(SelectStoreTag), "_");
		
		TagStoreID= ad.nextToken().trim();
		TagOrderID= ad.nextToken().trim();
		TagRouteID= ad.nextToken().trim();
		TagDistID= ad.nextToken().trim();
		TagDate= ad.nextToken().trim();
		setImageToLayout();
		System.out.println("Indresh Baba ="+SelectStoreTag);
		
		TextView DistriName = (TextView)findViewById(R.id.textView1_schemeVAL1111);
		TextView StrName = (TextView)findViewById(R.id.textView1_schemeVAL);
		
		System.out.println("Dangi new  testing StoreID on Invoice ProductList :"+StoreID);
		System.out.println("Dangi new  testing SelectStoreTag on Invoice ProductList :"+SelectStoreTag);

		 tl2 = (TableLayout) findViewById(R.id.dynprodtable);
		 ll_parentOfAllProduct = (LinearLayout) findViewById(R.id.ll_parentOfAllProduct);
		 dbengine.open();
		String Distname=dbengine.FetchDistNameBasedDistID(TagDistID);
		Storename=dbengine.FetchStoreNameBasedStoreID(TagStoreID,TagDate);
		CATID=dbengine.FetchCategoryID(); 
		CATDesc=dbengine.FetchCategoryDesc();
		dbengine.close();
		hmapRsnForCncl=dbengine.getReasonToCancel();
		for(Map.Entry<String,String> entry:hmapRsnForCncl.entrySet())
		{
			listRsnForCncl.add(entry.getKey());
		}
			DistriName.setText(Distname);
			StrName.setText(Storename);
			setUpvariable();

			System.out.println("Sameer 1");
			
			if(CATID.length>0)
			{
				System.out.println("Sameer 2");
				dbengine.open();
				PID = dbengine.FetchPidInvoice(TagStoreID,TagDate,TagOrderID);
				System.out.println("Singh Testing PID one:");
				System.out.println("Singh Testing PID :"+PID.length);
				 if(PID.length>0)
					{
					 But_Conform_Select_Invoice.setEnabled(true);
					 But_Cancel_Select.setEnabled(true);
				    	//storeSaveOnly.setEnabled(true);
					}
				    else
				    {
				    	But_Conform_Select_Invoice.setEnabled(false);
				    	But_Cancel_Select.setEnabled(false);
				    	//storeSaveOnly.setEnabled(false);
				    }
				    
				System.out.println("Sameer 3");
				//PName = dbengine.FetchPNameInvoice(StoreID);
				//PName = dbengine.FetchPNameInvoice();
				rt = dbengine.FetchRateInvoice(TagStoreID,TagDate,TagOrderID);
				Oqty=dbengine.FetchOrderQtyInvoice(TagStoreID,TagDate,TagOrderID);
				delvDiscount=dbengine.FetchOrderDiscountInvoice(TagStoreID,TagDate,TagOrderID);
				
				delvfreeQty=dbengine.FetchOrderFreeQtyInvoice(TagStoreID,TagDate,TagOrderID);
				/*PID = dbengine.FetchPidInvoice();
				//PName = dbengine.FetchPNameInvoice(StoreID);
				PName = dbengine.FetchPNameInvoice();
				rt = dbengine.FetchRateInvoice();*/
				//Oqty=dbengine.FetchOrderQtyInvoice(StoreID);
				
				CATIDFomProduct=dbengine.FetchCategoryIDfromInvoiceProduct(); 
				dbengine.close();
				
				
				ProductID = new String[PID.length];
				//ProductName = new String[PName.length];
				rate = new String[rt.length];
				OrderQty= new String[Oqty.length];
				
				
				
				category_spinner=(Spinner)findViewById(R.id.competition_spinner);
				category_spinner.setOnItemSelectedListener(this);
			    ArrayAdapter aa=new ArrayAdapter(this, android.R.layout.simple_spinner_item,CATDesc);
			    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			    category_spinner.setAdapter(aa);
			  
				
				//ProdpSampleQty= new String[PName.length];
				
				ProductID = PID;
				//ProductName = PName;
				pDisplayUnit=PDisplayUnit;
				rate = rt;
				OrderQty=Oqty;
				
				
				 LayoutInflater inflater = getLayoutInflater();
				 
				 for (int current = 0; current <= (ProductID.length - 1); current++) 
			        {
						//final TableRow row = (TableRow)inflater.inflate(R.layout.invoice_table_row, tl2 , false);
						//final LinearLayout row = (TableRow)inflater.inflate(R.layout.invoice_table_row, tl2 , false);
						final   View row=getLayoutInflater().inflate(R.layout.row_invoice_product,null);
						row.setTag(CATIDFomProduct[current]);

						row.setVisibility(View.VISIBLE);
						//TextView tv1 = (TextView)row.findViewById(R.id.tvProd);
						TextView tv1 = (TextView)row.findViewById(R.id.tv_product);
						
						//final EditText et1 = (EditText)row.findViewById(R.id.tvRate);
						final EditText et1 = (EditText)row.findViewById(R.id.et_rate);

						//final TextView tv2 = (TextView)row.findViewById(R.id.etOrderQty);
						final TextView tv2 = (TextView)row.findViewById(R.id.tv_order_qty);
						//final EditText et2 = (EditText)row.findViewById(R.id.etDeliverValue);
						final EditText et2 = (EditText)row.findViewById(R.id.et_del_qty);
						final EditText et3 = (EditText)row.findViewById(R.id.tvFreeQty);
						final EditText et4 = (EditText)row.findViewById(R.id.tvDiscountVal);
						final TextView linevalue = (TextView)row.findViewById(R.id.tv_Line_value);;

						dbengine.open();
						String PName=dbengine.FetchPNameInvoice(ProductID[current]);
						dbengine.close();
						tv1.setTag(current);
						tv1.setText(PName);
						//tv1.setTextSize(12);
						et1.setTag(current);

						//et1.setTextSize(12);
						et1.addTextChangedListener(new TextWatcher() {
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								Double delveryQtyDouble=0.0;
								if(et2.getText().toString().trim().equals("") || et2.getText().toString().trim().equals(".")){

								}
								else {
									delveryQtyDouble=Double.parseDouble(et2.getText().toString().trim());
								}
								Double	rateDouble=0.0;
								if(s.toString().trim().equals("") || s.toString().trim().equals(".")){
									String linevalueDouble=new DecimalFormat("##.##").format(rateDouble*delveryQtyDouble);
									linevalue.setText(String.format("%.2f", Double.parseDouble(linevalueDouble)));
								}
								else {
								   rateDouble=Double.parseDouble(s.toString().trim());
									String linevalueDouble=new DecimalFormat("##.##").format(rateDouble*delveryQtyDouble);
									linevalue.setText(String.format("%.2f", Double.parseDouble(linevalueDouble)));


								}
							}

							@Override
							public void afterTextChanged(Editable s) {

							}
						});
						et2.addTextChangedListener(new TextWatcher() {
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								Double rateDouble=0.0;
								if(et1.getText().toString().trim().equals("") || et1.getText().toString().trim().equals(".")){

								}
								else {
									rateDouble=Double.parseDouble(et1.getText().toString().trim());
								}
								Double	delQtyDouble=0.0;
								if(s.toString().trim().equals("") || s.toString().trim().equals(".")){
									String linevalueDouble=new DecimalFormat("##.##").format(rateDouble*delQtyDouble);
									linevalue.setText(String.format("%.2f", Double.parseDouble(linevalueDouble)));
								}
								else {
									delQtyDouble=Double.parseDouble(s.toString().trim());
									String linevalueDouble=new DecimalFormat("##.##").format(rateDouble*delQtyDouble);
									linevalue.setText(String.format("%.2f", Double.parseDouble(linevalueDouble)));


								}
							}

							@Override
							public void afterTextChanged(Editable s) {

							}
						});

						et2.setText(OrderQty[current]);
						//et2.setTextSize(12);
						
						et3.setText(delvfreeQty[current]);
						//et3.setTextSize(12);
						Double rateDouble=0.0;
						if(rate[current].equals("") || rate[current].equals(".")){

						}
						else {
							rateDouble=Double.parseDouble(rate[current]);
						}
						et1.setText(String.format("%.2f", Double.parseDouble(new DecimalFormat("##.##").format(rateDouble))));
						Double delveryQtyDouble=0.0;
						if(OrderQty[current].equals("") || OrderQty[current].equals(".")){

						}
						else {
							delveryQtyDouble=Double.parseDouble(OrderQty[current]);
						}
						String linevalueDouble=new DecimalFormat("##.##").format(rateDouble*delveryQtyDouble);

						linevalue.setText(String.format("%.2f", Double.parseDouble(linevalueDouble)));
						et4.setText(delvDiscount[current]);
						//et4.setTextSize(12);
						
						

						
						tv2.setText(OrderQty[current]);
						tv2.setTextSize(12);
						ll_parentOfAllProduct.addView(row);
						
			        }
				
				
				
				
				
				
			}
	}
	
	public void setUpvariable()
	{


		et_Reason = (EditText) findViewById(R.id.et_CancelationReason);
		spnrCnclRsn= (Spinner) findViewById(R.id.spnrCnclRsn);
		ArrayAdapter adapterRsnCncl = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listRsnForCncl);
		adapterRsnCncl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spnrCnclRsn.setAdapter(adapterRsnCncl);
		et_Reason.setVisibility(View.VISIBLE);
		et_Reason.setText("");

		etadditionalDiscountValue=(EditText)findViewById(R.id.tvadditionalDiscountValue);
		
		But_Conform_Select_Invoice=(Button) findViewById(R.id.But_Conform_Select_Invoice);
		
		But_Cancel_Select=(Button) findViewById(R.id.But_Cancel_Select);
		//Button But_Submit=(Button) findViewById(R.id.But_Submit);
		spnrCnclRsn.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedReason=spnrCnclRsn.getSelectedItem().toString();
				 idSelectedRsn=hmapRsnForCncl.get(selectedReason);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		But_Conform_Select_Invoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				int picsCHK = 0;//dbengine.getExistingPicNos(fStoreID);
				//dbengine.close();
				if(Validation()){
					if(picsCHK <= 0 && isOnline() ){

						showSubmitConfirm();
					}
					else{

						showNoConnAlert();

					}
				}

				
			}
		});
		
		
		But_Cancel_Select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub



				AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(InvoiceProductList.this);
				alertDialogNoConn.setTitle("Information");
				alertDialogNoConn.setMessage("Are you sure you want to cancel Order for Following Store: "+Storename);

				alertDialogNoConn.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which)
							{


									String InvNumber="0";
									String InvDate="NA";

								dbengine.UpdateProductCancelStoreFlag(TagOrderID.trim(),1);
								dbengine.open();
								dbengine.saveInvoiceButtonStoreTransac("NA",TagDate,TagStoreID,"0","0",0.0, 0,
										0, 0,TagOrderID,"","9",1,0.0,TagRouteID,"0",et_Reason.getText().toString().trim(),idSelectedRsn,InvNumber,InvDate,"0.0");

									dbengine.deletetblExecutionImages(TagStoreID,TagOrderID);




								Intent fireBackDetPg = new Intent(InvoiceProductList.this, InvoiceStoreSelection.class);


								fireBackDetPg.putExtra("imei", imei);
								fireBackDetPg.putExtra("currSysDate", currSysDate);
								fireBackDetPg.putExtra("pickerDate", pickerDate);
								fireBackDetPg.putExtra("spinnerSlctd", spinnerRouteSelected);
								fireBackDetPg.putExtra("spnrDistSlctd", spinnerDistSlctd);
								fireBackDetPg.putExtra("activityFrom", activityFrom);

								startActivity(fireBackDetPg);
								finish();
							}
						});
				alertDialogNoConn.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
								//chkIos.setChecked(false);

							}
						});
				alertDialogNoConn.setIcon(R.drawable.info_ico);
				AlertDialog alert = alertDialogNoConn.create();
				alert.show();
				return;


				// Setting Positive "Yes" Button

	          
				  
				 
		                
				
		 
			  
				
			}
		});
	
		
		
		storeBackDet = (ImageView)findViewById(R.id.btn_bck);
	   
	  //  storeSaveOnly = (Button)findViewById(R.id.button33);//Only Save
	    
	   
		storeBackDet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent fireBackDetPg = new Intent(InvoiceProductList.this, InvoiceStoreSelection.class);
				
			/*	fireBackDetPg.putExtra("SID", fStoreID);
				fireBackDetPg.putExtra("SN", SN);
				fireBackDetPg.putExtra("bck", 1);*/
				
				fireBackDetPg.putExtra("imei", imei);
				fireBackDetPg.putExtra("currSysDate", currSysDate);
				fireBackDetPg.putExtra("pickerDate", pickerDate);
				fireBackDetPg.putExtra("spinnerSlctd", spinnerRouteSelected);
				fireBackDetPg.putExtra("spnrDistSlctd", spinnerDistSlctd);
				fireBackDetPg.putExtra("activityFrom", activityFrom);
				startActivity(fireBackDetPg);
				finish();
			}
		});
		
		
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		int index=0;
		  LayoutInflater inflater = getLayoutInflater();
		  index=category_spinner.getSelectedItemPosition();
		 selected_Competitor_id=CATID[index];
		 
		 
		 //LayoutInflater inflater = getLayoutInflater();
		 
		 for (int current = 0; current <= (ProductID.length - 1); current++) 
	        {
			 //	dataRow = (TableRow)tl2.getChildAt(current);
				dataRowNew = ll_parentOfAllProduct.getChildAt(current);
				//final TableRow row = (TableRow)inflater.inflate(R.layout.table_row, tl2 , false);
				//row.setTag(CATID[current]);
			 	
			 	if(index ==0)
			 	{
			 		//dataRow.setVisibility(View.VISIBLE);
					dataRowNew.setVisibility(View.VISIBLE);
			 		selected_Competitor_id="0";
			 	}
			 	else
			 	{
					if(Integer.parseInt(selected_Competitor_id)==Integer.parseInt(dataRowNew.getTag().toString()))
					{
						dataRowNew.setVisibility(View.VISIBLE);
					}
					else
					{
						dataRowNew.setVisibility(View.GONE);
					}
			 	}
				
	        }
	

       
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			//if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	public void showNoConnAlert() 
	 {
			AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(InvoiceProductList.this);
			alertDialogNoConn.setTitle(R.string.genTermNoDataConnection);
			alertDialogNoConn.setMessage(R.string.genTermNoDataConnectionFullMsg);
			//alertDialogNoConn.setMessage(getText(R.string.connAlertErrMsg));
			alertDialogNoConn.setNeutralButton(R.string.txtOk,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) 
						{
                      dialog.dismiss();
                      
                      //finish();
						}
					});
			alertDialogNoConn.setIcon(R.drawable.error_ico);
			AlertDialog alert = alertDialogNoConn.create();
			alert.show();
			// alertDialogLowbatt.show();
		}
	public void showSubmitConfirm() {
		AlertDialog.Builder alertDialogSubmitConfirm = new AlertDialog.Builder(InvoiceProductList.this);
		alertDialogSubmitConfirm.setTitle(R.string.genTermInformation);
		alertDialogSubmitConfirm.setMessage(getText(R.string.genTernStoreOrderInvoicesubmitConfirmAlert));
		alertDialogSubmitConfirm.setCancelable(false);
		
		alertDialogSubmitConfirm.setNeutralButton(R.string.txtYes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						syncClick = 1;
						
						//storeSubmit.setEnabled(false);
						//storeSave4Later.setEnabled(false);
						//storeSaveOnly.setEnabled(false);
						//storeBackDet.setEnabled(false);
						
						String additionalDiscountValue="0";
						
						if(TextUtils.isEmpty(etadditionalDiscountValue.getText().toString().trim()))
						{
							additionalDiscountValue="0";
						}
						else
						{
							additionalDiscountValue=etadditionalDiscountValue.getText().toString().trim();
						}
						
						
						
				       
					//	dialog.dismiss();
						
						dbengine.open();
						// change by sunil
						dbengine.deleteOldInvoiceButtonStoreTransac(StoreID);
						//dbengine.deleteOldStoreInvoice(fStoreID);
						dbengine.close();
						String InvNumber="0";
						String InvDate="NA";
						InvNumber=	Et_invoiceNo.getText().toString().trim();
						InvDate=tv_invoiceDate.getText().toString().trim();
						//for(int countRow = 0; countRow <= tl2.getChildCount()-1; countRow++){
						for (int countRow = 0; countRow <= (ProductID.length - 1); countRow++)
						{
							
							//int haveRows = tl2.getChildCount();
							int haveRows = ll_parentOfAllProduct.getChildCount();

							//String CurrentRowCategoryId=(String)((TableRow)tl2.getChildAt(countRow)).getTag().toString();
							String CurrentRowCategoryId=(String)(ll_parentOfAllProduct.getChildAt(countRow)).getTag().toString();
							pName = new String[haveRows];
							
							rte = new Double[haveRows];
							
							oQty = new int[haveRows];
							DelQty = new int[haveRows];
							fQty = new int[haveRows];
							dVal=new Double[haveRows];
							LineValuearray=new String[haveRows];
							
							//TextView child = (TextView)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.tvProd);//(TextView)dataRow.getChildAt(0);
							TextView child = (TextView)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.tv_product);
							pName[countRow] = child.getText().toString().trim();
							
									
							//EditText child2 = (EditText)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.tvRate);//(TextView)dataRow.getChildAt(2);
							EditText child2 = (EditText)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.et_rate);
							rte[countRow] = Double.parseDouble(child2.getText().toString().trim());
							
							
							//TextView child3 =(TextView)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.etOrderQty);// (EditText)dataRow.getChildAt(4);
							TextView child3 =(TextView)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.tv_order_qty);
							if(!child3.getText().toString().isEmpty()){
								oQty[countRow] = Integer.parseInt(child3.getText().toString().trim());
							}
							else{
								oQty[countRow] = 0;
							}
							
							//EditText child4 = (EditText)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.etDeliverValue);//(TextView)dataRow.getChildAt(5);
							EditText child4 = (EditText)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.et_del_qty);
							if(!child4.getText().toString().isEmpty()){
								DelQty[countRow] = Integer.parseInt(child4.getText().toString().trim());
							}
							else{
								DelQty[countRow] = 0;
							}
							
							//EditText child5 = (EditText)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.tvFreeQty);//(TextView)dataRow.getChildAt(6);
							EditText child5 = (EditText)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.tvFreeQty);
							if(!child5.getText().toString().isEmpty()){
								fQty[countRow] = Integer.parseInt(child5.getText().toString().trim());
							}
							else{
								fQty[countRow] = 0;
							}
							
							//EditText child6 = (EditText)((TableRow)tl2.getChildAt(countRow)).findViewById(R.id.tvDiscountVal);//(TextView)dataRow.getChildAt(6);
							EditText child6 = (EditText)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.tvDiscountVal);
							if(!child6.getText().toString().isEmpty()){
								dVal[countRow] = Double.parseDouble(child6.getText().toString().trim());
							}
							else{
								dVal[countRow] = 0.0;
							}
							TextView tv_Line_value = (TextView)(ll_parentOfAllProduct.getChildAt(countRow)).findViewById(R.id.tv_Line_value);
							if(!tv_Line_value.getText().toString().isEmpty()){
								LineValuearray[countRow] = tv_Line_value.getText().toString().trim();
							}
							else{
								LineValuearray[countRow] = "0.0";
							}

							dbengine.open();
							// change by sunil
							//String OrderID=dbengine.FetchOrderIDInvoice(ProductID[countRow]);
							//String OrderID=dbengine.FetchOrderIDInvoiceStoremstr(StoreID);
							
							//TagStoreID= ad.nextToken().trim();
							//TagOrderID= ad.nextToken().trim();
							
							dbengine.saveInvoiceButtonStoreTransac(imei,TagDate,
									TagStoreID,ProductID[countRow],pName[countRow],rte[countRow], oQty[countRow],
									DelQty[countRow], fQty[countRow],TagOrderID,CurrentRowCategoryId,"9",0,dVal[countRow],TagRouteID,additionalDiscountValue,et_Reason.getText().toString().trim(),idSelectedRsn,InvNumber,InvDate,LineValuearray[countRow]);
							//dbengine.saveStoreTransac(imei, pickerDate, StoreID, ProductID[countRow], stk[countRow], oQty[countRow], oVal[countRow], fQty[countRow], dVal[countRow], AppliedSchemeID, AppliedSlab, AppliedAbsVal, newSampleQty, pName[countRow], rte[countRow],CurrentRowCategoryId);//, DisplayName
							dbengine.close();
						}
						
						
						
						dbengine.UpdateProductCancelStoreFlag(TagOrderID,0);
						dbengine.open();
						dbengine.deletetblExecutionImages(TagStoreID,TagOrderID);
if((hashMapImages!=null) && (hashMapImages.size()>0)){
	for(Map.Entry<String, String> entry:hashMapImages.entrySet())
	{
		String imageName= entry.getKey().trim();
		String imagePath= entry.getValue().trim();
		dbengine.insertExecutionImagesTable(TagStoreID,TagOrderID,imageName,imagePath,3,InvNumber,InvDate);
	}
}

						dbengine.close();


						Intent fireBackDetPg = new Intent(InvoiceProductList.this, InvoiceStoreSelection.class);
						
						
						fireBackDetPg.putExtra("imei", imei);
						fireBackDetPg.putExtra("currSysDate", currSysDate);
						fireBackDetPg.putExtra("pickerDate", pickerDate);
						fireBackDetPg.putExtra("spinnerSlctd", spinnerRouteSelected);
						fireBackDetPg.putExtra("spnrDistSlctd", spinnerDistSlctd);
						fireBackDetPg.putExtra("activityFrom", activityFrom);
						
						startActivity(fireBackDetPg);
						finish();
						
						// change by sunil
						/*try {
							//new FullSyncDataNow().execute().get();
							
							FullSyncDataNow task = new FullSyncDataNow(InvoiceProductList.this);
							 task.execute();
						   
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						
						//
					
					}
				});
		
		alertDialogSubmitConfirm.setNegativeButton(R.string.txtNo, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				

			}
		});
		
		alertDialogSubmitConfirm.setIcon(R.drawable.info_ico);
		
		AlertDialog alert = alertDialogSubmitConfirm.create();
		
		 /*Window window = alert.getWindow();
		 window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		 window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		 window.setBackgroundDrawableResource(android.R.color.darker_gray);*/
	     
		alert.show();
		
	}
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		  // TODO Auto-generated method stub
		  if(keyCode==KeyEvent.KEYCODE_BACK){
		   return true;
		  }
		  if(keyCode==KeyEvent.KEYCODE_HOME){
		   return true;
		  }
		  if(keyCode==KeyEvent.KEYCODE_MENU){
			  return true;
		  }
		  if(keyCode==KeyEvent.KEYCODE_SEARCH){
			  return true;
		  }

		  return super.onKeyDown(keyCode, event);
		 }
	 
	/* private class FullSyncDataNow extends AsyncTask<Void, Void, Void> {
			

		 ProgressDialog pDialogGetStores;
			public FullSyncDataNow(InvoiceProductList activity) 
			{
				pDialogGetStores = new ProgressDialog(activity);
			}
		 
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				pDialogSync = ProgressDialog.show(InvoiceProductList.this,getText(R.string.genTermPleaseWaitNew),getText(R.string.genTernSubmittingOrderDetails), true);
				pDialogSync.setIndeterminate(true);
				pDialogSync.setCancelable(false);
				pDialogSync.setCanceledOnTouchOutside(false);
				
				//getWindow().addFlags(WindowManager.LayoutParams.FLAG_);
				 Window window = pDialogSync.getWindow();
				 window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				 window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			     //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
			     window.setBackgroundDrawableResource(android.R.color.background_dark);
			     
			   pDialogSync.show();
			   
			     //window.requestFeature(window.FEATURE_NO_TITLE);
			   
			   
			   pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
				pDialogGetStores.setMessage("Submitting Order Details...");
				pDialogGetStores.setIndeterminate(false);
				pDialogGetStores.setCancelable(false);
				pDialogGetStores.setCanceledOnTouchOutside(false);
				pDialogGetStores.show();
			}

			@Override
			protected Void doInBackground(Void... params) {

				try {
					SyncNow();
					}

				 catch (Exception e) {
					Log.i("Sync ASync", "Sync ASync Failed!", e);

				}

				finally {

				}
				syncTIMESTAMP = System.currentTimeMillis();
				Date dateobj = new Date(syncTIMESTAMP);
				
				System.out.println("Hari singh imei :"+imei);
				
				SimpleDateFormat df = new SimpleDateFormat(imei + ".dd.MM.yyyy.HH.mm.ss",Locale.ENGLISH);
				fullFileName1 = df.format(dateobj);
				//fullFileName2 = df.format(dateobj) + "_2";
				
					String printINTERNALPATH0 = Environment.getExternalStorageDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
					String printINTERNALPATH = Environment.getDataDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
					String printINTERNALPATH2 = Environment.getRootDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
					
					 //System.out.println("printINTERNALPATH0: " + printINTERNALPATH0);
					 //System.out.println("printINTERNALPATH: " + printINTERNALPATH);
					 //System.out.println("printINTERNALPATH2: "+ printINTERNALPATH2);
				 
				
				try {
					 File MeijiIndirectSFAxmlFolder = new File(Environment.getExternalStorageDirectory(), "TJUKSFAInvoicexml");
						
					 if (!MeijiIndirectSFAxmlFolder.exists()) 
						{
						 MeijiIndirectSFAxmlFolder.mkdirs();
							 
						} 
				
					DA.open();
					DA.export(dbengine.DATABASE_NAME, fullFileName1,1);
					//DA.export("PROdb", fullFileName2);
					DA.close();
					
					pDialogSync.dismiss();
		 
					Intent syncIntent = new Intent(InvoiceProductList.this, InvoiceSyncMaster.class);
					syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/NMPdata/" + fullFileName1 + ".xml");
					syncIntent.putExtra("OrigZipFileName", fullFileName1);
					syncIntent.putExtra("whereTo", "Regular");
					
					syncIntent.putExtra("imei", imei);
					syncIntent.putExtra("currSysDate", currSysDate);
					syncIntent.putExtra("pickerDate", pickerDate);
					startActivity(syncIntent);
					

				} catch (IOException e) {
				
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			protected void onCancelled() {
				
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(pDialogGetStores.isShowing()) 
			      {
			    	   pDialogGetStores.dismiss();
				  }
				try
				{
				
				Intent syncIntent = new Intent(InvoiceProductList.this, InvoiceSyncMaster.class);
				syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/TJUKSFAInvoicexml/" + fullFileName1 + ".xml");
				syncIntent.putExtra("OrigZipFileName", fullFileName1);
				syncIntent.putExtra("whereTo", "11");
				
				syncIntent.putExtra("imei", imei);
				syncIntent.putExtra("currSysDate", currSysDate);
				syncIntent.putExtra("pickerDate", pickerDate);
				startActivity(syncIntent);
				finish();
				

			} catch (Exception e) {
			
				e.printStackTrace();
			}
			}
		}
	 
	 public void SyncNow(){
			
			syncTIMESTAMP = System.currentTimeMillis();
			Date dateobj = new Date(syncTIMESTAMP);
			
			System.out.println("Hari singh imei :"+imei);
			
			SimpleDateFormat df = new SimpleDateFormat(imei + ".dd.MM.yyyy.HH.mm.ss",Locale.ENGLISH);
			fullFileName1 = df.format(dateobj);
			//fullFileName2 = df.format(dateobj) + "_2";
			
				String printINTERNALPATH0 = Environment.getExternalStorageDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
				String printINTERNALPATH = Environment.getDataDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
				String printINTERNALPATH2 = Environment.getRootDirectory() + "/NMPdata/" + fullFileName1 + ".zip";
				
				 //System.out.println("printINTERNALPATH0: " + printINTERNALPATH0);
				 //System.out.println("printINTERNALPATH: " + printINTERNALPATH);
				 //System.out.println("printINTERNALPATH2: "+ printINTERNALPATH2);
			 
			
			try {
				 File MeijiIndirectSFAxmlFolder = new File(Environment.getExternalStorageDirectory(), "TJUKSFAInvoicexml");
					
				 if (!MeijiIndirectSFAxmlFolder.exists()) 
					{
					 MeijiIndirectSFAxmlFolder.mkdirs();
						 
					} 
			
				DA.open();
				DA.export(dbengine.DATABASE_NAME, fullFileName1,1);
				//DA.export("PROdb", fullFileName2);
				DA.close();
				
				pDialogSync.dismiss();
	 
				Intent syncIntent = new Intent(InvoiceProductList.this, InvoiceSyncMaster.class);
				syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/TJUKSFAInvoicexml/" + fullFileName1 + ".xml");
				syncIntent.putExtra("OrigZipFileName", fullFileName1);
				syncIntent.putExtra("whereTo", "Regular");
				
				syncIntent.putExtra("imei", imei);
				syncIntent.putExtra("currSysDate", currSysDate);
				syncIntent.putExtra("pickerDate", pickerDate);
				startActivity(syncIntent);
				

			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
		}
*/
//camera code starts
	public void openCamera()
	{
		InvoiceProductList.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		//arrImageData.clear();
		InvoiceProductList.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		dialog = new Dialog(InvoiceProductList.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

		//dialog.setTitle("Calculation");
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.activity_main);
		WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();


		parms.height=parms.MATCH_PARENT;
		parms.width=parms.MATCH_PARENT;
		cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

		mPreview = new CameraPreview(InvoiceProductList.this, mCamera);
		cameraPreview.addView(mPreview);
		//onResume code
		if (!hasCamera(InvoiceProductList.this)) {
			Toast toast = Toast.makeText(InvoiceProductList.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
			toast.show();

		}
		if (mCamera == null) {
			//if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(InvoiceProductList.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
				switchCamera.setVisibility(View.GONE);
			}

			//mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//for back camera

			//---------------------------for selfie camera uncomment below code and comment  above line----------------
           /* if (findFrontFacingCamera() < 0)
            {
                mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            else
            {
                mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }*/

			/*if(mCamera==null){
				mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}*/

			boolean isParameterSet=false;
			try {
				Camera.Parameters params= mCamera.getParameters();


				List<Camera.Size> sizes = params.getSupportedPictureSizes();
				Camera.Size size = sizes.get(0);
//Camera.Size size1 = sizes.get(0);
				for(int i=0;i<sizes.size();i++)
				{

					if(sizes.get(i).width > size.width)
						size = sizes.get(i);


				}

//System.out.println(size.width + "mm" + size.height);

				params.setPictureSize(size.width, size.height);
				params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				//	params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
				params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

				//	params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

				isLighOn = false;
				int minExpCom=params.getMinExposureCompensation();
				int maxExpCom=params.getMaxExposureCompensation();

				if( maxExpCom > 4 && minExpCom < 4)
				{
					params.setExposureCompensation(4);
				}
				else
				{
					params.setExposureCompensation(0);
				}
				params.setAutoExposureLock(false);
				params.setAutoWhiteBalanceLock(false);
				//String supportedIsoValues = params.get("iso-values");
				// String newVAlue = params.get("iso");
				//  params.set("iso","1600");
				params.setColorEffect("none");
				params.set("scene-mode","auto");


				params.setPictureFormat(ImageFormat.JPEG);
				params.setJpegQuality(70);
				params.setRotation(90); //for back camera
				//  params2.setRotation(270); //for selfie camera


				mCamera.setParameters(params);
				isParameterSet=true;
			}
			catch (Exception e)
			{

			}
			if(!isParameterSet)
			{
				Camera.Parameters params2= mCamera.getParameters();
				params2.setPictureFormat(ImageFormat.JPEG);
				params2.setJpegQuality(70);
				params2.setRotation(90); //for back camera
				//  params2.setRotation(270); //for selfie camera

				mCamera.setParameters(params2);
			}



//for selfie use it
           /* if (findFrontFacingCamera() < 0)
            {
                setCameraDisplayOrientation(InvoiceProductList.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
            }
            else
            {
                setCameraDisplayOrientation(InvoiceProductList.this, Camera.CameraInfo.CAMERA_FACING_FRONT,mCamera);
            }*/


			setCameraDisplayOrientation(InvoiceProductList.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
			mPicture = getPictureCallback();
			mPreview.refreshCamera(mCamera);
		}


		capture = (Button)dialog.  findViewById(R.id.button_capture);

		flashImage= (ImageView)dialog.  findViewById(R.id.flashImage);
		flashImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLighOn) {
					// turn off flash
					Camera.Parameters params = mCamera.getParameters();

					if (mCamera == null || params == null) {
						return;
					}


					params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(params);
					flashImage.setImageResource(R.drawable.flash_off);
					isLighOn=false;
				} else {

					// turn on flash
					Camera.Parameters params = mCamera.getParameters();

					if (mCamera == null || params == null) {
						return;
					}

					params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

					flashImage.setImageResource(R.drawable.flash_on);
					mCamera.setParameters(params);

					isLighOn=true;
				}
			}
		});

		final Button cancleCamera= (Button)dialog.  findViewById(R.id.cancleCamera);
		cancelCam=cancleCamera;
		cancleCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InvoiceProductList.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
				v.setEnabled(false);
				capture.setEnabled(false);
				cameraPreview.setEnabled(false);
				flashImage.setEnabled(false);

				Camera.Parameters params = mCamera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(params);
				isLighOn = false;
				dialog.dismiss();
				InvoiceProductList.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			}
		});
		capture.setOnClickListener(captrureListener);

		cameraPreview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Get the pointer ID
				Camera.Parameters params = mCamera.getParameters();
				int action = event.getAction();

				if (event.getPointerCount() > 1) {
					// handle multi-touch events
					if (action == MotionEvent.ACTION_POINTER_DOWN) {
						mDist = getFingerSpacing(event);
					} else if (action == MotionEvent.ACTION_MOVE
							&& params.isZoomSupported()) {
						mCamera.cancelAutoFocus();
						handleZoom(event, params);
					}
				} else {
					// handle single touch events
					if (action == MotionEvent.ACTION_UP) {
						handleFocus(event, params);
					}
				}
				return true;
			}
		});

		dialog.show();
		InvoiceProductList.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


	}

	public void   openCustomCamara()
	{
		if(dialog!=null)
		{
			if(!dialog.isShowing())
			{
				openCamera();


			}

		}
		else
		{
			openCamera();

		}

	}
	private static File getOutputMediaFile() {
		//make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File("/sdcard/", CommonInfo.ImagesFolder);

		//if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			//if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		//take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH).format(new Date());
		File mediaFile;
		//and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +CommonInfo.imei+ timeStamp + ".jpg");

		return mediaFile;
	}
	@Override
	public void onStop() {
		super.onStop();
		if(mCamera!=null){
			mCamera.release();
			mCamera=null;
			if(dialog!=null){
				if(dialog.isShowing()){
					dialog.dismiss();

				}
			}
		}
	}
	public Bitmap normalizeImageForUri(Context context, Uri uri) {
		Bitmap rotatedBitmap = null;

		try {

			ExifInterface exif = new ExifInterface(uri.getPath());

			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			rotatedBitmap = rotateBitmap(bitmap, orientation);
			if (!bitmap.equals(rotatedBitmap)) {
				saveBitmapToFile(context, rotatedBitmap, uri);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotatedBitmap;
	}
	private  Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();

			return bmRotated;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}
	private  void saveBitmapToFile(Context context, Bitmap croppedImage, Uri saveUri) {
		if (saveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = context.getContentResolver().openOutputStream(saveUri);
				if (outputStream != null) {
					croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
				}
			} catch (IOException e) {

			} finally {
				closeSilently(outputStream);
				croppedImage.recycle();
			}
		}
	}

	private  void closeSilently(@Nullable Closeable c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (Throwable t) {
			// Do nothing
		}
	}

	public void setSavedImageToScrollView(Bitmap bitmap,String imageValidName,String filePathImage,String clickedTagPhoto){

		if((bitmap!=null) && (imageValidName!=null) ){
			LayoutInflater inflater=(LayoutInflater) InvoiceProductList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final  View    viewStoreLocDetail=inflater.inflate(R.layout.store_loc_display,null);
			final RelativeLayout rl_photo=(RelativeLayout) viewStoreLocDetail.findViewById(R.id.rl_photo);
			final ImageView img_thumbnail=(ImageView)viewStoreLocDetail.findViewById(R.id.img_thumbnail);
			img_thumbnail.setImageBitmap(bitmap);
			img_thumbnail.setTag(filePathImage);
			img_thumbnail.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						String filePathName="";
						if(view.getTag().toString().contains("file:")){
							filePathName=view.getTag().toString().replace("file:","");
						}
						else {
							filePathName=view.getTag().toString();

						}
						File file = new File(filePathName);
						Uri intentUri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", file);
						intent.setDataAndType(intentUri, "image/*");
						startActivity(intent);

					}
					else{
						Uri intentUri = Uri.parse(view.getTag().toString());
						intent.setDataAndType(intentUri, "image/*");
						startActivity(intent);
					}


				}

			});

			final ImageView imgCncl=(ImageView) viewStoreLocDetail.findViewById(R.id.imgCncl);
			imgCncl.setTag(imageValidName);
			imgCncl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {


					String file_dj_path = img_thumbnail.getTag().toString();
					if(file_dj_path.contains("file:")){
						file_dj_path=    file_dj_path .replace("file:","");
					}
					File fdelete = new File(file_dj_path);
					if (fdelete.exists()) {
						if (fdelete.delete()) {

							callBroadCast();
						} else {

						}
					}
					if(hashMapImages.containsKey(imgCncl.getTag().toString())){
						hashMapImages.remove(imgCncl.getTag().toString());
					}
					ll_ParentOfImages.removeView(viewStoreLocDetail);
				}
			});






			if(ll_ParentOfImages!=null)
			{
				hashMapImages.put(imageValidName,filePathImage);
				ll_ParentOfImages.addView(viewStoreLocDetail);
			}


		}


	}
	public void callBroadCast() {
		if (Build.VERSION.SDK_INT >= 14) {
			Log.e("-->", " >= 14");
			MediaScannerConnection.scanFile(InvoiceProductList.this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

				public void onScanCompleted(String path, Uri uri) {

				}
			});
		} else {
			Log.e("-->", " < 14");
			InvoiceProductList.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		}
	}
	private boolean hasCamera(Context context) {
		//check if the device has camera
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}
	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}
	private int findBackFacingCamera() {
		int cameraId = -1;
		//Search for the back facing camera
		//get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		//for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}
	private void setCameraDisplayOrientation(Activity activity,
											 int cameraId, Camera camera) {
		Camera.CameraInfo info =
				new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
	private Camera.PictureCallback getPictureCallback() {
		Camera.PictureCallback picture = new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {



				//make a new picture file
				File pictureFile = getOutputMediaFile();

				Camera.Parameters params = mCamera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(params);
				isLighOn = false;

				if (pictureFile == null) {
					return;
				}
				try {
					//write the file
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();

					//Toast toast = Toast.makeText(getActivity(), "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
					//toast.show();
					//put data here

                  /*  arrImageData.add(0,pictureFile);
                    arrImageData.add(1,pictureFile.getName());*/
					dialog.dismiss();
					if(pictureFile!=null)
					{
						File file=pictureFile;
						System.out.println("File +++"+pictureFile);
						imageName=pictureFile.getName();
						normalizeImageForUri(InvoiceProductList.this, Uri.fromFile(pictureFile));


						// Convert ByteArray to Bitmap::\
						//
						uriSavedImage = Uri.fromFile(pictureFile);
						long syncTIMESTAMP = System.currentTimeMillis();
						Date dateobj = new Date(syncTIMESTAMP);
						SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
						String clkdTime = df.format(dateobj);
						//	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
						String filePathImage=uriSavedImage.toString();
						//   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);

						globalImageName=   imageName;
						globalImagePath=      uriSavedImage.toString() ;
						//
						Bitmap bitmap=null;
						try{
							String PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/";
							File file2 = new File(PATH + imageName);
							if (file2.exists()) {

								// final int THUMBSIZE = 170;//change quality
								final int THUMBSIZE = 130;
								bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(PATH + imageName),
										THUMBSIZE, THUMBSIZE);
							}

						}
						catch (Exception e){

						}
						setSavedImageToScrollView(bitmap, imageName,filePathImage,clickedTagPhoto);
					}



//Show dialog here
//...
//Hide dialog here

				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				//refresh camera to continue preview--------------------------------------------------------------
				//	mPreview.refreshCamera(mCamera);
				//if want to release camera
				if(mCamera!=null){
					mCamera.release();
					mCamera=null;
				}
			}
		};
		return picture;
	}
	View.OnClickListener captrureListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			v.setEnabled(false);
			File del = new File(Environment.getExternalStorageDirectory(), CommonInfo.ImagesFolder);

			//  checkNumberOfFiles(del);


			InvoiceProductList.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			cancelCam.setEnabled(false);
			flashImage.setEnabled(false);
			if(cameraPreview!=null)
			{
				cameraPreview.setEnabled(false);
			}

			if(mCamera!=null)
			{

				mCamera.takePicture(null, null, mPicture);
			}
			else
			{
				dialog.dismiss();
			}


			InvoiceProductList.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

		}
	};
	private float getFingerSpacing(MotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	private void handleZoom(MotionEvent event, Camera.Parameters params) {
		int maxZoom = params.getMaxZoom();
		int zoom = params.getZoom();
		float newDist = getFingerSpacing(event);
		if (newDist > mDist) {
			// zoom in
			if (zoom < maxZoom)
				zoom++;
		} else if (newDist < mDist) {
			// zoom out
			if (zoom > 0)
				zoom--;
		}
		mDist = newDist;
		params.setZoom(zoom);
		mCamera.setParameters(params);
	}
	public void handleFocus(MotionEvent event, Camera.Parameters params) {
		int pointerId = event.getPointerId(0);
		int pointerIndex = event.findPointerIndex(pointerId);
		// Get the pointer's current position
		float x = event.getX(pointerIndex);
		float y = event.getY(pointerIndex);

		List<String> supportedFocusModes = params.getSupportedFocusModes();
		if (supportedFocusModes != null
				&& supportedFocusModes
				.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			mCamera.autoFocus(new Camera.AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean b, Camera camera) {
					// currently set to auto-focus on single touch
				}
			});
		}
	}
   public void setImageToLayout(){
	   hashMapImages= dbengine.fnGetImageDataFrom_tblExecutionImages(TagStoreID,TagOrderID);
	   if(!hashMapImages.isEmpty()){
		   for(Map.Entry<String, String> entry:hashMapImages.entrySet())
		   {
			   String imageName= entry.getKey().trim();
			   String imagePath= entry.getValue().trim();
			   Bitmap bitmap=null;
			   try{
				   String PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/";
				   File file2 = new File(PATH + imageName);
				   if (file2.exists()) {

					   // final int THUMBSIZE = 170;//change quality
					   final int THUMBSIZE = 130;
					   bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(PATH + imageName),
							   THUMBSIZE, THUMBSIZE);
				   }

			   }
			   catch (Exception e){

			   }
			   setSavedImageToScrollView(bitmap, imageName,imagePath,"Camera");

		   }
	   }
   }
   public boolean Validation(){
	     /*if((btn_clickImage.getVisibility()==View.VISIBLE) && (ll_ParentOfImages.getChildCount()==0)){
		  // Toast.makeText(getApplicationContext(),"Please Click at least one Image",Toast.LENGTH_SHORT).show();
			 AlertCommonInfo("Please Click at least one Image");
		   return false;
	   }*/
	    // else  if(Et_invoiceNo.getText().toString().trim().equals("")){
	   if(Et_invoiceNo.getText().toString().trim().equals("")){
		   //Toast.makeText(getApplicationContext(),"Please Enter invoice number",Toast.LENGTH_SHORT).show();
			 AlertCommonInfo("Please Enter invoice number");
		   return false;
	   }
		 else  if(tv_invoiceDate.getText().toString().trim().equals("")){
		   //Toast.makeText(getApplicationContext(),"Please select invoice date",Toast.LENGTH_SHORT).show();
			 AlertCommonInfo("Please select invoice dater");
		   return false;
	   }
	   else{
		   return true;
	   }
   }
   public void calanderAllCode(){
	   calendar = Calendar.getInstance();
	   Year = calendar.get(Calendar.YEAR) ;
	   Month = calendar.get(Calendar.MONTH);
	   Day = calendar.get(Calendar.DAY_OF_MONTH);
	   datePickerDialog = DatePickerDialog.newInstance(InvoiceProductList.this, Year, Month, Day);

	   datePickerDialog.setThemeDark(false);

	   datePickerDialog.showYearPickerFirst(false);

	   Calendar calendarForSetDate = Calendar.getInstance();
	   calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

	   // calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
	   //YOU can set min or max date using this code
	   // datePickerDialog.setMaxDate(Calendar.getInstance());
	   // datePickerDialog.setMinDate(calendar);
	   datePickerDialog.setMaxDate(calendarForSetDate);
	   datePickerDialog.setAccentColor(Color.parseColor("#544f88"));

	   datePickerDialog.setTitle("SELECT DATE");
	   datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
   }

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String mon=MONTHS[monthOfYear];
		try{
			year=Integer.parseInt(String.valueOf(year).substring(2));
		}
		catch (Exception e){

		}

		tv_invoiceDate.setText(dayOfMonth+"-"+mon+"-"+year);
	}
	public void AlertCommonInfo(String msg)
	{
		AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(InvoiceProductList.this);
		alertDialogNoConn.setTitle(R.string.genTermNoDataConnection);
		alertDialogNoConn.setMessage(msg);
		alertDialogNoConn.setNeutralButton(R.string.txtOk,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						//finish();
					}
				});
		alertDialogNoConn.setIcon(R.drawable.info_icon);
		AlertDialog alert = alertDialogNoConn.create();
		alert.show();

	}

	@Override
	public void testFunctionOne(String fnLati, String fnLongi, String finalAccuracy, String fnAccurateProvider, String GpsLat, String GpsLong, String GpsAccuracy, String NetwLat, String NetwLong, String NetwAccuracy, String FusedLat, String FusedLong, String FusedAccuracy, String AllProvidersLocation, String GpsAddress, String NetwAddress, String FusedAddress, String FusedLocationLatitudeWithFirstAttempt, String FusedLocationLongitudeWithFirstAttempt, String FusedLocationAccuracyWithFirstAttempt, int flgLocationServicesOnOff, int flgGPSOnOff, int flgNetworkOnOff, int flgFusedOnOff, int flgInternetOnOffWhileLocationTracking, String address, String pincode, String city, String state) {

	}
}
