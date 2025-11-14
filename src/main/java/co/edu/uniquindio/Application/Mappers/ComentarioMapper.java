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
    @Mapping(source = "huesped.nombre", target = "nombreHuesped")  // Asume que Usuario tiene 'nombre'
    @Mapping(source = "alojamiento.anfitrion.usuario.nombre", target = "nombreAnfitrion")  // Asume que Alojamiento tiene 'anfitrion' (Usuario) con 'nombre'
    @Mapping(source = "huesped.fotoUrl", target = "fotoHuesped")  // Asume que Usuario tiene 'foto'
    @Mapping(source = "alojamiento.anfitrion.usuario.fotoUrl", target = "fotoAnfitrion")
    @Mapping(source = "respuesta.respuesta", target = "textoRespuesta")
    ComentarioDTO toDto(Comentario entity);
}