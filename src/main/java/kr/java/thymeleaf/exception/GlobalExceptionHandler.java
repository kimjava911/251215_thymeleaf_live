package kr.java.thymeleaf.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException e,
                                         // org.springframework.ui.Model
                                         Model model,
                                         HttpServletRequest request) {
        log.warn("파일 크기 초과 - IP: {}, URL: {}",
                request.getRemoteAddr(), request.getRequestURI());

        model.addAttribute("errorMessage", "파일 크기가 너무 큽니다. (최대 10MB)");
        model.addAttribute("errorDetail", "더 작은 파일을 선택해주세요.");

        return "error/error";
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public String handleInvalidFileType(InvalidFileTypeException e, Model model) {
        log.info("잘못된 파일 타입 업로드 시도: {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorDetail", "JPG, PNG, GIF, WebP 형식의 이미지만 업로드 가능합니다.");

        return "error/error";
    }

    @ExceptionHandler(FileStorageException.class)
    public String handleFileStorage(FileStorageException e, Model model) {
        // ERROR 레벨: 시스템 문제 가능성
        log.error("파일 저장 실패: {}", e.getMessage());

        model.addAttribute("errorMessage", "파일 저장 중 문제가 발생했습니다.");
        model.addAttribute("errorDetail", "잠시 후 다시 시도해주세요.");

        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        log.error("예상치 못한 예외 발생", e);

        model.addAttribute("errorMessage", "서비스 이용 중 문제가 발생했습니다.");
        model.addAttribute("errorDetail", "문제가 지속되면 관리자에게 문의해주세요.");

        return "error/error";
    }
}
