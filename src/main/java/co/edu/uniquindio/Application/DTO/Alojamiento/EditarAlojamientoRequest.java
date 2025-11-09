package co.edu.uniquindio.Application.DTO.Alojamiento;

import jakarta.validation.Valid;

public record EditarAlojamientoRequest(
        @Valid EditarAlojamientoDTO alojamientoDTO,
        @Valid UbicacionDTO ubicacionDTO
) {}
