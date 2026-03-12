package com.wms.backend.web;

import com.wms.backend.domain.enums.IssueType;
import com.wms.backend.domain.model.IssueMessage;
import com.wms.backend.repo.IssueRepository;
import com.wms.backend.service.FileStorageService;
import com.wms.backend.service.IssueService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@Validated
public class IssueController {

    private final IssueService issueService;
    private final IssueRepository issueRepository;
    private final FileStorageService fileStorageService;

    public IssueController(IssueService issueService, IssueRepository issueRepository, FileStorageService fileStorageService) {
        this.issueService = issueService;
        this.issueRepository = issueRepository;
        this.fileStorageService = fileStorageService;
    }

    public record CreateIssueRequest(@NotNull IssueType issueType, String description) {
    }

    public record IssueDto(
            Long id,
            String issueType,
            String status,
            String description,
            String photoUrl,
            String createdAtIso
    ) {
    }

    public record SenderDto(Long id, String email, String phone, String role) {
    }

    public record MessageDto(Long id, SenderDto sender, String message, String createdAtIso) {
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<IssueDto> create(
            @RequestPart("data") CreateIssueRequest data,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        var principal = SecurityUtils.principal();
        var issue = issueService.createIssue(principal.getId(), data.issueType(), data.description(), null);

        if (photo != null && !photo.isEmpty()) {
            var stored = fileStorageService.storeIssuePhoto(issue.getId(), photo);
            issue.setPhotoUrl(stored.publicUrl());
            issue = issueRepository.save(issue);
        }

        return ResponseEntity.ok(toIssueDto(issue));
    }

    @GetMapping("/me")
    public ResponseEntity<List<IssueDto>> myIssues() {
        var principal = SecurityUtils.principal();
        return ResponseEntity.ok(issueService.listMyIssues(principal.getId()).stream().map(this::toIssueDto).toList());
    }

    public record AddMessageRequest(@NotBlank String message) {
    }

    @GetMapping("/{issueId}/messages")
    public ResponseEntity<List<MessageDto>> messages(@PathVariable Long issueId) {
        return ResponseEntity.ok(issueService.listMessages(issueId).stream().map(this::toMessageDto).toList());
    }

    @PostMapping("/{issueId}/messages")
    public ResponseEntity<MessageDto> addMessage(@PathVariable Long issueId, @RequestBody AddMessageRequest req) {
        var principal = SecurityUtils.principal();
        return ResponseEntity.ok(toMessageDto(issueService.addMessage(issueId, principal.getId(), req.message())));
    }

    private IssueDto toIssueDto(com.wms.backend.domain.model.Issue issue) {
        return new IssueDto(
                issue.getId(),
                issue.getIssueType().name(),
                issue.getStatus().name(),
                issue.getDescription(),
                issue.getPhotoUrl(),
                issue.getCreatedAt() == null ? null : issue.getCreatedAt().toString()
        );
    }

    private MessageDto toMessageDto(IssueMessage msg) {
        var sender = msg.getSender();
        SenderDto senderDto = new SenderDto(sender.getId(), sender.getEmail(), sender.getPhone(), sender.getRole().name());
        return new MessageDto(
                msg.getId(),
                senderDto,
                msg.getMessage(),
                msg.getCreatedAt() == null ? null : msg.getCreatedAt().toString()
        );
    }
}
