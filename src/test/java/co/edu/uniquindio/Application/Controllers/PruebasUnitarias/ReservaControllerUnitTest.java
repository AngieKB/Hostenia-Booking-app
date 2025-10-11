//package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;
//
//import co.edu.uniquindio.Application.Controllers.ReservaController;
//import co.edu.uniquindio.Application.DTO.*;
//import co.edu.uniquindio.Application.DTO.Reserva.*;
//import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static co.edu.uniquindio.Application.Model.EstadoReserva.PENDIENTE;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ReservaControllerUnitTest {
//
//    @Mock
//    private ReservaServiceImpl reservaService;
//
//    @InjectMocks
//    private ReservaController reservaController;
//
//    private RealizarReservaDTO realizarReservaDTO;
//    private EditarReservaDTO editarReservaDTO;
//    private ReservaUsuarioDTO reservaUsuarioDTO;
//    private ReservaAlojamientoDTO reservaAlojamientoDTO;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        realizarReservaDTO = new RealizarReservaDTO(
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(3),
//                2,
//                2L
//
//
//        );
//
//        editarReservaDTO = new EditarReservaDTO(
//                LocalDateTime.now().plusDays(2),
//                LocalDateTime.now().plusDays(4),
//                3,50000.0
//        );
//
//        reservaUsuarioDTO = new ReservaUsuarioDTO(
//                1L,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(2),
//                2,
//                500.0,
//                PENDIENTE,
//                "Alojamiento Playa",
//                "Cartagena"
//        );
//
//         reservaAlojamientoDTO = new ReservaAlojamientoDTO(
//                1L,                   // id
//                1L,                   // idHuesped
//                LocalDateTime.now().plusDays(5),   // fechaCheckIn
//                LocalDateTime.now().plusDays(10), // fechaCheckOut
//                2,                     // cantidadHuespedes
//                400000.0,              // total
//                PENDIENTE, // estado
//                "Alojamiento Playa",   // alojamientoTitulo
//                "Cartagena"            // alojamientoCiudad
//        );
//
//    }
//
//    // -------------------- CREATE --------------------
//    @Test
//    void createReservaExitosa() throws Exception {
//        // Arrange
//        doNothing().when(reservaService).guardar(realizarReservaDTO);
//
//        // Act
//        ResponseEntity<ResponseDTO<String>> response = reservaController.create(realizarReservaDTO);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("La reserva ha sido registrada", response.getBody().content());
//        verify(reservaService, times(1)).guardar(realizarReservaDTO);
//    }
//
//    @Test
//    void createReservaLanzaExcepcion() throws Exception {
//        // Arrange
//        doThrow(new RuntimeException("Fechas inválidas")).when(reservaService).guardar(realizarReservaDTO);
//
//        // Act & Assert
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                reservaController.create(realizarReservaDTO)
//        );
//        assertEquals("Fechas inválidas", ex.getMessage());
//        verify(reservaService, times(1)).guardar(realizarReservaDTO);
//    }
//
//    // -------------------- DELETE --------------------
//    @Test
//    void cancelarReservaExitosa() throws Exception {
//        // Arrange
//        Long id = 1L;
//        doNothing().when(reservaService).cancelarReserva(id);
//
//        // Act
//        ResponseEntity<ResponseDTO<String>> response = reservaController.delete(id);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("La reserva ha sido cancelada", response.getBody().content());
//        assertFalse(response.getBody().error());
//        verify(reservaService, times(1)).cancelarReserva(id);
//    }
//
//    @Test
//    void cancelarReservaNoExiste() throws Exception {
//        // Arrange
//        Long id = 99L;
//        doThrow(new RuntimeException("No existe una reserva con el id 99"))
//                .when(reservaService).cancelarReserva(id);
//
//        // Act & Assert
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                reservaController.delete(id)
//        );
//        assertEquals("No existe una reserva con el id 99", ex.getMessage());
//        verify(reservaService, times(1)).cancelarReserva(id);
//    }
//
//    // -------------------- EDIT --------------------
//    @Test
//    void editarReservaExitosa() throws Exception {
//        // Arrange
//        Long id = 1L;
//        EditarReservaConUbicacionDTO dtoConUbicacion = new EditarReservaConUbicacionDTO(
//                LocalDateTime.now().plusDays(2),
//                LocalDateTime.now().plusDays(4),
//                3,
//                50000.0,
//                new UbicacionDTO("Cra 10 #20-30", "Cartagena", "Colombia", 10.4, -75.5)
//        );
//
//        doNothing().when(reservaService).editarReserva(id, dtoConUbicacion.reserva());
//
//        // Act
//        ResponseEntity<ResponseDTO<String>> response = reservaController.edit(id, dtoConUbicacion);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("La reserva ha sido actualizada", response.getBody().content());
//        verify(reservaService, times(1)).editarReserva(id, dtoConUbicacion.reserva());
//    }
//
//
//    @Test
//    void editarReservaCancelada() throws Exception {
//        // Arrange
//        Long id = 1L;
//        doThrow(new RuntimeException("No se puede editar una reserva cancelada."))
//                .when(reservaService).editarReserva(id, editarReservaDTO);
//
//        // Act & Assert
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                reservaController.edit(id, editarReservaDTO, null)
//        );
//        assertEquals("No se puede editar una reserva cancelada.", ex.getMessage());
//        verify(reservaService, times(1)).editarReserva(id, editarReservaDTO);
//    }
//
//    // -------------------- ACTUALIZAR COMPLETADAS --------------------
//    @Test
//    void actualizarReservasCompletadasExitosa() {
//        // Arrange
//        doNothing().when(reservaService).actualizarReservasCompletadas();
//
//        // Act
//        ResponseEntity<ResponseDTO<String>> response = reservaController.actualizarReservasCompletadas();
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Reservas completadas actualizadas correctamente", response.getBody().content());
//        verify(reservaService, times(1)).actualizarReservasCompletadas();
//    }
//
//    // -------------------- OBTENER RESERVAS POR HUÉSPED --------------------
//    @Test
//    void obtenerReservasPorHuespedExitosa() {
//        // Arrange
//        Long idHuesped = 1L;
//
//        when(reservaService.obtenerReservasPorIdHuesped(idHuesped))
//                .thenReturn(List.of(reservaUsuarioDTO));
//
//        // Act
//        ResponseEntity<ResponseDTO<List<ReservaUsuarioDTO>>> response =
//                reservaController.obtenerMisReservas(idHuesped);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertFalse(response.getBody().error());
//        assertEquals(1, response.getBody().content().size());
//        assertEquals("Alojamiento Playa", response.getBody().content().get(0).alojamientoTitulo());
//        verify(reservaService, times(1)).obtenerReservasPorIdHuesped(idHuesped);
//    }
//
//    // -------------------- OBTENER RESERVAS POR ALOJAMIENTO --------------------
//    @Test
//    void obtenerReservasPorAlojamientoExitosa() {
//        // Arrange
//        Long idAlojamiento = 2L;
//        when(reservaService.obtenerReservasPorIdAlojamiento(idAlojamiento))
//                .thenReturn(List.of(reservaAlojamientoDTO));
//
//        // Act
//        ResponseEntity<ResponseDTO<List<ReservaAlojamientoDTO>>> response =
//                reservaController.obtenerMisReservasPorAlojamiento(idAlojamiento);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertFalse(response.getBody().error());
//        assertEquals(1, response.getBody().content().size());
//        assertEquals(1L, response.getBody().content().get(0).idHuesped());
//        verify(reservaService, times(1)).obtenerReservasPorIdAlojamiento(idAlojamiento);
//    }
//}
