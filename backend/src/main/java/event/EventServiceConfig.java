package event;

import event.router.EventRouterType;
import rfid.RfidTagUid;

import java.util.Map;


public final class EventServiceConfig {
    private final EventRouterType eventRouterType;
    private final Map<RfidTagUid, String> rfidTagMapping;

    public EventServiceConfig(EventRouterType eventRouterType, Map<RfidTagUid, String> rfidTagMapping) {
        this.eventRouterType = eventRouterType;
        this.rfidTagMapping = rfidTagMapping;
    }

    public EventRouterType getEventRouterType() {
        return eventRouterType;
    }

    public Map<RfidTagUid, String> getRfidTagMapping() {
        return rfidTagMapping;
    }
}
