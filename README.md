# ![image](https://github.com/BeardedManZhao/DiskMirror/assets/113756063/b8a15b22-5ca0-4552-aab2-7131c63dc727) DiskMirror

用于进行磁盘文件管理的一面镜子，其包含许多的适配器，能够将任何类型的文件数据流中的数据接入到管理中，并将保存之后的 url 返回，支持不同文件所属空间的管控，您还可以通过此API 获取到指定 userid 下面的所有文件的
url，在诸多场景中可以简化IO相关的实现操作，能够降低开发量，例如web服务器中的磁盘管理操作!

## 什么是适配器

适配器在这里是用于进行文件传输的桥梁，能够让您将自己的数据源（例如您的后端服务器）与指定的数据终端（例如您的各类文件系统）进行连接，将数据提供给数据终端，减少了您手动开发IO代码的时间。

在未来，我们将会提供更多的适配器选项，让适配器的数据终端具有更多的支持。

### 我如何部署 盘镜 后端系统

#### 下载并修改源码

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
        // 配置后端的IO模式 在这里我们使用的是本地适配器 您可以选择其它适配器或自定义适配器
        DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
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
        DiskMirrorConfig.WEB_CONF.setSecureKey("diskMirror-BackEnd");
        // 设置后端的IO模式 请确保这个是最后一个配置项目 因为在配置了此项目之后 就会构建适配器
        DiskMirrorConfig.putOption(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
    }
```

#### 打包之后直接进行部署

您可以直接将源码打包成 war 包，然后直接将 war 包放入您的服务器中，然后运行您的web容器即可，例如 tomcat 等，这些操作结束之后 您需要访问您的盘镜 web 页面，页面的地址根据您的部署方式决定。 如果访问到了 web
页面则代表您的部署成功了，如果没有访问到 web 页面则代表您的部署失败了，您可以联系我，我的邮箱是：liming7887@qq.com。

### 我如何使用 盘镜

在这里您需要获取到我们在盘镜系统中的 [diskMirror.js](https://github.com/BeardedManZhao/DiskMirrorBackEnd/blob/main/web/js/diskMirror.js)
文件 装载到前端项目中，然后在前端的 JS 中进行函数的调用即可，接下来我们将演示一些操作。

#### 实例化盘镜

在盘镜实例化的之前 您需要将 axios 这个类引入到您的项目中，然后在实例化盘镜的时候 传入 盘镜的后端服务器地址。
如果您的实例化操作需要多次进行，也不用担心，盘镜整体是一个非常轻量级的框架，其实例化的时间是非常短的，所以您可以多次实例化盘镜，只需要传入不同的配置即可。

```js
    // 实例化 盘镜 在这里指向盘镜的后端服务器
const diskMirror = new DiskMirror("http://xxx.xxx.xxx");
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

- diskMirror 后端服务器版本：https://github.com/BeardedManZhao/DiskMirrorBackEnd.git
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git
