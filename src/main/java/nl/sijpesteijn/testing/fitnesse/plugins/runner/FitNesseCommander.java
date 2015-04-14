package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import org.apache.commons.lang.StringUtils;

/**
 * FitNesseCommander interface.
 */
public class FitNesseCommander {

    /**
     * Fitnesse commander configuration.
     */
    private final FitNesseCommanderConfig commanderConfig;

    /**
     * Error stream monitor.
     */
    private StreamToBufferMonitor errorMonitor;

    /**
     * Input stream monitor.
     */
    private StreamToBufferMonitor inputMonitor;

    /**
     * Process.
     */
    private Process process;

    /**
     * Constructor.
     *
     * @param commanderConfig
     *            - The command configuration.
     */
    public FitNesseCommander(final FitNesseCommanderConfig commanderConfig) {
        this.commanderConfig = commanderConfig;
    }

    /**
     * Start FitNesse.
     *
     * @throws MafiaException
     *             thrown in case of an error.
     */
    public final void start() throws MafiaException {
        String logArgument = "";
        if (commanderConfig.getFitNesseLogDirectory() != null) {
            logArgument = " -l " + commanderConfig.getFitNesseLogDirectory();
        }
        String authArgumentStart = "";
        if (commanderConfig.getFitNesseAuthenticateStart() != null) {
            authArgumentStart = " -a " + commanderConfig.getFitNesseAuthenticateStart();
        }

        String updatePreventsArgument = "";
        if (commanderConfig.getFitNesseUpdatePrevents() != null && commanderConfig.getFitNesseUpdatePrevents()) {
            updatePreventsArgument = " -o";
        }
        String verboseArgument = "";
        if (commanderConfig.getFitNesseVerbose() != null && commanderConfig.getFitNesseVerbose()) {
            verboseArgument = " -v";
        }

        final String command = "java" + getJVMArguments(commanderConfig.getJvmArguments())
            + " -cp " + commanderConfig.getClasspathString()
            + " fitnesseMain.FitNesseMain -p " + commanderConfig.getFitNessePort()
            + " -d " + commanderConfig.getWikiRoot()
            + " -r " + commanderConfig.getNameRootPage()
            + " -e " + commanderConfig.getRetainDays()
            + logArgument
            + authArgumentStart
            + updatePreventsArgument
            + verboseArgument;

        commanderConfig.getLog().info("Starting FitNesse. This could take some more seconds when first used....");

        this.commanderConfig.getLog().debug("Starting FitNesse with Command:" + command);

        run(command);
    }

    /**
     * Stop FitNesse.
     *
     * @throws MafiaException
     *             thrown in case of an error.
     */
    public final void stop() throws MafiaException {

        String authArgument = "";
        if (commanderConfig.getFitNesseAuthenticateStop() != null
            // in Shutdown fitnesse does not support user/paassword reading from file
            // so we detect a valid user:password with the ":" separator
            && commanderConfig.getFitNesseAuthenticateStop().contains(":")) {
            String userPasswordArgument = commanderConfig.getFitNesseAuthenticateStop().replaceFirst(":", " ");
            authArgument = " -c " + userPasswordArgument;
        }

        final String command = "java -cp " + commanderConfig.getClasspathString() + " fitnesse.Shutdown -p "
            + commanderConfig.getFitNessePort()
            + authArgument;

        this.commanderConfig.getLog().debug("Stopping FitNesse with Command:" + command);
        run(command);
    }

    /**
     * Run the command.
     *
     * @param command
     *            - command.
     * @throws MafiaException
     *             - unable to execute command.
     */
    private void run(final String command) throws MafiaException {
        try {
            commanderConfig.getLog().debug("Running command: " + command);
            process = Runtime.getRuntime().exec(command, null, new File(commanderConfig.getWikiRoot()));
            try {
                boolean isStartUp = command.contains("fitnesseMain.FitNesseMain");
                if (isStartUp) {
                    waitForFitnesseStartup();
                }
            } catch (MafiaException e) {
                throw e;
            } finally {
                errorMonitor = new StreamToBufferMonitor(process.getErrorStream());
                errorMonitor.run();
                inputMonitor = new StreamToBufferMonitor(process.getInputStream());
                inputMonitor.run();
            }
        } catch (Exception e) {
            throw new MafiaException("Could not run command.", e);
        }
    }

    private void waitForFitnesseStartup() throws MafiaException {
        String fitnesseUrl = "http://localhost:" + commanderConfig.getFitNessePort() + "/";
        commanderConfig.getLog().debug(
            "Polling URL " + fitnesseUrl + " in order to see when Fitnesse has finished startup.");
        sleep(2000);
        int maxTries = 4;
        for (int i = 1; i <= maxTries; i++) {
            try {
                tryConnect(fitnesseUrl);
                commanderConfig.getLog().info("Fitnesse started.");
                return;
            } catch (IOException e) {
                commanderConfig.getLog().debug(
                    "Couldn't connect to fitnesse: " + e.getMessage() + ". Waiting and then retry... (Try " + i + "/"
                        + maxTries + ")");
                sleep(3000);
            }
        }
        throw new MafiaException("Couldn't connect to fitnesse server.");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e1) {
            throw new RuntimeException("Thread couldn't sleep.");
        }
    }

    private void tryConnect(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();
    }

    /**
     * Get jvm arguments.
     *
     * @param arguments
     *            - jvm arguments.
     * @return - jvm argument string.
     */
    private String getJVMArguments(final List<String> arguments) {
        if (arguments == null) {
            return "";
        }
        StringBuffer list = new StringBuffer(" ");
        for (final String argument : arguments) {
            if (argument.startsWith("-")) {
                list.append(argument);
                list.append(" ");
            } else {
                list.append(" -D");
                list.append(argument);
                list.append(" ");
            }
        }
        return list.toString();
    }

    /**
     * Return the error output.
     *
     * @return {@link java.lang.String}
     */
    public final String getErrorOutput() {
        return errorMonitor.getBuffer().toString();
    }

    /**
     * Return the output.
     *
     * @return {@link java.lang.String}
     */
    public final String getOutput() {
        return inputMonitor.getBuffer().toString();
    }

    /**
     * Indication if an error occurred.
     *
     * @return boolean
     */
    public final boolean hasError() {
        if (inputMonitor.getBuffer().toString().trim().contains("Started...")
            || errorMonitor.getBuffer().toString().trim().contains("Please be patient.")
            || StringUtils
                .isEmpty(errorMonitor.getBuffer().toString())
            || inputMonitor
                .getBuffer()
                .toString()
                .contains(
                    "Bootstrapping FitNesse, the fully integrated standalone wiki and acceptance testing framework.")) {
            return false;
        }
        return true;
    }
}
