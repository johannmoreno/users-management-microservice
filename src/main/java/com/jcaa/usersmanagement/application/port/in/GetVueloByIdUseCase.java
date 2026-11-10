package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface GetVueloByIdUseCase {
  VueloModel execute(@NotNull @Valid GetVueloByIdQuery query);
}
