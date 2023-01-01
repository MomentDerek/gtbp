package site.yuanshen.gtbp.ws;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ws连接池
 *
 * @author moment
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class WebSocketWrap {
    public static final Map<Long, WebSocketWrap> SENDER = new ConcurrentHashMap<>();

    static {
        purge();
    }

    private Long id;
    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    /**
     * 发送广播消息
     *
     * @param obj 消息对象，会被转为JSON
     * @return void
     */
    public static void broadcastText(Object obj) {
        SENDER.values().forEach(wrap -> wrap.sendText(obj));
    }

    /**
     * 发送单条信息
     *
     * @param obj 消息对象，会被转为JSON
     */
    public void sendText(Object obj) {
        sink.next(session.textMessage(JSON.toJSONString(obj)));
    }

    /**
     * 清理不可用的SESSION
     */
    public static void purge() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            new ArrayList<>(SENDER.values()).forEach(wrap -> {
                if (!wrap.getSession().isOpen()) {
                    log.warn(String.format("用户ID: [%s] 的session: [%s] 已经关闭，将被清理", wrap.getId(), wrap.getSession().getId()));
                    SENDER.remove(wrap.getId());
                    wrap.getSession().close();
                }
            });
        }, 30, 30, TimeUnit.SECONDS);
    }

}

