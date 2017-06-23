package com.open.free.videoplay;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.huawei.ca.OTTCA;
import com.huawei.dmpbase.DmpBase;
import com.huawei.ott.sdk.LibraryManager.LibraryManager;
import com.huawei.ott.sdk.LibraryManager.LoadLibraryException;
import com.huawei.ott.sdk.OTTNDKVersion;
import com.huawei.ott.sdk.OTTSDK;
import com.huawei.ott.sdk.OTTSDKVersion;
import com.huawei.ott.sdk.bec.HTTPCommunication;
import com.huawei.ott.sdk.errorcode.MessageParse;
import com.huawei.ott.sdk.image.ImageLoaderUtil;
import com.huawei.ott.sdk.ottutil.android.ApplicationUtil;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.android.DeviceInfo;
import com.huawei.ott.sdk.ottutil.android.PathManager;
import com.huawei.ott.sdk.resourcecustomized.utils.ResourceUtils;
import com.huawei.ott.sdk.tracker.LogTracker;
import com.huawei.ott.vsp.OTTVSP;
import com.huawei.ott.vsp.OTTVSPVersion;
import com.huawei.ott.vsp.http.AsyncTaskPool;
import com.huawei.ott.vsp.http.VideoAppClient;
import com.huawei.playerinterface.version.PlayerVersion;
import com.open.free.videoplay.module.config.AppConfig;
import com.open.free.videoplay.module.config.ConfigUtil;
import com.open.free.videoplay.service.certificate.CertificateService;
import com.open.free.videoplay.utils.Colors;
import com.open.free.videoplay.utils.SharedPreferenceUtil;
import com.open.free.videoplay.utils.Strings;
import com.open.free.videoplay.utils.WhiteLabelParse;

import java.util.List;
import java.util.Locale;

import roboguice.RoboGuice;

/**
 * OTT Application Application
 * After the first load application  starts classes for App starts, destruction of life-cycle
 * management
 *
 * @see Application
 * @since 4.0
 */
public class OTTApplication extends Application
{
    private static final String TAG = OTTApplication.class.getSimpleName();
    private static OTTApplication instance;
    private static String appInfo = null;

