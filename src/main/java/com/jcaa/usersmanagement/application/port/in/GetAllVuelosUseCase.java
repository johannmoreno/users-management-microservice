package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.domain.model.VueloModel;
import java.util.List;

public interface GetAllVuelosUseCase {
  List<VueloModel> execute();
}
