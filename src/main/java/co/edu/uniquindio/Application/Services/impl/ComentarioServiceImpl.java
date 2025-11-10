package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.DTO.EmailDTO;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.NotFoundException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Exceptions.ValidationException;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.ComentarioRepository;
import co.edu.uniquindio.Application.Repository.ReservaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.ComentarioService;
import co.edu.uniquindio.Application.Mappers.ComentarioMapper;
import co.edu.uniquindio.Application.Services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final ComentarioMapper comentarioMapper;
    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;


    @Override
    public void comentar(Long reservaId, ComentarDTO comentarDTO) throws Exception {
        Usuario usuario = authService.getUsuarioAutenticado();

        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("La reserva no existe"));

        // 2. Validar que pertenezca al huésped y al alojamiento correctos
        if (!reserva.getHuesped().getId().equals(usuario.getId()) ||
                !reserva.getAlojamiento().getId().equals(comentarDTO.idAlojamiento())) {
            throw new InvalidOperationException("La reserva no corresponde al usuario o al alojamiento indicado");
        }

        // 3. Validar que la reserva esté completada
        if (reserva.getFechaCheckOut().isAfter(LocalDateTime.now())) {
            throw new ValidationException("No puede comentar: la reserva aún no ha finalizado");
        }

        // 4. Validar que no exista ya comentario para esa reserva
        if (comentarioRepository.existsByReservaId(reservaId)) {
            throw new InvalidOperationException("Ya ha comentado esta reserva");
        }

        // 5. Crear y guardar comentario
        Comentario comentario = comentarioMapper.toEntity(comentarDTO);
        comentario.setHuesped(usuario);
        comentario.setReserva(reserva);
        comentarioRepository.save(comentario);

        // 6. Actualizar promedio de calificación del alojamiento
        Alojamiento alojamiento = reserva.getAlojamiento();

        double promedio = comentarioRepository.findByAlojamientoId(alojamiento.getId())
                .stream()
                .mapToInt(Comentario::getCalificacion)
                .average()
                .orElse(0);

//        Usuario usuario = usuarioRepository.findById(comentarDTO.idUsuario()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));


        alojamiento.setCalificacionPromedio(promedio);
        alojamientoRepository.save(alojamiento);
        emailService.sendMail(
                new EmailDTO("Nuevo comentario en: " + alojamiento.getTitulo(),
                        "El usuario " + usuario.getNombre() + " realizó un comentario en el alojamiento " + alojamiento.getTitulo(),
                        alojamiento.getAnfitrion().getUsuario().getEmail())
        );
        emailService.sendMail(
                new EmailDTO("Has hecho un comentario en: " + alojamiento.getTitulo(),
                        "Usted ha realizado un comentario en el alojamiento " + alojamiento.getTitulo(),
                        usuario.getEmail())
        );
    }

    public Page<ComentarioDTO> listarComentariosPorAlojamiento(Long alojamientoId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return comentarioRepository.findByAlojamientoIdOrderByFechaDesc(alojamientoId, pageable)
                .map(comentarioMapper::toDto);
    }

}
