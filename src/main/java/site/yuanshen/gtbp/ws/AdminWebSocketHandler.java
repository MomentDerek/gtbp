package site.yuanshen.gtbp.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.auth.JWTCustomVerifier;
import site.yuanshen.gtbp.model.User;
import site.yuanshen.gtbp.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zhuquanwen
 * @version 1.0
 * @date 2022/4/13 13:37
 * @since jdk11
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminWebSocketHandler implements WebSocketHandler {

    private final UserService userService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 校验权限
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        //读取用户ID
        String idString = handshakeInfo.getHeaders().getFirst("id");
        if (idString != null) {
            Long id = Long.valueOf(idString);
            Mono<User> user = userService.getUser(id).map(u -> u.withPassword(""));
            // 输入输出封装
            Mono<Void> input = session.receive().doOnNext(message -> this.messageHandle(session, message))
                    .log()
                    .doOnError(throwable -> log.error("webSocket发生异常：" + throwable))
                    .doOnComplete(() -> log.info("webSocket结束")).then();
            Mono<Void> output = session.send(Flux.create(sink -> {
                WebSocketWrap.SENDER.put(id, new WebSocketWrap(id, session, sink));
                WebSocketWrap.SENDER.get(id).sendText("匹配中");
            }));
            return Mono.zip(input, output).then();
        } else {
            return session.close(new CloseStatus(400, "id为空,即将关闭连接"));
        }
    }


    @SuppressWarnings(value = "unused")
    private void messageHandle(WebSocketSession session, WebSocketMessage message) {
        // 接收客户端请求的处理回调
        switch (message.getType()) {
            case TEXT:
                session.send(Flux.just(session.textMessage("ok"))).subscribe();
            case BINARY:
            case PONG:
                break;
            case PING:
                session.send(Flux.just(session.pongMessage(s -> s.wrap(new byte[10])))).subscribe();
                break;
            default:
        }
    }

}

