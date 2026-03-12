package tyut.selab.Graduation.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @ClassName: PostEntity
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-09 21:57
 * @Version: 1.0
 **/


@Data
@TableName("tyut_post_data")
public class PostDataEntity {
    /**
     * 帖子ID
     */
    @TableId(value = "post_id", type = IdType.AUTO)
    private Integer postId;

    /**
     * 帖子ID
     */
    @TableField(value = "thread_id")
    private String threadId;

    /**
     * 分类ID
     */
    @TableField("cate_id")
    private String cateId;

    /**
     * 分类名称
     */
    @TableField("cate_name")
    private String cateName;

    /**
     * 帖子标题
     */
    @TableField("title")
    private String title;

    /**
     * 帖子内容
     */
    @TableField("content")
    private String content;

    /**
     * 图片路径列表（JSON格式存储）
     */
    @TableField("img_paths")
    private String imgPaths;

    /**
     * 评论数量
     */
    @TableField("c_count")
    private Integer cCount;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞数量
     */
    @TableField("l_count")
    private Integer lCount;

    /**
     * 悬赏金额
     */
    @TableField("reward_price")
    private Integer rewardPrice;

    /**
     * 发布时间戳
     */
    @TableField("p_time")
    private Long pTime;

    /**
     * 帖子状态
     */
    @TableField("finish_status")
    private Integer finishStatus;

    /**
     * 热度值
     */
    @TableField("hot_val")
    private Integer hotVal;

    /**
     * 用户头像URL
     */
    @TableField("headimgurl")
    private String headimgurl;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户等级
     */
    @TableField("user_level")
    private Integer userLevel;

    /**
     * 用户等级称号
     */
    @TableField("user_level_title")
    private String userLevelTitle;

    /**
     * 签名/验证信息
     */
    @TableField("sign")
    private String sign;


    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private String updateTime;
    /**
     * 逻辑删除(1删除 0未删除)
     */
    @TableLogic
    @JsonIgnore
    @TableField("del_flag")
    private Integer delFlag;

    @Override
    public String toString() {
        return "PostEntity{" +
                "threadId='" + threadId + '\'' +
                ", cateId='" + cateId + '\'' +
                ", cateName='" + cateName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imgPaths='" + imgPaths + '\'' +
                ", cCount=" + cCount +
                ", viewCount=" + viewCount +
                ", lCount=" + lCount +
                ", rewardPrice=" + rewardPrice +
                ", pTime=" + pTime +
                ", finishStatus=" + finishStatus +
                ", hotVal=" + hotVal +
                ", headimgurl='" + headimgurl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userLevel=" + userLevel +
                ", userLevelTitle='" + userLevelTitle + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}