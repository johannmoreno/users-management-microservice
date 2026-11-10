package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.EstadoVuelo;
import com.jcaa.usersmanagement.domain.exception.InvalidAeropuertoException;
import com.jcaa.usersmanagement.domain.exception.InvalidFechaVueloException;
import com.jcaa.usersmanagement.domain.exception.InvalidVueloFieldException;
import com.jcaa.usersmanagement.domain.valueobject.Aeropuerto;
import com.jcaa.usersmanagement.domain.valueobject.NumeroVuelo;
import com.jcaa.usersmanagement.domain.valueobject.PuestoVuelo;
import com.jcaa.usersmanagement.domain.valueobject.ValorVuelo;
import com.jcaa.usersmanagement.domain.valueobject.VueloId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para VueloModel.
 *
 * <p>Se cubren la fábrica {@code create()} que fija el estado inicial PROGRAMADO, las transiciones de
 * estado y las reglas de validación de fechas, textos y aeropuertos.
 */
@DisplayName("VueloModel")
class VueloModelTest {

  // ── Arrange (variables globales para la mayoría de las test)

  private static final VueloId ID = new VueloId("v-001");
  private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2026, 1, 10, 8, 0);
  private static final LocalDateTime FECHA_SALIDA = LocalDateTime.of(2026, 2, 15, 10, 30);
  private static final LocalDateTime FECHA_LLEGADA = LocalDateTime.of(2026, 2, 15, 13, 45);
  private static final String AGENCIA_VIAJES = "Agencia Viajes Caribe";
  private static final String AEROLINEA = "Avianca";
  private static final NumeroVuelo NUMERO = new NumeroVuelo("AV1234");
  private static final ValorVuelo VALOR = new ValorVuelo(new BigDecimal("450000.00"));
  private static final String CLIENTE = "Carlos Perez";
  private static final PuestoVuelo PUESTO = new PuestoVuelo("12A");
  private static final String AVION = "Airbus A320";
  private static final Aeropuerto AEROPUERTO_SALIDA = new Aeropuerto("Aeropuerto Rafael Nunez");
  private static final Aeropuerto AEROPUERTO_LLEGADA = new Aeropuerto("Aeropuerto El Dorado");
  private static final String PILOTO = "Capitan Juan Gomez";

  private VueloModel validVuelo;

  @BeforeEach
  void setUp() {
    validVuelo = createValidVuelo();
  }

  // ── create()

  @Test
  @DisplayName("create() debe fijar estado PROGRAMADO y preservar todos los campos recibidos")
  void shouldCreateVueloWithProgramadoStatusAndPreserveAllFields() {
    // Act
    final VueloModel model = createValidVuelo();

    // Assert
    assertAll(
        "create() factory",
        () ->
            assertEquals(
                EstadoVuelo.PROGRAMADO, model.getEstado(), "estado debe iniciar como PROGRAMADO"),
        () -> assertSame(ID, model.getId(), "id debe preservarse"),
        () -> assertEquals(FECHA_COMPRA, model.getFechaCompra(), "fechaCompra debe preservarse"),
        () -> assertEquals(FECHA_SALIDA, model.getFechaSalida(), "fechaSalida debe preservarse"),
        () -> assertEquals(FECHA_LLEGADA, model.getFechaLlegada(), "fechaLlegada debe preservarse"),
        () -> assertEquals(AGENCIA_VIAJES, model.getAgenciaViajes()),
        () -> assertEquals(AEROLINEA, model.getAerolinea()),
        () -> assertSame(NUMERO, model.getNumero()),
        () -> assertSame(VALOR, model.getValor()),
        () -> assertEquals(CLIENTE, model.getCliente()),
        () -> assertSame(PUESTO, model.getPuesto()),
        () -> assertEquals(AVION, model.getAvion()),
        () -> assertSame(AEROPUERTO_SALIDA, model.getAeropuertoSalida()),
        () -> assertSame(AEROPUERTO_LLEGADA, model.getAeropuertoLlegada()),
        () -> assertEquals(PILOTO, model.getPiloto()));
  }

  // ── Transiciones de estado

  @Test
  @DisplayName("confirmar() debe retornar nueva instancia con CONFIRMADO y demás campos intactos")
  void shouldConfirmAndPreserveOtherFields() {
    // Act
    final VueloModel confirmed = validVuelo.confirmar();

    // Assert
    assertAll(
        "resultado de confirmar()",
        () -> assertNotSame(validVuelo, confirmed, "debe ser una nueva instancia"),
        () -> assertEquals(EstadoVuelo.CONFIRMADO, confirmed.getEstado()),
        () -> assertSame(ID, confirmed.getId()),
        () -> assertSame(NUMERO, confirmed.getNumero()),
        () -> assertSame(PUESTO, confirmed.getPuesto()));
  }

  @Test
  @DisplayName("cancelar() debe retornar nueva instancia con CANCELADO y demás campos intactos")
  void shouldCancelAndPreserveOtherFields() {
    // Act
    final VueloModel cancelled = validVuelo.cancelar();

    // Assert
    assertAll(
        "resultado de cancelar()",
        () -> assertNotSame(validVuelo, cancelled, "debe ser una nueva instancia"),
        () -> assertEquals(EstadoVuelo.CANCELADO, cancelled.getEstado()),
        () -> assertSame(ID, cancelled.getId()),
        () -> assertSame(NUMERO, cancelled.getNumero()),
        () -> assertSame(PUESTO, cancelled.getPuesto()));
  }

  @Test
  @DisplayName("despegar() debe retornar nueva instancia con EN_VUELO y demás campos intactos")
  void shouldTakeOffAndPreserveOtherFields() {
    // Act
    final VueloModel inFlight = validVuelo.despegar();

    // Assert
    assertAll(
        "resultado de despegar()",
        () -> assertNotSame(validVuelo, inFlight, "debe ser una nueva instancia"),
        () -> assertEquals(EstadoVuelo.EN_VUELO, inFlight.getEstado()),
        () -> assertSame(ID, inFlight.getId()),
        () -> assertSame(NUMERO, inFlight.getNumero()),
        () -> assertSame(PUESTO, inFlight.getPuesto()));
  }

  @Test
  @DisplayName("aterrizar() debe retornar nueva instancia con ATERRIZADO y demás campos intactos")
  void shouldLandAndPreserveOtherFields() {
    // Act
    final VueloModel landed = validVuelo.aterrizar();

    // Assert
    assertAll(
        "resultado de aterrizar()",
        () -> assertNotSame(validVuelo, landed, "debe ser una nueva instancia"),
        () -> assertEquals(EstadoVuelo.ATERRIZADO, landed.getEstado()),
        () -> assertSame(ID, landed.getId()),
        () -> assertSame(NUMERO, landed.getNumero()),
        () -> assertSame(PUESTO, landed.getPuesto()));
  }

  @Test
  @DisplayName("retrasar() debe retornar nueva instancia con RETRASADO y demás campos intactos")
  void shouldDelayAndPreserveOtherFields() {
    // Act
    final VueloModel delayed = validVuelo.retrasar();

    // Assert
    assertAll(
        "resultado de retrasar()",
        () -> assertNotSame(validVuelo, delayed, "debe ser una nueva instancia"),
        () -> assertEquals(EstadoVuelo.RETRASADO, delayed.getEstado()),
        () -> assertSame(ID, delayed.getId()),
        () -> assertSame(NUMERO, delayed.getNumero()),
        () -> assertSame(PUESTO, delayed.getPuesto()));
  }

  // ── Validación de fechas

  @Test
  @DisplayName("Debe lanzar InvalidFechaVueloException cuando fechaCompra es null")
  void shouldThrowWhenFechaCompraIsNull() {
    assertThrows(
        InvalidFechaVueloException.class,
        () ->
            buildVuelo(
                ID,
                null,
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
                PILOTO));
  }

  @Test
  @DisplayName("Debe lanzar InvalidFechaVueloException cuando fechaSalida es null")
  void shouldThrowWhenFechaSalidaIsNull() {
    assertThrows(
        InvalidFechaVueloException.class,
        () ->
            buildVuelo(
                ID,
                FECHA_COMPRA,
                null,
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
                PILOTO));
  }

  @Test
  @DisplayName("Debe lanzar InvalidFechaVueloException cuando fechaLlegada es null")
  void shouldThrowWhenFechaLlegadaIsNull() {
    assertThrows(
        InvalidFechaVueloException.class,
        () ->
            buildVuelo(
                ID,
                FECHA_COMPRA,
                FECHA_SALIDA,
                null,
                AGENCIA_VIAJES,
                AEROLINEA,
                NUMERO,
                VALOR,
                CLIENTE,
                PUESTO,
                AVION,
                AEROPUERTO_SALIDA,
                AEROPUERTO_LLEGADA,
                PILOTO));
  }

  @Test
  @DisplayName("Debe lanzar InvalidFechaVueloException cuando fechaCompra es posterior a fechaSalida")
  void shouldThrowWhenFechaCompraIsAfterFechaSalida() {
    final LocalDateTime fechaCompraPosterior = FECHA_SALIDA.plusDays(1);

    assertThrows(
        InvalidFechaVueloException.class,
        () ->
            buildVuelo(
                ID,
                fechaCompraPosterior,
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
                PILOTO));
  }

  @Test
  @DisplayName("Debe permitir fechaCompra igual a fechaSalida")
  void shouldAllowFechaCompraEqualToFechaSalida() {
    final VueloModel model =
        buildVuelo(
            ID,
            FECHA_SALIDA,
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

    assertEquals(FECHA_SALIDA, model.getFechaCompra());
  }

  @Test
  @DisplayName("Debe lanzar InvalidFechaVueloException cuando fechaLlegada no es posterior a fechaSalida")
  void shouldThrowWhenFechaLlegadaIsNotAfterFechaSalida() {
    assertAll(
        "fechaLlegada inválida",
        () ->
            assertThrows(
                InvalidFechaVueloException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_SALIDA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidFechaVueloException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_SALIDA.minusHours(1),
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)));
  }

  // ── Validación de textos

  @Test
  @DisplayName("Debe lanzar InvalidVueloFieldException cuando un campo de texto está vacío")
  void shouldThrowWhenTextFieldsAreEmpty() {
    assertAll(
        "campos de texto vacíos",
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        "   ",
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        "",
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        "  ",
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        "",
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        "\t")));
  }

  @Test
  @DisplayName("Debe lanzar InvalidVueloFieldException cuando un campo de texto es null")
  void shouldThrowWhenTextFieldsAreNull() {
    assertAll(
        "campos de texto nulos",
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        null,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        null,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        null,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        null,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        null)));
  }

  @Test
  @DisplayName("Debe lanzar InvalidVueloFieldException cuando un campo de texto excede la longitud")
  void shouldThrowWhenTextFieldsAreTooLong() {
    assertAll(
        "campos de texto demasiado largos",
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        "a".repeat(101),
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        "a".repeat(101),
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        "a".repeat(101),
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        "a".repeat(51),
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                InvalidVueloFieldException.class,
                () ->
                    buildVuelo(
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
                        "a".repeat(101))));
  }

  @Test
  @DisplayName("Debe normalizar los campos de texto con trim")
  void shouldTrimTextFields() {
    // Act
    final VueloModel model =
        buildVuelo(
            ID,
            FECHA_COMPRA,
            FECHA_SALIDA,
            FECHA_LLEGADA,
            "  Agencia Viajes Caribe  ",
            "  Avianca  ",
            NUMERO,
            VALOR,
            "  Carlos Perez  ",
            PUESTO,
            "  Airbus A320  ",
            AEROPUERTO_SALIDA,
            AEROPUERTO_LLEGADA,
            "  Capitan Juan Gomez  ");

    // Assert
    assertAll(
        "campos normalizados",
        () -> assertEquals(AGENCIA_VIAJES, model.getAgenciaViajes()),
        () -> assertEquals(AEROLINEA, model.getAerolinea()),
        () -> assertEquals(CLIENTE, model.getCliente()),
        () -> assertEquals(AVION, model.getAvion()),
        () -> assertEquals(PILOTO, model.getPiloto()));
  }

  // ── Validación de aeropuertos

  @Test
  @DisplayName("Debe lanzar InvalidAeropuertoException cuando los aeropuertos son iguales")
  void shouldThrowWhenAirportsAreEqualIgnoringCase() {
    assertThrows(
        InvalidAeropuertoException.class,
        () ->
            buildVuelo(
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
                new Aeropuerto("Aeropuerto El Dorado"),
                new Aeropuerto("aeropuerto el dorado"),
                PILOTO));
  }

  // ── Validación de objetos obligatorios

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando un objeto obligatorio es null")
  void shouldThrowNullPointerExceptionWhenRequiredObjectsAreNull() {
    assertAll(
        "objetos obligatorios nulos",
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
                        null,
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
                        PILOTO)),
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        null,
                        VALOR,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        null,
                        CLIENTE,
                        PUESTO,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
                        ID,
                        FECHA_COMPRA,
                        FECHA_SALIDA,
                        FECHA_LLEGADA,
                        AGENCIA_VIAJES,
                        AEROLINEA,
                        NUMERO,
                        VALOR,
                        CLIENTE,
                        null,
                        AVION,
                        AEROPUERTO_SALIDA,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
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
                        null,
                        AEROPUERTO_LLEGADA,
                        PILOTO)),
        () ->
            assertThrows(
                NullPointerException.class,
                () ->
                    buildVuelo(
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
                        null,
                        PILOTO)));
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando estado es null en el constructor completo")
  void shouldThrowNullPointerExceptionWhenEstadoIsNull() {
    assertThrows(
        NullPointerException.class,
        () ->
            new VueloModel(
                ID,
                FECHA_COMPRA,
                FECHA_SALIDA,
                FECHA_LLEGADA,
                AGENCIA_VIAJES,
                AEROLINEA,
                NUMERO,
                null,
                VALOR,
                CLIENTE,
                PUESTO,
                AVION,
                AEROPUERTO_SALIDA,
                AEROPUERTO_LLEGADA,
                PILOTO));
  }

  // ── Helpers

  private static VueloModel createValidVuelo() {
    return buildVuelo(
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

  private static VueloModel buildVuelo(
      final VueloId id,
      final LocalDateTime fechaCompra,
      final LocalDateTime fechaSalida,
      final LocalDateTime fechaLlegada,
      final String agenciaViajes,
      final String aerolinea,
      final NumeroVuelo numero,
      final ValorVuelo valor,
      final String cliente,
      final PuestoVuelo puesto,
      final String avion,
      final Aeropuerto aeropuertoSalida,
      final Aeropuerto aeropuertoLlegada,
      final String piloto) {
    return VueloModel.create(
        id,
        fechaCompra,
        fechaSalida,
        fechaLlegada,
        agenciaViajes,
        aerolinea,
        numero,
        valor,
        cliente,
        puesto,
        avion,
        aeropuertoSalida,
        aeropuertoLlegada,
        piloto);
  }
}
