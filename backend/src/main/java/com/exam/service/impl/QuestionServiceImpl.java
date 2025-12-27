package com.exam.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import com.exam.service.QuestionService;
import com.exam.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目 Service 实现类
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 重排指定科目+类型的display_order
     */
    private void reorderDisplayOrder(String subject, String type) {
        jdbcTemplate.execute("SET @row_num = 0");
        jdbcTemplate.update(
            "UPDATE question SET display_order = (@row_num := @row_num + 1) WHERE subject = ? AND type = ? ORDER BY display_order",
            subject, type
        );
    }

    @Override
    public Page<Question> getQuestionPage(Long page, Long size, String subject, String type, String difficulty) {
        log.info("======= QuestionService.getQuestionPage =======");
        log.info("参数: page={}, size={}, subject={}, type={}, difficulty={}", page, size, subject, type, difficulty);
        
        Page<Question> pageParam = new Page<>(page, size);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            log.debug("添加subject条件: {}", subject);
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            log.debug("添加type条件: {}", type);
            wrapper.eq("type", type);
        }
        if (StrUtil.isNotBlank(difficulty)) {
            log.debug("添加difficulty条件: {}", difficulty);
            wrapper.eq("difficulty", difficulty);
        }

        // 按创建时间倒序
        wrapper.orderByDesc("create_time");
        
        log.debug("SQL: {}", wrapper.getTargetSql());

        Page<Question> result = this.page(pageParam, wrapper);
        log.info("查询结果: total={}, records={}", result.getTotal(), result.getRecords().size());
        log.info("===========================================");
        return result;
    }

    @Override
    public Question getRandomQuestion(String subject, String type, String difficulty) {
        log.info("======= QuestionService.getRandomQuestion =======");
        log.info("参数: subject='{}', type='{}', difficulty='{}'", subject, type, difficulty);
        
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            log.debug("添加subject条件: {}", subject);
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            log.debug("添加type条件: {}", type);
            wrapper.eq("type", type);
        }
        if (StrUtil.isNotBlank(difficulty)) {
            log.debug("添加difficulty条件: {}", difficulty);
            wrapper.eq("difficulty", difficulty);
        }

        // 获取符合条件的题目数量
        long count = this.count(wrapper);
        log.info("符合条件的题目数量: {}", count);
        if (count == 0) {
            return null;
        }

        // 随机偏移量
        int offset = RandomUtil.randomInt(0, (int) count);
        log.info("随机偏移量: {}", offset);
        wrapper.last("LIMIT 1 OFFSET " + offset);

        Question result = this.getOne(wrapper);
        if (result != null) {
            log.info("返回题目: ID={}, subject='{}', type='{}', content='{}...'", 
                result.getId(), result.getSubject(), result.getType(), 
                result.getContent().substring(0, Math.min(50, result.getContent().length())));
        }
        log.info("===========================================");
        return result;
    }

    @Override
    @Transactional
    public int clearAll(String subject, String type) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq("subject", subject);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq("type", type);
        }

        // 先统计各科目和类型的删除数量
        List<Question> questionsToDelete = this.list(wrapper);
        Map<String, Integer> subjectCountMap = new HashMap<>();
        Map<String, java.util.Set<String>> subjectTypeMap = new HashMap<>();

        for (Question q : questionsToDelete) {
            String subjectName = q.getSubject();
            String typeName = q.getType();

            subjectCountMap.put(subjectName, subjectCountMap.getOrDefault(subjectName, 0) + 1);

            subjectTypeMap.computeIfAbsent(subjectName, k -> new java.util.HashSet<>()).add(typeName);
        }

        // 删除符合条件的题目
        int count = questionsToDelete.size();
        if (count > 0) {
            this.remove(wrapper);

            // 更新科目表
            for (Map.Entry<String, Integer> entry : subjectCountMap.entrySet()) {
                subjectService.decrementQuestionCount(entry.getKey(), entry.getValue());
            }

            // 重排涉及的所有科目+类型组合的display_order
            for (Map.Entry<String, java.util.Set<String>> entry : subjectTypeMap.entrySet()) {
                String subjectName = entry.getKey();
                for (String typeName : entry.getValue()) {
                    reorderDisplayOrder(subjectName, typeName);
                }
            }
        }

        return count;
    }

    @Override
    @Transactional
    public boolean saveQuestion(Question question) {
        // 确保科目不为空
        if (StrUtil.isBlank(question.getSubject())) {
            question.setSubject("未分类");
        }
        
        boolean success = this.save(question);
        if (success) {
            // 更新科目表
            subjectService.incrementQuestionCount(question.getSubject(), 1);
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateQuestion(Question question) {
        // 获取旧的题目信息
        Question oldQuestion = this.getById(question.getId());
        if (oldQuestion == null) {
            return false;
        }
        
        boolean success = this.updateById(question);
        if (success) {
            // 如果科目发生变化，需要更新两个科目的计数
            if (!oldQuestion.getSubject().equals(question.getSubject())) {
                subjectService.decrementQuestionCount(oldQuestion.getSubject(), 1);
                subjectService.incrementQuestionCount(question.getSubject(), 1);
            }
        }
        return success;
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Long id) {
        Question question = this.getById(id);
        if (question == null) {
            return false;
        }
        
        String type = question.getType();
        boolean success = this.removeById(id);
        if (success) {
            // 更新科目表
            subjectService.decrementQuestionCount(question.getSubject(), 1);
            // 重排该类型的display_order
            reorderDisplayOrder(question.getSubject(), type);
        }
        return success;
    }

    @Override
    @Transactional
    public boolean batchDeleteQuestions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        // 统计各科目的删除数量和涉及的类型
        List<Question> questionsToDelete = this.listByIds(ids);
        Map<String, Integer> subjectCountMap = new HashMap<>();
        java.util.Set<String> typesToReorder = new java.util.HashSet<>();
        
        for (Question q : questionsToDelete) {
            String subject = q.getSubject();
            subjectCountMap.put(subject, subjectCountMap.getOrDefault(subject, 0) + 1);
            typesToReorder.add(q.getType());
        }
        
        boolean success = this.removeByIds(ids);
        if (success) {
            // 更新科目表
            for (Map.Entry<String, Integer> entry : subjectCountMap.entrySet()) {
                subjectService.decrementQuestionCount(entry.getKey(), entry.getValue());
            }
            for (String type : typesToReorder) {
                // 为每个涉及的科目和类型组合重排
                for (String subject : subjectCountMap.keySet()) {
                    reorderDisplayOrder(subject, type);
                }
            }
        }
        return success;
    }
}
