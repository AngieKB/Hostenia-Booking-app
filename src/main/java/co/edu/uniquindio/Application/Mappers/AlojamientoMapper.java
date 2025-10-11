package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Alojamiento.AlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.CrearAlojamientoDTO;
import co.edu.uniquindio.Application.DTO.Alojamiento.ResumenAlojamientoDTO;
import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.Ubicacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface AlojamientoMapper {
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "galeria", ignore = true)
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
    // NUEVO: Actualiza una entidad existente con un DTO
    default void updateEntity(Alojamiento entity, AlojamientoDTO dto) {
        if(dto == null || entity == null) return;

        entity.setTitulo(dto.titulo());
        entity.setDescripcion(dto.descripcion());
        entity.setCapacidadMax(dto.capacidadMax());
        entity.setPrecioNoche(dto.precioNoche());
        entity.setServicios(dto.servicios());
        entity.setEstado(dto.estado());

        if(entity.getUbicacion() == null) {
            entity.setUbicacion(new Ubicacion(
                    dto.ubicacion().direccion(),
                    dto.ubicacion().ciudad(),
                    dto.ubicacion().pais(),
                    dto.ubicacion().latitud(),
                    dto.ubicacion().longitud()
            ));
        } else {
            entity.getUbicacion().setDireccion(dto.ubicacion().direccion());
            entity.getUbicacion().setCiudad(dto.ubicacion().ciudad());
            entity.getUbicacion().setPais(dto.ubicacion().pais());
            entity.getUbicacion().setLatitud(dto.ubicacion().latitud());
            entity.getUbicacion().setLongitud(dto.ubicacion().longitud());
        }}

}
