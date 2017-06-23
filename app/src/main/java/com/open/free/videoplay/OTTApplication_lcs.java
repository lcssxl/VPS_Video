package com.open.free.videoplay;

import android.app.Application;

import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.open.free.videoplay.module.config.ConfigUtil;

/**
 * Created by lcssx on 6/22/2017.
 */

public class OTTApplication_lcs extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.initLogFileLevel(BuildConfig.DEBUG ? ConfigUtil.getConfig().getLogLevel() :
                DebugLog.LogLevel.OFF.getValue());
    }
}
