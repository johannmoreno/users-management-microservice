package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para VueloRepositoryMySQL.
 *
 * <p>Cubre todos los métodos públicos con sus ramas: save() — flujo feliz, fallo del INSERT y
 * vuelo no encontrado después del insert; update() — flujo feliz y fallo del UPDATE; getById() —
 * encontrado, no encontrado y SQLException; getByUniqueKey() — encontrado, no encontrado y
 * SQLException; getAll() — flujo feliz y SQLException; delete() — flujo feliz y SQLException.
 */
@DisplayName("VueloRepositoryMySQL")
@ExtendWith(MockitoExtension.class)
class VueloRepositoryMySQLTest {

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

  @Mock private DataSource dataSource;
  @Mock private Connection connection;
  @Mock private PreparedStatement statement;
  @Mock private ResultSet resultSet;

  private VueloRepositoryMySQL repository;
  private VueloModel vueloModel;
  private VueloId vueloId;
  private NumeroVuelo numero;
  private PuestoVuelo puesto;

  @BeforeEach
  void setUp() {
    repository = new VueloRepositoryMySQL(dataSource);
    vueloId = new VueloId(ID);
    numero = new NumeroVuelo(NUMERO);
    puesto = new PuestoVuelo(PUESTO);
    vueloModel =
        VueloModel.create(
            vueloId,
            FECHA_COMPRA,
            FECHA_SALIDA,
            FECHA_LLEGADA,
            AGENCIA_VIAJES,
            AEROLINEA,
            numero,
            new ValorVuelo(VALOR),
            CLIENTE,
            puesto,
            AVION,
            new Aeropuerto(AEROPUERTO_SALIDA),
            new Aeropuerto(AEROPUERTO_LLEGADA),
            PILOTO);
  }

