package rfid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class RfidTagUid {
    private final List<Integer> uid;

    public RfidTagUid(byte[] uid) {
        this.uid = uidAsIntegerList(uid);
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
