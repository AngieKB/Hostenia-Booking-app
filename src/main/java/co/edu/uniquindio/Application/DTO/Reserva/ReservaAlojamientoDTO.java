package co.edu.uniquindio.Application.DTO.Reserva;

import co.edu.uniquindio.Application.Model.EstadoReserva;

import java.time.LocalDateTime;

public record ReservaAlojamientoDTO(
        Long id,
        Long idHuesped,
        String nombreHuesped,
        LocalDateTime fechaCheckIn,
        LocalDateTime fechaCheckOut,
        Integer cantidadHuespedes,
        Double total,
        EstadoReserva estado,
        String alojamientoTitulo,
        String alojamientoCiudad
) {}

