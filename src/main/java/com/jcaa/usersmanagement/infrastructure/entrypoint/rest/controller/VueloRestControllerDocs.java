package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import com.jcaa.usersmanagement.infrastructure.config.OpenApiConfig;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateVueloRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ApiErrorResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.VueloRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Contrato OpenAPI / Swagger para {@link VueloRestController}.
 *
 * <p>Toda la documentación de la API (anotaciones {@code @Operation}, {@code @ApiResponses}, etc.)
 * vive aquí para mantener el controlador limpio y enfocado únicamente en la lógica de despacho.
 */
@Tag(name = "Vuelos", description = "Gestión de vuelos: crear, consultar, actualizar y eliminar.")
public interface VueloRestControllerDocs {

  // ─────────────────────────────────────────────────────────────────────────────
  // POST /api/vuelos
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Crear vuelo",
      description =
          "Registra un nuevo vuelo en el sistema. El ID debe ser único y la combinación "
              + "número, fecha de salida y puesto no puede estar en uso. Requiere rol ADMIN.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Vuelo creado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VueloRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos (campos requeridos vacíos o formato incorrecto).",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un vuelo con ese número, fecha de salida y puesto.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  VueloRestResponse create(@Valid @RequestBody CreateVueloRestRequest request);

  // ─────────────────────────────────────────────────────────────────────────────
  // GET /api/vuelos
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Listar todos los vuelos",
      description = "Retorna la lista completa de vuelos ordenada por fecha de salida.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista de vuelos obtenida exitosamente. Puede ser vacía.",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = VueloRestResponse.class)))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @SuppressWarnings("unused") // Invocado por Spring MVC y leído por springdoc mediante reflexión.
  List<VueloRestResponse> getAll();

  // ─────────────────────────────────────────────────────────────────────────────
  // GET /api/vuelos/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Obtener vuelo por ID",
      description = "Retorna los datos de un vuelo específico identificado por su ID único.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Vuelo encontrado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VueloRestResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún vuelo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  VueloRestResponse getById(
      @Parameter(description = "ID único del vuelo.", example = "vuelo-001", required = true)
          @PathVariable
          String id);

  // ─────────────────────────────────────────────────────────────────────────────
  // PUT /api/vuelos/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Actualizar vuelo",
      description =
          "Actualiza los datos de un vuelo existente. La combinación número, fecha de salida y "
              + "puesto no debe estar en uso por otro vuelo. Requiere rol ADMIN.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Vuelo actualizado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VueloRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún vuelo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "La nueva clave única ya está en uso por otro vuelo.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  VueloRestResponse update(
      @Parameter(description = "ID único del vuelo a actualizar.", example = "vuelo-001", required = true)
          @PathVariable
          String id,
      @Valid @RequestBody UpdateVueloRestRequest request);

  // ─────────────────────────────────────────────────────────────────────────────
  // DELETE /api/vuelos/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Eliminar vuelo",
      description = "Elimina permanentemente un vuelo del sistema por su ID. Requiere rol ADMIN.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Vuelo eliminado exitosamente."),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún vuelo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  void delete(
      @Parameter(description = "ID único del vuelo a eliminar.", example = "vuelo-001", required = true)
          @PathVariable
          String id);
}
