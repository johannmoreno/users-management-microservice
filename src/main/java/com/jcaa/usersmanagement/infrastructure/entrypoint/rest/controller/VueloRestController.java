package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import com.jcaa.usersmanagement.application.port.in.CreateVueloUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteVueloUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllVuelosUseCase;
import com.jcaa.usersmanagement.application.port.in.GetVueloByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateVueloUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.VueloRestResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.mapper.VueloRestMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vuelos")
@RequiredArgsConstructor
public class VueloRestController implements VueloRestControllerDocs {

  private final CreateVueloUseCase createVueloUseCase;
  private final UpdateVueloUseCase updateVueloUseCase;
  private final DeleteVueloUseCase deleteVueloUseCase;
  private final GetVueloByIdUseCase getVueloByIdUseCase;
  private final GetAllVuelosUseCase getAllVuelosUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public VueloRestResponse create(@Valid @RequestBody final CreateVueloRestRequest request) {
    final CreateVueloCommand command = VueloRestMapper.toCreateCommand(request);
    final VueloModel vuelo = createVueloUseCase.execute(command);
    return VueloRestMapper.toResponse(vuelo);
  }

  @GetMapping
  public List<VueloRestResponse> getAll() {
    return VueloRestMapper.toResponseList(getAllVuelosUseCase.execute());
  }

  @GetMapping("/{id}")
  public VueloRestResponse getById(@PathVariable final String id) {
    final GetVueloByIdQuery query = VueloRestMapper.toGetByIdQuery(id);
    final VueloModel vuelo = getVueloByIdUseCase.execute(query);
    return VueloRestMapper.toResponse(vuelo);
  }

  @PutMapping("/{id}")
  public VueloRestResponse update(
      @PathVariable final String id,
      @Valid @RequestBody final UpdateVueloRestRequest request) {
    final UpdateVueloCommand command = VueloRestMapper.toUpdateCommand(id, request);
    final VueloModel vuelo = updateVueloUseCase.execute(command);
    return VueloRestMapper.toResponse(vuelo);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable final String id) {
    final DeleteVueloCommand command = VueloRestMapper.toDeleteCommand(id);
    deleteVueloUseCase.execute(command);
  }
}
