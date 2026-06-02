package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.WechatLoginDTO;
import com.hospital.entity.WechatUser;

import java.util.Map;

public interface WechatUserService extends IService<WechatUser> {

    Map<String, Object> wechatLogin(WechatLoginDTO dto);

    WechatUser getByOpenid(String openid);

    boolean sendSmsCode(String phone, String smsType);

    boolean verifySmsCode(String phone, String code, String smsType);
}
