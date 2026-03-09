package tyut.selab.blog.domain.vo;

import lombok.Data;
import tyut.selab.blog.domain.entity.BlogEntity;

/**
 * @ClassName: BlogVo
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:40
 * @Version: 1.0
 **/
@Data
public class BlogVo {

    /**
     * BLOGid
     */
    private Integer blogId;

    /**
     * BLOG标题
     */
    private String blogTitle;
    /**
     * BLOG简介
     */
    private String blogIntroduction;
    /**
     * BLOG标题图片
     */
    private String headerImage;

    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 创建时间
     */
    private String createTime;

    public BlogVo(BlogEntity blogEntity){
        this.blogId = blogEntity.getBlogId();
        this.blogTitle = blogEntity.getBlogTitle();
        this.blogIntroduction = blogEntity.getBlogIntroduction();
        this.headerImage = blogEntity.getHeaderImage();
        this.createUser = blogEntity.getCreateUser();
        this.createTime = blogEntity.getCreateTime();

    }
}
