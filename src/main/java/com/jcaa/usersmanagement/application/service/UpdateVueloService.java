package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateVueloUseCase;
import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByUniqueKeyPort;
import com.jcaa.usersmanagement.application.port.out.UpdateVueloPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.mapper.VueloApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.VueloAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateVueloService implements UpdateVueloUseCase {

  private final UpdateVueloPort updateVueloPort;
  private final GetVueloByIdPort getVueloByIdPort;
  private final GetVueloByUniqueKeyPort getVueloByUniqueKeyPort;
  private final Validator validator;

  @Override
  public VueloModel execute(final UpdateVueloCommand command) {
    validateCommand(command);

    final VueloId vueloId = new VueloId(command.id());
    findExistingVueloOrFail(vueloId);

    final NumeroVuelo numero = new NumeroVuelo(command.numero());
    final PuestoVuelo puesto = new PuestoVuelo(command.puesto());
    ensureUniqueKeyIsNotTakenByAnotherVuelo(
        numero, command.fechaSalida(), puesto, vueloId);

    final VueloModel vueloToUpdate = VueloApplicationMapper.fromUpdateCommandToModel(command);
    return updateVueloPort.update(vueloToUpdate);
  }

  private void validateCommand(final UpdateVueloCommand command) {
    final Set<ConstraintViolation<UpdateVueloCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private VueloModel findExistingVueloOrFail(final VueloId vueloId) {
    return getVueloByIdPort
        .getById(vueloId)
        .orElseThrow(() -> VueloNotFoundException.becauseIdWasNotFound(vueloId.value()));
  }

  private void ensureUniqueKeyIsNotTakenByAnotherVuelo(
      final NumeroVuelo numero,
      final LocalDateTime fechaSalida,
      final PuestoVuelo puesto,
      final VueloId ownerId) {
    getVueloByUniqueKeyPort
        .getByUniqueKey(numero, fechaSalida, puesto)
        .ifPresent(
            found -> {
              if (!found.getId().equals(ownerId)) {
                throw VueloAlreadyExistsException.becauseFlightAlreadyExists(
                    numero.value(), fechaSalida, puesto.value());
              }
            });
  }
}
