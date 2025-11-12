package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;
import co.edu.uniquindio.Application.DTO.EmailDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Exceptions.BadCredentialsException;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Exceptions.ValidationException;
import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.EstadoReserva;
import co.edu.uniquindio.Application.Model.Reserva;
import co.edu.uniquindio.Application.Model.Usuario;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.ReservaRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.EmailService;
import co.edu.uniquindio.Application.Services.ReservaService;
import co.edu.uniquindio.Application.Mappers.ReservaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaServiceImpl implements ReservaService {
    private final ReservaMapper reservaMapper;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final EmailService emailService;
    private final AuthService authService;

    @Override
    public void cancelarReserva(Long id) {
        reservaRepository.findById(id).ifPresentOrElse(reserva -> {

            // Validar si la reserva ya está cancelada
            if(reserva.getEstado() == EstadoReserva.CANCELADA) {
                throw new InvalidOperationException("La reserva ya se encuentra cancelada.");
            }

            // Validar si la reserva ya está completada
            if(reserva.getEstado() == EstadoReserva.COMPLETADA) {
                throw new InvalidOperationException("No se puede cancelar una reserva que ya ha sido completada.");
            }

            LocalDateTime ahora = LocalDateTime.now();
            if(reserva.getFechaCheckIn().minusHours(48).isBefore(ahora)) {
                throw new InvalidOperationException("No se puede cancelar la reserva a menos de 48 horas del check-in.");
            }

            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);

            try {
                emailService.sendMail(
                        new EmailDTO("Cancelación de: " + reserva.getHuesped().getNombre(),
                                "El usuario " + reserva.getHuesped().getNombre() +
                                        " canceló su reserva que estaba registrada para el día " + reserva.getFechaCheckIn() +
                                        " en el alojamiento " + reserva.getAlojamiento().getTitulo(),
                                reserva.getAlojamiento().getAnfitrion().getUsuario().getEmail())
                );
                emailService.sendMail(
                        new EmailDTO("Cancelación de: " + reserva.getHuesped().getNombre(),
                                "Ha cancelado su reserva que estaba registrada para el día " + reserva.getFechaCheckIn() +
                                        " en el alojamiento " + reserva.getAlojamiento().getTitulo(),
                                reserva.getHuesped().getEmail())
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, () -> {
            throw new ResourceNotFoundException("No existe una reserva con el id " + id);
        });
    }
    @Override
    public void confirmarReservaUsuario(Long id) {
        reservaRepository.findById(id).ifPresentOrElse(reserva -> {

            // Validar si la reserva ya está cancelada
            if(reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                throw new InvalidOperationException("La reserva ya se encuentra confirmada.");
            }

            // Validar si la reserva ya está completada
            if(reserva.getEstado() == EstadoReserva.COMPLETADA) {
                throw new InvalidOperationException("No se puede confirmar una reserva que ya ha sido completada.");
            }


            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reservaRepository.save(reserva);

            try {
                emailService.sendMail(
                        new EmailDTO("Confirmación de: " + reserva.getAlojamiento().getAnfitrion().getUsuario().getNombre(),
                                "El anfitrion " + reserva.getAlojamiento().getAnfitrion().getUsuario().getNombre() +
                                        " confirmó su reserva que está registrada para el día " + reserva.getFechaCheckIn() +
                                        " en el alojamiento " + reserva.getAlojamiento().getTitulo(),
                                reserva.getHuesped().getEmail())
                );
                emailService.sendMail(
                        new EmailDTO("Confirmación de: " + reserva.getHuesped().getNombre(),
                                "Ha confirmado su reserva que está registrada para el día " + reserva.getFechaCheckIn() +
                                        " en el alojamiento " + reserva.getAlojamiento().getTitulo(),
                                reserva.getAlojamiento().getAnfitrion().getUsuario().getEmail())
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }, () -> {
            throw new ResourceNotFoundException("No existe una reserva con el id " + id);
        });
    }

    @Override
    public EstadoReserva obtenerEstadoReserva(Long id) {
        return null;
    }

    @Override
    public void editarReserva(Long id, EditarReservaDTO dto) {
        // Obtener reserva de la base de datos
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una reserva con el id " + id));
        System.out.println("Reserva encontrada en bd ");
        // Verificar estado de la reserva
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new InvalidOperationException("No se puede editar una reserva cancelada.");
        }
        System.out.println("Estado reserva validado ");

        // Obtener usuario autenticado
        Usuario usuarioAutenticado = authService.getUsuarioAutenticado();
        System.out.println("Usuario autenticado obtenido ");

        // Validar que el usuario pueda editar la reserva
        if (!reserva.getHuesped().getId().equals(usuarioAutenticado.getId())) {
            throw new InvalidOperationException("No tienes permisos para editar esta reserva.");
        }
        System.out.println("Permisos validados ");
//  Validar mínimo 1 noche
        if(Duration.between(dto.fechaCheckIn(), dto.fechaCheckOut()).toDays() < 1) {
            throw new InvalidOperationException("La reserva debe ser mínimo de 1 noche.");
        }
        if(reserva.getAlojamiento().getCapacidadMax()> dto.cantidadHuespedes()) {
            throw new InvalidOperationException("Se supera la capacidad máxima del alojamiento.");
        }

        // Actualizar campos de la reserva usando el mapper
        reservaMapper.updateReservaFromDTO(dto, reserva);
        System.out.println("Campos de la reserva actualizados ");
        double valorTotal = calcularValorTotal(reserva);
        reserva.setTotal(valorTotal);
        System.out.println("Valor total calculado ");

        // Guardar cambios
        reservaRepository.save(reserva);
    }


    @Override
    public Page<ReservaUsuarioDTO> obtenerMisReservas(int pagina, int tamanio) {
        Pageable pageable = Pageable.ofSize(tamanio).withPage(pagina);
        Usuario usuarioAutenticado = authService.getUsuarioAutenticado();

        Page<Reserva> reservas = reservaRepository.findByHuespedId(usuarioAutenticado.getId(), pageable);

        return reservas.map(reservaMapper::toUsuarioDTO);
    }


    @Override
    public Page<ReservaAlojamientoDTO> obtenerReservasPorIdAlojamiento(Long id, int pagina, int tamanio) {
        Pageable pageable = Pageable.ofSize(tamanio).withPage(pagina);
        Page<Reserva> reservas = reservaRepository.findByAlojamientoId(id, pageable);

        return reservas.map(reservaMapper::toAlojamientoDTO);
    }

    @Override
    public void guardar(RealizarReservaDTO reservadto) throws Exception {

        // convertir ids en entidades
        Usuario huesped = usuarioRepository.findById(authService.getUsuarioAutenticado().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe huésped con id " + authService.getUsuarioAutenticado().getId()));
        Alojamiento alojamiento = alojamientoRepository.findById(reservadto.alojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe alojamiento con id " + reservadto.alojamientoId()));
        if(reservadto.fechaCheckIn().isBefore(LocalDateTime.now()) || reservadto.fechaCheckOut().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("No se pueden reservar fechas pasadas.");
        }
        //  Validar mínimo 1 noche
        if(Duration.between(reservadto.fechaCheckIn(), reservadto.fechaCheckOut()).toDays() < 1) {
            throw new InvalidOperationException("La reserva debe ser mínimo de 1 noche.");
        }
        if(reservadto.cantidadHuespedes() > alojamiento.getCapacidadMax()) {
            throw new InvalidOperationException("Se supera la capacidad máxima del alojamiento.");
        }
        Reserva newReserva = reservaMapper.toEntity(reservadto);
        newReserva.setEstado(EstadoReserva.PENDIENTE);
        newReserva.setAlojamiento(alojamiento);
        newReserva.setHuesped(authService.getUsuarioAutenticado());

        Double valorTotal = calcularValorTotal(newReserva);
        newReserva.setTotal(valorTotal);

        reservaRepository.save(newReserva);

        emailService.sendMail(
                new EmailDTO("Reserva en appBooking de: " + newReserva.getHuesped().getNombre(),
                        "Su reserva se realizó satisfactoriamente con los siguientes datos. \nDía de llegada: " + newReserva.getFechaCheckIn().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "\nDía de salida: " + newReserva.getFechaCheckOut().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "\nCantidad de huéspedes: " + newReserva.getCantidadHuespedes()
                                + "\nEn el alojamiento: " + newReserva.getAlojamiento().getTitulo()
                                + "\nPor un total de: " + newReserva.getTotal(),
                        newReserva.getHuesped().getEmail())
        );
        emailService.sendMail(
                new EmailDTO("Reserva en appBooking de: " + newReserva.getHuesped().getNombre(),
                        "Una reserva se realizó en el alojamiento " + newReserva.getAlojamiento().getTitulo() + " con los siguientes datos. \nDía de llegada: " + newReserva.getFechaCheckIn()
                                + "\nDía de salida: " + newReserva.getFechaCheckOut()
                                + "\nCantidad de huéspedes: " + newReserva.getCantidadHuespedes()
                                + "\nPor un total de: " + newReserva.getTotal(),
                        newReserva.getAlojamiento().getAnfitrion().getUsuario().getEmail())
        );
    }
    @Override
    @Transactional
    public void actualizarReservasCompletadas() {
        int filasActualizadas = reservaRepository.actualizarReservasCompletadas(
                EstadoReserva.COMPLETADA,
                LocalDateTime.now(),
                List.of(EstadoReserva.CANCELADA, EstadoReserva.COMPLETADA)
        );
        System.out.println("Reservas completadas actualizadas: " + filasActualizadas);
    }

    public double calcularValorTotal(Reserva reserva){
        long dias = reserva.getFechaCheckOut().toLocalDate().toEpochDay() - reserva.getFechaCheckIn().toLocalDate().toEpochDay();
        return dias * reserva.getAlojamiento().getPrecioNoche()* reserva.getCantidadHuespedes();
    }
    @Override
    public ReservaUsuarioDTO obtenerPorId(Long id) throws Exception {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrado con id: " + id));
        return reservaMapper.toUsuarioDTO(reserva);
    }

}

