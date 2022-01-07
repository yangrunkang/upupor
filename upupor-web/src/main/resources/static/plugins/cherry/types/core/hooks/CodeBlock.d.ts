export default class CodeBlock extends ParagraphBase {
    static inlineCodeCache: {};
    constructor({ externals, config }: {
        externals: any;
        config: any;
    });
    codeCache: {};
    customLang: any[];
    customParser: any;
    wrap: any;
    lineNumber: any;
    customHighlighter: any;
    $codeCache(sign: any, str: any): any;
    parseCustomLanguage(lang: any, codeSrc: any, props: any): string | false;
    fillTag(lines: any): any;
    renderLineNumber(code: any): any;
    /**
     * 判断内置转换语法是否被覆盖
     * @param {string} lang
     */
    isInternalCustomLangCovered(lang: string): boolean;
    /**
     * 预处理代码块
     * @param {string} match
     * @param {string} leadingContent
     * @param {string} code
     */
    computeLines(match: string, leadingContent: string, code: string): {
        sign: any;
        lines: number;
    };
    /**
     * 补齐用codeBlock承载的mermaid
     * @param {string} $code
     * @param {string} $lang
     */
    appendMermaid($code: string, $lang: string): string[];
    /**
     * 包裹代码块，解决单行代码超出长度
     * @param {string} $code
     * @param {string} lang
     */
    wrapCode($code: string, lang: string): string;
    /**
     * 使用渲染引擎处理代码块
     * @param {string} $code
     * @param {string} $lang
     * @param {string} sign
     * @param {number} lines
     */
    renderCodeBlock($code: string, $lang: string, sign: string, lines: number): string;
    $replaceSpecialChar(str: any): any;
}
import ParagraphBase from "@/core/ParagraphBase";
