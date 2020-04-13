package ch.jbert.models;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

// This is a generated file. Do not edit.

@JsonAutoDetect(getterVisibility = Visibility.NON_PRIVATE)
@JsonIgnoreProperties({ "nameOptional", "tracksOptional" })
@JsonDeserialize(builder = PlaylistDto.Builder.class)
public final class PlaylistDto {
  private final String name;
  private final List<TrackDto> tracks;

  public PlaylistDto(String name, List<TrackDto> tracks) {
    this.name = name;
    this.tracks = tracks;
  }

  String getName() {
    return this.name;
  }
  
  public Optional<String> getNameOptional() {
    return Optional.ofNullable(this.name);
  }
  
  List<TrackDto> getTracks() {
    return this.tracks;
  }
  
  public Optional<List<TrackDto>> getTracksOptional() {
    return Optional.ofNullable(this.tracks);
  }

  public PlaylistDto withName(String name) {
    return new PlaylistDto(name, tracks);
  }
  
  public PlaylistDto withTracks(List<TrackDto> tracks) {
    return new PlaylistDto(name, tracks);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    PlaylistDto v = (PlaylistDto) other;
    return Objects.equals(name, v.name) && Objects.equals(tracks, v.tracks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, tracks);
  }

  @Override
  public String toString() {
    return String.format("PlaylistDto[name=%s, tracks=%s]", 
      name, tracks);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private String name;
    private List<TrackDto> tracks;
  
    public PlaylistDto build() {
      return new PlaylistDto(name, tracks);
    }
  
    public Builder setName(String value) {
      this.name  = value;
      return this;
    }
    
    public Builder setTracks(List<TrackDto> value) {
      this.tracks  = value;
      return this;
    }
  }
}