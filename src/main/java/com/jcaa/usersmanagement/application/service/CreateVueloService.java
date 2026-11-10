package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.CreateVueloUseCase;
import com.jcaa.usersmanagement.application.port.out.GetVueloByUniqueKeyPort;
import com.jcaa.usersmanagement.application.port.out.SaveVueloPort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.mapper.VueloApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.VueloAlreadyExistsException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateVueloService implements CreateVueloUseCase {

  private final SaveVueloPort saveVueloPort;
  private final GetVueloByUniqueKeyPort getVueloByUniqueKeyPort;
  private final Validator validator;

  @Override
  public VueloModel execute(final CreateVueloCommand command) {
    validateCommand(command);

    final NumeroVuelo numero = new NumeroVuelo(command.numero());
    final PuestoVuelo puesto = new PuestoVuelo(command.puesto());
    ensureVueloIsNotDuplicated(numero, command.fechaSalida(), puesto);

    final VueloModel vueloToSave = VueloApplicationMapper.fromCreateCommandToModel(command);
    return saveVueloPort.save(vueloToSave);
  }

  private void validateCommand(final CreateVueloCommand command) {
    final Set<ConstraintViolation<CreateVueloCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureVueloIsNotDuplicated(
      final NumeroVuelo numero, final LocalDateTime fechaSalida, final PuestoVuelo puesto) {
    getVueloByUniqueKeyPort
        .getByUniqueKey(numero, fechaSalida, puesto)
        .ifPresent(
            ignored -> {
              throw VueloAlreadyExistsException.becauseFlightAlreadyExists(
                  numero.value(), fechaSalida, puesto.value());
            });
  }
}
