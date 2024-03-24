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
        this.setSk()
        this.setController('/FsCrud')
    }

    /**
     * 通过异或算法加密
     * @param value {string} 要加密的字符串
     * @return {int} 加密后的结果
     */
    xorEncrypt(value) {
        let encrypted = 0;
        const sk_str = this.sk.toString();
        for (let i = 0; i < value.toString().length; i++) {
            encrypted ^= value.charCodeAt(i) ^ sk_str.charCodeAt(i % sk_str.length);
        }
        return encrypted;
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
     * 设置本组件使用的盘镜的 安全key
     * @param key {int} 此key 用于标识您的身份，让服务器相信您，且允许您访问，需要设置与服务器相同
     */
    setSk(key = 0) {
        this.sk = key;
    }

    /**
     * 获取本组件使用的盘镜的 安全key
     * @return {int} 安全密钥 如果目标 diskMirror 服务器需要密钥访问，则您需要在这里设置密钥
     */
    getSk() {
        return this.sk;
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
     * @param checkFun {function} 上传前的检查函数 输入是上传的文件对象的 json 数据 以及 文件对象本身，如果返回的是一个false 则代表不进行上传操作
     */
    upload(params, file, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined) {
        if (checkFun !== undefined && !checkFun(params, file)) {
            return;
        }
        const formData = new FormData();
        // 设置请求参数数据包
        params["secure.key"] = this.getSk();
        formData.append('params', JSON.stringify(params));
        // 设置文件数据包
        formData.append('file', file)
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
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
            if (res.data['res'] !== 'ok!!!!') {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            // 处理成功
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            // 处理错误
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 获取到指定空间的所有 文本文件的 url
     * @param userId {int} 需要被读取的空间id
     * @param type {'TEXT'|'Binary'} 文件类型
     * @param okFun {function} 操作成功之后的回调函数 输入是被获取额结果文件的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     * @param checkFun {function} 获取前的检查函数 输入是请求参数对象，如果返回的是一个false 则代表不进行获取操作
     */
    getUrls(userId, type, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined,) {
        // getUrls function body
        if (userId === undefined || type === undefined || type === '') {
            const err = "您必须要输入 userId 以及 type 参数才可以进行 url 的获取";
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
            return
        }
        const formData = new FormData();
        // 设置请求参数
        const params = {
            userId: userId,
            type: type,
            "secure.key": this.getSk()
        }
        if (checkFun !== undefined && !checkFun(params)) {
            return;
        }
        formData.append('params', JSON.stringify(params))
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/getUrls',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            if (res.data['res'] !== 'ok!!!!') {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 删除指定空间的指定文件
     * @param userId {int} 空间id
     * @param type {'TEXT'|'Binary'} 文件类型
     * @param fileName {string} 需要被删除的文件名称
     * @param okFun {function} 操作成功之后的回调函数 输入是被删除的文件的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     * @param checkFun {function} 删除前的检查函数 输入是请求参数对象，如果返回的是一个false 则代表不进行删除操作
     */
    remove(userId, type, fileName, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined,) {
        if (userId === undefined || type == null || type === '' || fileName === undefined || fileName === '') {
            const err = "您必须要输入 userId 以及 type 和 fileName 参数才可以进行删除";
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
            return
        }
        const formData = new FormData();
        // 设置请求参数
        const params = {
            fileName: fileName,
            userId: userId,
            type: type,
            "secure.key": this.getSk()
        }
        if (checkFun !== undefined && !checkFun(params)) {
            return;
        }
        formData.append('params', JSON.stringify(params))
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/remove',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            if (res.data['res'] !== 'ok!!!!') {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 针对指定空间的指定文件进行重命名
     * @param userId {int} 空间id
     * @param type {'TEXT'|'Binary'} 文件类型
     * @param fileName {string} 需要被变更的文件名称
     * @param newName {string} 变更之后的文件名称
     * @param okFun {function} 操作成功之后的回调函数 输入是被删除的文件的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     * @param checkFun {function} 操作前的检查函数 输入是请求参数对象，如果返回的是一个false 则代表检查失败不继续操作
     */
    reName(userId, type, fileName, newName, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined) {
        if (userId === undefined || type == null || type === '' || fileName === undefined || fileName === '' || newName === undefined || newName === '') {
            const err = "您必须要输入 userId 和 type 以及 fileName 和 newName 参数才可以进行重命名"
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
            return
        }
        const formData = new FormData();
        // 设置请求参数
        const params = {
            fileName: fileName,
            newName: newName,
            userId: userId,
            type: type,
            "secure.key": this.getSk()
        }
        if (checkFun !== undefined && !checkFun(params)) {
            return;
        }
        formData.append('params', JSON.stringify(params))
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/reName',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            if (res.data['res'] !== 'ok!!!!') {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 针对指定空间的指定文件目录进行创建
     * @param userId {int} 空间id
     * @param type {'TEXT'|'Binary'} 文件类型
     * @param fileName {string} 需要被创建的文件目录名称
     * @param okFun {function} 操作成功之后的回调函数 输入是被创建的文件目录的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     * @param checkFun {function} 操作前的检查函数 输入是请求参数对象，如果返回的是一个false 则代表检查失败不继续操作
     */
    mkdirs(userId, type, fileName, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined) {
        if (userId === undefined || type == null || type === '' || fileName === undefined || fileName === '') {
            const err = "您必须要输入 userId 和 type 以及 fileName 参数才可以进行文件目录的创建"
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
            return
        }
        const formData = new FormData();
        // 设置请求参数
        const params = {
            fileName: fileName,
            userId: userId,
            type: type,
            "secure.key": this.getSk()
        }
        if (checkFun !== undefined && !checkFun(params)) {
            return;
        }
        formData.append('params', JSON.stringify(params))
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/mkdirs',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function (res) {
            if (res.data['res'] !== 'ok!!!!') {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 将指定的文件的对象获取到，并传递给您自己提供的函数中！
     * @param userId {int} 空间id
     * @param type {'TEXT'|'Binary'} 文件类型
     * @param fileName {string} 需要被获取的文件目录名称
     * @param okFun {function} 操作成功之后的回调函数 输入是获取的文件的 url 地址！
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息的错误码
     * @param checkFun {function} 操作前的检查函数 输入是请求参数对象，如果返回的是一个false 则代表检查失败不继续操作
     */
    downLoad(userId, type, fileName, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e), checkFun = undefined) {
        if (userId === undefined || type == null || type === '' || fileName === undefined || fileName === '' || okFun === undefined) {
            const err = "您必须要输入 userId 和 type 以及 fileName 和 okFun 参数才可以进行文件对象的获取！"
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
            return
        }
        if (checkFun !== undefined && !checkFun(params)) {
            return;
        }
        // 开始计算 url
        okFun(this.diskMirrorUrl + this.getController() + `/downLoad/${userId}/${this.xorEncrypt(this.sk.toString())}/${type}?fileName=${fileName}`)
    }

    /**
     * 获取指定空间的最大容量 单位是 字节
     *
     * 需要确保远程的 diskMirror 服务器是在 2024年 2 月 17 日 以及之后发布的！！
     *
     * @param userId {int} 需要被检索的空间id
     * @param okFun {function} 操作成功之后的回调函数 输入是被创建的文件目录的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     */
    getSpaceMaxSize(userId, okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e)) {
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/mkdirs',
                params: {
                    spaceId: userId.toString()
                }
            }
        ).then(function (res) {
            if (isNaN(res.data)) {
                if (errorFun !== undefined) {
                    errorFun(res.data)
                }
                return;
            }
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }

    /**
     * 获取后端中的盘镜内核的版本
     *
     * 需要确保远程的 diskMirror 服务器是在 2024年 2 月 17 日 以及之后发布的！！
     *
     * @param okFun {function} 操作成功之后的回调函数 输入是被创建的文件目录的json对象
     * @param errorFun {function} 操作失败之后的回调函数 输入是错误信息
     */
    getVersion(okFun = undefined, errorFun = (e) => 'res' in e ? alert(e['res']) : alert(e)) {
        // 开始进行请求发送
        axios.defaults.withCredentials = true;
        axios(
            {
                method: 'post',
                url: this.diskMirrorUrl + this.getController() + '/getVersion',
            }
        ).then(function (res) {
            if (okFun !== undefined) {
                okFun(res.data)
            } else {
                console.info(res.data)
            }
        }).catch(function (err) {
            if (errorFun !== undefined) {
                errorFun(err)
            } else {
                console.error(err)
            }
        });
    }
}