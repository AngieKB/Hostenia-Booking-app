package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.RespuestaController;
import co.edu.uniquindio.Application.DTO.Comentario.ResponderDTO;
import co.edu.uniquindio.Application.DTO.Comentario.RespuestaDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.impl.RespuestaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RespuestaControllerUnitTest {

    @Mock
    private RespuestaServiceImpl respuestaService;

    @InjectMocks
    private RespuestaController respuestaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testResponderComentario_Exitoso() throws Exception {
        ResponderDTO responderDTO = new ResponderDTO("Gracias por tu comentario", 1L);

        doNothing().when(respuestaService).responderComentario(responderDTO);

        ResponseEntity<ResponseDTO<String>> response = respuestaController.responderComentario(responderDTO);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isFalse();
        assertThat(response.getBody().content()).isEqualTo("Respuesta enviada con exito");

        verify(respuestaService, times(1)).responderComentario(responderDTO);
    }

    @Test
    void testResponderComentario_Error() throws Exception {
        ResponderDTO responderDTO = new ResponderDTO("Gracias", 99L);

        doThrow(new Exception("Comentario no encontrado")).when(respuestaService).responderComentario(any());

        Exception exception = assertThrows(Exception.class, () ->
                respuestaController.responderComentario(responderDTO)
        );

        assertThat(exception.getMessage()).isEqualTo("Comentario no encontrado");
        verify(respuestaService, times(1)).responderComentario(any());
    }

    @Test
    void testObtenerRespuestaPorComentario_Exitoso() {
        Long idComentario = 1L;
        RespuestaDTO respuestaDTO = new RespuestaDTO(10L, idComentario, "Gracias por hospedarte", LocalDateTime.now());

        when(respuestaService.obtenerRespuestaPorComentario(idComentario)).thenReturn(respuestaDTO);

        ResponseEntity<ResponseDTO<RespuestaDTO>> response = respuestaController.obtenerRespuestaPorComentario(idComentario);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(Objects.requireNonNull(response.getBody()).error()).isFalse();
        assertThat(response.getBody().content().idComentario()).isEqualTo(idComentario);
        assertThat(response.getBody().content().respuesta()).isEqualTo("Gracias por hospedarte");

        verify(respuestaService, times(1)).obtenerRespuestaPorComentario(idComentario);
    }

    @Test
    void testObtenerRespuestaPorComentario_Error() {
        Long idComentario = 999L;
        when(respuestaService.obtenerRespuestaPorComentario(idComentario))
                .thenThrow(new RuntimeException("Comentario no encontrado"));

        assertThrows(RuntimeException.class, () ->
                respuestaController.obtenerRespuestaPorComentario(idComentario)
        );

        verify(respuestaService, times(1)).obtenerRespuestaPorComentario(idComentario);
    }

    @Test
    void testObtenerRespuestaPorId_Exitoso() throws Exception {
        Long id = 5L;
        RespuestaDTO respuestaDTO = new RespuestaDTO(id, 2L, "Respuesta correcta", LocalDateTime.now());

        when(respuestaService.obtener(id)).thenReturn(respuestaDTO);

        ResponseEntity<ResponseDTO<RespuestaDTO>> response = respuestaController.obtenerRespuestaPorId(id);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(Objects.requireNonNull(response.getBody()).error()).isFalse();
        assertThat(response.getBody().content().id()).isEqualTo(id);
        assertThat(response.getBody().content().respuesta()).isEqualTo("Respuesta correcta");

        verify(respuestaService, times(1)).obtener(id);
    }

    @Test
    void testObtenerRespuestaPorId_Error() throws Exception {
        Long id = 99L;
        when(respuestaService.obtener(id)).thenThrow(new Exception("Respuesta no encontrada"));

        Exception exception = assertThrows(Exception.class, () ->
                respuestaController.obtenerRespuestaPorId(id)
        );

        assertThat(exception.getMessage()).isEqualTo("Respuesta no encontrada");
        verify(respuestaService, times(1)).obtener(id);
    }
}
