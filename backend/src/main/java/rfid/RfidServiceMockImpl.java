package rfid;

import util.LogHelper;
import util.ThreadHelper;

import javax.inject.Inject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;


public class RfidServiceMockImpl implements RfidService {
    private static final Logger logger = LogHelper.getLogger(RfidServiceMockImpl.class.getName());
    private static final Map<String, RfidTagUid> PLAYLIST_RFID_TAG_UID_MAP = new HashMap<>();

    static {
        final String redTagUid = "55-68-00-D2-EF";
        final String redTagPlaylistName = "red";
        PLAYLIST_RFID_TAG_UID_MAP.put(redTagPlaylistName, new RfidTagUid(redTagUid));

        final String blueTagUid = "F5-BA-4E-D3-D2";
        final String blueTagPlaylistName = "blue";
        PLAYLIST_RFID_TAG_UID_MAP.put(blueTagPlaylistName, new RfidTagUid(blueTagUid));

        final String greenTagUid = "E5-EA-00-D2-DD";
        final String greenTagPlaylistName = "green";
        PLAYLIST_RFID_TAG_UID_MAP.put(greenTagPlaylistName, new RfidTagUid(greenTagUid));
    }

    private RfidTagUid rfidTagUid;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Inject
    RfidServiceMockImpl(ExecutorService executorService) {
        executorService.submit(this::fireRandomRfidTagFoundEvents);
    }

    private void fireRandomRfidTagFoundEvents() {
        while (true) {
            ThreadHelper.snooze(Duration.ofSeconds(10));
            RfidTagUid rfidTagUid = getRandomRfidTagUid();
            notifyListener(rfidTagUid);
        }
    }

    private RfidTagUid getRandomRfidTagUid() {
        Random r = new Random();
        List<RfidTagUid> values = new ArrayList<>(PLAYLIST_RFID_TAG_UID_MAP.values());
        return values.get(r.nextInt(values.size()));
    }

    public void notifyListener(RfidTagUid rfidTagUid) {
        propertyChangeSupport.firePropertyChange("rfidTagUid", this.rfidTagUid, rfidTagUid);
        this.rfidTagUid = rfidTagUid;

        logger.info(String.format("Notification for RFID Tag UID '%s' fired", rfidTagUid));
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}