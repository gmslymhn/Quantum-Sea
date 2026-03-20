package tyut.selab.framework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tyut.selab.common.annotation.AccessLogAnnotation;
import tyut.selab.common.annotation.WhitelistAnnotation;
import tyut.selab.common.domain.Lz;
import tyut.selab.common.domain.R;
import tyut.selab.common.utils.ObjectUtils;
import tyut.selab.common.utils.RedisUtils;
import tyut.selab.framework.domain.dto.UploadRadarDto;
import tyut.selab.framework.domain.model.WebpageRadar;
import tyut.selab.framework.service.IResourceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ForeignController
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-02-27 14:54
 * @Version: 1.0
 **/

@RestController
@Tag(name = "对外接口管理")
@Slf4j
@RequestMapping("/foreign")
public class ForeignController {

    @Autowired
    private IResourceService iResourceService;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/getResource")
    @Operation(summary = "通过蓝奏云三段获取资源")
    @AccessLogAnnotation()
    @WhitelistAnnotation(method = RequestMethod.GET)
    public void getResourceByLz(
            @RequestParam String isNewd,
            @RequestParam String fId,
            @RequestParam String pwd,
            HttpServletResponse response
    ) throws ExecutionException, InterruptedException, IOException {
        Lz lz = new Lz(pwd,fId,isNewd);
        String url = iResourceService.getResourceByLz(lz).get();

        // 直接重定向
        response.sendRedirect(url);
    }

    @PostMapping("/uploadRadar")
    @WhitelistAnnotation()//白名单
    public R uploadRadar(@RequestBody @Validated UploadRadarDto uploadRadarDto){
        redisUtils.setCacheObject("Radar_"+uploadRadarDto.roomId,
                uploadRadarDto.WebpageRadarList,
                10,
                TimeUnit.SECONDS);
        return R.success();
    }

    @GetMapping("/getRadar")
    @WhitelistAnnotation(method = RequestMethod.GET)
    @AccessLogAnnotation()
    public R getRadar(@RequestParam("roomId")String roomId){
        List<WebpageRadar> WebpageRadarList = (List<WebpageRadar>) redisUtils.getCacheObject("Radar_"+roomId);
        if (ObjectUtils.isNotNull(WebpageRadarList)){
            return R.success(WebpageRadarList);
        }
        return R.error("没有数据,或者房间不存在！");
    }
}
