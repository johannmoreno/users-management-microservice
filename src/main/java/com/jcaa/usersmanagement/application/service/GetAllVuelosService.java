package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetAllVuelosUseCase;
import com.jcaa.usersmanagement.application.port.out.GetAllVuelosPort;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllVuelosService implements GetAllVuelosUseCase {

  private final GetAllVuelosPort getAllVuelosPort;

  @Override
  public List<VueloModel> execute() {
    return getAllVuelosPort.getAll();
  }
}
