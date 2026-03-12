package tyut.selab.Graduation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: LizardEntity
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-10 16:30
 * @Version: 1.0
 **/
@Data
@TableName("tyut_lizard_log")
public class LizardLogEntity {
    /**
     * lizardid
     */
    @TableId(value = "lizard_id", type = IdType.AUTO)
    private Integer lizardId;

    /**
     * lizard数据
     */
    @TableField(value = "lizard_data")
    private String lizardData;

    /**
     * 创建时间
     */
    @TableField("lizard_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lizardTime;
}
