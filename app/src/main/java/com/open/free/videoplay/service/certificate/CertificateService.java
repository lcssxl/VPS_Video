package com.open.free.videoplay.service.certificate;

import com.huawei.ott.sdk.OTTSDK;
import com.huawei.ott.sdk.ottutil.android.DebugLog;
import com.huawei.ott.sdk.ottutil.android.PathManager;
import com.huawei.ott.sdk.ottutil.java.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

/**
 * Certificate Manage for APP https connection
 */
public class CertificateService
{
    private final String TAG = this.getClass().getSimpleName();

    private final String defaultDirPath = PathManager.getCertificatePath();

    private final String defaultFileName = "ott.pem";

    public void writeCertificate()
    {
        Executors.newSingleThreadExecutor().submit(new WriteCertificateThread(defaultFileName,
                defaultDirPath));
    }

    /**
     * write certificate to app
     *
     * @param fileName the name of certificate file in assets.
     */
    public void writeCertificate(String fileName)
    {
        Executors.newSingleThreadExecutor().submit(new WriteCertificateThread(fileName,
                defaultDirPath));
    }

    /**
     * write certificate to app
     *
     * @param fileName the name of certificate file in assets.
     * @param dirPath  the path for certificate to write
     */
    public void writeCertificate(String fileName, String dirPath)
    {
        Executors.newSingleThreadExecutor().submit(new WriteCertificateThread(fileName, dirPath));
    }

    /**
     * write the file in assets to dirPath
     */
    private class WriteCertificateThread implements Runnable
    {
        private String fileName;
        private String dirPath;

        public WriteCertificateThread(String fileName, String dirPath)
        {
            this.fileName = fileName;
            this.dirPath = dirPath;
        }

        @Override
        public void run()
        {
            DebugLog.debug(TAG, "run().");
            InputStream inStream = null;
            FileOutputStream outStream = null;
            try
            {
                if (FileUtil.isIllegalPath(fileName) || FileUtil.isIllegalType(fileName, "pem")
                        || FileUtil.isIllegalSize(fileName, 1))
                {
                    DebugLog.error(TAG, "Check the File is illegal.");
                    throw new IOException("file name is illegal");
                }
                inStream = OTTSDK.getApplicationContext().getResources().getAssets().open(fileName);

                File dir = new File(fileName);
                File file = new File(dirPath + fileName);
                if (!dir.exists())
                {
                    boolean result = dir.mkdirs();
                    DebugLog.debug(TAG, "dir.mkdirs result is " + result);
                }
                if (!file.exists())
                {
                    boolean result = file.createNewFile();
                    DebugLog.debug(TAG, "createNewFile result is " + result);
                }

                outStream = new FileOutputStream(file);
                int length = inStream.available();
                byte[] buffer = new byte[length];
                int r = inStream.read(buffer);
                if (length != r)
                {
                    throw new IOException("Read length of file is wrong.");
                }
                outStream.write(buffer);
            }
            catch (IOException e)
            {
                DebugLog.warn(TAG, e);
            }
            finally
            {
                FileUtil.closeInputStream(inStream);
                FileUtil.closeOutputStream(outStream);
            }
        }
    }

    public String getDefaultDirPath()
    {
        return defaultDirPath;
    }

    public String getDefaultFileName()
    {
        return defaultFileName;
    }
}