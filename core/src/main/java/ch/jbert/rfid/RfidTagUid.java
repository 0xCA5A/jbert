package ch.jbert.rfid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class RfidTagUid {
    private static final int RFID_TAG_UID_STRING_LENGTH = 5 * 2 + 4;

    private final List<Integer> uid;

    RfidTagUid(byte[] uid) {
        this.uid = uidAsIntegerList(uid);
    }

    /**
     * RfidTagUid constructor
     *
     * @param uid expected format "55-68-00-D2-EF"
     */
    public RfidTagUid(String uid) {
        if (uid.length() != RFID_TAG_UID_STRING_LENGTH) {
            throw new IllegalArgumentException(String.format("Got RFID UID with unexpected length: '%s'", uid.length()));
        }
        this.uid = uidAsIntegerList(uid);
    }

    private static List<Integer> uidAsIntegerList(String uid) {
        final List<String> values = Arrays.asList(uid.split("-"));
        return values.stream()
                .map(e -> Integer.valueOf(e, 16))
                .collect(Collectors.toList());
    }

    private static List<Integer> uidAsIntegerList(byte[] uid) {
        List<Integer> uidInteger = new ArrayList<>();
        for (byte b : uid) {
            int value = b & 0xFF;
            uidInteger.add(value);
        }
        return uidInteger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uid);
    }

    @Override
    public boolean equals(Object that) {

        if (that == this) return true;
        if (!(that instanceof RfidTagUid)) {
            return false;
        }
        RfidTagUid thatRfidTagUid = (RfidTagUid) that;
        return this.uid.equals(thatRfidTagUid.uid);
    }

    @Override
    public String toString() {
        return uid.stream()
                .map(e -> String.format("%02X", e))
                .collect(Collectors.joining("-"));
    }
}
