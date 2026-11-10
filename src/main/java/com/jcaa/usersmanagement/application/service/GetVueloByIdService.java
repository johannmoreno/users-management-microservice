package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetVueloByIdUseCase;
import com.jcaa.usersmanagement.application.port.out.GetVueloByIdPort;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.application.service.mapper.VueloApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.VueloNotFoundException;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetVueloByIdService implements GetVueloByIdUseCase {

  private final GetVueloByIdPort getVueloByIdPort;
  private final Validator validator;

  @Override
  public VueloModel execute(final GetVueloByIdQuery query) {
    validateQuery(query);

    final VueloId vueloId = VueloApplicationMapper.fromGetVueloByIdQueryToVueloId(query);
    return getVueloByIdPort
        .getById(vueloId)
        .orElseThrow(() -> VueloNotFoundException.becauseIdWasNotFound(vueloId.value()));
  }

  private void validateQuery(final GetVueloByIdQuery query) {
    final Set<ConstraintViolation<GetVueloByIdQuery>> violations = validator.validate(query);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
