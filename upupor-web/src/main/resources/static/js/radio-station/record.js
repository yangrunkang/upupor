$(function(){
    $(".reclog").hide();
    $(".recClose").hide();
    $(".recStop").hide();
    $(".record-form").hide();

    $("form").submit(function () {
        let radioIntro = $("#radio-intro").val();

        if (cvIsNull(radioIntro)) {
            $.cvError("音频简介内容为空");
            return false;
        }

        let blob=recBlob;
        if(!blob){
            reclog("请先录音，然后停止后再上传",1);
            return false;
        };

        let formData = new FormData();
        formData.append('radioFile', blob,"recorder.mp3");

        // 单数上传文件,可以读取进度
        $.ajax({
            url: '/radio/addRadioFile',
            type: 'post',
            data: formData,
            processData: false,// 告诉jQuery不要去处理发送的数据
            contentType: false,// 告诉jQuery不要去设置Content-Type请求头
            beforeSend: function () {//过程...
                $(".process-div").show();
                $(".progress-div-tips").text('上传中...请稍等');
                $(".progress-bar").removeClass("bg-danger").addClass("bg-success");
                $(".public-radio").addClass('disabled').text('发布中,请稍等');
            },
            xhr: function () {
                myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) { // check if upload property exists
                    myXhr.upload.addEventListener('progress', function (e) {
                        let loaded = e.loaded;//已经上传大小情况
                        let tot = e.total;//附件总大小
                        let per = ((loaded / tot) * 100).toFixed(2);
                        $(".progress-bar").attr('style', 'width:' + per + '%');
                        $(".public-radio").text('发布中,请稍等...' + per + '%');
                        $(".progress-text").text(per + '%');

                    }, false); // for handling the progress of the upload
                }
                return myXhr;
            },
            success: function (res) {
                if (res.code === 0) {
                    addRadio(radioIntro, res.data.data);
                } else {
                   publicFailed();
                }
            },
            error: function () {
                publicFailed();
            }
        })
        // 禁止时间
        return false;
    })

    function addRadio(radioIntro, fileUrl) {
        let formData = new FormData();
        formData.append('radioIntro', radioIntro);
        formData.append('fileUrl', fileUrl);

        //  这里涉及到上传文件,所以单独写一个ajax请求
        $.ajax({
            url: '/radio/add',
            type: 'post',
            data: formData,
            processData: false,// 告诉jQuery不要去处理发送的数据
            contentType: false,// 告诉jQuery不要去设置Content-Type请求头
            success: function (res) {
                if (res.code === 0) {
                    publicSuccess();
                } else {
                    publicFailed();
                }
            },
            error: function () {
                publicFailed();
            }
        });
    }


    function publicSuccess() {
        $(".public-radio").text('发布成功');
        $(".progress-text").text('发布成功');
        $(".progress-div-tips").text('上传成功');
        $.cvSuccess("发布成功");
        setTimeout(function () {
            window.location.href = '/radio-station';
        }, 1600);
    }

    function publicFailed() {
        $(".progress-text").text('发布失败,请重试');
        $(".progress-div-tips").text('上传失败,请重试');
        $(".progress-bar").removeClass("bg-success").addClass("bg-danger");
        $(".public-radio").removeClass('disabled').text('重新上传');
        $.cvError("上传电台文件失败,请重试");
    }



});
function reclog(s,color){
    $(".reclog").show();
    let now=new Date();
    let t=("0"+now.getHours()).substr(-2)
        +":"+("0"+now.getMinutes()).substr(-2)
        +":"+("0"+now.getSeconds()).substr(-2);
    $(".reclog").prepend('<div style="color:'+(!color?"":color==1?"red":color==2?"#0b1":color)+'">['+t+']'+s+'</div>');
};


let rec,wave,recBlob;
/**调用open打开录音请求好录音权限**/
let recOpen=function(){//一般在显示出录音按钮或相关的录音界面时进行此方法调用，后面用户点击开始录音时就能畅通无阻了
    rec=null;
    wave=null;
    recBlob=null;
    let newRec=Recorder({
        type:"mp3",sampleRate:16000,bitRate:16 //mp3格式，指定采样率hz、比特率kbps，其他参数使用默认配置；注意：是数字的参数必须提供数字，不要用字符串；需要使用的type类型，需提前把格式支持文件加载进来，比如使用wav格式需要提前加载wav.js编码引擎
        ,onProcess:function(buffers,powerLevel,bufferDuration,bufferSampleRate,newBufferIdx,asyncEnd){
            //录音实时回调，大约1秒调用12次本回调
            document.querySelector(".recpowerx").style.width=powerLevel+"%";
            //可视化图形绘制
            wave.input(buffers[buffers.length-1],powerLevel,bufferSampleRate);
        }
    });

    createDelayDialog(); //我们可以选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调，此处demo省略了弹窗的代码
    newRec.open(function(){//打开麦克风授权获得相关资源
        dialogCancel(); //如果开启了弹框，此处需要取消

        rec=newRec;

        //此处创建这些音频可视化图形绘制浏览器支持妥妥的
        wave=Recorder.FrequencyHistogramView({elem:".recwave"});

        reclog("已打开录音，可以点击录制开始录音了",2);
        $(".recClose").show();
        $(".recOpen").hide();
        $(".recStop").hide();
    },function(msg,isUserNotAllow){//用户拒绝未授权或不支持
        dialogCancel(); //如果开启了弹框，此处需要取消
        reclog((isUserNotAllow?"UserNotAllow，":"")+"打开录音失败："+msg,1);
        $(".recOpen").show();
    });

    window.waitDialogClick=function(){
        dialogCancel();
        reclog("打开失败：权限请求被忽略，<span style='color:#f00'>用户主动点击的弹窗</span>",1);
    };
};



