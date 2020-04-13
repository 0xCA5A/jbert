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
@JsonIgnoreProperties({ "systemLoadOptional", "memUsageOptional" })
@JsonDeserialize(builder = ConfigDto.Builder.class)
public final class ConfigDto {
  private final Float systemLoad;
  private final Float memUsage;

  public ConfigDto(Float systemLoad, Float memUsage) {
    this.systemLoad = systemLoad;
    this.memUsage = memUsage;
  }

  Float getSystemLoad() {
    return this.systemLoad;
  }
  
  public Optional<Float> getSystemLoadOptional() {
    return Optional.ofNullable(this.systemLoad);
  }
  
  Float getMemUsage() {
    return this.memUsage;
  }
  
  public Optional<Float> getMemUsageOptional() {
    return Optional.ofNullable(this.memUsage);
  }

  public ConfigDto withSystemLoad(Float systemLoad) {
    return new ConfigDto(systemLoad, memUsage);
  }
  
  public ConfigDto withMemUsage(Float memUsage) {
    return new ConfigDto(systemLoad, memUsage);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConfigDto v = (ConfigDto) other;
    return Objects.equals(systemLoad, v.systemLoad) && Objects.equals(memUsage, v.memUsage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(systemLoad, memUsage);
  }

  @Override
  public String toString() {
    return String.format("ConfigDto[systemLoad=%s, memUsage=%s]", 
      systemLoad, memUsage);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private Float systemLoad;
    private Float memUsage;
  
    public ConfigDto build() {
      return new ConfigDto(systemLoad, memUsage);
    }
  
    public Builder setSystemLoad(Float value) {
      this.systemLoad  = value;
      return this;
    }
    
    public Builder setMemUsage(Float value) {
      this.memUsage  = value;
      return this;
    }
  }
}