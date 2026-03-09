package tyut.selab.blog.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import tyut.selab.blog.domain.entity.BlogEntity;
import tyut.selab.common.utils.EncryptUtils;

/**
 * @ClassName: BlogMsgVo
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:41
 * @Version: 1.0
 **/
@Data
public class BlogMsgVo extends BlogVo{

    /**
     * BLOG前页
     */
    @TableField("blog_front_matter")
    private String blogFrontMatter;

    /**
     * BLOG内容
     */
    @TableField("blog_content")
    private String blogContent;

    public BlogMsgVo(BlogEntity blogEntity){
        super(blogEntity);
        this.blogFrontMatter = EncryptUtils.Base64Encrypt(blogEntity.getBlogFrontMatter());
        this.blogContent = EncryptUtils.Base64Encrypt(blogEntity.getBlogContent());
    }
}
