# Debug Session: api-500-error

**Status**: [CLOSED] ✅
**Created**: 2026-06-03
**Session ID**: api-500-error

---

## Problem Description
- **Symptom**: 用户反馈接口返回 500 错误
- **Expected**: 接口应正常返回数据
- **Actual**: 返回 HTTP 500 内部服务器错误

---

## Hypotheses (可证伪假设)

1. **H1: 数据库连接问题** ✅ **CONFIRMED** - Redis 服务未运行，导致验证码接口写入 Redis 时抛出 `RedisConnectionFailureException`
2. **H2: 空指针异常** ❌ **REJECTED** - 代码逻辑正常，无空指针风险
3. **H3: SQL 语法错误或数据不存在** ❌ **REJECTED** - 验证码接口不涉及数据库查询
4. **H4: 依赖注入/循环引用问题** ❌ **REJECTED** - Spring 容器启动正常
5. **H5: 配置缺失或错误** ❌ **REJECTED** - application.yml 配置完整

---

## Evidence Log

| Timestamp | Event | Data |
|-----------|-------|------|
| 2026-06-03 | 问题确认 | GET /api/auth/captcha 返回 HTTP 500 |
| 2026-06-03 | Redis 状态检查 | `redis-cli ping` 返回 `Connection refused`，Redis 未运行 |
| 2026-06-03 | 启动 Redis | `redis-server --daemonize yes` 启动成功 |
| 2026-06-03 | 修复验证 | 接口返回 200，包含 uuid 和 base64 验证码图片 |

---

## Root Cause Analysis

**根本原因**：Redis 服务未启动，导致 [AuthController.java](file:///Users/a1234/Documents/trae_projects/hospital-/hospital-admin-server/src/main/java/com/hospital/controller/AuthController.java#L86) 第86行 `redisTemplate.opsForValue().set()` 操作抛出 `RedisConnectionFailureException`，触发全局异常处理器返回 HTTP 500。

**Fix**：启动 Redis 服务（`redis-server --daemonize yes`）

---

## Fix Summary
- **操作**：启动 Redis 后台服务
- **耗时**：3秒
- **影响范围**：所有使用 Redis 的接口（验证码、Token 缓存等）
- **相关代码**：[AuthController.captcha()](file:///Users/a1234/Documents/trae_projects/hospital-/hospital-admin-server/src/main/java/com/hospital/controller/AuthController.java#L81-L94)

---

## Verification ✅

**Pre-fix** (HTTP 500):
```json
{"timestamp":"2026-06-03T02:29:27.949+00:00","status":500,"error":"Internal Server Error","path":"/api/auth/captcha"}
```

**Post-fix** (HTTP 200):
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "img": "data:image/png;base64,...",
        "uuid": "a439bda8-6d16-4d0d-bd40-0dea3fd6f295"
    }
}
```
