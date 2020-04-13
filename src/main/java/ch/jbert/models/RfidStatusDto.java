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
@JsonIgnoreProperties({ "enabledOptional" })
@JsonDeserialize(builder = RfidStatusDto.Builder.class)
public final class RfidStatusDto {
  private final Boolean enabled;

  public RfidStatusDto(Boolean enabled) {
    this.enabled = enabled;
  }

  Boolean isEnabled() {
    return this.enabled;
  }
  
  public Optional<Boolean> isEnabledOptional() {
    return Optional.ofNullable(this.enabled);
  }

  public RfidStatusDto withEnabled(Boolean enabled) {
    return new RfidStatusDto(enabled);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    RfidStatusDto v = (RfidStatusDto) other;
    return Objects.equals(enabled, v.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled);
  }

  @Override
  public String toString() {
    return String.format("RfidStatusDto[enabled=%s]", 
      enabled);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private Boolean enabled;
  
    public RfidStatusDto build() {
      return new RfidStatusDto(enabled);
    }
  
    public Builder setEnabled(Boolean value) {
      this.enabled  = value;
      return this;
    }
  }
}