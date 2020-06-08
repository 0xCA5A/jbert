package ch.jbert.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Metadata;
import ch.jbert.models.Track;
import ch.jbert.utils.TestData;
import ch.jbert.utils.TestEnvironment;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
class TrackServiceTest extends TestEnvironment {

    private final Track track;

    public TrackServiceTest() throws IOException, URISyntaxException {
        track = TestData.getTrack();
    }

    @Inject
    private TrackService trackService;

    @Test
    void create_when_newTrack_then_create() throws IOException {
        trackService.create(track);
        assertEquals(Arrays.asList(track), trackService.getAll());
    }

    @Test
    void create_when_withMetadata_then_keepMetadata() throws IOException {
        final Metadata metadata = track.getMetadata().get().getBuilder().withComment("foo, bar").build();
        final Track withMetadata = track.getBuilder().withMetadata(metadata).build();
        assertEquals(withMetadata, trackService.create(withMetadata));
    }

    @Test
    void create_when_withMetadata_then_changeHash() throws Exception {
        final Metadata metadata = track.getMetadata().get().getBuilder().withComment("foo, bar").build();
        final Track withMetadata = track.getBuilder().withMetadata(metadata).build();
        final Track created = trackService.create(withMetadata);
        assertFalse(withMetadata.calculateSha256().equals(created.calculateSha256()));
    }

    @Test
    void create_when_withoutMetadata_then_takeMetadataFromFile() throws IOException {
        final Track withoutMetadata = track.getBuilder().withMetadata(null).build();
        assertEquals(track, trackService.create(withoutMetadata));
    }

    @Test
    void create_when_withoutMetadata_then_doNotChangeHash() throws Exception {
        final Track withoutMetadata = track.getBuilder().withMetadata(null).build();
        final Track created = trackService.create(withoutMetadata);
        assertEquals(withoutMetadata.calculateSha256(), created.calculateSha256());
    }

    @Test
    void create_when_duplicateTrack_then_throwIllegalArgumentException() throws IOException {
        trackService.create(track);
        assertThrows(IllegalArgumentException.class, () -> trackService.create(track));
    }

    @Test
    void create_when_updatedTrack_then_throwIllegalArgumentException() throws IOException {
        final Metadata metadata = track.getMetadata().get().getBuilder().withComment("foo, bar").build();
        final Track update = track.getBuilder().withMetadata(metadata).build();
        trackService.create(track);
        assertThrows(IllegalArgumentException.class, () -> trackService.create(update));
    }

