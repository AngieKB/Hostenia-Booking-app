package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.impl.ComentarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comentarios")
public class ComentarioController {
    private final ComentarioServiceImpl comentarioService;

    @PreAuthorize("hasRole('HUESPED')")
    @PostMapping("/{reservaId}")
    public ResponseEntity<ResponseDTO<String>> crearComentario(@PathVariable("reservaId") Long reservaId, @RequestBody ComentarDTO comentarDto) throws Exception{
        comentarioService.comentar(reservaId, comentarDto);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), "Comentario creado exitosamente"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping("/alojamiento/{idAlojamiento}")
    public ResponseEntity<ResponseDTO<Page<ComentarioDTO>>> obtenerComentariosPorAlojamiento(@PathVariable("idAlojamiento") Long idAlojamiento,
           @RequestParam(name = "pagina", defaultValue = "0") int pagina,
           @RequestParam(name = "tamanio", defaultValue = "12") int tamanio) throws Exception{
        Page<ComentarioDTO> comentarios = comentarioService.listarComentariosPorAlojamiento(idAlojamiento, pagina, tamanio);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), comentarios));
    }
}