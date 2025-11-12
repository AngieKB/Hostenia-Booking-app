package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Anfitrion.CrearAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.Anfitrion.EditarAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.Anfitrion.PerfilAnfitrionDTO;

import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.PerfilAnfitrion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PerfilAnfitrionMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    PerfilAnfitrionDTO toDTO(PerfilAnfitrion entity);

    @Mapping(target = "usuario", ignore = true)
        // Ignorar el mapeo del campo usuario
    PerfilAnfitrion toEntity(CrearAnfitrionDTO dto);

    void updatePerfilAnfitrionFromDto(EditarAnfitrionDTO dto, @MappingTarget PerfilAnfitrion anfitrion);

    default Long alojamientoToId(Alojamiento alojamiento) {
        return alojamiento == null ? null : alojamiento.getId();
    }

    default List<Long> alojamientosToIds(List<Alojamiento> alojamientos) {
        return alojamientos == null ? null :
                alojamientos.stream()
                        .map(this::alojamientoToId)
                        .collect(Collectors.toList());
    }
}