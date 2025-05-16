package com.algofusion.businesscard.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algofusion.businesscard.dto.PrivacyUser;
import com.algofusion.businesscard.entities.BusinessCardPrivacy;

@Repository
public interface BusinessCardPrivacyRepository extends JpaRepository<BusinessCardPrivacy, Long> {
    List<BusinessCardPrivacy> findAllByUserUsername(String username);

    @Query("""
            SELECT new com.algofusion.businesscard.dto.PrivacyUser(u.id, u.username)
            FROM BusinessCardPrivacy bp
            JOIN bp.user u
            WHERE bp.businessCard.id = :businessCardId
            """)
    List<PrivacyUser> findPrivacyUsersByBusinessCardId(@Param("businessCardId") Long businessCardId);

}
