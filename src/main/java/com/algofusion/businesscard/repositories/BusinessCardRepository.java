package com.algofusion.businesscard.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algofusion.businesscard.entities.BusinessCard;

@Repository
public interface BusinessCardRepository extends JpaRepository<BusinessCard, Long> {
    List<BusinessCard> findAllByUserUsername(String username);
}
