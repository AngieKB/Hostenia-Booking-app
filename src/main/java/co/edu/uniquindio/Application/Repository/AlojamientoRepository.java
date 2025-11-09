package co.edu.uniquindio.Application.Repository;


import co.edu.uniquindio.Application.Model.Alojamiento;
import co.edu.uniquindio.Application.Model.EstadoAlojamiento;
import co.edu.uniquindio.Application.Model.PerfilAnfitrion;
import co.edu.uniquindio.Application.Model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    List<Alojamiento> findByEstado(EstadoAlojamiento estado);
    List<Alojamiento> findByPrecioNocheBetweenAndEstado(Double precioMin, Double precioMax,EstadoAlojamiento estado);
    List<Alojamiento> findByUbicacionCiudadContainingIgnoreCaseAndEstado(String ciudad, EstadoAlojamiento estado);
    List<Alojamiento> findByAnfitrionId(Long id);

    @Query("SELECT a FROM Alojamiento a " +
            "WHERE a.estado = :estado " +
            "AND NOT EXISTS (" +
            "   SELECT r FROM Reserva r " +
            "   WHERE r.alojamiento = a " +
            "   AND r.fechaCheckIn < :fin " +
            "   AND r.fechaCheckOut > :inicio" +
            ")")

    List<Alojamiento> findByDate(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("estado") EstadoAlojamiento estado
    );



    //Explicación:
    //SELECT a FROM Alojamiento a Selecciona cada entidad Alojamiento como a.
    //JOIN a.servicios s Hace un join con la lista de servicios de cada alojamiento, usando el alias s.
    //WHERE s IN :servicios Filtra los servicios para que solo se consideren los que están en la lista que recibe el método (:servicios).
    //GROUP BY a Agrupa los resultados por alojamiento, para poder usar funciones de agregación.
    //HAVING COUNT(DISTINCT s) = :size Solo deja los alojamientos donde la cantidad de servicios distintos encontrados es igual al tamaño de la lista de servicios buscados (:size). Así, solo se devuelven los alojamientos que tienen todos los servicios pedidos.
    @Query("SELECT a FROM Alojamiento a JOIN a.servicios s WHERE s IN :servicios GROUP BY a HAVING COUNT(DISTINCT s) = :size")
    List<Alojamiento> findByServicios(@Param("servicios") List<String> servicios, @Param("size") long size);


    //no se si quitarlo, además de que eso, oarece trabalenguas como dijo el profe, no se si ponerlo como query
    List<Alojamiento> findByUbicacionCiudadContainingIgnoreCaseAndPrecioNocheBetweenAndEstado(
        String ciudad, Double precioMin, Double precioMax, EstadoAlojamiento estado);

}
