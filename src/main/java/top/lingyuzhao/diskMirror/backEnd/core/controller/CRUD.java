package top.lingyuzhao.diskMirror.backEnd.core.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * 具有增删改查功能的控制器
 */
public interface CRUD {

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    String add(HttpServletRequest httpServletRequest, String jsonData);

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    String remove(HttpServletRequest httpServletRequest, String jsonData);

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @param jsonData           请求参数的 json 字符串
     * @return 返回结果
     */
    String get(HttpServletRequest httpServletRequest, String jsonData);
}
