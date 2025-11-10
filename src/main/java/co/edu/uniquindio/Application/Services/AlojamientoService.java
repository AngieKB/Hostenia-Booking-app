package co.edu.uniquindio.Application.Services;

import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface AlojamientoService {
    void guardar(CrearAlojamientoDTO alojamientodto) throws Exception;
    AlojamientoDTO obtenerPorId(Long id) throws Exception;
    Page<AlojamientoDTO> listarTodos(int pagina, int tamanio)throws Exception;
    void editarAlojamiento(Long id, EditarAlojamientoDTO dto, UbicacionDTO ubicaciondto)throws Exception;
    void eliminar(Long id)throws Exception;
    MetricasDTO verMetricas(Long id, LocalDateTime fechamin, LocalDateTime fechamax)throws Exception;
    Page<AlojamientoDTO> buscarPorCiudad(String ciudad, int pagina, int tamanio)throws Exception;
    Page<AlojamientoDTO> buscarPorPrecio(double min, double max, int pagina, int tamanio)throws Exception;
    Page<AlojamientoDTO> listarPorAnfitrion(Long idAnfitrion, int pagina, int tamanio)throws Exception;
    Page<AlojamientoDTO> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin, int pagina, int tamanio)throws Exception;
    Page<AlojamientoDTO> buscarPorServicios(List<String> servicios, int pagina, int tamanio)throws Exception;
    void agregarAFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    void quitarDeFavoritos(Long usuarioId, Long alojamientoId)throws Exception;
    Page<AlojamientoDTO> listarFavoritos(Long usuarioId, int pagina, int tamanio)throws Exception;
    int contarUsuariosFavorito(Long alojamientoId)throws Exception;
 }