package com.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.Question;

/**
 * 题目 Service 接口
 * 
 * @author Exam System
 * @since 2025-12-19
 */
public interface QuestionService extends IService<Question> {

    /**
     * 分页查询题目列表
     * 
     * @param page 当前页
     * @param size 每页显示条数
     * @param subject 科目（可选）
     * @param type 题型（可选）
     * @param difficulty 难度（可选）
     * @return 分页结果
     */
    Page<Question> getQuestionPage(Long page, Long size, String subject, String type, String difficulty);

    /**
     * 随机获取一道题目
     * 
     * @param subject 科目（可选）
     * @param type 题型（可选）
     * @param difficulty 难度（可选）
     * @return 随机题目
     */
    Question getRandomQuestion(String subject, String type, String difficulty);

    /**
     * 清空题库
     * 
     * @param subject 科目（可选）
     * @param type 题型（可选）
     * @return 删除的题目数量
     */
    int clearAll(String subject, String type);
}