    @Test
    void create_when_null_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> trackService.create(null));
    }

    @Test
    void create_when_withoutData_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trackService.create(new Track(track.getMetadata().get(), null)));
    }

    @Test
    void getAll_when_noTracks_then_empty() throws IOException {
        assertTrue(trackService.getAll().isEmpty());
    }

    @Test
    void findAllByName_when_caseSensitiveMatch_then_findTrack() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        trackService.create(track);
        trackService.create(track2);
        final List<Track> result = trackService.findAllByName("bar");
        assertEquals(Arrays.asList(track2), result);
    }

    @Test
    void findAllByName_when_caseInsensitiveMatch_then_findTrack() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        trackService.create(track);
        trackService.create(track2);
        final List<Track> result = trackService.findAllByName("BAR");
        assertEquals(Arrays.asList(track2), result);
    }

    @Test
    void findAllByName_when_noMatch_then_empty() throws IOException {
        trackService.create(track);
        assertTrue(trackService.findAllByName("no match").isEmpty());
    }

    @Test
    void findAllByName_when_null_then_getAll() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        trackService.create(track);
        trackService.create(track2);
        assertEquals(trackService.getAll(), trackService.findAllByName(null));
    }

    @Test
    void findAllByName_when_empty_then_getAll() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        trackService.create(track);
        trackService.create(track2);
        assertEquals(trackService.getAll(), trackService.findAllByName(""));
    }

    @Test
    void filterByName_when_caseSensitiveMatch_returnMatch() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        final List<Track> result = TrackService.filterByName(Arrays.asList(track, track2), "bar");
        assertEquals(Arrays.asList(track2), result);
    }

    @Test
    void filterByName_when_caseInsensitiveMatch_returnMatch() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        final List<Track> result = TrackService.filterByName(Arrays.asList(track, track2), "BAR");
        assertEquals(Arrays.asList(track2), result);
    }

    @Test
    void filterByName_when_filterNull_returnMatch() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        final List<Track> result = TrackService.filterByName(Arrays.asList(track, track2), null);
        assertEquals(Arrays.asList(track, track2), result);
    }

    @Test
    void filterByName_when_filterEmpty_returnMatch() throws Exception {
        final Track track2 = TestData.getChangedTrackWithTitle("foo bar baz");
        final List<Track> result = TrackService.filterByName(Arrays.asList(track, track2), "");
        assertEquals(Arrays.asList(track, track2), result);
    }

    @Test
    void filterByName_when_emptyList_then_empty() {
        assertTrue(TrackService.filterByName(Collections.emptyList(), "foo").isEmpty());
    }

    @Test
    void filterByName_when_emptyListAndFilterNull_then_empty() {
        assertTrue(TrackService.filterByName(Collections.emptyList(), null).isEmpty());
    }

    @Test
    void filterByName_when_emptyListAndFilterEmpty_then_empty() {
        assertTrue(TrackService.filterByName(Collections.emptyList(), "").isEmpty());
    }

    @Test
    void findOneByHash_when_match_then_returnMatch() throws Exception {
        final Track created = trackService.create(track);
        assertEquals(created, trackService.findOneByHash(created.calculateSha256()).get());
    }

    @Test
    void findOneByHash_when_noMatch_then_empty() throws Exception {
        trackService.create(track);
        assertFalse(trackService.findOneByHash(track.calculateSha256()).isPresent());
    }

    @Test
    void update_when_unchanged_then_replaceExisting() throws IOException {
        trackService.create(track);
        trackService.update(track, track);
        final List<Track> result = trackService.getAll();
        assertEquals(Arrays.asList(track), result);
    }

    @Test
    void update_when_changed_then_replaceExisting() throws Exception {
        trackService.create(track);
        final Track update = TestData.getChangedTrackWithComment("foo, bar");
        trackService.update(track, update);
        final List<Track> result = trackService.getAll();
        assertEquals(Arrays.asList(update), result);
    }

    @Test
    void update_when_withMetadata_then_keepMetadata() throws IOException {
        trackService.create(track);
        final Metadata metadata = track.getMetadata().get().getBuilder().withComment("foo, bar").build();
        final Track withMetadata = track.getBuilder().withMetadata(metadata).build();
        assertEquals(withMetadata, trackService.update(track, withMetadata));
    }

    @Test
    void update_when_changed_then_changeHash() throws Exception {
        trackService.create(track);
        final Track withMetadata = TestData.getChangedTrackWithComment("foo, bar");
        final Track updated = trackService.update(track, withMetadata);
        assertFalse(withMetadata.calculateSha256().equals(updated.calculateSha256()));
    }

    @Test
    void update_when_withoutMetadata_then_takeMetadataFromFile() throws IOException {
        trackService.create(track);
        final Track withoutMetadata = track.getBuilder().withMetadata(null).build();
        assertEquals(track, trackService.update(track, withoutMetadata));
    }

    @Test
    void update_when_unchanged_then_doNotChangeHash() throws Exception {
        final Track withoutMetadata = track.getBuilder().withMetadata(null).build();
        trackService.create(withoutMetadata);
        Track updated = trackService.update(withoutMetadata, withoutMetadata);
        assertEquals(withoutMetadata.calculateSha256(), updated.calculateSha256());
    }

    @Test
    void update_when_notExists_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trackService.update(track, track));
    }

    @Test
    void delete_when_exists_then_delete() throws IOException {
        trackService.create(track);
        trackService.delete(track);
        assertTrue(trackService.getAll().isEmpty());
    }

    @Test
    void delete_when_notExists_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trackService.delete(track));
    }

    @Test
    void exists_when_exists_then_true() throws IOException {
        trackService.create(track);
        assertTrue(trackService.exists(track));
    }

    @Test
    void exists_when_notExists_then_false() {
        assertFalse(trackService.exists(track));
    }

    @Test
    void notExists_when_exists_then_false() throws IOException {
        trackService.create(track);
        assertFalse(trackService.notExists(track));
    }

    @Test
    void notExists_when_notExists_then_true() {
        assertTrue(trackService.notExists(track));
    }

    @Test
    void getRelativeFilePath_when_wihMetadata_then_readFromMetadata() throws IOException {
        final Metadata metadata = track.getMetadata().get().getBuilder()
                .withArtist("David Hasselhoff")
                .withAlbum("Greatest Hits")
                .withTitle("Crazy for You")
                .build();
        final Track withMetadata = track.getBuilder().withMetadata(metadata).build();
        assertEquals("David Hasselhoff/Greatest Hits/Crazy for You.mp3", trackService.getRelativeFilePath(withMetadata));
    }

    @Test
    void getRelativeFilePath_when_withoutMetadata_then_readFromFile() throws IOException {
        final Track withoutMetadata = track.getBuilder().withMetadata(null).build();
        assertEquals("Silicon Transmitter/Additives/Virus.mp3", trackService.getRelativeFilePath(withoutMetadata));
    }

    @Test
    void getRelativeFilePath_when_withoutData_then_readFromMetadata() throws IOException {
        final Track withoutMetadata = track.getBuilder().withData((String) null).build();
        assertEquals("Silicon Transmitter/Additives/Virus.mp3", trackService.getRelativeFilePath(withoutMetadata));
    }

    @Test
    void getRelativeFilePath_when_emptyTrack_then_dunno() throws IOException {
        final Track emptyTrack = Track.newBuilder().build();
        assertEquals("Unknown Artist/Unknown Album/Unknown Title.mp3", trackService.getRelativeFilePath(emptyTrack));
    }

    @Test
    void getRelativeFilePath_when_null_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> trackService.getRelativeFilePath(null));
    }
}
