package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Subject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科目 Mapper 接口
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Mapper
public interface SubjectMapper extends BaseMapper<Subject> {
    
}
