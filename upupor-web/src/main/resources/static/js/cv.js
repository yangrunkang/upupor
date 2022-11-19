/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

/*主要写JQuery一些基础封装插件&封装(方便后面统一替换)*/
$(function () {
    // 全局开启过渡效果
    $.support.transition = true;
    // 封装AjaxPost请求
    jQuery.cvPost = packagingAjaxPost;
    jQuery.cvPostUnder = packagingAjaxPostUnder;
    jQuery.cvGet = packagingAjaxGet;

    // 封装提示插件
    jQuery.cvSuccess = packagingToastSuccess;
    jQuery.cvInfo = packagingToastInfo;
    jQuery.cvError = packagingToastWarn; // 修改成警告的函数,Error太不友好
    jQuery.cvWarn = packagingToastWarn;

    // 加载富文本编辑器js
    jQuery.cvLoadBootstrapRichText = loadBootstrapRichText;
    jQuery.cvGetEditorData = getEditorData;
    jQuery.cvGetEditorDataMd = getEditorDataMd;
    jQuery.cvSetEditorEmpty = setEditorEmpty;
    jQuery.cvSetEditorContent = setEditorContent;
    jQuery.cvLoadShowImg = loadShowImg;

    // 开启GoUp组件
    $.goup({
        trigger: 100,
        bottomOffset: 50,
        locationOffset: 100,
        titleAsText: true,
        containerColor: '#ffffff',
        arrowColor: '#edad00',
        containerClass: 'card border-0 shadow-lg'
    });
    // fix 点2次才会下拉的bug
    $('.dropdown-toggle').dropdown();
    // semantic-ui下拉框开启
    $('.ui.dropdown')
        .dropdown();
    // 鼠标放到用户头像,显示浮动内容
    // $('[data-toggle="popover"]').popover({
    //     html: true,
    //     placement: 'top',
    // }).on( 'mouseenter' , function () {
    //     $('[data-toggle="popover"]').popover( 'hide' );
    //     $(this).popover( 'show' );
    // });
    // $('body').click(function() {
    //     $('[data-toggle="popover"]').popover( 'hide' );
    // });
});

function packagingToastInfo(message) {
    swal(message, {
        icon: "info",
        timer: 1500,
        closeOnClickOutside: false,
        closeOnEsc: false,
    });
}

function loadShowImg() {
    // 点击图片放大(ckeditor)
    $(".image").children().click(function () {
        showImg(this);
    });
    // 点击图片放大(cherry-markdown)
    $(".cherry-markdown p img").click(function () {
        showImg(this);
    });
}

function packagingToastError(message) {
    swal(message, {
        icon: "error",
        closeOnClickOutside: false,
        closeOnEsc: false,
    });
}

function packagingToastWarn(message) {
    swal(message, {
        icon: "warning",
        // timer: 1500,
        closeOnClickOutside: false,
        closeOnEsc: false,
    });
}

function packagingToastSuccess(message) {
    swal(message, {
        icon: "success",
        timer: 1500,
        closeOnClickOutside: false,
        closeOnEsc: false,
    });
}

/**
 * 封装AjaxPost请求
 * @param url
 * @param data
 * @param okFunc
 */
function packagingAjaxPost(url, data, okFunc) {
    $(".global-loading-nav-tips-slave").show();
    $.ajax({
        url: url,
        type: "POST",
        data: data,
        success: function (data) {
            $(".global-loading-nav-tips-slave").hide();
            // console.log('///////');
            // console.log(data);
            // console.log('///////');
            // 请求正常响应
            if (data.code === 0) {
                // 业务正常
                okFunc(data.data);
            } else {
                // 处理特定状态码跳转
                if (data.code === 115) { // 未登录直接调整到登录页
                    window.location.href = '/login?back=' + window.location.pathname + window.location.search;
                } else {
                    $.cvError(data.data);
                }
            }
        },
        error: function (data) {
            $(".global-loading-nav-tips-slave").hide();
            console.log(data.data);
        }
    });
}

/**
 * 不加载请求动画
 * @param url
 * @param data
 * @param okFunc
 */
function packagingAjaxPostUnder(url, data, okFunc) {
    $.ajax({
        url: url,
        type: "POST",
        data: data,
        success: function (data) {
            // 请求正常响应
            if (data.code === 0) {
                // 业务正常
                okFunc(data.data);
            } else {
                // 处理特定状态码跳转
                if (data.code === 115) { // 未登录直接调整到登录页
                    window.location.href = '/login?back=' + window.location.pathname + window.location.search;
                } else {
                    $.cvError(data.data);
                }
            }
        },
        error: function (data) {
            console.log(data.data);
        }
    });
}

/**
 * 封装AjaxGet请求
 * @param url
 * @param data
 * @param okFunc
 */
function packagingAjaxGet(url, okFunc) {
    $.ajax({
        url: url,
        type: "GET",
        success: function (data) {
            // 请求正常响应
            if (data.code === 0) {
                // 业务正常
                okFunc(data.data);
            } else {
                $.cvError(data.data);
            }
        },
        error: function (data) {
            console.log(data.data);
        }
    });
}

/*************************加载编写文章的富文本编辑器Start***********************************************/
/**
 * 富文本资源加载-写文章
 * 在页面添加元素  <div id="vcr_editor"></div>
 */
function loadBootstrapRichText(isComment) {
    initEditor(isComment);
}

/**
 * 获取编辑器数据
 */
function getEditorData() {
    return window.editor.engine.makeHtml(window.editor.getMarkdown())
}

/**
 * 获取编辑器数据
 */
function getEditorDataMd() {
    return window.editor.getMarkdown();
}

