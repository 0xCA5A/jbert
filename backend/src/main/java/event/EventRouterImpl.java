package event;

import gpio.GpioService;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.PlaylistLoadAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import javax.inject.Inject;


public class EventRouterImpl implements EventRouter {
    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    @Inject
    EventRouterImpl(RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;

        configureGpiListener();
        configureRfidListener();
    }

    private void configureGpiListener() {
        gpioService.registerGpiListener("GPIO 4", new VolumeDownAction(mpdService)); // bcm 23
        gpioService.registerGpiListener("GPIO 27", new VolumeUpAction(mpdService)); // bcm 16

        gpioService.registerGpiListener("GPIO 21", new PlayPauseAction(mpdService)); // bcm 5
        gpioService.registerGpiListener("GPIO 22", new PlayNextTrackAction(mpdService)); // bcm 6

        gpioService.registerGpiListener("GPIO 3", new PlaylistLoadAction(mpdService, "red")); // bcm 22, red jbert button
        gpioService.registerGpiListener("GPIO 23", new PlaylistLoadAction(mpdService, "black")); // bcm 13, black jbert button
        gpioService.registerGpiListener("GPIO 7", new PlaylistLoadAction(mpdService, "green")); // bcm 4, blue jbert button
        gpioService.registerGpiListener("GPIO 25", new PlaylistLoadAction(mpdService, "blue")); // bcm 26, blue jbert button
        gpioService.registerGpiListener("GPIO 26", new PlaylistLoadAction(mpdService, "yellow")); // bcm 12, blue jbert button
    }

    private void configureRfidListener() {
        registerRfidServiceListener(new RfidTagUid("55-68-00-D2-EF"), "red");
        registerRfidServiceListener(new RfidTagUid("FA-00-9C-73-15"), "black");
        registerRfidServiceListener(new RfidTagUid("E5-EA-00-D2-DD"), "green");
        registerRfidServiceListener(new RfidTagUid("F5-BA-4E-D3-D2"), "blue");
        registerRfidServiceListener(new RfidTagUid("F5-82-FE-D1-58"), "yellow");
    }

    private void registerRfidServiceListener(RfidTagUid rfidTagUid, String playlist) {
        PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService, rfidTagUid, playlist);
        rfidService.addListener(playListChangeAction);
    }
}
