# ![image](https://github.com/BeardedManZhao/DiskMirror/assets/113756063/b8a15b22-5ca0-4552-aab2-7131c63dc727) DiskMirror

用于进行磁盘文件管理的一面镜子，其包含许多的适配器，能够将任何类型的文件数据流中的数据接入到管理中，并将保存之后的 url 返回，支持不同文件所属空间的管控，您还可以通过此API 获取到指定 userid 下面的所有文件的
url，在诸多场景中可以简化IO相关的实现操作，能够降低开发量，例如web服务器中的磁盘管理操作!

## 什么是适配器

适配器在这里是用于进行文件传输的桥梁，能够让您将自己的数据源（例如您的后端服务器）与指定的数据终端（例如您的各类文件系统）进行连接，将数据提供给数据终端，减少了您手动开发IO代码的时间。

在未来，我们将会提供更多的适配器选项，让适配器的数据终端具有更多的支持。

### 我如何部署 盘镜 后端系统

本软件支持两种方式进行配置，第一种就是直接下载后端服务器的源码，并进行修改和打包部署，第二种就是直接在 SpringBoot 中进行二次开发，期间进行配置

#### 源码配置 - 下载并修改源码

您需要下载我们的后端系统源码包，并在 `top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig`
类中去进行一些配置，下面就是需要被配置的代码块，您可以在类中找到下面的代码块，并在这里进行配置项目的设置或您自己任意的配置初始化行为 。

```
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
        DiskMirrorConfig.putOption(WebConf.PROTOCOL_PREFIX, "https://xxx/");
        // 设置后端的允许跨域的所有主机
        ALL_HOST = new String[]{
                "https://www.lingyuzhao.top",
                "https://www.lingyuzhao.top/",
                "*"
        };
        DiskMirrorConfig.putOption(WebConf.ALL_HOST_CONTROL, JSONArray.from(ALL_HOST));
        // 设置访问 diskMirror 时的密钥，这个密钥可以是数值也可以是字符串类型的对象，最终会根据特有的计算算法获取到一个数值
        // 获取到的数值会再后端服务运行的时候展示再日志中，前端的 diskMirror 的 js 文件中需要需要将这个数值做为key 才可以进行访问
        DiskMirrorConfig.putOption(WebConf.SECURE_KEY, 0);
        // 设置后端的IO模式 请确保这个是最后一个配置项目 因为在配置了此项目之后 就会构建适配器
        DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
        // 您还可以类似下面这样 显式的设置某个空间的磁盘配额 能让此用户空间不受到磁盘配额限制 这里是让 25 号空间不受限制 根据这里的配置来进行操作
        // WEB_CONF.setSpaceMaxSize("25", 256 << 10 << 10);
    }

```

在 2024.02.21 版本中，针对配置的修改操作，移动到了一个函数中，这是为了避免发生各种由于静态代码块导致的错误，且提高了配置的灵活性，因此在2024.02.21 版本 以及 之后的版本中 可以找到下面的函数进行配置的修改动作。

```
    /**
     * 加载配置 在 loadConf 函数中我们可以指定一些配置 用于初始化适配器，此函数会在 DiskMirrorConfig 实例化的时候调用，此函数可以进行重写，或者进行修改。
     * <p>
     * In the loadConf function, we can specify some configurations to initialize the adapter. This function will be called during the instantiation of DiskMirrorConfig, and can be rewritten or modified.
     */
    public static void loadConf() {
        // 配置需要被 盘镜 管理的路径 此路径也应该可以被 web 后端服务器访问到
        DiskMirrorConfig.putOption(WebConf.ROOT_DIR, "/DiskMirror/data");
        // 配置一切需要被盘镜处理的数据的编码
        DiskMirrorConfig.putOption(WebConf.DATA_TEXT_CHARSET, "UTF-8");
        // 设置每个空间中每种类型的文件存储最大字节数
        DiskMirrorConfig.putOption(WebConf.USER_DISK_MIRROR_SPACE_QUOTA, 128 << 10 << 10);
        // 设置协议前缀 需要确保你的服务器可以访问到这里！！！
        DiskMirrorConfig.putOption(WebConf.PROTOCOL_PREFIX, "https://xxx/");
        // 设置后端的允许跨域的所有主机
        DiskMirrorConfig.putOption(WebConf.ALL_HOST_CONTROL, JSONArray.from(
                new String[]{
                        "*"
                }
        ));

        // 设置访问 diskMirror 时的密钥，这个密钥可以是数值也可以是字符串类型的对象，最终会根据特有的计算算法获取到一个数值
        // 获取到的数值会再后端服务运行的时候展示再日志中，前端的 diskMirror 的 js 文件中需要需要将这个数值做为key 才可以进行访问
        DiskMirrorConfig.putOption(WebConf.SECURE_KEY, 0");
        // 显式的设置某个空间的磁盘配额 能让此用户空间不受到磁盘配额限制 这里是让 25 号空间不受限制 根据这里的配置来进行操作
        DiskMirrorConfig.WEB_CONF.setSpaceMaxSize("25", 256 << 10 << 10);

        // 设置后端的IO模式 请确保这个是最后一个配置项目 因为在配置了此项目之后 就会构建适配器
        DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
    }
```