    static
    {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    public final boolean OFF_RECORD_REMINDER = false;
    private boolean isShowUpGradeDialog;
    private boolean isForceUpgrade;
    private boolean isLogout = false;
    /**
     * Whether the upgrade service is started
     */
    private boolean isUpgradeServiceStarted = false;
    private boolean isUpdatedVersionExisted = false;
    /**
     * Whether remind upgrade on MY home page
     */
    private boolean isRemindUpgrade = false;
    /**
     * setting show updata
     */
    private boolean isShowUpdated = false;
    private boolean isLoginDialogDisplayed = false;
    private boolean isShowLoginDialog;

    /**
     * Whether start customizedFromUpdateService
     */
    private boolean isStartedCustomizedFromUpdateService = false;

    /**
     * Values of DeviceType
     */
    public interface DeviceType
    {
        /**
         * Android Phone
         */
        String PHONE = "AndroidPhone";

        /**
         * Android Pad
         */
        String PAD = "AndroidPad";

        /**
         * Android Phone PlayReady
         */
        String PHONE_PLAYREADY = "AndroidPhone_PlayReady";

        /**
         * Android Pad PlayReady
         */
        String PAD_PLAYREADY = "AndroidPad_PlayReady";

        /**
         * Android Phone Widevine
         */
        String PHONE_WIDEVINE = "AndroidPhone_Widevine";

        /**
         * Android Pad Widevine
         */
        String PAD_WIDEVINE = "AndroidPad_Widevine";
    }

    public OTTApplication()
    {
        instance = this;
    }

    public static OTTApplication getInstance()
    {
        return instance;
    }

    /**
     * get errormessage
     */
    public static String getArrawMessage(String message)
    {
        String tempError = message;
        if (tempError != null)
        {
            String[] splits = tempError.split("\\(");
            if (splits != null && splits.length > 0)
            {
                tempError = splits[0];
            }
        }
        return tempError;
    }

    /**
     * get the app info
     */
    public static String getAppInfo()
    {
        return appInfo;
    }


    @Override
    public void onCreate()
    {
        isShowUpGradeDialog = true;
        OTTSDK.init(this);
        DebugLog.initLogcatLevel(BuildConfig.DEBUG ? ConfigUtil.getConfig().getLogLevel() :
                DebugLog.LogLevel.OFF.getValue());
        DebugLog.initLogFileLevel(BuildConfig.DEBUG ? ConfigUtil.getConfig().getLogLevel() :
                DebugLog.LogLevel.OFF.getValue());

        OTTVSP.init(ConfigUtil.getConfig().getEdsURL(), ConfigUtil.getConfig().getSparedEdsURL());
        OTTVSP.setCheckHttps(!BuildConfig.DEBUG);
        OTTVSP.setGuestControl(true);

        DebugLog.info(TAG, "onCreate()");
        super.onCreate();

        // Get the version number to be comparative
        float resourceVersionWithApk = ConfigUtil.getConfig().getResourceVersion();
        float currentResourceVersion = ResourceUtils.getResourcePkgVersion();
        DebugLog.debug(TAG, "resourceVersionWithApk=" + resourceVersionWithApk + ", " +
                "currentResourceVersion=" + currentResourceVersion);
        if (resourceVersionWithApk > currentResourceVersion)
        {
            ResourceUtils.setResourcePkgVersion(resourceVersionWithApk);
            ResourceUtils.setNeedUseResourcePkg("");
        }
        else
        {
            // Set the white label resource bundle loading logo
            ResourceUtils.setNeedLoadResource();
        }

        initCurrentLanguage();

        // Set the current UI according dp accordance pad (> = 600dp) or phone (<600dp) shows,
        // the principle of proximity
        initDeviceType();

        // init video app sdk
        initVideoAppSDK();

        initAppData();

        // init fps function.
        initFPS();

        // init vsp interface for cache the packet of http
        initVSPCache();
    }

    private void initCurrentLanguage()
    {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        DebugLog.debug(TAG, "System language=" + language);

        String appCfgLang = ConfigUtil.getConfig().getLanguage();
        if (SharedPreferenceUtil.getInstance().isFirstStartup() && !TextUtils.isEmpty(appCfgLang))
        {
            String[] langArray = appCfgLang.split(",");
            ResourceUtils.setLanguage(langArray[0]);
            for (int i = 0; i < langArray.length; i++)
            {
                DebugLog.debug(TAG, "langArray[" + i + "]=" + langArray[i]);
                if (langArray[i].contains(language))
                {
                    ResourceUtils.setLanguage(language);
                    break;
                }
            }
        }
        ResourceUtils.setUserSelectedLanguage();
    }

    private void initAppData()
    {
        DebugLog.info(TAG, "initAppData()");
        AsyncTaskPool.getInstance().clearAll();
        new Thread("Thread - OTTApplication")
        {
            @Override
            public void run()
            {
                DebugLog.info(TAG, "initAppData()|run()");

                // Print application information
                showAppVersion();

                // Init image loader
                ImageLoaderUtil.initImageLoader(OTTApplication.this);

                /*// Read iso code file
                ISOCodeParse.readISOConfig();
                ISOCodeParse.readISO639_1_2();

                // Initialize twitter
                TwitterAuthConfig authConfig = new TwitterAuthConfig(Strings.getInstance()
                        .getString(R.string.twitter_consumer_k), Strings.getInstance().getString
                        (R.string.twitter_consumer_s));
                Fabric.with(OTTApplication.this, new TwitterCore(authConfig), new TweetComposer());

                UpgradeService.deleteAPKFile();*/
            }
        }.start();
    }

    @Override
    public void onTerminate()
    {
        DebugLog.info(TAG, "onTerminate()");
        super.onTerminate();

        DmpBase.close();
    }


    /**
     * According to the current res device.xml initialized current device type: PAD / PHONE
     */
    private void initDeviceType()
    {
        DebugLog.debug(TAG, "initDeviceType()");
        // Set the current UI according dp accordance pad (> = 600dp) or phone (<600dp) shows,
        // the principle of proximity
        if (getResources().getBoolean(R.bool.isPad))
        {
            DeviceInfo.setPad(true);
        }
        else
        {
            DeviceInfo.setPad(false);
        }
    }

    /**
     * White Label initialization properties associated resource files
     */
    public void initCustomizedResource()
    {
        DebugLog.debug(TAG, "initCustomizedResource()");

        WhiteLabelParse.getInstance().parse();
        Strings.getInstance().parse(ResourceUtils.getLanguage());
        MessageParse.getInstance().parse(ResourceUtils.getLanguage(), ResourceUtils
                .isNeedLoadResource());
        Colors.getInstance().parse();
    }

    /**
     * Initialize ott video library unified sdk
     */
    private void initVideoAppSDK()
    {
        DebugLog.debug(TAG, "initVideoAppSDK()");
        try
        {
            String certificateName = "ott.pem";
            new CertificateService().writeCertificate(certificateName);
            LibraryManager.getInstance().loadLibraries(this);
            if (BuildConfig.DEBUG)
            {
                LogTracker.getInstance().disableVerifyCert();
            }
            // Address certificate timeout (s), whether or calibration certificate (config
            // configuration file settings)
            boolean verifyCer = ConfigUtil.getConfig().isVerifyCerEnable();
            HTTPCommunication.getInstance().config(PathManager.getCertificatePath() +
                    certificateName, VideoAppClient.HTTP_TIME_OUT, verifyCer);
            DebugLog.debug(TAG, "HTTPCommunication config: setHttpPersistentConnection = " +
                    ConfigUtil.getConfig().isHttpPersistentConnection());
            HTTPCommunication.getInstance().setHttpPersistentConnection(ConfigUtil.getConfig()
                    .isHttpPersistentConnection());
            DebugLog.debug(TAG, "HTTPCommunication  config:timeout=" + VideoAppClient
                    .HTTP_TIME_OUT + "s ", "verify certificate=" + verifyCer);
        }
        catch (LoadLibraryException e)
        {
            DebugLog.error(TAG, e);
        }
    }

    /**
     * init the fps for UI refresh
     * this method need to init on main thread
     */
    private void initFPS()
    {
        /*if (ConfigUtil.getConfig().isFPS() && Build.VERSION.SDK_INT >= Build.VERSION_CODES
                .JELLY_BEAN)
        {
            FPSViewer.FPSViewerSession fpsViewerSession = FPSViewer.initialize(this);
            fpsViewerSession.setInterval(250);
            fpsViewerSession.setTextAlpha(0.5f);
            fpsViewerSession.setTextColor(Colors.getInstance().getColor(OTTSDK
                    .getApplicationContext().getResources(), R.color.C03));
            fpsViewerSession.setTextSize(16f);
            fpsViewerSession.setViewGravity(FPSViewer.FPSViewGravity.TOP_LEFT);
            fpsViewerSession.setListener(new FPSListener()
            {
                @Override
                public void fpsChanged(double fps)
                {
                    DebugLog.debugSimple(TAG, "Current FPS = " + fps);
                }
            });
            fpsViewerSession.start();
        }*/
    }

    /**
     * init the vsp cache interface
     */
    private void initVSPCache()
    {
        // one page
        HTTPCommunication.getInstance().registerInterface("QueryHomeData");
        HTTPCommunication.getInstance().registerInterface("QueryOTTLiveTVHomeData");
        HTTPCommunication.getInstance().registerInterface("VodHome");
        HTTPCommunication.getInstance().registerInterface("QueryBookmark");
        HTTPCommunication.getInstance().registerInterface("QueryChannelSubjectList");

        // secondary page
        HTTPCommunication.getInstance().registerInterface("QueryMoreRecommend");
        HTTPCommunication.getInstance().registerInterface("QueryVODListBySubject");
        HTTPCommunication.getInstance().registerInterface("QueryRecmContent");
        HTTPCommunication.getInstance().registerInterface("QueryPlaybillList");

        HTTPCommunication.getInstance().registerInterface("QueryRecmVODList");
        HTTPCommunication.getInstance().registerInterface("QueryHotPlaybill");
        HTTPCommunication.getInstance().registerInterface("GetContentConfig");
        HTTPCommunication.getInstance().registerInterface("QuerySubjectVODBySubjectID");
    }

    public boolean isShowUpGradeDialog()
    {
        return isShowUpGradeDialog;
    }

    public void setShowUpGradeDialog(boolean isShowUpGradeDialog)
    {
        this.isShowUpGradeDialog = isShowUpGradeDialog;
    }

    public boolean isForceUpgrade()
    {
        return isForceUpgrade;
    }

    public void setForceUpgrade(boolean isForceUpgrade)
    {
        this.isForceUpgrade = isForceUpgrade;
    }

    public boolean isLogout()
    {
        return isLogout;
    }

    public void setIsLogout(boolean isLogout)
    {
        this.isLogout = isLogout;
    }

    /**
     * get top activity name
     *
     * @return activity name
     */
    public String getTopActivityName()
    {
        DebugLog.debug(TAG, "getTopActivityName()");
        String topActivityName = "";
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am)
        {
            return topActivityName;
        }

        List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
        if (null == taskInfoList || taskInfoList.isEmpty())
        {
            return topActivityName;
        }
        ComponentName cn = taskInfoList.get(0).topActivity;
        if (null == cn)
        {
            return topActivityName;
        }
        return cn.getClassName();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        DebugLog.info(TAG, DebugLog.Scenario.DEVICE_CONFIG_CHANGE);
        super.onConfigurationChanged(newConfig);
        ResourceUtils.setUserSelectedLanguage();
    }


