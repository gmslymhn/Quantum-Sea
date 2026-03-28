package tyut.selab.Graduation.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tyut.selab.framework.domain.PageParam;

/**
 * @ClassName: PostParam
 * @Description:帖子分页
 * @Author: gmslymhn
 * @CreateTime: 2026-03-26 08:47
 * @Version: 1.0
 **/
@Data
public class PostParam extends PageParam {

    private String searchContent;


    /**
     * 分类名称
     */
    private String cateName;

    /**
     * 发布时间戳
     */
    private Long pTime;
}
