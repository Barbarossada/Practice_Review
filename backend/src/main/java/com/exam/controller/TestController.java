package com.exam.controller;

import com.exam.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试 Controller
 * 用于验证项目是否正常启动
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * 健康检查接口
     * 
     * @return 系统状态
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "ok");
        data.put("message", "期末复习题库系统运行正常");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "0.0.1");
        
        return Result.success(data);
    }

    /**
     * 欢迎页面
     * 
     * @return 欢迎信息
     */
    @GetMapping("/welcome")
    public Result<String> welcome() {
        return Result.success("欢迎使用期末复习在线题库系统！");
    }
}
