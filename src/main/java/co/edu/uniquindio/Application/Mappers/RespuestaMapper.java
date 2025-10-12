package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Comentario.ResponderDTO;
import co.edu.uniquindio.Application.DTO.Comentario.RespuestaDTO;
import co.edu.uniquindio.Application.Model.Respuesta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RespuestaMapper {
    @Mapping(target = "anfitrion", ignore = true)
    @Mapping(target = "fecha", expression = "java(java.time.LocalDateTime.now())")
    Respuesta toEntity(ResponderDTO dto);
    RespuestaDTO toDto(Respuesta entity);
}
