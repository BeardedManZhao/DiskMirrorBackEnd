# ![image](https://github.com/BeardedManZhao/DiskMirror/assets/113756063/b8a15b22-5ca0-4552-aab2-7131c63dc727) DiskMirror

用于进行磁盘文件管理的一面镜子，其包含许多的适配器，能够将任何类型的文件数据流中的数据接入到管理中，并将保存之后的 url
返回，支持不同文件所属空间的管控，您还可以通过此API 获取到指定 userid 下面的所有文件的
url，在诸多场景中可以简化IO相关的实现操作，能够降低开发量，例如web服务器中的磁盘管理操作!

## 什么是适配器

适配器在这里是用于进行文件传输的桥梁，能够让您将自己的数据源（例如您的后端服务器）与指定的数据终端（例如您的各类文件系统）进行连接，将数据提供给数据终端，减少了您手动开发IO代码的时间。

在未来，我们将会提供更多的适配器选项，让适配器的数据终端具有更多的支持。

### 我如何部署 盘镜 后端系统

本软件支持两种方式进行配置，第一种就是直接下载后端服务器的源码，并进行修改和打包部署，第二种就是直接在 SpringBoot
中进行二次开发，期间进行配置

#### 新版本 - 直接启动 - 自动生成配置文件

首先我们需要设置一下 `DiskMirror_CONF` 的环境变量，其指向一个文件，这个文件就是 diskMirror 的配置文件了！

从 2024-08-30 之后发布的版本中，在我们的程序启动时，会尝试从 `DiskMirror_CONF`
对应的环境变量读取，这个代表的就是配置文件的存储路径，如果没有读取到会自动生成一份默认的配置！
然后只需要修改配置文件就可以了，不再需要修改代码了！

新版本的设置是非常简单的！

关于配置文件的描述和信息 如下所示

