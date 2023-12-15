<%--
  Created by IntelliJ IDEA.
  User: zhao
  Date: 2023/12/15
  Time: 13:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传文件</title>
</head>
<body>
<form action="#" method="post" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <label>
        <input type="number" placeholder="输入空间id" name="userId">
    </label>
    <label>
        <input type="text" placeholder="是否为二进制数据?[Y/n]" name="fileType">
    </label>
    <input type="submit" value="上传"/>
</form>
<div id="res_show">文件上传成功之后，返回的数据会在这里显示</div>
<a id="download_a"></a>
</body>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/lib/axios.min.js"></script>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/utils.js"></script>
<script>
    const elementsByName0 = document.getElementsByName("userId")[0];
    const elementsByName1 = document.getElementsByName("file")[0];
    const elementsByName2 = document.getElementsByName("fileType")[0];


    document.querySelector('form').addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData();
        // 设置请求参数数据包
        formData.append('params', JSON.stringify({
            // 文件名字
            fileName: elementsByName1.files[0].name,
            // 空间id
            userId: parseInt(elementsByName0.value),
            // 文件类型
            type: elementsByName2.value === 'Y' || elementsByName2.value === 'y' ? 'Binary' : 'TEXT'
        }))
        // 设置文件数据包
        formData.append('file', elementsByName1.files[0])
        // 开始进行请求发送
        axios(
            {
                method: 'post',
                url: '/DiskMirrorBackEnd/FsCrud/add',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            // 打印结果
            document.getElementById('res_show').innerText = formatJson(res.data)
            // 设置 url
            const elementById = document.getElementById('download_a');
            elementById.href = res.data.url;
            elementById.innerText = '获取文件数据';
        }).catch(function (err) {
            // 处理错误
            console.log(err);
        });
    });
</script>
</html>
