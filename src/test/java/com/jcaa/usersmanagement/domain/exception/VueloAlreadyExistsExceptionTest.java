package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para VueloAlreadyExistsException.
 *
 * <p>La excepción no tiene lógica de dominio propia; su valor está en el mensaje que produce: el
 * número, la fecha de salida y el puesto del vuelo duplicado deben aparecer explícitamente para que
 * la capa superior pueda construir una respuesta de error útil.
 */
@DisplayName("VueloAlreadyExistsException")
class VueloAlreadyExistsExceptionTest {

  @Test
  @DisplayName("becauseFlightAlreadyExists() debe incluir número, fecha de salida y puesto")
  void shouldIncludeFlightDataInMessage() {
    // Arrange
    final String numero = "AV1234";
    final LocalDateTime fechaSalida = LocalDateTime.of(2026, 2, 15, 10, 30);
    final String puesto = "12A";

    // Act
    final String message =
        VueloAlreadyExistsException.becauseFlightAlreadyExists(numero, fechaSalida, puesto)
            .getMessage();

    // Assert
    assertTrue(message.contains(numero), "el mensaje debe identificar el número de vuelo");
    assertTrue(message.contains(fechaSalida.toString()), "el mensaje debe incluir la fecha de salida");
    assertTrue(message.contains(puesto), "el mensaje debe identificar el puesto duplicado");
  }
}