/**关闭录音，释放资源**/
function recClose(){
    if(rec){
        rec.close();
        reclog("已关闭");
        $(".recOpen").show();
        $(".recClose").hide();
    }else{
        reclog("未打开录音",1);
        $(".recOpen").show();
    };
};




/**关闭录音，释放资源**/
function recClose2(){
    if(rec){
        rec.close();
        reclog("已关闭");
        $(".recClose223").hide();
    }else{
        reclog("未打开录音",1);
        $(".recOpen").show();
    };
};



/**开始录音**/
function recStart(){//打开了录音后才能进行start、stop调用
    if(rec&&Recorder.IsOpen()){
        recBlob=null;
        rec.start();
        reclog("已开始录音...");
        $(".recStop").show();
        $(".recStart").hide();
        $(".recPlay").hide();
        $(".recUpload").hide();
    }else{
        reclog("未打开录音",1);
    };
};

/**暂停录音**/
function recPause(){
    if(rec&&Recorder.IsOpen()){
        rec.pause();
    }else{
        reclog("未打开录音",1);
    };
};
/**恢复录音**/
function recResume(){
    if(rec&&Recorder.IsOpen()){
        rec.resume();
    }else{
        reclog("未打开录音",1);
    };
};

/**结束录音，得到音频文件**/
function recStop(){
    if(!(rec&&Recorder.IsOpen())){
        reclog("未打开录音",1);
        return;
    };
    rec.stop(function(blob,duration){
        console.log(blob,(window.URL||webkitURL).createObjectURL(blob),"时长:"+duration+"ms");
        recBlob=blob;
        reclog("已录制mp3："+duration+"ms "+blob.size+"字节，可以点击播放、上传了",2);
        $(".recPauseAndRecResume").hide();
        $(".recStopBtn").hide();
        $(".recPlay").show();
        $(".recUpload").show();
        recClose2()
    },function(msg){
        // reclog("录音失败:"+msg,1);
        reclog("您已录制完成,可以播放或者上传",1);
    });
};


/**播放**/
function recPlay(){
    if(!recBlob){
        reclog("请先录音，然后停止后再播放",1);
        return;
    };
    let cls=("a"+Math.random()).replace(".","");
    reclog('播放中: <span class="'+cls+'"></span>');
    let audio=document.createElement("audio");
    audio.controls=true;
    document.querySelector("."+cls).appendChild(audio);
    //简单利用URL生成播放地址，注意不用了时需要revokeObjectURL，否则霸占内存
    audio.src=(window.URL||webkitURL).createObjectURL(recBlob);
    audio.play();

    setTimeout(function(){
        (window.URL||webkitURL).revokeObjectURL(audio.src);
    },5000);
};

/**上传**/
function recUpload(){
    $(".record-form").show();
    $(".recUpload").hide();
    $(".all-wave").hide();
    recClose();
    $(".recOpen2").hide();
    $(".recPlay").show();
};





//recOpen我们可以选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调
let showDialog=function(){
    if(!/mobile/i.test(navigator.userAgent)){
        return;//只在移动端开启没有权限请求的检测
    };
    dialogCancel();

    //显示弹框，应该使用自己的弹框方式
    let div=document.createElement("div");
    document.body.appendChild(div);
    div.innerHTML=(''
        +'<div class="waitDialog" style="z-index:99999;width:100%;height:100%;top:0;left:0;position:fixed;background:rgba(0,0,0,0.3);">'
        +'<div style="display:flex;height:100%;align-items:center;">'
        +'<div style="flex:1;"></div>'
        +'<div style="width:240px;background:#fff;padding:15px 20px;border-radius: 10px;">'
        +'<div style="padding-bottom:10px;">录音功能需要麦克风权限，请允许；如果未看到任何请求，请点击忽略~</div>'
        +'<div style="text-align:center;"><a onclick="waitDialogClick()" style="color:#0B1">忽略</a></div>'
        +'</div>'
        +'<div style="flex:1;"></div>'
        +'</div>'
        +'</div>');
};
let createDelayDialog=function(){
    dialogInt=setTimeout(function(){//定时8秒后打开弹窗，用于监测浏览器没有发起权限请求的情况，在open前放置定时器利于收到了回调能及时取消（不管open是同步还是异步回调的）
        showDialog();
    },8000);
};
let dialogInt;
let dialogCancel=function(){
    clearTimeout(dialogInt);

    //关闭弹框，应该使用自己的弹框方式
    let elems=document.querySelectorAll(".waitDialog");
    for(let i=0;i<elems.length;i++){
        elems[i].parentNode.removeChild(elems[i]);
    };
};
//recOpen弹框End
