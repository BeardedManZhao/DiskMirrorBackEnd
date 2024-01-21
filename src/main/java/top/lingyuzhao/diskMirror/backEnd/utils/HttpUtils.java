package top.lingyuzhao.diskMirror.backEnd.utils;

import com.alibaba.fastjson2.JSONObject;
import top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.utils.StrUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http 协议处理工具类
 *
 * @author zhao
 */
public final class HttpUtils {

    /**
     * 为 json 添加上结果并返回
     *
     * @param jsonObject       需要被添加结果的json对象
     * @param resReturnOkValue 返回数值
     * @return json的字符串
     */
    public static String getResJsonStr(JSONObject jsonObject, Object resReturnOkValue) {
        return getResJson(jsonObject, resReturnOkValue).toString();
    }

    /**
     * 为 json 添加上结果并返回
     *
     * @param jsonObject       需要被添加结果的json对象
     * @param resReturnOkValue 返回数值
     * @return json的字符串
     */
    public static JSONObject getResJson(JSONObject jsonObject, Object resReturnOkValue) {
        jsonObject.put(DiskMirrorConfig.getOptionString(WebConf.RES_KEY), resReturnOkValue);
        return jsonObject;
    }

    /**
     * 解析 url 中的参数
     *
     * @param url 需要被解析的url
     * @return 解析结果
     */
    public static Map<String, String> parseQueryParams(String url) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String[] pairs = StrUtils.splitBy(StrUtils.splitBy(url, '?')[1], '&');
        for (String pair : pairs) {
            String[] keyValue = StrUtils.splitBy(pair, '=');
            if (keyValue.length < 2) {
                continue;
            }
            queryPairs.put(keyValue[0], keyValue[1]);
        }
        return queryPairs;
    }
}
