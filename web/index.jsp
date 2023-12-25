<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig" %>
<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.WebConf" %><%--
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
<div id="res_show">

</div>
<div>
    <button onclick="window.open('http://www.lingyuzhao.top/js/diskMirror.js')">启动成功，点击获取 JS 文件</button>
</div>
</body>
</html>