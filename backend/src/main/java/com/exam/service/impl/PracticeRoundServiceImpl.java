package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.PracticeRound;
import com.exam.entity.Question;
import com.exam.mapper.PracticeRoundMapper;
import com.exam.service.PracticeRoundService;
import com.exam.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 练习轮次 Service 实现类
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@Slf4j
@Service
public class PracticeRoundServiceImpl extends ServiceImpl<PracticeRoundMapper, PracticeRound> implements PracticeRoundService {

    @Autowired
    private QuestionService questionService;

    @Override
    @Transactional
    public Question startOrContinueRound(Long userId, String subject) {
        log.info("开始/继续轮次: userId={}, subject={}", userId, subject);
        
        // 查找现有轮次
        PracticeRound round = baseMapper.selectByUserAndSubject(userId, subject);
        
        if (round == null || round.getIsFinished() || round.getQuestionIds() == null || round.getQuestionIds().isEmpty()) {
            // 创建新轮次（如果旧轮次的 questionIds 为 null，也需要重新创建）
            int nextRoundNumber = (round == null) ? 1 : round.getRoundNumber() + 1;
            round = createNewRound(userId, subject, nextRoundNumber);
        }
        
        if (round == null || round.getQuestionIds() == null || round.getQuestionIds().isEmpty()) {
            log.warn("该科目没有题目: subject={}", subject);
            return null;
        }
        
        // 返回当前索引对应的题目
        Long questionId = round.getQuestionIds().get(round.getCurrentIndex());
        return questionService.getById(questionId);
    }

    /**
     * 创建新的练习轮次
     */
    private PracticeRound createNewRound(Long userId, String subject, int roundNumber) {
        log.info("创建新轮次: userId={}, subject={}, roundNumber={}", userId, subject, roundNumber);
        
        // 获取该科目的所有题目ID
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubject, subject)
               .select(Question::getId);
        
        List<Question> questions = questionService.list(wrapper);
        if (questions.isEmpty()) {
            return null;
        }
        
        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        
        // 随机打乱顺序
        Collections.shuffle(questionIds);
        
        // 创建或更新轮次记录
        PracticeRound round = new PracticeRound();
        round.setUserId(userId);
        round.setSubject(subject);
        round.setQuestionIds(questionIds);
        round.setCurrentIndex(0);
        round.setTotalCount(questionIds.size());
        round.setIsFinished(false);
        round.setRoundNumber(roundNumber);
        
        // 使用 insertOrUpdate 逻辑
        PracticeRound existing = baseMapper.selectByUserAndSubject(userId, subject);
        if (existing != null) {
            round.setId(existing.getId());
            this.updateById(round);
        } else {
            this.save(round);
        }
        
        return round;
    }

    @Override
    @Transactional
    public Question nextQuestion(Long userId, String subject) {
        log.info("获取下一题: userId={}, subject={}", userId, subject);
        
        PracticeRound round = baseMapper.selectByUserAndSubject(userId, subject);
        
        // 详细日志诊断
        if (round != null) {
            log.info("轮次数据: questionIds大小={}, currentIndex={}, totalCount={}, isFinished={}", 
                round.getQuestionIds() != null ? round.getQuestionIds().size() : "null",
                round.getCurrentIndex(),
                round.getTotalCount(),
                round.getIsFinished());
        }
        
        // 添加 questionIds 为 null 的检查
        if (round == null || round.getIsFinished() || round.getQuestionIds() == null) {
            return null;
        }
        
        int nextIndex = round.getCurrentIndex() + 1;
        if (nextIndex >= round.getTotalCount() || nextIndex >= round.getQuestionIds().size()) {
            // 本轮已完成
            round.setIsFinished(true);
            round.setCurrentIndex(round.getTotalCount() - 1);
            this.updateById(round);
            return null;
        }
        
        // 更新索引
        round.setCurrentIndex(nextIndex);
        this.updateById(round);
        
        Long questionId = round.getQuestionIds().get(nextIndex);
        return questionService.getById(questionId);
    }

    @Override
    @Transactional
    public Question prevQuestion(Long userId, String subject) {
        log.info("获取上一题: userId={}, subject={}", userId, subject);
        
        PracticeRound round = baseMapper.selectByUserAndSubject(userId, subject);
        // 添加 questionIds 为 null 的检查
        if (round == null || round.getQuestionIds() == null) {
            return null;
        }
        
        int prevIndex = round.getCurrentIndex() - 1;
        if (prevIndex < 0 || prevIndex >= round.getQuestionIds().size()) {
            // 已是第一题
            return null;
        }
        
        // 更新索引
        round.setCurrentIndex(prevIndex);
        this.updateById(round);
        
        Long questionId = round.getQuestionIds().get(prevIndex);
        return questionService.getById(questionId);
    }

    @Override
    public PracticeRound getProgress(Long userId, String subject) {
        return baseMapper.selectByUserAndSubject(userId, subject);
    }

    @Override
    @Transactional
    public Question jumpToIndex(Long userId, String subject, int index) {
        log.info("跳转到指定索引: userId={}, subject={}, index={}", userId, subject, index);
        
        PracticeRound round = baseMapper.selectByUserAndSubject(userId, subject);
        // 添加 questionIds 为 null 的检查
        if (round == null || round.getQuestionIds() == null || index < 0 || index >= round.getQuestionIds().size()) {
            return null;
        }
        
        round.setCurrentIndex(index);
        this.updateById(round);
        
        Long questionId = round.getQuestionIds().get(index);
        return questionService.getById(questionId);
    }

    @Override
    @Transactional
    public Question resetRound(Long userId, String subject) {
        log.info("重置轮次: userId={}, subject={}", userId, subject);
        
        PracticeRound existing = baseMapper.selectByUserAndSubject(userId, subject);
        int nextRoundNumber = existing == null ? 1 : existing.getRoundNumber() + 1;
        
        PracticeRound newRound = createNewRound(userId, subject, nextRoundNumber);
        if (newRound != null && !newRound.getQuestionIds().isEmpty()) {
            Long questionId = newRound.getQuestionIds().get(0);
            return questionService.getById(questionId);
        }
        return null;
    }
}
