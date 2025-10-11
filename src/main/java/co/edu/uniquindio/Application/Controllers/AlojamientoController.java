package co.edu.uniquindio.Application.Controllers;


import co.edu.uniquindio.Application.DTO.*;
import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.Services.AlojamientoService;
import co.edu.uniquindio.Application.Services.impl.AlojamientoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alojamiento")
public class AlojamientoController {

    private final AlojamientoService alojamientoService;

    @PreAuthorize("hasRole('ANFITRION')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<String>> crear(@Valid @ModelAttribute CrearAlojamientoDTO alojamientoDTO) throws Exception {
        alojamientoService.guardar(alojamientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El alojamiento ha sido registrado"));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> editar(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditarAlojamientoRequest request
    ) throws Exception {

        alojamientoService.editarAlojamiento(id, request.alojamientoDTO(), request.ubicacionDTO());
        return ResponseEntity.ok(new ResponseDTO<>(false, "El alojamiento ha sido actualizado"));
    }


    @PreAuthorize("hasRole('ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> eliminar(@PathVariable("id") Long id) throws Exception{
        alojamientoService.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDTO<>(false, "El usuario ha sido eliminado"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> obtenerPorId(@PathVariable("id") Long id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(false, alojamientoService.obtenerPorId(id)));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarTodos() throws Exception {
        List<AlojamientoDTO> list = new ArrayList<>(alojamientoService.listarTodos());
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/ciudad")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorCiudad(@RequestParam String ciudad) throws Exception {
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorCiudad(ciudad));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/fechas")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorFechas(@RequestParam LocalDateTime fechaInicio, @RequestParam LocalDateTime fechaFin) throws Exception{
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorFechas(fechaInicio,fechaFin));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/precio")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorPrecio(@RequestParam Double precioMin, @RequestParam Double precioMax) throws Exception{
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorPrecio(precioMin,precioMax));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/" +"servicios")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> buscarPorServicios(@RequestParam List<String> servicios) throws Exception{
        List<AlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorServicios(servicios));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/listarPorAnfitrion/{idAnfitrion}")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarPorAnfitrion(@PathVariable("idAnfitrion") Long idAnfitrion) throws Exception {
        List<AlojamientoDTO> list = new ArrayList<>(alojamientoService.listarPorAnfitrion(idAnfitrion));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @PostMapping("/{usuarioId}/favoritos/{alojamientoId}")
    public ResponseEntity<ResponseDTO<String>> agregarAFavoritos(
            @PathVariable Long usuarioId,
            @PathVariable Long alojamientoId) throws Exception {

        alojamientoService.agregarAFavoritos(usuarioId, alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento agregado a favoritos"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @DeleteMapping("/{usuarioId}/favoritos/{alojamientoId}")
    public ResponseEntity<ResponseDTO<String>> quitarDeFavoritos(
            @PathVariable Long usuarioId,
            @PathVariable Long alojamientoId) throws Exception {

        alojamientoService.quitarDeFavoritos(usuarioId, alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento removido de favoritos"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/{usuarioId}/favoritos")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> listarFavoritos(@PathVariable Long usuarioId) throws Exception {
        List<ResumenAlojamientoDTO> favoritos = alojamientoService.listarFavoritos(usuarioId);
        return ResponseEntity.ok(new ResponseDTO<>(false, favoritos));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/{alojamientoId}/favorito/count")
    public ResponseEntity<ResponseDTO<Integer>> contarUsuariosFavorito(@PathVariable Long alojamientoId) throws Exception {
        int cantidad = alojamientoService.contarUsuariosFavorito(alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, cantidad));
    }


}