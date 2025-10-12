package co.edu.uniquindio.Application.Controllers.PruebasIntegracion;

import co.edu.uniquindio.Application.DTO.Usuario.ChangePasswordDTO;
import co.edu.uniquindio.Application.DTO.Usuario.EditarUsuarioDTO;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UsuarioControllerPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @Sql("classpath:dataset.sql")
    void testEditarUsuarioExitoso() throws Exception {
        Path path = Paths.get("src/test/resources/foto-test.jpg");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile foto = new MockMultipartFile(
                "fotoUrl",
                "foto.jpg",
                "image/jpeg",
                content
        );

        EditarUsuarioDTO editarUsuarioDTO = new EditarUsuarioDTO(
                "NuevoNombre",
                "1234567890",
                foto
        );

        mockMvc.perform(multipart("/api/usuario/{id}", 1L)
                        .file(foto)
                        .param("nombre", "NuevoNombre")
                        .param("telefono", "1234567890")
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("El usuario ha sido actualizado"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testDeleteUsuarioExitoso() throws Exception {
        mockMvc.perform(delete("/api/usuario/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("El usuario ha sido eliminado"));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerUsuarioPorIdExitoso() throws Exception {
        mockMvc.perform(get("/api/usuario/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerTodosLosUsuariosExitoso() throws Exception {
        mockMvc.perform(get("/api/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testChangePasswordExitoso() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("pass123", "NuevaPass123");

        mockMvc.perform(put("/api/usuario/{id}/cambiar-password", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Contraseña actualizada exitosamente"));
    }

}*/
