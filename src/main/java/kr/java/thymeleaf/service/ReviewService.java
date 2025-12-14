package kr.java.thymeleaf.service;

import ch.qos.logback.core.util.StringUtil;
import kr.java.thymeleaf.model.entity.Review;
import kr.java.thymeleaf.model.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
// org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public Review create(Review review, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFilename = fileStorageService.store(imageFile);
            review.setImageUrl(fileStorageService.getUrl(storedFilename));
        }

        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(Long id, Review updateData, MultipartFile newImageFile) {
        Review review = findById(id);

        review.setTitle(updateData.getTitle());
        review.setContent(updateData.getContent());
        review.setRating(updateData.getRating());

        if (newImageFile != null && !newImageFile.isEmpty()) {
            deleteOldImage(review.getImageUrl());
            String storedFilename = fileStorageService.store(newImageFile);
            review.setImageUrl(fileStorageService.getUrl(storedFilename));
        }

        return review;
    }

    @Transactional
    public void delete(Long id) {
        Review review = findById(id);
        deleteOldImage(review.getImageUrl());
        reviewRepository.delete(review);
    }

    private void deleteOldImage(String imageUrl) {
        if (StringUtil.isNullOrEmpty(imageUrl)) {
            String filename = imageUrl.replace("/images/", "");
            fileStorageService.delete(filename);
        }
    }
}
