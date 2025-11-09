package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.Usuario.*;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('HUESPED')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(
            @PathVariable("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("telefono") String telefono,
            @RequestPart(value = "fotoUrl", required = false) MultipartFile fotoUrl
    ) throws Exception {
        EditarUsuarioDTO usuarioDTO = new EditarUsuarioDTO(nombre, telefono, fotoUrl);
        usuarioService.edit(id, usuarioDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "El usuario ha sido actualizado"));
    }
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable("id") Long id) throws Exception{
        usuarioService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "El usuario ha sido eliminado"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> get(@PathVariable("id") Long id) throws Exception{
        UsuarioDTO usuarioDTO = usuarioService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), usuarioDTO));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> listAll(){
        List<UsuarioDTO> list = new ArrayList<>(usuarioService.listAll());
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), list));
    }

    @PreAuthorize("hasRole('HUESPED')")
    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<ResponseDTO<String>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO, @PathVariable("id") Long id) throws Exception{
        usuarioService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "Contraseña actualizada exitosamente"));
    }
}