package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.Comentario.ResponderDTO;
import co.edu.uniquindio.Application.DTO.Comentario.RespuestaDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.impl.RespuestaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/respuestas")
public class RespuestaController {
    private final RespuestaServiceImpl respuestaService;

    @PreAuthorize("hasRole('ANFITRION')")
    @PostMapping
    public ResponseEntity<ResponseDTO<String>> responderComentario(@RequestBody @Valid ResponderDTO responderDTO) throws Exception {
        respuestaService.responderComentario(responderDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(),"Respuesta enviada con exito"));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping("/comentario/{idComentario}")
    public ResponseEntity<ResponseDTO<RespuestaDTO>> obtenerRespuestaPorComentario(@PathVariable("idComentario") Long idComentario) {
        RespuestaDTO respuestaDTO = respuestaService.obtenerRespuestaPorComentario(idComentario);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), respuestaDTO));
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<RespuestaDTO>> obtenerRespuestaPorId(@PathVariable("id") Long id) throws Exception {
        RespuestaDTO respuestaDTO = respuestaService.obtener(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, HttpStatus.OK.value(), respuestaDTO));
    }
}