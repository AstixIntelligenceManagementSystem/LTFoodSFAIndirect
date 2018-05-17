package project.astix.com.ltfoodsfaindirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    View viewStoreLocDetail;
    LinearLayout ll_ImageToSet;
    EditText ET_mobile_credential,ET_firstname,ET_lastname ,ET_contact_no,editText_emailID;
    public int chkFlgForErrorToCloseApp=0;
    public String newfullFileName;
   ScrollView scrollViewParentOfMap;
    String SelfieNameURL="0";
    ImageView transparent_image,profile_image;
    ImageView logoutIcon;
    AlertDialog.Builder alertDialog;
    AlertDialog ad;
    SharedPreferences sharedPrefForRegistration;
    ListView listDistributor;
    String[] distributerList;
    ArrayAdapter<String> adapterDistributor;
    String[] merchantList;
    TextView spinnerCity;
    String filePathPhoto;
    File imageF;
    Uri uriSavedImage;
    String clickedTagPhoto;
    String userName,imageName,imagButtonTag,onlyDate;
    public static File imageF_savedInstance=null;
    public static String imageName_savedInstance=null;
    public static String clickedTagPhoto_savedInstance=null;
    public static Uri uriSavedImage_savedInstance=null;
    float mDist=0;
    String uriStringPath="";
    ImageView flashImage;
    private boolean cameraFront = false;
    private boolean isLighOn = false;
    private Button capture,cancelCam, switchCamera;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    signature mSignature;
    Bitmap bitmap;
    File file;
    View view;
    String     ButtonClick="";
    Dialog dialog;
    String globalImageName=   "";
    String globalImagePath=      "" ;
    private LinearLayout cameraPreview,recycler_view_images;
    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() +  "/" + CommonInfo.ImagesFolder + "/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    String personName="0";
    String StoredPath = DIRECTORY + pic_name +CommonInfo.imei+ ".png";
    LinearLayout mContent,llCamera;
    Button  mClear, mGetSign, mCancel,BtnNotYou;
    boolean signOrNot=false;
    public static String imei="";
    LinkedHashMap<String, String> hmapDsrRegAllDetails;
    Button Submit,validate_btn,CancelButton;
    DBAdapterKenya dbengine=new DBAdapterKenya(this);
    DataBaseAssistantRegistration DA=new DataBaseAssistantRegistration(this);
    String dbNameID="";
    String doNameID="";
    ImageView  backIcon;
    String PersonNameAndFlgRegistered="0";
    String   SalesAreaName="";
    String      ContactNumberFromServer="";
    TextView welcomeTextView,txt_Dob_credential,textMessage,text_UpdateNow,text_NotYou,text_Daystart,Text_Dob,CovrageArea , ContactOnWelcome , DobOnWelcome,textCoverage , textContact, textDob;
    EditText spinner_Bank;
       RelativeLayout transparentOverlay;
    LinearLayout parentOf_questionLayout,parentOf_validationLayout,parentOf_registrationformLayout;
    RadioButton rb_yes,rb_NO;
    Calendar calendar ;
    int Year, Month, Day ;
    DatePickerDialog datePickerDialog ;
    EditText ed_Name,ed_AccNo,ed_ifsc,ed_UPIID,ed_ContactNo;
    LinkedHashMap<String, String> hashmapBank;
    View convertView;
    ListView listDominantBrand;
    ArrayAdapter<String> adapterDominantBrand;
    boolean credential_dob_Bool=false;
    boolean dob_Bool=false;
    public String[] BankName;
    String userNodeIdGlobal="0";
    String userNodetypeGlobal="0";
    String FROM="";
    String mobNumberForService;
    String dobForService;

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
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
        setContentView(R.layout.registration_activity);
        Intent intent = getIntent();
        FROM= intent.getStringExtra("IntentFrom");
        if(FROM.equals("AllButtonActivity")){
           ButtonClick= intent.getStringExtra("Button");

        }
        sharedPrefForRegistration=getSharedPreferences("RegistrationValidation", MODE_PRIVATE);
        profile_image=(ImageView)findViewById(R.id.profile_image);
        getDataFromDataBase();
        LinearLayoutInitialize();
        TextViewInitialize();
        SpinnerInitialization();
        EditTextInitialization();
        RadioButtonInitialization();
        ButtonInitialize();
        fillDataToLayoutFromDataBase();



        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tManager.getDeviceId();
        if(CommonInfo.imei.equals("")){
            CommonInfo.imei=imei;
        }

        llCamera=(LinearLayout) findViewById(R.id.llCamera) ;


        ll_ImageToSet= (LinearLayout) findViewById(R.id.recycler_view_images);

        getCameraView();

        transparent_image=(ImageView)findViewById(R.id.transparent_image);
        scrollViewParentOfMap=(ScrollView)findViewById(R.id.scrollViewParentOfMap);

        transparent_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        logoutIcon=(ImageView) findViewById(R.id.logoutIcon);
        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        signatureAllCode();

        if(FROM.equals("AllButtonActivity"))
        {
            parentOf_questionLayout.setVisibility(View.GONE);
            parentOf_registrationformLayout.setVisibility(View.VISIBLE);
            Submit.setVisibility(View.VISIBLE);
            CancelButton.setVisibility(View.VISIBLE);

            if(!PersonNameAndFlgRegistered.equals("0"))
            {
                String personName=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[0];
                String FlgRegistered=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[1];



                if(FlgRegistered.equals("1")){
                    FillDsrDetailsToLayout();
                    BtnNotYou.setVisibility(View.VISIBLE);

                }
                if(FlgRegistered.equals("0")){
                    FillDsrDetailsToLayout();
                }
            }
            else{
                if(sharedPrefForRegistration.contains("FlgRegistered") && sharedPrefForRegistration.contains("SubmitOrNot")){
                    if( sharedPrefForRegistration.getString("SubmitOrNot", "").equals("1")){
                        FillDsrDetailsToLayout();
                    }

                }
            }

        }
        transparentOverlay.setVisibility(View.GONE);
    }
  /*public void  spinnerInitialization ()
  {
      merchantList=new String[5];
      merchantList[0]="Select City";
      merchantList[1]="Delhi";
      merchantList[2]="Gurgaon";
      merchantList[3]="Mumbai";
      merchantList[4]="Pune";
      adapterDistributor = new ArrayAdapter<String>(RegistrationActivity.this, R.layout.list_item, R.id.product_name, merchantList);

      spinnerCity= (TextView) findViewById(R.id.spinnerCity);
      spinnerCity.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              alertDialog = new AlertDialog.Builder(RegistrationActivity.this);
              LayoutInflater inflater = getLayoutInflater();
              convertView = (View) inflater.inflate(R.layout.activity_list, null);
              EditText inputSearch=	 (EditText) convertView.findViewById(R.id.inputSearch);
              inputSearch.setVisibility(View.GONE);
              listDistributor = (ListView)convertView. findViewById(R.id.list_view);
              listDistributor.setAdapter(adapterDistributor);
              alertDialog.setView(convertView);
              alertDialog.setTitle("CITY");
              listDistributor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      String abc=listDistributor.getItemAtPosition(position).toString().trim();
                      spinnerCity.setText(abc);
                      ad.dismiss();

                  }
              });
              ad=alertDialog.show();

          }
      });

  }*/
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
                        normalizeImageForUri(RegistrationActivity.this,Uri.fromFile(pictureFile));
                        Bitmap bmp = decodeSampledBitmapFromFile(file.getAbsolutePath(), 80, 80);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        uriSavedImage = Uri.fromFile(pictureFile);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] byteArray = stream.toByteArray();

                        // Convert ByteArray to Bitmap::\
                        //
                        long syncTIMESTAMP = System.currentTimeMillis();
                        Date dateobj = new Date(syncTIMESTAMP);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
                        String clkdTime = df.format(dateobj);
                        //	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
                        String valueOfKey=clickedTagPhoto+"~"+"1"+"~"+uriSavedImage.toString()+"~"+clkdTime+"~"+"1";
                        //   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                                byteArray.length);
                        globalImageName=   imageName;
                        globalImagePath=      uriSavedImage.toString() ;
                        //
                        setSavedImageToScrollView(bitmap, imageName,valueOfKey,clickedTagPhoto);
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


        RegistrationActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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


        RegistrationActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

    public void openCamera()
    {
        RegistrationActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //arrImageData.clear();
        RegistrationActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();


        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(RegistrationActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(RegistrationActivity.this)) {
            Toast toast = Toast.makeText(RegistrationActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();

        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(RegistrationActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }

            //mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
            if (findFrontFacingCamera() < 0)
            {
                mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            else
                {
                    mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }

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
                params.setRotation(270);


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
                params2.setRotation(270);

                mCamera.setParameters(params2);
            }




            if (findFrontFacingCamera() < 0)
            {
                setCameraDisplayOrientation(RegistrationActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
            }
            else
            {
                setCameraDisplayOrientation(RegistrationActivity.this, Camera.CameraInfo.CAMERA_FACING_FRONT,mCamera);
            }



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
                RegistrationActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                v.setEnabled(false);
                capture.setEnabled(false);
                cameraPreview.setEnabled(false);
                flashImage.setEnabled(false);

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;
                dialog.dismiss();
                RegistrationActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        RegistrationActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


    }

    public void   openCustomCamara()
    {

            openCamera();


    }

    public  void getCameraView()
    {
        Button btn_view=new Button(RegistrationActivity.this);
        btn_view.setTag("SelfieCamera");
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn_view.setLayoutParams(layoutParams1);
        btn_view.setBackground(getResources().getDrawable(R.drawable.btn_calender_background));;
        btn_view.setText("Click Selfie");
       // imagButtonTag=tagValue.split(Pattern.quote("^"))[0].toString();
        btn_view.setTextColor(Color.WHITE);
        //	btn_view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.camera_icon, 0);
       // cameraButton=btn_view;
        btn_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickedTagPhoto=v.getTag().toString();
                openCustomCamara();
				/*Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				saveImage();
				imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				startActivityForResult(imageIntent,
						requestCode );*/

            }
        });
        llCamera.addView(btn_view);
    }
    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath)
        {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());

            if (bitmap == null)
            {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try
            {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);
            signOrNot=true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        imageF=imageF_savedInstance;
        imageName=imageName_savedInstance;
        clickedTagPhoto=clickedTagPhoto_savedInstance;
        uriSavedImage=	uriSavedImage_savedInstance;

        if(requestCode == requestCode && resultCode ==RegistrationActivity.this.RESULT_OK) {


            if (imageF != null && imageF.getAbsolutePath() != null) {
                File file = imageF;
                System.out.println("File +++" + imageF);

                Bitmap bmp = decodeSampledBitmapFromFile(file.getAbsolutePath(), 80, 80);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteArray = stream.toByteArray();

                // Convert ByteArray to Bitmap::\
                //
                long syncTIMESTAMP = System.currentTimeMillis();
                Date dateobj = new Date(syncTIMESTAMP);
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
                String clkdTime = df.format(dateobj);
                //	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
                String valueOfKey = clickedTagPhoto + "~" + "1" + "~" + uriSavedImage.toString() + "~" + clkdTime + "~" + "1";
                //   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                globalImageName=   imageName;
                globalImagePath=      uriStringPath ;
                //
                setSavedImageToScrollView(bitmap, imageName, valueOfKey,clickedTagPhoto);
            } else if (filePathPhoto != null) {
                Bitmap bmp = decodeSampledBitmapFromFile(filePathPhoto, 80, 80);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteArray = stream.toByteArray();

                // Convert ByteArray to Bitmap::\
                //
                long syncTIMESTAMP = System.currentTimeMillis();
                Date dateobj = new Date(syncTIMESTAMP);
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
                String clkdTime = df.format(dateobj);
                //	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
                String valueOfKey = clickedTagPhoto + "~" + "1"+ "~" + uriStringPath + "~" + clkdTime + "~" + "1";
                //   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
              globalImageName=   imageName;
                globalImagePath=      uriStringPath ;
                //
                setSavedImageToScrollView(bitmap, imageName, valueOfKey, clickedTagPhoto);
            } else {
                Toast.makeText(RegistrationActivity.this, "Please click another photo,this photo is too heavy to load", Toast.LENGTH_SHORT).show();
            }

/*
             ImageView image = new ImageView(getActivity());

               //btn.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));


               image.setImageBitmap(bitmap);
               LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 90);
               params.setMargins(10,0,10,0);

               ll_Image.addView(image,params);*/
        }
    }


    public void setSavedImageToScrollView(Bitmap bitmap,String imageValidName,String valueOfKey,final String tagOfClkdPic)
    {
        //clickedTagPhoto
        // Bitmap bmRotated = rotateBitmap(bitmap,ExifInterface.ORIENTATION_FLIP_VERTICAL );
        LayoutInflater inflater=(LayoutInflater) RegistrationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewStoreLocDetail=inflater.inflate(R.layout.store_loc_display,null);


        final RelativeLayout rl_photo=(RelativeLayout) viewStoreLocDetail.findViewById(R.id.rl_photo);
        final ImageView img_thumbnail=(ImageView)viewStoreLocDetail.findViewById(R.id.img_thumbnail);
        img_thumbnail.setImageBitmap(bitmap);
        img_thumbnail.setTag(imageValidName+"!"+valueOfKey);

        img_thumbnail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String filePathName="";
                    if(view.getTag().toString().split(Pattern.quote("!"))[1].split(Pattern.quote("~"))[2].contains("file:")){
                        filePathName=view.getTag().toString().split(Pattern.quote("!"))[1].split(Pattern.quote("~"))[2].replace("file:","");
                    }
                    else {
                        filePathName=view.getTag().toString().split(Pattern.quote("!"))[1].split(Pattern.quote("~"))[2];

                    }
                    File file = new File(filePathName);
                    Uri intentUri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", file);
                    intent.setDataAndType(intentUri, "image/*");
                    startActivity(intent);

                }
                else{
                    Uri intentUri = Uri.parse(view.getTag().toString().split(Pattern.quote("!"))[1].split(Pattern.quote("~"))[2]);


                    intent.setDataAndType(intentUri, "image/*");
                    startActivity(intent);
                }


            }

        });

        final ImageView imgCncl=(ImageView) viewStoreLocDetail.findViewById(R.id.imgCncl);
        imgCncl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +img_thumbnail.getTag().toString().split(Pattern.quote("!"))[1].split(Pattern.quote("~"))[2];
                File fdelete = new File(file_dj_path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        globalImageName= "";
                        globalImagePath=  "" ;
                        callBroadCast();
                    } else {

                    }
                }
                ll_ImageToSet.removeView(viewStoreLocDetail);
            }
        });






        if(ll_ImageToSet!=null)
        {
            ll_ImageToSet.addView(viewStoreLocDetail);
        }







    }
    public void checkNumberOfFiles(File dir)
    {
        int NoOfFiles=0;
        String [] Totalfiles = null;

        if (dir.isDirectory())
        {
            String[] children = dir.list();
            NoOfFiles=children.length;
            Totalfiles=new String[children.length];
if(Totalfiles.length>0) {
    for (int i = 0; i < children.length; i++) {
        // Totalfiles[i]=children[i];
        File fdelete = new File(Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" + children[i]);
        if (fdelete.exists()) {
            if (fdelete.delete()) {

                callBroadCast();
            } else {

            }
        }

        ll_ImageToSet.removeAllViews();
    }
}
        }

    }
    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(RegistrationActivity.this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            Log.e("-->", " < 14");
            RegistrationActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void saveImage()
    {
        long syncTIMESTAMP = System.currentTimeMillis();
        Date datefromat = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
        onlyDate=df.format(datefromat);
        onlyDate=onlyDate.replace(":","").trim().replace("-", "").replace(" ","").trim().toString();
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.ImagesFolder);
        if (!imagesFolder.exists())
        {
            imagesFolder.mkdirs();
        }
        imageName=onlyDate+".jpg";
        imageF = new File(imagesFolder,imageName);

        try {
            FileOutputStream fo = new FileOutputStream(imageF);

				/*	fo.write(bmp);
					fo.close();*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        uriSavedImage = Uri.fromFile(imageF);



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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +imei+ timeStamp + ".jpg");

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
    public void signatureAllCode()
    {
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
        mContent= (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mGetSign= (Button) findViewById(R.id.getsign);
        //by default disable it when , person do sign it will enable
        mGetSign.setEnabled(false);
        mClear= (Button) findViewById(R.id.clear);
        mCancel= (Button) findViewById(R.id.cancel);
        view=mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
                signOrNot=false;
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath);

                Toast.makeText(getApplicationContext(),getResources().getString(R.string.txtSuccessfullySaved), Toast.LENGTH_SHORT).show();
                // Calling the same class
                //  recreate();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");

                // Calling the same class
                //   recreate();
            }
        });
        transparent_image=(ImageView)findViewById(R.id.transparent_image);
        scrollViewParentOfMap=(ScrollView)findViewById(R.id.scrollViewParentOfMap);

        transparent_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollViewParentOfMap.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }
    public boolean validate()
    {
        if(ed_Name.getText().toString().trim().equals("") )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(ed_ContactNo.getText().toString().trim().equals("") ||  (ed_ContactNo.getText().toString().length()<10))
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter Valid Contact No.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Text_Dob.getText().toString().trim().equals("Select Date")  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }
       else if(ed_AccNo.getText().toString().trim().equals("") || ed_AccNo.getText().toString().trim().length()<9 || ed_AccNo.getText().toString().trim().length()>18  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter Proper Account No.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(spinner_Bank.getText().toString().trim().equals("Select")  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Select Bank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(spinner_Bank.getText().toString().trim().equals("")  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Select Bank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!hashmapBank.containsKey(spinner_Bank.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Please Select Proper Bank", Toast.LENGTH_SHORT).show();
            spinner_Bank.setText("");
            return false;
        }
        else  if(ed_ifsc.getText().toString().trim().equals("")  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter IFSC Code", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if(ed_ifsc.getText().toString().trim().length()!=11  )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter Proper IFSC Code", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(!(rb_yes.isChecked())  && !(rb_NO.isChecked()) )
            {
                //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
                Toast.makeText(getApplicationContext(), "Please select UPI ID Available", Toast.LENGTH_SHORT).show();
                return false;
            }
        else if(ed_UPIID.getText().toString().trim().equals("")  && (ed_UPIID.getVisibility()==View.VISIBLE) )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter UPI ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!ed_UPIID.getText().toString().trim().contains("@") && (ed_UPIID.getVisibility()==View.VISIBLE))
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Enter Proper UPI ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if(ll_ImageToSet==null )
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Take Selfie", Toast.LENGTH_SHORT).show();
            return false;
        }
       else  if(ll_ImageToSet!=null && ll_ImageToSet.getChildCount()==0)
        {
            //showAlertForEveryOne(getResources().getString(R.string.txtValidateUpdatePhoto));
            Toast.makeText(getApplicationContext(), "Please Take Selfie", Toast.LENGTH_SHORT).show();
            return false;
        }

         else  if(!signOrNot)
        {
           // showAlertForEveryOne(getResources().getString(R.string.txtValidateSignature));
            Toast.makeText(getApplicationContext(), "Please do Signature", Toast.LENGTH_SHORT).show();
            return false;
        }

        else{
            return true;
        }

    }

    private class FullSyncDataNow extends AsyncTask<Void, Void, Void>
    {


        ProgressDialog pDialogGetStores;
        public FullSyncDataNow(RegistrationActivity activity)
        {
            pDialogGetStores = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


            pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
            pDialogGetStores.setMessage("Submitting  Details...");
            pDialogGetStores.setIndeterminate(false);
            pDialogGetStores.setCancelable(false);
            pDialogGetStores.setCanceledOnTouchOutside(false);
            pDialogGetStores.show();


        }

        @Override

        protected Void doInBackground(Void... params)
        {

            int Outstat=3;

            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);

            SimpleDateFormat df1 = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss", Locale.ENGLISH);
            String dateString=df1.format(dateobj);
            newfullFileName=CommonInfo.imei+"."+dateString;
            LinkedHashMap<String,String> hmapstlist=new LinkedHashMap<String, String>();



            try {
                File LTFoodxmlFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);
                if (!LTFoodxmlFolder.exists())
                {
                    LTFoodxmlFolder.mkdirs();

                }

                DA.open();
                DA.export(CommonInfo.DATABASE_NAME, newfullFileName,"0");
                DA.close();
                sharedPrefForRegistration.edit().putString("SubmitOrNot", "1").commit();//1 means Submitted now not need to open page in day end
                dbengine.savetbl_XMLfiles(newfullFileName, "3","10");
                dbengine.open();

                dbengine.updateSstat();


                dbengine.close();

                //  helperDb.fnupdateDisributorMstrLocationtrackRemapFlg(UniqueDistribtrMapId);

            } catch (Exception e)
            {

                e.printStackTrace();
                if(pDialogGetStores.isShowing())
                {
                    pDialogGetStores.dismiss();
                }
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
                if(isOnline())
                {
                    Intent syncIntent = new Intent(RegistrationActivity.this, SyncRegistrationDetails.class);
                    syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + newfullFileName + ".xml");
                    syncIntent.putExtra("OrigZipFileName", newfullFileName);
                    syncIntent.putExtra("whereTo", "Regular");
                    syncIntent.putExtra("IntentFrom", FROM);

                    syncIntent.putExtra("Button", ButtonClick);


                    startActivity(syncIntent);
                    finish();
                }
                else {
                    Intent i;
                    if(FROM.equals("SPLASH")){
                         i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                    }
                    else{
                        i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                    }

                    i.putExtra("IntentFrom", 0);
                    startActivity(i);
                    finish();
                }


            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    }
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }
    public String genTempID()
    {

        String cxz;
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tManager.getDeviceId();
        String IMEIid =  imei.substring(9);
        long  syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        String StampEndsTime = df.format(dateobj);
        cxz = IMEIid +"-"+StampEndsTime.replace(" ", "").replace(":", "").trim();
        return cxz;

    }
    public void fillDataToLayoutFromDataBase()
    {
        if(!PersonNameAndFlgRegistered.equals("0"))
        {
             personName=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[0];
            String FlgRegistered=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[1];
            userNodeIdGlobal=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[2];
            userNodetypeGlobal=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[3];
            SalesAreaName=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[4];
        ContactNumberFromServer=   PersonNameAndFlgRegistered.split(Pattern.quote("^"))[5];

            welcomeTextView.setText(getResources().getString(R.string.txtWelcome)+" "+personName);
            //
            //FlgRegistered="1";
            String flagReg="0";
            if(sharedPrefForRegistration.contains("FlgRegistered") && sharedPrefForRegistration.contains("SubmitOrNot")){
                if( sharedPrefForRegistration.getString("SubmitOrNot", "").equals("1")){
                    flagReg="1";
                }

            }
            sharedPrefForRegistration.edit().putString("FlgRegistered", FlgRegistered).commit();
            sharedPrefForRegistration.edit().putString("SubmitOrNot", flagReg).commit();

            if(FlgRegistered.equals("0"))
            {
                textMessage.setText(getResources().getString(R.string.txtDSRMsg));

                text_UpdateNow.setVisibility(View.VISIBLE);
                String text=text_UpdateNow.getText().toString().trim();
                SpannableString content1 = new SpannableString(text);
                content1.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                text_UpdateNow.setText(content1);
                CovrageArea.setVisibility(View.VISIBLE);
                ContactOnWelcome.setVisibility(View.VISIBLE);
                DobOnWelcome.setVisibility(View.GONE);
                textCoverage.setVisibility(View.VISIBLE);
                textContact.setVisibility(View.VISIBLE);
                textDob.setVisibility(View.GONE);
                profile_image.setVisibility(View.GONE);
                CovrageArea.setText(SalesAreaName);


                if(!ContactNumberFromServer.equals("0")){
                    ed_Name.setText(personName);
                    ed_ContactNo.setText(ContactNumberFromServer);
                    ContactOnWelcome.setText(ContactNumberFromServer);
                }

            }
            if(FlgRegistered.equals("1"))
            {
                text_NotYou.setVisibility(View.VISIBLE);
                String text=text_NotYou.getText().toString().trim();
                SpannableString content1 = new SpannableString(text);
                content1.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                text_NotYou.setText(content1);
                hmapDsrRegAllDetails=  dbengine.fnGettblDsrRegDetails();
                CovrageArea.setText(SalesAreaName);
                ContactOnWelcome.setText(ContactNumberFromServer);
                hmapDsrRegAllDetails=  dbengine.fnGettblDsrRegDetails();
                if(hmapDsrRegAllDetails!=null && (!hmapDsrRegAllDetails.isEmpty()) && hmapDsrRegAllDetails.containsKey("DSRDETAILS")){
                    String DSR_All_DATA=   hmapDsrRegAllDetails.get("DSRDETAILS");
                    String ContactNo=   DSR_All_DATA.split(Pattern.quote("^"))[1];
                    String DOB=   DSR_All_DATA.split(Pattern.quote("^"))[2];
                    String SelfieName=   DSR_All_DATA.split(Pattern.quote("^"))[3];

                    ContactOnWelcome.setText(ContactNo);
                    DobOnWelcome.setText(DOB);
                    try{
                        String PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolderServer + "/";

                        File file2 = new File(PATH + SelfieName);
                        if (file2.exists()) {

                            Bitmap myBitmap = BitmapFactory.decodeFile(file2.getAbsolutePath());

                            profile_image.setImageBitmap(myBitmap);
                        }

                    }
                    catch (Exception e){

                    }

                }

            }

        }

    }
    public void getDataFromDataBase()
    {

        PersonNameAndFlgRegistered=  dbengine.fnGetPersonNameAndFlgRegistered();
        hashmapBank = dbengine.fnGettblBankMaster();


        //PersonNameAndFlgRegistered="Shivam"+"^"+"0";
    }
    public void TextViewInitialize()
    {
        textCoverage= (TextView) findViewById(R.id.textCoverage);
        textContact= (TextView) findViewById(R.id.textContact);
        textDob= (TextView) findViewById(R.id.textDob);

        CovrageArea= (TextView) findViewById(R.id.CovrageArea);
        ContactOnWelcome= (TextView) findViewById(R.id.ContactOnWelcome);
        DobOnWelcome= (TextView) findViewById(R.id.DobOnWelcome);

        welcomeTextView= (TextView) findViewById(R.id.welcomeTextView);
        textMessage= (TextView) findViewById(R.id.textMessage);


        text_UpdateNow= (TextView) findViewById(R.id.text_UpdateNow);
        text_UpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentOf_questionLayout.setVisibility(View.GONE);
                parentOf_registrationformLayout.setVisibility(View.VISIBLE);
                Submit.setVisibility(View.VISIBLE);
                CancelButton.setVisibility(View.VISIBLE);

            }
        });

        text_NotYou= (TextView) findViewById(R.id.text_NotYou);
        text_NotYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentOf_questionLayout.setVisibility(View.GONE);
                parentOf_validationLayout.setVisibility(View.VISIBLE);

            }
        });


        text_Daystart= (TextView) findViewById(R.id.text_Daystart);
        text_Daystart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if(FROM.equals("SPLASH")){
                    i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                }
                else{
                    i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                }
                i.putExtra("IntentFrom", 0);
                startActivity(i);
                finish();

            }
        });



        Text_Dob= (TextView) findViewById(R.id.Text_Dob);
        Text_Dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dob_Bool=true;
                calendar = Calendar.getInstance();
                Year = calendar.get(Calendar.YEAR) ;
                Month = calendar.get(Calendar.MONTH);
                Day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = DatePickerDialog.newInstance(RegistrationActivity.this, Year-24, Month, Day);

                datePickerDialog.setThemeDark(false);

                datePickerDialog.showYearPickerFirst(false);

                Calendar calendarForSetDate = Calendar.getInstance();
                calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

                // calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
                //YOU can set min or max date using this code
                // datePickerDialog.setMaxDate(Calendar.getInstance());
                // datePickerDialog.setMinDate(calendar);

                //  datePickerDialog.setMinDate(calendarForSetDate);
                calendarForSetDate.set(Year - 24, Month, Day);
                datePickerDialog.setMaxDate(calendarForSetDate);
                datePickerDialog.setAccentColor(Color.parseColor("#544f88"));

                datePickerDialog.setTitle(getResources().getString(R.string.txtSELECTDOB));
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });
        txt_Dob_credential= (TextView) findViewById(R.id.txt_Dob_credential);
        txt_Dob_credential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credential_dob_Bool=true;
                calendar = Calendar.getInstance();
                Year = calendar.get(Calendar.YEAR) ;
                Month = calendar.get(Calendar.MONTH);
                Day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = DatePickerDialog.newInstance(RegistrationActivity.this, Year-24, Month, Day);

                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);

                Calendar calendarForSetDate = Calendar.getInstance();
                calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

                // calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
                //YOU can set min or max date using this code
                // datePickerDialog.setMaxDate(Calendar.getInstance());
                // datePickerDialog.setMinDate(calendar);

                //surbhi
                calendarForSetDate.set(Year - 24, Month, Day);
                datePickerDialog.setMaxDate(calendarForSetDate);
                datePickerDialog.setAccentColor(Color.parseColor("#544f88"));

                datePickerDialog.setTitle(getResources().getString(R.string.txtSELECTDOB));

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });



    }
    public void LinearLayoutInitialize()
    {

        parentOf_questionLayout= (LinearLayout) findViewById(R.id.parentOf_questionLayout);
        parentOf_validationLayout= (LinearLayout) findViewById(R.id.parentOf_validationLayout);
        parentOf_registrationformLayout= (LinearLayout) findViewById(R.id.parentOfAllDynamicData);


    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String mon=MONTHS[monthOfYear];

        if(credential_dob_Bool)
        {
            txt_Dob_credential.setText(dayOfMonth+"/"+mon+"/"+year);
        }
        if(dob_Bool)
        {
            Text_Dob.setText(dayOfMonth+"/"+mon+"/"+year);
        }

        credential_dob_Bool=false;
        dob_Bool=false;



    }
    public void ButtonInitialize()
    {


        BtnNotYou=(Button)findViewById(R.id.BtnNotYou);
        BtnNotYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentOf_questionLayout.setVisibility(View.GONE);
                parentOf_registrationformLayout.setVisibility(View.GONE);
                parentOf_validationLayout.setVisibility(View.VISIBLE);
            }
        });
        CancelButton=(Button)findViewById(R.id.CancelButton);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                if(FROM.equals("SPLASH")){
                    i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                }
                else{
                    i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                }

                i.putExtra("IntentFrom", 0);
                startActivity(i);
                finish();

            }
        });
        validate_btn=(Button)findViewById(R.id.validate_btn);
        validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uncomment it
                check_validationAndGetDataFromServer();


            }
        });
       /* backIcon=(ImageView)findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storeIntent = new Intent(RegistrationActivity.this, AllButtonActivity.class);
                startActivity(storeIntent);
                finish();
            }
        });*/
        Submit= (Button)  findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){
                    // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    String SignName_string="0";
                    String SignPath_string="0";
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, StoredPath);
                    SignName_string=pic_name +CommonInfo.imei+ ".png";
                    SignPath_string=StoredPath;
                    long syncTIMESTAMP = System.currentTimeMillis();
                    Date dateobj = new Date(syncTIMESTAMP);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
                    String photoClickedDateTime = df.format(dateobj);
                    String RecordId=genTempID();

                    String IMEI_string=CommonInfo.imei;
                    String NAME_String="0";
                    String ContactNo_string="0";
                    String DOB_string="0";
                    String AccNO_string="0";
                    String BankID="0";
                    String IFSC_string="0";
                    String UPI_ID_YesNO="0";
                    String UPIID_string="NA";
                    String PersonNodeId_string=userNodeIdGlobal;
                    String PersonNodeType_string=userNodetypeGlobal;

                    NAME_String=ed_Name.getText().toString().trim();
                    ContactNo_string=ed_ContactNo.getText().toString().trim();
                    DOB_string=Text_Dob.getText().toString().trim();
                    AccNO_string=ed_AccNo.getText().toString().trim();
                    if(hashmapBank!=null && hashmapBank.containsKey(spinner_Bank.getText().toString().trim())){
                        BankID= hashmapBank.get(spinner_Bank.getText().toString().trim());
                    }
                    IFSC_string=ed_ifsc.getText().toString().trim();
                    if(rb_yes.isChecked()){
                        UPI_ID_YesNO="1";
                        UPIID_string=ed_UPIID.getText().toString().trim();
                    }
                    if(rb_NO.isChecked()){
                        UPI_ID_YesNO="0";
                    }



                    //-------*******************************************************
                    dbengine.open();
                    // dbengine.deletetblRegistrationDetail();
                    dbengine.inserttblProfilePhotoSignDetail(RecordId,photoClickedDateTime,globalImageName,globalImagePath,SignName_string,SignPath_string,NAME_String,ContactNo_string,DOB_string,AccNO_string,BankID,IFSC_string,UPI_ID_YesNO,UPIID_string,IMEI_string,PersonNodeId_string,PersonNodeType_string);
                  //  int count=    dbengine.CheckDataInRegistration();

                    dbengine.Delete_tblDsrRegDetails();

                    dbengine.savetblDsrRegDetails(PersonNodeId_string,PersonNodeType_string,NAME_String,ContactNo_string,DOB_string,globalImageName,SignName_string,AccNO_string,BankID,IFSC_string,UPI_ID_YesNO,UPIID_string, SelfieNameURL);
                    dbengine.close();


                    FullSyncDataNow task = new FullSyncDataNow(RegistrationActivity.this);
                    task.execute();
                    //-------*******************************************************

                }
            }
        });


    }
    public void SpinnerInitialization(){

        transparentOverlay=(RelativeLayout) findViewById(R.id.transparentOverlay);
        BankName=changeHmapToArrayValue(hashmapBank);
        spinner_Bank=(EditText) findViewById(R.id.spinner_Bank);
        spinner_Bank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if(arg0.toString().trim().equals("")){
                    transparentOverlay.setVisibility(View.GONE);
                }
                else{
                    transparentOverlay.setVisibility(View.VISIBLE);
                }
                adapterDominantBrand.getFilter().filter(arg0.toString().trim());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listDominantBrand = (ListView)findViewById(R.id.list_view);
        adapterDominantBrand = new ArrayAdapter<String>(RegistrationActivity.this, R.layout.list_item, R.id.product_name, BankName);
        listDominantBrand.setAdapter(adapterDominantBrand);

        listDominantBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                String abc=listDominantBrand.getItemAtPosition(arg2).toString().trim();
                spinner_Bank.setText(abc);
                transparentOverlay.setVisibility(View.GONE);




            }
        });


       /* spinner_Bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder	 alertDialog = new AlertDialog.Builder(RegistrationActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                convertView = (View) inflater.inflate(R.layout.activity_list, null);
                EditText inputSearch=	 (EditText) convertView.findViewById(R.id.inputSearch);
                inputSearch.setVisibility(View.GONE);
                listDominantBrand = (ListView)convertView. findViewById(R.id.list_view);
                adapterDominantBrand = new ArrayAdapter<String>(RegistrationActivity.this, R.layout.list_item, R.id.product_name, BankName);
                listDominantBrand.setAdapter(adapterDominantBrand);
                alertDialog.setView(convertView);
                alertDialog.setTitle("DB NAME");
                listDominantBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                        String abc=listDominantBrand.getItemAtPosition(arg2).toString().trim();
                        spinner_Bank.setText(abc);


                        ad.dismiss();

                    }
                });
                ad=alertDialog.show();
            }
        });*/

    }
    public void  EditTextInitialization(){

        ET_mobile_credential=(EditText) findViewById(R.id.ET_mobile_credential);
        ed_Name=(EditText) findViewById(R.id.ed_Name);
        ed_AccNo=(EditText) findViewById(R.id.ed_AccNo);
        ed_ifsc=(EditText) findViewById(R.id.ed_ifsc);
        ed_UPIID=(EditText) findViewById(R.id.ed_UPIID);
        ed_ContactNo=(EditText) findViewById(R.id.ed_ContactNo);

    }
    public void  RadioButtonInitialization(){
        rb_yes=(RadioButton) findViewById(R.id.rb_yes);
        rb_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_NO.setChecked(false);
                ed_UPIID.setVisibility(View.VISIBLE);
            }
        });
        rb_NO=(RadioButton) findViewById(R.id.rb_NO);
        rb_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_yes.setChecked(false);
                ed_UPIID.setVisibility(View.GONE);
            }
        });

    }
    public String[] changeHmapToArrayValue(LinkedHashMap<String, String> hmap)
    {
        String[] stringArray=new String[hmap.size()];
        int index=0;
        if(hmap!=null)
        {
            Set set2 = hmap.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator.next();
				/*stringArray[index]=me2.getValue().toString();*/
                stringArray[index]=me2.getKey().toString();
                index=index+1;
            }
        }
        return stringArray;
    }
    private class ValidateAndGetDsrData extends AsyncTask<Void, Void, Void>
    {

        ProgressDialog pDialogGetStores;
        public ValidateAndGetDsrData(RegistrationActivity activity)
        {
            pDialogGetStores = new ProgressDialog(activity);
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
            pDialogGetStores.setMessage(getText(R.string.ValidatingMsg));
            pDialogGetStores.setIndeterminate(false);
            pDialogGetStores.setCancelable(false);
            pDialogGetStores.setCanceledOnTouchOutside(false);
            pDialogGetStores.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ServiceWorker newservice = new ServiceWorker();

            try
            {
                newservice = newservice.getDsrRegistrationData(getApplicationContext(),CommonInfo.imei,mobNumberForService,dobForService);
                if(!newservice.director.toString().trim().equals("1"))
                {
                    if(chkFlgForErrorToCloseApp==0)
                    {
                        chkFlgForErrorToCloseApp=1;
                    }

                }
            }
            catch(Exception e)
            {}

            finally
            {}

            return null;
        }


        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if(pDialogGetStores.isShowing())
            {
                pDialogGetStores.dismiss();
            }
            if(chkFlgForErrorToCloseApp==1)   // if Webservice showing exception or not excute complete properly
            {
                chkFlgForErrorToCloseApp=0;
                Toast.makeText(getApplicationContext(),"Internet connection is slow ,please try again.", Toast.LENGTH_LONG).show();
                // finish();
            }
            else
            {
                String flag="0";
                String message="0";
                String FlagAndMessage=  dbengine.fnGettblUserRegistarationStatus();
                if(!FlagAndMessage.equals("0"))
                {
                    flag    =   FlagAndMessage.split(Pattern.quote("^"))[0];
                    message =   FlagAndMessage.split(Pattern.quote("^"))[1];


                }

                if(flag.equals("0"))
                {
                    parentOf_validationLayout.setVisibility(View.GONE);
                    parentOf_registrationformLayout.setVisibility(View.VISIBLE);
                    Submit.setVisibility(View.VISIBLE);
                    CancelButton.setVisibility(View.VISIBLE);
                    userNodeIdGlobal  = "0";
                    userNodetypeGlobal=  "0";
                    ReInitializeAllView();
                   /* LL_banner_image.setVisibility(View.GONE);
                    Submit_btn.setVisibility(View.VISIBLE);
                    BtnCancel.setVisibility(View.VISIBLE);*/

                }
                if(flag.equals("1"))
                {
                    AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(RegistrationActivity.this);
                    alertDialogNoConn.setTitle(getResources().getString(R.string.genTermNoDataConnection));

                    alertDialogNoConn.setMessage(message);
                    alertDialogNoConn.setCancelable(false);
                    alertDialogNoConn.setNeutralButton(getResources().getString(R.string.AlertDialogOkButton),new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();

                            Intent i=new Intent(RegistrationActivity.this,AllButtonActivity.class);
                            i.putExtra("IntentFrom", 0);
                            startActivity(i);
                            finish();

                        }
                    });
                    alertDialogNoConn.setIcon(R.drawable.info_ico);
                    AlertDialog alert = alertDialogNoConn.create();
                    alert.show();

                }
                if(flag.equals("2"))
                {
                   // uncommentIT
                    FillDsrDetailsToLayout();
                    parentOf_validationLayout.setVisibility(View.GONE);
                    parentOf_registrationformLayout.setVisibility(View.VISIBLE);
                    Submit.setVisibility(View.VISIBLE);
                    CancelButton.setVisibility(View.VISIBLE);
                    /*LL_banner_image.setVisibility(View.GONE);
                    Submit_btn.setVisibility(View.VISIBLE);
                    BtnCancel.setVisibility(View.VISIBLE);
*/

                }


            }


        }
    }

    public void FillDsrDetailsToLayout()
    {


        hmapDsrRegAllDetails=  dbengine.fnGettblDsrRegDetails();
        if(hmapDsrRegAllDetails!=null && !hmapDsrRegAllDetails.isEmpty())
        {
            String DSR_All_DATA=   hmapDsrRegAllDetails.get("DSRDETAILS");

            String Name=   DSR_All_DATA.split(Pattern.quote("^"))[0];
            String ContactNo=   DSR_All_DATA.split(Pattern.quote("^"))[1];
            String DOB=   DSR_All_DATA.split(Pattern.quote("^"))[2];


            String BankAccountnumber=   DSR_All_DATA.split(Pattern.quote("^"))[5];
            String BankID=   DSR_All_DATA.split(Pattern.quote("^"))[6];
            String IFSCCode=   DSR_All_DATA.split(Pattern.quote("^"))[7];
            String flgUPIID=   DSR_All_DATA.split(Pattern.quote("^"))[8];
            String UPIID=   DSR_All_DATA.split(Pattern.quote("^"))[9];



            userNodeIdGlobal  =   DSR_All_DATA.split(Pattern.quote("^"))[10];
            userNodetypeGlobal=   DSR_All_DATA.split(Pattern.quote("^"))[11];
            SelfieNameURL=   DSR_All_DATA.split(Pattern.quote("^"))[12];

            if(!Name.equals("0")){
                ed_Name.setText(Name);
                ed_Name.setEnabled(false);
            }
            if(!ContactNo.equals("0")){
                ed_ContactNo.setText(ContactNo);
                ed_ContactNo.setEnabled(false);
            }
            if(!DOB.equals("0")){
                Text_Dob.setText(DOB);
                Text_Dob.setEnabled(false);
            }
            if(!BankAccountnumber.equals("0")){
                ed_AccNo.setText(BankAccountnumber);
            }
            if(flgUPIID.equals("0")){
                rb_NO.setChecked(true);
            }
            if(flgUPIID.equals("1")){
                rb_NO.setChecked(false);
                rb_yes.setChecked(true);
                ed_UPIID.setVisibility(View.VISIBLE);
                if(!UPIID.equals("0")){
                    ed_UPIID.setText(UPIID);
                }
            }
            if(!BankID.equals("0")){
               String bankName= dbengine.fnGetBankName(BankID);
                if(!bankName.equals("0")){
                    spinner_Bank.setText(bankName);
                }

            }
            if(!IFSCCode.equals("0")){
                ed_ifsc.setText(IFSCCode);

            }






        }
        transparentOverlay.setVisibility(View.GONE);

    }
    public void showAlertForEveryOne(String msg)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(RegistrationActivity.this);
        alertDialogNoConn.setTitle(getResources().getString(R.string.genTermNoDataConnection));

        alertDialogNoConn.setMessage(msg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(getResources().getString(R.string.AlertDialogOkButton),new DialogInterface.OnClickListener()
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
    public void check_validationAndGetDataFromServer()
    {
        if( ET_mobile_credential.getText().toString().trim().equals("0000000000") || ET_mobile_credential.getText().toString().trim().equals("") || ET_mobile_credential.getText().toString().trim().length()<10)
        {
            showAlertForEveryOne(getResources().getString(R.string.txtErrorMobileNo));

        }
        else if(txt_Dob_credential.getText().toString().trim().equals(""))
        {
            showAlertForEveryOne(getResources().getString(R.string.txtSelectDOB));

        }
        else
        {
            mobNumberForService=     ET_mobile_credential.getText().toString().trim();
            dobForService=    txt_Dob_credential.getText().toString().trim();
            if(isOnline())
            {

                try
                {
                    ValidateAndGetDsrData cuv = new ValidateAndGetDsrData(RegistrationActivity.this);
                    cuv.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();

                }

            }
            else {
                showNoConnAlert();

            }
             /* parentOf_validationLayout.setVisibility(View.GONE);
              parentOf_registrationformLayout.setVisibility(View.VISIBLE);
              LL_banner_image.setVisibility(View.GONE);
              Submit_btn.setVisibility(View.VISIBLE);
              BtnCancel.setVisibility(View.VISIBLE);*/

        }

    }
    public void showNoConnAlert()
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(RegistrationActivity.this);
        alertDialogNoConn.setTitle(getResources().getString(R.string.txtErrorNoDataConnection));

        alertDialogNoConn.setMessage(getResources().getString(R.string.txtErrorInternetConnection));
        //alertDialogNoConn.setMessage(getText(R.string.connAlertErrMsg));
        alertDialogNoConn.setNeutralButton(getResources().getString(R.string.AlertDialogOkButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*if(isMyServiceRunning())
                  		{
                        stopService(new Intent(DynamicActivity.this,GPSTrackerService.class));
                  		}
                        finish();*/
                        //finish();
                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();
        // alertDialogLowbatt.show();
    }
    public void ReInitializeAllView(){
        ed_Name.setText("");

        ed_Name.setEnabled(true);
                ed_ContactNo.setText("");
        ed_ContactNo.setEnabled(true);
        Text_Dob.setText("");
        Text_Dob.setEnabled(true);
                ed_AccNo.setText("");
        spinner_Bank.setText("");
                rb_yes.setChecked(false);
        rb_NO.setChecked(false);
                ed_ifsc.setText("");
        ed_UPIID.setText("");
        ed_UPIID.setVisibility(View.GONE);
    }


    public  Bitmap normalizeImageForUri(Context context, Uri uri) {
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
}
