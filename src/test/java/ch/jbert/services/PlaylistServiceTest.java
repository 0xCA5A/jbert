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
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Playlist;
import ch.jbert.models.Track;
import ch.jbert.utils.TestData;
import ch.jbert.utils.TestEnvironment;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class PlaylistServiceTest extends TestEnvironment {

    private final Playlist playlist;

    public PlaylistServiceTest() throws IOException, URISyntaxException {
        playlist = TestData.getPlaylist();
    }

    @Inject
    private PlaylistService playlistService;

    @Test
    void create_when_newPlaylist_then_create() throws IOException {
        playlistService.create(playlist);
        assertEquals(Arrays.asList(playlist), playlistService.getAll());
    }

    @Test
    void create_when_withoutName_then_throwIllegalArgumentException() throws IOException {
        Playlist withoutName = playlist.getBuilder().withName(null).build();
        assertThrows(IllegalArgumentException.class, () -> playlistService.create(withoutName));
    }

    @Test
    void create_when_null_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> playlistService.create(null));
    }

    @Test
    void create_when_empty_then_create() throws IOException {
        Playlist empty = playlist.getBuilder().withTracks(Collections.emptyList()).build();
        playlistService.create(empty);
        assertEquals(empty, playlistService.getAll().get(0));
    }

    @Test
    void getAll_when_noPlaylists_then_empty() throws IOException {
        assertTrue(playlistService.getAll().isEmpty());
    }

    @Test
    void findAllByName_when_caseSensitiveMatch_then_findPlaylist() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        final List<Playlist> result = playlistService.findAllByName("bar");
        assertEquals(Arrays.asList(playlist2), result);
    }

    @Test
    void findAllByName_when_caseInsensitiveMatch_then_findPlaylist() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        final List<Playlist> result = playlistService.findAllByName("BAR");
        assertEquals(Arrays.asList(playlist2), result);
    }

    @Test
    void findAllByName_when_noMatch_then_empty() throws IOException {
        playlistService.create(playlist);
        assertTrue(playlistService.findAllByName("no match").isEmpty());
    }

    @Test
    void findAllByName_when_null_then_getAll() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        assertEquals(playlistService.getAll(), playlistService.findAllByName(null));
    }

    @Test
    void findAllByName_when_empty_then_getAll() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        assertEquals(playlistService.getAll(), playlistService.findAllByName(""));
    }

    @Test
    void findOneByName_when_caseSensitiveMatch_then_findPlaylist() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        Optional<Playlist> result = playlistService.findOneByName("foo bar baz");
        assertTrue(result.isPresent());
        assertEquals(playlist2, result.get());
    }

    @Test
    void findOneByName_when_caseInsensitiveMatch_then_noResult() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        assertFalse(playlistService.findOneByName("FOO BAR BAZ").isPresent());
    }

    @Test
    void findOneByName_when_noMatch_then_noResult() throws IOException {
        playlistService.create(playlist);
        assertFalse(playlistService.findOneByName("no match").isPresent());
    }

    @Test
    void findOneByName_when_null_then_noResult() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        assertFalse(playlistService.findOneByName(null).isPresent());
    }

    @Test
    void findOneByName_when_empty_then_noResult() throws IOException {
        final Playlist playlist2 = playlist.getBuilder().withName("foo bar baz").build();
        playlistService.create(playlist);
        playlistService.create(playlist2);
        assertFalse(playlistService.findOneByName("").isPresent());
    }

    @Test
    void update_when_unchanged_then_replaceExisting() throws IOException {
        playlistService.create(playlist);
        playlistService.update(playlist, playlist);
        final List<Playlist> result = playlistService.getAll();
        assertEquals(Arrays.asList(playlist), result);
    }

    @Test
    void update_when_changed_then_replaceExisting() throws Exception {
        playlistService.create(playlist);
        final Playlist update = playlist.getBuilder().withName("foo").build();
        playlistService.update(playlist, update);
        final List<Playlist> result = playlistService.getAll();
        assertEquals(Arrays.asList(update), result);
    }

    @Test
    void update_when_originalNotExists_then_throwIllegalArgumentException() throws IOException {
        playlistService.create(playlist);
        final Playlist nonexisting = playlist.getBuilder().withName("foo").build();
        assertThrows(IllegalArgumentException.class, () -> playlistService.update(nonexisting, playlist));
    }

    @Test
    void update_when_updateExists_then_throwIllegalArgumentException() throws IOException {
        playlistService.create(playlist);
        final Playlist existing = playlist.getBuilder().withName("foo").build();
        playlistService.create(existing);
        assertThrows(IllegalArgumentException.class, () -> playlistService.update(playlist, existing));
    }

    @Test
    void update_when_withoutName_then_keepOriginalName() throws IOException {
        playlistService.create(playlist);
        final Playlist withoutName = playlist.getBuilder().withName(null).build();
        playlistService.update(playlist, withoutName);
        assertEquals(Arrays.asList(playlist), playlistService.getAll());
    }

    @Test
    void delete_when_exists_then_delete() throws IOException {
        playlistService.create(playlist);
        playlistService.delete(playlist);
        assertTrue(playlistService.getAll().isEmpty());
    }

    @Test
    void delete_when_notExists_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> playlistService.delete(playlist));
    }

    @Test
    void addTrack_when_exists_then_addTrack() throws Exception {
        playlistService.create(playlist);
        playlistService.addTrack(playlist, TestData.getChangedTrackWithTitle("foo"));
        final Playlist result = playlistService.findOneByName(playlist.getName().get()).get();
        assertEquals(playlist.getTracks().size() + 1, result.getTracks().size());
    }

    @Test
    void addTrack_when_notExists_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> playlistService.addTrack(playlist, TestData.getChangedTrackWithTitle("foo")));
    }

    @Test
    void addTrack_when_playlistNull_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> playlistService.addTrack(null, TestData.getChangedTrackWithTitle("foo")));
    }

    @Test
    void addTrack_when_trackNull_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> playlistService.addTrack(playlist, null));
    }

    @Test
    void deleteTrackByIndex_when_exists_then_deleteTrack() throws Exception {
        playlistService.create(playlist);
        final Track track2 = TestData.getChangedTrackWithTitle("foo");
        playlistService.addTrack(playlist, track2);
        assertEquals(Arrays.asList(track2), playlistService.deleteTrackByIndex(playlist, 0).getTracks());
    }

    @Test
    void deleteTrackByIndex_when_playlistNotExists_then_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> playlistService.deleteTrackByIndex(playlist, 0));
    }

    @Test
    void deleteTrackByIndex_when_indexNotExists_then_throwIndexOutOfBoundsException() throws IOException {
        playlistService.create(playlist);
        assertThrows(IndexOutOfBoundsException.class, () -> playlistService.deleteTrackByIndex(playlist, 42));
    }

    @Test
    void deleteTrackByIndex_when_PlaylistNull_then_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> playlistService.deleteTrackByIndex(null, 0));
    }

    @Test
    void exists_when_exists_then_true() throws IOException {
        playlistService.create(playlist);
        assertTrue(playlistService.exists(playlist));
    }

    @Test
    void exists_when_notExists_then_false() {
        assertFalse(playlistService.exists(playlist));
    }

    @Test
    void notExists_when_exists_then_false() throws IOException {
        playlistService.create(playlist);
        assertFalse(playlistService.notExists(playlist));
    }

    @Test
    void notExists_when_notExists_then_true() {
        assertTrue(playlistService.notExists(playlist));
    }
}
