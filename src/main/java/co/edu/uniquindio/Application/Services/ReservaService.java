package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Model.EstadoReserva;

import java.util.List;

public interface ReservaService {
    void guardar(RealizarReservaDTO dto) throws Exception;
    void editarReserva(Long id, EditarReservaDTO dto);
    List<ReservaUsuarioDTO> obtenerMisReservas();
    List<ReservaAlojamientoDTO> obtenerReservasPorIdAlojamiento(Long id);
    void cancelarReserva(Long id);
    EstadoReserva obtenerEstadoReserva(Long id);
    void actualizarReservasCompletadas();
}