当然 您如果不希望修改代码，也可以直接在 `top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorInit` 初始化类中的 `createServletApplicationContext`
方法内设置一些额外配置。

```
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        // 初始化容器对象
        final AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        // 初始化配置 这里的函数可以传递形参 您可以在形参中指定一个 webConf 对象 这里是 null 代表不需要进行额外配置
        // 形参中的 webConf 中的配置将会覆盖原有的内置配置数据！
        DiskMirrorConfig.loadConf(null);
        // 将配置类注册到容器对象中
        webApplicationContext.register(DiskMirrorConfig.class);
        // 返回容器对象
        return webApplicationContext;
    }
```

#### 二开配置 - 从 maven 获取到后端软件包 并使用 SpringBoot 进行二次开发

您可以在这里查阅奥如何使用 SpringBoot 进行二次开发的方式配置 diskMirror 以及 将程序启动与部署

##### 导入 maven 依赖

```
    <!--  设置 SpringBoot3 做为父项目 所有的 SpringBoot 项目都应该以此为父项目  -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.5</version>
    </parent>

    <dependencies>

        <!-- 在 SpringBoot 项目中 有着许多已经实现好的 starter -->
        <!-- 在这里我们引用的就是 web 中的 starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- diskMirror 的 starter 依赖 帮助您获取到适配器-->
        <dependency>
            <groupId>io.github.BeardedManZhao</groupId>
            <artifactId>diskMirror-spring-boot-starter</artifactId>
            <version>1.0.1</version>
        </dependency>

        <!-- diskMirror 的后端服务器依赖 会自动的使用 diskMirror starter 获取到的适配器 -->
        <dependency>
            <groupId>io.github.BeardedManZhao</groupId>
            <artifactId>DiskMirrorBackEnd</artifactId>
            <version>2024.02.21</version>
        </dependency>

        <!-- diskMirror 后端服务器需要的依赖 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
        </dependency>

    </dependencies>

```
##### 开发控制器

这个控制器只需要直接集成 FsCurd 并使用自动配置为 控制器赋予适配器对象即可!

```java
package top.lingyuzhao.diskMirror.backEnd.springController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.lingyuzhao.diskMirror.core.Adapter;

/**
 * 控制器 此控制器直接继承了后端版本的控制器 能够直接使用!
 *
 * @author zhao
 */
@Controller
@RequestMapping(
        value = {"FsCrud"},
        produces = {"text/html;charset=UTF-8"},
        method = {RequestMethod.POST}
)
public class FsCrud extends top.lingyuzhao.diskMirror.backEnd.core.controller.FsCrud {

    @Autowired
    public FsCrud(Adapter adapter) {
        super(adapter);
    }
}

```

##### 开发启动类

```java
package top.lingyuzhao.diskMirror.backEnd.springConf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * diskMirror 的后端服务器主类
 * @author zhao
 */
@SpringBootApplication(scanBasePackages = "top.lingyuzhao.diskMirror.backEnd.springController")
public class DiskMirrorMAIN {
    public final static Logger logger = LoggerFactory.getLogger(DiskMirrorMAIN.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext run = SpringApplication.run(DiskMirrorMAIN.class);
    }
}

```

#### 打包之后直接进行部署

您可以直接将源码打包成 war 包，然后直接将 war 包放入您的服务器中，然后运行您的web容器即可，例如 tomcat 等，这些操作结束之后 您需要访问您的盘镜 web 页面，页面的地址根据您的部署方式决定。 如果访问到了 web
页面则代表您的部署成功了，如果没有访问到 web 页面则代表您的部署失败了，您可以联系我，我的邮箱是：liming7887@qq.com。

##### TomCat 启用 目录映射

由于我们的盘镜后端是涉及文件上传的，并且读取是使用的 url，所以我们需要确保 TomCat 可以访问我们的目录，您可以按照下面的方式配置`server.xml`。

```xml

<Context docBase="存储文件的真实路径（一般就是 WebConf.ROOT_DIR 对应的值）" path="存储文件的访问路径（一般就是 WebConf.PROTOCOL_PREFIX 对应的值的路径部分）"
    reloadable="true" />
```

##### TomCat 设置 allowCasualMultipartParsing

因为要上传文件，所以在这里我们要确保 服务器 是可以处理 part 的，以及url，因此可以在 `context.xml` 里面这样配置

```xml
<!-- TODO 在这里设置 allowCasualMultipartParsing = true 即可成功 -->
<Context allowCasualMultipartParsing="true">

    <!-- Default set of monitored resources. If one of these changes, the    -->
    <!-- web application will be reloaded.                                   -->
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <WatchedResource>WEB-INF/tomcat-web.xml</WatchedResource>
    <WatchedResource>${catalina.base}/conf/web.xml</WatchedResource>
</Context>
```

