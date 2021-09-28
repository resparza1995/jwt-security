package spring.jwtsecurity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.jwtsecurity.entity.Rol;
import spring.jwtsecurity.enums.RolName;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByRolName(RolName rolName);

}
