package co.edu.uniquindio.Application.Services.PruebasUnitarias;

import co.edu.uniquindio.Application.DTO.Comentario.ResponderDTO;
import co.edu.uniquindio.Application.DTO.Comentario.RespuestaDTO;
import co.edu.uniquindio.Application.DTO.EmailDTO;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Mappers.RespuestaMapper;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.RespuestaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.EmailService;
import co.edu.uniquindio.Application.Services.impl.RespuestaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/*public class RespuestaServiceUnitTest {

    @Mock
    private RespuestaRepository respuestaRepository;

    @Mock
    private RespuestaMapper respuestaMapper;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RespuestaServiceImpl respuestaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testResponderComentario_Exitoso() throws Exception {
        // --- Arrange ---
        ResponderDTO dto = new ResponderDTO("Gracias por tu comentario", 1L);

        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(2L);
        anfitrionUsuario.setNombre("Carlos");
        anfitrionUsuario.setEmail("carlos@host.com");

        Usuario huesped = new Usuario();
        huesped.setId(3L);
        huesped.setNombre("Ana");
        huesped.setEmail("ana@mail.com");

        Reserva reserva = new Reserva();
        reserva.setHuesped(huesped);

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setTitulo("Casa de playa");
        PerfilAnfitrion anfitrion = new PerfilAnfitrion();
        anfitrion.setUsuario(anfitrionUsuario);
        alojamiento.setAnfitrion(anfitrion);

        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setTexto("Muy buen lugar");
        comentario.setAlojamiento(alojamiento);
        comentario.setReserva(reserva);

        Respuesta respuesta = new Respuesta();
        respuesta.setTexto("Gracias por hospedarte");

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
        when(respuestaRepository.existsByComentarioId(1L)).thenReturn(false);
        when(respuestaMapper.toEntity(dto)).thenReturn(respuesta);
        when(respuestaRepository.save(any(Respuesta.class))).thenReturn(respuesta);

        doNothing().when(emailService).sendMail(any(EmailDTO.class));

        // --- Act ---
        respuestaService.responderComentario(dto);

        // --- Assert ---
        verify(comentarioRepository, times(1)).findById(1L);
        verify(respuestaRepository, times(1)).save(any(Respuesta.class));
        verify(emailService, times(2)).sendMail(any(EmailDTO.class));
    }

    @Test
    void testResponderComentario_ComentarioNoExiste() {
        ResponderDTO dto = new ResponderDTO("Gracias", 99L);
        when(comentarioRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                respuestaService.responderComentario(dto)
        );

        assertThat(exception.getMessage()).isEqualTo("El comentario no existe");
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void testResponderComentario_NoEsAnfitrion() {
        ResponderDTO dto = new ResponderDTO("Hola", 1L);

        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(2L);
        PerfilAnfitrion anfitrion = new PerfilAnfitrion();
        anfitrion.setUsuario(anfitrionUsuario);

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setAnfitrion(anfitrion);

        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setAlojamiento(alojamiento);

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));

        assertThrows(InvalidOperationException.class, () ->
                respuestaService.responderComentario(dto)
        );

        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void testResponderComentario_YaRespondido() {
        ResponderDTO dto = new ResponderDTO("Ya respondí", 1L);

        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(2L);
        PerfilAnfitrion anfitrion = new PerfilAnfitrion();
        anfitrion.setUsuario(anfitrionUsuario);

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setAnfitrion(anfitrion);

        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setAlojamiento(alojamiento);

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
        when(respuestaRepository.existsByComentarioId(1L)).thenReturn(true);

        assertThrows(InvalidOperationException.class, () ->
                respuestaService.responderComentario(dto)
        );

        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void testObtenerRespuestaPorComentario_Exitoso() {
        Long idComentario = 1L;
        Respuesta respuesta = new Respuesta();
        respuesta.setId(10L);

        RespuestaDTO dto = new RespuestaDTO(10L, idComentario, "Todo bien", null);

        when(respuestaRepository.findByComentarioId(idComentario)).thenReturn(respuesta);
        when(respuestaMapper.toDTO(respuesta)).thenReturn(dto);

        RespuestaDTO result = respuestaService.obtenerRespuestaPorComentario(idComentario);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.idComentario()).isEqualTo(idComentario);
        verify(respuestaRepository, times(1)).findByComentarioId(idComentario);
    }

    @Test
    void testObtenerRespuestaPorComentario_SinResultado() {
        Long idComentario = 999L;
        when(respuestaRepository.findByComentarioId(idComentario)).thenReturn(null);
        when(respuestaMapper.toDTO(null)).thenReturn(null);

        RespuestaDTO result = respuestaService.obtenerRespuestaPorComentario(idComentario);

        assertThat(result).isNull();
        verify(respuestaRepository, times(1)).findByComentarioId(idComentario);
    }

    @Test
    void testObtenerPorId_Exitoso() throws Exception {
        Long id = 1L;
        Respuesta respuesta = new Respuesta();
        respuesta.setId(id);

        RespuestaDTO dto = new RespuestaDTO(id, 2L, "Respuesta ok", null);

        when(respuestaRepository.findById(id)).thenReturn(Optional.of(respuesta));
        when(respuestaMapper.toDTO(respuesta)).thenReturn(dto);

        RespuestaDTO result = respuestaService.obtener(id);

        assertThat(result.id()).isEqualTo(id);
        verify(respuestaRepository, times(1)).findById(id);
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(respuestaRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                respuestaService.obtener(99L)
        );

        assertThat(exception.getMessage()).isEqualTo("La respuesta no existe");
        verify(respuestaRepository, times(1)).findById(99L);
    }
}
*/