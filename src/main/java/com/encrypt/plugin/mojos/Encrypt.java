package com.encrypt.plugin.mojos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Encrypt {

    private static String arch = System.getProperty("os.arch");
    private static String os = System.getProperty("os.name").toLowerCase();

    public static void loadLibrary(String path) throws IOException {
        // 从 resources 目录中读取 DLL 文件
        InputStream inputStream = Encrypt.class.getClassLoader().getResourceAsStream(path);
        String suffix = ".so";
        if(os.contains("win")){
            suffix = ".dll";
        }
        File tempFile = File.createTempFile("encrypt", suffix);

        // 将 DLL 文件写入到本地磁盘上
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        // 加载 DLL 文件
        System.load(tempFile.getAbsolutePath());
    }

    static {
        try {
            String path = "cpu";
            switch (arch){
                case "amd64":
                    if(os.contains("win")){
                        path = path+"/windows/encrypt.dll";
                    }else {
                        path = path+"/arch/encrypt.so";
                    }

                    break;
            }
            loadLibrary(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public native byte[] encrypt(byte[] _buf);
}
