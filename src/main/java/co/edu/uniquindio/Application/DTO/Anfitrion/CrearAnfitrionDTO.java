package co.edu.uniquindio.Application.DTO.Anfitrion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearAnfitrionDTO(
        @NotBlank @Length(max = 300) String descripcion,
        @NotNull List<String> documentosLegales,
        @NotNull Long usuarioId
) {
}