    /**
     * In the form of logs integrated display terminal version number and version number of
     * third-party components
     * The main concern volatile components and upgrade may exist, such as facebook, twitter etc.
     *
     * @return Version number information
     */
    private void showAppVersion()
    {
        DebugLog.debug(TAG, "showAppVersion()");
        String packageName = this.getPackageName();
        // the different version need to split with ";"
        StringBuffer information = new StringBuffer();
        information.append(DebugLog.Scenario.APP_VERSION);
        information.append("packageName:" + packageName);
        information.append(";VersionName:" + ApplicationUtil.getCurrentAppVersionName());
        information.append(";" + DebugLog.Scenario.DMP_PLAYER_VERSION);
        information.append(";PLAYER_VERSION:" + PlayerVersion.getVersion());
        information.append(";CA:" + OTTCA.getVer());
        information.append(";DmpBase:" + DmpBase.getDmpBaseVer());
        information.append(";" + DebugLog.Scenario.OPEN_SOURCE_VERSION);
        information.append(";Facebook:V4.3.0");
        information.append(";Twitter:V1.5.0");
        information.append(";Google play service:V3.2.25");
        information.append(";" + DebugLog.Scenario.MSA_SDK_VERSION);
        information.append(";" + OTTSDKVersion.getVersion());
        information.append(";" + OTTVSPVersion.getVersion());
        information.append(";" + OTTNDKVersion.getVersion());
        information.append(";" + DebugLog.Scenario.DEVICE_INFO);
        information.append(";Device information:" + DeviceInfo.getDeviceInformation());
        appInfo = information.toString();
        DebugLog.info(TAG, appInfo);
    }

