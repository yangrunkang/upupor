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
    showKeyword();
    intiSearchInput()
});

function showKeyword() {
    let queryString = getQueryString('keyword');
    if(cvIsNull(queryString)){
        $.cvError("您未输入任何关键词,无法搜索. 即将跳转至首页");
        setTimeout(function(){
            window.location.href = '/';
        },6000);
        return;
    }
    $("#navbar_search_keyword").text(queryString);
}


function intiSearchInput() {
    let searchTotal = $("#content_more_alert_value").val();
    let searchTotalNum = parseInt(searchTotal);
    if (searchTotalNum > 3000) {
        $("#content_more_alert").show();
    } else {
        $("#content_more_alert").hide();
    }
}