function setEditorEmpty() {
    return window.editor.setMarkdown('');
}

function setEditorContent(t) {
    return window.editor.setMarkdown(t);
}

/**
 * 上传失败
 */
function uploadFailed() {
    $.cvError("上传失败");
    $(".upload-progress-bar").attr('style', 'width:0%');
    $(".upload-progress-bar-div").hide();
}

/**
 * 显示服务器具体原因
 * @param res
 */
function uploadFailedDetail(res) {
    $.cvError("上传失败," + res.data);
    $(".upload-progress-bar").attr('style', 'width:0%');
    $(".upload-progress-bar-div").hide();
}

// cherry默认模式以及高度
let _height = '800px';
let _defaultModel = 'editOnly';

// cherry配置
let cherryConfig = {
    id: 'vcr_editor',
    value: '',
    // 解析引擎配置
    engine: {
        // 内置语法配置
        syntax: {
            emoji: {
                useUnicode: true, // 是否使用unicode进行渲染
            },
            fontEmphasis: {
                allowWhitespace: false, // 是否允许首尾空格
            },
            strikethrough: {
                needWhitespace: false, // 是否必须有首位空格
            },
            header: {
                /**
                 * 标题的样式：
                 *  - default       默认样式，标题前面有锚点
                 *  - autonumber    标题前面有自增序号锚点
                 *  - none          标题没有锚点
                 */
                anchorStyle: 'default',
            },
        },
    },
    editor: {
        theme: 'default',
        defaultModel: _defaultModel,
        height: _height,
        // 粘贴时是否自动将html转成markdown
        convertWhenPaste: true,
    },
    toolbars: {
        theme: 'light', // light or dark
        // 'size' 字体大小
        // ,
        toolbar: ['bold', 'header', 'list', 'quote', {
            insert: [
                'image',
                'link',
                'hr',
                'br',
                'code',
                'formula',
                // 'toc',
                'table',
                // 'line-table',
                // 'bar-table',
                // 'pdf',
                // 'word',
            ],
        }, 'switchModel'],
        bubble: ['bold', 'italic', 'strikethrough', 'sub', 'sup', 'size', 'color'], // array or false
        float: ['h1', 'h2', 'h3', 'checklist', 'quote', 'quickTable', 'code', 'graph'], // array or false
        customMenu: {},
    },
    fileUpload(file, callback) {

        let formData = new FormData();
        formData.append('file', file);
        $.ajax('/file/upload/CONTENT', {
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) { // check if upload property exists
                    $(".upload-progress-bar-div").show();
                    myXhr.upload.addEventListener('progress', function (e) {
                        let loaded = e.loaded;//已经上传大小情况
                        let tot = e.total;//附件总大小
                        let per = ((loaded / tot) * 100).toFixed(2);
                        $(".upload-progress-bar").attr('style', 'width:' + per + '%');
                    }, false); // for handling the progress of the upload
                }
                return myXhr;
            },
            success: function (res) {
                if (respCodeOk(res)) {
                    callback(res.data.data);
                    $(".upload-progress-bar").attr('style', 'width:0%');
                    $(".upload-progress-bar-div").hide();
                } else {
                    uploadFailedDetail(res)
                }
            },

            error: function (e) {
                uploadFailed()
            }
        });
    },
    callback: {
        /** 编辑器内容改变并完成渲染后触发 */
        afterChange: function () {
            // console.log("内容变更")
        },
        /** 编辑器完成初次渲染后触发 */
        afterInit: function () {
            $("#comment_btn_group").show();
            $("#comment_loading").hide();
        },

        onClickPreview: function (e) {
            const {target} = e;
            if (target.tagName === 'IMG') {
                console.log('click img', target);
                const tmp = new Viewer(target, {
                    button: false,
                    navbar: false,
                    title: [1, (image, imageData) => `${image.alt.replace(/#.+$/, '')} (${imageData.naturalWidth} × ${imageData.naturalHeight})`],
                });
                tmp.show();
            }
        }
    }
}

/**
 * 加载不同的编辑器js,初始化是同一个
 */
function initEditor(isComment) {
    try {
        $("#editor_load_failure").hide();
        $("#editor_loading").show();

        if (isComment) {
            _height = '400px';
            _defaultModel = 'editOnly';
        }

        let config = Object.assign({}, cherryConfig);
        window.editor = new Cherry(config);

        let mdValue = getElementValue("md_value");
        let htmlValue = getElementValue("html_value");

        if (cvIsNull(mdValue) && !cvIsNull(htmlValue)) {
            // 将html转为markdown
            mdValue = window.editor.engine.makeMarkdown(htmlValue);
        }

        if (!cvIsNull(mdValue)) {
            window.editor.setMarkdown(mdValue);
        }
    } catch (e) {
        $("#editor_load_failure").show();
        $("#editor_loading").hide();
    }

}

function reloadEditor(isComment) {
    $.cvLoadBootstrapRichText(isComment)
}


function getQueryVariable(variable) {
    let query = window.location.search.substring(1);
    let vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        let pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return false;
}

function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
    }
    return "";
}

function showImg(img) {
    const viewer = new Viewer(img, {
        inline: false,
        viewed() {
            viewer.zoomTo(1);
        },
        toolbar: {
            zoomIn: 1,
            zoomOut: 1,
            oneToOne: 1,
            reset: 0,
            prev: 0,
            play: {
                show: 0,
                size: 'large',
            },
            next: 0,
            rotateLeft: 1,
            rotateRight: 1,
            flipHorizontal: 1,
            flipVertical: 1,
        },
    });
    viewer.show();
}
