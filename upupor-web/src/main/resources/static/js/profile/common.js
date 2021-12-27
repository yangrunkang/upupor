/**
 * 关注作者
 * @param contentUserId
 */
function attentionAuthor(contentUserId){
    let attentionTips = $("#attention_tips").text();
    let isAttention = false;
    if(attentionTips === '已关注作者'){
        isAttention = true;
    }else if(attentionTips === '关注作者'){
        isAttention = false;
    }else{
        console.error("文章详情页,关注作者文字有误")
    }

    console.log(contentUserId);
    let req = {
        attentionUserId:contentUserId,
        isAttention:isAttention
    };

    $.cvPost('/attention/add',req,function(res){
            console.log(res);
            if(respCodeOk(res)){
                if(isAttention){
                    // 已关注作者的操作重复点击(二次点击)意味着取消关注
                    showCancelAttentionText();
                }else{
                    showAttentionText();
                }
            }else{
                $.cvError("关注失败")}
        }
    );

}

function showAttentionText() {
    $("#attention_glyphicon").attr("class", "glyphicon glyphicon-ok");
    $("#attention_tips").text("已关注作者");
}
function showCancelAttentionText() {
    $("#attention_glyphicon").attr("class", "glyphicon glyphicon-plus-sign");
    $("#attention_tips").text("关注作者");
}
