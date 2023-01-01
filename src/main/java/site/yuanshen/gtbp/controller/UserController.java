package site.yuanshen.gtbp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.model.User;
import site.yuanshen.gtbp.model.response.R;
import site.yuanshen.gtbp.model.response.RUtils;
import site.yuanshen.gtbp.service.UserService;

/**
 * 用户API
 *
 * @author Moment
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 登录接口
     *
     * @return 登录成功则在header返回密钥
     */
    @GetMapping("/login")
    public Mono<R<String>> login() {
        return RUtils.momoOk("Login Successful");
    }

    /**
     * 登录接口
     *
     * @return 登录成功则在header返回密钥
     */
    @PostMapping("/register")
    public Mono<R<String>> register(@RequestBody User user) {
        return userService.register(
                        user.getUid(),
                        user.getPassword(),
                        user.getNickname())
                .thenReturn(RUtils.create("注册成功"));
    }

}
