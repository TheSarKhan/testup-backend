package com.exam.examapp.service.impl.information;

import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Advertisement;
import com.exam.examapp.model.information.MediaContent;
import com.exam.examapp.model.information.Superiority;
import com.exam.examapp.repository.information.AdvertisementRepository;
import com.exam.examapp.repository.information.MediaContentRepository;
import com.exam.examapp.repository.information.SuperiorityRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.information.InitializeInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializeInformationServiceImpl implements InitializeInformationService {
    private final static String ADVERTISEMENT_IMAGE_PATH = "uploads/images/advertisements";

    private final static String SUPERIORITY_IMAGE_PATH = "uploads/images/superiority";

    private final static String MEDIA_CONTENT_IMAGE_PATH = "uploads/images/media_contents";

    private final AdvertisementRepository advertisementRepository;

    private final SuperiorityRepository superiorityRepository;

    private final MediaContentRepository mediaContentRepository;

    private final LocalFileServiceImpl fileService;

    @Override
    public void initializeAdvertisement(List<MultipartFile> images) {
        if (advertisementRepository.count() == 0) {
            List<Advertisement> advertisements = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String imageUrl = fileService.uploadFile(ADVERTISEMENT_IMAGE_PATH, images.get(i));
                Advertisement build = Advertisement.builder()
                        .title("Example Advertisement " + i)
                        .redirectUrl("Example URL " + i)
                        .imageUrl(imageUrl)
                        .build();
                advertisements.add(build);
            }
            advertisementRepository.saveAll(advertisements);
        }
    }

    @Override
    public void initializeSuperiority(List<MultipartFile> icons) {
        if (superiorityRepository.count() == 0) {
            List<Superiority> superiority = new ArrayList<>();
            for (int i = 0; i < icons.size(); i++) {
                String iconUrl = fileService.uploadFile(SUPERIORITY_IMAGE_PATH, icons.get(i));
                Superiority build = Superiority.builder()
                        .text("Example Superiority Text " + i)
                        .type(i % 3 == 0 ? SuperiorityType.ADVANTAGES_FOR_STUDENT :
                                i % 3 == 1 ? SuperiorityType.ADVANTAGES_FOR_TEACHER :
                                        SuperiorityType.WHY_TEST_UP)
                        .iconUrl(iconUrl)
                        .build();
                superiority.add(build);
            }
            superiorityRepository.saveAll(superiority);
        }
    }

    @Override
    public void initializeMediaContent(List<MultipartFile> images) {
        if (mediaContentRepository.count() == 0) {
            List<MediaContent> mediaContents = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String imageUrl = fileService.uploadFile(MEDIA_CONTENT_IMAGE_PATH, images.get(i));
                MediaContent build = MediaContent.builder()
                        .text("Example Media Content " + i)
                        .author("Example Author " + i)
                        .backgroundColor("rgba(24, 101, 242, 1)")
                        .textColor("rgba(255, 255, 255, 1)")
                        .pageType(PageType.values()[i % 4])
                        .pictureUrl(imageUrl)
                        .build();
                mediaContents.add(build);
            }
            mediaContentRepository.saveAll(mediaContents);
        }
    }
}
