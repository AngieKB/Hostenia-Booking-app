package co.edu.uniquindio.Application.DTO.Alojamiento;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.DTO.Reserva.ReservaDTO;
import co.edu.uniquindio.Application.Model.EstadoAlojamiento;

import java.util.List;

public record AlojamientoDTO (
    Long id,
    String titulo,
    String descripcion,
    List<String> servicios,
    List<String> galeria,
    UbicacionDTO ubicacion,
    Double precioNoche,
    Integer capacidadMax,
    List<ComentarioDTO> comentarios,
    List<ReservaDTO> reservas,
    EstadoAlojamiento estado
){
}
