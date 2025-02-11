package com.siscem.portal_sae.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siscem.portal_sae.models.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
}
