package tyut.selab.framework.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tyut.selab.framework.domain.model.WebpageRadar;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UploadRadarDto
 * @Description:上传雷达DTO
 * @Author: gmslymhn
 * @CreateTime: 2026-03-20 14:05
 * @Version: 1.0
 **/

public class UploadRadarDto {
    @Schema(description = "房间id")
    @NotNull(message = "房间id不能为空")
    @Size(min = 4, max = 10, message = "房间号错误!")
    public String roomId;

    @Schema(description = "雷达数组")
    @NotNull(message = "雷达数组不能为空")
    public List<WebpageRadar> WebpageRadarList = new ArrayList<>();

}
