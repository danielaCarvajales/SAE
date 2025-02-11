package com.siscem.portal_sae.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
	List<Activity> findByCodigoTareaCodigo(Integer taskCode);
}
