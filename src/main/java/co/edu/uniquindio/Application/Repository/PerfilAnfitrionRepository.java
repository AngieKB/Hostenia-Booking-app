package co.edu.uniquindio.Application.Repository;

import co.edu.uniquindio.Application.Model.PerfilAnfitrion;
import co.edu.uniquindio.Application.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerfilAnfitrionRepository extends JpaRepository<PerfilAnfitrion, Long> {
    Optional<PerfilAnfitrion> findByUsuario(Usuario usuario);}
