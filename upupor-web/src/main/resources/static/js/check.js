/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

// Function called if AdBlock is not detected
function adBlockNotDetected() {

}
// Function called if AdBlock is detected
function adBlockDetected() {
    // 使用了广告插件
    $(".used-block").show();
    console.log("使用了广告插件")
}
window.onload =function () {
// We look at whether FuckAdBlock already exists.
    if(typeof fuckAdBlock !== 'undefined' || typeof FuckAdBlock !== 'undefined') {
        // If this is the case, it means that something tries to usurp are identity
        // So, considering that it is a detection
        adBlockDetected();
    } else {
        // Otherwise, you import the script FuckAdBlock
        var importFAB = document.createElement('script');
        importFAB.onload = function() {
            // If all goes well, we configure FuckAdBlock
            fuckAdBlock.onDetected(adBlockDetected)
            fuckAdBlock.onNotDetected(adBlockNotDetected);
        };
        importFAB.onerror = function() {
            // If the script does not load (blocked, integrity error, ...)
            // Then a detection is triggered
            adBlockDetected();
        };
        importFAB.integrity = 'sha256-xjwKUY/NgkPjZZBOtOxRYtK20GaqTwUCf7WYCJ1z69w=';
        importFAB.crossOrigin = 'anonymous';
        importFAB.src = 'https://cdnjs.cloudflare.com/ajax/libs/fuckadblock/3.2.1/fuckadblock.min.js';
        document.head.appendChild(importFAB);
    }
};