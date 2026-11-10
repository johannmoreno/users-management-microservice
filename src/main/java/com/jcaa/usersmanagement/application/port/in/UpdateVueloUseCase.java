package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface UpdateVueloUseCase {
  VueloModel execute(@NotNull @Valid UpdateVueloCommand command);
}
