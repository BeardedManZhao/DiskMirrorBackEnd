<%@ page import="top.lingyuzhao.diskMirror.backEnd.conf.SpringConfig" %>
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
<div>
    <button onclick="window.open('<%=SpringConfig.getOptionString(WebConf.PROTOCOL_PREFIX)%>')">前往盘镜文件系统</button>
    <button onclick="window.open('seeConf.jsp')">查看盘镜配置</button>
    <button onclick="window.open('upload.jsp')">上传</button>
    <button onclick="getUrls(prompt('输入您要查询的空间id', '1024'), prompt('输入您要查询的文件类型', 'Binary/TEXT'))">查询</button>
    <button onclick="remove(prompt('输入您要删除的文件所在空间id', '1024'), prompt('输入您要删除的文件类型', 'Binary/TEXT'), prompt('输入您要删除的文件名称', 'test.txt'))">
        删除
    </button>
</div>
</body>
</html>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/lib/axios.min.js"></script>
<script>
    /**
     * 获取到指定空间的所有 文本文件的 url
     * @param userId  需要被读取的空间id
     * @param type {'TEXT'|'Binary'}
     */
    function getUrls(userId, type) {
        if (userId === null || userId === '' || type == null || type === '') {
            return
        }
        const formData = new FormData();
        // 设置请求参数
        formData.append('params', JSON.stringify({
            userId: parseInt(userId),
            type: type
        }))
        // 开始进行请求发送
        axios(
            {
                method: 'post',
                url: '/DiskMirrorBackEnd/FsCrud/getUrls',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            alert(JSON.stringify(res.data))
        }).catch(function (err) {
            console.log(err);
        });
    }

    /**
     * 删除指定空间的指定文件
     * @param userId 空间id
     * @param type 文件类型
     * @param fileName 需要被删除的文件名称
     */
    function remove(userId, type, fileName) {
        if (userId === null || userId === '' || type == null || type === '' || fileName === null || fileName === '') {
            return
        }
        const formData = new FormData();
        // 设置请求参数
        formData.append('params', JSON.stringify({
            fileName: fileName,
            userId: parseInt(userId),
            type: type
        }))
        // 开始进行请求发送
        axios(
            {
                method: 'post',
                url: '/DiskMirrorBackEnd/FsCrud/remove',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            alert(JSON.stringify(res.data))
        }).catch(function (err) {
            console.log(err);
        });
    }
</script>