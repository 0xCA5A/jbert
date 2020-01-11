package event.router;

import gpio.GpioService;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdService;

import static hal.JbertControlButtonName.*;


public class EventRouterUtil {
    public static void configureControlGpiListener(GpioService gpioService, MpdService mpdService) {
        gpioService.registerGpiListener(YELLOW_FRONT_RIGHT, new VolumeDownAction(mpdService));
        gpioService.registerGpiListener(YELLOW_FRONT_LEFT, new VolumeUpAction(mpdService));
        gpioService.registerGpiListener(YELLOW_TOP_LEFT, new PlayPauseAction(mpdService));
        gpioService.registerGpiListener(YELLOW_TOP_RIGHT, new PlayNextTrackAction(mpdService));
    }
}
