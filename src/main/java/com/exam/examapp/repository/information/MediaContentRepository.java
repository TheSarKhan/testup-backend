package com.exam.examapp.repository.information;

import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.model.information.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, UUID> {
    List<MediaContent> getMediaContentByPageType(PageType pageType);

    boolean existsMediaContentByText(String text);

    Optional<MediaContent> getMediaContentByText(String text);
}
