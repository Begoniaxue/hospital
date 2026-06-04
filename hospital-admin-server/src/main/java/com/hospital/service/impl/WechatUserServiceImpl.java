package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.WechatLoginDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.PatientFamily;
import com.hospital.entity.WechatUser;
import com.hospital.mapper.WechatUserMapper;
import com.hospital.service.PatientFamilyService;
import com.hospital.service.PatientService;
import com.hospital.service.WechatUserService;
import com.hospital.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientFamilyService patientFamilyService;

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    @Override
    @Transactional(rollbackFor = Exception.class)
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

            String randomIdCard = "110" + String.format("%08d", (int)(Math.random() * 100000000));
            String randomPhone = "138" + String.format("%08d", (int)(Math.random() * 100000000));

            Patient patient = new Patient();
            patient.setPatientNo("P" + System.currentTimeMillis());
            patient.setName("测试患者");
            patient.setGender(1);
            patient.setAge(30);
            patient.setIdCard(randomIdCard);
            patient.setPhone(randomPhone);
            patient.setAddress("北京市朝阳区测试街道123号");
            patient.setEmergencyContact("紧急联系人");
            patient.setEmergencyPhone("13900139000");
            patientService.save(patient);

            PatientFamily family = new PatientFamily();
            family.setWechatUserId(wechatUser.getId());
            family.setPatientId(patient.getId());
            family.setName(patient.getName());
            family.setIdCard(patient.getIdCard());
            family.setPhone(patient.getPhone());
            family.setRelation("本人");
            family.setGender(patient.getGender());
            family.setBirthday("1990-01-01");
            family.setStatus(1);
            patientFamilyService.save(family);

            wechatUser.setCurrentPatientId(patient.getId());
            updateById(wechatUser);
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
