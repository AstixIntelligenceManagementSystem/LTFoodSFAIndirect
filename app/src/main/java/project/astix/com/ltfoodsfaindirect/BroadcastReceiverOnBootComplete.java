package project.astix.com.ltfoodsfaindirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by ALOK on 9/7/2017.
 */

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            DBAdapterKenya helperDb=new DBAdapterKenya(context);
           String prvsStoreId=helperDb.getPreviousShownPopUpStoreId();

            if(!TextUtils.isEmpty(prvsStoreId))
            {

                helperDb.updateisRestartDoneByDSR(prvsStoreId);
               /* Intent intentTemp=new Intent(context,Splash_Activity.class);
                intentTemp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentTemp);*/
            }
        }

    }
}
