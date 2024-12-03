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

/**
 * 文件系统的增删操作接口
 *
 * @author zhao
 */
public class FsCrudWebSocketHandler extends TextWebSocketHandler {

    private final Adapter adapter;

    public FsCrudWebSocketHandler() {
        this(DiskMirrorConfig.getAdapter());
    }

    public FsCrudWebSocketHandler(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 处理响应 确保其中带有来源的 command 标识
     *
     * @param command 处理的来源
     * @param res     响应结果
     * @return 结果的 TextMessage 对象
     */
    private static TextMessage handleRes(String command, JSONObject res) {
        return new TextMessage(String.format("{\"command\": \"%s\", \"data\": %s}", command, res.toJSONString()));
    }

    private static TextMessage handleRes(String command, long res) {
        return new TextMessage(String.format("{\"command\": \"%s\", \"data\": %s}", command, res));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject params = JSONObject.parseObject(payload);

        // 处理不同的命令
        String command = params.getString("command");
        switch (command) {
            case "upload":
                handleAdd(session, params);
                break;
            case "remove":
                handleRemove(session, params);
                break;
            case "reName":
                handleRename(session, params);
                break;
            case "getUrls":
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
            case "getUseSize":
                handleGetUseSize(session, params);
                break;
            default:
                session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(new JSONObject(), "未知命令:" + command)));
                break;
        }
    }

    private void handleAdd(WebSocketSession session, JSONObject params) throws IOException {
        session.sendMessage(new TextMessage(HttpUtils.getResJsonStr(params, "不支持此操作：upload")));
    }

    private void handleRemove(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 remove 命令
        session.sendMessage(handleRes("remove", adapter.remove(params)));
        // 由于是修改 因此一并返回修改之后的字节
        session.sendMessage(handleRes("getUseSize", adapter.getUseSize(params)));
    }

    private void handleRename(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 rename 命令
        session.sendMessage(handleRes("reName", adapter.reName(params)));
    }

    private void handleGet(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 get 命令
        session.sendMessage(handleRes("getUrls", adapter.getUrls(params)));
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
        session.sendMessage(handleRes("transferDeposit", adapter.transferDeposit(params)));
        session.sendMessage(handleRes("getUseSize", adapter.getUseSize(params)));
    }

    private void handleGetAllProgressBar(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getAllProgressBar 命令
        session.sendMessage(handleRes("getAllProgressBar", adapter.getAllProgressBar(params.getString("userId"))));
    }

    private void handleTransferDepositStatus(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 transferDepositStatus 命令
        session.sendMessage(handleRes("transferDepositStatus", adapter.transferDepositStatus(params)));
    }

    private void handleMkdirs(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 mkdirs 命令
        session.sendMessage(handleRes("mkdirs", adapter.mkdirs(params)));
    }

    // data 无校验码
    private void handleGetSpaceSize(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getSpaceSize 命令
        session.sendMessage(handleRes("getSpaceSize", adapter.getSpaceMaxSize(params.getString("userId"))));
    }

    // data 无校验码
    private void handleGetUseSize(WebSocketSession session, JSONObject params) throws IOException {
        // 处理 getUseSize 命令
        session.sendMessage(handleRes("getUseSize", adapter.getUseSize(params)));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 连接建立后的处理
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 连接关闭后的处理
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        // 处理传输错误
        WebConf.LOGGER.error("webSocket 传输错误: ", exception);
    }
}
