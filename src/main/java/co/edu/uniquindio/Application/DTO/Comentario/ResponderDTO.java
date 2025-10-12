package co.edu.uniquindio.Application.DTO.Comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ResponderDTO(
        @NotNull @NotBlank @Length(max = 300) String respuesta,
        @NotNull Long idComentario
){}