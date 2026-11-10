package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.VueloModel;
import java.util.List;

public interface GetAllVuelosPort {
  List<VueloModel> getAll();
}
