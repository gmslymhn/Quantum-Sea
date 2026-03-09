package tyut.selab.blog.service;

import tyut.selab.blog.domain.dto.AddBlogDto;
import tyut.selab.blog.domain.dto.UpdateBlogDto;
import tyut.selab.blog.domain.dto.param.BlogParam;
import tyut.selab.common.domain.R;

public interface IBlogService {
    R getBlogVoList(BlogParam blogParam);

    R getBlogMsg(Integer blogId);

    R addBlog(AddBlogDto addBlogDto);

    R updateBlog(UpdateBlogDto updateBlogDto);

    R deleteBlogById(Integer blogId);
}
