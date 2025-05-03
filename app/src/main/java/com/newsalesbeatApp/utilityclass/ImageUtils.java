package com.newsalesbeatApp.utilityclass;
import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static String getBase64FromFile(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(imageFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            fis.close();

            // Convert byte array to Base64 string
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
