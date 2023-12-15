package top.lingyuzhao.diskMirror.backEnd.core.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import top.lingyuzhao.diskMirror.backEnd.conf.SpringConfig;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;
import zhao.utils.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件系统的增删操作接口
 *
 * @author zhao
 */
@Controller
@RequestMapping(
        value = "FsCrud",
        // 告知前端页面，回复数据的解析方式
        produces = "text/html;charset=" + SpringConfig.CHARSET
)
@CrossOrigin(value = "*", allowCredentials = "true")
public class FsCrud implements CRUD {

    static {
        // 设置协议前缀 需要确保你的服务器可以访问到这里！！！
        SpringConfig.putOption(WebConf.PROTOCOL_PREFIX, "http://diskmirror.lingyuzhao.top");
        // 设置后端的IO模式
        SpringConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
    }

    /**
     * 从配置类中获取到适配器对象
     */
    final Adapter adapter = SpringConfig.getAdapter();

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String add(HttpServletRequest httpServletRequest) {
        try {
            // 提取出数据
            final Part file;
            final Part params;
            Part[] parts = {httpServletRequest.getPart("file"), httpServletRequest.getPart("params")};
            file = parts[0];
            params = parts[1];
            if (file == null || params == null) {
                if (file == null) {
                    return HttpUtils.getResJsonStr(new JSONObject(), "您的文件数据为空，请确保您要上传的文件数据存储在 ”file“ 对应的请求数据包中!!!");
                } else {
                    return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
                }
            }
            try (
                    final InputStream inputStream0 = params.getInputStream();
                    final InputStream inputStream1 = file.getInputStream()
            ) {
                return adapter.upload(inputStream1, JSONObject.parseObject(IOUtils.getStringByStream(inputStream0, SpringConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("add 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String remove(HttpServletRequest httpServletRequest) {
        try {
            final Part params = httpServletRequest.getPart("params");
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.remove(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, SpringConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | ServletException e) {
            WebConf.LOGGER.error("remove 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String get(HttpServletRequest httpServletRequest) {
        try {
            final Part params = httpServletRequest.getPart("params");
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrls(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, SpringConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | ServletException e) {
            WebConf.LOGGER.error("add 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }
}