### 我如何使用 盘镜

在这里您需要获取到我们在盘镜系统中的 [diskMirror.js](https://github.com/BeardedManZhao/DiskMirrorBackEnd/blob/main/web/js/diskMirror.js)
文件 装载到前端项目中，然后在前端的 JS 中进行函数的调用即可，接下来我们将演示一些操作。

#### 实例化盘镜

在盘镜实例化的之前 您需要将 axios 这个类引入到您的项目中，然后在实例化盘镜的时候 传入 盘镜的后端服务器地址。
如果您的实例化操作需要多次进行，也不用担心，盘镜整体是一个非常轻量级的框架，其实例化的时间是非常短的，所以您可以多次实例化盘镜，只需要传入不同的配置即可。

```js
    // 实例化 盘镜 在这里指向盘镜的后端服务器网址 就是可以直接访问到后端服务器页面的网址
const diskMirror = new DiskMirror("https://xxx.xxx.xxx");
// 设置密钥 这个密钥需要与后端服务器的一致，让后端服务器信任您的身份
diskMirror.setSk(123123)
```

#### 上传文件数据

我们在这里提供了一个 HTML 代码 演示了如何上传文件

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>diskMirror Text</title>
</head>

<body>
<div id="show">
    <input id="fileInput" type="file">
</div>
<img alt="" id="show_img" src="#">
</body>
<!-- 您需要将 axios 和 盘镜 引入到您的项目中 -->
<script rel="script" src="xx/xxx/axios.min.js" type="text/javascript"></script>
<script rel="script" src="xx/xxx/diskMirror.js" type="text/javascript"></script>
<script>
    // 实例化 盘镜 在这里指向盘镜的后端服务器
    const diskMirror = new DiskMirror("http://xxx.xxx.xxx");
    document.getElementById("fileInput").onchange = () => {
        // 获取到文件数据
        const file = document.getElementById("fileInput").files[0];
        // 上传文件
        diskMirror.upload(
                // 这个 json 就是请求参数的设置
                {
                    // 设置要上传的文件名称，这个名称就是在后端中的文件名称
                    fileName: file.name,
                    // 设置要上传的文件所属空间
                    userId: 1024,
                    // 设置要上传的文件类型
                    type: 'TEXT'
                },
                // 设置要上传的文件对象
                file,
                // 设置上传成功之后的回调函数
                (res) => {
                    // 在这里可以设置上传成功之后的回调函数
                    // 如果上传成功 就在这里将被上传的文件数据的 url 显示
                    document.getElementById("show").innerText = JSON.stringify(res);
                    // 假设我们要上传的是图片 希望可以在上传之后将图片显示出来 可以在这里接收返回的 url
                    document.getElementById("show_img").src = res.url
                },
                // 设置上传失败之后的回调函数
                (res) => alert(res)
        )
    }

</script>

</html>
```

### 获取文件 url

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>测试</title>
</head>

<body>
<div id="show">

</div>
</body>
<script rel="script" src="xx/xx/axios.min.js" type="text/javascript"></script>
<script rel="script" src="xx/xxx/diskMirror.js" type="text/javascript"></script>
<script>
    // 实例化 盘镜 在这里指向盘镜的后端服务器
    const diskMirror = new DiskMirror("http://xxx.xxx.xxx");
    // 查询出文件数据 参数分别是 文件所属空间 类型 以及回调函数 在这里您同样可以传递两个回调，第一个是成功之后的回调，第二个是失败之后的回调，在这里我们只传递了一个函数 这是为了方便观察代码
    diskMirror.getUrls(1024, 'TEXT', (res) => {
        // 如果查询到数据 就在这里将所有的 文件数据的 url 显示
        document.getElementById("show").innerText = JSON.stringify(res);
    })
</script>

</html>
```

### 删除文件

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>测试</title>
</head>

<body>
<div id="show">

</div>
</body>
<script rel="script" src="xx/xx/axios.min.js" type="text/javascript"></script>
<script rel="script" src="xx/xxx/diskMirror.js" type="text/javascript"></script>
<script>
    // 实例化 盘镜 在这里指向盘镜的后端服务器
    const diskMirror = new DiskMirror("http://xxx.xxx.xxx");
    // 删除文件的操作和获取文件 url 的操作很接近，只是在 文件类型的后面 多了文件名称的形参
    diskMirror.remove(1024, 'TEXT', 'fileName.txt', (res) => {
        // 如果查询到数据 就在这里将所有的 文件数据的 url 显示
        document.getElementById("show").innerText = JSON.stringify(res);
    })
</script>

</html>
```

----

- diskMirror starter SpringBoot：https://github.com/BeardedManZhao/diskMirror-spring-boot-starter.git
- diskMirror 后端服务器MVC版本：https://github.com/BeardedManZhao/DiskMirrorBackEnd.git
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git

[//]: # (- diskMirror SpringBoot 版本：https://github.com/BeardedManZhao/diskMirror-web-spring-boot.git)