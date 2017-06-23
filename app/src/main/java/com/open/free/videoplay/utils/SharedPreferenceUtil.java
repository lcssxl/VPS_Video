package com.open.free.videoplay.utils;

import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ott.sdk.ottutil.android.ApplicationUtil;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.java.OTTSharedPreference;
import com.huawei.ott.sdk.security.OTTSecurity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * util for SharedPreference
 */
public final class SharedPreferenceUtil extends OTTSharedPreference
{
    private final static String TAG = SharedPreferenceUtil.class.getSimpleName();
    private static SharedPreferenceUtil sharedPreferenceUtil;
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * use enum the keep the SharedPreference key unique
     * enum use as key,need to transform string.
     * format:function type _ use
     */
    public interface Key
    {

        /**
         * the current app language
         */
        String APP_CURRENT_LANGUAGE = "APP_CURRENT_LANGUAGE";

        /**
         * app version code in manifest.xml
         */
        String APP_VERSION_CODE = "APP_VERSION_CODE";

        /**
         * user first time use app
         */
        String FIRST_TIME_USE_APP = "FIRST_TIME_USE_APP";

        /**
         * play parameter:last play channel id
         */
        String LAST_PLAY_CHANNEL_LIST = "LAST_PLAY_CHANNEL_LIST";

        /**
         * user preference
         */
        String USER_PREFERENCE_RECOMMEND_REFRESH = "USER_PREFERENCE_RECOMMEND_REFRESH";
        String USER_PREFERENCE_LIVE_TV_HOME_REFRESH = "USER_PREFERENCE_LIVE_TV_HOME_REFRESH";
        String DLNA_LOG_SWITCH = "DLNA_LOG_SWITCH";
        String OPEN_DYNAMIC_PAGE = "OPEN_DYNAMIC_PAGE";
        String OPEN_VR_FLAG = "OPEN_VR_FLAG";
        String PROFILE_GUEST = "guest";
        String DATA_CLEARED = "dataCleared";

        /**
         * GCM access key ID registration
         */
        String GCM_PROPERTY_REG_ID = "gcmRegistrationId";

        /**
         * Application version access key
         */
        String GCM_PROPERTY_APP_VERSION = "gcmAppVersion";

        /**
         * GCM registration expired time access key
         */
        String GCM_PROPERTY_ON_SERVER_EXPIRATION_TIME = "gcmOnServerExpirationTimeMs";

        /**
         * Profile ID access key
         */
        String GCM_PROFILE_ID = "gcmProfileId";

        String CATEGORY_ID_LIST = "CATEGORY_ID_LIST";

        String SUBJECT_ID_LIST = "SUBJECT_ID_LIST";
        /**
         * preview history
         */
        String PREVIEW_HISTORY = "preview_history";

        /**
         * receive message's switch
         */
        String RECEIVE_MESSAGE = "receive_message";
        /**
         * support cpvr
         */
        String SUPPORT_CPVR = "support_cpvr";
    }

    private SharedPreferenceUtil()
    {
        super("OTT_APP_SharedPreference");
    }

    public static SharedPreferenceUtil getInstance()
    {
        if (null == sharedPreferenceUtil)
        {
            sharedPreferenceUtil = new SharedPreferenceUtil();
        }
        return sharedPreferenceUtil;
    }

    public static void setVODScore(String key, int value)
    {
        sharedPreferenceUtil.putInt(key, value);
    }

    public static int getVODScore(String key, int value)
    {
        return sharedPreferenceUtil.getInt(key, value);
    }

    public static void setVODVerage(String key, String value)
    {
        sharedPreferenceUtil.putString(key, value);
    }

    public static String getVODVerage(String key, String value)
    {
        return sharedPreferenceUtil.getString(key, value);
    }

