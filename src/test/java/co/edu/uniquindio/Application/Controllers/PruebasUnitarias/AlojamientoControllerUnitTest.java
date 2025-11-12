package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.AlojamientoController;
import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.DTO.Reserva.ReservaDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Model.EstadoAlojamiento;
import co.edu.uniquindio.Application.Model.EstadoReserva;
import co.edu.uniquindio.Application.Services.AlojamientoService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlojamientoControllerUnitTest {

    @Mock
    private AlojamientoService alojamientoService;

    @InjectMocks
    private AlojamientoController alojamientoController;
    private ComentarioDTO comentarioDTO;
    private ReservaDTO reservaDTO;
    private AlojamientoDTO alojamientoDTO;
    private EditarAlojamientoDTO editarAlojamientoDTO;
    private UbicacionDTO ubicacionDTO;
    private CrearAlojamientoDTO crearAlojamientoDTO;
    private List<MultipartFile> galeriaMultipart;
    private LocalDateTime fechaMin;
    private LocalDateTime fechaMax;
    private MetricasDTO metricasDTO;


    @BeforeEach
    void setUp() {
        fechaMin = LocalDateTime.now().minusDays(10);
        fechaMax = LocalDateTime.now();
        metricasDTO = new MetricasDTO(3.0, 50);
        // Ubicación
        ubicacionDTO = new UbicacionDTO("Calle 123", "Bogotá D.C.", "Colombia", 4.7, -74.1);

        // Galería
        galeriaMultipart = List.of(new MockMultipartFile("imagen1", "imagen1.jpg", "image/jpeg", new byte[]{1, 2, 3}));

        // DTO que recibe el controller (multipart)
        crearAlojamientoDTO = new CrearAlojamientoDTO(
                "Hotel Test", "Descripción del hotel", List.of("WiFi", "Piscina"), galeriaMultipart,
                "Bogotá D.C.", "Calle 123", 4.7, -74.1, 150.0, 2, "Colombia"
        );

        comentarioDTO = new ComentarioDTO(1L, "Escelente atención, increíble", 4, LocalDateTime.now());
        reservaDTO = new ReservaDTO(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), 1, 300.0, EstadoReserva.PENDIENTE);

        alojamientoDTO = new AlojamientoDTO(
                1L,
                "Cabaña en las montañas",
                "Una cabaña acogedora con vista al bosque y chimenea incluida.",
                List.of("WiFi", "Chimenea", "Parqueadero", "Cocina equipada"),
                null,
                ubicacionDTO,
                150.00,
                4,
                List.of(comentarioDTO),
                List.of(reservaDTO),
                EstadoAlojamiento.ACTIVO,
                1L,
                "Juan Pérez"
        );

        editarAlojamientoDTO = new EditarAlojamientoDTO(
                "Cabaña en las montañas",
                "Una cabaña acogedora con vista al bosque y chimenea incluida.",
                List.of("WiFi", "Chimenea", "Parqueadero", "Cocina equipada"),
                null,
                "Armenia",
                "Calle 45 #23-56",
                23.5667,
                -87.1234,
                150000.0,
                4
        );
    }


    @Test
    void crearAlojamientoExitoso() throws Exception {
        ResponseEntity<ResponseDTO<String>> response = alojamientoController.crear(crearAlojamientoDTO);
        verify(alojamientoService, times(1)).guardar(crearAlojamientoDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertFalse(response.getBody().error());
    }

    @Test
    void editarAlojamientoExitoso() throws Exception {
        // Crear DTOs simulados
        EditarAlojamientoDTO editarAlojamientoDTO = new EditarAlojamientoDTO(
                "Cabaña en las montañas",
                "Hermosa cabaña con vista al lago",
                List.of("WiFi", "Piscina", "Cocina equipada"),
                List.of(),
                "Medellín",
                "Calle 123 #45-67",
                6.25184,
                -75.56359,
                250000.0,
                6
        );

        UbicacionDTO ubicacionDTO = new UbicacionDTO(
                "Ciudad Actualizada",
                "Calle 123",
                "Colombia",
                4.567,
                -74.123
        );

        // Simular respuesta esperada del servicio (no devuelve nada)
        doNothing().when(alojamientoService).editarAlojamiento(1L, editarAlojamientoDTO, ubicacionDTO);

        // Ejecutar el método del controlador
        ResponseEntity<ResponseDTO<String>> response = alojamientoController.editar(1L, editarAlojamientoDTO, ubicacionDTO);

        // Verificar interacciones con el servicio
        verify(alojamientoService, times(1))
                .editarAlojamiento(1L, editarAlojamientoDTO, ubicacionDTO);

        // Verificar respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().error());
        assertEquals("El alojamiento ha sido actualizado", response.getBody().content());
    }


    @Test
    void eliminarAlojamientoExitoso() throws Exception {
        ResponseEntity<ResponseDTO<String>> response = alojamientoController.eliminar(1L);
        verify(alojamientoService, times(1)).eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(response.getBody().error());
    }

    @Test
    void obtenerPorIdExitoso() throws Exception {
        when(alojamientoService.obtenerPorId(1L)).thenReturn(alojamientoDTO);
        ResponseEntity<ResponseDTO<AlojamientoDTO>> response = alojamientoController.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alojamientoDTO, response.getBody().content());
        verify(alojamientoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void verMetricasExitosa() throws Exception {
        // Arrange
        Long idAlojamiento = 1L;
        when(alojamientoService.verMetricas(idAlojamiento, fechaMin, fechaMax))
                .thenReturn(metricasDTO);

        // Act
        ResponseEntity<ResponseDTO<MetricasDTO>> response =
                alojamientoController.verMetricas(idAlojamiento, fechaMin, fechaMax);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().error());
        assertEquals(metricasDTO, response.getBody().content());
        verify(alojamientoService, times(1)).verMetricas(idAlojamiento, fechaMin, fechaMax);
    }

    @Test
    void verMetricasAlojamientoNoExiste() throws Exception {
        // Arrange
        Long idAlojamiento = 99L;
        when(alojamientoService.verMetricas(idAlojamiento, fechaMin, fechaMax))
                .thenThrow(new RuntimeException("Alojamiento no encontrado"));

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () ->
                alojamientoController.verMetricas(idAlojamiento, fechaMin, fechaMax)
        );
        assertEquals("Alojamiento no encontrado", ex.getMessage());
        verify(alojamientoService, times(1)).verMetricas(idAlojamiento, fechaMin, fechaMax);
    }

    @Test
    void listarTodosExitoso() throws Exception {
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));
        when(alojamientoService.listarTodos(anyInt(), anyInt())).thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response = alojamientoController.listarTodos(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().error());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1)).listarTodos(anyInt(), anyInt());
    }

    @Test
    void buscarPorCiudadExitoso() throws Exception {
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));
        when(alojamientoService.buscarPorCiudad(eq("Bogotá"), anyInt(), anyInt())).thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response =
                alojamientoController.buscarPorCiudad("Bogotá", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1))
                .buscarPorCiudad(eq("Bogotá"), anyInt(), anyInt());
    }

    @Test
    void buscarPorFechasExitoso() throws Exception {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(5);
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));

        when(alojamientoService.buscarPorFechas(eq(inicio), eq(fin), anyInt(), anyInt()))
                .thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response =
                alojamientoController.buscarPorFechas(inicio, fin, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1))
                .buscarPorFechas(eq(inicio), eq(fin), anyInt(), anyInt());
    }

    @Test
    void buscarPorPrecioExitoso() throws Exception {
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));
        when(alojamientoService.buscarPorPrecio(eq(50.0), eq(200.0), anyInt(), anyInt()))
                .thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response =
                alojamientoController.buscarPorPrecio(50.0, 200.0, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1))
                .buscarPorPrecio(eq(50.0), eq(200.0), anyInt(), anyInt());
    }

    @Test
    void buscarPorServiciosExitoso() throws Exception {
        List<String> servicios = List.of("WiFi", "Piscina");
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));

        when(alojamientoService.buscarPorServicios(eq(servicios), anyInt(), anyInt()))
                .thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response =
                alojamientoController.buscarPorServicios(servicios, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1))
                .buscarPorServicios(eq(servicios), anyInt(), anyInt());
    }

    @Test
    void listarPorAnfitrionExitoso() throws Exception {
        Page<AlojamientoDTO> page = new PageImpl<>(List.of(alojamientoDTO));
        when(alojamientoService.listarPorAnfitrion(eq(1L), anyInt(), anyInt()))
                .thenReturn(page);

        ResponseEntity<ResponseDTO<Page<AlojamientoDTO>>> response =
                alojamientoController.listarPorAnfitrion(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().getContent().size());
        verify(alojamientoService, times(1))
                .listarPorAnfitrion(eq(1L), anyInt(), anyInt());
    }
}