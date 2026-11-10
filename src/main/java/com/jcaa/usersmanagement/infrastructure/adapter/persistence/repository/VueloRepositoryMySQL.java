package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.DeleteVueloPort;
import com.jcaa.usersmanagement.application.port.out.GetAllVuelosPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByUniqueKeyPort;
import com.jcaa.usersmanagement.application.port.out.SaveVueloPort;
import com.jcaa.usersmanagement.application.port.out.UpdateVueloPort;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.VueloPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper.VueloPersistenceMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VueloRepositoryMySQL
    implements SaveVueloPort,
        UpdateVueloPort,
        GetVueloByIdPort,
        GetVueloByUniqueKeyPort,
        GetAllVuelosPort,
        DeleteVueloPort {

  private static final String COLUMNS =
      "id, fecha_compra, fecha_salida, fecha_llegada, agencia_viajes, aerolinea, numero, estado, "
      + "valor, cliente, puesto, avion, aeropuerto_salida, aeropuerto_llegada, piloto, "
      + "created_at, updated_at";

  private static final String SQL_INSERT =
      "INSERT INTO vuelos "
      + "(id, fecha_compra, fecha_salida, fecha_llegada, agencia_viajes, aerolinea, numero, estado, "
      + "valor, cliente, puesto, avion, aeropuerto_salida, aeropuerto_llegada, piloto, "
      + "created_at, updated_at) "
      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

  private static final String SQL_UPDATE =
      "UPDATE vuelos SET fecha_compra = ?, fecha_salida = ?, fecha_llegada = ?, "
      + "agencia_viajes = ?, aerolinea = ?, numero = ?, estado = ?, valor = ?, cliente = ?, "
      + "puesto = ?, avion = ?, aeropuerto_salida = ?, aeropuerto_llegada = ?, piloto = ?, "
      + "updated_at = NOW() "
      + "WHERE id = ?";

  private static final String SQL_SELECT_BY_ID =
      "SELECT " + COLUMNS + " FROM vuelos WHERE id = ? LIMIT 1";

  private static final String SQL_SELECT_BY_UNIQUE_KEY =
      "SELECT " + COLUMNS + " FROM vuelos "
      + "WHERE numero = ? AND fecha_salida = ? AND puesto = ? LIMIT 1";

  private static final String SQL_SELECT_ALL =
      "SELECT " + COLUMNS + " FROM vuelos ORDER BY fecha_salida ASC";

  private static final String SQL_DELETE = "DELETE FROM vuelos WHERE id = ?";

  private final DataSource dataSource;

  @Override
  public VueloModel save(final VueloModel vuelo) {
    final VueloPersistenceDto dto = VueloPersistenceMapper.fromModelToDto(vuelo);
    executeSave(dto);
    return findByIdOrFail(vuelo.getId());
  }

  @Override
  public VueloModel update(final VueloModel vuelo) {
    final VueloPersistenceDto dto = VueloPersistenceMapper.fromModelToDto(vuelo);
    executeUpdate(dto);
    return findByIdOrFail(vuelo.getId());
  }

  @Override
  public Optional<VueloModel> getById(final VueloId vueloId) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
      statement.setString(1, vueloId.value());
      final ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      return Optional.of(VueloPersistenceMapper.fromResultSetToModel(resultSet));
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloFindByIdFailed(vueloId.value(), exception);
    }
  }

  @Override
  public Optional<VueloModel> getByUniqueKey(
      final NumeroVuelo numero, final LocalDateTime fechaSalida, final PuestoVuelo puesto) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement =
             connection.prepareStatement(SQL_SELECT_BY_UNIQUE_KEY)) {
      statement.setString(1, numero.value());
      statement.setTimestamp(2, Timestamp.valueOf(fechaSalida));
      statement.setString(3, puesto.value());
      final ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      return Optional.of(VueloPersistenceMapper.fromResultSetToModel(resultSet));
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloFindByUniqueKeyFailed(numero.value(), exception);
    }
  }

  @Override
  public List<VueloModel> getAll() {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL)) {
      final ResultSet resultSet = statement.executeQuery();
      return VueloPersistenceMapper.fromResultSetToModelList(resultSet);
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloFindAllFailed(exception);
    }
  }

  @Override
  public void delete(final VueloId vueloId) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
      statement.setString(1, vueloId.value());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloDeleteFailed(vueloId.value(), exception);
    }
  }

  private void executeSave(final VueloPersistenceDto dto) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
      bindInsertParameters(statement, dto);
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloSaveFailed(dto.id(), exception);
    }
  }

  private void executeUpdate(final VueloPersistenceDto dto) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
      bindUpdateParameters(statement, dto);
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseVueloUpdateFailed(dto.id(), exception);
    }
  }

  private void bindInsertParameters(
      final PreparedStatement statement, final VueloPersistenceDto dto) throws SQLException {
    statement.setString(1, dto.id());
    statement.setTimestamp(2, Timestamp.valueOf(dto.fechaCompra()));
    statement.setTimestamp(3, Timestamp.valueOf(dto.fechaSalida()));
    statement.setTimestamp(4, Timestamp.valueOf(dto.fechaLlegada()));
    statement.setString(5, dto.agenciaViajes());
    statement.setString(6, dto.aerolinea());
    statement.setString(7, dto.numero());
    statement.setString(8, dto.estado());
    statement.setBigDecimal(9, dto.valor());
    statement.setString(10, dto.cliente());
    statement.setString(11, dto.puesto());
    statement.setString(12, dto.avion());
    statement.setString(13, dto.aeropuertoSalida());
    statement.setString(14, dto.aeropuertoLlegada());
    statement.setString(15, dto.piloto());
  }

  private void bindUpdateParameters(
      final PreparedStatement statement, final VueloPersistenceDto dto) throws SQLException {
    statement.setTimestamp(1, Timestamp.valueOf(dto.fechaCompra()));
    statement.setTimestamp(2, Timestamp.valueOf(dto.fechaSalida()));
    statement.setTimestamp(3, Timestamp.valueOf(dto.fechaLlegada()));
    statement.setString(4, dto.agenciaViajes());
    statement.setString(5, dto.aerolinea());
    statement.setString(6, dto.numero());
    statement.setString(7, dto.estado());
    statement.setBigDecimal(8, dto.valor());
    statement.setString(9, dto.cliente());
    statement.setString(10, dto.puesto());
    statement.setString(11, dto.avion());
    statement.setString(12, dto.aeropuertoSalida());
    statement.setString(13, dto.aeropuertoLlegada());
    statement.setString(14, dto.piloto());
    statement.setString(15, dto.id());
  }

  private VueloModel findByIdOrFail(final VueloId vueloId) {
    return getById(vueloId)
        .orElseThrow(() -> VueloNotFoundException.becauseIdWasNotFound(vueloId.value()));
  }
}
