package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.MessageNotification;
import com.hospital.service.MessageNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mini/message")
public class MiniMessageController {

    @Autowired
    private MessageNotificationService messageNotificationService;

    @GetMapping("/list/{wechatUserId}")
    public Result<List<MessageNotification>> getMessageList(
            @PathVariable Long wechatUserId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<MessageNotification> messages = messageNotificationService.getByWechatUserId(wechatUserId, offset, limit);
        return Result.success(messages);
    }

    @GetMapping("/unread/{wechatUserId}")
    public Result<Integer> getUnreadCount(@PathVariable Long wechatUserId) {
        Integer count = messageNotificationService.countUnread(wechatUserId);
        return Result.success(count);
    }

    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageNotificationService.markAsRead(id);
        return Result.success();
    }

    @PostMapping("/read-all/{wechatUserId}")
    public Result<Void> markAllAsRead(@PathVariable Long wechatUserId) {
        messageNotificationService.markAllAsRead(wechatUserId);
        return Result.success();
    }
}
