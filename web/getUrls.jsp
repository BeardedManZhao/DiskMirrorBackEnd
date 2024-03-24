<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>获取到文件URL</title>
</head>
<body>
<form action="" method="post" enctype="multipart/form-data">
    <label>
        <input type="number" placeholder="输入您要查询的空间id" id="userId">
    </label>
    <label>
        <input type="text" placeholder="是否查询二进制数据?[Y/n]" id="fileType">
    </label>
    <input id="buttonInput" type="button" value="查询" onclick="query()"/>
</form>

<div id="show_res">

</div>

</body>
<script rel="script" type="text/javascript" src="https://www.lingyuzhao.top/js/utils.js"></script>
<script rel="script" type="text/javascript" src="https://www.lingyuzhao.top/js/lib/axios.min.js"></script>
<script rel="script" type="text/javascript"
        src="./js/diskMirror.js"></script>
<script>
    // 实例化盘镜
    const diskMirror = new DiskMirror("http://localhost:8080/DiskMirrorBackEnd");

    function query() {
        console.info("query() run!!!")
        diskMirror.getUrls(
            parseInt(document.getElementById('userId').value),
            document.getElementById('fileType').value === 'y' || document.getElementById('fileType').value === 'Y' ? 'Binary' : 'TEXT',
            (res) => {
                document.getElementById('show_res').innerText = '查询结果:' + formatJson(res);
            },
            (err) => console.log(err)
        );
    }

</script>
</html>