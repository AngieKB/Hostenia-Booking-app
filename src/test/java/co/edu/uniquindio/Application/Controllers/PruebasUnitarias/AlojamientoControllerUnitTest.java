package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.AlojamientoController;
import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Model.EstadoAlojamiento;
import co.edu.uniquindio.Application.Services.AlojamientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private AlojamientoDTO alojamientoDTO;
    private UbicacionDTO ubicacionDTO;
    private CrearAlojamientoDTO crearAlojamientoDTO;
    private ResumenAlojamientoDTO resumenDTO;
    private List<MultipartFile> galeriaMultipart;
    private List<String> galeriaUrls;

    @BeforeEach
    void setUp() {
        // Ubicación
        ubicacionDTO = new UbicacionDTO("Calle 123", "Bogotá D.C.", "Colombia", 4.7, -74.1);

        // Galería
        galeriaMultipart = List.of(new MockMultipartFile("imagen1", "imagen1.jpg", "image/jpeg", new byte[]{1,2,3}));
        galeriaUrls = List.of("url1.jpg", "url2.jpg");

        // DTO que recibe el controller (multipart)
        crearAlojamientoDTO = new CrearAlojamientoDTO(
                "Hotel Test", "Descripción del hotel", List.of("WiFi", "Piscina"), galeriaMultipart,
                "Bogotá D.C.", "Calle 123", 4.7, -74.1, 150.0, 2, "Colombia"
        );

        // DTO que devuelve el service (URLs)
        alojamientoDTO = new AlojamientoDTO(
                1L, "Hotel Test", "Descripción del hotel", List.of("WiFi", "Piscina"), galeriaUrls,
                ubicacionDTO, 150.0, 2, List.of(), List.of(), EstadoAlojamiento.ACTIVO
        );

        resumenDTO = new ResumenAlojamientoDTO(
                1L, "Hotel Test", "Bogotá D.C.", 150.0, 4.5, "url1.jpg"
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
        // Crear el DTO combinado
        EditarAlojamientoRequest request = new EditarAlojamientoRequest(alojamientoDTO, ubicacionDTO);

        // Ejecutar el método del controlador
        ResponseEntity<ResponseDTO<String>> response = alojamientoController.editar(1L, request);

        // Verificar interacciones con el servicio
        verify(alojamientoService, times(1))
                .editarAlojamiento(1L, alojamientoDTO, ubicacionDTO);

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
    void listarTodosExitoso() throws Exception {
        when(alojamientoService.listarTodos()).thenReturn(List.of(alojamientoDTO));
        ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> response = alojamientoController.listarTodos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).listarTodos();
    }

    @Test
    void buscarPorCiudadExitoso() throws Exception {
        when(alojamientoService.buscarPorCiudad("Bogotá")).thenReturn(List.of(resumenDTO));
        ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> response = alojamientoController.buscarPorCiudad("Bogotá");
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).buscarPorCiudad("Bogotá");
    }

    @Test
    void buscarPorFechasExitoso() throws Exception {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(5);
        when(alojamientoService.buscarPorFechas(inicio, fin)).thenReturn(List.of(resumenDTO));
        ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> response = alojamientoController.buscarPorFechas(inicio, fin);
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).buscarPorFechas(inicio, fin);
    }

    @Test
    void buscarPorPrecioExitoso() throws Exception {
        when(alojamientoService.buscarPorPrecio(50.0, 200.0)).thenReturn(List.of(resumenDTO));
        ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> response = alojamientoController.buscarPorPrecio(50.0, 200.0);
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).buscarPorPrecio(50.0, 200.0);
    }

    @Test
    void buscarPorServiciosExitoso() throws Exception {
        when(alojamientoService.buscarPorServicios(List.of("WiFi", "Piscina"))).thenReturn(List.of(alojamientoDTO));
        ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> response = alojamientoController.buscarPorServicios(List.of("WiFi", "Piscina"));
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).buscarPorServicios(List.of("WiFi", "Piscina"));
    }

    @Test
    void listarPorAnfitrionExitoso() throws Exception {
        when(alojamientoService.listarPorAnfitrion(1L)).thenReturn(List.of(alojamientoDTO));
        ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> response = alojamientoController.listarPorAnfitrion(1L);
        assertEquals(1, response.getBody().content().size());
        verify(alojamientoService, times(1)).listarPorAnfitrion(1L);
    }
}
