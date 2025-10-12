package co.edu.uniquindio.Application.Services.PruebasIntegracion;

import co.edu.uniquindio.Application.DTO.Usuario.CrearUsuarioDTO;
import co.edu.uniquindio.Application.Exceptions.ValueConflictException;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.UsuarioService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
/*
@SpringBootTest
@Transactional
@TestPropertySource(properties = "jwt.secret=MiSecretoSuperSeguro123")
public class UsuarioServicePITest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private  UsuarioRepository usuarioRepository;

    //por el momento las images las estoy poniendo así, pero luego miramos como mejorarlo
    @Test
    @Sql("classpath:dataset.sql")
    void testCrearUsuarioExitoso() throws IOException {
        Path path = Paths.get("src/test/resources/foto-test.jpg");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile foto = new MockMultipartFile(
                "foto",
                "foto.jpg",
                "image/jpeg",
                content
        );
        var nuevo = new CrearUsuarioDTO(
                "Carlos",
                "carlos@email.com",
                "123456789",
                "Password123",
                LocalDate.of(1990, 1, 1),
                foto
        );

        assertDoesNotThrow(() -> usuarioService.create(nuevo));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testCrearUsuario_EmailExistente() throws Exception {
        Path path = Paths.get("src/test/resources/foto-test.jpg");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile foto = new MockMultipartFile(
                "foto",
                "foto.jpg",
                "image/jpeg",
                content
        );
        var existente = usuarioRepository.findById(1L).orElseThrow();
        var duplicado = new CrearUsuarioDTO(
                "Pepe",
                existente.getEmail(),
                "987654321",
                "Pass321",
                LocalDate.of(1995, 5, 5),
                foto
        );

        assertThrows(ValueConflictException.class, () -> usuarioService.create(duplicado));
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerUsuarioPorIdExitoso() throws Exception {
        var usuarioGuardado = usuarioRepository.findById(1L).orElseThrow();
        var usuarioEncontrado = usuarioService.get(usuarioGuardado.getId());

        assertEquals(usuarioGuardado.getNombre(), usuarioEncontrado.nombre());
        assertEquals(usuarioGuardado.getEmail(), usuarioEncontrado.email());
    }

    @Test
    @Sql("classpath:dataset.sql")
    void testObtenerUsuarioPorIdNoExitoso() {
        assertThrows(Exception.class, () -> usuarioService.get(999L));
    }
}*/
