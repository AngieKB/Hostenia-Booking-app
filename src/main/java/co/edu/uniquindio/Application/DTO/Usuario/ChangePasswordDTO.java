package co.edu.uniquindio.Application.DTO.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotBlank String oldPassword,
        @NotBlank @Size(min=8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).*$",
                message = "La contraseña debe contener al menos una letra mayúscula, una letra minúsucla, y un número"
        ) String newPassword
) {
}
