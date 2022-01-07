export default class Engine {
    /**
     *
     * @param {Partial<import('./Cherry').CherryOptions>} markdownParams 初始化Cherry时传入的选项
     * @param {import('./Cherry').default} cherry Cherry实例
     */
    constructor(markdownParams: Partial<import('./Cherry').CherryOptions>, cherry: import('./Cherry').default);
    $cherry: import("./Cherry").default;
    hookCenter: HookCenter;
    hooks: Record<import("../types/syntax").HookType, SyntaxBase[]>;
    md5Cache: {};
    md5StrMap: {};
    markdownParams: Partial<import("../types/cherry").CherryOptions>;
    currentStrMd5: any[];
    htmlWhiteListAppend: string;
    initMath(opts: any): void;
    $configInit(params: any): void;
    $beforeMakeHtml(str: any): any;
    $afterMakeHtml(str: any): any;
    $dealSentenceByCache(md: any): {
        sign: any;
        html: any;
    };
    $dealSentence(md: any): any;
    $fireHookAction(md: any, type: any, action: any, actionArgs: any): any;
    md5(str: any): any;
    $checkCache(str: any, func: any): {
        sign: any;
        html: any;
    };
    $dealParagraph(md: any): any;
    makeHtml(md: any): any;
    mounted(): void;
    makeMarkdown(html: any): string;
}
import HookCenter from "./core/HookCenter";
import SyntaxBase from "./core/SyntaxBase";
