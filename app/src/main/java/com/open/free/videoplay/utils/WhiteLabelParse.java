package com.open.free.videoplay.utils;

import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.java.FileUtil;
import com.huawei.ott.sdk.ottutil.java.JsonParse;
import com.huawei.ott.sdk.resourcecustomized.Constant;
import com.huawei.ott.sdk.resourcecustomized.utils.ResourceUtils;
import com.open.free.videoplay.utils.bean.WhiteLabel;

import java.io.File;

/**
 * Parse white label config
 *
 */
public class WhiteLabelParse
{
    private static final String TAG = WhiteLabelParse.class.getSimpleName();

    private static WhiteLabelParse instance = new WhiteLabelParse();

    public static WhiteLabelParse getInstance()
    {
        return instance;
    }

    /**
     * Load whiteLabel.ini and parse.
     */
    public void parse()
    {
        if (ResourceUtils.isNeedLoadResource())
        {
            String whiteLabelFileName = ResourceUtils.getResourcePath() + File.separator +
                    Constant.RESOURCE_WHITE_LABEL_FILENAME;
            String content = FileUtil.readFileByLines(whiteLabelFileName);
            DebugLog.debug(TAG, "content=" + content);
            if (null != content)
            {
                WhiteLabel whiteLabel = JsonParse.toObject(WhiteLabel.class, content);
                if (null != whiteLabel)
                {
                    DebugLog.debug(TAG, "colorPath=" + whiteLabel.getColorPath(), "stringPath=" +
                            whiteLabel.getStringPath(), "imagePath=" + whiteLabel.getImagePath(),
                            "buildPath=" + whiteLabel.getBuildPath());
                    ResourceUtils.setColorPath(whiteLabel.getColorPath());
                    ResourceUtils.setStringPath(whiteLabel.getStringPath());
                    ResourceUtils.setImagePath(whiteLabel.getImagePath());
                    ResourceUtils.setBuildPath(whiteLabel.getBuildPath());
                }
            }
        }
    }
}