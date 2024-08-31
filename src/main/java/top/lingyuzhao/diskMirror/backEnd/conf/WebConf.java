package top.lingyuzhao.diskMirror.backEnd.conf;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingyuzhao.diskMirror.conf.Config;

/**
 * web 配置类
 */
public final class WebConf extends Config {
    /**
     * 文本数据处理时需要使用的编码集
     */
    public static final String DATA_TEXT_CHARSET = "data.text.charset";
    /**
     * 盘镜 的 处理模式设置
     */
    public static final String IO_MODE = "diskMirror.mode";
    /**
     * 盘镜后端跨域列表
     */
    public static final String ALL_HOST_CONTROL = "all.host.control";
    /**
     * 盘镜后端 设置接收数据的最大大小，单位是字节。-1 代表无限
     */
    public static final String MAX_UPLOAD_SIZE = "max.upload.size";
    /**
     * 盘镜后端 设置接收数据时 可在内存中存储的数据容量 超出则会临时存在磁盘中
     */
    public static final String MAX_IN_MEMORY_SIZE = "max.in.memory.size";
    /**
     * 日志控制器
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("DiskMirrorBackEnd");

    @Override
    public String toString() {
        // 移除不可加载的配置项，这类配置被使用无用处
        JSONObject clone = super.clone();
        clone.remove(GENERATION_RULES);
        // 返回
        return clone.toString();
    }
}
