package tyut.selab.blog.domain.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tyut.selab.framework.domain.PageParam;

/**
 * @ClassName: BlogParam
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:28
 * @Version: 1.0
 **/
@Data
public class BlogParam extends PageParam {

    /**
     * BLOG搜索信息
     */
    @Schema(description = "搜索信息")
    private String searchInformation;
}
