package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.DTO.Comentario.ResponderDTO;
import co.edu.uniquindio.Application.DTO.Comentario.RespuestaDTO;
import co.edu.uniquindio.Application.DTO.EmailDTO;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Mappers.RespuestaMapper;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.RespuestaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.EmailService;
import co.edu.uniquindio.Application.Services.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RespuestaServiceImpl implements RespuestaService {
    private final RespuestaRepository respuestaRepository;
    private final RespuestaMapper respuestaMapper;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final AuthService authService;

    @Override
    public void responderComentario(ResponderDTO dto) throws Exception {
        // Validar que el comentario exista
        Comentario comentario = comentarioRepository.findById(dto.idComentario())
                .orElseThrow(() -> new ResourceNotFoundException("El comentario no existe"));

        Alojamiento alojamiento = comentario.getAlojamiento();
        Usuario anfitrion = authService.getUsuarioAutenticado();

        // verifificar que el usuario que responde es el anfitrion del alojamiento
        if(!anfitrion.getId().equals(alojamiento.getAnfitrion().getUsuario().getId())) {
            throw new InvalidOperationException("Solo el anfitrión del alojamiento puede responder comentarios.");
        }

        // Verificar que el comentario no tenga ya una respuesta
        if(respuestaRepository.existsByComentarioId(dto.idComentario())) {
            throw new InvalidOperationException("El comentario ya ha sido respondido.");
        }

        Respuesta respuesta = respuestaMapper.toEntity(dto);
        respuesta.setComentario(comentario);
        respuesta.setAnfitrion(anfitrion);
        respuestaRepository.save(respuesta);

        Usuario huesped = comentario.getReserva().getHuesped();

        emailService.sendMail(
                new EmailDTO("Han respondido al comentario que dejaste en el alojamiento: " + comentario.getAlojamiento().getTitulo(),
                        "El anfitrión " + anfitrion.getNombre() +
                        " respondio a tu comentario \n" + comentario.getTexto() +
                        "\nCon la siguiente respuesta: " + respuesta.getRespuesta(),
                        huesped.getEmail())
        );
        emailService.sendMail(
                new EmailDTO("Has respondido al comentario de: " + huesped.getNombre(),
                        "Has respondio al comentario \n" + comentario.getTexto() +
                                "\nCon la siguiente respuesta: " + respuesta.getRespuesta(),
                        anfitrion.getEmail())
        );
    }

    @Override
    public RespuestaDTO obtenerRespuestaPorComentario(Long idComentario) {
        return respuestaMapper.toDto(respuestaRepository.findByComentarioId(idComentario));
    }

    @Override
    public RespuestaDTO obtener(Long id) throws Exception {
        return respuestaMapper.toDto(
                respuestaRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("La respuesta no existe"))
        );
    }
}