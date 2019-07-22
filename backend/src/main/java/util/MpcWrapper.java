package util;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MpcWrapper {
    private final static int MAX_VOLUME_PERCENT = 80;
    private final static int MIN_VOLUME_PERCENT = 15;
    private static Logger logger = LogHelper.getLogger(MpcWrapper.class.getName());
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final String mpcVolumeCommandSkeleton;

    public MpcWrapper(String server, int port) {
        String mpcCommandSkeleton = String.format("mpc --host %s --port %d ", server, port);
        mpcVolumeCommandSkeleton = mpcCommandSkeleton + "volume";
    }

    private void throwExceptionOnUnexpectedExitCode(String command, SystemCallResult result, int expectedExitCode) {
        if (result.exitCode != expectedExitCode) {
            String message = String.format("Command '%s' returned with unexpected exit code '%d'." +
                    "\nStdout: %s\nStderr: %s", command, result.exitCode, result.stdOut, result.stdErr);
            throw new RuntimeException(message);
        }
    }

    private Optional<String> parseVolumeInfoFromLine(String mpcVolumeLine) {
        return Arrays.stream(mpcVolumeLine.split(" "))
                .map(e -> e.split(":"))
                .flatMap(Arrays::stream)
                .filter(e -> e.contains("%"))
                .findFirst();
    }

    private int getVolumePercent(String mpcVolumeCommandOut) {
        Optional<String> volumeLine = Arrays.stream(mpcVolumeCommandOut.split("\n"))
                .filter(e -> e.startsWith("volume:")).findFirst();

        if (!volumeLine.isPresent()) {
            logger.warning("Volume information not found in MPC volume command output stream");
            return -1;
        }

        Optional<String> volumeInfo = parseVolumeInfoFromLine(volumeLine.get());
        if (!volumeInfo.isPresent()) {
            logger.warning("Volume information not found in MPC volume command output stream");
            return -1;
        }

        return Integer.parseInt(volumeInfo.get().replace('%', ' ').trim());
    }

    public void volumeUp(int percentUp) {

        if (getVolume() >= MAX_VOLUME_PERCENT) {
            logger.fine(String.format("Ignore volume up event, volume level is already @ %d%%", MAX_VOLUME_PERCENT));
            return;
        }

        String command = String.format("%s +%d", mpcVolumeCommandSkeleton, percentUp);
        SystemCallResult result = new SystemCall().fire(executorService, command);
        throwExceptionOnUnexpectedExitCode(command, result, 0);
        logger.info(String.format("Increased volume to %d%%", getVolumePercent(result.stdOut)));
    }

    public void volumeDown(int percentDown) {
        if (getVolume() <= MIN_VOLUME_PERCENT) {
            logger.fine(String.format("Ignore volume down event, volume level is already @ %d%%", MIN_VOLUME_PERCENT));
            return;
        }

        String command = String.format("%s -%d", mpcVolumeCommandSkeleton, percentDown);
        SystemCallResult result = new SystemCall().fire(executorService, command);
        throwExceptionOnUnexpectedExitCode(command, result, 0);
        logger.info(String.format("Decrease volume to %d%%", getVolumePercent(result.stdOut)));
    }

    private int getVolume() {
        String command = mpcVolumeCommandSkeleton;
        SystemCallResult result = new SystemCall().fire(executorService, command);
        throwExceptionOnUnexpectedExitCode(command, result, 0);
        return getVolumePercent(result.stdOut);
    }
}