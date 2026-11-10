package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidAeropuertoException;
import java.util.Objects;

public record Aeropuerto(String value) {

  private static final int MAXIMUM_LENGTH = 100;

  public Aeropuerto {
    final String normalizedValue =
        Objects.requireNonNull(value, "Aeropuerto cannot be null").trim();
    validateNotEmpty(normalizedValue);
    validateMaximumLength(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidAeropuertoException.becauseValueIsEmpty();
    }
  }

  private static void validateMaximumLength(final String normalizedValue) {
    if (normalizedValue.length() > MAXIMUM_LENGTH) {
      throw InvalidAeropuertoException.becauseLengthIsTooLong(MAXIMUM_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
