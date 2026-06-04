package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.MessageNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageNotificationMapper extends BaseMapper<MessageNotification> {

    @Select("SELECT * FROM message_notification WHERE wechat_user_id = #{wechatUserId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<MessageNotification> selectByWechatUserId(@Param("wechatUserId") Long wechatUserId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM message_notification WHERE wechat_user_id = #{wechatUserId} AND read_status = 0")
    Integer countUnread(@Param("wechatUserId") Long wechatUserId);

    @Update("UPDATE message_notification SET read_status = 1, read_time = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);

    @Update("UPDATE message_notification SET read_status = 1, read_time = NOW() WHERE wechat_user_id = #{wechatUserId} AND read_status = 0")
    int markAllAsRead(@Param("wechatUserId") Long wechatUserId);

    @Select("SELECT * FROM message_notification WHERE msg_status = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<MessageNotification> selectPendingMessages(@Param("limit") Integer limit);
}
