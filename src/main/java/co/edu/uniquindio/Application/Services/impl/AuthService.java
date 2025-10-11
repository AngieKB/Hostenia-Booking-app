package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.Model.Usuario;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay un usuario autenticado");
        }

        String idStr = authentication.getName(); // en tu caso, guardas el ID como username
        Long id = Long.parseLong(idStr);

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }
}