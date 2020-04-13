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
@JsonIgnoreProperties({ "titleOptional", "artistOptional", "albumOptional", "yearOptional", "genreOptional", "commentOptional", "durationOptional" })
@JsonDeserialize(builder = MetadataDto.Builder.class)
public final class MetadataDto {
  private final String title;
  private final String artist;
  private final String album;
  private final Integer year;
  private final String genre;
  private final String comment;
  private final Integer duration;

  public MetadataDto(String title, String artist, String album, Integer year, String genre, String comment, Integer duration) {
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.year = year;
    this.genre = genre;
    this.comment = comment;
    this.duration = duration;
  }

  String getTitle() {
    return this.title;
  }
  
  public Optional<String> getTitleOptional() {
    return Optional.ofNullable(this.title);
  }
  
  String getArtist() {
    return this.artist;
  }
  
  public Optional<String> getArtistOptional() {
    return Optional.ofNullable(this.artist);
  }
  
  String getAlbum() {
    return this.album;
  }
  
  public Optional<String> getAlbumOptional() {
    return Optional.ofNullable(this.album);
  }
  
  Integer getYear() {
    return this.year;
  }
  
  public Optional<Integer> getYearOptional() {
    return Optional.ofNullable(this.year);
  }
  
  String getGenre() {
    return this.genre;
  }
  
  public Optional<String> getGenreOptional() {
    return Optional.ofNullable(this.genre);
  }
  
  String getComment() {
    return this.comment;
  }
  
  public Optional<String> getCommentOptional() {
    return Optional.ofNullable(this.comment);
  }
  
  Integer getDuration() {
    return this.duration;
  }
  
  public Optional<Integer> getDurationOptional() {
    return Optional.ofNullable(this.duration);
  }

  public MetadataDto withTitle(String title) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withArtist(String artist) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withAlbum(String album) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withYear(Integer year) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withGenre(String genre) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withComment(String comment) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }
  
  public MetadataDto withDuration(Integer duration) {
    return new MetadataDto(title, artist, album, year, genre, comment, duration);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    MetadataDto v = (MetadataDto) other;
    return Objects.equals(title, v.title) && Objects.equals(artist, v.artist) && Objects.equals(album, v.album) && Objects.equals(year, v.year) && Objects.equals(genre, v.genre) && Objects.equals(comment, v.comment) && Objects.equals(duration, v.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, artist, album, year, genre, comment, duration);
  }

  @Override
  public String toString() {
    return String.format("MetadataDto[title=%s, artist=%s, album=%s, year=%s, genre=%s, comment=%s, duration=%s]", 
      title, artist, album, year, genre, comment, duration);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private String title;
    private String artist;
    private String album;
    private Integer year;
    private String genre;
    private String comment;
    private Integer duration;
  
    public MetadataDto build() {
      return new MetadataDto(title, artist, album, year, genre, comment, duration);
    }
  
    public Builder setTitle(String value) {
      this.title  = value;
      return this;
    }
    
    public Builder setArtist(String value) {
      this.artist  = value;
      return this;
    }
    
    public Builder setAlbum(String value) {
      this.album  = value;
      return this;
    }
    
    public Builder setYear(Integer value) {
      this.year  = value;
      return this;
    }
    
    public Builder setGenre(String value) {
      this.genre  = value;
      return this;
    }
    
    public Builder setComment(String value) {
      this.comment  = value;
      return this;
    }
    
    public Builder setDuration(Integer value) {
      this.duration  = value;
      return this;
    }
  }
}