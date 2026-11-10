package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.DeleteVueloUseCase;
import com.jcaa.usersmanagement.application.port.out.DeleteVueloPort;
import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.mapper.VueloApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteVueloService implements DeleteVueloUseCase {

  private final DeleteVueloPort deleteVueloPort;
  private final GetVueloByIdPort getVueloByIdPort;
  private final Validator validator;

  @Override
  public void execute(final DeleteVueloCommand command) {
    validateCommand(command);

    final VueloId vueloId = VueloApplicationMapper.fromDeleteCommandToVueloId(command);
    ensureVueloExists(vueloId);
    deleteVueloPort.delete(vueloId);
  }

  private void validateCommand(final DeleteVueloCommand command) {
    final Set<ConstraintViolation<DeleteVueloCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureVueloExists(final VueloId vueloId) {
    getVueloByIdPort
        .getById(vueloId)
        .orElseThrow(() -> VueloNotFoundException.becauseIdWasNotFound(vueloId.value()));
  }
}
