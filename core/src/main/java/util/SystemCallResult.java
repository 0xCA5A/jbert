package util;

class SystemCallResult {
    String stdOut;
    String stdErr;
    int exitCode;

    SystemCallResult(int exitCode, String stdOut, String stdErr) {
        this.exitCode = exitCode;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }
}
