package com.jcaa.usersmanagement.application.service.mapper;

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
import lombok.experimental.UtilityClass;

@UtilityClass
public class VueloApplicationMapper {

  public VueloModel fromCreateCommandToModel(final CreateVueloCommand command) {
    return VueloModel.create(
        new VueloId(command.id()),
        command.fechaCompra(),
        command.fechaSalida(),
        command.fechaLlegada(),
        command.agenciaViajes(),
        command.aerolinea(),
        new NumeroVuelo(command.numero()),
        new ValorVuelo(command.valor()),
        command.cliente(),
        new PuestoVuelo(command.puesto()),
        command.avion(),
        new Aeropuerto(command.aeropuertoSalida()),
        new Aeropuerto(command.aeropuertoLlegada()),
        command.piloto());
  }

  public VueloModel fromUpdateCommandToModel(final UpdateVueloCommand command) {
    return new VueloModel(
        new VueloId(command.id()),
        command.fechaCompra(),
        command.fechaSalida(),
        command.fechaLlegada(),
        command.agenciaViajes(),
        command.aerolinea(),
        new NumeroVuelo(command.numero()),
        EstadoVuelo.fromString(command.estado()),
        new ValorVuelo(command.valor()),
        command.cliente(),
        new PuestoVuelo(command.puesto()),
        command.avion(),
        new Aeropuerto(command.aeropuertoSalida()),
        new Aeropuerto(command.aeropuertoLlegada()),
        command.piloto());
  }

  public VueloId fromGetVueloByIdQueryToVueloId(final GetVueloByIdQuery query) {
    return new VueloId(query.id());
  }

  public VueloId fromDeleteCommandToVueloId(final DeleteVueloCommand command) {
    return new VueloId(command.id());
  }
}
