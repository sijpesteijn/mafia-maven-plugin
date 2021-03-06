package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class TestResult {

    private String[] path;
    private int rightTestCount;
    private int wrongTestCount;
    private int ignoredTestCount;
    private int exceptionCount;
    private long runTimeInMillis;
    private Integer exitCode;
    private String executionLogException;

    TestResult() {}

    public TestResult withPath(String path) {
        this.path = StringUtils.split(path, '.');
        return this;
    }

    public TestResult withRightTestCount(int rightTestCount) {
        this.rightTestCount = rightTestCount;
        return this;
    }

    public TestResult withWrongTestCount(int wrongTestCount) {
        this.wrongTestCount = wrongTestCount;
        return this;
    }

    public TestResult withIgnoredTestCount(int ignoredTestCount) {
        this.ignoredTestCount = ignoredTestCount;
        return this;
    }

    public TestResult withExceptionCount(int exceptionCount) {
        this.exceptionCount = exceptionCount;
        return this;
    }

    public TestResult withRunTimeInMillis(long runTimeInMillis) {
        this.runTimeInMillis = runTimeInMillis;
        return this;
    }

    public TestResult withExitCode(Integer exitCode) {
        this.exitCode = exitCode;
        return this;
    }

    public TestResult withExecutionLogException(String executionLogException) {
        this.executionLogException = executionLogException;
        return this;
    }

    public String getPath() {
        return StringUtils.join(path, ".");
    }

    public int getRightTestCount() {
        return rightTestCount;
    }

    public int getWrongTestCount() {
        return wrongTestCount;
    }

    public int getIgnoredTestCount() {
        return ignoredTestCount;
    }

    public int getExceptionCount() {
        return exceptionCount;
    }

    public long getRunTimeInMillis() {
        return runTimeInMillis;
    }

    public double getRunTimeInSec() {
        return runTimeInMillis / 1000.0;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public boolean executedSuccessfully() {
        if (exitCode == null){
            return true;
        }
        return exitCode == 0;
    }

    public String getExecutionLogException() {
        return executionLogException;
    }

    public Object getTotalTestCount() {
        return rightTestCount + wrongTestCount + exceptionCount + ignoredTestCount;
    }

    public Object getSuiteName() {
        String[] pathWithoutTest = Arrays.copyOf(path, path.length - 1);
        return StringUtils.join(pathWithoutTest, ".");
    }

    public Object getTestName() {
        return path[path.length - 1];
    }

    @Override
    public String toString() {
        return "TestResult [path=" + Arrays.toString(path) + ", rightTestCount=" + rightTestCount + ", wrongTestCount="
            + wrongTestCount + ", ignoredTestCount=" + ignoredTestCount + ", exceptionCount=" + exceptionCount
            + ", runTimeInMillis=" + runTimeInMillis + ", exitCode=" + exitCode + ", excutionLogException=" + executionLogException
            + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + exceptionCount;
        result = prime * result + ((executionLogException == null) ? 0 : executionLogException.hashCode());
        result = prime * result + ((exitCode == null) ? 0 : exitCode.hashCode());
        result = prime * result + ignoredTestCount;
        result = prime * result + Arrays.hashCode(path);
        result = prime * result + rightTestCount;
        result = prime * result + (int) (runTimeInMillis ^ (runTimeInMillis >>> 32));
        result = prime * result + wrongTestCount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TestResult other = (TestResult) obj;
        if (exceptionCount != other.exceptionCount) return false;
        if (executionLogException == null) {
            if (other.executionLogException != null) return false;
        } else if (!executionLogException.equals(other.executionLogException)) return false;
        if (exitCode == null) {
            if (other.exitCode != null) return false;
        } else if (!exitCode.equals(other.exitCode)) return false;
        if (ignoredTestCount != other.ignoredTestCount) return false;
        if (!Arrays.equals(path, other.path)) return false;
        if (rightTestCount != other.rightTestCount) return false;
        if (runTimeInMillis != other.runTimeInMillis) return false;
        if (wrongTestCount != other.wrongTestCount) return false;
        return true;
    }

    

}
