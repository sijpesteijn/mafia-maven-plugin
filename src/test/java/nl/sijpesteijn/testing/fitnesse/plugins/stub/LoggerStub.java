package nl.sijpesteijn.testing.fitnesse.plugins.stub;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.Logger;

/**
 * User: gijs
 * Date: 3/31/13 5:13 PM
 */
public class LoggerStub implements Log, Logger {
    private String debug = "";
    private String info = "";
    private String warn = "";
    private String error = "";

    @Override
    public void debug(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void debug(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void info(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void info(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void debug(CharSequence charSequence) {
        debug += charSequence;
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        debug += charSequence + throwable.getMessage();
    }

    @Override
    public void debug(Throwable throwable) {
        debug += throwable.getMessage();
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void warn(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void warn(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void info(CharSequence charSequence) {
        info += charSequence;
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        info += charSequence + throwable.getMessage();
    }

    @Override
    public void info(Throwable throwable) {
        info += throwable.getMessage();
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void error(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void error(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void warn(CharSequence charSequence) {
        warn += charSequence;
    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {
        warn += charSequence + throwable.getMessage();
    }

    @Override
    public void warn(Throwable throwable) {
        warn += throwable.getMessage();
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void fatalError(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFatalErrorEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Logger getChildLogger(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getThreshold() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setThreshold(int threshold) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void error(CharSequence charSequence) {
        error += charSequence;
    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {
        error += charSequence + throwable.getMessage();
    }

    @Override
    public void error(Throwable throwable) {
        error += throwable.getMessage();
    }

    public String getInfo() {
        return info;
    }
}
