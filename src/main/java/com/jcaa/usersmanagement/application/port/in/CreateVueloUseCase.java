package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface CreateVueloUseCase {
  VueloModel execute(@NotNull @Valid CreateVueloCommand command);
}
