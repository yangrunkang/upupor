/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

const { Editor } = toastui;
const { chart, codeSyntaxHighlight, colorSyntax, tableMergedCell } = Editor.plugin;

const chartOptions = {
    minWidth: 100,
    maxWidth: 600,
    minHeight: 100,
    maxHeight: 300
};



const editor = new Editor({
    el: document.querySelector('#editor'),
    previewStyle: 'vertical',
    height: '500px',
    initialValue: allPluginsContent,
    plugins: [[chart, chartOptions], [codeSyntaxHighlight, { highlighter: Prism }], colorSyntax, tableMergedCell]
});

// 移除默认的上传图片钩子
editor.removeHook('addImageBlobHook');
// 添加自己定义的上传图片钩子
editor.addHook('addImageBlobHook', (blob, callback) => {
    // 此处填写自己的上传逻辑，url为上传后的图片地址
    let upload_url = '/uploadFile';

    let formData = new FormData();
    formData.append('upload', blob, 'upload-md-img.jpg');
    $.ajax(upload_url, {
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (res) {
            callback(JSON.parse(res).url);
        },
        error: function () {
        }
    });
});
