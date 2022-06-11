/**
 * 段落级语法
 * 段落级语法可以具备以下特性：
 *  1、排他性，可以排除将当前语法之后的所有段落语法
 *  2、可排序，在../HooksConfig.js里设置排序，顺序在前面的段落语法先渲染
 *  3、可嵌套行内语法
 *
 * 段落级语法有以下义务：
 *  1、维护签名，签名用来实现预览区域的局部更新功能
 *  2、维护行号，行号用来实现编辑区和预览区同步滚动
 *     每个段落语法负责计算上文的行号，上文行号不是0就是1，大于1会由BR语法计算行号
 */
export default class Paragraph extends ParagraphBase {
    constructor(options: any);
    classicBr: any;
    removeBrAfterBlock: RegExp;
    removeBrBeforeBlock: RegExp;
    removeNewlinesBetweenTags: RegExp;
    /**
     * 处理经典换行问题
     * @param {string} str markdown源码
     * @returns markdown源码
     */
    $cleanParagraph(str: string): string;
    /**
     * remove all newlines in html text
     *
     * @param {string} textContainsHtml
     */
    joinRawHtml(textContainsHtml: string): string;
}
import ParagraphBase from "@/core/ParagraphBase";
