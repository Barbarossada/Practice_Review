package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目 Mapper 接口
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    
}
