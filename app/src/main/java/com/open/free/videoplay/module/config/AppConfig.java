package com.open.free.videoplay.module.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application settings interface corresponding configuration data
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfig
{
    @JsonProperty("versionCode")
    private int versionCode;

    @JsonProperty("edsUrl")
    private String edsURL;

    @JsonProperty("sparedEdsUrl")
    private String sparedEdsURL;

    @JsonProperty("useHttps")
    private boolean useHTTPS;

    @JsonProperty("verifyCerEnable")
    private boolean verifyCerEnable;

    @JsonProperty("resourceVersion")
    private float resourceVersion;

    @JsonProperty("logLevel")
    private int logLevel;

    @JsonProperty("homeOptimization")
    private int homeOptimization;

    @JsonProperty("fps")
    private boolean fps;

    @JsonProperty("caType")
    private String caType;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("openVR")
    private boolean openVR;


    public interface CAType
    {
        String VMX = "VMX";
        String PLAYREADY = "PlayReady";
        String WIDEVINE = "Widevine";
    }

    @JsonProperty("httpPersistentConnection")
    private boolean httpPersistentConnection;

    @JsonProperty("pvrMode")
    private String pvrMode;

    @JsonProperty("language")
    private String language;

    @JsonProperty("isSupportCPVR")
    private boolean isSupportCPVR;

    public boolean isFPS()
    {
        return fps;
    }

    public void setFPS(boolean fps)
    {
        this.fps = fps;
    }

    public int getVersionCode()
    {
        return versionCode;
    }

    public void setVersionCode(int versionCode)
    {
        this.versionCode = versionCode;
    }

    public String getEdsURL()
    {
        return edsURL;
    }

    public void setEdsURL(String edsURL)
    {
        this.edsURL = edsURL;
    }

    public String getSparedEdsURL()
    {
        return sparedEdsURL;
    }

    public void setSparedEdsURL(String sparedEdsURL)
    {
        this.sparedEdsURL = sparedEdsURL;
    }

    public boolean isUseHTTPS()
    {
        return useHTTPS;
    }

    public void setUseHTTPS(boolean useHTTPS)
    {
        this.useHTTPS = useHTTPS;
    }

    public boolean isVerifyCerEnable()
    {
        return verifyCerEnable;
    }

    public void setVerifyCerEnable(boolean verifyCerEnable)
    {
        this.verifyCerEnable = verifyCerEnable;
    }

    public float getResourceVersion()
    {
        return resourceVersion;
    }

    public void setResourceVersion(float resourceVersion)
    {
        this.resourceVersion = resourceVersion;
    }

    public int getLogLevel()
    {
        return logLevel;
    }

    public void setLogLevel(int logLevel)
    {
        this.logLevel = logLevel;
    }

    public int getHomeOptimization()
    {
        return homeOptimization;
    }

    public void setHomeOptimization(int homeOptimization)
    {
        this.homeOptimization = homeOptimization;
    }

    public boolean isHttpPersistentConnection()
    {
        return httpPersistentConnection;
    }

    public void setHttpPersistentConnection(boolean httpPersistentConnection)
    {
        this.httpPersistentConnection = httpPersistentConnection;
    }

    public String getCaType()
    {
        return caType;
    }

    public void setCaType(String caType)
    {
        this.caType = caType;
    }

    public String getPvrMode()
    {
        return pvrMode;
    }

    public void setPvrMode(String pvrMode)
    {
        this.pvrMode = pvrMode;
    }

    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
    }

    public String getCategoryId()
    {
        return this.categoryId;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public boolean isSupportCPVR()
    {
        return isSupportCPVR;
    }

    public void setSupportCPVR(boolean supportCPVR)
    {
        isSupportCPVR = supportCPVR;
    }


    public boolean isOpenVR()
    {
        return openVR;
    }

    public void setOpenVR(boolean openVR)
    {
        this.openVR = openVR;
    }
}