package ch.jbert.models;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Error.Builder.class)
public final class Error {

    private final int code;
    private final String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Error error = (Error) o;
        return Objects.equals(code, error.code)
            && Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return String.format("Error[code=%s, message=%s]", code, message);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private int code;
        private String message;

        public Error build() {
            return new Error(code, message);
        }

        public Builder withCode(int code) {
            this.code = code;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }
    }
}
