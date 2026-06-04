package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.MessageNotification;

import java.util.List;

public interface MessageNotificationService extends IService<MessageNotification> {

    boolean sendRegistrationSuccess(Long registrationId, Long wechatUserId, Long patientId, String patientName);

    boolean sendRegistrationCancel(Long registrationId, Long wechatUserId, Long patientId, String patientName);

    boolean sendCallingReminder(Long registrationId, Long wechatUserId, Long patientId, String patientName, Integer queueNumber, String roomNo);

    boolean sendVisitReminder(Long registrationId, Long wechatUserId, Long patientId, String patientName);

    List<MessageNotification> getByPatientId(Long patientId, Integer readStatus);

    List<MessageNotification> getByWechatUserId(Long wechatUserId, Integer offset, Integer limit);

    int countUnread(Long wechatUserId);

    boolean markAsRead(Long id);

    boolean markAllAsRead(Long wechatUserId);

    int getUnreadCount(Long patientId);
}
