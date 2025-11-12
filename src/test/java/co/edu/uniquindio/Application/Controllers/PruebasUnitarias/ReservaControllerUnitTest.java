package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.ReservaController;
import co.edu.uniquindio.Application.DTO.*;
import co.edu.uniquindio.Application.DTO.Alojamiento.UbicacionDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static co.edu.uniquindio.Application.Model.EstadoReserva.PENDIENTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaControllerUnitTest {

    @Mock
    private ReservaServiceImpl reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private RealizarReservaDTO realizarReservaDTO;
    private EditarReservaDTO editarReservaDTO;
    private EditarReservaConUbicacionDTO editarReservaConUbicacionDTO;
    private ReservaUsuarioDTO reservaUsuarioDTO;
    private ReservaAlojamientoDTO reservaAlojamientoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        realizarReservaDTO = new RealizarReservaDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                2,
                2L
        );

        editarReservaDTO = new EditarReservaDTO(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                3
        );

        editarReservaConUbicacionDTO = new EditarReservaConUbicacionDTO(
                editarReservaDTO,
                new UbicacionDTO("Cra 10 #20-30", "Cartagena", "Colombia", 10.4, -75.5)
        );

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

        reservaAlojamientoDTO = new ReservaAlojamientoDTO(
                1L,                   // id
                1L,                   // idHuesped
                LocalDateTime.now().plusDays(5),   // fechaCheckIn
                LocalDateTime.now().plusDays(10), // fechaCheckOut
                2,                     // cantidadHuespedes
                400000.0,              // total
                PENDIENTE,             // estado
                "Alojamiento Playa",   // alojamientoTitulo
                "Cartagena"            // alojamientoCiudad
        );
    }

    // -------------------- CREATE --------------------
    @Test
    void createReservaExitosa() throws Exception {
        doNothing().when(reservaService).guardar(realizarReservaDTO);

        ResponseEntity<ResponseDTO<String>> response = reservaController.create(realizarReservaDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("La reserva ha sido registrada", response.getBody().content());
        verify(reservaService, times(1)).guardar(realizarReservaDTO);
    }

    @Test
    void createReservaLanzaExcepcion() throws Exception {
        doThrow(new RuntimeException("Fechas inválidas")).when(reservaService).guardar(realizarReservaDTO);

        Exception ex = assertThrows(RuntimeException.class, () ->
                reservaController.create(realizarReservaDTO)
        );
        assertEquals("Fechas inválidas", ex.getMessage());
        verify(reservaService, times(1)).guardar(realizarReservaDTO);
    }

    // -------------------- DELETE --------------------
    @Test
    void cancelarReservaExitosa() throws Exception {
        Long id = 1L;
        doNothing().when(reservaService).cancelarReserva(id);

        ResponseEntity<ResponseDTO<String>> response = reservaController.delete(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("La reserva ha sido cancelada", response.getBody().content());
        assertFalse(response.getBody().error());
        verify(reservaService, times(1)).cancelarReserva(id);
    }

    @Test
    void cancelarReservaNoExiste() throws Exception {
        Long id = 99L;
        doThrow(new RuntimeException("No existe una reserva con el id 99"))
                .when(reservaService).cancelarReserva(id);

        Exception ex = assertThrows(RuntimeException.class, () ->
                reservaController.delete(id)
        );
        assertEquals("No existe una reserva con el id 99", ex.getMessage());
        verify(reservaService, times(1)).cancelarReserva(id);
    }

    // -------------------- EDIT --------------------
//    @Test
//    void editarReservaExitosa() throws Exception {
//        Long id = 1L;
//        doNothing().when(reservaService).editarReserva(id, editarReservaConUbicacionDTO.reserva());
//
//        ResponseEntity<ResponseDTO<String>> response = reservaController.edit(id, editarReservaConUbicacionDTO);
//
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("La reserva ha sido actualizada", response.getBody().content());
//        verify(reservaService, times(1)).editarReserva(id, editarReservaConUbicacionDTO.reserva());
//    }

//    @Test
//    void editarReservaCancelada() throws Exception {
//        Long id = 1L;
//        doThrow(new RuntimeException("No se puede editar una reserva cancelada."))
//                .when(reservaService).editarReserva(id, editarReservaConUbicacionDTO.reserva());
//
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                reservaController.edit(id, editarReservaConUbicacionDTO)
//        );
//        assertEquals("No se puede editar una reserva cancelada.", ex.getMessage());
//        verify(reservaService, times(1)).editarReserva(id, editarReservaConUbicacionDTO.reserva());
//    }

    // -------------------- ACTUALIZAR COMPLETADAS --------------------
    @Test
    void actualizarReservasCompletadasExitosa() {
        doNothing().when(reservaService).actualizarReservasCompletadas();

        ResponseEntity<ResponseDTO<String>> response = reservaController.actualizarReservasCompletadas();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Reservas completadas actualizadas correctamente", response.getBody().content());
        verify(reservaService, times(1)).actualizarReservasCompletadas();
    }

    // -------------------- OBTENER RESERVAS POR HUÉSPED --------------------
    @Test
    void obtenerReservasPorHuespedExitosa() {
        Page<ReservaUsuarioDTO> reservasPage = new PageImpl<>(List.of(reservaUsuarioDTO), PageRequest.of(0, 12), 1);

        when(reservaService.obtenerMisReservas(0, 12)).thenReturn(reservasPage);

        ResponseEntity<ResponseDTO<Page<ReservaUsuarioDTO>>> response =
                reservaController.obtenerMisReservas(0, 12);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().error());
        assertEquals(1, response.getBody().content().getContent().size());
        assertEquals("Alojamiento Playa", response.getBody().content().getContent().get(0).alojamientoTitulo());

        verify(reservaService, times(1)).obtenerMisReservas(0, 12);
    }

    // -------------------- OBTENER RESERVAS POR ALOJAMIENTO --------------------
    @Test
    void obtenerReservasPorAlojamientoExitosa() {
        Long idAlojamiento = 2L;
        int page = 0;
        int size = 12;

        // Simulamos una página con 1 reserva
        Page<ReservaAlojamientoDTO> reservasPage = new PageImpl<>(List.of(reservaAlojamientoDTO), PageRequest.of(page, size), 1);

        when(reservaService.obtenerReservasPorIdAlojamiento(idAlojamiento, page, size)).thenReturn(reservasPage);

        ResponseEntity<ResponseDTO<Page<ReservaAlojamientoDTO>>> response =
                reservaController.obtenerMisReservasPorAlojamiento(idAlojamiento, page, size);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().error());
        assertEquals(1, response.getBody().content().getContent().size());
        assertEquals(1L, response.getBody().content().getContent().get(0).idHuesped());

        verify(reservaService, times(1)).obtenerReservasPorIdAlojamiento(idAlojamiento, page, size);
    }
}
