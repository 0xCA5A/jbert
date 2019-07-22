package event;


import mpd.MpdService;
import rfid.RfidTagDetectionListener;
import rfid.RfidTagUid;

public class PlaylistChangeAction extends RfidTagDetectionListener {

    private final MpdService mpdService;
    private final RfidTagUid rfidTagUid;
    private final String playlist;

    public PlaylistChangeAction(MpdService mpdService, RfidTagUid rfidTagUid, String playlist) {
        this.mpdService = mpdService;
        this.rfidTagUid = rfidTagUid;
        this.playlist = playlist;
    }

    @Override
    public void rfidTagAction(RfidTagUid rfidTagUid) {
        if (this.rfidTagUid.equals(rfidTagUid)) {
            mpdService.loadPlaylist(playlist);
        }
    }
}
