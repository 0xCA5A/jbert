package hal;


public enum JbertPlaylistSelectButton {
    RED("GPIO 3"), // bcm 22
    BLACK("GPIO 23"), // bcm 13
    GREEN("GPIO 7"), // bcm 4
    BLUE("GPIO 25"), // bcm 26
    YELLOW("GPIO 26"); // bcm 12

    private final String gpioName;

    JbertPlaylistSelectButton(String gpioName) {
        this.gpioName = gpioName;
    }

    public String getGpioName() {
        return gpioName;
    }
}
