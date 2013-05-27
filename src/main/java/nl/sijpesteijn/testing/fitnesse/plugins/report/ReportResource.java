package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ReportResource.
 */
public class ReportResource {

    /**
     * Report location.
     */
    private final String outputDirectory;

    /**
     * Root of resources.
     */
    private String pluginResources;

    /**
     * Constructor.
     *
     * @param outputDirectory - the outputdirectory for the resources.
     * @param pluginResources - the root of the plugin resource.
     */
    public ReportResource(final String outputDirectory, final String pluginResources) {
        this.outputDirectory = outputDirectory;
        this.pluginResources = pluginResources;
    }

    /**
     * Copy the resources to the output directory.
     *
     * @param resourceName - the resources to copy.
     * @throws IOException - thrown in case of an error.
     */
    public final void copy(final String resourceName) throws IOException {
        final File destination = new File(outputDirectory);
        final URL url =
                Thread.currentThread().getContextClassLoader().getResource(pluginResources + resourceName);

        if (url != null) {
            if (isFile(url)) {
                copyDirectory(new File(url.getFile()), destination);
            } else if (isJar(url)) {
                copyFilesFromJar(url, destination.getAbsolutePath(), resourceName);
            }
        }
    }

    /**
     * Copy the files from a jar file.
     *
     * @param url - the url to copy.
     * @param destination - destination.
     * @param resourceName - name of the resource.
     * @throws IOException - unable to extract resources from jar.
     */
    private void copyFilesFromJar(final URL url, final String destination, final String resourceName)
            throws IOException {
        String dest = destination;
        if (!dest.endsWith(File.separator)) {
            dest += File.separator;
        }
        ZipFile zip = getZipFile(url);
        String jarPath = getJarPath(url);
        ZipEntry zipEntry = zip.getEntry(jarPath);
        if (zipEntry.isDirectory()) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String path = entry.toString();
                if (path.startsWith(pluginResources + resourceName) && path.length()
                        > (pluginResources + resourceName).length()) {
                    String stripped = path.replaceAll(pluginResources, "");

                    String dir = dest + stripped;
                    int end = stripped.lastIndexOf(File.separator);
                    if (end > 0) {
                        dir = dest + stripped.substring(0, end + 1);
                        new File(dir).mkdirs();
                    }
                    String outputFile = dest + stripped;
                    if (!dir.equals(outputFile)) {
                        final InputStream is = zip.getInputStream(entry);
                        saveInputStream(is, outputFile);
                        is.close();
                    }
                }
            }
        } else {
            String path = zipEntry.toString();
            String stripped = path.replaceAll(pluginResources, "");
            String dir;
            int end = stripped.lastIndexOf(File.separator);
            if (end > 0) {
                dir = destination + stripped.substring(0, end + 1);
                new File(dir).mkdirs();
            }
            String outputFile = destination + stripped;
            final InputStream is = zip.getInputStream(zipEntry);
            saveInputStream(is, outputFile);
        }
    }

    /**
     * Save the input stream.
     *
     * @param is - inputstream.
     * @param outputFile - output file.
     * @throws IOException - unable to save stream.
     */
    private void saveInputStream(final InputStream is, final String outputFile) throws IOException {
        int bufferSize = 1024;
        final FileOutputStream fos = new FileOutputStream(outputFile);
        try {
            final byte[] bytes = new byte[bufferSize];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
        } finally {
            fos.close();
        }
    }

    /**
     * Get the zip file from the url.
     *
     * @param url - the url.
     * @return - zip file.
     * @throws IOException - unable to get zip file.
     */
    private ZipFile getZipFile(final URL url) throws IOException {
        String file = url.getFile();
        int bangIndex = file.indexOf('!');
        file = new URL(file.substring(0, bangIndex)).getFile();
        ZipFile zip = new ZipFile(file);
        return zip;
    }

    /**
     * Get jar path from url.
     *
     * @param url - the url.
     * @return - jar path.
     */
    private String getJarPath(final URL url) {
        String file = url.getFile();
        int bangIndex = file.indexOf('!');
        String jarPath = file.substring(bangIndex + 2);
        return jarPath;
    }

    /**
     * Copy resource directory.
     *
     * @param source - source.
     * @param destination - destination.
     * @throws IOException - unable to copy directory.
     */
    private void copyDirectory(final File source, final File destination) throws IOException {
        FileUtils.copyDirectoryToDirectory(source, destination);
    }

    /**
     * Check if the url is a jar file.
     *
     * @param url - url.
     * @return - boolean.
     */
    private boolean isJar(final URL url) {
        return url.getProtocol().equalsIgnoreCase("jar");
    }

    /**
     * Check if the url is a file.
     *
     * @param url - url.
     * @return - boolean.
     */
    private boolean isFile(final URL url) {
        return url.getProtocol().equalsIgnoreCase("file");

    }
}
