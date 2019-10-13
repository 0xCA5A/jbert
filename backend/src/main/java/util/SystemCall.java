package util;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

class SystemCall {
    private static Logger logger = LogHelper.getLogger(SystemCall.class.getName());

    public Optional<SystemCallResult> fire(ExecutorService executorService, String command) {
        return fire(executorService, command, 0);
    }

    public Optional<SystemCallResult> fire(ExecutorService executorService, String command, int expectedExitCode) {
        logger.fine(String.format("Fire system call '%s'", command));

        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(false).command(command.split(" "));

        try {
            Process process = builder.start();

            final StringBuilder stdOutStringBuilder = new StringBuilder();
            final StreamEater outStreamGobbler = new StreamEater(process.getInputStream(), line -> stdOutStringBuilder.append(line).append("\n"));
            executorService.submit(outStreamGobbler);

            final StringBuilder stdErrStringBuilder = new StringBuilder();
            final StreamEater errStreamGobbler = new StreamEater(process.getErrorStream(), line -> stdErrStringBuilder.append(line).append("\n"));
            executorService.submit(errStreamGobbler);

            SystemCallResult systemCallResult = new SystemCallResult(process.waitFor(),
                    stdOutStringBuilder.toString(), stdErrStringBuilder.toString());

            return checkExitCode(command, systemCallResult, expectedExitCode);

        } catch (IOException | InterruptedException exception) {
            logger.severe(String.format("Failed running system call '%s': %s", command, exception.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<SystemCallResult> checkExitCode(String command, SystemCallResult systemCallResult, int expectedExitCode) {
        if (systemCallResult.getExitCode() == expectedExitCode) {
            return Optional.of(systemCallResult);
        } else {
            final String errorMessage = String.format("Command '%s' returned with unexpected exit code '%d': '%s'",
                    command, systemCallResult.getExitCode(), systemCallResult);
            logger.severe(errorMessage);
            return Optional.empty();
        }
    }
}
