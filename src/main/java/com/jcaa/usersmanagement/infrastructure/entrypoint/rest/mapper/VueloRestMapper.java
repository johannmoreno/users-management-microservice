package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.VueloRestResponse;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VueloRestMapper {

  public CreateVueloCommand toCreateCommand(final CreateVueloRestRequest request) {
    return new CreateVueloCommand(
        request.id(),
        request.fechaCompra(),
        request.fechaSalida(),
        request.fechaLlegada(),
        request.agenciaViajes(),
        request.aerolinea(),
        request.numero(),
        request.valor(),
        request.cliente(),
        request.puesto(),
        request.avion(),
        request.aeropuertoSalida(),
        request.aeropuertoLlegada(),
        request.piloto());
  }

  public UpdateVueloCommand toUpdateCommand(
      final String id, final UpdateVueloRestRequest request) {
    return new UpdateVueloCommand(
        id,
        request.fechaCompra(),
        request.fechaSalida(),
        request.fechaLlegada(),
        request.agenciaViajes(),
        request.aerolinea(),
        request.numero(),
        request.estado(),
        request.valor(),
        request.cliente(),
        request.puesto(),
        request.avion(),
        request.aeropuertoSalida(),
        request.aeropuertoLlegada(),
        request.piloto());
  }

  public GetVueloByIdQuery toGetByIdQuery(final String id) {
    return new GetVueloByIdQuery(id);
  }

  public DeleteVueloCommand toDeleteCommand(final String id) {
    return new DeleteVueloCommand(id);
  }

  public VueloRestResponse toResponse(final VueloModel vuelo) {
    return new VueloRestResponse(
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
        vuelo.getPiloto());
  }

  public List<VueloRestResponse> toResponseList(final List<VueloModel> vuelos) {
    return vuelos.stream().map(VueloRestMapper::toResponse).toList();
  }
}
