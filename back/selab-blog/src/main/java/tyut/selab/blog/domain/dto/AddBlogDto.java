package tyut.selab.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @ClassName: AddBlogVo
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:22
 * @Version: 1.0
 **/

@Schema(description = "Add博客实体类")
@Data
public class AddBlogDto {
    /**
     * BLOG标题
     */
    @NotBlank
    @Size(min = 1,max = 63,message = "BLOG标题在1~63字符之间")
    private String blogTitle;
    /**
     * BLOG简介
     */
    @NotBlank
    @Size(min = 1,max = 255,message = "BLOG简介在1~255字符之间")
    private String blogIntroduction;
    /**
     * BLOG标题图片
     */
    @NotBlank
    @Size(min = 1,max = 255,message = "BLOG标题图片在1~255字符之间")
    private String headerImage;
}
