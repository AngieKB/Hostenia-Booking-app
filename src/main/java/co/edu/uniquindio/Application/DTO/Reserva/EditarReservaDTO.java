package co.edu.uniquindio.Application.DTO.Reserva;

import java.time.LocalDateTime;

public record EditarReservaDTO(
        LocalDateTime fechaCheckIn,
        LocalDateTime fechaCheckOut,
        Integer cantidadHuespedes
) {
}
