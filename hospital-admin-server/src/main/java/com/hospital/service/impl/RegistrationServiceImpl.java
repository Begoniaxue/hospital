package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Registration;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.service.RegistrationService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {
}
