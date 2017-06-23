package com.open.free.videoplay.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.open.free.videoplay.utils.IntentMining;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handling of GCM messages.
 * Created by lcssx on 6/22/2017.
 */
public class GCMBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = GCMBroadcastReceiver.class.getSimpleName();
    private static final String ACTION = "com.google.android.c2dm.intent.RECEIVE";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = IntentMining.getAction(intent);
        DebugLog.info(TAG, DebugLog.Scenario.FCM, DebugLog.Scenario.NOTIFICATION, "intent action " +
                "= " + action);
        this.context = context;
        if (ACTION.equals(action))
        {
            handleGcmReceive(intent);
        }
    }

    /**
     * com.google.android.c2dm.intent.RECEIVE
     *
     * @param intent intent
     */
    private void handleGcmReceive(Intent intent)
    {
        DebugLog.debug(TAG, "handleGcmReceive");

        if (null == intent)
        {
            DebugLog.warn(TAG, "intent is null, do nothing and return.");
            return;
        }

        /*GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        DebugLog.debug(TAG, DebugLog.Scenario.FCM, "getMessageType(),onReceive: message_type=" +
                messageType);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
        {
            DebugLog.warn(TAG, "Send error");
        }
        else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
        {
            DebugLog.warn(TAG, "Deleted messages on server");
        }
        else
        {
            Bundle bundle = intent.getExtras();

            if (bundle == null)
            {
                DebugLog.error(TAG, "generateReminderInfoFromGCM, intent.getExtras() is null.");
                return;
            }

            ReminderNotifier notifier = new ReminderNotifier();
            String title = bundle.getString(ReminderNotifier.TITLE);

            // reminder: {"Command":"103","channelID":"1","programmeID":"204"}
            // tvms: just a String message
            String content = bundle.getString(ReminderNotifier.CONTENT);
            String validTime = bundle.getString(ReminderNotifier.VALID_TIME);
            String mode = bundle.getString(ReminderNotifier.MODE);
            String domain = bundle.getString(ReminderNotifier.DOMAIN);
            notifier.generateInfoFromGCM(domain, title, mode, validTime, content);
        }*/
    }
}
