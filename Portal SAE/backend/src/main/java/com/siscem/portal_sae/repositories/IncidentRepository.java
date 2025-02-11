package com.siscem.portal_sae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Incident;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
	List<Incident> findByCodigoConsultorCodigo(Integer consultantCode);

	List<Incident> findByCodigoEmpresarioCodigo(Integer businessmanCode);
}
