package util;


public class SystemCallResult {
    private final String stdOut;
    private final String stdErr;
    private final int exitCode;

    SystemCallResult(int exitCode, String stdOut, String stdErr) {
        this.exitCode = exitCode;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    int getExitCode() {
        return this.exitCode;
    }

    String getStdOut() {
        return this.stdOut;
    }

    String getStdErr() {
        return this.stdErr;
    }

    @Override
    public String toString() {
        return String.format("{exitCode: %d, stdOut: %s, stdErr: %s}", exitCode, stdOut, stdErr);
    }
}
