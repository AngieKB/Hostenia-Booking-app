package co.edu.uniquindio.Application.Services.impl;

import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.Exceptions.AccessDeniedException;
import co.edu.uniquindio.Application.Exceptions.InvalidOperationException;
import co.edu.uniquindio.Application.Exceptions.ResourceNotFoundException;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.PerfilAnfitrionRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.AlojamientoService;
import co.edu.uniquindio.Application.Services.ImageService;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.Application.Mappers.AlojamientoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static co.edu.uniquindio.Application.Model.EstadoAlojamiento.ACTIVO;

@Service
@RequiredArgsConstructor
@Transactional
public class AlojamientoServiceImpl implements AlojamientoService {
    private final AlojamientoRepository alojamientoRepository;
    private final AlojamientoMapper alojamientoMapper;
    private final ImageService imageService;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final PerfilAnfitrionRepository perfilAnfitrionRepository;

    @Override
    public void guardar(CrearAlojamientoDTO dto) throws Exception {
        // Subir imágenes y obtener URLs
        List<String> urls = new ArrayList<>();
        for (MultipartFile imagen : dto.galeria()) {
            Map result = imageService.upload(imagen);
            urls.add(result.get("url").toString());  // guardamos la URL pública
        }
        Alojamiento alojamiento = alojamientoMapper.toEntity(dto);
        Ubicacion ubicacion = alojamientoMapper.crearUbicacion(dto);
        alojamiento.setGaleria(urls);
        alojamiento.setEstado(ACTIVO);
        alojamiento.setUbicacion(ubicacion);

        Usuario usuario = authService.getUsuarioAutenticado();

        PerfilAnfitrion perfilAnfitrion = perfilAnfitrionRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("El usuario autenticado no tiene perfil de anfitrión."));

        alojamiento.setAnfitrion(perfilAnfitrion);

        alojamientoRepository.save(alojamiento);
    }

    @Override
    public AlojamientoDTO obtenerPorId(Long id) throws Exception{
        return alojamientoRepository.findById(id).map(alojamientoMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));
    }

    @Override
    public void editarAlojamiento(Long id, AlojamientoDTO alojadto, UbicacionDTO ubicaciondto) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        Usuario usuarioActual = authService.getUsuarioAutenticado();

        if (alojamiento.getAnfitrion() == null ||
                !alojamiento.getAnfitrion().getUsuario().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No tienes permiso para editar este alojamiento.");
        }

        alojamientoMapper.updateEntity(alojamiento, alojadto, ubicaciondto);

        if (alojadto.estado() == null) {
            alojamiento.setEstado(alojamiento.getEstado());
        }

        alojamientoRepository.save(alojamiento);
    }




    @Override
    public List<AlojamientoDTO> listarTodos() {
        return alojamientoRepository.findAll().stream().map(alojamientoMapper::toDTO).toList() ;
    }

    @Override
    public void eliminar(Long id) throws Exception{
        // Buscar alojamiento
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        // Verificar si tiene reservas futuras
        boolean tieneReservasFuturas = alojamiento.getReservas().stream()
                .anyMatch(reserva -> reserva.getFechaCheckIn().isAfter(LocalDateTime.now())
                        && reserva.getEstado() != EstadoReserva.CANCELADA);

        if (tieneReservasFuturas) {
            throw new InvalidOperationException("No se puede eliminar el alojamiento porque tiene reservas futuras.");
        }

        // Soft delete: cambiar estado a ELIMINADO
        alojamiento.setEstado(EstadoAlojamiento.INACTIVO);

        // Guardar cambios en la base de datos
        alojamientoRepository.save(alojamiento);
    }


    @Override
    public MetricasDTO verMetricas(Long id, LocalDateTime fechaMin, LocalDateTime fechaMax) throws Exception{
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        // Filtrar reservas por rango de fechas
        int reservas = (int) alojamiento.getReservas().stream()
                .filter(r -> !r.getFechaCheckIn().isBefore(fechaMin) && !r.getFechaCheckOut().isAfter(fechaMax))
                .count();

        // Filtrar comentarios por fecha de creación (si existe campo fecha), opcional
        List<Comentario> comentariosFiltrados = alojamiento.getComentarios().stream()
                .filter(c -> c.getFecha().isAfter(fechaMin) && c.getFecha().isBefore(fechaMax))
                .toList();

        double promedio = 0.0;
        if (!comentariosFiltrados.isEmpty()) {
            promedio = comentariosFiltrados.stream()
                    .mapToInt(Comentario::getCalificacion)
                    .average()
                    .orElse(0.0);
        }

        return new MetricasDTO(promedio, reservas);
    }


    @Override
    public List<ResumenAlojamientoDTO> buscarPorCiudad(String ciudad) {
        return alojamientoRepository.findByUbicacionCiudadContainingIgnoreCaseAndEstado(ciudad,ACTIVO).stream().map(alojamientoMapper::toResumenDTO).toList();
    }

    @Override
    public List<AlojamientoDTO> listarPorAnfitrion(Long idAnfitrion) {
        return alojamientoRepository.findByAnfitrionId(idAnfitrion).stream().map(alojamientoMapper::toDTO).toList();
    }

    @Override
    public List<ResumenAlojamientoDTO> buscarPorPrecio(double min, double max) {
        return alojamientoRepository.findByPrecioNocheBetweenAndEstado(min,max,ACTIVO).stream().map(alojamientoMapper::toResumenDTO).toList();
    }

    @Override
    public List<ResumenAlojamientoDTO> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return alojamientoRepository.findByDate(fechaInicio,fechaFin,ACTIVO).stream().map(alojamientoMapper::toResumenDTO).toList();
    }

    @Override
    public List<AlojamientoDTO> buscarPorServicios(List<String> servicios) {
        return alojamientoRepository.findByServicios(servicios, servicios.size())
                .stream()
                .map(alojamientoMapper::toDTO)
                .toList();
    }
    @Override
    public void agregarAFavoritos(Long usuarioId, Long alojamientoId) throws Exception{
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        if (!usuario.getFavoritos().contains(alojamiento)) {
            usuario.getFavoritos().add(alojamiento);
            alojamiento.getUsuariosFavoritos().add(usuario);
            usuarioRepository.save(usuario);
        }
    }

    @Override
    public void quitarDeFavoritos(Long usuarioId, Long alojamientoId) throws Exception{
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        usuario.getFavoritos().remove(alojamiento);
        alojamiento.getUsuariosFavoritos().remove(usuario);
        usuarioRepository.save(usuario);
    }

    @Override
    public List<ResumenAlojamientoDTO> listarFavoritos(Long usuarioId) throws Exception{
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return usuario.getFavoritos().stream()
                .map(alojamientoMapper::toResumenDTO)
                .toList();
    }

    @Override
    public int contarUsuariosFavorito(Long alojamientoId) throws Exception{
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        return alojamiento.getUsuariosFavoritos().size();
    }
}