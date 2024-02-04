package top.lingyuzhao.diskMirror.backEnd.conf;

import com.alibaba.fastjson2.JSONArray;
import com.sun.istack.internal.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;

import java.util.Arrays;

import static top.lingyuzhao.diskMirror.backEnd.conf.WebConf.IO_MODE;

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
public final class DiskMirrorConfig implements WebMvcConfigurer {

    public static final WebConf WEB_CONF = new WebConf();
    /**
     * 回复页面要使用的字符集
     */
    public static final String CHARSET = "UTF-8";
    /**
     * 跨域允许的所有主机对应的数组对象
     * 设置允许跨域访问的主机
     */
    public static final String[] ALL_HOST;
    /**
     * 操作过程中需要使用的适配器对象
     */
    public static Adapter adapter;
    public static Logger logger = Logger.getLogger(DiskMirrorConfig.class);

    /*
     * 静态代码块 用于初始化一些配置
     * TODO 需要配置
     */
    static {
        // 配置需要被 盘镜 管理的路径 此路径也应该可以被 web 后端服务器访问到
        DiskMirrorConfig.putOption(WebConf.ROOT_DIR, "/DiskMirror/data");
        // 配置一切需要被盘镜处理的数据的编码
        DiskMirrorConfig.putOption(WebConf.DATA_TEXT_CHARSET, "UTF-8");
        // 设置每个空间中每种类型的文件存储最大字节数
        DiskMirrorConfig.putOption(WebConf.USER_DISK_MIRROR_SPACE_QUOTA, 128 << 10 << 10);
        // 设置协议前缀 需要确保你的服务器可以访问到这里！！！
        DiskMirrorConfig.putOption(WebConf.PROTOCOL_PREFIX, "http://diskmirror.lingyuzhao.top");
        // 设置后端的允许跨域的所有主机
        ALL_HOST = new String[]{
                "http://www.lingyuzhao.top",
                "http://www.lingyuzhao.top/"
        };
        DiskMirrorConfig.putOption(WebConf.ALL_HOST_CONTROL, JSONArray.from(ALL_HOST));
        // 设置访问 diskMirror 时的密钥
        DiskMirrorConfig.putOption(WebConf.SECURE_KEY, "diskMirror");
        // 设置后端的IO模式 请确保这个是最后一个配置项目 因为在配置了此项目之后 就会构建适配器
        DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
    }

    /**
     * 手动修改并设置一些配置
     *
     * @param key   配置的 key
     * @param value 配置的value
     */
    public static void putOption(String key, Object value) {
        WEB_CONF.put(key, value);
        // 现在参数少，未来参数多了需要换成 switch
        switch (key) {
            case WebConf.SECURE_KEY:
                WEB_CONF.setSecureKey(value);
                break;
            case WebConf.IO_MODE:
                reload();
                break;
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
        final DiskMirror diskMirror = (DiskMirror) getOption(WebConf.IO_MODE);
        logger.info("diskMirror 构建适配器:" + diskMirror.toString() + "\n" + diskMirror.getVersion());
        adapter = diskMirror.getAdapter(WEB_CONF);
    }

    /**
     * 获取 盘镜 后端系统 版本号
     *
     * @return 操作成功之后返回的结果
     */
    public static String getVersion() {
        final Object orDefault = WEB_CONF.getOrDefault(IO_MODE, DiskMirror.LocalFSAdapter);
        if (orDefault instanceof DiskMirror) {
            return ((DiskMirror) orDefault).getVersion();
        }
        return orDefault.toString();
    }

    /**
     * 添加跨域配置 并进行信息日志的打印
     *
     * @param registry 注册对象
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("盘镜 后端启动，允许跨域列表：" + Arrays.toString(ALL_HOST));
        logger.info("盘镜 后端加载配置，安全密钥：" + WEB_CONF.getSecureKey());
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOrigins(ALL_HOST)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
