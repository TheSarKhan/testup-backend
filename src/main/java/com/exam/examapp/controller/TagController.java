package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.Tag;
import com.exam.examapp.service.interfaces.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "APIs for managing tags")
public class TagController {
  private final TagService tagService;

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create Tag", description = "Create a new tag (ADMIN only)")
  public ResponseEntity<ApiResponse<Void>> createTag(@RequestParam @NotBlank String tagName) {
    tagService.createTag(tagName);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.CREATED, "Teq uğurla yaradıldı", null));
  }

  @GetMapping
  @Operation(summary = "Get All Tags", description = "Retrieve all available tags")
  public ResponseEntity<ApiResponse<List<Tag>>> getAllTags() {
    List<Tag> allTags = tagService.getAllTags();
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Teqlər uğurla əldə edildi", allTags));
  }

  @GetMapping("/name")
  @Operation(summary = "Get Tag by Name", description = "Retrieve a specific tag by its name")
  public ResponseEntity<ApiResponse<Tag>> getTagsByName(@RequestParam @NotBlank String tagName) {
    Tag tagByName = tagService.getTagByName(tagName);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Teqlər uğurla əldə edildi", tagByName));
  }

  @GetMapping("/id")
  @Operation(summary = "Get Tag by ID", description = "Retrieve a specific tag by its unique ID")
  public ResponseEntity<ApiResponse<Tag>> getTagsById(@RequestParam UUID id) {
    Tag tagById = tagService.getTagById(id);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Teqlər uğurla əldə edildi", tagById));
  }

  @PutMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update Tag", description = "Update an existing tag by ID (ADMIN only)")
  public ResponseEntity<ApiResponse<Void>> updateTag(
      @RequestParam UUID id, @RequestParam @NotBlank String tagName) {
    tagService.updateTag(id, tagName);
    return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Teq uğurla yeniləndi", null));
  }

  @DeleteMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete Tag", description = "Delete a tag by its ID (ADMIN only)")
  public ResponseEntity<ApiResponse<Void>> deleteTag(@RequestParam UUID id) {
    tagService.deleteTag(id);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.NO_CONTENT, "Teq uğurla silindi", null));
  }
}
