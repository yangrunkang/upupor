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
    addConsultantApply();
});


/**
 * 添加广告申请
 */
function addConsultantApply() {
    $('form').submit(function (event) {

        let topic = $("#topic").val();
        let desc = $("#desc").val();

        if (cvIsNull(topic)) {
            $('#topic').popover('show');
            return false;
        } else {
            $('#topic').popover('hide');
        }

        if(cvIsNull(desc)){
            $.cvWarn("描述信息为空");
            return false;
        }

        let adReq = {
            topic: topic,
            desc: desc,
            type: 'CONSULTING_SERVICE',
        };

        $.cvPost('/apply/addConsultant', adReq, function (data) {
            if (respSuccess(data)) {
                $.cvSuccess("申请成功");
                setTimeout(function () {
                    window.location.href = '/user/manage/apply';
                }, 1600)
            } else {
                $.cvError(data)
            }
        });

        return false;
    });
}
