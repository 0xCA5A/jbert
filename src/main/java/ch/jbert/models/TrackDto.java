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
@JsonIgnoreProperties({ "metadataOptional", "dataOptional" })
@JsonDeserialize(builder = TrackDto.Builder.class)
public final class TrackDto {
  private final MetadataDto metadata;
  private final String data;

  public TrackDto(MetadataDto metadata, String data) {
    this.metadata = metadata;
    this.data = data;
  }

  MetadataDto getMetadata() {
    return this.metadata;
  }
  
  public Optional<MetadataDto> getMetadataOptional() {
    return Optional.ofNullable(this.metadata);
  }
  
  String getData() {
    return this.data;
  }
  
  /**
   * Base64 encoded data
   */
  public Optional<String> getDataOptional() {
    return Optional.ofNullable(this.data);
  }

  public TrackDto withMetadata(MetadataDto metadata) {
    return new TrackDto(metadata, data);
  }
  
  /**
   * Base64 encoded data
   */
  public TrackDto withData(String data) {
    return new TrackDto(metadata, data);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TrackDto v = (TrackDto) other;
    return Objects.equals(metadata, v.metadata) && Objects.equals(data, v.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metadata, data);
  }

  @Override
  public String toString() {
    return String.format("TrackDto[metadata=%s, data=%s]", 
      metadata, data);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private MetadataDto metadata;
    private String data;
  
    public TrackDto build() {
      return new TrackDto(metadata, data);
    }
  
    public Builder setMetadata(MetadataDto value) {
      this.metadata  = value;
      return this;
    }
    
    /**
     * Base64 encoded data
     */
    public Builder setData(String value) {
      this.data  = value;
      return this;
    }
  }
}