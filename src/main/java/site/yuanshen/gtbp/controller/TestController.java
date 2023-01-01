package site.yuanshen.gtbp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.model.response.R;
import site.yuanshen.gtbp.model.response.RUtils;

/**
 * 测试api
 *
 * @author Moment
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
@Tag(name = "test", description = "测试api")
public class TestController {

    /**
     * 测试服务连通性
     */
    @GetMapping("/")
    public Mono<R<String>> hello() {
        return RUtils.momoOk("Server is ok");
    }

    /**
     * 测试是否登录
     */
    @GetMapping("/auth/test")
    @PreAuthorize("hasRole('USER')")
    public Mono<R<String>> privateMessageAdmin() {
        return RUtils.momoOk("Authorize ok");
    }
}
