package com.wms.backend.service;

import com.wms.backend.domain.enums.IssueType;
import com.wms.backend.domain.model.Address;
import com.wms.backend.domain.model.Issue;
import com.wms.backend.domain.model.IssueMessage;
import com.wms.backend.domain.model.User;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.IssueMessageRepository;
import com.wms.backend.repo.IssueRepository;
import com.wms.backend.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueMessageRepository messageRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public IssueService(
            IssueRepository issueRepository,
            IssueMessageRepository messageRepository,
            AddressRepository addressRepository,
            UserRepository userRepository
    ) {
        this.issueRepository = issueRepository;
        this.messageRepository = messageRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Issue createIssue(Long userId, IssueType type, String description, String photoUrl) {
        User user = userRepository.findById(userId).orElseThrow();
        Address address = addressRepository.findByUserId(userId).orElse(null);

        Issue issue = new Issue();
        issue.setCreatedBy(user);
        issue.setAddress(address);
        issue.setIssueType(type);
        issue.setDescription(description);
        issue.setPhotoUrl(photoUrl);

        return issueRepository.save(issue);
    }

    public List<Issue> listMyIssues(Long userId) {
        return issueRepository.findByCreatedByIdOrderByCreatedAtDesc(userId);
    }

    public List<IssueMessage> listMessages(Long issueId) {
        return messageRepository.findByIssueIdOrderByCreatedAtAsc(issueId);
    }

    @Transactional
    public IssueMessage addMessage(Long issueId, Long senderId, String message) {
        Issue issue = issueRepository.findById(issueId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();

        IssueMessage msg = new IssueMessage();
        msg.setIssue(issue);
        msg.setSender(sender);
        msg.setMessage(message);
        return messageRepository.save(msg);
    }
}

