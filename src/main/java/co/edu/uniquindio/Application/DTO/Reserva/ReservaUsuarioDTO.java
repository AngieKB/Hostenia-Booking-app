package co.edu.uniquindio.Application.DTO.Reserva;

import co.edu.uniquindio.Application.Model.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaUsuarioDTO(
        Long id,
        LocalDateTime fechaCheckIn,
        LocalDateTime fechaCheckOut,
        Integer cantidadHuespedes,
        Double total,
        EstadoReserva estado,
        String alojamientoTitulo,
        String alojamientoCiudad,
        List<String> alojamientoGaleria
) {}

