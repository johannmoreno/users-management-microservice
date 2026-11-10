package com.jcaa.usersmanagement.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidEstadoVueloException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class EstadoVueloTest {

  @Test
  @DisplayName("Should return the correct EstadoVuelo for valid input")
  void shouldReturnCorrectEstadoVueloForValidInput() {
    assertEquals(EstadoVuelo.PROGRAMADO, EstadoVuelo.fromString("PROGRAMADO"));
    assertEquals(EstadoVuelo.CONFIRMADO, EstadoVuelo.fromString("CONFIRMADO"));
    assertEquals(EstadoVuelo.EN_VUELO, EstadoVuelo.fromString("EN_VUELO"));
    assertEquals(EstadoVuelo.ATERRIZADO, EstadoVuelo.fromString("ATERRIZADO"));
    assertEquals(EstadoVuelo.CANCELADO, EstadoVuelo.fromString("CANCELADO"));
    assertEquals(EstadoVuelo.RETRASADO, EstadoVuelo.fromString("RETRASADO"));
  }

  @Test
  @DisplayName("Should parse EstadoVuelo ignoring case")
  void shouldParseEstadoVueloIgnoringCase() {
    assertEquals(EstadoVuelo.CONFIRMADO, EstadoVuelo.fromString("confirmado"));
    assertEquals(EstadoVuelo.EN_VUELO, EstadoVuelo.fromString("En_Vuelo"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "INVALID", "EN VUELO"})
  @DisplayName("Should throw InvalidEstadoVueloException for invalid input")
  void shouldThrowWhenInputIsInvalid(final String input) {
    assertThrows(InvalidEstadoVueloException.class, () -> EstadoVuelo.fromString(input));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw InvalidEstadoVueloException when input is null")
  void shouldThrowWhenInputIsNull(final String input) {
    assertThrows(InvalidEstadoVueloException.class, () -> EstadoVuelo.fromString(input));
  }
}
