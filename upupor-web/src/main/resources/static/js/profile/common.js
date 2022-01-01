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
