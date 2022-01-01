/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
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

$(function () {
    // 优先加载回复框
    $.cvLoadBootstrapRichText(true);

    // 实现点击图片放大效果
    $(".image").children().click(function(){
        showImg(this);
    });
});


function cancel() {
    let commentContent = $.cvGetEditorData();
    if (cvIsNull(commentContent)) {
        $.cvWarn("评论内容为空");
        return;
    }

    swal({
        title: '确定取消评论?',
        text: "当前评论内容将会被清空",
        icon: "warning",
        buttons: [{
            text: "确认",
            value: true,
            visible: true,
        }, {
            text: "取消",
            value: false,
            visible: true,
        }],
        closeOnClickOutside: false,
        closeOnEsc: false,
    }).then((confirmed) => {
        if (confirmed) {
            window.editor.setData('');
        }
    });

}

/**
 * 评论
 * @param contentId 目标Id
 * @param commentSource 评论来源
 */
function comment(contentId,commentSource) {
    if (cvIsNull(contentId)) {
        $.cvWarn("文章唯一Id缺失,禁止评论");
        return;
    }
    if (cvIsNull(commentSource)) {
        $.cvWarn("文章类型异常,禁止评论");
        return;
    }

    let commentContent = $.cvGetEditorData();
    if (cvIsNull(commentContent)) {
        $.cvWarn("评论内容为空");
        return;
    }
    let userId = $("#reply_to_user").val();
    let comment = {
        targetId: contentId,
        commentSource: commentSource,
        commentContent: commentContent,
        replyToUserId: userId
    };

    $.cvPost('/comment/add', comment, function (data) {
        if (respSuccess(data)) {
            $.cvSuccess("评论成功");
            setTimeout(function () {
                history.go()
            }, 1500);
        } else {
            $.cvError("评论失败")
        }
    });

}

/**
 * 回复评论
 * @param beReplyComment 被回复的评论
 */
function reply(userName,userId,sourceCommentContent) {
    try { // @被评论的用户
        let str = '<span style="font-weight: bold;color: #7D8B99">'+'@' + userName+':&nbsp;</span>';
        let ht ='<a href="/profile/' + userId + '">' + str + '&nbsp;</a>';
        window.editor.setData(ht);
        $("#reply_to_user").val(userId);
        //滚动到锚点位置
        $('html,body').animate({scrollTop: $(".btn-cv-comment").offset().top - 240}, 800);
    } catch (e) {
        window.location.href = '/login';
    }
}
