package com.jcaa.usersmanagement.application.service.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para VueloApplicationMapper.
 *
 * <p>Cubre cada método público de conversión entre los DTOs de aplicación y el modelo de dominio.
 */
@DisplayName("VueloApplicationMapper")
class VueloApplicationMapperTest {

  private static final String ID = "v-001";
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final String AGENCIA_VIAJES = "Agencia Viajes Caribe";
  private static final String AEROLINEA = "Avianca";
  private static final String NUMERO = "AV1234";
  private static final String ESTADO = "CONFIRMADO";
  private static final BigDecimal VALOR = new BigDecimal("450000.00");
  private static final String CLIENTE = "Carlos Perez";
  private static final String PUESTO = "12A";
  private static final String AVION = "Airbus A320";
  private static final String AEROPUERTO_SALIDA = "Aeropuerto Rafael Nunez";
  private static final String AEROPUERTO_LLEGADA = "Aeropuerto El Dorado";
  private static final String PILOTO = "Capitan Juan Gomez";

  // ── fromCreateCommandToModel()

  @Test
  @DisplayName("fromCreateCommandToModel() mapea todos los campos y fija estado PROGRAMADO")
  void shouldMapCreateCommandToModelWithProgramadoStatus() {
    // Arrange
    final CreateVueloCommand command = createCommand();

    // Act
    final VueloModel result = VueloApplicationMapper.fromCreateCommandToModel(command);

    // Assert
    assertAll(
        "fromCreateCommandToModel()",
        () -> assertEquals(ID, result.getId().value(), "id"),
        () -> assertEquals(FECHA_COMPRA, result.getFechaCompra(), "fechaCompra"),
        () -> assertEquals(FECHA_SALIDA, result.getFechaSalida(), "fechaSalida"),
        () -> assertEquals(FECHA_LLEGADA, result.getFechaLlegada(), "fechaLlegada"),
        () -> assertEquals(AGENCIA_VIAJES, result.getAgenciaViajes(), "agenciaViajes"),
        () -> assertEquals(AEROLINEA, result.getAerolinea(), "aerolinea"),
        () -> assertEquals(NUMERO, result.getNumero().value(), "numero"),
        () -> assertEquals(EstadoVuelo.PROGRAMADO, result.getEstado(), "estado"),
        () -> assertEquals(VALOR, result.getValor().value(), "valor"),
        () -> assertEquals(CLIENTE, result.getCliente(), "cliente"),
        () -> assertEquals(PUESTO, result.getPuesto().value(), "puesto"),
        () -> assertEquals(AVION, result.getAvion(), "avion"),
        () -> assertEquals(AEROPUERTO_SALIDA, result.getAeropuertoSalida().value(), "salida"),
        () -> assertEquals(AEROPUERTO_LLEGADA, result.getAeropuertoLlegada().value(), "llegada"),
        () -> assertEquals(PILOTO, result.getPiloto(), "piloto"));
  }

  // ── fromUpdateCommandToModel()

  @Test
  @DisplayName("fromUpdateCommandToModel() mapea todos los campos y usa el estado recibido")
  void shouldMapUpdateCommandToModelWithReceivedStatus() {
    // Arrange
    final UpdateVueloCommand command = updateCommand();

    // Act
    final VueloModel result = VueloApplicationMapper.fromUpdateCommandToModel(command);

    // Assert
    assertAll(
        "fromUpdateCommandToModel()",
        () -> assertEquals(ID, result.getId().value(), "id"),
        () -> assertEquals(EstadoVuelo.CONFIRMADO, result.getEstado(), "estado"),
        () -> assertEquals(NUMERO, result.getNumero().value(), "numero"),
        () -> assertEquals(PUESTO, result.getPuesto().value(), "puesto"),
        () -> assertEquals(VALOR, result.getValor().value(), "valor"));
  }

  // ── fromGetVueloByIdQueryToVueloId()

  @Test
  @DisplayName("fromGetVueloByIdQueryToVueloId() extrae el VueloId del query")
  void shouldExtractVueloIdFromQuery() {
    // Arrange
    final GetVueloByIdQuery query = new GetVueloByIdQuery(ID);

    // Act
    final VueloId result = VueloApplicationMapper.fromGetVueloByIdQueryToVueloId(query);

    // Assert
    assertEquals(ID, result.value(), "id debe coincidir con el del query");
  }

  // ── fromDeleteCommandToVueloId()

  @Test
  @DisplayName("fromDeleteCommandToVueloId() extrae el VueloId del command")
  void shouldExtractVueloIdFromDeleteCommand() {
    // Arrange
    final DeleteVueloCommand command = new DeleteVueloCommand(ID);

    // Act
    final VueloId result = VueloApplicationMapper.fromDeleteCommandToVueloId(command);

    // Assert
    assertEquals(ID, result.value(), "id debe coincidir con el del command");
  }

  // ── helpers

  private static CreateVueloCommand createCommand() {
    return new CreateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        NUMERO,
        VALOR,
        CLIENTE,
        PUESTO,
        AVION,
        AEROPUERTO_SALIDA,
        AEROPUERTO_LLEGADA,
        PILOTO);
  }

  private static UpdateVueloCommand updateCommand() {
    return new UpdateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        NUMERO,
        ESTADO,
        VALOR,
        CLIENTE,
        PUESTO,
        AVION,
        AEROPUERTO_SALIDA,
        AEROPUERTO_LLEGADA,
        PILOTO);
  }
}