> 所有关于`DiskMirror所需要的配置`
>
的配置项目，我们可以在 [diskMirror-spring-boot-starter的配置文件中查看到](https://github.com/BeardedManZhao/diskMirror-spring-boot-starter#%E9%85%8D%E7%BD%AE-starter)

| 项目名称                         | 配置值类型     | 解释                                                                          |
|------------------------------|-----------|-----------------------------------------------------------------------------|
| root.dir                     | String    | DiskMirror所需要的配置                                                            |
| fs.defaultFS                 | String    | DiskMirror所需要的配置                                                            |
| ok.value                     | String    | DiskMirror所需要的配置                                                            |
| resK                         | String    | DiskMirror所需要的配置 res-key                                                    |
| protocol.prefix              | String    | DiskMirror所需要的配置                                                            |
| user.disk.mirror.space.quota | long      | DiskMirror所需要的配置                                                            |
| params                       | json      | DiskMirror所需要的配置                                                            |
| secure.key                   | int       | DiskMirror所需要的配置                                                            |
| diskMirror.charset           | String    | DiskMirror所需要的配置                                                            |
| max.in.memory.size           | int       | 盘镜后端 设置接收数据时 可在内存中存储的数据容量 超出则会临时存在磁盘中                                       |
| max.upload.size              | long      | 盘镜后端 设置接收数据的最大大小，单位是字节。-1 代表无限                                              |
| data.text.charset            | String    | 文本数据处理时需要使用的编码集                                                             |
| all.host.control             | jsonArray | 本服务器运行时的跨域允许列表                                                              |
| verification.list            | jsonArray | 盘镜后端 设置需要被装载的模块列表 `SkCheckModule$writer` 代表的就是在写操作模式下使用 `SkCheckModule` 模块。 |
| diskMirror.mode              | String    | DiskMirror使用的适配器类型                                                          |
| SpaceMaxSize                 | json      | config中对于每个用户空间数据容量的配置项目                                                    |

```json
{
  "root.dir": "/DiskMirror/data",
  "fs.defaultFS": "hdfs://127.0.0.1:8020",
  "ok.value": "ok!!!!",
  "resK": "res",
  "protocol.prefix": "",
  "user.disk.mirror.space.quota": 134217728,
  "params": {},
  "secure.key": 1001101010,
  "diskMirror.charset": "UTF-8",
  "data.text.charset": "UTF-8",
  "all.host.control": [
    "https://www.lingyuzhao.top",
    "https://www.lingyuzhao.top/"
  ],
  "verification.list":[
    "SkCheckModule$writer"
  ],
  "diskMirror.mode": "LocalFSAdapter",
  "SpaceMaxSize": {
    "1": 1342177280
  }
}

```

#### 旧版本 - 源码配置 - 下载并修改源码

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

在 2024.02.21 版本中，针对配置的修改操作，移动到了一个函数中，这是为了避免发生各种由于静态代码块导致的错误，且提高了配置的灵活性，因此在2024.02.21
版本 以及 之后的版本中 可以找到下面的函数进行配置的修改动作。

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

当然 您如果不希望修改代码，也可以直接在 `top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorInit`
初始化类中的 `createServletApplicationContext`
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

#### 二开配置 - 从 maven 获取到后端软件包 并使用 SpringBoot 进行二次开发 - 基于 javax 的 servlet

您可以在这里查阅如何使用 SpringBoot 进行二次开发的方式配置 diskMirror 以及 将程序启动与部署

##### 导入 maven 依赖

```
    <!--  设置 SpringBoot 做为父项目 所有的 SpringBoot 项目都应该以此为父项目  -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>x.x.x</version>
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

您可以直接将源码打包成 war 包，然后直接将 war 包放入您的服务器中，然后运行您的web容器即可，例如 tomcat 等，这些操作结束之后
您需要访问您的盘镜 web 页面，页面的地址根据您的部署方式决定。 如果访问到了 web
页面则代表您的部署成功了，如果没有访问到 web 页面则代表您的部署失败了，您可以联系我，我的邮箱是：liming7887@qq.com。

##### TomCat 启用 目录映射（二选一）

由于我们的盘镜后端是涉及文件上传的，肯定是支持文件下载的，盘镜中的文件上传和下载支持两种方式，第一种就是直接读取 url
这种操作就是通过普通的 url 映射来下载文件，所以我们需要确保 TomCat
可以访问我们的目录，您可以按照下面的方式配置`server.xml`。

```xml

<Context docBase="存储文件的真实路径（一般就是 WebConf.ROOT_DIR 对应的值）"
    path="存储文件的访问路径（一般就是 WebConf.PROTOCOL_PREFIX 对应的值的路径部分）" reloadable="true" />
```

##### TomCat 不启用目录映射 使用 JS 下载（二选一）

在 2024.03.25 之后的项目中，我们使用了 diskMirror 1.1.4 版本，在 JS 文件中可以看到 内置实现了 `download` 函数，因此您可以直接使用
JS 下载文件，而不通过服务器容器映射！

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

### 关于 DiskMirror 的日志

在 jar 包中有自动包含日志配置，它的日志文件输出位置是在 `./logs` 也就是说，启动Web容器的位置非常重要，它会在您启动
Web容器的目录中创建一个 logs 目录，并将日志输出到此目录中！
也可以在控制台中查看到对应的日志输出！

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
// 第二个参数是代表的域，也就是您的盘镜后端服务在哪个域名下！这会做为一个密钥验证！
diskMirror.setSk(123123, 'xxx.com')
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

## webSocket 操作

```javascript
// 实例化 diskMirror 的 JS Socket 客户端 值得注意的是 webSocket 和 http 不一样 是长连接 因此 url 在这里要提供全！
const diskMirrorWebSocket = new DiskMirrorWebSocket("wss://xxx.xxx.xxx/socketFsCrud");

// 设置当服务器返回数据的时候的操作 （如果不设置也会有默认的）
diskMirrorWebSocket.setCommandHandler("getUseSize", (data) => console.log(data), (data) => console.error(data))

// 设置 sk
diskMirrorWebSocket.setSk(651682360);

// 定时器是确保 diskMirrorWebSocket 已经连接上服务器
setTimeout(() => {
    // 客户端可以主动推送操作给服务器 这个操作将会主动让服务器返回数据
    // 删除一个文件
    diskMirrorWebSocket.sendCommand("remove", {
        userId: 4,
        type: "Binary",
        fileName: "/img.png"
    });
    // 重命名一个文件
    diskMirrorWebSocket.sendCommand("reName", {
        userId: 4,
        type: "Binary",
        oldName: "she.sh",
        fileName: "she1.sh"
    });
    // 获取到文件目录树结构
    diskMirrorWebSocket.sendCommand("getUrls", {
        userId: 4,
        type: "Binary"
    });
    // 将一个 url 中的文件数据转存下载到盘镜
    diskMirrorWebSocket.sendCommand("transferDeposit", {
        userId: 4,
        type: "Binary",
        fileName: "textIndexCm.html",
        url: "https://www.lingyuzhao.top"
    });
    // 获取到所有进度条
    diskMirrorWebSocket.sendCommand("getAllProgressBar", {
        userId: 4,
        type: "Binary"
    });
    // 获取到所有处于转存状态的文件信息
    diskMirrorWebSocket.sendCommand("transferDepositStatus", {
        userId: 4,
        type: "Binary"
    });
    // 创建一个文件夹
    diskMirrorWebSocket.sendCommand("mkdirs", {
        userId: 4,
        type: "Binary",
        fileName: "/newnewenw"
    });
    // 获取到当前用户最大空间
    diskMirrorWebSocket.sendCommand("getSpaceSize", {
        userId: 4,
        type: "Binary"
    });
    // 获取到当前用户已使用空间
    diskMirrorWebSocket.sendCommand("getUseSize", {
        userId: 4,
        type: "Binary"
    })
}, 2000);
```

## 更新记录与信息（从 2024-03-25 之后开始记录）

### 2024-12-04 版本开发

- 修复 download 函数有时候匹配不上的问题

### 2024-12-03 版本发布

- 为 DiskMirror 核心组件版本升级到 1.3.5
- 为 DiskMirror 增加 `verification.list` 配置项
- 为 DiskMirror 增加 webSocket 的调用支持！【测试阶段 - 不一定稳定】
- 修正配置文件 日志目录的问题

### 2024-11-05 版本发布

- 为 DiskMirror 核心组件版本升级到 1.3.2
- 下载文件时，对于文件大小的显示做了优化，如果被下载的文件数据流中无文件字节数显示，则不会强制设置为 0，这能够让文件的下载模块支持多种适配器！

### 2024-08-31 稳定版本

- 新增了日志功能
- 对于表单上传文件，我们使用了一些临时落盘的手段来避免表单过大导致内存溢出
- 提供了两个新的设置项目 `max.in.memory.size` 和 `max.upload.size`
- 移除了 `WebConf.LOGGER.info("download = {}", fileName);` 减少冗余日志产生！
- 重新为 JS 添加 `getAllProgressBar` 操作!

### 2024-08-30 版本

- 重构了 diskMirror 服务器的配置加载方式，不需要修改源码，可直接通过配置文件来操作！

### 2024-06-13 版本

- 针对 `diskMirror.js` 进行优化，使得其支持直接写字符串数据！
- 针对 `getAllProgressBar` 的操作提供了支持！

### 2024-04-12 版本

- 下载接口中提供了数据流长度的元数据
- 新增了对于 `transferDeposit` 的支持
- 对于 `getVersion` 函数进行优化，使得其在各种环境下都可以准确的反映出当前的版本号！
- 将 `FsCrud` 中的适配器对象对于其子类的权限打开，这有益于重写和拓展！
- 对于 `DiskMirrorConfig.getVersion` 函数进行优化，使得其能够接收来自外界的配置类并计算到正确的版本号！
- 新增 ` String getUseSize(HttpServletRequest httpServletRequest);` 函数，其可以直接获取到一个 diskMirror 空间的已使用的空间大小！
- 优化 JS 脚本中的cookie证书机制

### 2024-03-26 版本

- 针对 `downLoad` 服务进行优化，使得其能够接收来自 Java API 的请求！
- 更新 JS 函数，减少其针对 cookie 的操作次数！

## 更多

----

- diskMirror starter SpringBoot：https://github.com/BeardedManZhao/diskMirror-spring-boot-starter.git
- diskMirror 后端服务器版本（MVC）：https://github.com/BeardedManZhao/DiskMirrorBackEnd.git
- diskMirror 后端服务器版本（SpringBoot）：https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot.git
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git
