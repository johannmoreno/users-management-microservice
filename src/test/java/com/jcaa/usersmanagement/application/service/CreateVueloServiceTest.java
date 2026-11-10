package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetVueloByUniqueKeyPort;
import com.jcaa.usersmanagement.application.port.out.SaveVueloPort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.exception.VueloAlreadyExistsException;
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
 * Tests para CreateVueloService.
 *
 * <p>Cubre: validación del command, vuelo duplicado y flujo feliz (save).
 */
@DisplayName("CreateVueloService")
@ExtendWith(MockitoExtension.class)
class CreateVueloServiceTest {

  private static final String ID = "v-001";
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final BigDecimal VALOR = new BigDecimal("450000.00");

  @Mock private SaveVueloPort saveVueloPort;
  @Mock private GetVueloByUniqueKeyPort getVueloByUniqueKeyPort;

  private CreateVueloService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new CreateVueloService(
              saveVueloPort, getVueloByUniqueKeyPort, validatorFactory.getValidator());
    }
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() guarda el vuelo cuando no existe otro con la misma clave única")
  void shouldSaveVueloWhenUniqueKeyIsFree() {
    // Arrange
    final CreateVueloCommand command = validCommand();
    final VueloModel savedVuelo = validVuelo();

    when(getVueloByUniqueKeyPort.getByUniqueKey(any(), any(), any()))
        .thenReturn(Optional.empty());
    when(saveVueloPort.save(any())).thenReturn(savedVuelo);

    // Act
    final VueloModel result = service.execute(command);

    // Assert
    assertAll(
        "flujo feliz de CreateVueloService",
        () -> assertNotNull(result, "resultado no debe ser null"),
        () -> assertEquals(ID, result.getId().value(), "id del vuelo guardado"),
        () -> assertEquals(EstadoVuelo.PROGRAMADO, result.getEstado()));

    verify(saveVueloPort).save(any(VueloModel.class));
  }

  // ── vuelo duplicado

  @Test
  @DisplayName("execute() lanza VueloAlreadyExistsException cuando la clave única ya existe")
  void shouldThrowWhenVueloAlreadyExists() {
    // Arrange
    final CreateVueloCommand command = validCommand();
    final VueloModel existing = validVuelo();

    when(getVueloByUniqueKeyPort.getByUniqueKey(any(), any(), any()))
        .thenReturn(Optional.of(existing));

    // Act & Assert
    assertThrows(VueloAlreadyExistsException.class, () -> service.execute(command));
    verify(saveVueloPort, never()).save(any());
  }

  // ── validación del command

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el command tiene campos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange — id en blanco, fechas nulas y valor no positivo
    final CreateVueloCommand command =
        new CreateVueloCommand(
            "",
            null,
            null,
            null,
            "Agencia",
            "Avianca",
            "AV1234",
            new BigDecimal("-1"),
            "Carlos",
            "12A",
            "Airbus A320",
            "Rafael Nunez",
            "El Dorado",
            "Piloto");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(saveVueloPort, getVueloByUniqueKeyPort);
  }

  // ── helpers

  private static CreateVueloCommand validCommand() {
    return new CreateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        "Agencia Viajes Caribe",
        "Avianca",
        "AV1234",
        VALOR,
        "Carlos Perez",
        "12A",
        "Airbus A320",
        "Aeropuerto Rafael Nunez",
        "Aeropuerto El Dorado",
        "Capitan Juan Gomez");
  }

  private static VueloModel validVuelo() {
    return VueloModel.create(
        new VueloId(ID),
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        "Agencia Viajes Caribe",
        "Avianca",
        new NumeroVuelo("AV1234"),
        new ValorVuelo(VALOR),
        "Carlos Perez",
        new PuestoVuelo("12A"),
        "Airbus A320",
        new Aeropuerto("Aeropuerto Rafael Nunez"),
        new Aeropuerto("Aeropuerto El Dorado"),
        "Capitan Juan Gomez");
  }
}
