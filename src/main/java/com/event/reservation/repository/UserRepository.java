package com.event.reservation.repository;

import com.event.reservation.entity.User;
import com.event.reservation.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Vérifier l’existence d’un email
    boolean existsByEmail(String email);

    // Trouver tous les utilisateurs actifs par rôle
    List<User> findByRoleAndActif(Role role, boolean actif);

    // Trouver les utilisateurs par nom ou prénom (insensible à la casse)
    List<User> findByNomIgnoreCaseContainingOrPrenomIgnoreCaseContaining(String nom, String prenom);
    @Query("""
    SELECT u FROM User u
    WHERE (:keyword IS NULL OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                            OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:role IS NULL OR u.role = :role)
    AND (:active IS NULL OR u.actif = :active)
""")
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("role") Role role,
                           @Param("active") Boolean active);
    // Compter les utilisateurs par rôle
    long countByRole(Role role);
}
