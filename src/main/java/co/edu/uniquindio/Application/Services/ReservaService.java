package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Model.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservaService {
    void guardar(RealizarReservaDTO dto) throws Exception;
    void editarReserva(Long id, EditarReservaDTO dto);
    Page<ReservaUsuarioDTO> obtenerMisReservas(int pagina, int tamanio);
    Page<ReservaAlojamientoDTO> obtenerReservasPorIdAlojamiento(Long id, int pagina, int tamanio);
    void cancelarReserva(Long id);
    void confirmarReservaUsuario(Long id);
    EstadoReserva obtenerEstadoReserva(Long id);
    void actualizarReservasCompletadas();
    ReservaUsuarioDTO obtenerPorId(Long id) throws Exception;

}
