package com.open.free.videoplay.module.config;

import com.huawei.ott.sdk.OTTSDK;
import com.huawei.ott.sdk.ottutil.android.ApplicationUtil;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.java.FileUtil;
import com.huawei.ott.sdk.ottutil.java.JsonParse;
import com.open.free.videoplay.R;
import com.open.free.videoplay.utils.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Read the application configuration tools
 *
 */
public class ConfigUtil
{
    private static final String TAG = ConfigUtil.class.getSimpleName();
    private static final String FILENAME = "config.json";

    private static final int DEFAULT_VERSION = 1;

    private static AppConfig appConfig=createAppConfig();

    private static final Object lock = new Object();


    /**
     * Get the applications stored configuration information
     */
    public static AppConfig getConfig()
    {
            return appConfig;
    }

    private static AppConfig createAppConfig(){
        AppConfig appConfig = readConfig();
        int currentVersion = ApplicationUtil.getCurrentAppVersionCode();
        DebugLog.debug(TAG,currentVersion+"");
        if (appConfig.getVersionCode() < currentVersion)
        {
            deleteBackupConfigFile();
            appConfig = readConfig();
            appConfig.setVersionCode(currentVersion);
            saveToFile(appConfig);
        }
        return appConfig;
    }
    public static void refreshConfig()
    {
        appConfig = readConfig();
    }


    /**
     * Reads the configuration file, read the backup files priority, and then read in the apk
     */
    private static AppConfig readConfig()
    {
        InputStream is = null;
        AppConfig appConfig = null;
        try
        {
            try
            {
                String filePath = getBackupConfigFilePath();
                File file = new File(filePath);
                if (file.exists())
                {
                    is = new FileInputStream(file);
                }
                else
                {
                    DebugLog.debug(TAG, "get config from assets");
                    is = OTTSDK.getApplicationContext().getAssets().open(FILENAME);
                }

                String content = FileUtil.getContent(is);
                appConfig = JsonParse.toObject(AppConfig.class, content);
            }
            finally
            {
                if (is == null)
                {
                    DebugLog.error(TAG, "cannot found config file in assets");
                }
                closeInputStream(is);
            }
        }
        catch (IOException e)
        {
            DebugLog.error(TAG, e);
        }
        if(null == appConfig)
        {
            appConfig = new AppConfig();
        }
        return appConfig;
    }


    private static void deleteBackupConfigFile()
    {
        File file = new File(getBackupConfigFilePath());
        if (file.exists())
        {
            if (file.delete())
            {
                DebugLog.debug(TAG, "file delete success");
            }
        }
    }

    /**
     * Save configuration information to a backup file
     */
    public static void saveToFile(AppConfig appConfig)
    {
        String content = JsonParse.toJsonString(appConfig);
        String filePath = getBackupConfigFilePath();
        FileUtil.saveContentToFile(filePath, content);
        refreshConfig();
    }

    /**
     * Get the backup file path
     */
    private static String getBackupConfigFilePath()
    {
        return OTTSDK.getApplicationContext().getFilesDir() + File.separator + FILENAME;
    }

    private static void closeInputStream(InputStream is)
    {
        if (is != null)
        {
            try
            {
                is.close();
                is = null;
            }
            catch (IOException e)
            {
                DebugLog.error(TAG, e);
            }
        }
    }

    public static List<String> getLogLevelSAtrings()
    {
        List<String> logLevelStrings = new ArrayList<>();
        logLevelStrings.add(Strings.getInstance().getString(R.string.debug));
        logLevelStrings.add(Strings.getInstance().getString(R.string.info));
        logLevelStrings.add(Strings.getInstance().getString(R.string.warning));
        logLevelStrings.add(Strings.getInstance().getString(R.string.error));
        logLevelStrings.add(Strings.getInstance().getString(R.string.none));
        return logLevelStrings;
    }
}