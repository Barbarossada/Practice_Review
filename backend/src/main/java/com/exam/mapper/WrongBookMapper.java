package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.WrongBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 错题本 Mapper 接口
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@Mapper
public interface WrongBookMapper extends BaseMapper<WrongBook> {
    
    /**
     * 查询用户错题本中的所有题目ID
     * 
     * @param userId 用户ID
     * @return 题目ID列表
     */
    @Select("SELECT question_id FROM wrong_book WHERE user_id = #{userId} AND mastery_level < 100 ORDER BY create_time DESC")
    List<Long> selectQuestionIdsByUserId(@Param("userId") Long userId);
}
