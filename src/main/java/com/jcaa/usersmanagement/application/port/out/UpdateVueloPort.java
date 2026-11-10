package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.VueloModel;

public interface UpdateVueloPort {
  VueloModel update(VueloModel vuelo);
}
