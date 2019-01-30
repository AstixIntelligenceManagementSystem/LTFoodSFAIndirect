package project.astix.com.ltfoodsfaindirect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astix.Common.CommonFunction;
import com.astix.Common.CommonInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class PicClkBfrStock extends AppCompatActivity implements DeletePic {

    LinkedHashMap<String, Integer> hmapCtgry_Imageposition = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, String> hmapPhotoDetailsForSaving = new LinkedHashMap<>();
    LinkedHashMap<String, ArrayList<String>> hmapCtgryPhotoSection = new LinkedHashMap<String, ArrayList<String>>();
    ImageAdapter adapterImage;
    DBAdapterKenya dbengine = new DBAdapterKenya(PicClkBfrStock.this);
    public String storeID;
    public String imei;
    public String date;
    public String pickerDate;
    public String selStoreName;

    int isStockAvlbl = 0;
    int isCmpttrAvlbl = 0;

    int picAddPosition = 0;
    int removePicPositin = 0;
    ExpandableHeightGridView expandable_gridview;
    Button btn_camera, btn_next;
    ImageView img_back_Btn;
    String imageName;
    File imageF;
    String clickedTagPhoto;
    Dialog dialog;
    Uri uriSavedImage;
    ImageView flashImage;
    float mDist = 0;
    private boolean isLighOn = false;
    ArrayList<Object> arrImageData = new ArrayList<Object>();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture, cancelCam, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    CheckBox chkBox_Rtailer;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // finish();
            return true;
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
        setContentView(R.layout.activity_pic_clk_bfr_stock);


        chkBox_Rtailer = (CheckBox) findViewById(R.id.chkBox_Rtailer);
        expandable_gridview = (ExpandableHeightGridView) findViewById(R.id.expandable_gridview);
        btn_camera = (Button) findViewById(R.id.btn_camera);

        btn_next = (Button) findViewById(R.id.btn_next);
        img_back_Btn = (ImageView) findViewById(R.id.img_back_Btn);
        getDataFromIntent();
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomCamara();
            }
        });
        adapterImage = new ImageAdapter(this);
        expandable_gridview.setAdapter(adapterImage);
        chkBox_Rtailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkBox_Rtailer.isChecked()) {
                    btn_camera.setEnabled(false);
                } else {
                    btn_camera.setEnabled(true);
                }

            }
        });
        img_back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ready4GetLoc = new Intent(PicClkBfrStock.this, LastVisitDetails.class);
                //enableGPSifNot();

                ready4GetLoc.putExtra("storeID", storeID);
                ready4GetLoc.putExtra("SN", selStoreName);
                ready4GetLoc.putExtra("bck", 1);
                ready4GetLoc.putExtra("imei", imei);
                ready4GetLoc.putExtra("userDate", date);
                ready4GetLoc.putExtra("pickerDate", pickerDate);

                startActivity(ready4GetLoc);
                finish();


            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chkBox_Rtailer.isChecked()) {
                    dbengine.updateStockRetailerAllowed(storeID, 1);
                    if ((hmapCtgryPhotoSection != null) && (hmapCtgryPhotoSection.size() > 0)) {
                        if (saveImageSection1()) {
                            Intent nxtP4 = new Intent(PicClkBfrStock.this, ActualVisitStock.class);

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

                    } else {
                        Toast.makeText(PicClkBfrStock.this, "Click atleast one pic", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dbengine.updateStockRetailerAllowed(storeID, 0);
                    if (isCmpttrAvlbl == 1) {
                        Intent nxtP4 = new Intent(PicClkBfrStock.this, FeedbackCompetitorActivity.class);
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
                        VideoPageOpenOrProductOrderPageOpen();
                    }
                }


            }
        });

        setSavedImage();
    }


    public void setSavedImage() {
        if ((hmapPhotoDetailsForSaving != null) && (hmapPhotoDetailsForSaving.size() > 0)) {
            for (Map.Entry<String, String> entry : hmapPhotoDetailsForSaving.entrySet()) {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(entry.getValue().split(Pattern.quote("^"))[0]), 180, 180);
                String valueOfKey = entry.getValue().split(Pattern.quote("^"))[0] + "~" + entry.getValue().split(Pattern.quote("^"))[1];
                String clkTagPic = entry.getValue().split(Pattern.quote("^"))[2];
                setSavedImageToScrollView(bitmap, entry.getKey(), valueOfKey, clkTagPic, true);
            }
        }
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
        hmapPhotoDetailsForSaving = dbengine.getImageData(storeID, "1");
        if (dbengine.getStockRetailerAllowed(storeID) == 0) {
            chkBox_Rtailer.setChecked(true);
            btn_camera.setEnabled(false);
        }
    }

    //custom camera
    public void openCustomCamara() {
        if (dialog != null) {
            if (!dialog.isShowing()) {
                openCamera();
            }
        } else {
            openCamera();
        }
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

    @Override
    public void onStop() {
        super.onStop();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                }
            }
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
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

                    arrImageData.add(0, pictureFile);
                    arrImageData.add(1, pictureFile.getName());
                    dialog.dismiss();
                    if (pictureFile != null) {
                        File file = pictureFile;
                        System.out.println("File +++" + pictureFile);

                        imageName = pictureFile.getName();
                        CommonFunction.normalizeImageForUri(PicClkBfrStock.this, Uri.fromFile(pictureFile));
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 120, 120);

                        long syncTIMESTAMP = System.currentTimeMillis();
                        Date dateobj = new Date(syncTIMESTAMP);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
                        String clkdTime = df.format(dateobj);
                        String valueOfKey = file.getAbsolutePath() + "~" + clkdTime;
                        setSavedImageToScrollView(bitmap, imageName, valueOfKey, "1", false);

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
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        };
        return picture;
    }

    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            cancelCam.setEnabled(false);
            flashImage.setEnabled(false);
            if (cameraPreview != null) {
                cameraPreview.setEnabled(false);
            }

            if (mCamera != null) {
                mCamera.takePicture(null, null, mPicture);
            } else {
                dialog.dismiss();
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    };

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
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss.SSS", Locale.ENGLISH).format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + CommonInfo.imei + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public void openCamera() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        arrImageData.clear();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(PicClkBfrStock.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height = parms.MATCH_PARENT;
        parms.width = parms.MATCH_PARENT;
        cameraPreview = (LinearLayout) dialog.findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(PicClkBfrStock.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(PicClkBfrStock.this)) {
            Toast toast = Toast.makeText(PicClkBfrStock.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(PicClkBfrStock.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }

            //mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			/*if(mCamera==null){
				mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}*/

            boolean isParameterSet = false;
            try {
                Camera.Parameters params = mCamera.getParameters();


                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                //Camera.Size size1 = sizes.get(0);
                for (int i = 0; i < sizes.size(); i++) {

                    if (sizes.get(i).width > size.width)
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
                int minExpCom = params.getMinExposureCompensation();
                int maxExpCom = params.getMaxExposureCompensation();

                if (maxExpCom > 4 && minExpCom < 4) {
                    params.setExposureCompensation(4);
                } else {
                    params.setExposureCompensation(0);
                }

                params.setAutoExposureLock(false);
                params.setAutoWhiteBalanceLock(false);
                //String supportedIsoValues = params.get("iso-values");
                // String newVAlue = params.get("iso");
                //  params.set("iso","1600");
                params.setColorEffect("none");
                params.set("scene-mode", "auto");
                params.setPictureFormat(ImageFormat.JPEG);
                params.setJpegQuality(70);
                params.setRotation(90);

                mCamera.setParameters(params);
                isParameterSet = true;
            } catch (Exception e) {

            }
            if (!isParameterSet) {
                Camera.Parameters params2 = mCamera.getParameters();
                params2.setPictureFormat(ImageFormat.JPEG);
                params2.setJpegQuality(70);
                params2.setRotation(90);

                mCamera.setParameters(params2);
            }

            setCameraDisplayOrientation(PicClkBfrStock.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }

        capture = (Button) dialog.findViewById(R.id.button_capture);

        flashImage = (ImageView) dialog.findViewById(R.id.flashImage);
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
                    isLighOn = false;
                } else {
                    // turn on flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    flashImage.setImageResource(R.drawable.flash_on);
                    mCamera.setParameters(params);

                    isLighOn = true;
                }
            }
        });

        final Button cancleCamera = (Button) dialog.findViewById(R.id.cancleCamera);
        cancelCam = cancleCamera;
        cancleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                v.setEnabled(false);
                capture.setEnabled(false);
                cameraPreview.setEnabled(false);
                flashImage.setEnabled(false);

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;
                dialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    public void setSavedImageToScrollView(Bitmap bitmap, String imageName, String valueOfKey, String clickedTagPhoto, boolean isSavedImage) {

        if (hmapCtgry_Imageposition != null && hmapCtgry_Imageposition.size() > 0) {
            if (hmapCtgry_Imageposition.containsKey(clickedTagPhoto)) {
                picAddPosition = hmapCtgry_Imageposition.get(clickedTagPhoto);
            } else {
                picAddPosition = 0;
            }
        } else {
            picAddPosition = 0;
            chkBox_Rtailer.setEnabled(false);
        }
        removePicPositin = picAddPosition;
        ArrayList<String> listClkdPic = new ArrayList<String>();
        if (hmapCtgryPhotoSection != null && hmapCtgryPhotoSection.containsKey(clickedTagPhoto)) {
            listClkdPic = hmapCtgryPhotoSection.get(clickedTagPhoto);
        }

        listClkdPic.add(imageName);
        hmapCtgryPhotoSection.put(clickedTagPhoto, listClkdPic);

        adapterImage.add(picAddPosition, bitmap, imageName + "^" + clickedTagPhoto);
        System.out.println("Picture Adapter" + picAddPosition);
        picAddPosition++;
        hmapCtgry_Imageposition.put(clickedTagPhoto, picAddPosition);
        if (!isSavedImage) {
            hmapPhotoDetailsForSaving.put(imageName, valueOfKey.split(Pattern.quote("~"))[0] + "^" + valueOfKey.split(Pattern.quote("~"))[1] + "^" + clickedTagPhoto);
        }


    }

    public boolean saveImageSection1() {
        boolean isPicSaved = false;
        dbengine.deletePicSectionImage(storeID, "1");
        if ((hmapPhotoDetailsForSaving != null) && (hmapPhotoDetailsForSaving.size() > 0)) {
            for (Map.Entry<String, String> entry : hmapPhotoDetailsForSaving.entrySet()) {
                String imageNameToSave = entry.getKey();
                String imagePath = entry.getValue().split(Pattern.quote("^"))[0];
                String dateTime = entry.getValue().split(Pattern.quote("^"))[1];
                String clkdTagPic = entry.getValue().split(Pattern.quote("^"))[2];
                //(String StoreID,String imageName,String imagePath,String ImageClicktime,String flgSctnPic,int Sstat)
                dbengine.savePicSectionImage(storeID, imageNameToSave, imagePath, dateTime, clkdTagPic, 1);
                isPicSaved = true;

            }
        }
        return isPicSaved;
    }

    @Override
    public void delPic(Bitmap bmp, String imageNameToDel) {

        String imageNameToDelVal = imageNameToDel.split(Pattern.quote("^"))[0];
        String tagPhoto = imageNameToDel.split(Pattern.quote("^"))[1];

        picAddPosition = hmapCtgry_Imageposition.get(tagPhoto);
        if (picAddPosition > 1) {
            removePicPositin = picAddPosition - 1;
        } else {
            removePicPositin = picAddPosition;
        }

        removePicPositin = removePicPositin - 1;
        picAddPosition = picAddPosition - 1;
        hmapCtgry_Imageposition.put(tagPhoto, picAddPosition);
        //	String photoToBeDletedFromPath=dbengine.getPdaPhotoPath(imageNameToDel);

        // dbengine.updatePhotoValidation("0", imageNameToDel);
        ArrayList<String> listClkdPic = new ArrayList<String>();
        if (hmapCtgryPhotoSection != null && hmapCtgryPhotoSection.containsKey(tagPhoto)) {
            listClkdPic = hmapCtgryPhotoSection.get(tagPhoto);
        }

        if (listClkdPic.contains(imageNameToDelVal)) {
            listClkdPic.remove(imageNameToDelVal);
            //  ImageAdapter adapterImage=hmapImageAdapter.get(tagPhoto);
            adapterImage.remove(bmp);
            hmapCtgryPhotoSection.put(tagPhoto, listClkdPic);
            if (listClkdPic.size() < 1) {
                hmapCtgryPhotoSection.remove(tagPhoto);
            }
        }
        if (hmapPhotoDetailsForSaving.containsKey(imageNameToDelVal)) {
            hmapPhotoDetailsForSaving.remove(imageNameToDelVal);
            dbengine.deleteSinglePicSectionImage(storeID, "1", imageNameToDelVal);

        }
        if (hmapPhotoDetailsForSaving.size() < 1) {
            chkBox_Rtailer.setEnabled(true);
        }
        //  String file_dj_path = Environment.getExternalStorageDirectory() + "/RSPLSFAImages/"+imageNameToDel;
        String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" + imageNameToDelVal;
        // dbengine.validateAndDelPic(storeId,businessUnitID,tagPhoto,imageNameToDelVal);
        File fdelete = new File(file_dj_path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                callBroadCast();
            } else {
            }
        }
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    @Override
    public void getProductPhotoDetail(String productIdTag) {

    }


    public void VideoPageOpenOrProductOrderPageOpen() {
        dbengine.open();
        String VideoData = dbengine.getVideoNameByStoreID(storeID, "2");
        dbengine.close();
        int flagPlayVideoForStore = 0;
        String Video_Name = "0";
        String VIDEO_PATH = "0";
        String VideoViewed = "0";
        String Contentype = "0";
        if (!VideoData.equals("0") && VideoData.contains("^")) {
            Video_Name = VideoData.toString().split(Pattern.quote("^"))[0];
            flagPlayVideoForStore = Integer.parseInt(VideoData.toString().split(Pattern.quote("^"))[1]);
            VideoViewed = VideoData.toString().split(Pattern.quote("^"))[2];
            Contentype = VideoData.toString().split(Pattern.quote("^"))[3];
        }

                /*  VIDEO_PATH= "/sdcard/WhatsApp/Media/WhatsApp Video/VID-20180303-WA0030.mp4";
                VIDEO_PATH= "/sdcard/VideoLTFOODS/SampleVideo5mb.mp4";*/
        VIDEO_PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.VideoFolder + "/" + Video_Name;
        Uri intentUri;
        //if videoShown check
        if (flagPlayVideoForStore == 1 && !(VIDEO_PATH.equals("0")) && VideoViewed.equals("0") && Contentype.equals("2")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = new File(VIDEO_PATH);
                intentUri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", file);
            } else {
                intentUri = Uri.parse(VIDEO_PATH);
            }


            if (intentUri != null) {
                Intent intent = new Intent(PicClkBfrStock.this, VideoPlayerActivityForStore.class);
                intent.putExtra("FROM", "FeedbackCompetitorActivity");
                intent.putExtra("STRINGPATH", VIDEO_PATH);
                intent.putExtra("storeID", storeID);
                intent.putExtra("SN", selStoreName);
                intent.putExtra("imei", imei);
                intent.putExtra("userdate", date);
                intent.putExtra("pickerDate", pickerDate);
                intent.putExtra("flgOrderType", 1);
                startActivity(intent);
                finish();
                // openVideoPlayerDialog(VIDEO_PATH);

            } else {
                Toast.makeText(PicClkBfrStock.this, "No video Found", Toast.LENGTH_LONG).show();
                passIntentToProductOrderFilter();
            }

        } else {

            passIntentToProductOrderFilter();
        }
    }

    public void passIntentToProductOrderFilter() {
        Intent nxtP4 = new Intent(PicClkBfrStock.this, ProductOrderFilterSearch.class);
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
