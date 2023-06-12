package com.encrypt.plugin.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@Mojo(name = "encrypt", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class EncryptMojos extends AbstractMojo {

    @Parameter
    private List<Jar> jars;

    @Parameter( defaultValue = "${project.build.directory}" )
    private File projectDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Encrypt encrypt = new Encrypt();
        for (Jar jar: jars){
            String src_name = projectDirectory.getAbsolutePath()+File.separator+jar.getName();
            String dst_name = src_name.substring(0, src_name.length() - 4) + "_encrypt.jar";

            System.out.printf("encode jar file: [%s ==> %s ]\n", src_name, dst_name);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            File dst_file = new File(dst_name);
            File src_file = new File(src_name);

            try {
                FileOutputStream dst_fos = new FileOutputStream(dst_file);
                JarOutputStream dst_jar = new JarOutputStream(dst_fos);

                JarFile src_jar = new JarFile(src_file);
                for (Enumeration<JarEntry> enumeration = src_jar.entries(); enumeration.hasMoreElements();) {
                    JarEntry entry = enumeration.nextElement();

                    InputStream is = src_jar.getInputStream(entry);
                    int len;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    byte[] bytes = baos.toByteArray();

                    String name = entry.getName();
                    if (name.endsWith(".class") && includeMatch(jar.getIncludes(),name)
                    && !excludeMatch(jar.getExcludes(),name)) {
                        try {
                            bytes = encrypt.encrypt(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    JarEntry ne = new JarEntry(name);
                    dst_jar.putNextEntry(ne);
                    dst_jar.write(bytes);
                    baos.reset();
                }
                src_jar.close();
                dst_jar.close();
                dst_fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    private boolean includeMatch(List<String> includes,String name){
        if(Objects.isNull(includes)){
            return true;
        }
        for (String include: includes){
            if(name.matches(include)){
                return true;
            }
        }
        return false;
    }

    private boolean excludeMatch(List<String> excludes,String name){
        if(Objects.isNull(excludes)){
            return false;
        }
        for (String exclude : excludes){
            if(name.matches(exclude)){
                return true;
            }
        }
        return false;
    }

}
