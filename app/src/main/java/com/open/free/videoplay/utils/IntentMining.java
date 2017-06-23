package com.open.free.videoplay.utils;

import android.content.Intent;
import android.os.Bundle;

import com.huawei.ott.sdk.ottutil.android.DebugLog;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Intent correlation method
 */
public class IntentMining
{
    private static final String TAG = IntentMining.class.getSimpleName();

    public static String getAction(Intent intent)
    {
        return null != intent ? intent.getAction() : null;
    }

    public static String getStringExtra(Intent intent, String key)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                return intent.getStringExtra(key);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
        return null;
    }

    public static int getIntExtra(Intent intent, String key, int defaultValue)
    {
        return null != intent ? intent.getIntExtra(key, defaultValue) : defaultValue;
    }

    public static float getFloatExtra(Intent intent, String key, float defaultValue)
    {
        return null != intent ? intent.getFloatExtra(key, defaultValue) : defaultValue;
    }

    public static boolean getBooleanExtra(Intent intent, String key, boolean defaultValue)
    {
        return null != intent ? intent.getBooleanExtra(key, defaultValue) : defaultValue;
    }

    public static ArrayList<String> getStringArrayListExtra(Intent intent, String key)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                return intent.getStringArrayListExtra(key);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
        return null;
    }

    public static Serializable getSerializableExtra(Intent intent, String key)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                return intent.getSerializableExtra(key);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
        return null;
    }

    public static Bundle getExtras(Intent intent)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                return intent.getExtras();
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
        return null;
    }

    public static Bundle getBundleExtra(Intent intent, String key)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                return intent.getBundleExtra(key);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
        return null;
    }

    public static void putExtra(Intent intent, String key, boolean value)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                intent.putExtra(key, value);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
    }

    public static void putExtra(Intent intent, String key, String value)
    {
        // Important: security needed, the try can't be delete, as the android OS's vulnerabilities
        try
        {
            if (null != intent)
            {
                intent.putExtra(key, value);
            }
        }
        catch (Exception e)
        {
            DebugLog.error(TAG, e);
        }
    }
}
