package com.wms.backend.repo;

import com.wms.backend.domain.model.IssueMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueMessageRepository extends JpaRepository<IssueMessage, Long> {
    List<IssueMessage> findByIssueIdOrderByCreatedAtAsc(Long issueId);
}

