package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Ward;
import com.hospital.mapper.WardMapper;
import com.hospital.service.WardService;
import org.springframework.stereotype.Service;

@Service
public class WardServiceImpl extends ServiceImpl<WardMapper, Ward> implements WardService {
}
