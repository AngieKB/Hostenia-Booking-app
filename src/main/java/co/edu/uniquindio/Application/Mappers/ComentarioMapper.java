package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Comentario.ComentarDTO;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.Model.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComentarioMapper {
    @Mapping(source = "idAlojamiento", target = "alojamiento.id")
    @Mapping(target = "reserva", ignore = true) // se pasa aparte
    @Mapping(target = "huesped", ignore = true) // se pasa aparte
    @Mapping(target = "fecha", expression = "java(java.time.LocalDateTime.now())")
    Comentario toEntity(ComentarDTO dto);
    ComentarioDTO toDto(Comentario entity);
}