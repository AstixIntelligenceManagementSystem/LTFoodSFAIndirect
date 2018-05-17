package project.astix.com.ltfoodsfaindirect;

/**
 * Created by Shivam on 11/9/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoPlayerActivityForStore extends Activity {
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            return true;

        }
        if(keyCode==KeyEvent.KEYCODE_HOME)
        {

        }
        if(keyCode==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        if(keyCode== KeyEvent.KEYCODE_SEARCH)
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public String storeID;
    public String imei;
    public String date;
    public String pickerDate;
    public String selStoreName;
    public String FROM;
    boolean  flagvideoPlayed=false;
    public String STRINGPATH;
    boolean thumnailClickedToPlay=false;
    DBAdapterKenya dbengine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_layout_for_store);
        dbengine=new DBAdapterKenya(VideoPlayerActivityForStore.this);
        Intent passedvals= getIntent();
        STRINGPATH=  passedvals.getStringExtra("STRINGPATH");
        FROM=  passedvals.getStringExtra("FROM");
        if(FROM.equals("FeedbackCompetitorActivity")){
            storeID = passedvals.getStringExtra("storeID");
            imei = passedvals.getStringExtra("imei");
            date = passedvals.getStringExtra("userdate");
            pickerDate = passedvals.getStringExtra("pickerDate");
            selStoreName = passedvals.getStringExtra("SN");
        }


//***************** All code is inside onResume ***********-----------------------


    }


    public void passIntentToProductOrderFilter(){
        Intent nxtP4 = new Intent(VideoPlayerActivityForStore.this,ProductOrderFilterSearch.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        final Button btnNextDialog =(Button)findViewById(R.id.btnNextDialog);
        final Button btnSkip =(Button)findViewById(R.id.btnSkip);
      final   VideoView videoView =(VideoView)findViewById(R.id.videoView1);
     final    LinearLayout ll_parentOfThumnail =(LinearLayout)findViewById(R.id.ll_parentOfThumnail);

        MediaController mediaController= new MediaController(VideoPlayerActivityForStore.this);
        mediaController.setAnchorView(videoView);
        //specify the location of media file
        Bitmap bitmap2;
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            File file = new File(STRINGPATH);
             bitmap2 = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            uri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", file);
        }
        else{
             uri=Uri.parse(STRINGPATH);
             bitmap2 = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        }


        //  Uri uri=new Uri(STRINGPATH);
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("Completely played");
                try{
                    dbengine.open();
                    dbengine.UpdateFlagVideoFullyPlayed(storeID,"2","1");
                    dbengine.close();
                }
                catch (Exception e){

                }


            }
        });

        if(FROM.equals("FeedbackCompetitorActivity") ){
            ImageView imageThumnail =(ImageView)findViewById(R.id.imageThumnail);
            imageThumnail.setImageBitmap(bitmap2);
            imageThumnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumnailClickedToPlay=true;//means user clicked for play video
                    ll_parentOfThumnail.setVisibility(View.GONE);
                    btnNextDialog.setVisibility(View.VISIBLE);
                    long syncTIMESTAMP = System.currentTimeMillis();
                    Date dateobj = new Date(syncTIMESTAMP);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
                    String ClickedDateTime = df.format(dateobj);
                    dbengine.open();
                    dbengine.UpdateFlagVideoIsShownToUser(storeID,"2",ClickedDateTime);
                    dbengine.close();
                    videoView.start();

                }
            });
        }
        else{
            ll_parentOfThumnail.setVisibility(View.GONE);
            //video play code
            long syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String ClickedDateTime = df.format(dateobj);
            dbengine.open();
            dbengine.UpdateFlagVideoIsShownToUser(storeID,"2",ClickedDateTime);
            dbengine.close();
            videoView.start();
            //video play code end
        }

        //Creating MediaController


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FROM.equals("FeedbackCompetitorActivity")){//meens came from actual stock page
                    passIntentToProductOrderFilter();
                }
                else{

                    finish();
                    //if from productOrder page simple finish app

                }

            }
        });
        if(FROM.equals("FeedbackCompetitorActivity")){
            btnNextDialog.setVisibility(View.GONE);
        }

        if(FROM.equals("ProductOrderFilterSearch")){
            btnNextDialog.setText("CLOSE");
        }
        if(!(ll_parentOfThumnail.getVisibility()==View.VISIBLE)){
            btnNextDialog.setVisibility(View.VISIBLE);
        }
        btnNextDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FROM.equals("FeedbackCompetitorActivity")){//meens came from actual stock page
                    passIntentToProductOrderFilter();
                }
                else{

                    finish();
                    //if from productOrder page simple finish app

                }


            }
        });
    }
}