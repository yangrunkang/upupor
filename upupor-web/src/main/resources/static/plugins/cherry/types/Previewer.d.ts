/**
 * 作用：
 *  dom更新
 *  局部加载（分片）
 *  与左侧输入区域滚动同步
 */
export default class Previewer {
    /**
     *
     * @param {Partial<import('~types/previewer').PreviewerOptions>} options 预览区域设置
     */
    constructor(options: Partial<import('~types/previewer').PreviewerOptions>);
    /**
     * @property
     * @private
     * @type {boolean} 等待预览区域更新。预览区域更新时，预览区的滚动不会引起编辑器滚动，避免因插入的元素高度变化导致编辑区域跳动
     */
    private applyingDomChanges;
    /**
     * @property
     * @private
     * @type {number} 释放同步滚动锁定的定时器ID
     */
    private syncScrollLockTimer;
    /**
     * @property
     * @public
     * @type {boolean} 是否为移动端预览模式
     */
    public isMobilePreview: boolean;
    /**
     * @property
     * @type {import('~types/previewer').PreviewerOptions}
     */
    options: import('~types/previewer').PreviewerOptions;
    $cherry: import("./Cherry").default;
    instanceId: string;
    /**
     * @property
     * @private
     * @type {{ timer?: number; destinationTop?: number }}
     */
    private animation;
    init(editor: any): void;
    /**
     * @property
     * @private
     * @type {boolean} 禁用滚动事件监听
     */
    private disableScrollListener;
    editor: any;
    lazyLoadImg: LazyLoadImg;
    $initPreviewerBubble(): void;
    previewerBubble: PreviewerBubble;
    /**
     * @returns {HTMLElement}
     */
    getDomContainer(): HTMLElement;
    getDom(): HTMLDivElement;
    /**
     * 获取预览区内的html内容
     * @param {boolean} wrapTheme 是否在外层包裹主题class
     * @returns html内容
     */
    getValue(wrapTheme?: boolean): string;
    isPreviewerHidden(): boolean;
    calculateRealLayout(editorWidth: any): {
        editorPercentage: string;
        previewerPercentage: string;
    };
    setRealLayout(editorPercentage: any, previewerPercentage: any): void;
    syncVirtualLayoutFromReal(): void;
    calculateVirtualLayout(editorLeft: any, editorRight: any): {
        startWidth: number;
        leftWidth: number;
        rightWidth: number;
    };
    setVirtualLayout(startWidth: any, leftWidth: any, rightWidth: any): void;
    bindDrag(): void;
    bindScroll(): void;
    removeScroll(): void;
    $html2H(dom: any): any;
    $getAttrsForH(obj: any): {};
    $updateDom(newDom: any, oldDom: any): any;
    $testChild(dom: any): any;
    _testMaxIndex(index: any, arr: any): boolean;
    $getSignData(dom: any): {
        list: any[];
        signs: {};
    };
    _hasNewSign(list: any, sign: any, signIndex: any): boolean;
    $dealWithMyersDiffResult(result: any, oldContent: any, newContent: any, domContainer: any): void;
    $dealUpdate(domContainer: any, oldHtmlList: any, newHtmlList: any): void;
    update(html: any): void;
    $dealEditAndPreviewOnly(isEditOnly?: boolean): void;
    previewOnly(): void;
    editOnly(dealToolbar?: boolean): void;
    recoverPreviewer(dealToolbar?: boolean): void;
    doHtmlCache(html: any): void;
    cleanHtmlCache(): void;
    afterUpdate(): void;
    registerAfterUpdate(fn: any): void;
    /**
     * 根据行号计算出top值
     * @param {Number} lineNum
     * @param {Number} linePercent
     * @return {Number} top
     */
    $getTopByLineNum(lineNum: number, linePercent?: number): number;
    /**
     * 滚动到对应行号位置并加上偏移量
     * @param {Number} lineNum
     * @param {Number} offset
     */
    scrollToLineNumWithOffset(lineNum: number, offset: number): void;
    /**
     * 实现滚动动画
     * @param { Number } targetY 目标位置
     */
    $scrollAnimation(targetY: number): void;
    scrollToLineNum(lineNum: any, linePercent: any): void;
    /**
     * 导出预览区域内容
     * @public
     * @param {String} type 'pdf'：导出成pdf文件; 'img'：导出成图片
     */
    public export(type?: string): void;
}
import LazyLoadImg from "@/utils/lazyLoadImg";
import PreviewerBubble from "./toolbars/PreviewerBubble";
