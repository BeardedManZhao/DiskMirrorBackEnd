<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig" %><%--
  Created by IntelliJ IDEA.
  User: zhao
  Date: 2023/12/15
  Time: 13:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>盘镜后端服务</title>
    <!-- 窗口标签页的 logo  ./image/logo.svg 也是盘镜的 logo -->
    <link rel="icon" href="./image/logo.svg">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        div {
            text-align: center;
        }
    </style>
</head>
<body>
<script src="js/diskMirror.min.js"></script>
<div>
    <pre id="res_show"><%=DiskMirrorConfig.getVersion()%></pre>
</div>
<div>
    <button onclick="window.open('./js/diskMirror.js')">启动成功，点击获取 JS 文件</button>
</div>
</body>
</html>