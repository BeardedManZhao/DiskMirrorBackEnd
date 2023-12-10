package top.lingyuzhao.diskMirror.backEnd.core.controller;

import com.alibaba.fastjson2.JSONObject;
import top.lingyuzhao.diskMirror.backEnd.conf.SpringConfig;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.core.Adapter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author zhao
 */
public class FsCrud implements CRUD {

    /**
     * 从配置类中获取到适配器对象
     */
    final Adapter adapter = SpringConfig.getAdapter();

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    @Override
    public String add(HttpServletRequest httpServletRequest, String jsonData) {
        try {
            try (final ServletInputStream inputStream = httpServletRequest.getInputStream()) {
                adapter.upload(inputStream, JSONObject.parseObject(jsonData));
            }
            return HttpUtils.getResJsonStr(new JSONObject(), SpringConfig.getOption(SpringConfig.WebConf.OK_VALUE));
        } catch (IOException e) {
            e.printStackTrace();
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    @Override
    public String remove(HttpServletRequest httpServletRequest, String jsonData) {
        return null;
    }

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    @Override
    public String get(HttpServletRequest httpServletRequest, String jsonData) {
        return null;
    }
}
