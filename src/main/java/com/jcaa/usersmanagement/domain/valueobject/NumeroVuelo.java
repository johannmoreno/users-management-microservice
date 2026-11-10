package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidNumeroVueloException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record NumeroVuelo(String value) {

  private static final Pattern NUMERO_PATTERN = Pattern.compile("^[A-Z0-9]{2,10}$");

  public NumeroVuelo {
    final String normalizedValue =
        Objects.requireNonNull(value, "NumeroVuelo cannot be null")
            .trim()
            .toUpperCase(Locale.ROOT);
    validateNotEmpty(normalizedValue);
    validateFormat(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidNumeroVueloException.becauseValueIsEmpty();
    }
  }

  private static void validateFormat(final String normalizedValue) {
    if (!NUMERO_PATTERN.matcher(normalizedValue).matches()) {
      throw InvalidNumeroVueloException.becauseFormatIsInvalid(normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
