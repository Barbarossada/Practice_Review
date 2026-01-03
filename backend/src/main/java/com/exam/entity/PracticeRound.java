package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.exam.config.LongListTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 练习轮次实体类
 * 记录用户按科目的刷题轮次状态
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@Data
@TableName(value = "practice_round", autoResultMap = true)
public class PracticeRound {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 科目
     */
    private String subject;

    /**
     * 乱序题目ID列表 (JSON格式存储)
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> questionIds;

    /**
     * 当前索引位置
     */
    private Integer currentIndex;

    /**
     * 本轮总题数
     */
    private Integer totalCount;

    /**
     * 是否已完成本轮
     */
    private Boolean isFinished;

    /**
     * 第几轮
     */
    private Integer roundNumber;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
