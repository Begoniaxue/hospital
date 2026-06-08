package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.common.Result;
import com.hospital.entity.Complaint;
import com.hospital.entity.DoctorReview;
import com.hospital.entity.InpatientDeposit;
import com.hospital.entity.Patient;
import com.hospital.entity.Prescription;
import com.hospital.entity.Registration;
import com.hospital.entity.Settlement;
import com.hospital.mapper.InpatientDepositMapper;
import com.hospital.mapper.PatientMapper;
import com.hospital.mapper.PrescriptionMapper;
import com.hospital.mapper.RegistrationMapper;
import com.hospital.mapper.SettlementMapper;
import com.hospital.service.ComplaintService;
import com.hospital.service.DoctorReviewService;
import com.hospital.service.PatientService;
import com.hospital.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mini/profile")
public class MiniProfileController {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private SettlementMapper settlementMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private InpatientDepositMapper inpatientDepositMapper;

    @Autowired
    private DoctorReviewService doctorReviewService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrders(@RequestParam Long patientId,
                                                  @RequestParam(required = false, defaultValue = "all") String type) {
        List<Map<String, Object>> orders = new ArrayList<>();

        if ("all".equals(type) || "registration".equals(type)) {
            List<Registration> registrations = registrationMapper.selectList(
                    new LambdaQueryWrapper<Registration>()
                            .eq(Registration::getPatientId, patientId)
                            .orderByDesc(Registration::getCreateTime)
            );
            for (Registration reg : registrations) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", reg.getId());
                order.put("orderNo", reg.getRegistrationNo());
                order.put("type", "registration");
                order.put("typeName", "挂号订单");
                order.put("amount", reg.getRegistrationFee() != null ? reg.getRegistrationFee() : BigDecimal.ZERO);
                order.put("status", reg.getStatus());
                order.put("statusName", getRegistrationStatusName(reg.getStatus()));
                order.put("createTime", reg.getCreateTime());
                Map<String, Object> detail = new HashMap<>();
                detail.put("patientName", reg.getPatientName());
                detail.put("department", reg.getDepartment());
                detail.put("doctorName", reg.getDoctorName());
                detail.put("visitDate", reg.getVisitDate());
                detail.put("visitTimeSlot", reg.getVisitTimeSlot());
                detail.put("visitLocation", reg.getVisitLocation());
                detail.put("queueNumber", reg.getQueueNumber());
                order.put("detail", detail);
                orders.add(order);
            }
        }

