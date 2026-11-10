package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidAeropuertoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AeropuertoTest {

  @Test
  @DisplayName("Should create Aeropuerto with trimmed value")
  void shouldCreateAeropuertoWithTrimmedValue() {
    assertEquals(
        "Aeropuerto Rafael Nunez", new Aeropuerto("  Aeropuerto Rafael Nunez  ").value());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r"})
  @DisplayName("Should throw InvalidAeropuertoException when value is empty")
  void shouldThrowWhenAeropuertoIsEmpty(final String input) {
    assertThrows(InvalidAeropuertoException.class, () -> new Aeropuerto(input));
  }

  @Test
  @DisplayName("Should throw InvalidAeropuertoException when value is too long")
  void shouldThrowWhenAeropuertoIsTooLong() {
    final String longName = "a".repeat(101);
    assertThrows(InvalidAeropuertoException.class, () -> new Aeropuerto(longName));
  }

  @Test
  @DisplayName("Should throw NullPointerException when Aeropuerto is null")
  void shouldThrowNullPointerExceptionWhenAeropuertoIsNull() {
    assertThrows(NullPointerException.class, () -> new Aeropuerto(null));
  }

  @Test
  @DisplayName("toString() should return the normalized value")
  void shouldReturnNormalizedValueOnToString() {
    assertEquals("Aeropuerto El Dorado", new Aeropuerto(" Aeropuerto El Dorado ").toString());
  }
}
