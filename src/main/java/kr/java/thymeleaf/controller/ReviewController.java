package kr.java.thymeleaf.controller;

import jakarta.validation.Valid;
import kr.java.thymeleaf.exception.FileStorageException;
import kr.java.thymeleaf.exception.InvalidFileTypeException;
import kr.java.thymeleaf.model.entity.Review;
import kr.java.thymeleaf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    // org.springframework.ui.Model;
    public String list(Model model) {
        List<Review> reviews = reviewService.findAll();
        model.addAttribute("reviews", reviews);
        model.addAttribute("pageName", "리뷰 목록");
        return "review/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Review review = reviewService.findById(id);
        model.addAttribute("review", review);
        model.addAttribute("pageName", "리뷰 상세");
        return "review/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new Review());
        model.addAttribute("pageName", "리뷰 작성");
        return "review/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Review review,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageName", "리뷰 작성");
            return "review/form";
        }

        try {
            reviewService.create(review, imageFile);
            redirectAttributes.addFlashAttribute("message", "리뷰가 등록되었습니다.");
            return "redirect:/reviews";

        } catch (InvalidFileTypeException e) {
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", e.getMessage());
            return "review/form";

        } catch (FileStorageException e) {
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "review/form";
        }
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Review review = reviewService.findById(id);
        model.addAttribute("review", review);
        model.addAttribute("pageName", "리뷰 수정");
        return "review/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute Review review,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageName", "리뷰 수정");
            return "review/edit"; // 검증 미통과 시 수정 페이지로 포워드
        }
        reviewService.update(id, review, imageFile);
        redirectAttributes.addFlashAttribute("message", "리뷰가 수정되었습니다.");

        return "redirect:/reviews/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.delete(id);
        redirectAttributes.addFlashAttribute("message", "리뷰가 삭제되었습니다.");

        return "redirect:/reviews";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }
}
