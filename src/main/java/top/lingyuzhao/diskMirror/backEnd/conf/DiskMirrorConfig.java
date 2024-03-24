package top.lingyuzhao.diskMirror.backEnd.conf;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;

import java.util.Map;

import static top.lingyuzhao.diskMirror.backEnd.conf.WebConf.IO_MODE;
import static top.lingyuzhao.diskMirror.backEnd.conf.WebConf.LOGGER;

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

    /**
     * diskMirror 后端文件系统的配置对象，此配置对象的作用与 DiskMirror 中的配置对象一致，是来自同一个类的。
     */
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
     * 使用额外的配置进行初始化动作，可以通过调用此构造函数来实现针对外界配置的加载
     *
     * @param webConf 这里就是代表使用的额外的配置
     */
    public static void loadConf(Map<String, Object> webConf) {
        final boolean b = webConf != null;
        loadConf(!b);
        if (b) {
            LOGGER.info("public static void loadConf(webConf) run!!!");
            // 加载额外配置 会覆盖原本的配置
            webConf.forEach(DiskMirrorConfig::putOption);
            reload();
        }
    }

    /**
     * 加载配置 在 loadConf 函数中我们可以指定一些配置 用于初始化适配器，此函数会在 DiskMirrorConfig 实例化的时候调用，此函数可以进行重写，或者进行修改。
     * <p>
     * In the loadConf function, we can specify some configurations to initialize the adapter. This function will be called during the instantiation of DiskMirrorConfig, and can be rewritten or modified.
     */
    public static void loadConf() {
        loadConf(true);
    }

    /**
     * 加载配置 在 loadConf 函数中我们可以指定一些配置 用于初始化适配器，此函数会在 DiskMirrorConfig 实例化的时候调用，此函数可以进行重写，或者进行修改。
     * <p>
     * In the loadConf function, we can specify some configurations to initialize the adapter. This function will be called during the instantiation of DiskMirrorConfig, and can be rewritten or modified.
     *
     * @param useReLoad 是否重新刷新配置
     */
    public static void loadConf(boolean useReLoad) {
        LOGGER.info("public static void loadConf() run!!!");
        // 配置需要被 盘镜 管理的路径 此路径也应该可以被 web 后端服务器访问到
        DiskMirrorConfig.putOption(WebConf.ROOT_DIR, "/DiskMirror/data");
        // 配置一切需要被盘镜处理的数据的编码
        DiskMirrorConfig.putOption(WebConf.DATA_TEXT_CHARSET, "UTF-8");
        // 设置每个空间中每种类型的文件存储最大字节数
        DiskMirrorConfig.putOption(WebConf.USER_DISK_MIRROR_SPACE_QUOTA, 128 << 10 << 10);
        // 设置协议前缀 需要确保你的服务器可以访问到这里！！！
        DiskMirrorConfig.putOption(WebConf.PROTOCOL_PREFIX, "https://xxx.xxx/");
        // 设置后端的允许跨域的所有主机
        DiskMirrorConfig.putOption(WebConf.ALL_HOST_CONTROL, JSONArray.from(
                new String[]{
                        "https://www.lingyuzhao.top",
                        "https://www.lingyuzhao.top/"
                }
        ));

        // 设置访问 diskMirror 时的密钥，这个密钥可以是数值也可以是字符串类型的对象，最终会根据特有的计算算法获取到一个数值
        // 获取到的数值会再后端服务运行的时候展示再日志中，前端的 diskMirror 的 js 文件中需要需要将这个数值做为key 才可以进行访问
        DiskMirrorConfig.putOption(WebConf.SECURE_KEY, 0);
        // 显式的设置某个空间的磁盘配额 能让此用户空间不受到磁盘配额限制 这里是让 25 号空间不受限制 根据这里的配置来进行操作
        DiskMirrorConfig.WEB_CONF.setSpaceMaxSize("25", 256 << 10 << 10);
        if (useReLoad) {
            // 如果在这个时候操作都准备好了（使用 useReLoad 判断是否已经准备好了）
            // 最后就设置后端的IO模式 请确保这个是最后一个配置项目 因为在配置了此项目之后 就会构建适配器
            DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
        } else {
            // 如果没有准备好就使用这样的函数赋值 这可以不触发 reload
            WEB_CONF.put(IO_MODE, DiskMirror.LocalFSAdapter);
        }
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
        LOGGER.info("diskMirror 构建适配器:" + diskMirror.toString() + "\n" + diskMirror.getVersion());
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
        final JSONArray jsonArray = WEB_CONF.getJSONArray(WebConf.ALL_HOST_CONTROL);
        LOGGER.info("盘镜 后端启动，允许跨域列表：" + jsonArray);
        LOGGER.info("盘镜 后端加载配置，安全密钥：" + WEB_CONF.getSecureKey());
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOrigins(jsonArray.stream().map(Object::toString).toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
