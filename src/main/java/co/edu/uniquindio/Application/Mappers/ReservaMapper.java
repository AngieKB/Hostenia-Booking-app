package co.edu.uniquindio.Application.Mappers;

import co.edu.uniquindio.Application.DTO.Reserva.*;
import co.edu.uniquindio.Application.Model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservaMapper {
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "huesped", ignore = true)
    Reserva toEntity(RealizarReservaDTO dto);
    ReservaDTO toDTO(Reserva entity);
    @Mapping(source = "reserva.alojamiento.titulo", target = "alojamientoTitulo")
    @Mapping(source = "reserva.alojamiento.ubicacion.ciudad", target = "alojamientoCiudad")
    @Mapping(source = "reserva.alojamiento.galeria", target = "alojamientoGaleria")
    ReservaUsuarioDTO toUsuarioDTO(Reserva reserva);
    @Mapping(source = "reserva.alojamiento.titulo", target = "alojamientoTitulo")
    @Mapping(source = "reserva.alojamiento.ubicacion.ciudad", target = "alojamientoCiudad")
    @Mapping(source = "reserva.huesped.id", target = "idHuesped")
    @Mapping(source = "reserva.huesped.nombre", target = "nombreHuesped")
    ReservaAlojamientoDTO toAlojamientoDTO(Reserva reserva);
    @Mapping(target = "total", ignore = true)
    void updateReservaFromDTO(EditarReservaDTO dto, @MappingTarget Reserva reserva);

}
