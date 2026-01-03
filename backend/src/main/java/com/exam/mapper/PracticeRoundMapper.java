package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.config.LongListTypeHandler;
import com.exam.entity.PracticeRound;
import org.apache.ibatis.annotations.*;

/**
 * 练习轮次 Mapper 接口
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@Mapper
public interface PracticeRoundMapper extends BaseMapper<PracticeRound> {
    
    /**
     * 查询用户在某科目的当前轮次
     * 使用 @Results 注解来正确映射 JSON 字段
     * 
     * @param userId 用户ID
     * @param subject 科目
     * @return 轮次记录
     */
    @Results(id = "practiceRoundResultMap", value = {
        @Result(column = "id", property = "id"),
        @Result(column = "user_id", property = "userId"),
        @Result(column = "subject", property = "subject"),
        @Result(column = "question_ids", property = "questionIds", typeHandler = LongListTypeHandler.class),
        @Result(column = "current_index", property = "currentIndex"),
        @Result(column = "total_count", property = "totalCount"),
        @Result(column = "is_finished", property = "isFinished"),
        @Result(column = "round_number", property = "roundNumber"),
        @Result(column = "create_time", property = "createTime"),
        @Result(column = "update_time", property = "updateTime")
    })
    @Select("SELECT * FROM practice_round WHERE user_id = #{userId} AND subject = #{subject}")
    PracticeRound selectByUserAndSubject(@Param("userId") Long userId, @Param("subject") String subject);
}
