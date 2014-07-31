package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

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
     * @param commanderConfig - The command configuration.
     */
    public FitNesseCommander(final FitNesseCommanderConfig commanderConfig) {
        this.commanderConfig = commanderConfig;
    }

    /**
     * Start FitNesse.
     *
     * @throws MafiaException thrown in case of an error.
     */
    public final void start() throws MafiaException {
        String logArgument = "";
        if (commanderConfig.getFitNesseLogDirectory() != null) {
            logArgument = " -l " + commanderConfig.getFitNesseLogDirectory();
        }
        final String command = "java" + getJVMArguments(commanderConfig.getJvmArguments())
                + " -cp " + commanderConfig.getClasspathString()
                + " fitnesseMain.FitNesseMain -p " + commanderConfig.getFitNessePort()
                + " -d " + commanderConfig.getWikiRoot()
                + " -r " + commanderConfig.getNameRootPage()
                + " -e " + commanderConfig.getRetainDays()
                + logArgument;
        commanderConfig.getLog().info("Starting FitNesse. This could take some more seconds when first used....");
        run(command);
    }

    /**
     * Stop FitNesse.
     *
     * @throws MafiaException thrown in case of an error.
     */
    public final void stop() throws MafiaException {
        final String command = "java -cp " + commanderConfig.getClasspathString() + " fitnesse.Shutdown -p "
                + commanderConfig.getFitNessePort();
        run(command);
    }

    /**
     * Run the command.
     *
     * @param command - command.
     * @throws MafiaException - unable to execute command.
     */
    private void run(final String command) throws MafiaException {
        try {
            commanderConfig.getLog().debug("Running command: " + command);
            process = Runtime.getRuntime().exec(command, null, new File(commanderConfig.getWikiRoot()));
            errorMonitor = new StreamToBufferMonitor(process.getErrorStream());
            new Thread(errorMonitor).start();
            inputMonitor = new StreamToBufferMonitor(process.getInputStream());
            new Thread(inputMonitor).start();
            waitForProcess();
        } catch (Exception e) {
            throw new MafiaException("Could not run command.", e);
        }
    }

    /**
     * Wait for process.
     *
     * @throws InterruptedException - unable to check process status.
     */
    @SuppressWarnings("PMD")
    private void waitForProcess() throws InterruptedException {
        while (true) {
            try {
                process.exitValue();
                return;
            } catch (IllegalThreadStateException itse) {
                // process has not finished yet.
            }
            if (inputMonitor.isFinished()) {
                if (errorMonitor.getBuffer().toString().trim()
                        .endsWith("Unpacking new version of FitNesse resources. Please be patient.")) {
                    Thread.sleep(commanderConfig.getUnpackWaitTime());
                }
                return;
            }
            Thread.sleep(commanderConfig.getUnpackWaitTime());
        }
    }

    /**
     * Get jvm arguments.
     *
     * @param arguments - jvm arguments.
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
                || errorMonitor.getBuffer().toString().trim().contains("Please be patient.") || StringUtils
                .isEmpty(errorMonitor.getBuffer().toString())
                || inputMonitor.getBuffer().toString().contains("Bootstrapping FitNesse, the fully integrated standalone wiki and acceptance testing framework.")) {
            return false;
        }
        return true;
    }
}
