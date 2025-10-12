package co.edu.uniquindio.Application.Services.PruebasUnitarias;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.NotFoundException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Exceptions.ValidationException;
import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.Comentario;
import co.edu.uniquindio.Application.Model.PerfilAnfitrion;
import co.edu.uniquindio.Application.Model.Reserva;
import co.edu.uniquindio.Application.Model.Usuario;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.ReservaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.impl.AuthService;
import co.edu.uniquindio.Application.Services.impl.ComentarioServiceImpl;
import co.edu.uniquindio.Application.Mappers.ComentarioMapper;
import co.edu.uniquindio.Application.Services.EmailService;
import co.edu.uniquindio.Application.DTO.EmailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceUnitTest {

    @Mock private ComentarioRepository comentarioRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AlojamientoRepository alojamientoRepository;
    @Mock private ComentarioMapper comentarioMapper;
    @Mock private EmailService emailService;
    @Mock private AuthService authService;

    @InjectMocks
    private ComentarioServiceImpl comentarioService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        // Usuario huésped
        usuario = new Usuario();
        usuario.setId(1L);

        // Perfil anfitrión y alojamiento
        PerfilAnfitrion anfitrion = new PerfilAnfitrion();
        Usuario usuarioAnfitrion = new Usuario();
        usuarioAnfitrion.setId(99L);
        anfitrion.setUsuario(usuarioAnfitrion);

        alojamiento = new Alojamiento();
        alojamiento.setId(1L);
        alojamiento.setAnfitrion(anfitrion);

        // Reserva finalizada
        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setHuesped(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaCheckOut(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testCrearComentarioExitoso() throws Exception {
        Long reservaId = reserva.getId();

        ComentarDTO comentarDTO = new ComentarDTO(
                "Excelente estancia",
                5,
                alojamiento.getId()
        );

        Comentario comentario = new Comentario();
        comentario.setId(200L);
        comentario.setTexto(comentarDTO.texto());
        comentario.setCalificacion(comentarDTO.calificacion());
        comentario.setReserva(reserva);
        comentario.setAlojamiento(alojamiento);

        // Mock: reserva existente
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reserva));

        // Mock: no existe comentario previo
        when(comentarioRepository.existsByReservaId(reservaId)).thenReturn(false);

        // Mock: el mapper convierte correctamente el DTO en entidad
        when(comentarioMapper.toEntity(any(ComentarDTO.class))).thenReturn(comentario);

        // Mock: simulamos guardado del comentario
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        // Mock: al calcular el promedio, hay 1 comentario de 5 estrellas
        when(comentarioRepository.findByAlojamientoId(alojamiento.getId()))
                .thenReturn(List.of(comentario));

        // Mock: alojamiento guardado correctamente
        when(alojamientoRepository.save(any(Alojamiento.class))).thenReturn(alojamiento);

        // Mock: email service (solo se verifica que se invoque)
        doNothing().when(emailService).sendMail(any(EmailDTO.class));

        when(authService.getUsuarioAutenticado()).thenReturn(usuario);

        // Ejecución
        assertDoesNotThrow(() -> comentarioService.comentar(reservaId, comentarDTO));

        // Verificaciones
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
        verify(emailService, times(2)).sendMail(any(EmailDTO.class));
    }

    @Test
    void testListarComentariosPorAlojamientoExitoso() throws Exception {
        Long alojamientoId = alojamiento.getId();

        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setTexto("Todo excelente");
        comentario.setCalificacion(5);
        comentario.setAlojamiento(alojamiento);
        comentario.setFecha(LocalDateTime.now());

        ComentarioDTO comentarioDTO = new ComentarioDTO(
                comentario.getId(),
                comentario.getTexto(),
                comentario.getCalificacion(),
                comentario.getFecha()
        );

        when(comentarioRepository.findByAlojamientoIdOrderByFechaDesc(alojamientoId))
                .thenReturn(List.of(comentario));

        // Mock del mapper
        when(comentarioMapper.toDto(any(Comentario.class))).thenReturn(comentarioDTO);

        // Ejecución
        List<ComentarioDTO> resultado = comentarioService.listarComentariosPorAlojamiento(alojamientoId);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Debe devolver exactamente 1 comentario");
        assertEquals("Todo excelente", resultado.get(0).texto());
        assertEquals(5, resultado.get(0).calificacion());

        verify(comentarioRepository, times(1)).findByAlojamientoIdOrderByFechaDesc(alojamientoId);
    }

    @Test
    void testCrearComentarioReservaNoExiste() {
        ComentarDTO dto = new ComentarDTO("No debería guardarse", 4, 1L);
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> comentarioService.comentar(999L, dto));
    }

    @Test
    void testCrearComentarioReservaNoFinalizada() {
        reserva.setFechaCheckOut(LocalDateTime.now().plusDays(1));
        lenient().when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        lenient().when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        lenient().when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        lenient().when(authService.getUsuarioAutenticado()).thenReturn(usuario);

        ComentarDTO dto = new ComentarDTO(
                "Intento antes del checkout", 3, alojamiento.getId()
        );

        ValidationException ex = assertThrows(ValidationException.class,
                () -> comentarioService.comentar(reserva.getId(), dto));
        assertEquals("No puede comentar: la reserva aún no ha finalizado", ex.getMessage());
    }

    @Test
    void testCrearComentarioReservaYaComentada() {
        lenient().when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        lenient().when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        lenient().when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        lenient().when(comentarioRepository.existsByReservaId(reserva.getId())).thenReturn(true);
        lenient().when(authService.getUsuarioAutenticado()).thenReturn(usuario);

        ComentarDTO dto = new ComentarDTO("Duplicado", 5, alojamiento.getId());
        assertThrows(InvalidOperationException.class, () -> comentarioService.comentar(reserva.getId(), dto));
    }


    @Test
    void testListarComentariosPorAlojamientoVacio() throws Exception {
        lenient().when(comentarioRepository.findByAlojamientoId(99L))
                .thenReturn(Collections.emptyList());

        List<ComentarioDTO> resultado = comentarioService.listarComentariosPorAlojamiento(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
