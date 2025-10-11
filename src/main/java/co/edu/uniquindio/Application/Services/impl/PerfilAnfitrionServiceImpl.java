package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.DTO.Anfitrion.CrearAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.Anfitrion.EditarAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.Anfitrion.PerfilAnfitrionDTO;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Mappers.PerfilAnfitrionMapper;
import co.edu.uniquindio.Application.Model.PerfilAnfitrion;
import co.edu.uniquindio.Application.Model.Rol;
import co.edu.uniquindio.Application.Model.Usuario;
import co.edu.uniquindio.Application.Repository.PerfilAnfitrionRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.PerfilAnfitrionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilAnfitrionServiceImpl implements PerfilAnfitrionService {
    private final PerfilAnfitrionRepository perfilAnfitrionRepository;
    private final PerfilAnfitrionMapper perfilAnfitrionMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void crearPerfil(CrearAnfitrionDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.usuarioId() + " no encontrado"));

        PerfilAnfitrion perfil = perfilAnfitrionMapper.toEntity(dto);
        perfil.setUsuario(usuario);
        perfilAnfitrionRepository.save(perfil);

        usuario.setRol(Rol.ANFITRION);

        usuarioRepository.save(usuario);
    }

    @Override
    public PerfilAnfitrionDTO obtenerPerfil(Long id) {
        PerfilAnfitrion perfil = perfilAnfitrionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de anfitrión con id " + id + " no encontrado"));
        return perfilAnfitrionMapper.toDTO(perfil);
    }

    @Override
    public List<PerfilAnfitrionDTO> listarPerfiles() {
        return perfilAnfitrionRepository.findAll()
                .stream()
                .map(perfilAnfitrionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void actualizarPerfil(Long id, EditarAnfitrionDTO dto) {
        PerfilAnfitrion perfil = perfilAnfitrionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de anfitrión con id " + id + " no encontrado"));
        perfilAnfitrionMapper.updatePerfilAnfitrionFromDto(dto, perfil);
        perfilAnfitrionRepository.save(perfil);
    }

    @Override
    public void eliminarPerfil(Long id) {
        if (!perfilAnfitrionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil de anfitrión con id " + id + " no encontrado");
        }
        perfilAnfitrionRepository.deleteById(id);
    }
}
