package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.Anfitrion.EditarAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.Anfitrion.PerfilAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.PerfilAnfitrionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfiles-anfitrion")
public class PerfilAnfitrionController {

    private final PerfilAnfitrionService perfilAnfitrionService;

    public PerfilAnfitrionController(PerfilAnfitrionService perfilAnfitrionService) {
        this.perfilAnfitrionService = perfilAnfitrionService;
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<PerfilAnfitrionDTO>> obtenerPerfil(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), perfilAnfitrionService.obtenerPerfil(id)));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<PerfilAnfitrionDTO>>> listarPerfiles() {
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), perfilAnfitrionService.listarPerfiles()));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> actualizarPerfil(@PathVariable("id") Long id, @Valid @RequestBody EditarAnfitrionDTO dto) {
        perfilAnfitrionService.actualizarPerfil(id, dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "Perfil actualizado exitosamente"));
    }

    @PreAuthorize("hasRole('ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> eliminarPerfil(@PathVariable("id") Long id) {
        perfilAnfitrionService.eliminarPerfil(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "Perfil eliminado exitosamente"));
    }
}