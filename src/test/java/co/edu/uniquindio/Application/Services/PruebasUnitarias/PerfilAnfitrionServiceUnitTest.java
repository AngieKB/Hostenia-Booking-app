package co.edu.uniquindio.Application.Services.PruebasUnitarias;

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
import co.edu.uniquindio.Application.Services.impl.PerfilAnfitrionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PerfilAnfitrionServiceUnitTest {

    @Mock
    private PerfilAnfitrionRepository perfilAnfitrionRepository;

    @Mock
    private PerfilAnfitrionMapper perfilAnfitrionMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PerfilAnfitrionServiceImpl perfilAnfitrionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearAnfitrionExitoso() {
        CrearAnfitrionDTO dto = new CrearAnfitrionDTO(
                "Soy anfitriona responsable",
                List.of("documento1.pdf"),
                1L
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.HUESPED);

        PerfilAnfitrion perfil = new PerfilAnfitrion();

        when(perfilAnfitrionMapper.toEntity(dto)).thenReturn(perfil);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        perfilAnfitrionService.crearPerfil(dto);

        verify(perfilAnfitrionRepository).save(perfil);
        verify(usuarioRepository).save(usuario);
        assertEquals(Rol.ANFITRION, usuario.getRol());
    }

    @Test
    void testCrearAnfitrionNoEncontrado() {
        CrearAnfitrionDTO dto = new CrearAnfitrionDTO(
                "Soy anfitriona responsable",
                List.of("documento1.pdf"),
                1L
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> perfilAnfitrionService.crearPerfil(dto));
        verify(perfilAnfitrionRepository, never()).save(any());
    }

    @Test
    void testObtenerAnfitrionExitoso() {
        PerfilAnfitrion perfil = new PerfilAnfitrion();
        perfil.setId(5L);
        PerfilAnfitrionDTO dto = new PerfilAnfitrionDTO(5L, 1L, "desc", List.of(), List.of());

        when(perfilAnfitrionRepository.findById(5L)).thenReturn(Optional.of(perfil));
        when(perfilAnfitrionMapper.toDTO(perfil)).thenReturn(dto);

        PerfilAnfitrionDTO result = perfilAnfitrionService.obtenerPerfil(5L);

        assertEquals(5L, result.id());
        verify(perfilAnfitrionRepository).findById(5L);
    }

    @Test
    void testObtenerAnfitrionNoExistente() {
        when(perfilAnfitrionRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> perfilAnfitrionService.obtenerPerfil(10L));
    }

    @Test
    void testListarAnfitriones() {
        PerfilAnfitrion perfil1 = new PerfilAnfitrion();
        PerfilAnfitrion perfil2 = new PerfilAnfitrion();
        PerfilAnfitrionDTO dto1 = new PerfilAnfitrionDTO(1L, 1L, "desc1", List.of(), List.of());
        PerfilAnfitrionDTO dto2 = new PerfilAnfitrionDTO(2L, 2L, "desc2", List.of(), List.of());

        when(perfilAnfitrionRepository.findAll()).thenReturn(List.of(perfil1, perfil2));
        when(perfilAnfitrionMapper.toDTO(perfil1)).thenReturn(dto1);
        when(perfilAnfitrionMapper.toDTO(perfil2)).thenReturn(dto2);

        List<PerfilAnfitrionDTO> result = perfilAnfitrionService.listarPerfiles();

        assertEquals(2, result.size());
        verify(perfilAnfitrionRepository).findAll();
    }

    @Test
    void testActualizarAnfitrionExitoso() {
        EditarAnfitrionDTO dto = new EditarAnfitrionDTO("Ana", "3001234567", "http://foto.jpg", "Descripción actualizada");
        PerfilAnfitrion perfil = new PerfilAnfitrion();
        perfil.setId(1L);

        when(perfilAnfitrionRepository.findById(1L)).thenReturn(Optional.of(perfil));

        perfilAnfitrionService.actualizarPerfil(1L, dto);

        verify(perfilAnfitrionMapper).updatePerfilAnfitrionFromDto(dto, perfil);
        verify(perfilAnfitrionRepository).save(perfil);
    }

    @Test
    void testActualizarAnfitrionNoEncontrado() {
        EditarAnfitrionDTO dto = new EditarAnfitrionDTO("Ana", "3001234567", "http://foto.jpg", "Descripción actualizada");
        when(perfilAnfitrionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> perfilAnfitrionService.actualizarPerfil(1L, dto));
        verify(perfilAnfitrionRepository, never()).save(any());
    }

    @Test
    void testEliminarAnfitrionExitoso() {
        when(perfilAnfitrionRepository.existsById(1L)).thenReturn(true);
        perfilAnfitrionService.eliminarPerfil(1L);
        verify(perfilAnfitrionRepository).deleteById(1L);
    }

    @Test
    void testEliminarAnfitrionNoExistente() {
        when(perfilAnfitrionRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> perfilAnfitrionService.eliminarPerfil(1L));
        verify(perfilAnfitrionRepository, never()).deleteById(any());
    }
}
