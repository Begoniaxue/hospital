package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.MessageNotification;
import com.hospital.entity.Registration;
import com.hospital.mapper.MessageNotificationMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.service.MessageNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageNotificationServiceImpl extends ServiceImpl<MessageNotificationMapper, MessageNotification> implements MessageNotificationService {

    @Autowired
    private MessageNotificationMapper messageNotificationMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Override
    public boolean sendRegistrationSuccess(Long registrationId, Long wechatUserId, Long patientId, String patientName) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return false;
        }
        MessageNotification notification = new MessageNotification();
        notification.setWechatUserId(wechatUserId);
        notification.setPatientId(patientId);
        notification.setPatientName(patientName);
        notification.setMsgType("REGISTRATION_SUCCESS");
        notification.setTitle("挂号成功通知");
        notification.setContent("您已成功预约" + registration.getDepartment() + " " + registration.getDoctorName() + "医生的号源，请准时就诊。");
        notification.setPagePath("/pages/registration/detail?id=" + registrationId);
        notification.setMsgStatus(1);
        notification.setSendTime(LocalDateTime.now());
        notification.setReadStatus(0);
        notification.setCreateTime(LocalDateTime.now());
        return save(notification);
    }

    @Override
    public boolean sendRegistrationCancel(Long registrationId, Long wechatUserId, Long patientId, String patientName) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return false;
        }
        MessageNotification notification = new MessageNotification();
        notification.setWechatUserId(wechatUserId);
        notification.setPatientId(patientId);
        notification.setPatientName(patientName);
        notification.setMsgType("REGISTRATION_CANCEL");
        notification.setTitle("挂号取消通知");
        notification.setContent("您已取消" + registration.getDepartment() + " " + registration.getDoctorName() + "医生的预约。");
        notification.setPagePath("/pages/registration/list");
        notification.setMsgStatus(1);
        notification.setSendTime(LocalDateTime.now());
        notification.setReadStatus(0);
        notification.setCreateTime(LocalDateTime.now());
        return save(notification);
    }

    @Override
    public boolean sendCallingReminder(Long registrationId, Long wechatUserId, Long patientId, String patientName, Integer queueNumber, String roomNo) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return false;
        }
        MessageNotification notification = new MessageNotification();
        notification.setWechatUserId(wechatUserId);
        notification.setPatientId(patientId);
        notification.setPatientName(patientName);
        notification.setMsgType("CALLING_REMINDER");
        notification.setTitle("叫号提醒");
        notification.setContent("请" + patientName + "患者到" + roomNo + "就诊，您的号码是" + queueNumber + "号。");
        notification.setPagePath("/pages/registration/detail?id=" + registrationId);
        notification.setMsgStatus(1);
        notification.setSendTime(LocalDateTime.now());
        notification.setReadStatus(0);
        notification.setCreateTime(LocalDateTime.now());
        return save(notification);
    }

    @Override
    public boolean sendVisitReminder(Long registrationId, Long wechatUserId, Long patientId, String patientName) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            return false;
        }
        MessageNotification notification = new MessageNotification();
        notification.setWechatUserId(wechatUserId);
        notification.setPatientId(patientId);
        notification.setPatientName(patientName);
        notification.setMsgType("VISIT_REMINDER");
        notification.setTitle("就诊提醒");
        notification.setContent("您好，您明天有" + registration.getDepartment() + " " + registration.getDoctorName() + "医生的预约，请准时就诊。");
        notification.setPagePath("/pages/registration/detail?id=" + registrationId);
        notification.setMsgStatus(1);
        notification.setSendTime(LocalDateTime.now());
        notification.setReadStatus(0);
        notification.setCreateTime(LocalDateTime.now());
        return save(notification);
    }

    @Override
    public List<MessageNotification> getByPatientId(Long patientId, Integer readStatus) {
        com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper<MessageNotification> query = lambdaQuery().eq(MessageNotification::getPatientId, patientId);
        if (readStatus != null) {
            query.eq(MessageNotification::getReadStatus, readStatus);
        }
        return query.orderByDesc(MessageNotification::getCreateTime).list();
    }

    @Override
    public boolean markAsRead(Long id) {
        MessageNotification notification = getById(id);
        if (notification == null) {
            return false;
        }
        notification.setReadStatus(1);
        notification.setReadTime(LocalDateTime.now());
        return updateById(notification);
    }

    @Override
    public List<MessageNotification> getByWechatUserId(Long wechatUserId, Integer offset, Integer limit) {
        return lambdaQuery()
                .eq(MessageNotification::getWechatUserId, wechatUserId)
                .orderByDesc(MessageNotification::getCreateTime)
                .last("LIMIT " + offset + ", " + limit)
                .list();
    }

    @Override
    public int countUnread(Long wechatUserId) {
        return lambdaQuery()
                .eq(MessageNotification::getWechatUserId, wechatUserId)
                .eq(MessageNotification::getReadStatus, 0)
                .count()
                .intValue();
    }

    @Override
    public boolean markAllAsRead(Long wechatUserId) {
        List<MessageNotification> list = lambdaQuery()
                .eq(MessageNotification::getWechatUserId, wechatUserId)
                .eq(MessageNotification::getReadStatus, 0)
                .list();
        for (MessageNotification notification : list) {
            notification.setReadStatus(1);
            notification.setReadTime(LocalDateTime.now());
        }
        return updateBatchById(list);
    }

    @Override
    public int getUnreadCount(Long patientId) {
        return lambdaQuery()
                .eq(MessageNotification::getPatientId, patientId)
                .eq(MessageNotification::getReadStatus, 0)
                .count()
                .intValue();
    }
}
