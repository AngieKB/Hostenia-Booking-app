package co.edu.uniquindio.Application.DTO.Comentario;

import java.time.LocalDateTime;

public record RespuestaDTO (
        Long id,
        Long idComentario,
        String respuesta,
        LocalDateTime fecha
) {
}