package tyut.selab.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tyut.selab.blog.domain.dto.AddBlogDto;
import tyut.selab.blog.domain.dto.UpdateBlogDto;
import tyut.selab.blog.domain.dto.param.BlogParam;
import tyut.selab.blog.domain.entity.BlogEntity;
import tyut.selab.blog.domain.vo.BlogMsgVo;
import tyut.selab.blog.domain.vo.BlogVo;
import tyut.selab.blog.mapper.BlogMapper;
import tyut.selab.blog.service.IBlogService;
import tyut.selab.common.domain.R;
import tyut.selab.common.utils.ObjectUtils;
import tyut.selab.common.utils.StringUtils;
import tyut.selab.framework.web.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AcgServiceImpl
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2025-09-21 22:34
 * @Version: 1.0
 **/
@Service
@Slf4j
public class BlogServiceImpl  implements IBlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public R getBlogVoList(BlogParam blogParam){

        Page<BlogEntity> page = new Page<>(blogParam.getPageNum(), blogParam.getPageSize());
        QueryWrapper<BlogEntity> blogEntityQueryWrapper = new QueryWrapper<>();
        blogEntityQueryWrapper.like(StringUtils.isNotEmpty(blogParam.getSearchInformation()),"blog_front_matter",blogParam.getSearchInformation());

        Page<BlogEntity> blogEntityPage = blogMapper.selectPage(page,blogEntityQueryWrapper);
        List<BlogVo> blogVos = new ArrayList<>();

        blogEntityPage.getRecords().forEach(blogEntity -> {
            BlogVo blogVo = new BlogVo(blogEntity);
            blogVos.add(blogVo);
        });
        Page<BlogVo> blogVoPage = new Page<>(blogEntityPage.getCurrent(),blogEntityPage.getSize(),blogEntityPage.getTotal());
        blogVoPage.setRecords(blogVos);
        return R.success(blogVoPage);
    }

    @Override
    public R getBlogMsg(Integer blogId){
        BlogEntity blogEntity = blogMapper.selectById(blogId);
        if (ObjectUtils.isNull(blogEntity)){
            return R.error("文章不存在！");
        }
        BlogMsgVo blogMsgVo = new BlogMsgVo(blogEntity);
        return R.success(blogMsgVo);
    }
    @Override
    public R addBlog(AddBlogDto addBlogDto){
        QueryWrapper<BlogEntity> blogEntityQueryWrapper = new QueryWrapper<>();
        blogEntityQueryWrapper.eq("blog_title",addBlogDto.getBlogTitle());
        BlogEntity blogEntity1 = blogMapper.selectOne(blogEntityQueryWrapper);
        if (ObjectUtils.isNotNull(blogEntity1)){
            return R.error("博客标题已存在！");
        }
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setBlogIntroduction(addBlogDto.getBlogIntroduction());
        blogEntity.setBlogTitle(addBlogDto.getBlogTitle());
        blogEntity.setHeaderImage(addBlogDto.getHeaderImage());
        blogEntity.setBlogFrontMatter("shortTitle: \"\"   # 【可选】用于导航栏/侧边栏显示的简短标题\n" +
                "\n" +
                "icon: \"\" # 【可选】标题旁显示的图标（支持iconfont/图片路径）\n" +
                "\n" +
                "author:\n" +
                "\n" +
                "- name: \"GM\"    # 支持字符串或对象形式\n" +
                "  url: \"https://github.com/gmslymhn\"\n" +
                "  email: \"gmslymhn@163.com\"\n" +
                "\n" +
                "isOriginal: true              # 【可选】标记为原创文章（默认false）\n" +
                "\n" +
                "license: \"\"    # 协议类型（默认使用主题配置）\n" +
                "copyright: \"未经许可禁止转载\"  # 自定义版权文字（设为false可隐藏）\n" +
                "\n" +
                "pageview: false                # 是否显示浏览量（需要Waline配置）\n" +
                "article: true                 # 是否加入文章列表（默认true）\n" +
                "timeline: true                # 是否显示在时间线（默认true）\n" +
                "\n" +
                "category: [\"默认\"]         # 支持字符串或数组\n" +
                "tag: [\"默认\"]      # 多个标签用数组表示\n" +
                "\n" +
                "sticky:                     # 置顶优先级（数字越大越靠前，false关闭）\n" +
                "star:                        # 星标优先级（数字越大越靠前）");
        blogEntity.setCreateUser(SecurityUtils.getUserAccount());
        blogEntity.setBlogContent(addBlogDto.getBlogIntroduction()+"\n" + "<!-- more -->");
        blogEntity.setDelFlag(0);
        blogMapper.insert(blogEntity);
        return R.success("添加成功！");
    }

    @Override
    public R updateBlog(UpdateBlogDto updateBlogDto){
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setBlogId(updateBlogDto.getBlogId());
        blogEntity.setBlogTitle(updateBlogDto.getBlogTitle());
        blogEntity.setBlogContent(updateBlogDto.getBlogContent());
        blogEntity.setHeaderImage(updateBlogDto.getHeaderImage());
        blogEntity.setBlogFrontMatter(updateBlogDto.getBlogFrontMatter());
        blogEntity.setBlogIntroduction(updateBlogDto.getBlogIntroduction());
        blogEntity.setUpdateUser(SecurityUtils.getUserAccount());
        blogMapper.updateById(blogEntity);
        return R.success("修改成功！");
    }
    @Override
    public R deleteBlogById(Integer blogId){
        blogMapper.deleteById(blogId);
        return R.success("删除成功！");
    }
}
