package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.VueloPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.VueloEntity;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para VueloPersistenceMapper.
 *
 * <p>Cubre: fromModelToDto(), fromEntityToModel(), fromResultSetToEntity() (happy path y
 * propagación de SQLException) y fromResultSetToModelList() — lista vacía, múltiples filas y
 * propagación de SQLException durante la iteración.
 */
@DisplayName("VueloPersistenceMapper")
@ExtendWith(MockitoExtension.class)
class VueloPersistenceMapperTest {

  private static final String ID = "v-001";
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final String AGENCIA_VIAJES = "Agencia Viajes Caribe";
  private static final String AEROLINEA = "Avianca";
  private static final String NUMERO = "AV1234";
  private static final String ESTADO = "PROGRAMADO";
  private static final BigDecimal VALOR = new BigDecimal("450000.00");
  private static final String CLIENTE = "Carlos Perez";
  private static final String PUESTO = "12A";
  private static final String AVION = "Airbus A320";
  private static final String AEROPUERTO_SALIDA = "Aeropuerto Rafael Nunez";
  private static final String AEROPUERTO_LLEGADA = "Aeropuerto El Dorado";
  private static final String PILOTO = "Capitan Juan Gomez";
  private static final String CREATED_AT = "2026-01-10 08:00:00";
  private static final String UPDATED_AT = "2026-01-10 08:00:00";

  @Mock private ResultSet resultSet;

  private VueloModel vueloModel;
  private VueloEntity vueloEntity;

  @BeforeEach
  void setUp() {
    vueloModel =
        VueloModel.create(
            new VueloId(ID),
            FECHA_COMPRA,
            FECHA_SALIDA,
            FECHA_LLEGADA,
            AGENCIA_VIAJES,
            AEROLINEA,
            new NumeroVuelo(NUMERO),
            new ValorVuelo(VALOR),
            CLIENTE,
            new PuestoVuelo(PUESTO),
            AVION,
            new Aeropuerto(AEROPUERTO_SALIDA),
            new Aeropuerto(AEROPUERTO_LLEGADA),
            PILOTO);

    vueloEntity =
        new VueloEntity(
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
            PILOTO,
            CREATED_AT,
            UPDATED_AT);
  }

  // ── fromModelToDto()

  @Test
  @DisplayName("fromModelToDto() mapea todos los campos y deja los timestamps en null")
  void shouldMapModelToDto() {
    // Act
    final VueloPersistenceDto result = VueloPersistenceMapper.fromModelToDto(vueloModel);

    // Assert
    assertAll(
        "fromModelToDto()",
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
        () -> assertEquals(PILOTO, result.piloto(), "piloto"),
        () -> assertNull(result.createdAt(), "createdAt debe ser null"),
        () -> assertNull(result.updatedAt(), "updatedAt debe ser null"));
  }

  // ── fromEntityToModel()

  @Test
  @DisplayName("fromEntityToModel() mapea todos los campos de VueloEntity al modelo de dominio")
  void shouldMapEntityToModel() {
    // Act
    final VueloModel result = VueloPersistenceMapper.fromEntityToModel(vueloEntity);

    // Assert
    assertAll(
        "fromEntityToModel()",
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

  // ── fromResultSetToEntity() — happy path

  @Test
  @DisplayName("fromResultSetToEntity() lee todas las columnas del ResultSet")
  void shouldReadAllColumnsFromResultSet() throws SQLException {
    // Arrange
    configureResultSetRow();

    // Act
    final VueloEntity result = VueloPersistenceMapper.fromResultSetToEntity(resultSet);

    // Assert
    assertAll(
        "fromResultSetToEntity()",
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
        () -> assertEquals(AEROPUERTO_SALIDA, result.aeropuertoSalida(), "salida"),
        () -> assertEquals(AEROPUERTO_LLEGADA, result.aeropuertoLlegada(), "llegada"),
        () -> assertEquals(PILOTO, result.piloto(), "piloto"),
        () -> assertEquals(CREATED_AT, result.createdAt(), "createdAt"),
        () -> assertEquals(UPDATED_AT, result.updatedAt(), "updatedAt"));
  }

  // ── fromResultSetToEntity() — SQLException propagation

  @Test
  @DisplayName("fromResultSetToEntity() propaga SQLException cuando falla la lectura")
  void shouldPropagateExceptionFromResultSet() throws SQLException {
    // Arrange
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Column read failed"));

    // Act + Assert
    assertThrows(
        SQLException.class,
        () -> VueloPersistenceMapper.fromResultSetToEntity(resultSet),
        "debe propagar SQLException cuando getString falla");
  }

  // ── fromResultSetToModelList() — empty

  @Test
  @DisplayName("fromResultSetToModelList() retorna lista vacía cuando no hay filas")
  void shouldReturnEmptyListWhenResultSetIsEmpty() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(false);

    // Act
    final List<VueloModel> result = VueloPersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertTrue(result.isEmpty(), "debe retornar lista vacía cuando no hay filas");
  }

  // ── fromResultSetToModelList() — multiple rows

  @Test
  @DisplayName("fromResultSetToModelList() retorna un modelo por cada fila del ResultSet")
  void shouldReturnOneModelPerRow() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true, true, false);
    configureResultSetRowWithSecondId("v-002");

