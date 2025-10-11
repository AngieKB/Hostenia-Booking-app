package co.edu.uniquindio.Application.DTO.Alojamiento;

import jakarta.validation.Valid;

public record EditarAlojamientoRequest(
        @Valid AlojamientoDTO alojamientoDTO,
        @Valid UbicacionDTO ubicacionDTO
) {}
