package com.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.exam.dto.JudgeQuestionImportDTO;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 判断题导入监听器
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Slf4j
public class JudgeQuestionImportListener extends AnalysisEventListener<JudgeQuestionImportDTO> {

    private static final int BATCH_SIZE = 100;
    
    private List<Question> questions = new ArrayList<>();
    private QuestionService questionService;
    private int successCount = 0;
    private int failCount = 0;

    public JudgeQuestionImportListener(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void invoke(JudgeQuestionImportDTO data, AnalysisContext context) {
        try {
            Question question = convertToQuestion(data);
            questions.add(question);

            // 每100条批量保存一次
            if (questions.size() >= BATCH_SIZE) {
                saveBatch();
            }
        } catch (Exception e) {
            log.error("导入判断题失败: {}", data.getContent(), e);
            failCount++;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 保存剩余数据
        saveBatch();
        log.info("判断题导入完成，成功: {}，失败: {}", successCount, failCount);
    }

    private void saveBatch() {
        if (!questions.isEmpty()) {
            questionService.saveBatch(questions);
            successCount += questions.size();
            questions.clear();
        }
    }

    private Question convertToQuestion(JudgeQuestionImportDTO dto) {
        Question question = new Question();
        
        // 明确设置ID为null，让数据库自增
        question.setId(null);
        
        question.setType("judge");
        question.setContent(dto.getContent());
        question.setOptions(null); // 判断题没有选项
        
        // 标准化答案格式
        String answer = dto.getAnswer();
        if (answer != null) {
            answer = answer.trim();
            if (answer.equals("正确") || answer.equalsIgnoreCase("true") || answer.equals("T") || answer.equals("√")) {
                answer = "正确";
            } else if (answer.equals("错误") || answer.equalsIgnoreCase("false") || answer.equals("F") || answer.equals("×")) {
                answer = "错误";
            }
        }
        question.setAnswer(answer);
        
        question.setAnalysis(dto.getAnalysis());
        question.setSubject(dto.getSubject() != null ? dto.getSubject() : "未分类");
        question.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : "medium");
        question.setIsMarked(false);
        question.setWrongCount(0);
        question.setPracticeCount(0);

        return question;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }
}
