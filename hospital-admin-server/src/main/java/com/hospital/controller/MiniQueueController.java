package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.CallingQueue;
import com.hospital.service.CallingQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mini/queue")
public class MiniQueueController {

    @Autowired
    private CallingQueueService callingQueueService;

    @GetMapping("/registration/{registrationId}")
    public Result<CallingQueue> getQueueByRegistration(@PathVariable Long registrationId) {
        CallingQueue queue = callingQueueService.getByRegistrationId(registrationId);
        return Result.success(queue);
    }

    @GetMapping("/doctor/{doctorId}")
    public Result<List<CallingQueue>> getQueueByDoctor(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<CallingQueue> queues = callingQueueService.getByDoctorAndDate(doctorId, date);
        return Result.success(queues);
    }

    @GetMapping("/current/{doctorId}")
    public Result<CallingQueue> getCurrentNumber(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        CallingQueue queue = callingQueueService.getCurrentQueue(doctorId, date);
        return Result.success(queue);
    }
}
