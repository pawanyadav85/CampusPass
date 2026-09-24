package com.campuspass.controller;

import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.NotificationResponse;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyNotifications(@AuthenticationPrincipal UserPrincipal principal) {
        List<NotificationResponse> list = notificationService.getUserNotifications(principal.getId());
        long unreadCount = notificationService.getUnreadCount(principal.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("notifications", list);
        data.put("unreadCount", unreadCount);

        return ResponseEntity.ok(ApiResponse.success("Notifications fetched", data));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
