package com.exam.examapp.controller.information;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.information.AboutResponse;
import com.exam.examapp.service.interfaces.information.AboutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/about")
@Tag(
    name = "About Information",
    description = "Endpoints for retrieving about information of the application")
public class AboutController {
  private final AboutService aboutService;

  @GetMapping
  @Operation(
      summary = "Get about information",
      description = "Retrieve the application's about information.")
  public ResponseEntity<ApiResponse<AboutResponse>> get() {
    AboutResponse about = aboutService.getAbout();
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Haqqında uğurla əldə edildi", about));
  }
}
