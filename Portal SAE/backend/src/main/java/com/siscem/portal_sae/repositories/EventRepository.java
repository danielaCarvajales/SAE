package com.siscem.portal_sae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Event;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
	List<Event> findByCodigoConsultorCodigo(Integer consultantCode);

	List<Event> findByCodigoEmpresarioCodigo(Integer businessmanCode);
}