    public boolean isNetworkAbnormal()
    {
        boolean isNetworkAbnormal = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (null != cm)
        {
            networkInfo = cm.getActiveNetworkInfo();
        }

        if (null != networkInfo)
        {
            if (!networkInfo.isAvailable())
            {
                isNetworkAbnormal = true;
            }
        }
        else
        {
            isNetworkAbnormal = true;
        }
        return isNetworkAbnormal;
    }

    /**
     * Check whether the password is valid
     *
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    public boolean isPasswordValid(String oldPassword, String newPassword, String confirmPassword)
    {
        /*if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword) && !TextUtils
                .isEmpty(confirmPassword))
        {
            Session session = SessionService.getInstance().getSession();

            if (!InterfaceParamCheckUtil.isCheckPassword(newPassword))
            {
                DebugLog.info("InterfaceParamCheckUtil", "check == " + false);
                return false;
            }
            String loginId = "";
            if (session != null && session.getProfile() != null)
            {
                loginId = session.getProfile().getLoginName();
            }
            String decString = new StringBuilder(loginId).reverse().toString();
            if (newPassword.equals(decString) || newPassword.equals(loginId))
            {
                OTTToast.showToast(Strings.getInstance().getString(R.string
                        .usename_positive_reverse_order_agreement));
                return false;
            }
            if (newPassword.equals(oldPassword))
            {
                OTTToast.showToast(Strings.getInstance().getString(R.string
                        .message_password_old_new_same_error));
                return false;
            }

            if (!confirmPassword.equals(newPassword))
            {
                OTTToast.showToast(Strings.getInstance().getString(R.string
                        .message_password_match_error));
                return false;
            }
        }*/

