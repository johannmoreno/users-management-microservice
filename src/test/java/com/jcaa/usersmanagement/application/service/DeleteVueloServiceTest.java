package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.DeleteVueloPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para DeleteVueloService.
 *
 * <p>Cubre: flujo feliz (delete invocado), vuelo no encontrado y validación del command.
 */
@DisplayName("DeleteVueloService")
@ExtendWith(MockitoExtension.class)
class DeleteVueloServiceTest {

  @Mock private DeleteVueloPort deleteVueloPort;
  @Mock private GetVueloByIdPort getVueloByIdPort;

  private DeleteVueloService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new DeleteVueloService(
              deleteVueloPort, getVueloByIdPort, validatorFactory.getValidator());
    }
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() invoca deleteVueloPort cuando el vuelo existe")
  void shouldDeleteWhenVueloExists() {
    // Arrange
    final DeleteVueloCommand command = new DeleteVueloCommand("v-001");

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.of(validVuelo()));

    // Act
    service.execute(command);

    // Assert
    verify(deleteVueloPort).delete(new VueloId("v-001"));
  }

  // ── vuelo no encontrado

  @Test
  @DisplayName("execute() lanza VueloNotFoundException cuando el id no existe")
  void shouldThrowWhenVueloNotFound() {
    // Arrange
    final DeleteVueloCommand command = new DeleteVueloCommand("no-existe");

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(VueloNotFoundException.class, () -> service.execute(command));
    verify(deleteVueloPort, never()).delete(any());
  }

  // ── validación del command

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el id está en blanco")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange
    final DeleteVueloCommand command = new DeleteVueloCommand("  ");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(deleteVueloPort, getVueloByIdPort);
  }

  // ── helper

  private static VueloModel validVuelo() {
    return VueloModel.create(
        new VueloId("v-001"),
        LocalDateTime.of(2026, 1, 10, 8, 0),
        LocalDateTime.of(2026, 2, 15, 10, 30),
        LocalDateTime.of(2026, 2, 15, 13, 45),
        "Agencia Viajes Caribe",
        "Avianca",
        new NumeroVuelo("AV1234"),
        new ValorVuelo(new BigDecimal("450000.00")),
        "Carlos Perez",
        new PuestoVuelo("12A"),
        "Airbus A320",
        new Aeropuerto("Aeropuerto Rafael Nunez"),
        new Aeropuerto("Aeropuerto El Dorado"),
        "Capitan Juan Gomez");
  }
}
