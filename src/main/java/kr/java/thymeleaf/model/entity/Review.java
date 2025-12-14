package kr.java.thymeleaf.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Review extends BaseEntity {
    @NotBlank(message = "리뷰 제목은 필수입니다.")
    @Length(max = 100, message = "리뷰 제목은 최대 100자까지 가능합니다.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Column(nullable = false)
    private String content;

    @Min(value = 1, message = "평점은 최소 1점 이상입니다.")
    @Max(value = 5, message = "평점은 최대 5점 이하입니다.")
    @Column(nullable = false)
    private Integer rating;

    private String imageUrl;
}
