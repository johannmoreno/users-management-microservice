package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidNumeroVueloException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NumeroVueloTest {

  @Test
  @DisplayName("Should create NumeroVuelo with a valid alphanumeric value")
  void shouldCreateNumeroVueloWithValidValue() {
    assertEquals("AV1234", new NumeroVuelo("AV1234").value());
  }

  @Test
  @DisplayName("Should normalize NumeroVuelo to uppercase and trim spaces")
  void shouldNormalizeNumeroVuelo() {
    assertEquals("AV1234", new NumeroVuelo("  av1234  ").value());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "A", "AV-123", "AV 123", "AV_123", "12345678901"})
  @DisplayName("Should throw InvalidNumeroVueloException for invalid values")
  void shouldThrowWhenNumeroVueloIsInvalid(final String input) {
    assertThrows(InvalidNumeroVueloException.class, () -> new NumeroVuelo(input));
  }

  @Test
  @DisplayName("Should throw NullPointerException when NumeroVuelo is null")
  void shouldThrowNullPointerExceptionWhenNumeroVueloIsNull() {
    assertThrows(NullPointerException.class, () -> new NumeroVuelo(null));
  }

  @Test
  @DisplayName("toString() should return the normalized value")
  void shouldReturnNormalizedValueOnToString() {
    assertEquals("AV1234", new NumeroVuelo(" av1234 ").toString());
  }
}
