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
@JsonIgnoreProperties({ "systemStatusOptional", "playerStatusOptional", "rfidStatusOptional" })
@JsonDeserialize(builder = StatusDto.Builder.class)
public final class StatusDto {
  private final SystemStatusDto systemStatus;
  private final PlayerStatusDto playerStatus;
  private final RfidStatusDto rfidStatus;

  public StatusDto(SystemStatusDto systemStatus, PlayerStatusDto playerStatus, RfidStatusDto rfidStatus) {
    this.systemStatus = systemStatus;
    this.playerStatus = playerStatus;
    this.rfidStatus = rfidStatus;
  }

  SystemStatusDto getSystemStatus() {
    return this.systemStatus;
  }
  
  public Optional<SystemStatusDto> getSystemStatusOptional() {
    return Optional.ofNullable(this.systemStatus);
  }
  
  PlayerStatusDto getPlayerStatus() {
    return this.playerStatus;
  }
  
  public Optional<PlayerStatusDto> getPlayerStatusOptional() {
    return Optional.ofNullable(this.playerStatus);
  }
  
  RfidStatusDto getRfidStatus() {
    return this.rfidStatus;
  }
  
  public Optional<RfidStatusDto> getRfidStatusOptional() {
    return Optional.ofNullable(this.rfidStatus);
  }

  public StatusDto withSystemStatus(SystemStatusDto systemStatus) {
    return new StatusDto(systemStatus, playerStatus, rfidStatus);
  }
  
  public StatusDto withPlayerStatus(PlayerStatusDto playerStatus) {
    return new StatusDto(systemStatus, playerStatus, rfidStatus);
  }
  
  public StatusDto withRfidStatus(RfidStatusDto rfidStatus) {
    return new StatusDto(systemStatus, playerStatus, rfidStatus);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StatusDto v = (StatusDto) other;
    return Objects.equals(systemStatus, v.systemStatus) && Objects.equals(playerStatus, v.playerStatus) && Objects.equals(rfidStatus, v.rfidStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(systemStatus, playerStatus, rfidStatus);
  }

  @Override
  public String toString() {
    return String.format("StatusDto[systemStatus=%s, playerStatus=%s, rfidStatus=%s]", 
      systemStatus, playerStatus, rfidStatus);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private SystemStatusDto systemStatus;
    private PlayerStatusDto playerStatus;
    private RfidStatusDto rfidStatus;
  
    public StatusDto build() {
      return new StatusDto(systemStatus, playerStatus, rfidStatus);
    }
  
    public Builder setSystemStatus(SystemStatusDto value) {
      this.systemStatus  = value;
      return this;
    }
    
    public Builder setPlayerStatus(PlayerStatusDto value) {
      this.playerStatus  = value;
      return this;
    }
    
    public Builder setRfidStatus(RfidStatusDto value) {
      this.rfidStatus  = value;
      return this;
    }
  }
}