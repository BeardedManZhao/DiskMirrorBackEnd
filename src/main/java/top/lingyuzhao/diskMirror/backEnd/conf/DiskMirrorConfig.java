package top.lingyuzhao.diskMirror.backEnd.conf;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.lingyuzhao.diskMirror.conf.Config;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;
import top.lingyuzhao.diskMirror.core.module.ModuleManager;
import top.lingyuzhao.diskMirror.core.module.SkCheckModule;
import top.lingyuzhao.diskMirror.core.module.Verification;
import top.lingyuzhao.utils.IOUtils;
import top.lingyuzhao.utils.StrUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
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

    static {
        LOGGER.info("DiskMirrorConfig 类加载器: {}", DiskMirrorConfig.class.getClassLoader());
        // 盘镜后端 服务器 访问地址
        DiskMirrorConfig.putOption(WebConf.URL_PATH_PREFIX, "/DiskMirrorBackEnd");
        // 盘镜后端 设置接收数据时 可在内存中存储的数据容量 超出则会临时存在磁盘中 这里是 4Mb
        DiskMirrorConfig.putOption(WebConf.MAX_IN_MEMORY_SIZE, 4096 << 10);
        // 盘镜后端 设置接收数据的最大大小，单位是字节。-1 代表无限 这是 1 Gb
        DiskMirrorConfig.putOption(WebConf.MAX_UPLOAD_SIZE, 1024 << 10 << 10);
        // 配置需要被 盘镜 管理的路径 此路径也应该可以被 web 后端服务器访问到
        DiskMirrorConfig.putOption(WebConf.ROOT_DIR, "/DiskMirror/data");
        // 配置一切需要被盘镜处理的数据的编码
        DiskMirrorConfig.putOption(WebConf.DATA_TEXT_CHARSET, "UTF-8");
        // 设置每个空间中每种类型的文件存储最大字节数
        DiskMirrorConfig.putOption(WebConf.USER_DISK_MIRROR_SPACE_QUOTA, 128 << 10 << 10);
        // 设置协议前缀 需要确保你的服务器可以访问到这里！！！
        DiskMirrorConfig.putOption(WebConf.PROTOCOL_PREFIX, "https://xxx.lingyuzhao.top/");
        // 设置后端的允许跨域的所有主机
        DiskMirrorConfig.putOption(WebConf.ALL_HOST_CONTROL, JSONArray.from(
                new String[]{
                        "https://www.lingyuzhao.top",
                        "https://www.lingyuzhao.top/",
                }
        ));
        // 设置访问 diskMirror 时的密钥，这个密钥可以是数值也可以是字符串类型的对象，最终会根据特有的计算算法获取到一个数值
        // 获取到的数值会再后端服务运行的时候展示再日志中，前端的 diskMirror 的 js 文件中需要需要将这个数值做为key 才可以进行访问
        DiskMirrorConfig.putOption(WebConf.SECURE_KEY, 0);
        // 设置盘镜要加载的校验模块 以及模式 默认是 空列表
        DiskMirrorConfig.putOption(WebConf.VERIFICATION_LIST, JSONArray.from(new ArrayList<>()));

        /* 尝试加载文件中的配置 */
        // 加载配置文件 读取名为 DiskMirror_CONF 的值
        String diskMirrorConf1 = System.getenv("DiskMirror_CONF");
        if (diskMirrorConf1 != null) {
            File diskMirrorConf = new File(diskMirrorConf1);
            if (diskMirrorConf.exists()) {
                LOGGER.info("加载配置文件：{}", diskMirrorConf1);
                try (FileInputStream fileInputStream = new FileInputStream(diskMirrorConf)) {
                    DiskMirrorConfig.loadConf(JSONObject.parse(IOUtils.getStringByStream(fileInputStream)));
                } catch (IOException e) {
                    LOGGER.error("配置文件有尝试加载，但是失败了！因此使用默认配置！", e);
                }
            } else {
                try (FileOutputStream fileOutputStream = new FileOutputStream(diskMirrorConf)) {
                    fileOutputStream.write(DiskMirrorConfig.WEB_CONF.toString().getBytes());
                    LOGGER.info("您设置了配置文件目录为：{}，但是您的配置文件不存在，我们为您创建了默认配置文件！", diskMirrorConf1);
                } catch (IOException e) {
                    LOGGER.error("我们无法为您生成配置文件，可能是您指定的路径有目录不存在，或我们无权限创建文件，但我们使用了默认配置文件来运行 diskMirror", e);
                }
            }
        } else {
            LOGGER.warn("我们建议您设置一个名为 DiskMirror_CONF 的环境变量，我们期望使用其指向的文件作为配置文件！");
        }
        DiskMirrorConfig.reload();
    }

    /**
     * 使用额外的配置进行初始化动作，可以通过调用此构造函数来实现针对外界配置的加载
     *
     * @param webConf 这里就是代表使用的额外的配置
     */
    public static void loadConf(Map<String, Object> webConf) {
        final boolean b = webConf == null;
        // 如果有 webConf 则 loadConf不会生效
        if (!b) {
            LOGGER.info("public static void loadConf(webConf) run!!!");
            // 先移除 避免 putOption 的时候直接刷新了 IO_MODE 因为这个时候的 IO_MODE 类型是String 不是 DiskMirror, 后面处理了一下
            Object io_mode = webConf.remove(IO_MODE);
            // 加载额外配置 会覆盖原本的配置
            webConf.forEach(DiskMirrorConfig::putOption);
            try {
                JSONObject spaceMaxSize = (JSONObject) webConf.getOrDefault("SpaceMaxSize", new JSONObject());
                spaceMaxSize.forEach((spaceId, maxSize) -> DiskMirrorConfig.WEB_CONF.setSpaceMaxSize(spaceId, ((Number) maxSize).longValue()));
            } catch (ClassCastException e) {
                throw new UnsupportedOperationException("SpaceMaxSize 的格式不正确，其应该是一个，String为key类型，Long为value，其代表每个用户空间对应的数据容量！", e);
            }
            // 在这里将 string 的 io_mode 转换为枚举类型
            DiskMirrorConfig.putOption(IO_MODE, DiskMirror.valueOf(io_mode.toString()));
        } else {
            LOGGER.info("public static void loadConf(default) run!!!");
            DiskMirrorConfig.putOption(IO_MODE, DiskMirror.LocalFSAdapter);
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
            case WebConf.VERIFICATION_LIST:
                addVerifications((JSONArray) value);
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
        if (initialized.compareAndSet(false, true)) {
            final DiskMirror diskMirror = (DiskMirror) getOption(IO_MODE);
            LOGGER.info("diskMirror 构建空间配置器与diskMirror适配器:{}\n{}", diskMirror.toString(), diskMirror.getVersion());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(WEB_CONF::loadSpaceConfig);

            try {
                // 设置超时时间为 10 秒，可以根据需要调整
                future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                LOGGER.error("loadSpaceConfig 超时!!! " + WEB_CONF.getString(Config.USE_SPACE_CONFIG_MODE) + " -> " + WEB_CONF.getString(Config.REDIS_HOST_PORT_DB), e);
                throw new RuntimeException("loadSpaceConfig 超时", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("loadSpaceConfig 被中断!!! " + WEB_CONF.getString(Config.USE_SPACE_CONFIG_MODE) + " -> " + WEB_CONF.getString(Config.REDIS_HOST_PORT_DB), e);
                throw new RuntimeException("loadSpaceConfig 被中断", e);
            } catch (ExecutionException e) {
                LOGGER.error("loadSpaceConfig 执行异常!!! " + WEB_CONF.getString(Config.USE_SPACE_CONFIG_MODE) + " -> " + WEB_CONF.getString(Config.REDIS_HOST_PORT_DB), e);
                throw new RuntimeException("loadSpaceConfig 执行异常", e);
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
            }

            LOGGER.info(WEB_CONF.getString(Config.USE_SPACE_CONFIG_MODE) + " -> " + WEB_CONF.getString(Config.REDIS_HOST_PORT_DB));
            adapter = diskMirror.getAdapter(WEB_CONF);
        } else {
            LOGGER.warn("reload 方法已经被调用过了，不再重复执行。");
        }
    }

    /**
     * 获取 盘镜 后端系统 版本号
     *
     * @return 操作成功之后返回的结果
     */
    public static String getVersion() {
        return getVersion(WEB_CONF);
    }

    /**
     * 获取 盘镜 后端系统 版本号
     *
     * @param config 如果在外界设置了 config 对象，则您可以直接将外界的 config 传递进来 实现版本的计算与获取！
     * @return 操作成功之后返回的结果
     */
    public static String getVersion(Config config) {
        final Object orDefault = config.getOrDefault(IO_MODE, DiskMirror.LocalFSAdapter);
        if (orDefault instanceof DiskMirror) {
            return ((DiskMirror) orDefault).getVersion();
        }
        return orDefault.toString();
    }

    /**
     * 添加一个或多个验证器
     *
     * @param jsonArray 验证器数组
     */
    private static void addVerifications(JSONArray jsonArray) {
        jsonArray.forEach(o -> addVerification(o.toString()));
    }

    /**
     * 通过字符串命令添加一个验证器
     *
     * @param moduleName$loadMode 模块名$加载模式
     */
    private static void addVerification(String moduleName$loadMode) {
        final String[] strings = StrUtils.splitBy(moduleName$loadMode, '$');
        addVerification(strings[0], strings[1]);
    }

    /**
     * 添加一个验证器
     *
     * @param moduleName 校验模块的类名字
     * @param loadMode   验证模块的加载模式
     */
    private static void addVerification(String moduleName, String loadMode) {
        Verification verification = null;
        // 加载出模块对象
        if (moduleName.equals("SkCheckModule")) {
            verification = new SkCheckModule(moduleName, "密钥校验服务");
        } else {
            LOGGER.error("未定义的模块：{}", moduleName);
        }
        if (verification != null) {
            // 开始进行模块加载
            switch (loadMode) {
                case "read":
                    ModuleManager.registerModuleRead(verification);
                    break;
                case "writer":
                    ModuleManager.registerModuleWriter(verification);
                    break;
                default:
                    LOGGER.error("未定义的模块加载模式：{}", loadMode);
                    return;
            }
        }
        LOGGER.info("已加载 {} 模块：{}", loadMode, moduleName);
    }

    /**
     * 添加跨域配置 并进行信息日志的打印
     *
     * @param registry 注册对象
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final JSONArray jsonArray = WEB_CONF.getJSONArray(WebConf.ALL_HOST_CONTROL);
        LOGGER.info("盘镜 后端 MvcHttp 启动，允许跨域列表：{}", jsonArray);
        LOGGER.info("盘镜 后端 MvcHttp 加载配置，安全密钥：{}", WEB_CONF.getSecureKey());
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOrigins(jsonArray.stream().map(Object::toString).toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
        WebConf.LOGGER.info("diskMirror MvcHttp 处理器生效。");
    }

    // 定义一个CommonsMultipartResolver Bean，用于处理文件上传
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        // 创建一个新的CommonsMultipartResolver实例
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        // 设置默认字符编码 DATA_TEXT_CHARSET ，以支持中文文件名
        resolver.setDefaultEncoding(DiskMirrorConfig.getOptionString(WebConf.DATA_TEXT_CHARSET));
        // 设置上传文件的最大大小，单位是字节。此处设置为10MB
        resolver.setMaxUploadSize(((Number) DiskMirrorConfig.getOption(WebConf.MAX_UPLOAD_SIZE)).longValue());
        // 设置保存在内存中的文件的最大大小，超过此大小的文件将被保存到磁盘上
        resolver.setMaxInMemorySize(((Number) DiskMirrorConfig.getOption(WebConf.MAX_IN_MEMORY_SIZE)).intValue()); // 4KB
        // 返回配置好的CommonsMultipartResolver实例
        return resolver;
    }
}
