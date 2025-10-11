package co.edu.uniquindio.Application.Services.PruebasUnitarias;

import co.edu.uniquindio.Application.DTO.EmailDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Mappers.ReservaMapper;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.ReservaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.EmailService;
import co.edu.uniquindio.Application.Services.impl.AuthService;
import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceUnitTest {

    @Mock private ReservaMapper reservaMapper;
    @Mock private ReservaRepository reservaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AlojamientoRepository alojamientoRepository;
    @Mock private EmailService emailService;
    @Mock private AuthService authService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Usuario huesped;
    private Alojamiento alojamiento;
    private Reserva reserva;
    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        huesped = new Usuario();
        huesped.setId(1L);
        huesped.setNombre("Juan");
        huesped.setEmail("juan@mail.com");

        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(2L);
        anfitrionUsuario.setEmail("anfitrion@mail.com");

        PerfilAnfitrion perfilAnfitrion = new PerfilAnfitrion();
        perfilAnfitrion.setUsuario(anfitrionUsuario);

        ubicacion = new Ubicacion();
        ubicacion.setCiudad("Armenia");
        ubicacion.setPais("Colombia");
        ubicacion.setLatitud(4.5339);
        ubicacion.setLongitud(-75.6811);

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setTitulo("Cabaña del Bosque");
        alojamiento.setCapacidadMax(4);
        alojamiento.setPrecioNoche(100.0);
        alojamiento.setAnfitrion(perfilAnfitrion);
        alojamiento.setUbicacion(ubicacion);

        reserva = new Reserva();
        reserva.setId(99L);
        reserva.setHuesped(huesped);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaCheckIn(LocalDateTime.now().plusDays(3));
        reserva.setFechaCheckOut(LocalDateTime.now().plusDays(5));
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCantidadHuespedes(2);
        reserva.setTotal(200.0); // Total calculado como 2 noches * 100 * 1 huésped
    }

    // === TEST CANCELAR RESERVA EXITOSO ===
    @Test
    void testCancelarReservaExitosa() throws Exception {
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        doNothing().when(emailService).sendMail(any(EmailDTO.class));

        assertDoesNotThrow(() -> reservaService.cancelarReserva(reserva.getId()));

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        verify(reservaRepository).save(reserva);
        verify(emailService, times(2)).sendMail(any(EmailDTO.class));
    }

    @Test
    void testCancelarReservaYaCancelada() {
        reserva.setEstado(EstadoReserva.CANCELADA);
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.cancelarReserva(reserva.getId()));
        assertEquals("La reserva ya se encuentra cancelada.", ex.getMessage());
    }

    @Test
    void testCancelarReservaMenosDe48Horas() {
        reserva.setFechaCheckIn(LocalDateTime.now().plusHours(30));
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.cancelarReserva(reserva.getId()));
        assertTrue(ex.getMessage().contains("No se puede cancelar la reserva"));
    }

    @Test
    void testCancelarReservaNoExiste() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> reservaService.cancelarReserva(999L));
        assertEquals("No existe una reserva con el id 999", ex.getMessage());
    }

    // === TEST GUARDAR RESERVA EXITOSO ===
    @Test
    void testGuardarReservaExitosa() throws Exception {
        RealizarReservaDTO dto = new RealizarReservaDTO(
                reserva.getFechaCheckIn(),
                reserva.getFechaCheckOut(),
                reserva.getCantidadHuespedes(),
                alojamiento.getId()
        );

        when(authService.getUsuarioAutenticado()).thenReturn(huesped);
        when(usuarioRepository.findById(huesped.getId())).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        when(reservaMapper.toEntity(dto)).thenReturn(reserva);
        doNothing().when(emailService).sendMail(any(EmailDTO.class));

        assertDoesNotThrow(() -> reservaService.guardar(dto));

        verify(reservaRepository).save(reserva);
        verify(emailService, times(2)).sendMail(any(EmailDTO.class));
    }

    @Test
    void testGuardarReservaFechasPasadas() {
        RealizarReservaDTO dto = new RealizarReservaDTO(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                2,
                alojamiento.getId()
        );

        when(authService.getUsuarioAutenticado()).thenReturn(huesped);
        when(usuarioRepository.findById(huesped.getId())).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.guardar(dto));
        assertEquals("No se pueden reservar fechas pasadas.", ex.getMessage());
    }

    @Test
    void testGuardarReservaMenosDeUnaNoche() {
        RealizarReservaDTO dto = new RealizarReservaDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusHours(12),
                2,
                alojamiento.getId()
        );

        when(authService.getUsuarioAutenticado()).thenReturn(huesped);
        when(usuarioRepository.findById(huesped.getId())).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.guardar(dto));
        assertEquals("La reserva debe ser mínimo de 1 noche.", ex.getMessage());
    }

    @Test
    void testGuardarReservaSuperaCapacidad() {
        RealizarReservaDTO dto = new RealizarReservaDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                10,
                alojamiento.getId()
        );
        alojamiento.setCapacidadMax(5);

        when(authService.getUsuarioAutenticado()).thenReturn(huesped);
        when(usuarioRepository.findById(huesped.getId())).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.guardar(dto));
        assertEquals("Se supera la capacidad máxima del alojamiento.", ex.getMessage());
    }

    @Test
    void testEditarReservaExitosa() {
        EditarReservaDTO dto = new EditarReservaDTO(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                3,
                500.0
        );

        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        when(authService.getUsuarioAutenticado()).thenReturn(huesped);

        assertDoesNotThrow(() -> reservaService.editarReserva(reserva.getId(), dto));

        verify(reservaMapper).updateReservaFromDTO(dto, reserva);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testEditarReservaCancelada() {
        reserva.setEstado(EstadoReserva.CANCELADA);
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> reservaService.editarReserva(reserva.getId(), new EditarReservaDTO(null, null, null, null)));
        assertTrue(ex.getMessage().contains("cancelada"));
    }

    @Test
    void testObtenerReservasPorHuesped() {
        when(reservaRepository.findByHuespedId(huesped.getId())).thenReturn(List.of(reserva));
        when(authService.getUsuarioAutenticado()).thenReturn(huesped);

        ReservaUsuarioDTO dto = new ReservaUsuarioDTO(
                reserva.getId(),
                reserva.getFechaCheckIn(),
                reserva.getFechaCheckOut(),
                reserva.getCantidadHuespedes(),
                reserva.getTotal(),
                reserva.getEstado(),
                reserva.getAlojamiento().getTitulo(),
                reserva.getAlojamiento().getUbicacion().getCiudad()
        );

        when(reservaMapper.toUsuarioDTO(reserva)).thenReturn(dto);

        List<ReservaUsuarioDTO> result = reservaService.obtenerMisReservas();
        assertEquals(1, result.size());
        assertEquals("Cabaña del Bosque", result.get(0).alojamientoTitulo());
    }

    @Test
    void testObtenerReservasPorAlojamiento() {
        when(reservaRepository.findByAlojamientoId(alojamiento.getId())).thenReturn(List.of(reserva));

        ReservaAlojamientoDTO dto = new ReservaAlojamientoDTO(
                reserva.getId(),
                huesped.getId(),
                reserva.getFechaCheckIn(),
                reserva.getFechaCheckOut(),
                reserva.getCantidadHuespedes(),
                reserva.getTotal(),
                reserva.getEstado(),
                alojamiento.getTitulo(),
                ubicacion.getCiudad()
        );

        when(reservaMapper.toAlojamientoDTO(reserva)).thenReturn(dto);

        List<ReservaAlojamientoDTO> result = reservaService.obtenerReservasPorIdAlojamiento(alojamiento.getId());
        assertEquals(1, result.size());
        assertEquals(huesped.getId(), result.get(0).idHuesped());
    }
}
