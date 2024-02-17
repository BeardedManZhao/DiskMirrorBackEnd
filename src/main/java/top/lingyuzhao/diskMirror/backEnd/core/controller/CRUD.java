package top.lingyuzhao.diskMirror.backEnd.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 具有增删改查功能的控制器接口
 */
public interface CRUD {

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/add")
    @ResponseBody
    String add(HttpServletRequest httpServletRequest);

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/remove")
    @ResponseBody
    String remove(HttpServletRequest httpServletRequest);

    /**
     * 重命名类的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/reName")
    @ResponseBody
    String reName(HttpServletRequest httpServletRequest);

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/getUrls")
    @ResponseBody
    String get(HttpServletRequest httpServletRequest);

    /**
     * 创建一个文件目录的后端处理函数
     *
     * @param httpServletRequest 来自前端的请求对象
     * @return 操作成功之后的返回结果
     */
    @RequestMapping("/mkdirs")
    @ResponseBody
    String mkdirs(HttpServletRequest httpServletRequest);

    /**
     * 获取到指定空间的大小
     *
     * @param spaceId 指定的空间的id
     * @return 返回指定空间的大小 单位是字节
     */
    @RequestMapping("/getSpaceSize")
    @ResponseBody
    String getSpaceSize(String spaceId);

    /**
     * 设置指定空间的大小，此操作需要提供安全密钥
     *
     * @param httpServletRequest 请求对象
     * @return 操作结果
     */
    @RequestMapping("/setSpaceSize")
    @ResponseBody
    String setSpaceSize(HttpServletRequest httpServletRequest);

    /**
     * 获取 盘镜 后端系统 版本号
     *
     * @return 操作成功之后返回的结果
     */
    @RequestMapping("/getVersion")
    @ResponseBody
    String getVersion();
}
