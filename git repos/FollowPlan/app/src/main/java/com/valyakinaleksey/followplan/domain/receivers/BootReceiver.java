package com.valyakinaleksey.followplan.domain.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.valyakinaleksey.followplan.domain.services.MyService;
import com.valyakinaleksey.followplan.util.Constants;


public class BootReceiver extends BroadcastReceiver {
    public static final int ACTION_BOOT = 4;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "BootReceiver onReceive");
        final Intent intent1 = new Intent(context, MyService.class);
        intent1.putExtra(MyService.TYPE, ACTION_BOOT);
        context.startService(intent1);
    }
}
