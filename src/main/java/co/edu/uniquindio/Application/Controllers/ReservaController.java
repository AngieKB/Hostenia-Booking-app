package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.*;
import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.UbicacionDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.DTO.Usuario.UsuarioDTO;
import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "La reserva ha sido registrada"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditarReservaDTO dto) throws Exception {

        reservaService.editarReserva(id, dto);
        // si quieres también actualizar ubicación aquí con dto.ubicacion()
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "La reserva ha sido actualizada"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable("id") Long id) throws Exception{
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "La reserva ha sido cancelada"));
    }
    @PreAuthorize("hasRole('ANFITRION')")
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<ResponseDTO<String>> confirmar(@PathVariable("id") Long id) throws Exception{
        reservaService.confirmarReservaUsuario(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "La reserva ha sido confirmada"));
    }


    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @PutMapping("/actualizar-completadas")
    public ResponseEntity<ResponseDTO<String>> actualizarReservasCompletadas() {
        reservaService.actualizarReservasCompletadas();
        return ResponseEntity.ok(
                new ResponseDTO<>(false, HttpStatus.OK.value(), "Reservas completadas actualizadas correctamente")
        );
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/mis-reservas")
    public ResponseEntity<ResponseDTO<Page<ReservaUsuarioDTO>>> obtenerMisReservas(
            @RequestParam(name= "page", defaultValue = "0") int page,
            @RequestParam(name= "size", defaultValue = "12") int size) {

        Page<ReservaUsuarioDTO> reservas = reservaService.obtenerMisReservas(page, size);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), reservas));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/mis-reservas-aloja/{alojamientoId}")
    public ResponseEntity<ResponseDTO<Page<ReservaAlojamientoDTO>>> obtenerMisReservasPorAlojamiento(
            @PathVariable("alojamientoId") Long alojamientoId,
            @RequestParam(name= "page", defaultValue = "0") int page,
            @RequestParam(name= "size", defaultValue = "12") int size) {

        Page<ReservaAlojamientoDTO> reservas = reservaService.obtenerReservasPorIdAlojamiento(alojamientoId, page, size);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), reservas));
    }
    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservaUsuarioDTO>> obtenerPorId(@PathVariable("id") Long id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), reservaService.obtenerPorId(id)));
    }
}
