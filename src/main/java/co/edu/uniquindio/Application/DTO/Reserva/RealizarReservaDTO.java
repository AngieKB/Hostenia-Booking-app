package co.edu.uniquindio.Application.DTO.Reserva;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RealizarReservaDTO(
        @NotNull @Future LocalDateTime fechaCheckIn,
        @NotNull @Future LocalDateTime fechaCheckOut,
        @NotNull Integer cantidadHuespedes,
        @NotNull Long alojamientoId
        ) {
}