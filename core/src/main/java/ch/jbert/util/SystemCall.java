package ch.jbert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;


class SystemCall {
    private static Logger logger = LoggerFactory.getLogger(SystemCall.class);

    public Optional<SystemCallResult> fire(ExecutorService executorService, String command) {
        return fire(executorService, command, 0);
    }

    public Optional<SystemCallResult> fire(ExecutorService executorService, String command, int expectedExitCode) {
        logger.debug("Fire system call '{}'", command);

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
            logger.error("Failed running system call '{}': {}", command, exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<SystemCallResult> checkExitCode(String command, SystemCallResult systemCallResult, int expectedExitCode) {
        if (systemCallResult.getExitCode() == expectedExitCode) {
            return Optional.of(systemCallResult);
        } else {
            logger.error("Command '{}' returned with unexpected exit code: '{}}'", command, systemCallResult);
            return Optional.empty();
        }
    }
}
