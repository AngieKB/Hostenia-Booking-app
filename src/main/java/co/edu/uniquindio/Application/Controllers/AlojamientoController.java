package co.edu.uniquindio.Application.Controllers;


import co.edu.uniquindio.Application.DTO.*;
import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.Services.AlojamientoService;
import co.edu.uniquindio.Application.Services.impl.AlojamientoServiceImpl;
import co.edu.uniquindio.Application.Services.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final AuthService authService;

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

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> obtenerPorId(@PathVariable("id") Long id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(false, alojamientoService.obtenerPorId(id)));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> listarTodos() throws Exception {
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.listarTodos());
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/{id}/metricas")
    public ResponseEntity<ResponseDTO<MetricasDTO>> verMetricas(
            @PathVariable("id") Long id,
            @RequestParam("fechaMin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaMin,
            @RequestParam("fechaMax") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaMax
    ) throws Exception {
        MetricasDTO metricas = alojamientoService.verMetricas(id, fechaMin, fechaMax);
        return ResponseEntity.ok(new ResponseDTO<>(false, metricas));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/ciudad")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorCiudad(@RequestParam("ciudad") String ciudad) throws Exception {
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorCiudad(ciudad));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/fechas")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorFechas(@RequestParam("fechaInicio") LocalDateTime fechaInicio, @RequestParam("fechaFin") LocalDateTime fechaFin) throws Exception{
        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorFechas(fechaInicio,fechaFin));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/precio")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> buscarPorPrecio(
            @RequestParam("precioMin") Double precioMin,
            @RequestParam("precioMax") Double precioMax) throws Exception {

        List<ResumenAlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorPrecio(precioMin, precioMax));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/buscar/servicios")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> buscarPorServicios(
            @RequestParam("servicios") List<String> servicios) throws Exception {

        List<AlojamientoDTO> list = new ArrayList<>(alojamientoService.buscarPorServicios(servicios));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/listarPorAnfitrion/{idAnfitrion}")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarPorAnfitrion(
            @PathVariable("idAnfitrion") Long idAnfitrion) throws Exception {

        List<AlojamientoDTO> list = new ArrayList<>(alojamientoService.listarPorAnfitrion(idAnfitrion));
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @PostMapping("/favoritos/{alojamientoId}")
    public ResponseEntity<ResponseDTO<String>> agregarAFavoritos(
            @PathVariable("alojamientoId") Long alojamientoId) throws Exception {

        Long usuarioId = authService.getUsuarioAutenticado().getId(); // <-- obtenemos el id desde el token
        alojamientoService.agregarAFavoritos(usuarioId, alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento agregado a favoritos"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @DeleteMapping("/favoritos/{alojamientoId}")
    public ResponseEntity<ResponseDTO<String>> quitarDeFavoritos(
            @PathVariable("alojamientoId") Long alojamientoId) throws Exception {

        Long usuarioId = authService.getUsuarioAutenticado().getId();
        alojamientoService.quitarDeFavoritos(usuarioId, alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento removido de favoritos"));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/favoritos")
    public ResponseEntity<ResponseDTO<List<ResumenAlojamientoDTO>>> listarFavoritos() throws Exception {

        Long usuarioId = authService.getUsuarioAutenticado().getId();
        List<ResumenAlojamientoDTO> favoritos = alojamientoService.listarFavoritos(usuarioId);
        return ResponseEntity.ok(new ResponseDTO<>(false, favoritos));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @GetMapping("/{alojamientoId}/favorito/count")
    public ResponseEntity<ResponseDTO<Integer>> contarUsuariosFavorito(
            @PathVariable("alojamientoId") Long alojamientoId) throws Exception {

        int cantidad = alojamientoService.contarUsuariosFavorito(alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, cantidad));
    }
}