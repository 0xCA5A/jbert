package ch.jbert.models;

import java.util.Objects;
import java.util.Optional;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

// This is a generated file. Do not edit.

@JsonAutoDetect(getterVisibility = Visibility.NON_PRIVATE)
@JsonIgnoreProperties({ "volumeOptional", "numberOfPlaylistsOptional", "numberOfTracksOptional" })
@JsonDeserialize(builder = PlayerStatusDto.Builder.class)
public final class PlayerStatusDto {
  private final Float volume;
  private final Integer numberOfPlaylists;
  private final Integer numberOfTracks;

  public PlayerStatusDto(Float volume, Integer numberOfPlaylists, Integer numberOfTracks) {
    this.volume = volume;
    this.numberOfPlaylists = numberOfPlaylists;
    this.numberOfTracks = numberOfTracks;
  }

  Float getVolume() {
    return this.volume;
  }
  
  public Optional<Float> getVolumeOptional() {
    return Optional.ofNullable(this.volume);
  }
  
  Integer getNumberOfPlaylists() {
    return this.numberOfPlaylists;
  }
  
  public Optional<Integer> getNumberOfPlaylistsOptional() {
    return Optional.ofNullable(this.numberOfPlaylists);
  }
  
  Integer getNumberOfTracks() {
    return this.numberOfTracks;
  }
  
  public Optional<Integer> getNumberOfTracksOptional() {
    return Optional.ofNullable(this.numberOfTracks);
  }

  public PlayerStatusDto withVolume(Float volume) {
    return new PlayerStatusDto(volume, numberOfPlaylists, numberOfTracks);
  }
  
  public PlayerStatusDto withNumberOfPlaylists(Integer numberOfPlaylists) {
    return new PlayerStatusDto(volume, numberOfPlaylists, numberOfTracks);
  }
  
  public PlayerStatusDto withNumberOfTracks(Integer numberOfTracks) {
    return new PlayerStatusDto(volume, numberOfPlaylists, numberOfTracks);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    PlayerStatusDto v = (PlayerStatusDto) other;
    return Objects.equals(volume, v.volume) && Objects.equals(numberOfPlaylists, v.numberOfPlaylists) && Objects.equals(numberOfTracks, v.numberOfTracks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(volume, numberOfPlaylists, numberOfTracks);
  }

  @Override
  public String toString() {
    return String.format("PlayerStatusDto[volume=%s, numberOfPlaylists=%s, numberOfTracks=%s]", 
      volume, numberOfPlaylists, numberOfTracks);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private Float volume;
    private Integer numberOfPlaylists;
    private Integer numberOfTracks;
  
    public PlayerStatusDto build() {
      return new PlayerStatusDto(volume, numberOfPlaylists, numberOfTracks);
    }
  
    public Builder setVolume(Float value) {
      this.volume  = value;
      return this;
    }
    
    public Builder setNumberOfPlaylists(Integer value) {
      this.numberOfPlaylists  = value;
      return this;
    }
    
    public Builder setNumberOfTracks(Integer value) {
      this.numberOfTracks  = value;
      return this;
    }
  }
}