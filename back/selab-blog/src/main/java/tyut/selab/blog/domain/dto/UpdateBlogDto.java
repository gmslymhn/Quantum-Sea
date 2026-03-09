package tyut.selab.blog.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @ClassName: UpdateBlogDto
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:24
 * @Version: 1.0
 **/
@Data
public class UpdateBlogDto {

    /**
     * BLOGid
     */
    @NotNull(message = "blogId不能为空")
    private Integer blogId;

    /**
     * BLOG标题
     */
    @Size(min = 1,max = 63,message = "BLOG标题在1~63字符之间")
    private String blogTitle;
    /**
     * BLOG简介
     */
    @Size(min = 1,max = 255,message = "BLOG简介在1~255字符之间")
    private String blogIntroduction;
    /**
     * BLOG标题图片
     */
    @Size(min = 1,max = 255,message = "BLOG标题图片在1~255字符之间")
    private String headerImage;
    /**
     * BLOG前页
     */
    @Size(min = 1,max = 1023,message = "BLOG前页在1~1023字符之间")
    private String blogFrontMatter;

    /**
     * BLOG内容
     */
    @Size(min = 1,max = 65535,message = "BLOG内容在1~65535字符之间")
    private String blogContent;
}
