package co.edu.uniquindio.Application.Repository;

import co.edu.uniquindio.Application.Model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Page<Comentario> findByAlojamientoIdOrderByFechaDesc(Long alojamientoId, Pageable pageable);
    boolean existsByReservaId(Long reservaId);
    boolean existsByHuespedIdAndAlojamientoId(Long huespedId, Long alojamientoId);
    List<Comentario> findByAlojamientoId(Long alojamientoId);
}
