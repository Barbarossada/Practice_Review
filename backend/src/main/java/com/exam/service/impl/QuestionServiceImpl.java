package com.exam.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import com.exam.service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * 题目 Service 实现类
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public Page<Question> getQuestionPage(Long page, Long size, String subject, String type, String difficulty) {
        Page<Question> pageParam = new Page<>(page, size);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq("type", type);
        }
        if (StrUtil.isNotBlank(difficulty)) {
            wrapper.eq("difficulty", difficulty);
        }

        // 按创建时间倒序
        wrapper.orderByDesc("create_time");

        return this.page(pageParam, wrapper);
    }

    @Override
    public Question getRandomQuestion(String subject, String type, String difficulty) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq("type", type);
        }
        if (StrUtil.isNotBlank(difficulty)) {
            wrapper.eq("difficulty", difficulty);
        }

        // 获取符合条件的题目数量
        long count = this.count(wrapper);
        if (count == 0) {
            return null;
        }

        // 随机偏移量
        int offset = RandomUtil.randomInt(0, (int) count);
        wrapper.last("LIMIT 1 OFFSET " + offset);

        return this.getOne(wrapper);
    }

    @Override
    public int clearAll(String subject, String type) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        
        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq("type", type);
        }
        
        // 先统计数量
        int count = (int) this.count(wrapper);
        
        // 删除符合条件的题目
        if (count > 0) {
            this.remove(wrapper);
        }
        
        return count;
    }
}
