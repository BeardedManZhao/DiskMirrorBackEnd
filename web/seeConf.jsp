<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig" %>
<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig" %><%--
  Created by IntelliJ IDEA.
  User: zhao
  Date: 2023/12/15
  Time: 19:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>盘镜后端配置</title>
</head>
<body>
<div id="conf_show_div"></div>
</body>
<script rel="script" type="text/javascript" src="https://www.lingyuzhao.top/js/utils.js"></script>
<script rel="script" type="text/javascript" src="./js/diskMirror.js"></script>
<script>
    document.getElementById('conf_show_div').innerText = formatJson('<%=DiskMirrorConfig.WEB_CONF.toString()%>')
</script>
</html>
