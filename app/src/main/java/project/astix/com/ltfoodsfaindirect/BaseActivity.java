package project.astix.com.ltfoodsfaindirect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sunil on 11/30/2017.
 */
// Here is a generic baseAcitvity class example

public class BaseActivity extends Activity
{
    private ProgressDialog mProgressDialog;
    ServiceWorker newservice = new ServiceWorker();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


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



    //showProgress(getResources().getString(R.string.RetrivingDataMsg));

    protected void showProgress(String msg)
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
             dismissProgress();
        }
        else
        {
             mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.genTermPleaseWaitNew), msg);
        }
    }

    protected void dismissProgress()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }



    //showAlertSingleButtonInfo(getResources().getString(R.string.PleaseSelectDistributor));
    public void showAlertSingleButtonInfo(String msg)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.AlertDialogHeaderMsg))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.info_ico)
                .setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void showAlertSingleButtonError(String msg)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.AlertDialogHeaderErrorMsg))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.error_ico)
                .setPositiveButton(getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public String[] changeHmapToArrayKey(HashMap hmap)
    {
        String[] stringArray=new String[hmap.size()];
        int index=0;
        if(hmap!=null)
        {
            Set set2 = hmap.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext())
            {
                Map.Entry me2 = (Map.Entry)iterator.next();
                stringArray[index]=me2.getKey().toString();
                index=index+1;
            }
        }
        return stringArray;
    }

    public String[] changeHmapToArrayValue(HashMap hmap)
    {
        String[] stringArray=new String[hmap.size()];
        int index=0;
        if(hmap!=null)
        {
            Set set2 = hmap.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext())
            {
                Map.Entry me2 = (Map.Entry)iterator.next();
                stringArray[index]=me2.getValue().toString();
                index=index+1;
            }
        }
        return stringArray;
    }

}
