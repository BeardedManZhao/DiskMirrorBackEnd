package top.lingyuzhao.diskMirror.backEnd.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 具有增删改查功能的控制器
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
     * @param httpServletRequest 来自前端的请求对象
     * @return 操作成功之后的返回结果
     */
    @RequestMapping("/mkdirs")
    @ResponseBody
    String mkdirs(HttpServletRequest httpServletRequest);
}
