package ch.jbert.event.router;

import ch.jbert.mpd.MpdService;
import ch.jbert.rfid.RfidTagDetectionListener;
import ch.jbert.rfid.RfidTagUid;


public class PlaylistChangeAction extends RfidTagDetectionListener {

    private final MpdService mpdService;
    private final RfidTagUid rfidTagUid;
    private final String playlist;
    private final boolean autoPlayback;

    public PlaylistChangeAction(MpdService mpdService, RfidTagUid rfidTagUid, String playlist, boolean autoPlayback) {
        this.mpdService = mpdService;
        this.rfidTagUid = rfidTagUid;
        this.playlist = playlist;
        this.autoPlayback = autoPlayback;
    }

    @Override
    public void rfidTagAction(RfidTagUid rfidTagUid) {
        if (this.rfidTagUid.equals(rfidTagUid)) {
            mpdService.loadPlaylist(playlist);

            if (autoPlayback) {
                mpdService.play();
            }
        }

    }
}
