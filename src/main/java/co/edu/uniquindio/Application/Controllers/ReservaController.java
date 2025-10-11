package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.*;
import co.edu.uniquindio.Application.DTO.Alojamiento.UbicacionDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.DTO.Usuario.UsuarioDTO;
import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reserva")

public class ReservaController {
    private final ReservaServiceImpl reservaService;

    @PreAuthorize("hasRole('HUESPED')")
    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody RealizarReservaDTO realizarReservaDTO) throws Exception {
        reservaService.guardar(realizarReservaDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido registrada"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditarReservaConUbicacionDTO dto) throws Exception {

        reservaService.editarReserva(id, dto.reserva());
        // si quieres también actualizar ubicación aquí con dto.ubicacion()
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido actualizada"));
    }


    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable("id") Long id) throws Exception{
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "La reserva ha sido cancelada"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @PutMapping("/actualizar-completadas")
    public ResponseEntity<ResponseDTO<String>> actualizarReservasCompletadas() {
        reservaService.actualizarReservasCompletadas();
        return ResponseEntity.ok(
                new ResponseDTO<>(false, "Reservas completadas actualizadas correctamente")
        );
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/mis-reservas")
    public ResponseEntity<ResponseDTO<List<ReservaUsuarioDTO>>> obtenerMisReservas() {
        List<ReservaUsuarioDTO> reservas = reservaService.obtenerMisReservas();
        return ResponseEntity.ok(new ResponseDTO<>(false, reservas));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/mis-reservas-aloja/{alojamientoId}")
    public ResponseEntity<ResponseDTO<List<ReservaAlojamientoDTO>>> obtenerMisReservasPorAlojamiento(@PathVariable("alojamientoId") Long alojamientoId) {
        List<ReservaAlojamientoDTO> reservas = reservaService.obtenerReservasPorIdAlojamiento(alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, reservas));
    }

}
