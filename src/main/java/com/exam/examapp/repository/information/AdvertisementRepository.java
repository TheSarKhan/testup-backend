package com.exam.examapp.repository.information;

import com.exam.examapp.model.information.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, UUID> {
    Optional<Advertisement> getAdvertisementByTitle(String title);
}
