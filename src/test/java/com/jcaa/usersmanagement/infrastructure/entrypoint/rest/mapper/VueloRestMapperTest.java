package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.VueloRestResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para VueloRestMapper.
 *
 * <p>Cubre las conversiones request → command/query y model → response, incluyendo el mapeo de
 * listas.
 */
@DisplayName("VueloRestMapper")
class VueloRestMapperTest {

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

  // ── toCreateCommand()

  @Test
  @DisplayName("toCreateCommand() mapea todos los campos del request")
  void shouldMapCreateRequestToCommand() {
    // Arrange
    final CreateVueloRestRequest request =
        new CreateVueloRestRequest(
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

    // Act
    final CreateVueloCommand result = VueloRestMapper.toCreateCommand(request);

    // Assert
    assertAll(
        "toCreateCommand()",
        () -> assertEquals(ID, result.id(), "id"),
        () -> assertEquals(FECHA_COMPRA, result.fechaCompra(), "fechaCompra"),
        () -> assertEquals(FECHA_SALIDA, result.fechaSalida(), "fechaSalida"),
        () -> assertEquals(FECHA_LLEGADA, result.fechaLlegada(), "fechaLlegada"),
        () -> assertEquals(AGENCIA_VIAJES, result.agenciaViajes(), "agenciaViajes"),
        () -> assertEquals(AEROLINEA, result.aerolinea(), "aerolinea"),
        () -> assertEquals(NUMERO, result.numero(), "numero"),
        () -> assertEquals(VALOR, result.valor(), "valor"),
        () -> assertEquals(CLIENTE, result.cliente(), "cliente"),
        () -> assertEquals(PUESTO, result.puesto(), "puesto"),
        () -> assertEquals(AVION, result.avion(), "avion"),
        () -> assertEquals(AEROPUERTO_SALIDA, result.aeropuertoSalida(), "aeropuertoSalida"),
        () -> assertEquals(AEROPUERTO_LLEGADA, result.aeropuertoLlegada(), "aeropuertoLlegada"),
        () -> assertEquals(PILOTO, result.piloto(), "piloto"));
  }

  // ── toUpdateCommand()

  @Test
  @DisplayName("toUpdateCommand() mapea todos los campos del request y usa el id de la ruta")
  void shouldMapUpdateRequestToCommand() {
    // Arrange
    final UpdateVueloRestRequest request =
        new UpdateVueloRestRequest(
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

    // Act
    final UpdateVueloCommand result = VueloRestMapper.toUpdateCommand(ID, request);

    // Assert
    assertAll(
        "toUpdateCommand()",
        () -> assertEquals(ID, result.id(), "id"),
        () -> assertEquals(FECHA_COMPRA, result.fechaCompra(), "fechaCompra"),
        () -> assertEquals(FECHA_SALIDA, result.fechaSalida(), "fechaSalida"),
        () -> assertEquals(FECHA_LLEGADA, result.fechaLlegada(), "fechaLlegada"),
        () -> assertEquals(AGENCIA_VIAJES, result.agenciaViajes(), "agenciaViajes"),
        () -> assertEquals(AEROLINEA, result.aerolinea(), "aerolinea"),
        () -> assertEquals(NUMERO, result.numero(), "numero"),
        () -> assertEquals(ESTADO, result.estado(), "estado"),
        () -> assertEquals(VALOR, result.valor(), "valor"),
        () -> assertEquals(CLIENTE, result.cliente(), "cliente"),
        () -> assertEquals(PUESTO, result.puesto(), "puesto"),
        () -> assertEquals(AVION, result.avion(), "avion"),
        () -> assertEquals(AEROPUERTO_SALIDA, result.aeropuertoSalida(), "aeropuertoSalida"),
        () -> assertEquals(AEROPUERTO_LLEGADA, result.aeropuertoLlegada(), "aeropuertoLlegada"),
        () -> assertEquals(PILOTO, result.piloto(), "piloto"));
  }

  // ── toGetByIdQuery()

  @Test
  @DisplayName("toGetByIdQuery() crea el query con el id recibido")
  void shouldMapIdToGetByIdQuery() {
    // Act
    final GetVueloByIdQuery result = VueloRestMapper.toGetByIdQuery(ID);

    // Assert
    assertEquals(ID, result.id(), "id debe coincidir con el recibido");
  }

  // ── toDeleteCommand()

  @Test
  @DisplayName("toDeleteCommand() crea el command con el id recibido")
  void shouldMapIdToDeleteCommand() {
    // Act
    final DeleteVueloCommand result = VueloRestMapper.toDeleteCommand(ID);

    // Assert
    assertEquals(ID, result.id(), "id debe coincidir con el recibido");
  }

  // ── toResponse()

  @Test
  @DisplayName("toResponse() mapea todos los campos del modelo de dominio")
  void shouldMapModelToResponse() {
    // Arrange
    final VueloModel vuelo = buildVuelo(ID, EstadoVuelo.CONFIRMADO);

    // Act
    final VueloRestResponse result = VueloRestMapper.toResponse(vuelo);

    // Assert
    assertAll(
        "toResponse()",
        () -> assertEquals(ID, result.id(), "id"),
        () -> assertEquals(FECHA_COMPRA, result.fechaCompra(), "fechaCompra"),
        () -> assertEquals(FECHA_SALIDA, result.fechaSalida(), "fechaSalida"),
        () -> assertEquals(FECHA_LLEGADA, result.fechaLlegada(), "fechaLlegada"),
        () -> assertEquals(AGENCIA_VIAJES, result.agenciaViajes(), "agenciaViajes"),
        () -> assertEquals(AEROLINEA, result.aerolinea(), "aerolinea"),
        () -> assertEquals(NUMERO, result.numero(), "numero"),
        () -> assertEquals(ESTADO, result.estado(), "estado"),
        () -> assertEquals(VALOR, result.valor(), "valor"),
        () -> assertEquals(CLIENTE, result.cliente(), "cliente"),
        () -> assertEquals(PUESTO, result.puesto(), "puesto"),
        () -> assertEquals(AVION, result.avion(), "avion"),
        () -> assertEquals(AEROPUERTO_SALIDA, result.aeropuertoSalida(), "aeropuertoSalida"),
        () -> assertEquals(AEROPUERTO_LLEGADA, result.aeropuertoLlegada(), "aeropuertoLlegada"),
        () -> assertEquals(PILOTO, result.piloto(), "piloto"));
  }

  // ── toResponseList()

  @Test
  @DisplayName("toResponseList() mapea cada modelo de la lista")
  void shouldMapModelListToResponseList() {
    // Arrange
    final List<VueloModel> vuelos =
        List.of(buildVuelo(ID, EstadoVuelo.PROGRAMADO), buildVuelo("v-002", EstadoVuelo.CANCELADO));

    // Act
    final List<VueloRestResponse> result = VueloRestMapper.toResponseList(vuelos);

    // Assert
    assertAll(
        "toResponseList()",
        () -> assertEquals(2, result.size(), "tamaño de la lista"),
        () -> assertEquals(ID, result.get(0).id(), "id del primer vuelo"),
        () -> assertEquals("PROGRAMADO", result.get(0).estado(), "estado del primer vuelo"),
        () -> assertEquals("v-002", result.get(1).id(), "id del segundo vuelo"),
        () -> assertEquals("CANCELADO", result.get(1).estado(), "estado del segundo vuelo"));
  }

  @Test
  @DisplayName("toResponseList() retorna lista vacía cuando no hay vuelos")
  void shouldReturnEmptyListWhenNoVuelos() {
    // Act
    final List<VueloRestResponse> result = VueloRestMapper.toResponseList(List.of());

    // Assert
    assertTrue(result.isEmpty(), "debe retornar lista vacía");
  }

  // ── helpers

  private VueloModel buildVuelo(final String id, final EstadoVuelo estado) {
    return new VueloModel(
        new VueloId(id),
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        new NumeroVuelo(NUMERO),
        estado,
        new ValorVuelo(VALOR),
        CLIENTE,
        new PuestoVuelo(PUESTO),
        AVION,
        new Aeropuerto(AEROPUERTO_SALIDA),
        new Aeropuerto(AEROPUERTO_LLEGADA),
        PILOTO);
  }
}
