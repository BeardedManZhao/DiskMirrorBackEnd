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
    <input type="submit" value="上传"/>
</form>
</body>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/lib/axios.min.js"></script>
<script>
    const elementsByName0 = document.getElementsByName("userId")[0];
    const elementsByName1 = document.getElementsByName("file")[0];

    document.querySelector('form').addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData();
        // 设置请求参数
        formData.append('params', JSON.stringify({
            fileName: elementsByName1.files[0].name,
            userId: parseInt(elementsByName0.value),
            type: 'TEXT'
        }))
        // 设置文件
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
            alert(JSON.stringify(res.data))
        }).catch(function (err) {
            console.log(err);
        });
    });
</script>
</html>
