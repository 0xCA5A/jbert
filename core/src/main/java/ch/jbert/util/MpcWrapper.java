package ch.jbert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MpcWrapper {
    private static final String VOLUME_MATCH_GROUP_NAME = "volume";
    private static final String VOLUME_PATTERN = "^\\s*volume:\\s(?<" + VOLUME_MATCH_GROUP_NAME + ">\\d+)%.*$";
    private static final Pattern MPC_VOLUME_PATTERN = Pattern.compile(VOLUME_PATTERN, Pattern.DOTALL);
    private static Logger logger = LoggerFactory.getLogger(MpcWrapper.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final String mpcCommandSkeleton;
    private final String mpcVolumeCommandSkeleton;

    public MpcWrapper(String server, int port) {
        mpcCommandSkeleton = String.format("mpc --host %s --port %d", server, port);
        mpcVolumeCommandSkeleton = String.format("%s volume", mpcCommandSkeleton);
    }

    public Optional<SystemCallResult> setVolume(int volume) {
        return new SystemCall().fire(executorService, String.format("%s %d", mpcVolumeCommandSkeleton, volume));
    }

    public Optional<Integer> getVolume() {
        return new SystemCall()
                .fire(executorService, mpcVolumeCommandSkeleton)
                .flatMap(e -> extractVolumeValue(e.getStdOut()));
    }

    /**
     * Parses the volume information from the MPC command output
     * <p>>
     * Expected line:
     * volume: 26%
     *
     * @return an optional integer, if found in string
     */
    private Optional<Integer> extractVolumeValue(String mpcVolumeCommandOut) {
        final Matcher matcher = MPC_VOLUME_PATTERN.matcher(mpcVolumeCommandOut);
        if (!matcher.find()) {
            logger.error("Volume information not found in MPC volume command output stream");
            return Optional.empty();
        } else {
            final String match = matcher.group(VOLUME_MATCH_GROUP_NAME);
            return Optional.of(Integer.valueOf(match));
        }
    }
}
