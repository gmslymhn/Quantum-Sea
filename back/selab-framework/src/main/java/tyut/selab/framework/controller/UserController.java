package tyut.selab.framework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tyut.selab.common.annotation.*;
import tyut.selab.common.domain.R;
import tyut.selab.framework.domain.dto.*;
import tyut.selab.framework.domain.dto.param.UserParam;
import tyut.selab.framework.service.IUserService;
import tyut.selab.framework.web.SecurityUtils;
import tyut.selab.framework.web.service.LoginService;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName: UserController
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2024-05-20 10:58
 * @Version: 1.0
 **/
@RestController
@Tag(name = "用户")
@RequestMapping("/background")
@Slf4j
public class UserController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "用户登陆")
    @LoginLogAnnotation()
    @WhitelistAnnotation()
    public R login(@RequestBody @Validated LoginDto loginDto) {
        R verifyr = loginService.mayLogin(loginDto.getAccount());
        if (verifyr.getCode()!=200){
            return verifyr;
        }
        R r = iUserService.login(loginDto);
        return r;
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public R logout() {
        R r = iUserService.logoutUser();
        return r;
    }
    @PostMapping("/register")
    @AccessLogAnnotation(method = "用户注册")
    @Operation(summary = "用户注册")
    @WhitelistAnnotation()
    public R register(@RequestBody @Validated AddUserDto addUserDto) {
        R verifyr = loginService.mayRegister(addUserDto.getUserAccount(), addUserDto.getUserEmail(),addUserDto.getVerificationCode());
        if (verifyr.getCode()!=200){
            return verifyr;
        }
        R r = iUserService.addUser(addUserDto);
        return r;
    }

    @PostMapping("/register/verify")
    @Operation(summary = "用户注册验证")
    @WhitelistAnnotation()
    @AccessLogAnnotation(method = "用户注册验证")
    public R verifyRegister(@RequestBody @Validated VerifyRegisterDto verifyRegister) {
        R verifyr = loginService.mayRegisterVerify();
        if (verifyr.getCode()!=200){
            return verifyr;
        }

        R r = iUserService.verifyRegister(verifyRegister);
        return r;
    }

    @PostMapping("/getSelfMag")
    @Operation(summary = "获取自己的登陆信息",description = "登陆成功后第一时间通过token调取，可以获得用户自己的信息")
    public R getSelfMsg() throws ExecutionException, InterruptedException {
        Integer userId = SecurityUtils.getUserId();
        return iUserService.getUserMsgById(userId);
    }
    @PostMapping("/userList")
    @Operation(summary = "用户列表",description= "获取用户列表")
    public R userList(@RequestBody @Validated UserParam userParam) {
        return iUserService.getUserList(userParam);
    }

    @PostMapping("/userMsg")
    @Operation(summary = "用户信息",description= "获取用户详细信息")
    public R userMsg(@RequestParam("userId")Integer userId) throws ExecutionException, InterruptedException {
        return iUserService.getUserMsgById(userId);
    }


    @PostMapping("/resetUserPassword")
    @Operation(summary = "用户重置",description= "重置用户密码")
    @SysLogAnnotation(operModul = "用户管理",operType = "修改",operDesc = "重置用户密码")
    @PreAuthorize("@ss.hasPort('user:reset')")
    public R resetUserPassword(@RequestParam("userId")Integer userId) {
        return iUserService.resetUserPassword(userId);
    }



    @PostMapping("/updateSelfMsg")
    @Operation(summary = "修改自己信息",description= "修改自己信息")
    @SysLogAnnotation(operModul = "用户管理",operType = "修改",operDesc = "修改自己信息")
    public R updateSelfMsg(@RequestBody @Validated UpdateUserDto updateUserDto) {
        updateUserDto.setUserId(SecurityUtils.getUserId());
        return iUserService.updateUser(updateUserDto);
    }


    @PostMapping("/updateSelfPassword")
    @Operation(summary = "修改自己密码",description= "修改自己密码")
    public R updateSelfPassword(@RequestBody @Validated UpdateUserPasswordDto updateUserPasswordDto) {
        updateUserPasswordDto.setUserId(SecurityUtils.getUserId());
        return iUserService.updateUserPassword(updateUserPasswordDto);
    }



    @PostMapping("/updateUser")
    @Operation(summary = "修改用户",description= "修改用户信息")
    @PreAuthorize("@ss.hasPort('user:update')")
    public R updateUser(@RequestBody @Validated UpdateUserDto updateUserDto) {
        return iUserService.updateUser(updateUserDto);
    }

    @PostMapping("/deleteUser")
    @PreAuthorize("@ss.hasPort('user:delete')")
    @Operation(summary = "删除用户",description= "通过用户id删除用户")
    public R delectUserById(@RequestParam("userId")Integer userId) {
        return iUserService.delectUserById(userId);
    }


}
