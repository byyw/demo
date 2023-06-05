package com.byyw.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class JarUtils {
    public static void extractFilesFromJar(String targetFolderInJar, String destDir) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] rs = resolver.getResources("classpath:" + destDir);
            for (int i = 0; i < rs.length; i++) {
                File file = new File(targetFolderInJar);
                if(!file.exists()){
                    File p = file.getParentFile();
                    if(p != null){
                        p.mkdirs();
                    }
                    file.createNewFile();
                }
                InputStream is = rs[i].getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                FileCopyUtils.copy(is, fos);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}