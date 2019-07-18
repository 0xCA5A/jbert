# The jbert Project
## Idea
The Hörbert (https://www.hoerbert.com) is cool but too 
expensive and not flexible enough.

## Prototype #1
### Hardware
 * Raspberry Pi Zero W
 * Waveshare WM8960 Hi-Fi Sound Card HAT for Raspberry Pi
 * MFRC522 RFID Reader

### Software (not complete)
 * Raspbian Lite: https://www.raspberrypi.org/downloads/raspbian/
 * WM8960 driver: https://github.com/waveshare/WM8960-Audio-HAT
 * MPD: https://www.musicpd.org/
 * Raspberry Pi GPIO config tool / lib: https://projects.drogon.net/raspberry-pi/wiringpi/
 * Pi4J: https://pi4j.com/1.2/index.html
 * JavaMPD: https://github.com/finnyb/javampd
 * sbt-openapi-schema: https://github.com/eikek/sbt-openapi-schema

### How-to
#### Raspbian installation
 * Copy the Raspbian system to the microSD card (Tested with 2019-07-10-raspbian-buster-lite.zip)
   * Place an empty file named 'ssh' onto the boot (FAT) partition (https://hackernoon.com/raspberry-pi-headless-install-462ccabd75d0)
   * Place a file named 'wpa_supplicant.conf' with this content onto the boot partition:
    ```text
    country=EU
    ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
    update_config=1

    network={
        ssid="NETWORK-NAME"
        psk="NETWORK-PASSWORD"
    }
    ```

#### System bootstrap
To install all needed software packages / drivers:
```
sam@guido:~/projects/git/jbert$ ssh-copy-id pi@10.0.50.123
sam@guido:~/projects/git/jbert$ ./bootstrap.sh 10.0.50.123
```

#### Application deployment
Debian package build (sbt-native-packager) and deploy is documented in this two scripts:
```
sam@guido:~/projects/git/jbert$ ssh-copy-id pi@10.0.50.123
sam@guido:~/projects/git/jbert$ ./cleanBuild.sh && ./deploy.sh 10.0.50.123 ./application/target/application_0.1.0-SNAPSHOT_all.deb
```

#### Target JVM monitoring
Due to the fact that the Raspberry Pi Zero hardware is not the most powerful one it might be good to understand
what is going on on the target JVM.
 * JMX enabled in the javaOptions defined in `build.sbt`
 * SOCKS tunnel to the target opened:
```
sam@guido:~$ ssh -v -D 9696 pi@10.0.50.123
```
 * VisualVM configured to use the SOCKS proxy
 * Connection to the target port opened (build.sbt, jmxremote.port)

Source: https://dzone.com/articles/visualvm-monitoring-remote-jvm
