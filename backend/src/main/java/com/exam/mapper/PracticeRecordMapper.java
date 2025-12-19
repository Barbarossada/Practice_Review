package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.PracticeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 练习记录 Mapper 接口
 * 
 * @author Exam System
 * @since 2025-12-19
 */
@Mapper
public interface PracticeRecordMapper extends BaseMapper<PracticeRecord> {
    
}
