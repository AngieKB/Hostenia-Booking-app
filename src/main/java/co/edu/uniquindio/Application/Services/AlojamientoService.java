package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Alojamiento.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface AlojamientoService {
    void guardar(CrearAlojamientoDTO alojamientodto) throws Exception;
    ResumenAlojamientoDTO obtenerPorId(Long id) throws Exception;
    List<ResumenAlojamientoDTO> listarTodos()throws Exception;
    void editarAlojamiento(Long id, AlojamientoDTO dto, UbicacionDTO ubicaciondto)throws Exception;
    void eliminar(Long id)throws Exception;
    MetricasDTO verMetricas(Long id, LocalDateTime fechamin, LocalDateTime fechamax)throws Exception;
    List<ResumenAlojamientoDTO> buscarPorCiudad(String ciudad)throws Exception;
    List<ResumenAlojamientoDTO> buscarPorPrecio(double min, double max)throws Exception;
    List<ResumenAlojamientoDTO> listarPorAnfitrion(Long idAnfitrion)throws Exception;
    List<ResumenAlojamientoDTO> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin)throws Exception;
    List<ResumenAlojamientoDTO> buscarPorServicios(List<String> servicios)throws Exception;
    void agregarAFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    void quitarDeFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    List<ResumenAlojamientoDTO> listarFavoritos(Long usuarioId)throws Exception;
    int contarUsuariosFavorito(Long alojamientoId)throws Exception;
}