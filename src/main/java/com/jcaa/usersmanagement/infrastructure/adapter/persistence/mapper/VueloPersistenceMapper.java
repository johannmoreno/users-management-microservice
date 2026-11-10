package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.VueloPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.VueloEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VueloPersistenceMapper {

  public VueloPersistenceDto fromModelToDto(final VueloModel vuelo) {
    return new VueloPersistenceDto(
        vuelo.getId().value(),
        vuelo.getFechaCompra(),
        vuelo.getFechaSalida(),
        vuelo.getFechaLlegada(),
        vuelo.getAgenciaViajes(),
        vuelo.getAerolinea(),
        vuelo.getNumero().value(),
        vuelo.getEstado().name(),
        vuelo.getValor().value(),
        vuelo.getCliente(),
        vuelo.getPuesto().value(),
        vuelo.getAvion(),
        vuelo.getAeropuertoSalida().value(),
        vuelo.getAeropuertoLlegada().value(),
        vuelo.getPiloto(),
        null,
        null);
  }

  public VueloEntity fromResultSetToEntity(final ResultSet resultSet) throws SQLException {
    return new VueloEntity(
        resultSet.getString("id"),
        resultSet.getTimestamp("fecha_compra").toLocalDateTime(),
        resultSet.getTimestamp("fecha_salida").toLocalDateTime(),
        resultSet.getTimestamp("fecha_llegada").toLocalDateTime(),
        resultSet.getString("agencia_viajes"),
        resultSet.getString("aerolinea"),
        resultSet.getString("numero"),
        resultSet.getString("estado"),
        resultSet.getBigDecimal("valor"),
        resultSet.getString("cliente"),
        resultSet.getString("puesto"),
        resultSet.getString("avion"),
        resultSet.getString("aeropuerto_salida"),
        resultSet.getString("aeropuerto_llegada"),
        resultSet.getString("piloto"),
        resultSet.getString("created_at"),
        resultSet.getString("updated_at"));
  }

  public VueloModel fromEntityToModel(final VueloEntity entity) {
    return new VueloModel(
        new VueloId(entity.id()),
        entity.fechaCompra(),
        entity.fechaSalida(),
        entity.fechaLlegada(),
        entity.agenciaViajes(),
        entity.aerolinea(),
        new NumeroVuelo(entity.numero()),
        EstadoVuelo.fromString(entity.estado()),
        new ValorVuelo(entity.valor()),
        entity.cliente(),
        new PuestoVuelo(entity.puesto()),
        entity.avion(),
        new Aeropuerto(entity.aeropuertoSalida()),
        new Aeropuerto(entity.aeropuertoLlegada()),
        entity.piloto());
  }

  public VueloModel fromResultSetToModel(final ResultSet resultSet) throws SQLException {
    return fromEntityToModel(fromResultSetToEntity(resultSet));
  }

  public List<VueloModel> fromResultSetToModelList(final ResultSet resultSet) throws SQLException {
    final List<VueloModel> vuelos = new ArrayList<>();
    while (resultSet.next()) {
      vuelos.add(fromResultSetToModel(resultSet));
    }
    return vuelos;
  }
}
