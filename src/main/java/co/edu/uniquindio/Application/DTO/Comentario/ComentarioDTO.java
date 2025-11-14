package co.edu.uniquindio.Application.DTO.Comentario;

import java.time.LocalDateTime;

public record ComentarioDTO (
        Long id,
        String texto,
        Integer calificacion,
        LocalDateTime fecha,
        String nombreHuesped,
        String nombreAnfitrion,
        String fotoHuesped,
        String fotoAnfitrion,
        String textoRespuesta


) {}