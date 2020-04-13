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
@JsonIgnoreProperties({  })
@JsonDeserialize(builder = ErrorDto.Builder.class)
public final class ErrorDto {
  private final int code;
  private final String message;

  public ErrorDto(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return this.code;
  }
  
  public String getMessage() {
    return this.message;
  }

  public ErrorDto withCode(int code) {
    return new ErrorDto(code, message);
  }
  
  public ErrorDto withMessage(String message) {
    return new ErrorDto(code, message);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ErrorDto v = (ErrorDto) other;
    return Objects.equals(code, v.code) && Objects.equals(message, v.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message);
  }

  @Override
  public String toString() {
    return String.format("ErrorDto[code=%s, message=%s]", 
      code, message);
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {
    private int code;
    private String message;
  
    public ErrorDto build() {
      return new ErrorDto(code, message);
    }
  
    public Builder setCode(int value) {
      this.code  = value;
      return this;
    }
    
    public Builder setMessage(String value) {
      this.message  = value;
      return this;
    }
  }
}