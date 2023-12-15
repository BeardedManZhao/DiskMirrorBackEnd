package top.lingyuzhao.diskMirror.backEnd.conf;

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
     * 日志控制器
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("DiskMirrorBackEnd");

}
