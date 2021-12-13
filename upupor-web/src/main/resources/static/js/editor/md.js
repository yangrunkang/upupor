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
