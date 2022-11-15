/** @typedef {import('~types/cherry').CherryOptions} CherryOptions */
export default class Cherry extends CherryStatic {
    /**
     * @protected
     */
    protected static initialized: boolean;
    /**
     * @readonly
     */
    static readonly config: {
        /** @type {Partial<CherryOptions>} */
        defaults: Partial<CherryOptions>;
    };
    /**
     * @param {Partial<CherryOptions>} options
     */
    constructor(options: Partial<CherryOptions>);
    defaultToolbar: false | (string | import("~types/cherry").CherryInsertToolbar)[];
    /**
     * @property
     * @type {Partial<CherryOptions>}
     */
    options: Partial<CherryOptions>;
    status: {
        toolbar: string;
        previewer: string;
        editor: string;
    };
    /**
     * @property
     * @type {string} 实例ID
     */
    instanceId: string;
    /**
     * @private
     * @type {Engine}
     */
    private engine;
    /**
     * 初始化工具栏、编辑区、预览区等
     * @private
     */
    private init;
    cherryDom: HTMLElement;
    toolbar: any;
    sidebar: any;
    /**
     *  监听 cherry 高度变化，高度改变触发 codemirror 内容刷新
     * @private
     */
    private cherryDomResize;
    cherryDomReiszeObserver: ResizeObserver;
    /**
     * 切换编辑模式
     * @param {'edit&preview'|'editOnly'|'previewOnly'} model 模式类型
     * 一般纯预览模式和纯编辑模式适合在屏幕较小的终端使用，比如手机移动端
     *
     * @returns
     */
    switchModel(model?: 'edit&preview' | 'editOnly' | 'previewOnly'): void;
    /**
     * 获取实例id
     * @returns {string}
     * @public
     */
    public getInstanceId(): string;
    /**
     * 获取编辑器状态
     * @returns  {Object}
     */
    getStatus(): any;
    /**
     * 获取编辑区内的markdown源码内容
     * @returns markdown源码内容
     */
    getValue(): string;
    /**
     * 获取编辑区内的markdown源码内容
     * @returns markdown源码内容
     */
    getMarkdown(): string;
    /**
     * 获取CodeMirror实例
     * @returns CodeMirror实例
     */
    getCodeMirror(): import("codemirror").Editor;
    /**
     * 获取预览区内的html内容
     * @param {boolean} wrapTheme 是否在外层包裹主题class
     * @returns html内容
     */
    getHtml(wrapTheme?: boolean): string;
    getPreviewer(): Previewer;
    /**
     * 获取目录，目录由head1~6组成
     * @returns 标题head数组
     */
    getToc(): {
        level: number;
        id: string;
        text: string;
    }[];
    /**
     * 覆盖编辑区的内容
     * @param {string} content markdown内容
     * @param {boolean} keepCursor 是否保持光标位置
     * @returns
     */
    setValue(content: string, keepCursor?: boolean): void;
    /**
     * 在光标处或者指定行+偏移量插入内容
     * @param {string} content 被插入的文本
     * @param {boolean} [isSelect=false] 是否选中刚插入的内容
     * @param {[number, number]|false} [anchor=false] [x,y] 代表x+1行，y+1字符偏移量，默认false 会从光标处插入
     * @param {boolean} [focus=true] 保持编辑器处于focus状态
     * @returns
     */
    insert(content: string, isSelect?: boolean, anchor?: [number, number] | false, focus?: boolean): void;
    /**
     * 在光标处或者指定行+偏移量插入内容
     * @param {string} content 被插入的文本
     * @param {boolean} [isSelect=false] 是否选中刚插入的内容
     * @param {[number, number]|false} [anchor=false] [x,y] 代表x+1行，y+1字符偏移量，默认false 会从光标处插入
     * @param {boolean} [focus=true] 保持编辑器处于focus状态
     * @returns
     */
    insertValue(content: string, isSelect?: boolean, anchor?: [number, number] | false, focus?: boolean): void;
    /**
     * 覆盖编辑区的内容
     * @param {string} content markdown内容
     * @param {boolean} keepCursor 是否保持光标位置
     * @returns
     */
    setMarkdown(content: string, keepCursor?: boolean): void;
    /**
     * @private
     * @returns
     */
    private createWrapper;
    wrapperDom: HTMLDivElement;
    /**
     * @private
     * @returns
     */
    private createToolbar;
    /**
     * @private
     * @returns
     */
    private createSidebar;
    /**
     * @private
     * @returns
     */
    private createFloatMenu;
    floatMenu: FloatMenu;
    /**
     * @private
     * @returns
     */
    private createBubble;
    bubble: Bubble;
    /**
     * @private
     * @returns {import('@/Editor').default}
     */
    private createEditor;
    editor: Editor;
    /**
     * @private
     * @returns {import('@/Previewer').default}
     */
    private createPreviewer;
    previewer: Previewer;
    /**
     * @private
     * @param {import('codemirror').Editor} codemirror
     */
    private initText;
    /**
     * @private
     * @param {Event} _evt
     * @param {import('codemirror').Editor} codemirror
     */
    private editText;
    timer: NodeJS.Timeout;
    /**
     * @private
     * @param {any} cb
     */
    private onChange;
    /**
     * @private
     * @param {*} evt
     */
    private fireShortcutKey;
    /**
     * 导出预览区域内容
     * @public
     * @param {String} type 'pdf'：导出成pdf文件; 'img'：导出成图片
     */
    public export(type?: string): void;
}
export type CherryOptions = import('~types/cherry').CherryOptions;
import { CherryStatic } from "./CherryStatic";
import Previewer from "./Previewer";
import FloatMenu from "./toolbars/FloatMenu";
import Bubble from "./toolbars/Bubble";
import Editor_1 from "./Editor";
