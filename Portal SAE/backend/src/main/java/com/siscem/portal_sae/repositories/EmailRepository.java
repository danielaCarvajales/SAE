package com.siscem.portal_sae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, String> {

	List<Email> findByUsuarioAsignadoCodigo(Integer userCode);
	List<Email> findByEventoAsignadoCodigo(Integer eventCode);
	@Query("SELECT e FROM Email e WHERE e.usuarioAsignado.codigo = :userCode ORDER BY e.fechaRecibido DESC")
	List<Email> findByUsuarioAsignadoCodigoOrderByFechaRecibidoDesc(@Param("userCode") Integer userCode);
}
