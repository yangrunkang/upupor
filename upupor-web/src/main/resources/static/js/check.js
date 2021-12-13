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