        if ("all".equals(type) || "settlement".equals(type)) {
            List<Settlement> settlements = settlementMapper.selectList(
                    new LambdaQueryWrapper<Settlement>()
                            .eq(Settlement::getPatientId, patientId)
                            .orderByDesc(Settlement::getCreateTime)
            );
            for (Settlement set : settlements) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", set.getId());
                order.put("orderNo", set.getSettlementNo());
                order.put("type", "settlement");
                order.put("typeName", "缴费订单");
                order.put("amount", set.getActualAmount() != null ? set.getActualAmount() : BigDecimal.ZERO);
                order.put("status", set.getStatus());
                order.put("statusName", getSettlementStatusName(set.getStatus()));
                order.put("createTime", set.getCreateTime());
                Map<String, Object> detail = new HashMap<>();
                detail.put("patientName", set.getPatientName());
                detail.put("totalAmount", set.getTotalAmount());
                detail.put("discountAmount", set.getDiscountAmount());
                detail.put("registrationFee", set.getRegistrationFee());
                detail.put("drugFee", set.getDrugFee());
                detail.put("examFee", set.getExamFee());
                detail.put("otherFee", set.getOtherFee());
                detail.put("payMethod", set.getPayMethod());
                detail.put("settleTime", set.getSettleTime());
                order.put("detail", detail);
                orders.add(order);
            }
        }

        if ("all".equals(type) || "prescription".equals(type)) {
            List<Prescription> prescriptions = prescriptionMapper.selectList(
                    new LambdaQueryWrapper<Prescription>()
                            .eq(Prescription::getPatientId, patientId)
                            .orderByDesc(Prescription::getCreateTime)
            );
            for (Prescription pre : prescriptions) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", pre.getId());
                order.put("orderNo", pre.getPrescriptionNo());
                order.put("type", "prescription");
                order.put("typeName", "处方订单");
                order.put("amount", BigDecimal.ZERO);
                order.put("status", pre.getStatus());
                order.put("statusName", getPrescriptionStatusName(pre.getStatus()));
                order.put("createTime", pre.getCreateTime());
                Map<String, Object> detail = new HashMap<>();
                detail.put("patientName", pre.getPatientName());
                detail.put("doctorName", pre.getDoctorName());
                detail.put("diagnosis", pre.getDiagnosis());
                detail.put("auditTime", pre.getAuditTime());
                detail.put("dispenseTime", pre.getDispenseTime());
                detail.put("issueTime", pre.getIssueTime());
                order.put("detail", detail);
                orders.add(order);
            }
        }

        if ("all".equals(type) || "deposit".equals(type)) {
            List<InpatientDeposit> deposits = inpatientDepositMapper.selectList(
                    new LambdaQueryWrapper<InpatientDeposit>()
                            .eq(InpatientDeposit::getPatientId, patientId)
                            .orderByDesc(InpatientDeposit::getCreateTime)
            );
            for (InpatientDeposit dep : deposits) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", dep.getId());
                order.put("orderNo", dep.getDepositNo());
                order.put("type", "deposit");
                order.put("typeName", "住院预缴");
                order.put("amount", dep.getAmount() != null ? dep.getAmount() : BigDecimal.ZERO);
                order.put("status", dep.getStatus());
                order.put("statusName", getDepositStatusName(dep.getStatus()));
                order.put("createTime", dep.getCreateTime());
                Map<String, Object> detail = new HashMap<>();
                detail.put("patientName", dep.getPatientName());
                detail.put("inpatientNo", dep.getInpatientNo());
                detail.put("payMethod", dep.getPayMethod());
                detail.put("payTime", dep.getPayTime());
                detail.put("refundTime", dep.getRefundTime());
                order.put("detail", detail);
                orders.add(order);
            }
        }

        orders.sort((o1, o2) -> {
            LocalDateTime t1 = (LocalDateTime) o1.get("createTime");
            LocalDateTime t2 = (LocalDateTime) o2.get("createTime");
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", orders.size());
        return Result.success(result);
    }

    @PostMapping("/review")
    public Result<DoctorReview> submitReview(@RequestBody DoctorReview review) {
        review.setStatus(0);
        DoctorReview saved = doctorReviewService.saveReview(review);
        return Result.success(saved);
    }

    @GetMapping("/reviews")
    public Result<List<DoctorReview>> getReviews(@RequestParam Long patientId) {
        List<DoctorReview> reviews = doctorReviewService.listByPatientId(patientId);
        return Result.success(reviews);
    }

    @PostMapping("/complaint")
    public Result<Complaint> submitComplaint(@RequestBody Complaint complaint) {
        complaint.setStatus(0);
        Complaint saved = complaintService.saveComplaint(complaint);
        return Result.success(saved);
    }

    @GetMapping("/complaints")
    public Result<List<Complaint>> getComplaints(@RequestParam Long patientId) {
        List<Complaint> complaints = complaintService.listByPatientId(patientId);
        return Result.success(complaints);
    }

    @PostMapping("/updatePhone")
    public Result<?> updatePhone(@RequestBody Map<String, String> params) {
        Long patientId = Long.valueOf(params.get("patientId"));
        String oldPhone = params.get("oldPhone");
        String newPhone = params.get("newPhone");
        String smsCode = params.get("smsCode");
        Patient patient = patientService.getById(patientId);
        if (patient == null) {
            return Result.error("患者信息不存在");
        }
        if (!oldPhone.equals(patient.getPhone())) {
            return Result.error("原手机号不正确");
        }
        boolean verified = wechatUserService.verifySmsCode(newPhone, smsCode, "updatePhone");
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        patient.setPhone(newPhone);
        patientService.updatePatient(patient);
        return Result.success("手机号修改成功");
    }

    @GetMapping("/messageSettings")
    public Result<Map<String, Boolean>> getMessageSettings(@RequestParam Long patientId) {
        Map<String, Boolean> settings = new HashMap<>();
        String prefix = "message:setting:" + patientId + ":";
        settings.put("appointmentNotice", getBooleanSetting(prefix + "appointmentNotice", true));
        settings.put("paymentNotice", getBooleanSetting(prefix + "paymentNotice", true));
        settings.put("reportNotice", getBooleanSetting(prefix + "reportNotice", true));
        settings.put("systemNotice", getBooleanSetting(prefix + "systemNotice", true));
        return Result.success(settings);
    }

    @PostMapping("/messageSettings")
    public Result<?> saveMessageSettings(@RequestBody Map<String, Object> params) {
        Long patientId = Long.valueOf(params.get("patientId").toString());
        String prefix = "message:setting:" + patientId + ":";
        if (params.containsKey("appointmentNotice")) {
            setBooleanSetting(prefix + "appointmentNotice", Boolean.TRUE.equals(params.get("appointmentNotice")));
        }
        if (params.containsKey("paymentNotice")) {
            setBooleanSetting(prefix + "paymentNotice", Boolean.TRUE.equals(params.get("paymentNotice")));
        }
        if (params.containsKey("reportNotice")) {
            setBooleanSetting(prefix + "reportNotice", Boolean.TRUE.equals(params.get("reportNotice")));
        }
        if (params.containsKey("systemNotice")) {
            setBooleanSetting(prefix + "systemNotice", Boolean.TRUE.equals(params.get("systemNotice")));
        }
        return Result.success("消息设置保存成功");
    }

    @GetMapping("/about")
    public Result<Map<String, String>> getAbout() {
        Map<String, String> about = new HashMap<>();
        about.put("hospitalName", "智慧医院");
        about.put("introduction", "智慧医院是一家集医疗、教学、科研、预防、保健、康复为一体的现代化综合性医院。医院秉承\"以患者为中心\"的服务理念，致力于为广大患者提供优质、高效、便捷的医疗服务。");
        about.put("address", "北京市朝阳区健康路88号");
        about.put("phone", "400-888-8888");
        about.put("website", "www.hospital.com");
        about.put("workingHours", "周一至周日 8:00-17:30（急诊24小时）");
        return Result.success(about);
    }

    @GetMapping("/privacy")
    public Result<Map<String, String>> getPrivacy() {
        Map<String, String> privacy = new HashMap<>();
        privacy.put("title", "隐私协议");
        privacy.put("content", "我们非常重视您的个人信息和隐私保护。为了给您提供更准确、更有个性化的服务，我们会按照本隐私协议的规定使用和披露您的个人信息。我们将以高度的勤勉、审慎义务对待这些信息。除本隐私协议另有规定外，在未征得您事先许可的情况下，我们不会将这些信息对外披露或向第三方提供。我们会不时更新本隐私协议。您在同意我们服务使用协议之时，即视为您已经同意本隐私协议全部内容。");
        return Result.success(privacy);
    }

    private String getRegistrationStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已签到";
            case 3: return "已取消";
            case 4: return "已完成";
            case 5: return "已退款";
            default: return "未知";
        }
    }

    private String getSettlementStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已退款";
            default: return "未知";
        }
    }

    private String getPrescriptionStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已审核";
            case 2: return "已配药";
            case 3: return "已发药";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    private String getDepositStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已退款";
            default: return "未知";
        }
    }

    private boolean getBooleanSetting(String key, boolean defaultValue) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equals(value);
    }

    private void setBooleanSetting(String key, boolean value) {
        redisTemplate.opsForValue().set(key, value ? "true" : "false");
    }
}
