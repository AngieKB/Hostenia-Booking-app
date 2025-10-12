package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.ComentarioController;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.impl.ComentarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ComentarioControllerUnitTest {

    @Mock
    private ComentarioServiceImpl comentarioService;

    @InjectMocks
    private ComentarioController comentarioController;

    private ComentarDTO comentarDTO;

    @BeforeEach
    void setUp() {
        comentarDTO = new ComentarDTO(
                "Excelente alojamiento",
                5,
                1L
        );
    }

    @Test
    void testCrearComentarioExitoso() throws Exception {
        // No lanza excepciones → caso exitoso
        doNothing().when(comentarioService).comentar(any(Long.class), any(ComentarDTO.class));

        ResponseEntity<ResponseDTO<String>> response =
                comentarioController.crearComentario(1L, comentarDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().error());
        assertEquals("Comentario creado exitosamente", response.getBody().content());

        verify(comentarioService, times(1)).comentar(1L, comentarDTO);
    }

    @Test
    void testCrearComentarioLanzaExcepcion() throws Exception {
        doThrow(new Exception("Error al comentar"))
                .when(comentarioService).comentar(any(Long.class), any(ComentarDTO.class));

        Exception exception = assertThrows(Exception.class, () ->
                comentarioController.crearComentario(1L, comentarDTO)
        );

        assertEquals("Error al comentar", exception.getMessage());
        verify(comentarioService, times(1)).comentar(1L, comentarDTO);
    }

    @Test
    void testListarComentariosPorAlojamientoExitoso() throws Exception {
        var comentarioDTO = new co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO(
                1L, "Todo bien", 5, LocalDateTime.now()
        );

        when(comentarioService.listarComentariosPorAlojamiento(1L))
                .thenReturn(List.of(comentarioDTO));

        ResponseEntity<ResponseDTO<List<co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO>>> response =
                comentarioController.obtenerComentariosPorAlojamiento(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().error());
        assertEquals(1, response.getBody().content().size());
        assertEquals("Todo bien", response.getBody().content().get(0).texto());

        verify(comentarioService, times(1)).listarComentariosPorAlojamiento(1L);
    }

    @Test
    void testListarComentariosPorAlojamientoError() throws Exception {
        when(comentarioService.listarComentariosPorAlojamiento(1L))
                .thenThrow(new Exception("Error al listar"));

        Exception exception = assertThrows(Exception.class, () ->
                comentarioController.obtenerComentariosPorAlojamiento(1L)
        );

        assertEquals("Error al listar", exception.getMessage());
        verify(comentarioService, times(1)).listarComentariosPorAlojamiento(1L);
    }
}
