$(function () {
    // 优先加载回复框
    $.cvLoadBootstrapRichText(true);
    // 加载分页

    // 实现点击图片放大效果
    $(".image").children().click(function(){
        showImg(this);
    });

    $("." + window.location.pathname.split("/")[1].split("-").join("_")).addClass('active');

});


function cancel() {
    let commentContent = $.cvGetEditorData();
    if (cvIsNull(commentContent)) {
        $.cvWarn("留言内容为空");
        return;
    }

    swal({
        title: '确定取消留言?',
        text: "当前留言内容将会被清空",
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
function comment(userId) {
    if (cvIsNull(userId)) {
        $.cvWarn("用户Id缺失,禁止评论");
        return;
    }

    let commentContent = $.cvGetEditorData();
    if (cvIsNull(commentContent)) {
        $.cvWarn("评论内容为空");
        return;
    }
    let beReplyUserId = $("#reply_to_user").val();
    let comment = {
        targetId: userId,
        commentSource: 7,
        commentContent: commentContent,
        replyToUserId: beReplyUserId
    };

    $.cvPost('/comment/add', comment, function (data) {
        if (respSuccess(data)) {
            $.cvSuccess("留言成功");
            setTimeout(function () {
                history.go()
            }, 1500);
        } else {
            $.cvError("留言失败")
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


