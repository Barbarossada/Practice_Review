package com.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.exam.dto.QuestionImportDTO;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择题导入监听器
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Slf4j
public class ChoiceQuestionImportListener extends AnalysisEventListener<QuestionImportDTO> {

    private static final int BATCH_SIZE = 100;
    
    private List<Question> questions = new ArrayList<>();
    private QuestionService questionService;
    private int successCount = 0;
    private int failCount = 0;

    public ChoiceQuestionImportListener(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void invoke(QuestionImportDTO data, AnalysisContext context) {
        try {
            Question question = convertToQuestion(data);
            questions.add(question);

            // 每100条批量保存一次
            if (questions.size() >= BATCH_SIZE) {
                saveBatch();
            }
        } catch (Exception e) {
            log.error("导入选择题失败: {}", data.getContent(), e);
            failCount++;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 保存剩余数据
        saveBatch();
        log.info("选择题导入完成，成功: {}，失败: {}", successCount, failCount);
    }

    private void saveBatch() {
        if (!questions.isEmpty()) {
            questionService.saveBatch(questions);
            successCount += questions.size();
            questions.clear();
        }
    }

    private Question convertToQuestion(QuestionImportDTO dto) {
        Question question = new Question();
        
        // 明确设置ID为null，让数据库自增
        question.setId(null);
        
        // 根据答案长度自动判断题型
        String answer = dto.getAnswer() != null ? dto.getAnswer().toUpperCase() : "";
        if (answer.length() > 1) {
            question.setType("multiple-choice");  // 多选题（答案如：ABC）
        } else {
            question.setType("single-choice");     // 单选题（答案如：A）
        }
        
        question.setContent(dto.getContent());
        
        // 构建选项 JSON 数组
        List<String> optionsList = new ArrayList<>();
        if (dto.getOptionA() != null && !dto.getOptionA().trim().isEmpty()) {
            optionsList.add("A:" + dto.getOptionA());
        }
        if (dto.getOptionB() != null && !dto.getOptionB().trim().isEmpty()) {
            optionsList.add("B:" + dto.getOptionB());
        }
        if (dto.getOptionC() != null && !dto.getOptionC().trim().isEmpty()) {
            optionsList.add("C:" + dto.getOptionC());
        }
        if (dto.getOptionD() != null && !dto.getOptionD().trim().isEmpty()) {
            optionsList.add("D:" + dto.getOptionD());
        }
        question.setOptions(optionsList);
        
        question.setAnswer(dto.getAnswer() != null ? dto.getAnswer().toUpperCase() : "");
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
