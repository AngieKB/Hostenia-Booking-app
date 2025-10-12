package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.CrearAlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.ResumenAlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.UbicacionDTO;
import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.Ubicacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface AlojamientoMapper {
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "galeria", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    Alojamiento toEntity(CrearAlojamientoDTO dto);
    AlojamientoDTO toDTO(Alojamiento entity);
    default Ubicacion crearUbicacion(CrearAlojamientoDTO dto) {
        return new Ubicacion(
                dto.direccion(),
                dto.ciudad(),
                dto.pais(),
                dto.latitud(),
                dto.longitud()
        );
    }
    @Mapping(target = "imagenPrincipal", expression = "java(getPrimeraFoto(entity))")
    @Mapping(target = "ciudad", source = "ubicacion.ciudad")
    ResumenAlojamientoDTO toResumenDTO(Alojamiento entity);
    default String getPrimeraFoto(Alojamiento entity) {
        if (entity.getGaleria() != null && !entity.getGaleria().isEmpty()) {
            return entity.getGaleria().getFirst(); // primera foto
        }
        return null;
    }
    // NUEVO: Actualiza una entidad existente con los DTO separados
    default void updateEntity(Alojamiento entity, AlojamientoDTO dto, UbicacionDTO ubicacionDTO) {
        if (entity == null || dto == null) return;

        entity.setTitulo(dto.titulo());
        entity.setDescripcion(dto.descripcion());
        entity.setCapacidadMax(dto.capacidadMax());
        entity.setPrecioNoche(dto.precioNoche());
        entity.setServicios(dto.servicios());
        entity.setGaleria(dto.galeria());
        entity.setEstado(dto.estado());

        // Manejo de la ubicación separada
        if (ubicacionDTO != null) {
            if (entity.getUbicacion() == null) {
                entity.setUbicacion(new Ubicacion(
                        ubicacionDTO.direccion(),
                        ubicacionDTO.ciudad(),
                        ubicacionDTO.pais(),
                        ubicacionDTO.latitud(),
                        ubicacionDTO.longitud()
                ));
            } else {
                entity.getUbicacion().setDireccion(ubicacionDTO.direccion());
                entity.getUbicacion().setCiudad(ubicacionDTO.ciudad());
                entity.getUbicacion().setPais(ubicacionDTO.pais());
                entity.getUbicacion().setLatitud(ubicacionDTO.latitud());
                entity.getUbicacion().setLongitud(ubicacionDTO.longitud());
            }
        }
    }


}
