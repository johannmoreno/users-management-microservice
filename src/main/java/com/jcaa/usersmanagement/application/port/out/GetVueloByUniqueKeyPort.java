package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GetVueloByUniqueKeyPort {
  Optional<VueloModel> getByUniqueKey(
      NumeroVuelo numero, LocalDateTime fechaSalida, PuestoVuelo puesto);
}