    public void saveDLNALogState(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.DLNA_LOG_SWITCH, value);
    }

    public boolean getDLNALogState()
    {
        return sharedPreferenceUtil.getBoolean(Key.DLNA_LOG_SWITCH, false);
    }

    public void saveDynamicPageState(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.OPEN_DYNAMIC_PAGE, value);
    }

    public boolean isDynamicPageState()
    {
        return sharedPreferenceUtil.getBoolean(Key.OPEN_DYNAMIC_PAGE, true);
    }

    public void saveVRFlag(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.OPEN_VR_FLAG, value);
    }

    public boolean isVRFlag()
    {
        return sharedPreferenceUtil.getBoolean(Key.OPEN_VR_FLAG, false);
    }

    /**
     * Analyzing the first start
     *
     * @return Whether to return the first start value
     */
    public boolean isFirstStartup()
    {
        return sharedPreferenceUtil.getBoolean(Key.FIRST_TIME_USE_APP, true);
    }

    /**
     * Save the first start state
     */
    public void saveIsFirstStartup(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.FIRST_TIME_USE_APP, value);
    }

    /**
     * Save the version number
     */
    public void saveVersionCode()
    {
        int code = ApplicationUtil.getCurrentAppVersionCode();
        sharedPreferenceUtil.putInt(Key.APP_VERSION_CODE, code);
    }

    /**
     * Get version number
     */
    public int getVersionCode()
    {

        return sharedPreferenceUtil.getInt(Key.APP_VERSION_CODE, 0);
    }

    /**
     * get current app language
     * the language is set by user
     *
     * @return language string
     */
    public String getCurrentLanguage()
    {
        return sharedPreferenceUtil.getString(Key.APP_CURRENT_LANGUAGE, "en");
    }

    public void setLastPlayChannelID(String profileId, String channelId)
    {
        if (TextUtils.isEmpty(profileId) || TextUtils.isEmpty(channelId))
        {
            return;
        }
        String channelJson = OTTSecurity.decrypt(sharedPreferenceUtil.getString(Key
                .LAST_PLAY_CHANNEL_LIST, ""));
        Map<String, String> channelMap = null;
        try
        {
            if (!TextUtils.isEmpty(channelJson))
            {
                channelMap = mapper.readValue(channelJson, new TypeReference<Map<String, String>>
                        () {});
                if (channelMap == null)
                {
                    channelMap = new HashMap<>();
                }
                channelMap.put(profileId, channelId);
                channelJson = mapper.writeValueAsString(channelMap);
                sharedPreferenceUtil.putString(Key.LAST_PLAY_CHANNEL_LIST, OTTSecurity.encrypt
                        (channelJson));
            }
            else
            {
                channelMap = new HashMap<>();
                channelMap.put(profileId, channelId);
                channelJson = mapper.writeValueAsString(channelMap);
                sharedPreferenceUtil.putString(Key.LAST_PLAY_CHANNEL_LIST, OTTSecurity.encrypt
                        (channelJson));
            }
        }
        catch (IOException e)
        {
            DebugLog.error(TAG, e);
        }
    }

    public String getLastPlayChannelID(String profileId)
    {
        String channelId = "";
        String channelJson = OTTSecurity.decrypt(sharedPreferenceUtil.getString(Key
                .LAST_PLAY_CHANNEL_LIST, ""));
        Map<String, String> channelMap = null;
        if (!TextUtils.isEmpty(channelJson))
        {
            try
            {
                channelMap = mapper.readValue(channelJson, new TypeReference<Map<String, String>>
                        () {});
                if (channelMap != null)
                {
                    channelId = channelMap.get(profileId);
                }
            }
            catch (IOException e)
            {
                DebugLog.error(TAG, e);
            }
        }

        return channelId;
    }

    public static void setRecommendUserPreference(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.USER_PREFERENCE_RECOMMEND_REFRESH, value);
    }

    public static boolean getRecommendUserPreference()
    {
        boolean isSetUserPreference;
        isSetUserPreference = sharedPreferenceUtil.getBoolean(Key
                .USER_PREFERENCE_RECOMMEND_REFRESH, false);
        setRecommendUserPreference(false);
        return isSetUserPreference;
    }

    public static void setTVHomeUserPreference(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.USER_PREFERENCE_LIVE_TV_HOME_REFRESH, value);
    }

    public static boolean getTVHomeUserPreference()
    {
        boolean isSetUserPreference;
        isSetUserPreference = sharedPreferenceUtil.getBoolean(Key
                .USER_PREFERENCE_LIVE_TV_HOME_REFRESH, false);
        setTVHomeUserPreference(false);
        return isSetUserPreference;
    }

    public static void setMessagePreference(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.RECEIVE_MESSAGE, value);
    }

    public static boolean getMessagePreference()
    {
        return sharedPreferenceUtil.getBoolean(Key.RECEIVE_MESSAGE, true);
    }

    public void saveCPVRFlag(boolean value)
    {
        sharedPreferenceUtil.putBoolean(Key.SUPPORT_CPVR, value);
    }

    public boolean isSupportCPVR()
    {
        return sharedPreferenceUtil.getBoolean(Key.SUPPORT_CPVR, false);
    }
}