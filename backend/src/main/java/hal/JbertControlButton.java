package hal;

public enum JbertControlButton {
    YELLOW_FRONT_RIGHT("GPIO 4"), // bcm 23
    YELLOW_FRONT_LEFT("GPIO 27"), // bcm 16
    YELLOW_TOP_LEFT("GPIO 21"), // bcm 5
    YELLOW_TOP_RIGHT("GPIO 22"); // bcm 6

    private final String gpioName;

    JbertControlButton(String gpioName) {
        this.gpioName = gpioName;
    }

    public String getGpioName() {
        return gpioName;
    }
}
