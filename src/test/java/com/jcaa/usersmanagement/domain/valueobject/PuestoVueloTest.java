package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidPuestoVueloException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PuestoVueloTest {

  @Test
  @DisplayName("Should create PuestoVuelo with a valid seat")
  void shouldCreatePuestoVueloWithValidSeat() {
    assertEquals("12A", new PuestoVuelo("12A").value());
    assertEquals("1B", new PuestoVuelo("1B").value());
  }

  @Test
  @DisplayName("Should normalize PuestoVuelo to uppercase and trim spaces")
  void shouldNormalizePuestoVuelo() {
    assertEquals("12A", new PuestoVuelo(" 12a ").value());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "A12", "123A", "12", "12AA", "12-A"})
  @DisplayName("Should throw InvalidPuestoVueloException for invalid seats")
  void shouldThrowWhenPuestoVueloIsInvalid(final String input) {
    assertThrows(InvalidPuestoVueloException.class, () -> new PuestoVuelo(input));
  }

  @Test
  @DisplayName("Should throw NullPointerException when PuestoVuelo is null")
  void shouldThrowNullPointerExceptionWhenPuestoVueloIsNull() {
    assertThrows(NullPointerException.class, () -> new PuestoVuelo(null));
  }

  @Test
  @DisplayName("toString() should return the normalized value")
  void shouldReturnNormalizedValueOnToString() {
    assertEquals("12A", new PuestoVuelo(" 12a ").toString());
  }
}
