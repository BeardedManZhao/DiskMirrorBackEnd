<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>删除文件</title>
</head>
<body>
<form action="" method="post" enctype="multipart/form-data">
    <label>
        <input type="text" placeholder="输入要删除的文件名称" id="fileName">
    </label>
    <label>
        <input type="number" placeholder="输入要操作的空间id" id="userId">
    </label>
    <label>
        <input type="text" placeholder="是否为二进制数据?[Y/n]" id="fileType">
    </label>
    <input id="buttonInput" type="button" value="开始删除" onclick="rm()"/>
</form>

<div id="show_res">

</div>
</body>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/utils.js"></script>
<script rel="script" type="text/javascript" src="http://www.lingyuzhao.top/js/lib/axios.min.js"></script>
<script rel="script" type="text/javascript"
        src="http://diskmirror.lingyuzhao.top/webJavaScript//diskMirror.js"></script>
<script>
    // 实例化盘镜
    const diskMirror = new DiskMirror("http://diskMirror.lingyuzhao.top/DiskMirrorBackEnd");

    function rm() {
        console.info("rm() run!!!")
        diskMirror.remove(
            parseInt(document.getElementById('userId').value),
            document.getElementById('fileType').value === 'y' || document.getElementById('fileType').value === 'Y' ? 'Binary' : 'TEXT',
            document.getElementById('fileName').value,
            (res) => {
                document.getElementById('show_res').innerText = '删除结果:' + formatJson(res);
            },
            (err) => console.log(err)
        )
    }
</script>
</html>