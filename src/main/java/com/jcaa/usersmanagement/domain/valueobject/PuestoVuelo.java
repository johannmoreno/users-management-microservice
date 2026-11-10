package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidPuestoVueloException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record PuestoVuelo(String value) {

  private static final Pattern PUESTO_PATTERN = Pattern.compile("^[0-9]{1,2}[A-Z]$");

  public PuestoVuelo {
    final String normalizedValue =
        Objects.requireNonNull(value, "PuestoVuelo cannot be null")
            .trim()
            .toUpperCase(Locale.ROOT);
    validateNotEmpty(normalizedValue);
    validateFormat(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidPuestoVueloException.becauseValueIsEmpty();
    }
  }

  private static void validateFormat(final String normalizedValue) {
    if (!PUESTO_PATTERN.matcher(normalizedValue).matches()) {
      throw InvalidPuestoVueloException.becauseFormatIsInvalid(normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
