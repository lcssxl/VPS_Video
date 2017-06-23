package com.open.free.videoplay.utils.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * White label config bean
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility =
        JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiteLabel
{
    /**
     * {
     * "color": "color",
     * "string": "string",
     * "image": "image\Android",
     * "build": "build"
     * }
     */
    @JsonProperty("color")
    private String colorPath;

    @JsonProperty("string")
    private String stringPath;

    @JsonProperty("image")
    private String imagePath;

    @JsonProperty("build")
    private String buildPath;

    public String getColorPath()
    {
        return colorPath;
    }

    public void setColorPath(String colorPath)
    {
        this.colorPath = colorPath;
    }

    public String getStringPath()
    {
        return stringPath;
    }

    public void setStringPath(String stringPath)
    {
        this.stringPath = stringPath;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getBuildPath()
    {
        return buildPath;
    }

    public void setBuildPath(String buildPath)
    {
        this.buildPath = buildPath;
    }
}