package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import static java.text.MessageFormat.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class FitNesseResourceAccess {

    public static final String RESOURCES_FOLDER_NAME_WITHIN_MAFIARESULTS = "resources";
    private static final String RESOURCES_PATH_WITHIN_FITNESSERJAR = "/fitnesse/resources";
    private Project project;

    public FitNesseResourceAccess(Project project) {
        this.project = project;
    }

    public void copyResourcesTo(String targetResourceFolder) throws MafiaRuntimeException {
        try {
            FitNesseJarLocator locator = new FitNesseJarLocator(project);
            String fitNesseJarPath = locator.getFitNesseJarPath();
            Path resourcesPath = getResourcesPath(fitNesseJarPath);
            copyResources(resourcesPath, targetResourceFolder);
        } catch (MafiaException e) {
            throw new MafiaRuntimeException(e);
        }
    }

    private void copyResources(Path sourceResourcesPath, final String targetResourceFolder)
        throws MafiaRuntimeException {
        try {
            Files.walkFileTree(sourceResourcesPath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path sourceResourceFile, BasicFileAttributes attrs) throws IOException {
                    String subPathWithinResource = sourceResourceFile.toString().replace(
                        RESOURCES_PATH_WITHIN_FITNESSERJAR, "");
                    Path targetFile = Paths.get(targetResourceFolder, subPathWithinResource);
                    Files.createDirectories(targetFile);
                    Files.copy(sourceResourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            String message = format("Could copy resources (css, js) from {0} to {1}", sourceResourcesPath,
                targetResourceFolder);
            throw new MafiaRuntimeException(message, e);
        }
    }

    private Path getResourcesPath(String fitnesseJarPathString) throws MafiaRuntimeException {
        Path fitnesseJarPath = Paths.get(fitnesseJarPathString);
        try {
            FileSystem fitnesseJar = FileSystems.newFileSystem(fitnesseJarPath, null);
            Path resources = fitnesseJar.getPath(RESOURCES_PATH_WITHIN_FITNESSERJAR);
            if (!Files.exists(resources)) {
                String message = format("Could not find folder {0} in jar {1}", RESOURCES_PATH_WITHIN_FITNESSERJAR,
                    fitnesseJar);
                throw new MafiaRuntimeException(message);
            }
            return resources;
        } catch (IOException e) {
            throw new MafiaRuntimeException("Could not access the fitnesse artifact " + fitnesseJarPathString, e);
        }
    }

}
