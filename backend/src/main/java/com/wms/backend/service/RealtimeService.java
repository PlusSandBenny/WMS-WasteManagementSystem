package com.wms.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeService {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishFinanceUnpaidChanged(String lga, Object payload) {
        messagingTemplate.convertAndSend("/topic/finance/unpaid/" + sanitize(lga), payload);
    }

    public void publishResidentStatusChanged(Long addressId, Object payload) {
        messagingTemplate.convertAndSend("/topic/resident/status/" + addressId, payload);
    }

    private String sanitize(String s) {
        if (s == null) {
            return "unknown";
        }
        return s.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}