  // Helper: wire dataSource → connection → statement → resultSet
  private void configureStatementAndResultSet() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
  }

  // Helper: configure resultSet to return one full flight row
  private void configureResultSetRow() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
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

  // ── save() — flujo feliz

  @Test
  @DisplayName("save() ejecuta INSERT y retorna el vuelo persistido consultado por id")
  void shouldSaveVueloAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final VueloModel result = repository.save(vueloModel);

    // Assert
    assertAll(
        "save() flujo feliz",
        () -> assertEquals(ID, result.getId().value(), "id"),
        () -> assertEquals(NUMERO, result.getNumero().value(), "numero"),
        () -> assertEquals(PUESTO, result.getPuesto().value(), "puesto"),
        () -> assertEquals(EstadoVuelo.PROGRAMADO, result.getEstado()));
  }

  // ── save() — INSERT falla → PersistenceException

  @Test
  @DisplayName("save() lanza PersistenceException cuando el INSERT produce SQLException")
  void shouldThrowPersistenceExceptionWhenInsertFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.save(vueloModel),
        "debe lanzar PersistenceException cuando el INSERT falla");
  }

  // ── save() → findByIdOrFail — vuelo no encontrado → VueloNotFoundException

  @Test
  @DisplayName("save() lanza VueloNotFoundException cuando el vuelo guardado no se encuentra")
  void shouldThrowVueloNotFoundExceptionWhenVueloNotFoundAfterSave() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act + Assert
    assertThrows(
        VueloNotFoundException.class,
        () -> repository.save(vueloModel),
        "debe lanzar VueloNotFoundException cuando el SELECT no retorna filas tras el INSERT");
  }

  // ── update() — flujo feliz

  @Test
  @DisplayName("update() ejecuta UPDATE y retorna el vuelo refrescado consultado por id")
  void shouldUpdateVueloAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final VueloModel result = repository.update(vueloModel);

    // Assert
    assertEquals(ID, result.getId().value(), "el id debe coincidir con el vuelo actualizado");
  }

  // ── update() — UPDATE falla → PersistenceException

  @Test
  @DisplayName("update() lanza PersistenceException cuando el UPDATE produce SQLException")
  void shouldThrowPersistenceExceptionWhenUpdateFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Update failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.update(vueloModel),
        "debe lanzar PersistenceException cuando el UPDATE falla");
  }

  // ── getById() — fila encontrada → Optional.of(vuelo)

  @Test
  @DisplayName("getById() retorna Optional.of(vuelo) cuando existe una fila coincidente")
  void shouldReturnVueloWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<VueloModel> result = repository.getById(vueloId);

    // Assert
    assertAll(
        "getById() encontrado",
        () -> assertTrue(result.isPresent(), "debe estar presente"),
        () -> assertEquals(ID, result.map(v -> v.getId().value()).orElse(null), "id"));
  }

  // ── getById() — sin fila → Optional.empty()

  @Test
  @DisplayName("getById() retorna Optional.empty() cuando no existe fila coincidente")
  void shouldReturnEmptyWhenNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<VueloModel> result = repository.getById(vueloId);

    // Assert
    assertTrue(result.isEmpty(), "debe retornar Optional.empty() cuando no hay fila");
  }

  // ── getById() — SQLException en prepareStatement

  @Test
  @DisplayName("getById() lanza PersistenceException cuando prepareStatement falla")
  void shouldThrowPersistenceExceptionOnGetByIdFailure() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getById(vueloId),
        "debe lanzar PersistenceException cuando prepareStatement falla");
  }

  // ── getById() — SQLException en executeQuery

  @Test
  @DisplayName("getById() lanza PersistenceException cuando executeQuery falla")
  void shouldThrowPersistenceExceptionWhenGetByIdExecuteQueryFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenThrow(new SQLException("Execute query failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getById(vueloId),
        "debe lanzar PersistenceException cuando executeQuery falla");
  }

  // ── getByUniqueKey() — fila encontrada

  @Test
  @DisplayName("getByUniqueKey() retorna Optional.of(vuelo) cuando existe la clave única")
  void shouldReturnVueloByUniqueKeyWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<VueloModel> result =
        repository.getByUniqueKey(numero, FECHA_SALIDA, puesto);

    // Assert
    assertAll(
        "getByUniqueKey() encontrado",
        () -> assertTrue(result.isPresent(), "debe estar presente"),
        () -> assertEquals(NUMERO, result.map(v -> v.getNumero().value()).orElse(null), "numero"));
  }

  // ── getByUniqueKey() — sin fila

  @Test
  @DisplayName("getByUniqueKey() retorna Optional.empty() cuando no existe la clave única")
  void shouldReturnEmptyWhenUniqueKeyNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<VueloModel> result =
        repository.getByUniqueKey(numero, FECHA_SALIDA, puesto);

    // Assert
    assertTrue(result.isEmpty(), "debe retornar Optional.empty() cuando no hay fila");
  }

  // ── getByUniqueKey() — SQLException

  @Test
  @DisplayName("getByUniqueKey() lanza PersistenceException cuando la consulta falla")
  void shouldThrowPersistenceExceptionOnGetByUniqueKeyFailure() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getByUniqueKey(numero, FECHA_SALIDA, puesto),
        "debe lanzar PersistenceException cuando prepareStatement falla");
  }

  // ── getAll() — flujo feliz

  @Test
  @DisplayName("getAll() retorna un modelo por cada fila del result set")
  void shouldReturnAllVuelos() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true, false);
    configureResultSetRow();

    // Act
    final List<VueloModel> result = repository.getAll();

    // Assert
    assertAll(
        "getAll() flujo feliz",
        () -> assertEquals(1, result.size(), "tamaño de la lista"),
        () -> assertEquals(ID, result.get(0).getId().value(), "id del primer vuelo"));
  }

  // ── getAll() — SQLException

  @Test
  @DisplayName("getAll() lanza PersistenceException cuando la consulta falla")
  void shouldThrowPersistenceExceptionOnGetAllFailure() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getAll(),
        "debe lanzar PersistenceException cuando el SELECT falla");
  }

  // ── delete() — flujo feliz

  @Test
  @DisplayName("delete() ejecuta DELETE sin lanzar excepción")
  void shouldDeleteVueloWithoutThrowing() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);

    // Act + Assert
    assertDoesNotThrow(
        () -> repository.delete(vueloId),
        "delete() no debe lanzar excepción cuando el DELETE se ejecuta correctamente");
  }

  // ── delete() — SQLException

  @Test
  @DisplayName("delete() lanza PersistenceException cuando el DELETE falla")
  void shouldThrowPersistenceExceptionWhenDeleteFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.delete(vueloId),
        "debe lanzar PersistenceException cuando el DELETE falla");
  }
}
