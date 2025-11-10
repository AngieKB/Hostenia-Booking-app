package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ComentarioService {
    void comentar(Long reservaId,ComentarDTO comentarDTO) throws Exception;
    Page<ComentarioDTO> listarComentariosPorAlojamiento(Long alojamientoId, int pagina, int tamanio) throws Exception;
}
