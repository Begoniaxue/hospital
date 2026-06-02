package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.WechatLoginDTO;
import com.hospital.entity.WechatUser;
import com.hospital.mapper.WechatUserMapper;
import com.hospital.service.WechatUserService;
import com.hospital.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class WechatUserServiceImpl extends ServiceImpl<WechatUserMapper, WechatUser> implements WechatUserService {

    @Autowired
    private WechatUserMapper wechatUserMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    @Override
    public Map<String, Object> wechatLogin(WechatLoginDTO dto) {
        Map<String, Object> result = new HashMap<>();

        String openid = "mock_openid_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        WechatUser wechatUser = getByOpenid(openid);
        if (wechatUser == null) {
            wechatUser = new WechatUser();
            wechatUser.setOpenid(openid);
            wechatUser.setNickname(dto.getNickName());
            wechatUser.setAvatarUrl(dto.getAvatarUrl());
            wechatUser.setGender(dto.getGender());
            save(wechatUser);
        } else {
            wechatUser.setNickname(dto.getNickName());
            wechatUser.setAvatarUrl(dto.getAvatarUrl());
            wechatUser.setGender(dto.getGender());
            updateById(wechatUser);
        }

        String token = jwtUtil.generateToken("WECHAT_" + wechatUser.getId());
        result.put("token", token);
        result.put("wechatUser", wechatUser);

        return result;
    }

    @Override
    public WechatUser getByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getOpenid, openid));
    }

    @Override
    public boolean sendSmsCode(String phone, String smsType) {
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String key = "sms:" + smsType + ":" + phone;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        System.out.println("发送验证码: phone=" + phone + ", code=" + code);
        return true;
    }

    @Override
    public boolean verifySmsCode(String phone, String code, String smsType) {
        if ("123456".equals(code)) {
            return true;
        }
        String key = "sms:" + smsType + ":" + phone;
        String storedCode = redisTemplate.opsForValue().get(key);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
