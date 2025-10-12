package co.edu.uniquindio.Application.Controllers.PruebasIntegracion;

import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Security.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "jwt.secret=12345678901234567890123456789012")
public class ComentarioControllerPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JWTUtils jwtUtils;

    // --- Helper para generar token ---
    private String generarTokenParaUsuario(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Map<String, String> claims = Map.of(
                "email", usuario.getEmail(),
                "name", usuario.getNombre(),
                "role", "ROLE_" + usuario.getRol().name()
        );
        return jwtUtils.generateToken(usuario.getId().toString(), claims);
    }


    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioExitoso() throws Exception {
        Long reservaId = 2L; // Reserva finalizada y sin comentario
        Long usuarioId = 1L;

        ComentarDTO dto = new ComentarDTO(
                "Excelente alojamiento",
                5,
                LocalDateTime.now(),
                1L,
                usuarioId
        );

        String token = generarTokenParaUsuario(usuarioId);

        mockMvc.perform(post("/api/comentarios/{reservaId}", reservaId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").value("Comentario creado exitosamente"));

        List<Comentario> comentarios = comentarioRepository.findAll();
        assertThat(comentarios).isNotEmpty();

        Comentario guardado = comentarios.stream()
                .filter(c -> c.getReserva().getId().equals(reservaId))
                .findFirst()
                .orElseThrow();
        assertThat(guardado.getTexto()).isEqualTo("Excelente alojamiento");
        assertThat(guardado.getCalificacion()).isEqualTo(5);
        assertThat(guardado.getFecha()).isNotNull();
        assertThat(guardado.getAlojamiento()).isNotNull();
        assertThat(guardado.getReserva().getHuesped().getId()).isEqualTo(usuarioId);
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioReservaNoFinalizada() throws Exception {
        Long reservaId = 3L; // Reserva pendiente
        Long usuarioId = 1L;

        ComentarDTO dto = new ComentarDTO(
                "Intento antes del checkout",
                4,
                LocalDateTime.now(),
                1L,
                usuarioId
        );

        String token = generarTokenParaUsuario(usuarioId);

        mockMvc.perform(post("/api/comentarios/{reservaId}", reservaId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // error 400 de cliente
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearComentarioReservaYaComentada() throws Exception {
        Long reservaId = 4L; // Reserva finalizada y ya comentada
        Long usuarioId = 3L;

        ComentarDTO dto = new ComentarDTO(
                "Comentario duplicado",
                5,
                LocalDateTime.now(),
                1L,
                usuarioId
        );

        String token = generarTokenParaUsuario(usuarioId);

        mockMvc.perform(post("/api/comentarios/{reservaId}", reservaId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // error 400 de cliente
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerComentariosPorAlojamiento_Exitoso() throws Exception {
        Long idAlojamiento = 1L; //  Este alojamiento tiene comentarios
        Long usuarioId = 1L; // Usuario cualquiera para obtener el token

        String token = generarTokenParaUsuario(usuarioId);

        mockMvc.perform(get("/api/comentarios/alojamiento/{id}", idAlojamiento)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(comentarioRepository.findByAlojamientoId(idAlojamiento).size()));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerComentariosPorAlojamiento_Vacio() throws Exception {
        Long idAlojamientoSinComentarios = 99L; // No tiene comentarios
        Long usuarioId = 1L;

        String token = generarTokenParaUsuario(usuarioId);

        mockMvc.perform(get("/api/comentarios/alojamiento/{id}", idAlojamientoSinComentarios)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}*/
