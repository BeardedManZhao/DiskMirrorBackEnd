package top.lingyuzhao.diskMirror.backEnd.core.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.conf.Config;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.utils.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private final static IOException fileNotExist = new IOException("文件不存在");

    /**
     * 从配置类中获取到适配器对象
     */
    final Adapter adapter;
    private final String errorParamsIsNull, errorFileIsNull;

    public FsCrud() {
        this(DiskMirrorConfig.getAdapter());
    }

    /**
     * 直接使用在外部初始化好的适配器来进行初始化
     *
     * @param adapter 在外界实例化好的适配器对象
     */
    public FsCrud(Adapter adapter) {
        this.adapter = adapter;
        this.errorFileIsNull  = HttpUtils.getResJsonStr(new JSONObject(), "您的文件数据为空，请确保您要上传的文件数据存储在 ”file“ 对应的请求数据包中!!!");
        this.errorParamsIsNull = HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
    }

    /**
     * 获取文件加密的密钥
     *
     * @param httpServletRequest 来自前端的请求对象
     * @param defKey             默认的密钥值，如果请求对象中没有包含 请求对象 则会使用此参数！
     * @param jsonObject         需要用来存储 key 的 json对象
     */
    private static void getDiskMirrorXorSecureKey(HttpServletRequest httpServletRequest, int defKey, JSONObject jsonObject) {
        if (httpServletRequest == null || httpServletRequest.getCookies() == null) {
            jsonObject.put("secure.key", defKey);
            return;
        }
        for (Cookie cookie1 : httpServletRequest.getCookies()) {
            if ("diskMirror_xor_secure_key".equals(cookie1.getName())) {
                jsonObject.put("secure.key", HttpUtils.xorDecrypt(Integer.parseInt(cookie1.getValue())));
                break;
            }
        }
    }

    @Override
    public String add(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params, @RequestPart("file") MultipartFile file) {
        try {
            // 校验数据
            if (file == null || params == null) {
                if (file == null) {
                    return this.errorFileIsNull;
                } else {
                    return this.errorParamsIsNull;
                }
            }
            try (
                    final InputStream inputStream0 = params.getInputStream();
                    final InputStream inputStream1 = file.getInputStream()
            ) {
                return adapter.upload(inputStream1, JSONObject.parseObject(IOUtils.getStringByStream(inputStream0, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("add 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String remove(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.remove(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("remove 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String reName(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.reName(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("reName 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String get(HttpServletRequest httpServletRequest, @RequestParam("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrls(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("get 函数调用错误!!!", e);
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
    public String getUrlsNoRecursion(HttpServletRequest httpServletRequest, @RequestParam("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrlsNoRecursion(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("getUrlsNoRecursion 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public void downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         @PathVariable("userId") String userId, @PathVariable("type") String type,
                         String fileName, Integer sk) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("fileName", fileName);
        jsonObject.put("type", type);

        // 解密并提取 sk
        getDiskMirrorXorSecureKey(httpServletRequest, sk == null ? 0 : sk, jsonObject);

        // 获取文件元数据及最后修改时间
        final JSONObject urlsNoRecursion;
        try {
            urlsNoRecursion = adapter.getUrlsNoRecursion(jsonObject.clone());
            if (urlsNoRecursion == null) {
                throw fileNotExist;
            }
        } catch (IOException e) {
            WebConf.LOGGER.warn("文件不存在: " + fileName);
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Long lastModified = (Long) urlsNoRecursion.get("lastModified");
        if (lastModified == null || lastModified <= 0) {
            lastModified = System.currentTimeMillis(); // 默认当前时间
        }

        // 设置 Last-Modified 响应头
        httpServletResponse.setDateHeader("Last-Modified", lastModified);
        // 设置 Content-Length 响应头
        httpServletResponse.setHeader("Content-Length", urlsNoRecursion.getString("size"));

        // 检查 If-Modified-Since 请求头
        long ifModifiedSince = httpServletRequest.getDateHeader("If-Modified-Since");
        if (ifModifiedSince >= lastModified) {
            // 文件未修改，返回 304 Not Modified
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // 设置其他响应头
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        try (InputStream fileInputStream = adapter.downLoad(jsonObject)) {
            if (fileInputStream == null) {
                httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                IOUtils.copy(fileInputStream, outputStream, true);
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.warn(e.toString());
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public String transferDeposit(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDeposit(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("transferDeposit 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String getAllProgressBar(String id) {
        return adapter.getAllProgressBar(id).toString();
    }

    @Override
    public String transferDepositStatus(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDepositStatus(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("transferDeposit 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    public String mkdirs(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.mkdirs(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("mkdirs 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String getSpaceSize(String spaceId) {
        return HttpUtils.getResJsonStr(new JSONObject(), adapter.getConfig().getSpaceMaxSize(spaceId));
    }

    @Override
    public String setSpaceSize(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    // 获取到 id 和 size 以及安全密钥
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)));
                    final Object userId = jsonObject.get("userId");
                    final Long newSize = jsonObject.getLong("newSize");
                    final int sk = jsonObject.getIntValue(WebConf.SECURE_KEY);
                    if (userId == null || newSize == null) {
                        throw new UnsupportedOperationException("请求参数不合规，请确保您在调用 setSpaceSize 函数的参数中设置了 userId and newSize， error:" + jsonObject);
                    }
                    adapter.setSpaceMaxSize(userId.toString(), newSize, sk);
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("setSpaceSize 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    public String getVersion() {
        return DiskMirrorConfig.getVersion(adapter.getConfig());
    }

    @Override
    public String getUseSize(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)));
                    jsonObject.put("useSize", adapter.getUseSize(jsonObject));
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("getUseSize 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String setSpaceSk(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET)));
                    final int i = this.adapter.setSpaceSk(jsonObject.getString("userId"), jsonObject.getIntValue(WebConf.SECURE_KEY));
                    jsonObject.put(WebConf.SECURE_KEY, i);
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("setSpaceSk 函数调用错误!!!", e);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(WebConf.SECURE_KEY, e.toString());
            return HttpUtils.getResJsonStr(jsonObject, e.toString());
        }
    }
}
