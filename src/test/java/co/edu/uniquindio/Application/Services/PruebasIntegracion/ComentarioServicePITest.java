/*package co.edu.uniquindio.Application.Services.PruebasIntegracion;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.NotFoundException;
import co.edu.uniquindio.Application.Exceptions.ValidationException;
import co.edu.uniquindio.Application.Model.Comentario;
import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.ReservaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.impl.ComentarioServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "jwt.secret=MiSecretoSuperSeguro123")
public class ComentarioServicePITest {

    @Autowired
    private ComentarioServiceImpl comentarioService;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioExitoso() throws Exception {
        // Reserva finalizada y sin comentario
        var reserva = reservaRepository.findById(2L).orElseThrow();
        var usuario = usuarioRepository.findById(reserva.getHuesped().getId()).orElseThrow();

        var dto = new ComentarDTO(
                "Muy buena experiencia",
                5,
                reserva.getAlojamiento().getId()
        );

        assertDoesNotThrow(() -> comentarioService.comentar(reserva.getId(), dto));

        List<Comentario> comentarios = comentarioRepository.findByAlojamientoId(reserva.getAlojamiento().getId());
        assertFalse(comentarios.isEmpty(), "El comentario no se guardó en la base de datos");

        Comentario comentario = comentarios.stream()
                .filter(c -> c.getReserva().getId().equals(reserva.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(dto.texto(), comentario.getTexto());
        assertEquals(dto.calificacion(), comentario.getCalificacion());
        assertEquals(dto.idAlojamiento(), comentario.getAlojamiento().getId());
        assertEquals(dto.idUsuario(), comentario.getReserva().getHuesped().getId());
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioReservaNoExiste() {
        var dto = new ComentarDTO(
                "No debería guardarse",
                4,
                1L
        );

        assertThrows(NotFoundException.class, () -> comentarioService.comentar(999L, dto));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioReservaNoFinalizada() {
        var reserva = reservaRepository.findById(3L).orElseThrow();
        var usuario = usuarioRepository.findById(reserva.getHuesped().getId()).orElseThrow();

        var dto = new ComentarDTO(
                "Intento comentar antes del checkout",
                3,
                LocalDateTime.now(),
                reserva.getAlojamiento().getId(),
                usuario.getId()
        );

        ValidationException exception = assertThrows(ValidationException.class,
                () -> comentarioService.comentar(reserva.getId(), dto));
        assertEquals("No puede comentar: la reserva aún no ha finalizado", exception.getMessage());
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioReservaYaComentada() {
        var reserva = reservaRepository.findById(4L).orElseThrow();
        var usuario = usuarioRepository.findById(reserva.getHuesped().getId()).orElseThrow();

        var dto = new ComentarDTO(
                "Comentario duplicado",
                5,
                reserva.getAlojamiento().getId()
        );

        assertThrows(InvalidOperationException.class, () -> comentarioService.comentar(reserva.getId(), dto));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testListarComentariosPorAlojamientoExitoso() throws Exception {
        Long idAlojamiento = 1L; //este alojamiento tiene comentarios

        List<ComentarioDTO> comentarios = comentarioService.listarComentariosPorAlojamiento(idAlojamiento);

        assertNotNull(comentarios);
        assertFalse(comentarios.isEmpty(), "Debe devolver al menos un comentario");
        comentarios.forEach(c -> {
            assertNotNull(c.texto());
            assertTrue(c.calificacion() >= 1 && c.calificacion() <= 5);
        });
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testListarComentariosPorAlojamientoVacio() throws Exception {
        Long idAlojamientoSinComentarios = 99L; //No existe el alojamiento, por tanto deberia devolverse una lista vacia

        List<ComentarioDTO> comentarios = comentarioService.listarComentariosPorAlojamiento(idAlojamientoSinComentarios);

        assertNotNull(comentarios);
        assertTrue(comentarios.isEmpty(), "No debería haber comentarios para este alojamiento");
    }
}
*/