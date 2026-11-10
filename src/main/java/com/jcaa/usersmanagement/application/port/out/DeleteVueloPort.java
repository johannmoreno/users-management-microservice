package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.valueobject.VueloId;

public interface DeleteVueloPort {
  void delete(VueloId vueloId);
}
