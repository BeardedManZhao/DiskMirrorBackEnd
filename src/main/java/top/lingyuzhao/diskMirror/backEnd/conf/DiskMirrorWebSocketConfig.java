package top.lingyuzhao.diskMirror.backEnd.conf;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import top.lingyuzhao.diskMirror.backEnd.core.controller.FsCrudWebSocketHandler;

import static top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig.WEB_CONF;
import static top.lingyuzhao.diskMirror.backEnd.conf.WebConf.LOGGER;

/**
 * WebSocket配置
 *
 * @author 赵凌宇
 */
@Configuration
@EnableWebSocket
public class DiskMirrorWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        final JSONArray jsonArray = WEB_CONF.getJSONArray(WebConf.ALL_HOST_CONTROL);
        LOGGER.info("盘镜 webSocket 后端启动，允许跨域列表：{}", jsonArray);
        LOGGER.info("盘镜 webSocket 后端加载配置，安全密钥：{}", WEB_CONF.getSecureKey());
        // 如果当前主机为聊天处理器 就启动实时通道
        registry.addHandler(new FsCrudWebSocketHandler(), "/socketFsCrud")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
        // .setAllowedOrigins(jsonArray.stream().map(Object::toString).toArray(String[]::new)); //允许跨域访问
        WebConf.LOGGER.info("diskMirror webSocket 处理器生效，路径：/socketFsCrud");
    }
}