        return true;
    }

    public boolean isPasswrodVaild(String password, String loginName)
    {
        /*Session session = SessionService.getInstance().getSession();

        if (!InterfaceParamCheckUtil.isCheckPassword(password))
        {
            DebugLog.info("InterfaceParamCheckUtil", "check == " + false);
            return false;
        }
        String decString = new StringBuilder(loginName).reverse().toString();
        if (password.equals(decString) || password.equals(loginName))
        {
            OTTToast.showToast(Strings.getInstance().getString(R.string
                    .usename_positive_reverse_order_agreement));
            return false;
        }*/
        return true;
    }

    public boolean isUpgradeServiceStarted()
    {
        return isUpgradeServiceStarted;
    }

    public void setUpgradeServiceStarted(boolean upgradeServiceStarted)
    {
        isUpgradeServiceStarted = upgradeServiceStarted;
    }

    public boolean isUpdatedVersionExisted()
    {
        return isUpdatedVersionExisted;
    }

    public void setUpdatedVersionExisted(boolean updatedVersionExisted)
    {
        isUpdatedVersionExisted = updatedVersionExisted;
    }

    public boolean isShowUpdated()
    {
        return isShowUpdated;
    }

    public void setIsShowUpdated(boolean isShowUpdated)
    {
        this.isShowUpdated = isShowUpdated;
    }

    public boolean isRemindUpgrade()
    {
        return isRemindUpgrade;
    }

    public void setRemindUpgrade(boolean remindUpgrade)
    {
        this.isRemindUpgrade = remindUpgrade;
    }

    public boolean isLoginDialogDisplayed()
    {
        return isLoginDialogDisplayed;
    }

    public void setLoginDialogDisplayed(boolean loginDialogDisplayed)
    {
        isLoginDialogDisplayed = loginDialogDisplayed;
    }

    public String getDeviceModel()
    {
        if (DeviceInfo.isPad())
        {
            if (AppConfig.CAType.PLAYREADY.equalsIgnoreCase(ConfigUtil.getConfig().getCaType()))
            {
                return DeviceType.PAD_PLAYREADY;
            }
            else if (AppConfig.CAType.WIDEVINE.equalsIgnoreCase(ConfigUtil.getConfig().getCaType()))
            {
                return DeviceType.PAD_WIDEVINE;
            }
            else
            {
                return DeviceType.PAD;
            }
        }
        else
        {
            if (AppConfig.CAType.PLAYREADY.equalsIgnoreCase(ConfigUtil.getConfig().getCaType()))
            {
                return DeviceType.PHONE_PLAYREADY;
            }
            else if (AppConfig.CAType.WIDEVINE.equalsIgnoreCase(ConfigUtil.getConfig().getCaType()))
            {
                return DeviceType.PHONE_WIDEVINE;
            }
            else
            {
                return DeviceType.PHONE;
            }
        }
    }

    public String getDeviceType()
    {
        if (DeviceInfo.isPad())
        {
            return DeviceType.PAD;
        }
        else
        {
            return DeviceType.PHONE;
        }
    }

    public boolean isShowLoginDialog()
    {
        return isShowLoginDialog;
    }

    public void setShowLoginDialog(boolean showLoginDialog)
    {
        isShowLoginDialog = showLoginDialog;
    }

    public boolean isStartedCustomizedFromUpdateService()
    {
        return isStartedCustomizedFromUpdateService;
    }

    public void setStartedCustomizedFromUpdateService(boolean startedCustomizedFromUpdateService)
    {
        isStartedCustomizedFromUpdateService = startedCustomizedFromUpdateService;
    }
}