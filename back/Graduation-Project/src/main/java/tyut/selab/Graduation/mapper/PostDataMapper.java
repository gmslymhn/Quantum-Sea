package tyut.selab.Graduation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tyut.selab.Graduation.domain.entity.PostDataEntity;

import java.util.List;
import java.util.Map;

public interface PostDataMapper extends BaseMapper<PostDataEntity> {

    @Select("${sql}")
    List<Map<String, Object>> executeRawQuery(@Param("sql") String sql);


}
