package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jcaa.usersmanagement.application.port.in.CreateVueloUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteVueloUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllVuelosUseCase;
import com.jcaa.usersmanagement.application.port.in.GetVueloByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateVueloUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateVueloCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetVueloByIdQuery;
import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.model.VueloModel;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import com.jcaa.usersmanagement.infrastructure.security.JwtAuthenticationFilter;
import com.jcaa.usersmanagement.infrastructure.security.JwtPrincipal;
import com.jcaa.usersmanagement.infrastructure.security.JwtTokenService;
import com.jcaa.usersmanagement.infrastructure.security.RestAccessDeniedHandler;
import com.jcaa.usersmanagement.infrastructure.security.RestAuthenticationEntryPoint;
import com.jcaa.usersmanagement.infrastructure.security.SecurityConfig;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = VueloRestController.class,
    properties = "spring.main.web-application-type=servlet")
@Import({
  SecurityConfig.class,
  JwtAuthenticationFilter.class,
  RestAuthenticationEntryPoint.class,
  RestAccessDeniedHandler.class
})
@DisplayName("VueloRestController")
class VueloRestControllerTest {

  private static final String ID = "v-001";
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final String AGENCIA_VIAJES = "Agencia Viajes Caribe";
  private static final String AEROLINEA = "Avianca";
  private static final String NUMERO = "AV1234";
  private static final BigDecimal VALOR = new BigDecimal("450000.00");
  private static final String CLIENTE = "Carlos Perez";
  private static final String PUESTO = "12A";
  private static final String AVION = "Airbus A320";
  private static final String AEROPUERTO_SALIDA = "Aeropuerto Rafael Nunez";
  private static final String AEROPUERTO_LLEGADA = "Aeropuerto El Dorado";
  private static final String PILOTO = "Capitan Juan Gomez";

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateVueloUseCase createVueloUseCase;
  @MockBean private UpdateVueloUseCase updateVueloUseCase;
  @MockBean private DeleteVueloUseCase deleteVueloUseCase;
  @MockBean private GetVueloByIdUseCase getVueloByIdUseCase;
  @MockBean private GetAllVuelosUseCase getAllVuelosUseCase;
  @MockBean private JwtTokenService jwtTokenService;

  // ── GET /api/vuelos

  @Test
  @DisplayName("GET /api/vuelos debe retornar todos los vuelos para un REVIEWER autenticado")
  void shouldGetAllVuelosThroughSpringMvc() throws Exception {
    // Arrange
    when(getAllVuelosUseCase.execute())
        .thenReturn(List.of(buildVuelo(ID, EstadoVuelo.PROGRAMADO)));
    when(jwtTokenService.parse("valid-token"))
        .thenReturn(new JwtPrincipal(ID, UserRole.REVIEWER));

    // Act & Assert
    mockMvc
        .perform(get("/api/vuelos").header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(ID))
        .andExpect(jsonPath("$[0].numero").value(NUMERO))
        .andExpect(jsonPath("$[0].estado").value("PROGRAMADO"))
        .andExpect(jsonPath("$[0].valor").value(450000.00));
  }

  // ── GET /api/vuelos/{id}

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("GET /api/vuelos/{id} debe responder con el vuelo consultado")
  void shouldGetVueloByIdThroughSpringMvc() throws Exception {
    // Arrange
    when(getVueloByIdUseCase.execute(new GetVueloByIdQuery(ID)))
        .thenReturn(buildVuelo(ID, EstadoVuelo.CONFIRMADO));

    // Act & Assert
    mockMvc
        .perform(get("/api/vuelos/{id}", ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ID))
        .andExpect(jsonPath("$.numero").value(NUMERO))
        .andExpect(jsonPath("$.puesto").value(PUESTO))
        .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
  }

