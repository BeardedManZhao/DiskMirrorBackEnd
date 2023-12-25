package top.lingyuzhao.diskMirror.backEnd.conf;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * 这里是存储者所有 虚拟路径与处理类数据的配置类
 *
 * @author zhao
 */
public class DiskMirrorInit extends AbstractDispatcherServletInitializer {

    /**
     * 我们需要在这里加载 SpringMVC对应的容器对象
     *
     * @return SpringMVC对应的容器对象
     */
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        // 初始化容器对象
        final AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        // 将配置类注册到容器对象中
        webApplicationContext.register(DiskMirrorConfig.class);
        // 返回容器对象
        return webApplicationContext;
    }

    /**
     * 我们需要在这里设置哪些请求可以由SpringMVC来处理，哪些由TomCat处理
     *
     * @return 使用 SpringMVC 处理的所有请求资源名称
     */
    @Override
    protected String[] getServletMappings() {
        // 根目录中的所有服务都由SpringMVC处理
        return new String[]{"/"};
    }

    /**
     * 我们需要在这里加载 Spring 对应的容器对象 不过现在用不上Spring
     *
     * @return Spring 对应的容器对象
     */
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }

    /**
     * 我们需要在这里设置 过滤器 数组
     *
     * @return 过滤器数组
     */
    @Override
    protected Filter[] getServletFilters() {
        // 实例化字符编码过滤器
        final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        // 设置字符编码集
        characterEncodingFilter.setEncoding(DiskMirrorConfig.getOption(WebConf.DATA_TEXT_CHARSET).toString());
        // 将过滤器数组返回出去
        return new Filter[]{
                characterEncodingFilter
        };
    }

}