    // Act
    final List<VueloModel> result = VueloPersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertAll(
        "fromResultSetToModelList()",
        () -> assertEquals(2, result.size(), "debe retornar un modelo por fila"),
        () -> assertEquals(ID, result.get(0).getId().value(), "primer id"),
        () -> assertEquals("v-002", result.get(1).getId().value(), "segundo id"));
  }

  // ── fromResultSetToModelList() — SQLException propagation during iteration

  @Test
  @DisplayName("fromResultSetToModelList() propaga SQLException cuando falla una fila")
  void shouldPropagateExceptionDuringIteration() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Row read failed"));

    // Act + Assert
    assertThrows(
        SQLException.class,
        () -> VueloPersistenceMapper.fromResultSetToModelList(resultSet),
        "debe propagar SQLException cuando una fila falla");
  }

  // ── helpers

  private void configureResultSetRow() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
    configureSharedColumns();
  }

  private void configureResultSetRowWithSecondId(final String secondId) throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID, secondId);
    when(resultSet.getTimestamp("fecha_compra"))
        .thenReturn(Timestamp.valueOf(FECHA_COMPRA), Timestamp.valueOf(FECHA_COMPRA));
    when(resultSet.getTimestamp("fecha_salida"))
        .thenReturn(Timestamp.valueOf(FECHA_SALIDA), Timestamp.valueOf(FECHA_SALIDA));
    when(resultSet.getTimestamp("fecha_llegada"))
        .thenReturn(Timestamp.valueOf(FECHA_LLEGADA), Timestamp.valueOf(FECHA_LLEGADA));
    when(resultSet.getString("agencia_viajes")).thenReturn(AGENCIA_VIAJES, AGENCIA_VIAJES);
    when(resultSet.getString("aerolinea")).thenReturn(AEROLINEA, AEROLINEA);
    when(resultSet.getString("numero")).thenReturn(NUMERO, NUMERO);
    when(resultSet.getString("estado")).thenReturn(ESTADO, ESTADO);
    when(resultSet.getBigDecimal("valor")).thenReturn(VALOR, VALOR);
    when(resultSet.getString("cliente")).thenReturn(CLIENTE, CLIENTE);
    when(resultSet.getString("puesto")).thenReturn(PUESTO, PUESTO);
    when(resultSet.getString("avion")).thenReturn(AVION, AVION);
    when(resultSet.getString("aeropuerto_salida"))
        .thenReturn(AEROPUERTO_SALIDA, AEROPUERTO_SALIDA);
    when(resultSet.getString("aeropuerto_llegada"))
        .thenReturn(AEROPUERTO_LLEGADA, AEROPUERTO_LLEGADA);
    when(resultSet.getString("piloto")).thenReturn(PILOTO, PILOTO);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT, CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT, UPDATED_AT);
  }

  private void configureSharedColumns() throws SQLException {
    when(resultSet.getTimestamp("fecha_compra")).thenReturn(Timestamp.valueOf(FECHA_COMPRA));
    when(resultSet.getTimestamp("fecha_salida")).thenReturn(Timestamp.valueOf(FECHA_SALIDA));
    when(resultSet.getTimestamp("fecha_llegada")).thenReturn(Timestamp.valueOf(FECHA_LLEGADA));
    when(resultSet.getString("agencia_viajes")).thenReturn(AGENCIA_VIAJES);
    when(resultSet.getString("aerolinea")).thenReturn(AEROLINEA);
    when(resultSet.getString("numero")).thenReturn(NUMERO);
    when(resultSet.getString("estado")).thenReturn(ESTADO);
    when(resultSet.getBigDecimal("valor")).thenReturn(VALOR);
    when(resultSet.getString("cliente")).thenReturn(CLIENTE);
    when(resultSet.getString("puesto")).thenReturn(PUESTO);
    when(resultSet.getString("avion")).thenReturn(AVION);
    when(resultSet.getString("aeropuerto_salida")).thenReturn(AEROPUERTO_SALIDA);
    when(resultSet.getString("aeropuerto_llegada")).thenReturn(AEROPUERTO_LLEGADA);
    when(resultSet.getString("piloto")).thenReturn(PILOTO);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }
}
