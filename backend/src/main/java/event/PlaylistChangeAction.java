package event;


import mpd.MpdService;
import rfid.RfidTagDetectionListener;
import rfid.RfidTagUid;
import util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlaylistChangeAction extends RfidTagDetectionListener {
    // Static config
    private final static Map<String, String> playListMap;
    private static final String redTagUid = "55-68-00-D2-EF";
    private static final String redTagPlaylistName = "red";
    private static final String blueTagUid = "F5-BA-4E-D3-D2";
    private static final String blueTagPlaylistName = "blue";
    private static final String greenTagUid = "E5-EA-00-D2-DD";
    private static final String greenTagPlaylistName = "green";
    private static Logger logger = LogHelper.getLogger(PlaylistChangeAction.class.getName());

    static {
        playListMap = new HashMap<>();
        playListMap.put(redTagUid, redTagPlaylistName);
        playListMap.put(blueTagUid, blueTagPlaylistName);
        playListMap.put(greenTagUid, greenTagPlaylistName);
    }

    private final MpdService mpdService;

    public PlaylistChangeAction(MpdService mpdService) {
        this.mpdService = mpdService;
    }

    @Override
    public void rfidTagAction(RfidTagUid rfidTagUid) {
        final String key = rfidTagUid.toString();
        if (playListMap.containsKey(key)) {
            mpdService.loadPlaylist(playListMap.get(key));
        } else {
            logger.warning(String.format("No action for unsupported RFID Tag UID '%s'", rfidTagUid.toString()));
        }
    }
}
