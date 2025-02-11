package com.siscem.portal_sae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
	List<Task> findByConsultorAsignadoCodigo(Integer consultantCode);

	List<Task> findByEmpresarioAsignadoCodigo(Integer businessmanCode);
}