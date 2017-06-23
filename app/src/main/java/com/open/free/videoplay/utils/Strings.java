package com.open.free.videoplay.utils;

import android.util.Xml;

import com.huawei.ott.sdk.OTTSDK;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.java.FileUtil;
import com.huawei.ott.sdk.resourcecustomized.utils.ResourceUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Static utility class used for reading colors from xml and storing the names
 */
public class Strings
{
    private static final String TAG = Strings.class.getSimpleName();
    private static final String STRINGS_NAME = "string_%1$s.xml";
    private static final String LOAD_FLAG = "LoadFlag";
    private static final String LOADED = "Loaded";

    private static HashMap<String, String> stringsHashMap = new HashMap<String, String>();
    private static HashMap<String, String> needTransferHashMap = new HashMap<String, String>();
    private static Strings instance = new Strings();

    public static Strings getInstance()
    {
        return instance;
    }

    public Strings()
    {
        needTransferHashMap.put("&amp;", "&");
        needTransferHashMap.put("&lt;", "<");
        needTransferHashMap.put("&gt;", ">");
        needTransferHashMap.put("\\'", "'");
        needTransferHashMap.put("\\\"", "\"");
        needTransferHashMap.put("\\n", System.getProperty("line.separator"));
    }

    /**
     * Load string_XX.xml and parse it.
     *
     * @param lang
     */
    public void parse(String lang)
    {
        if (ResourceUtils.isNeedLoadResource() && !LOADED.equals(stringsHashMap.get(LOAD_FLAG +
                "#" + lang)))
        {
            String stringsName = String.format(STRINGS_NAME, lang);
            String stringsPath = ResourceUtils.getResourcePath() +
                    ResourceUtils.getStringPath() + File.separator + stringsName;
            File stringsFile = new File(stringsPath);
            if (stringsFile.exists())
            {
                InputStream is = null;
                try
                {
                    is = new FileInputStream(stringsFile);
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(is, null);
                    parser.nextTag();

                    int depth = parser.getDepth();
                    while (parser.getEventType() != XmlPullParser.END_TAG || parser.getDepth() >
                            depth)
                    {
                        parser.next();

                        if (parser.getEventType() == XmlPullParser.START_TAG)
                        {
                            String name = parser.getAttributeValue(null, "name");
                            parser.next();

                            String value = parser.getText();
                            Set set = needTransferHashMap.keySet();
                            for (Iterator it = set.iterator(); it.hasNext(); )
                            {
                                String key = (String) it.next();
                                if (value.contains(key))
                                {
                                    value = value.replace(key, needTransferHashMap.get
                                            (key));
                                    break;
                                }
                            }

                            stringsHashMap.put(name + "#" + lang, value);
                        }
                    }

                    stringsHashMap.put(LOAD_FLAG + "#" + lang, LOADED);
                }
                catch (XmlPullParserException e)
                {
                    DebugLog.error(TAG, e);
                }
                catch (IOException e)
                {
                    DebugLog.error(TAG, e);
                }
                finally
                {
                    FileUtil.closeInputStream(is);
                }
            }
        }
    }

    /**
     * Get the string by name
     *
     * @param name
     * @param defaultStr
     * @return
     */
    public String getString(String name, String defaultStr)
    {
        if (ResourceUtils.isNeedLoadResource())
        {
            String lang = ResourceUtils.getCurrentLanguage();
            if (!LOADED.equals(stringsHashMap.get(LOAD_FLAG + "#" + lang)))
            {
                parse(lang);
            }

            String key = name + "#" + lang;
            if (stringsHashMap.containsKey(key))
            {
                return stringsHashMap.get(key);
            }
        }
        return defaultStr;
    }

    public String getString(int resId)
    {
        String name = OTTSDK.getApplicationContext().getResources().getResourceEntryName(resId);
        String defaultStr = OTTSDK.getApplicationContext().getResources().getString(resId);
        return getString(name, defaultStr);
    }

    public String getString(int resId, Object... formatArgs)
    {
        String name = OTTSDK.getApplicationContext().getResources().getResourceEntryName(resId);
        String value = getString(name, OTTSDK.getApplicationContext().getResources().getString
                (resId));
        return String.format(value, formatArgs);
    }
}
