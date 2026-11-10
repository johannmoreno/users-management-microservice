package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByUniqueKeyPort;
import com.jcaa.usersmanagement.application.port.out.UpdateVueloPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.domain.exception.VueloAlreadyExistsException;
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
 * Tests para UpdateVueloService.
 *
 * <p>Cubre: flujo feliz, vuelo no encontrado, clave única tomada por otro vuelo, clave única del
 * mismo vuelo (no debe fallar) y validación del command.
 */
@DisplayName("UpdateVueloService")
@ExtendWith(MockitoExtension.class)
class UpdateVueloServiceTest {

  private static final String ID = "v-001";
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final BigDecimal VALOR = new BigDecimal("450000.00");

  @Mock private UpdateVueloPort updateVueloPort;
  @Mock private GetVueloByIdPort getVueloByIdPort;
  @Mock private GetVueloByUniqueKeyPort getVueloByUniqueKeyPort;

  private UpdateVueloService service;

  private VueloModel existingVuelo;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new UpdateVueloService(
              updateVueloPort,
              getVueloByIdPort,
              getVueloByUniqueKeyPort,
              validatorFactory.getValidator());
    }

    existingVuelo = validVuelo(ID, "Carlos Perez");
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() actualiza el vuelo cuando los datos son válidos")
  void shouldUpdateVueloWhenDataIsValid() {
    // Arrange
    final UpdateVueloCommand command = validCommand();

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.of(existingVuelo));
    when(getVueloByUniqueKeyPort.getByUniqueKey(any(), any(), any()))
        .thenReturn(Optional.of(existingVuelo));
    when(updateVueloPort.update(any())).thenReturn(existingVuelo);

    // Act
    final VueloModel result = service.execute(command);

    // Assert
    assertNotNull(result);
    verify(updateVueloPort).update(any(VueloModel.class));
  }

  // ── vuelo no encontrado

  @Test
  @DisplayName("execute() lanza VueloNotFoundException cuando el id no existe")
  void shouldThrowWhenVueloNotFound() {
    // Arrange
    final UpdateVueloCommand command = validCommand();

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(VueloNotFoundException.class, () -> service.execute(command));
    verify(updateVueloPort, never()).update(any());
  }

  // ── clave única tomada por otro vuelo

  @Test
  @DisplayName("execute() lanza VueloAlreadyExistsException cuando la clave única pertenece a otro vuelo")
  void shouldThrowWhenUniqueKeyBelongsToAnotherVuelo() {
    // Arrange
    final UpdateVueloCommand command = validCommand();
    final VueloModel otherVuelo = validVuelo("v-999", "Otro Cliente");

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.of(existingVuelo));
    when(getVueloByUniqueKeyPort.getByUniqueKey(any(), any(), any()))
        .thenReturn(Optional.of(otherVuelo));

    // Act & Assert
    assertThrows(VueloAlreadyExistsException.class, () -> service.execute(command));
    verify(updateVueloPort, never()).update(any());
  }

  // ── clave única del mismo vuelo: no debe lanzar excepción

  @Test
  @DisplayName("execute() permite mantener la misma clave única del propio vuelo")
  void shouldAllowKeepingOwnUniqueKey() {
    // Arrange
    final UpdateVueloCommand command = validCommand();

    when(getVueloByIdPort.getById(any())).thenReturn(Optional.of(existingVuelo));
    when(getVueloByUniqueKeyPort.getByUniqueKey(any(), any(), any()))
        .thenReturn(Optional.of(existingVuelo));
    when(updateVueloPort.update(any())).thenReturn(existingVuelo);

    // Act & Assert
    assertDoesNotThrow(() -> service.execute(command));
    verify(updateVueloPort).update(any());
  }

  // ── validación del command

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el command tiene campos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange — id en blanco, estado en blanco y valor no positivo
    final UpdateVueloCommand command =
        new UpdateVueloCommand(
            "",
            FECHA_COMPRA,
            FECHA_SALIDA,
            FECHA_LLEGADA,
            "Agencia Viajes Caribe",
            "Avianca",
            "AV1234",
            "",
            new BigDecimal("-1"),
            "Carlos Perez",
            "12A",
            "Airbus A320",
            "Aeropuerto Rafael Nunez",
            "Aeropuerto El Dorado",
            "Capitan Juan Gomez");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(updateVueloPort, getVueloByIdPort, getVueloByUniqueKeyPort);
  }

  // ── helpers

  private static UpdateVueloCommand validCommand() {
    return new UpdateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        "Agencia Viajes Caribe",
        "Avianca",
        "AV1234",
        "CONFIRMADO",
        VALOR,
        "Carlos Perez",
        "12A",
        "Airbus A320",
        "Aeropuerto Rafael Nunez",
        "Aeropuerto El Dorado",
        "Capitan Juan Gomez");
  }

  private static VueloModel validVuelo(final String id, final String cliente) {
    return VueloModel.create(
        new VueloId(id),
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        "Agencia Viajes Caribe",
        "Avianca",
        new NumeroVuelo("AV1234"),
        new ValorVuelo(VALOR),
        cliente,
        new PuestoVuelo("12A"),
        "Airbus A320",
        new Aeropuerto("Aeropuerto Rafael Nunez"),
        new Aeropuerto("Aeropuerto El Dorado"),
        "Capitan Juan Gomez");
  }
}
