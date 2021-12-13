function goToFeedback() {
    let feedBack = $("#cv-feedback-textarea").val();
    if(cvIsNull(feedBack)){
        $.cvWarn("反馈内容为空");
        return;
    }
    let contact = $("#cv-feedback-contact").val();
    if(cvIsNull(contact)){
        contact = '无';
    }

    let req = {
        content: feedBack + '  <br />反馈者联系方式:' + contact,
    };

    $.cvPost('/feedback/add',req,function(res){
       if(respSuccess(res)){
           $.cvSuccess("已经反馈,我们会及时处理");
           setTimeout(function(){
               window.location.href = '/';
           },1600)
       }else{
           $.cvWarn("反馈失败,抱歉");
       }
    });
}