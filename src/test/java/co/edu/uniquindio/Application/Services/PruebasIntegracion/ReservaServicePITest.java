//package co.edu.uniquindio.Application.Services.PruebasIntegracion;
//
//import co.edu.uniquindio.Application.DTO.Reserva.EditarReservaDTO;
//import co.edu.uniquindio.Application.DTO.Reserva.RealizarReservaDTO;
//import co.edu.uniquindio.Application.DTO.Reserva.ReservaAlojamientoDTO;
//import co.edu.uniquindio.Application.DTO.Reserva.ReservaUsuarioDTO;
//
//import co.edu.uniquindio.Application.Model.EstadoReserva;
//import co.edu.uniquindio.Application.Model.Reserva;
//import co.edu.uniquindio.Application.Repository.ReservaRepository;
//import co.edu.uniquindio.Application.Services.ReservaService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//@TestPropertySource(properties = "jwt.secret=MiSecretoSuperSeguro123")
//public class ReservaServicePITest {
//    @Autowired
//    private ReservaService reservaService;
//    @Autowired
//    private ReservaRepository reservaRepository;
//
//    @Test
//    @Sql("classpath:dataset.sql")
//    void crearReservaExitosa() throws Exception {
//        Long idHuesped = 1L;
//        Long idAlojamiento = 1L;
//
//        RealizarReservaDTO dto = new RealizarReservaDTO(
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(3),
//                2,
//                idHuesped,
//                idAlojamiento
//        );
//
//        assertDoesNotThrow(() -> reservaService.guardar(dto));
//
//        var reservas = reservaRepository.findByHuespedIdAndAlojamientoId(idHuesped, idAlojamiento);
//        assertFalse(reservas.isEmpty());
//    }
//    @Test
//    @Sql("classpath:dataset.sql")
//    void cancelarReservaExitosa() {
//        // Id de una reserva que esté a más de 48h del check-in
//        Long reservaId = 1L;
//
//        // Ejecutar la cancelación
//        reservaService.cancelarReserva(reservaId);
//
//        // Recuperar la reserva desde la base de datos para verificar
//        Reserva reserva = reservaRepository.findById(reservaId)
//                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
//
//        // Verificar que el estado cambió a CANCELADA
//        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
//    }
//
//    @Test
//    @Sql("classpath:dataset.sql")
//    void obtenerReservasPorIdHuesped_correctamente() {
//        Long huespedId = 1L; // Id de un usuario existente en dataset.sql
//
//        List<ReservaUsuarioDTO> reservasDTO = reservaService.obtenerReservasPorIdHuesped(huespedId);
//
//        // Verificar que no esté vacía
//        assertFalse(reservasDTO.isEmpty(), "La lista de reservas no debería estar vacía");
//
//        // Verificar que todas pertenezcan al huesped
//        reservasDTO.forEach(dto -> {
//            Reserva reserva = reservaRepository.findById(dto.id())
//                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
//            assertEquals(huespedId, reserva.getHuesped().getId());
//        });
//
//        // Verificar orden de más reciente a más antigua
//        for (int i = 0; i < reservasDTO.size() - 1; i++) {
//            assertTrue(
//                    reservasDTO.get(i).fechaCheckIn().isAfter(reservasDTO.get(i + 1).fechaCheckIn())
//                            || reservasDTO.get(i).fechaCheckIn().isEqual(reservasDTO.get(i + 1).fechaCheckIn()),
//                    "Las reservas no están ordenadas de más reciente a más antigua"
//            );
//        }
//
//        // Verificar mapeo de campos del DTO
//        reservasDTO.forEach(dto -> {
//            Reserva reserva = reservaRepository.findById(dto.id()).get();
//            assertEquals(reserva.getCantidadHuespedes(), dto.cantidadHuespedes());
//            assertEquals(reserva.getTotal(), dto.total());
//            assertEquals(reserva.getEstado(), dto.estado());
//            assertEquals(reserva.getAlojamiento().getTitulo(), dto.alojamientoTitulo());
//            assertEquals(reserva.getAlojamiento().getUbicacion().getCiudad(), dto.alojamientoCiudad());
//        });
//    }
//
//    @Test
//    @Sql("classpath:dataset.sql")
//    void obtenerReservasPorIdAlojamiento_correctamente() {
//        Long alojamientoId = 1L; // Id de un alojamiento existente en dataset.sql
//
//        List<ReservaAlojamientoDTO> reservasDTO = reservaService.obtenerReservasPorIdAlojamiento(alojamientoId);
//
//        // Verificar que no esté vacía
//        assertFalse(reservasDTO.isEmpty(), "La lista de reservas no debería estar vacía");
//
//        // Verificar que todas pertenezcan al alojamiento
//        reservasDTO.forEach(dto -> {
//            Reserva reserva = reservaRepository.findById(dto.id())
//                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
//            assertEquals(alojamientoId, reserva.getAlojamiento().getId());
//        });
//
//        // Verificar orden de más reciente a más antigua
//        for (int i = 0; i < reservasDTO.size() - 1; i++) {
//            assertTrue(
//                    reservasDTO.get(i).fechaCheckIn().isAfter(reservasDTO.get(i + 1).fechaCheckIn())
//                            || reservasDTO.get(i).fechaCheckIn().isEqual(reservasDTO.get(i + 1).fechaCheckIn()),
//                    "Las reservas no están ordenadas de más reciente a más antigua"
//            );
//        }
//
//        // Verificar mapeo de campos del DTO
//        reservasDTO.forEach(dto -> {
//            Reserva reserva = reservaRepository.findById(dto.id()).get();
//            assertEquals(reserva.getHuesped().getId(), dto.idHuesped());
//            assertEquals(reserva.getCantidadHuespedes(), dto.cantidadHuespedes());
//            assertEquals(reserva.getTotal(), dto.total());
//            assertEquals(reserva.getEstado(), dto.estado());
//            assertEquals(reserva.getAlojamiento().getTitulo(), dto.alojamientoTitulo());
//            assertEquals(reserva.getAlojamiento().getUbicacion().getCiudad(), dto.alojamientoCiudad());
//        });
//    }
//    @Test
//    @Sql("classpath:dataset.sql")
//    void editarReserva_exito() {
//        Long reservaId = 1L; // Id de una reserva existente
//        LocalDateTime nuevaCheckIn = LocalDateTime.now().plusDays(10);
//        LocalDateTime nuevaCheckOut = LocalDateTime.now().plusDays(15);
//        int nuevaCantidad = 3;
//        double nuevoTotal = 750.0;
//
//        EditarReservaDTO dto = new EditarReservaDTO(
//                nuevaCheckIn,
//                nuevaCheckOut,
//                nuevaCantidad,
//                nuevoTotal
//        );
//
//        try {
//            reservaService.editarReserva(reservaId, dto);
//        } catch (Exception e) {
//            // Manejar la excepción: log, mensaje al usuario, etc.
//            e.printStackTrace();
//        }
//
//
//        // Verificar cambios en la base de datos
//        Reserva reservaActualizada = reservaRepository.findById(reservaId)
//                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
//
//        assertEquals(nuevaCheckIn, reservaActualizada.getFechaCheckIn());
//        assertEquals(nuevaCheckOut, reservaActualizada.getFechaCheckOut());
//        assertEquals(nuevaCantidad, reservaActualizada.getCantidadHuespedes());
//        assertEquals(nuevoTotal, reservaActualizada.getTotal());
//    }
//}
//
