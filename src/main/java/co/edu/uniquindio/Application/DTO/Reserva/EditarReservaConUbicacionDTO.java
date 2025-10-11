package co.edu.uniquindio.Application.DTO.Reserva;

import co.edu.uniquindio.Application.DTO.Alojamiento.UbicacionDTO;

public record EditarReservaConUbicacionDTO(
        EditarReservaDTO reserva,
        UbicacionDTO ubicacion
) {}

