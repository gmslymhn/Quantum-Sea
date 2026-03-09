package tyut.selab.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tyut.selab.common.domain.BaseEntity;

/**
 * @ClassName: BlogEntity
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:15
 * @Version: 1.0
 **/
@TableName("tyut_blog")
@Schema(description = "博客实体类")
@Data
public class BlogEntity extends BaseEntity {


    /**
     * BLOGid
     */
    @TableId(value = "blog_id",type = IdType.AUTO)
    private Integer blogId;

    /**
     * BLOG标题
     */
    @TableField("blog_title")
    private String blogTitle;
    /**
     * BLOG简介
     */
    @TableField("blog_introduction")
    private String blogIntroduction;
    /**
     * BLOG标题图片
     */
    @TableField("header_image")
    private String headerImage;
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
}
