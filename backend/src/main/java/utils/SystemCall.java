package utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

class SystemCall {
    private static Logger logger = LogHelper.getLogger(SystemCall.class.getName());

    public SystemCallResult fire(ExecutorService executorService, String command) {
        logger.fine(String.format("Fire system call '%s'", command));

        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(false).command(command.split(" "));

        try {
            Process process = builder.start();

            StringBuilder stdOutStringBuilder = new StringBuilder();
            StreamEater outStreamGobbler = new StreamEater(process.getInputStream(), e -> stdOutStringBuilder.append(e).append("\n"));
            executorService.submit(outStreamGobbler);

            StringBuilder stdErrStringBuilder = new StringBuilder();
            StreamEater errStreamGobbler = new StreamEater(process.getErrorStream(), e -> stdErrStringBuilder.append(e).append("\n"));
            executorService.submit(errStreamGobbler);

            return new SystemCallResult(process.waitFor(), stdOutStringBuilder.toString(), stdErrStringBuilder.toString());
        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}