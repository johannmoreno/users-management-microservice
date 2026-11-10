package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para VueloNotFoundException.
 *
 * <p>La excepción no tiene lógica de dominio propia; su valor está en el mensaje que produce: el id
 * del vuelo no encontrado debe aparecer explícitamente para que la capa superior pueda construir
 * una respuesta de error útil.
 */
@DisplayName("VueloNotFoundException")
class VueloNotFoundExceptionTest {

  @Test
  @DisplayName("becauseIdWasNotFound() debe incluir el id del vuelo en el mensaje de error")
  void shouldIncludeVueloIdInMessage() {
    // Arrange
    final String vueloId = "v-404";

    // Act
    final String message = VueloNotFoundException.becauseIdWasNotFound(vueloId).getMessage();

    // Assert
    assertTrue(
        message.contains(vueloId), "el mensaje debe identificar el id del vuelo no encontrado");
  }
}
