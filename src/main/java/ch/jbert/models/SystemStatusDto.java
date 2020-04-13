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
@JsonIgnoreProperties({ "systemLoadOptional", "memUsageOptional", "uptimeOptional" })
@JsonDeserialize(builder = SystemStatusDto.Builder.class)
public final class SystemStatusDto {
  private final Float systemLoad;
  private final Float memUsage;
  private final Integer uptime;

  public SystemStatusDto(Float systemLoad, Float memUsage, Integer uptime) {
    this.systemLoad = systemLoad;
    this.memUsage = memUsage;
    this.uptime = uptime;
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
  
  Integer getUptime() {
    return this.uptime;
  }
  
  public Optional<Integer> getUptimeOptional() {
    return Optional.ofNullable(this.uptime);
  }

  public SystemStatusDto withSystemLoad(Float systemLoad) {
    return new SystemStatusDto(systemLoad, memUsage, uptime);
  }
  
  public SystemStatusDto withMemUsage(Float memUsage) {
    return new SystemStatusDto(systemLoad, memUsage, uptime);
  }
  
  public SystemStatusDto withUptime(Integer uptime) {
    return new SystemStatusDto(systemLoad, memUsage, uptime);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    SystemStatusDto v = (SystemStatusDto) other;
    return Objects.equals(systemLoad, v.systemLoad) && Objects.equals(memUsage, v.memUsage) && Objects.equals(uptime, v.uptime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(systemLoad, memUsage, uptime);
  }

  @Override
  public String toString() {
    return String.format("SystemStatusDto[systemLoad=%s, memUsage=%s, uptime=%s]", 
      systemLoad, memUsage, uptime);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private Float systemLoad;
    private Float memUsage;
    private Integer uptime;
  
    public SystemStatusDto build() {
      return new SystemStatusDto(systemLoad, memUsage, uptime);
    }
  
    public Builder setSystemLoad(Float value) {
      this.systemLoad  = value;
      return this;
    }
    
    public Builder setMemUsage(Float value) {
      this.memUsage  = value;
      return this;
    }
    
    public Builder setUptime(Integer value) {
      this.uptime  = value;
      return this;
    }
  }
}