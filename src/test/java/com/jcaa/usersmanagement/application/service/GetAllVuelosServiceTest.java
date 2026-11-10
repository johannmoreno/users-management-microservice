package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllVuelosPort;
import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para GetAllVuelosService.
 *
 * <p>Cubre: lista con vuelos y lista vacía.
 */
@DisplayName("GetAllVuelosService")
@ExtendWith(MockitoExtension.class)
class GetAllVuelosServiceTest {

  @Mock private GetAllVuelosPort getAllVuelosPort;

  private GetAllVuelosService service;

  @BeforeEach
  void setUp() {
    service = new GetAllVuelosService(getAllVuelosPort);
  }

  // ── lista con vuelos

  @Test
  @DisplayName("execute() retorna la lista de vuelos del puerto")
  void shouldReturnVuelosFromPort() {
    // Arrange
    final VueloModel vuelo =
        VueloModel.create(
            new VueloId("v-001"),
            LocalDateTime.of(2026, 1, 10, 8, 0),
            LocalDateTime.of(2026, 2, 15, 10, 30),
            LocalDateTime.of(2026, 2, 15, 13, 45),
            "Agencia Viajes Caribe",
            "Avianca",
            new NumeroVuelo("AV1234"),
            new ValorVuelo(new BigDecimal("450000.00")),
            "Carlos Perez",
            new PuestoVuelo("12A"),
            "Airbus A320",
            new Aeropuerto("Aeropuerto Rafael Nunez"),
            new Aeropuerto("Aeropuerto El Dorado"),
            "Capitan Juan Gomez");

    when(getAllVuelosPort.getAll()).thenReturn(List.of(vuelo));

    // Act
    final List<VueloModel> result = service.execute();

    // Assert
    assertAll(
        "getAll con un vuelo",
        () -> assertEquals(1, result.size(), "debe retornar exactamente un vuelo"),
        () -> assertSame(vuelo, result.get(0), "debe ser el mismo objeto del puerto"),
        () -> assertEquals(EstadoVuelo.PROGRAMADO, result.get(0).getEstado()));
  }

  // ── lista vacía

  @Test
  @DisplayName("execute() retorna lista vacía cuando no hay vuelos")
  void shouldReturnEmptyListWhenNoVuelos() {
    // Arrange
    when(getAllVuelosPort.getAll()).thenReturn(List.of());

    // Act
    final List<VueloModel> result = service.execute();

    // Assert
    assertTrue(result.isEmpty(), "debe retornar lista vacía");
  }
}
