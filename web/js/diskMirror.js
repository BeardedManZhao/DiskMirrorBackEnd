/**
 * 盘镜 JS 脚本类 您可以通过此脚本来实现盘镜相关的操作调用
 * TODO 此脚本需要先引入依赖 axios
 */
class DiskMirror {

    /**
     * 获取到盘镜对象
     * @param url {string} 盘镜后端服务器后端的url
     */
    constructor(url) {
        // constructor body
        this.diskMirrorUrl = url; // This is the diskMirrorUrl variable
        this.setController('/FsCrud')
    }

    /**
     * 设置本组件要使用的盘镜控制器
     * @param controllerName 盘镜控制器名称
     */
    setController(controllerName) {
        this.controller = controllerName;
    }

    /**
     *
     * @return {string} 本组件的盘镜控制器名称
     */
    getController() {
        return this.controller;
    }

    /**
     * 向后端中上传一个文件
     * @param params {{
     *      fileName: string,
     *      userId: int,
     *      type: 'Binary'|'TEXT'
     * }} 这里是请求参数对象 其中的文件名字代表上传到后端之后的文件名字，userId 代表的就是文件要上传到的指定空间的id；type就是代表的文件的类型 支持二进制和文本两种格式
     * @param file {File} 需要被上传的文件对象
     * @param okFun {function} 操作成功之后的回调函数 输入是被上传文件的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     */
    upload(params, file, okFun = undefined, errorFun = undefined) {
        const formData = new FormData();
        // 设置请求参数数据包
        formData.append('params', JSON.stringify(params))
        // 设置文件数据包
        formData.append('file', elementsByName1.files[0])
        // 开始进行请求发送
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/add',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            // 处理成功
            if (okFun !== undefined) {
                okFun(res.data)
            }
        }).catch(function (err) {
            // 处理错误
            if (errorFun !== undefined) {
                errorFun(err)
            }
        });
    }

    /**
     * 获取所有URL
     * @returns {Array} - 所有URL的数组
     */
    getUrls() {
        // getUrls function body
    }

    /**
     * 移除URL
     * @param {string} url - 要移除的URL
     */
    remove(url) {
        // remove function body
    }
}