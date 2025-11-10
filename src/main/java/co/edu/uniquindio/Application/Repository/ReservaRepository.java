package co.edu.uniquindio.Application.Repository;

import co.edu.uniquindio.Application.Model.EstadoReserva;
import co.edu.uniquindio.Application.Model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByHuespedId(long huespedId, Pageable pageable);
    Page<Reserva> findByAlojamientoId(long alojamientoId, Pageable pageable);
    Page<Reserva> findByEstado(EstadoReserva estado, Pageable pageable);
    Page<Reserva> findByHuespedIdAndAlojamientoId(long huespedId, long alojamientoId, Pageable pageable);
    @Modifying
    @Query("UPDATE Reserva r SET r.estado = :nuevoEstado WHERE r.fechaCheckOut < :fecha AND r.estado NOT IN :excluir")
    int actualizarReservasCompletadas(@Param("nuevoEstado") EstadoReserva nuevoEstado,
                                       @Param("fecha") LocalDateTime fecha,
                                       @Param("excluir") List<EstadoReserva> estadosExcluir);

}
