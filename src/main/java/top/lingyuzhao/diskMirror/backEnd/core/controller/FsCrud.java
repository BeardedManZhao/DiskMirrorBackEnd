package top.lingyuzhao.diskMirror.backEnd.core.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.conf.Config;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.utils.IOUtils;

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
        produces = "text/html;charset=" + DiskMirrorConfig.CHARSET,
        method = {RequestMethod.POST}
)
public class FsCrud implements CRUD {

    /**
     * 从配置类中获取到适配器对象
     */
    final Adapter adapter;

    /**
     * 使用 diskMirror 的配置类进行初始化
     */
    public FsCrud() {
        this.adapter = DiskMirrorConfig.getAdapter();
    }

    /**
     * 直接使用在外部初始化好的适配器来进行初始化
     *
     * @param adapter 在外界实例化好的适配器对象
     */
    public FsCrud(Adapter adapter) {
        this.adapter = adapter;
    }

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
                return adapter.upload(inputStream1, JSONObject.parseObject(IOUtils.getStringByStream(inputStream0, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
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
                    return adapter.remove(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("remove 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 重命名类的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String reName(HttpServletRequest httpServletRequest) {
        try {
            final Part params = httpServletRequest.getPart("params");
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.reName(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("reName 函数调用错误!!!", e);
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
                    return adapter.getUrls(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("get 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 创建一个文件目录的后端处理函数
     *
     * @param httpServletRequest 来自前端的请求对象
     * @return 操作成功之后的返回结果
     */
    public String mkdirs(HttpServletRequest httpServletRequest) {
        try {
            final Part params = httpServletRequest.getPart("params");
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.mkdirs(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("mkdirs 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取到指定空间的大小
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @param spaceId 指定的空间的id
     * @return 返回指定空间的大小 单位是字节
     */
    @Override
    public String getSpaceSize(String spaceId) {
        return HttpUtils.getResJsonStr(new JSONObject(), adapter.getConfig().getSpaceMaxSize(spaceId));
    }

    /**
     * 设置指定空间的大小，此操作需要提供安全密钥
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @param httpServletRequest 请求对象
     * @return 操作结果
     */
    @Override
    public String setSpaceSize(HttpServletRequest httpServletRequest) {
        try {
            final Part params = httpServletRequest.getPart("params");
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    // 获取到 id 和 size 以及安全密钥
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)));
                    final Object userId = jsonObject.get("userId");
                    final Long newSize = jsonObject.getLong("newSize");
                    if (userId == null || newSize == null) {
                        throw new UnsupportedOperationException("请求参数不合规，请确保您在调用 setSpaceSize 函数的参数中设置了 userId and newSize， error:" + jsonObject);
                    }
                    adapter.setSpaceMaxSize(userId.toString(), newSize);
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException | ServletException e) {
            WebConf.LOGGER.error("mkdirs 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取 盘镜 后端系统 版本号
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @return 操作成功之后返回的结果
     */
    public String getVersion() {
        return DiskMirrorConfig.getVersion();
    }
}
