package de.kabuecher.downloader.v4;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class Main {

    public static void main(String[] args) {
        String jarUrl = "https://github.com/username/repository/releases/latest/download/your-jar-file.jar";

        try {
            byte[] jarData = downloadJarToMemory(jarUrl);
            runJarFromMemory(jarData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] downloadJarToMemory(String jarUrl) throws IOException {
        System.out.println("Downloading JAR from: " + jarUrl);

        HttpURLConnection connection = (HttpURLConnection) new URL(jarUrl).openConnection();
        connection.setRequestProperty("Accept", "application/octet-stream");

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Downloaded JAR to memory.");
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static void runJarFromMemory(byte[] jarData) throws Exception {
        System.out.println("Running JAR from memory.");

        try (ByteArrayInputStream jarInputStream = new ByteArrayInputStream(jarData);
             JarInputStream jis = new JarInputStream(jarInputStream)) {

            Manifest manifest = jis.getManifest();
            if (manifest == null) {
                throw new IllegalStateException("No manifest found in JAR.");
            }

            String mainClass = manifest.getMainAttributes().getValue("Main-Class");
            if (mainClass == null) {
                throw new IllegalStateException("No Main-Class found in JAR manifest.");
            }

            System.out.println("Main-Class: " + mainClass);

            // Construct a temporary in-memory class loader
            InMemoryClassLoader classLoader = new InMemoryClassLoader(jarData);

            Class<?> clazz = classLoader.loadClass(mainClass);
            clazz.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
        }
    }

    private static class InMemoryClassLoader extends ClassLoader {
        private final byte[] jarData;

        public InMemoryClassLoader(byte[] jarData) {
            this.jarData = jarData;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try (ByteArrayInputStream jarInputStream = new ByteArrayInputStream(jarData);
                 JarInputStream jis = new JarInputStream(jarInputStream)) {
                JarEntry entry;
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (entry.getName().equals(name.replace('.', '/') + ".class")) {
                        byte[] classData = jis.readAllBytes();
                        return defineClass(name, classData, 0, classData.length);
                    }
                }
            } catch (IOException e) {
                throw new ClassNotFoundException("Cannot load class " + name, e);
            }

            return super.findClass(name);
        }
    }
}