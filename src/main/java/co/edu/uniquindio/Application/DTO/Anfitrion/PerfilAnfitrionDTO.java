package co.edu.uniquindio.Application.DTO.Anfitrion;

import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;

import java.util.List;

public record PerfilAnfitrionDTO(
        Long id,
        Long usuarioId,
        String descripcion,
        List<String> domentosLegales,
        List<Long> alojamientos
){
}