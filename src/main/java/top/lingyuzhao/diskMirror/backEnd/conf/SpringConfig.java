package top.lingyuzhao.diskMirror.backEnd.conf;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;

/**
 * Spring mvc 的配置类
 *
 * @author zhao
 */
@Configurable
// 在这里配置的就是扫描所有的请求处理器 所在的包路径
@ComponentScan(
        // 在这里设置的就是需要被扫描的包
        value = "top.lingyuzhao.diskMirror.backEnd.core.controller"
)
@EnableWebMvc
public final class SpringConfig {

    public static final WebConf WEB_CONF = new WebConf();


    /**
     * 回复页面要使用的字符集
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 操作过程中需要使用的适配器对象
     */
    public static Adapter adapter;

    /**
     * 手动修改并设置一些配置
     *
     * @param key   配置的 key
     * @param value 配置的value
     */
    public static void putOption(String key, Object value) {
        WEB_CONF.put(key, value);
        // 现在参数少，未来参数多了需要换成 switch
        if (WebConf.IO_MODE.equals(key)) {
            reload();
        }
    }

    /**
     * 获取一指定名字的配置
     *
     * @param key 配置项目的名字
     * @return 配置项目的参数
     */
    public static Object getOption(String key) {
        return WEB_CONF.get(key);
    }

    /**
     * 获取一指定名字的配置
     *
     * @param key 配置项目的名字
     * @return 配置项目的参数
     */
    public static String getOptionString(String key) {
        return WEB_CONF.get(key).toString();
    }

    /**
     * @return 当前系统中使用的适配器对象
     */
    public static Adapter getAdapter() {
        return adapter;
    }

    /**
     * 重新刷新配置 使其生效 此操作常用于刷新适配器等参数
     */
    public static void reload() {
        adapter = ((DiskMirror) getOption(WebConf.IO_MODE)).getAdapter(WEB_CONF);
    }

}
