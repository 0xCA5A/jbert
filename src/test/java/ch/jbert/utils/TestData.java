package ch.jbert.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.jbert.models.Metadata;
import ch.jbert.models.Playlist;
import ch.jbert.models.Track;

public class TestData {

    private static final Logger LOG = LoggerFactory.getLogger(TestData.class);

    private TestData() {}

    public static Track getTrack() throws IOException, URISyntaxException {
        final Metadata metadata = Metadata.newBuilder().withArtist("Silicon Transmitter").withAlbum("Additives")
            .withTitle("Virus").withGenre("Krautrock").withYear(2018).withDuration(1)
            .withComment("URL: http://freemusicarchive.org/music/Silicon_Transmitter/Additives/Virus_1339\r\n"
                    + "Comments: http://freemusicarchive.org/\r\n" + "Curator: Nul Tiel Records\r\n"
                    + "Copyright: Attribution-NonCommercial-ShareAlike: http://creativecommons.org/licenses/by-nc-sa/4.0/")
            .build();
        return Track.newBuilder()
                .withMetadata(metadata)
                .withData(readFileDataBase64("Silicon_Transmitter_-_08_-_Virus_1s.mp3"))
                .build();
    }

    public static Track getChangedTrackWithTitle(String title) throws IOException, URISyntaxException {
        final Metadata metadata = getTrack().getMetadata().get().getBuilder().withTitle("foo bar baz").build();
        return getTrack().getBuilder().withMetadata(metadata).build();
    }

    public static Track getChangedTrackWithComment(String Comment) throws IOException, URISyntaxException {
        final Metadata metadata = getTrack().getMetadata().get().getBuilder().withComment("foo, bar").build();
        return getTrack().getBuilder().withMetadata(metadata).build();
    }

    public static List<Track> getTracks() throws IOException, URISyntaxException {
        return Arrays.asList(getTrack());
    }

    public static Playlist getPlaylist() throws IOException, URISyntaxException {
        return Playlist.newBuilder()
                .withName("Creative Commons")
                .withTracks(getTracks())
                .build();
    }

    public static Playlist getChangedPlaylist() throws IOException, URISyntaxException {
        return getPlaylist().getBuilder().withTracks(Arrays.asList(getChangedTrackWithTitle("foo"))).build();
    }

    public static String readFileDataBase64(String filename) throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource("data/" + filename).toURI());
        final String data = Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));
        LOG.debug("Reading file data of file {}", filePath);
        LOG.trace("File data read: {}", data);
        return data;
    }
}
