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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        alojamiento.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento.setUbicacion(ubicacion);

        Usuario usuario = authService.getUsuarioAutenticado();

        PerfilAnfitrion perfilAnfitrion = perfilAnfitrionRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("El usuario autenticado no tiene perfil de anfitrión."));

        alojamiento.setAnfitrion(perfilAnfitrion);

        alojamientoRepository.save(alojamiento);
    }

    @Override
    public AlojamientoDTO obtenerPorId(Long id) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));
        return alojamientoMapper.toDTO(alojamiento);
    }


    @Override
    public void editarAlojamiento(Long id, EditarAlojamientoDTO alojadto, UbicacionDTO ubicaciondto) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile imagen : alojadto.galeria()) {
            Map result = imageService.upload(imagen);
            urls.add(result.get("url").toString());  // guardamos la URL pública
        }
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con id: " + id));

        Usuario usuarioActual = authService.getUsuarioAutenticado();

        if (alojamiento.getAnfitrion() == null ||
                !alojamiento.getAnfitrion().getUsuario().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No tienes permiso para editar este alojamiento.");
        }

        alojamientoMapper.updateEntity(alojamiento, alojadto, ubicaciondto);
        alojamiento.setGaleria(urls);
        alojamientoRepository.save(alojamiento);
    }




    @Override
    public Page<AlojamientoDTO> listarTodos(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return alojamientoRepository.findAll(pageable).map(alojamientoMapper::toDTO);
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
    public Page<AlojamientoDTO> buscarPorCiudad(String ciudad, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return alojamientoRepository.findByUbicacionCiudadContainingIgnoreCaseAndEstado(ciudad, ACTIVO, pageable).map(alojamientoMapper::toDTO);
    }

    @Override
    public Page<AlojamientoDTO> listarPorAnfitrion(Long idAnfitrion, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return alojamientoRepository.findByAnfitrionId(idAnfitrion, pageable).map(alojamientoMapper::toDTO);
    }

    @Override
    public Page<AlojamientoDTO> buscarPorPrecio(double min, double max, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return alojamientoRepository.findByPrecioNocheBetweenAndEstado(min,max,ACTIVO, pageable).map(alojamientoMapper::toDTO);
    }

    @Override
    public Page<AlojamientoDTO> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);

        Page<Alojamiento> alojamientos = alojamientoRepository.findByDate(inicio, fin, EstadoAlojamiento.ACTIVO, pageable);

        // Convertir a DTO
        List<AlojamientoDTO> dtos = alojamientos.stream()
                .map(alojamientoMapper::toDTO)
                .toList();

        // Calcular índices para la sublista paginada
        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        // Crear la página paginada manualmente
        List<AlojamientoDTO> paginados = dtos.subList(start, end);
        return new PageImpl<>(paginados, pageable, dtos.size());
    }

    @Override
    public Page<AlojamientoDTO> buscarPorServicios(List<String> servicios, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return alojamientoRepository.findByServicios(servicios, servicios.size(), pageable)
                .map(alojamientoMapper::toDTO);
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
    public Page<AlojamientoDTO> listarFavoritos(Long usuarioId, int pagina, int tamanio) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<AlojamientoDTO> listaDTO = usuario.getFavoritos().stream()
                .map(alojamientoMapper::toDTO)
                .toList();

        int start = Math.min(pagina * tamanio, listaDTO.size());
        int end = Math.min(start + tamanio, listaDTO.size());

        return new org.springframework.data.domain.PageImpl<>(listaDTO.subList(start, end),
                PageRequest.of(pagina, tamanio), listaDTO.size());
    }

    @Override
    public int contarUsuariosFavorito(Long alojamientoId) throws Exception{
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        return alojamiento.getUsuariosFavoritos().size();
    }

    public Page<AlojamientoDTO> listarAlojamientosPaginados(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio); // Página 0, tamaño 9 por ejemplo
        return alojamientoRepository.findAll(pageable)
                .map(alojamientoMapper::toDTO); // Convierte entidad a DTO
    }
}