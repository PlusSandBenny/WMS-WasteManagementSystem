package com.wms.backend.repo;

import com.wms.backend.domain.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
}

