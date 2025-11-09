package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Alojamiento.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface AlojamientoService {
    void guardar(CrearAlojamientoDTO alojamientodto) throws Exception;
   AlojamientoDTO obtenerPorId(Long id) throws Exception;
    List<AlojamientoDTO> listarTodos()throws Exception;
    void editarAlojamiento(Long id, EditarAlojamientoDTO dto, UbicacionDTO ubicaciondto)throws Exception;
    void eliminar(Long id)throws Exception;
    MetricasDTO verMetricas(Long id, LocalDateTime fechamin, LocalDateTime fechamax)throws Exception;
    List<AlojamientoDTO> buscarPorCiudad(String ciudad)throws Exception;
    List<AlojamientoDTO> buscarPorPrecio(double min, double max)throws Exception;
    List<AlojamientoDTO> listarPorAnfitrion(Long idAnfitrion)throws Exception;
    List<AlojamientoDTO> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin)throws Exception;
    List<AlojamientoDTO> buscarPorServicios(List<String> servicios)throws Exception;
    void agregarAFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    void quitarDeFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    List<AlojamientoDTO> listarFavoritos(Long usuarioId)throws Exception;
    int contarUsuariosFavorito(Long alojamientoId)throws Exception;
}