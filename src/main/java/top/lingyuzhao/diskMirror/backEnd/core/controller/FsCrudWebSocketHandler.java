package top.lingyuzhao.diskMirror.backEnd.core.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.core.Adapter;

import java.io.IOException;

public class FsCrudWebSocketHandler extends TextWebSocketHandler {

    private final Adapter adapter;

    public FsCrudWebSocketHandler() {
        this(DiskMirrorConfig.getAdapter());
    }

    public FsCrudWebSocketHandler(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject params = JSONObject.parseObject(payload);

        // 处理不同的命令
        String command = params.getString("command");
        switch (command) {
            case "add":
                handleAdd(session, params);
                break;
            case "remove":
                handleRemove(session, params);
                break;
            case "rename":
                handleRename(session, params);
                break;
            case "get":
                handleGet(session, params);
                break;
            case "download":
                handleDownload(session, params);
                break;
            case "transferDeposit":
                handleTransferDeposit(session, params);
                break;
            case "getAllProgressBar":
                handleGetAllProgressBar(session, params);
                break;
            case "transferDepositStatus":
                handleTransferDepositStatus(session, params);
                break;
            case "mkdirs":
                handleMkdirs(session, params);
                break;
            case "getSpaceSize":
                handleGetSpaceSize(session, params);
                break;
            case "setSpaceSize":
                handleSetSpaceSize(session, params);
                break;
            case "getVersion":
                handleGetVersion(session, params);
                break;
            case "getUseSize":
                handleGetUseSize(session, params);
                break;
            case "setSpaceSk":
                handleSetSpaceSk(session, params);
                break;
            default:
                session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(new JSONObject(), "未知命令")));
                break;
        }
    }

    private void handleAdd(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 add 命令
        // 这里需要处理文件上传，可以考虑将文件上传部分单独封装成一个方法
        // 示例代码：
        // String result = adapter.upload(inputStream, params);
        // session.sendMessage(new TextMessage(result));
        session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(params, "不支持此操作：add")));
    }

    private void handleRemove(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 remove 命令
        String result = adapter.remove(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleRename(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 rename 命令
        String result = adapter.reName(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleGet(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 get 命令
        String result = adapter.getUrls(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleDownload(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 download 命令
        // 这里需要处理文件下载，可以考虑将文件下载部分单独封装成一个方法
        // 示例代码：
        // InputStream fileInputStream = adapter.downLoad(params);
        // 发送文件流给客户端
        session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(params, "不支持此操作：download")));
    }

    private void handleTransferDeposit(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 transferDeposit 命令
        String result = adapter.transferDeposit(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleGetAllProgressBar(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getAllProgressBar 命令
        String result = adapter.getAllProgressBar(params.getString("id")).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleTransferDepositStatus(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 transferDepositStatus 命令
        String result = adapter.transferDepositStatus(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleMkdirs(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 mkdirs 命令
        String result = adapter.mkdirs(params).toString();
        session.sendMessage(new TextMessage(result));
    }

    private void handleGetSpaceSize(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getSpaceSize 命令
        String result = String.valueOf(adapter.getSpaceMaxSize(params.getString("spaceId")));
        session.sendMessage(new TextMessage(result));
    }

    private void handleSetSpaceSize(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 setSpaceSize 命令
        String userId = params.getString("userId");
        Long newSize = params.getLong("newSize");
        adapter.setSpaceMaxSize(userId, newSize);
        session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(params, "设置成功")));
    }

    private void handleGetVersion(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getVersion 命令
        String result = adapter.version();
        session.sendMessage(new TextMessage(result));
    }

    private void handleGetUseSize(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getUseSize 命令
        String result = String.valueOf(adapter.getUseSize(params));
        session.sendMessage(new TextMessage(result));
    }

    private void handleSetSpaceSk(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 setSpaceSk 命令
        String userId = params.getString("userId");
        String result = String.valueOf(adapter.setSpaceSk(userId));
        session.sendMessage(new TextMessage(result));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后的处理
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭后的处理
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 处理传输错误
        WebConf.LOGGER.error("webSocket 传输错误: ", exception);
    }
}
