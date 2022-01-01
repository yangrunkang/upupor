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
 * 检查是否为空
 * @param value
 * @returns {boolean} true 为空, false 不为空
 */
function cvIsNull(value) {
    return value === 'null' || value === null || value === '' || typeof (value) === undefined || value === 'undefined' || typeof (value) === 'undefined';
}

function cvEquals(str1, str2) {
    return str1 === str2;
}

/**
 * 判断是否全被为空
 * @returns {boolean}  true: 全部为空   false: 不是全部为空
 */
function allEmpty() {
    if (arguments.length === 0) {
        return true;
    }
    for (let i = 0; i < arguments.length; i++) {
        if(!cvIsNull(arguments[i])){
            return false;
        }
    }
    return true;
}

/**
 * 响应成功检查
 * @param data
 * @returns {boolean}
 */
function respSuccess(data) {
    return data.code === 0 && data.data === true;
}

function respDataTrue(data) {
    return data === true;
}

function respCodeOk(data) {
    return data.code === 0;
}

/**
 * 异步加载 CSS
 * Demo: 加载 CSS 后执行 then 的回调函数
 loadCss('http://qtdebug.com/css/style.css').then(msg => {
    console.log(msg);
 });

 * @param {String} url CSS 路径
 * @param {String} id  CSS 的 <link> 的 ID，如果已经存在则不再重复加载，默认为时间戳+随机数
 * @return 返回 Promise 对象
 */
function loadCss(url, id = Date.now() + '-' + Math.random()) {
    return new Promise(function (resolve, reject) {
        // 避免重复加载
        if (document.getElementById(id)) {
            resolve('success: ' + url);
            return;
        }

        var script = document.createElement('link');

        if (script.readyState) {  // IE
            script.onreadystatechange = function () {
                if (script.readyState == 'loaded' || script.readyState == 'complete') {
                    script.onreadystatechange = null;
                    resolve('success: ' + url);
                }
            };
        } else {  // Other Browsers
            script.onload = function () {
                resolve('success: ' + url);
            };
        }

        script.onerror = function () {
            reject(Error(url + ' load error!'));
        };

        script.rel = 'stylesheet';
        script.id = id;
        script.href = `${url}?hash=${id}`;
        document.getElementsByTagName('head').item(0).appendChild(script);
    });
}


/**
 * 使用 Promise 异步加载 JS
 *
 * Demo:
 loadJs('http://cdn.bootcss.com/jquery/1.9.1/jquery.min.js', 'jq').then(msg => {
    console.log(msg);
 });
 *
 * @param {String} url JS 的路径
 * @param {String} id  JS 的 <style> 的 ID，如果已经存在则不再重复加载，默认为时间戳+随机数
 * @return 返回 Promise 对象, then 的参数为加载成功的信息，无多大意义
 */
function loadJs(url, id = Date.now() + '-' + Math.random()) {
    return new Promise(function (resolve, reject) {
        // 避免重复加载
        if (document.getElementById(id)) {
            resolve('success: ' + url);
            return;
        }

        var script = document.createElement('script');

        if (script.readyState) {  // IE
            script.onreadystatechange = function () {
                if (script.readyState == 'loaded' || script.readyState == 'complete') {
                    script.onreadystatechange = null;
                    resolve('success: ' + url);
                }
            };
        } else {  // Other Browsers
            script.onload = function () {
                resolve('success: ' + url);
            };
        }

        script.onerror = function () {
            reject(Error(url + ' load error!'));
        };

        script.type = 'text/javascript';
        script.id = id;
        script.src = `${url}?hash=${id}`;
        document.getElementsByTagName('head').item(0).appendChild(script);
    });
}

/**
 * 获取内容类型
 * @param from
 * @returns {number}
 */
function getContentType(fromSource) {
    switch (fromSource) {
        case 'tech/index':
            return 1;
        case 'qa/index':
            return 2;
        case 'share/index':
            return 3;
        case 'workplace/index':
            return 4;
        case 'record/index':
            return 5;
        default:
            // 默认
            return 0;
    }
}

/**
 * 格式化时间戳
 * @param unixTime
 * @returns {string}
 */
function formatUnixTime(unixTime) {
    if (unixTime === 0 || unixTime === null) {
        return '-1';
    }
    return new Date(parseInt(unixTime) * 1000).toLocaleString().replace(/:\d{1,2}$/, ' ');
}

/**
 * 获取url中的参数值
 * @param name
 * @returns {string|null}
 * link: https://www.jianshu.com/p/708c915fb905
 */
function getQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    let r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

function getExplorer(){
    let explorer = window.navigator.userAgent ;
    //判断是否为IE浏览器
    if (explorer.indexOf("MSIE") >= 0) {
        return 'ie';
    }
    //判断是否为Firefox浏览器
    else if (explorer.indexOf("Firefox") >= 0) {
        return 'Firefox';
    }
    //判断是否为Chrome浏览器
    else if(explorer.indexOf("Chrome") >= 0){
        return 'Chrome';
    }
    //判断是否为Opera浏览器
    else if(explorer.indexOf("Opera") >= 0){
        return 'Opera';
    }
    //判断是否为Safari浏览器
    else if(explorer.indexOf("Safari") >= 0){
        return 'Safari';
    }
}

function getFormatDate() {
    let date = new Date();
    let month = date.getMonth() + 1;
    let strDate = date.getDate();
    let minute = date.getMinutes();
    let seconds = date.getSeconds();

    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }

    if(minute >= 1 && minute <= 9){
        minute= "0" + minute;
    }

    if(seconds >= 1 && seconds <= 9){
        seconds= "0" + seconds;
    }


    return date.getFullYear() + "-" + month + "-" + strDate
        + " " + date.getHours() + ":" + minute + ":" + seconds;
}