  // ── POST /api/vuelos

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/vuelos debe crear un vuelo válido")
  void shouldCreateVueloThroughSpringMvc() throws Exception {
    // Arrange
    final String request =
        """
        {
          "id": "%s",
          "fechaCompra": "2026-01-10T08:00:00",
          "fechaSalida": "2026-02-15T10:30:00",
          "fechaLlegada": "2026-02-15T13:45:00",
          "agenciaViajes": "%s",
          "aerolinea": "%s",
          "numero": "%s",
          "valor": 450000.00,
          "cliente": "%s",
          "puesto": "%s",
          "avion": "%s",
          "aeropuertoSalida": "%s",
          "aeropuertoLlegada": "%s",
          "piloto": "%s"
        }
        """
            .formatted(
                ID,
                AGENCIA_VIAJES,
                AEROLINEA,
                NUMERO,
                CLIENTE,
                PUESTO,
                AVION,
                AEROPUERTO_SALIDA,
                AEROPUERTO_LLEGADA,
                PILOTO);
    when(createVueloUseCase.execute(buildCreateCommand()))
        .thenReturn(buildVuelo(ID, EstadoVuelo.PROGRAMADO));

    // Act & Assert
    mockMvc
        .perform(post("/api/vuelos").contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(ID))
        .andExpect(jsonPath("$.numero").value(NUMERO))
        .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/vuelos debe rechazar una solicitud inválida antes del caso de uso")
  void shouldRejectInvalidCreateRequest() throws Exception {
    // Arrange
    final String invalidRequest =
        """
        {
          "id": "",
          "fechaCompra": null,
          "fechaSalida": null,
          "fechaLlegada": null,
          "agenciaViajes": "",
          "aerolinea": "",
          "numero": "",
          "valor": 0,
          "cliente": "",
          "puesto": "",
          "avion": "",
          "aeropuertoSalida": "",
          "aeropuertoLlegada": "",
          "piloto": ""
        }
        """;

    // Act & Assert
    mockMvc
        .perform(
            post("/api/vuelos").contentType(MediaType.APPLICATION_JSON).content(invalidRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  // ── PUT /api/vuelos/{id}

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("PUT /api/vuelos/{id} debe actualizar un vuelo")
  void shouldUpdateVueloThroughSpringMvc() throws Exception {
    // Arrange
    final String request =
        """
        {
          "fechaCompra": "2026-01-10T08:00:00",
          "fechaSalida": "2026-02-15T10:30:00",
          "fechaLlegada": "2026-02-15T13:45:00",
          "agenciaViajes": "%s",
          "aerolinea": "%s",
          "numero": "%s",
          "estado": "CONFIRMADO",
          "valor": 450000.00,
          "cliente": "%s",
          "puesto": "%s",
          "avion": "%s",
          "aeropuertoSalida": "%s",
          "aeropuertoLlegada": "%s",
          "piloto": "%s"
        }
        """
            .formatted(
                AGENCIA_VIAJES,
                AEROLINEA,
                NUMERO,
                CLIENTE,
                PUESTO,
                AVION,
                AEROPUERTO_SALIDA,
                AEROPUERTO_LLEGADA,
                PILOTO);
    when(updateVueloUseCase.execute(buildUpdateCommand()))
        .thenReturn(buildVuelo(ID, EstadoVuelo.CONFIRMADO));

    // Act & Assert
    mockMvc
        .perform(
            put("/api/vuelos/{id}", ID).contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ID))
        .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
  }

  // ── DELETE /api/vuelos/{id}

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("DELETE /api/vuelos/{id} debe eliminar un vuelo")
  void shouldDeleteVueloThroughSpringMvc() throws Exception {
    // Act & Assert
    mockMvc.perform(delete("/api/vuelos/{id}", ID)).andExpect(status().isNoContent());

    verify(deleteVueloUseCase).execute(new DeleteVueloCommand(ID));
  }

  // ── Seguridad

  @Test
  @DisplayName("GET /api/vuelos/{id} debe requerir autenticación")
  void shouldRejectUnauthenticatedRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/api/vuelos/{id}", ID))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("GET /api/vuelos/{id} debe rechazar un rol sin permisos")
  void shouldRejectUnauthorizedRoleOnGet() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/api/vuelos/{id}", ID))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("POST /api/vuelos debe rechazar a un REVIEWER")
  void shouldRejectReviewerOnCreate() throws Exception {
    // Act & Assert
    mockMvc
        .perform(post("/api/vuelos").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("PUT /api/vuelos/{id} debe rechazar a un REVIEWER")
  void shouldRejectReviewerOnUpdate() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            put("/api/vuelos/{id}", ID).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("DELETE /api/vuelos/{id} debe rechazar a un REVIEWER")
  void shouldRejectReviewerOnDelete() throws Exception {
    // Act & Assert
    mockMvc
        .perform(delete("/api/vuelos/{id}", ID))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  // ── helpers

  private VueloModel buildVuelo(final String id, final EstadoVuelo estado) {
    return new VueloModel(
        new VueloId(id),
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        new NumeroVuelo(NUMERO),
        estado,
        new ValorVuelo(VALOR),
        CLIENTE,
        new PuestoVuelo(PUESTO),
        AVION,
        new Aeropuerto(AEROPUERTO_SALIDA),
        new Aeropuerto(AEROPUERTO_LLEGADA),
        PILOTO);
  }

  private CreateVueloCommand buildCreateCommand() {
    return new CreateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        NUMERO,
        VALOR,
        CLIENTE,
        PUESTO,
        AVION,
        AEROPUERTO_SALIDA,
        AEROPUERTO_LLEGADA,
        PILOTO);
  }

  private UpdateVueloCommand buildUpdateCommand() {
    return new UpdateVueloCommand(
        ID,
        FECHA_COMPRA,
        FECHA_SALIDA,
        FECHA_LLEGADA,
        AGENCIA_VIAJES,
        AEROLINEA,
        NUMERO,
        "CONFIRMADO",
        VALOR,
        CLIENTE,
        PUESTO,
        AVION,
        AEROPUERTO_SALIDA,
        AEROPUERTO_LLEGADA,
        PILOTO);
  }
}
