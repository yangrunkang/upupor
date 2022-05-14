/**
 * @typedef {import('~types/editor').EditorConfiguration} EditorConfiguration
 * @typedef {import('~types/editor').EditorEventCallback} EditorEventCallback
 * @typedef {import('codemirror')} CodeMirror
 */
/** @type {import('~types/editor')} */
export default class Editor {
    /**
     * @constructor
     * @param {Partial<EditorConfiguration>} options
     */
    constructor(options: Partial<EditorConfiguration>);
    /**
     * @property
     * @type {EditorConfiguration}
     */
    options: EditorConfiguration;
    /**
     * @property
     * @private
     * @type {{ timer?: number; destinationTop?: number }}
     */
    private animation;
    $cherry: import("./Cherry").default;
    instanceId: string;
    /**
     *
     * @param {ClipboardEvent} e
     * @param {CodeMirror.Editor} codemirror
     */
    onPaste(e: ClipboardEvent, codemirror: CodeMirror.Editor): void;
    /**
     *
     * @param {ClipboardEvent} event
     * @param {ClipboardEvent['clipboardData']} clipboardData
     * @param {CodeMirror.Editor} codemirror
     * @returns {boolean | void}
     */
    handlePaste(event: ClipboardEvent, clipboardData: ClipboardEvent['clipboardData'], codemirror: CodeMirror.Editor): boolean | void;
    /**
     *
     * @param {CodeMirror.Editor} codemirror
     */
    onScroll: (codemirror: CodeMirror.Editor) => void;
    disableScrollListener: boolean;
    /**
     *
     * @param {CodeMirror.Editor} codemirror
     * @param {MouseEvent} evt
     */
    onMouseDown: (codemirror: CodeMirror.Editor, evt: MouseEvent) => void;
    /**
     *
     * @param {*} previewer
     */
    init(previewer: any): void;
    previewer: any;
    /**
     * @property
     * @type {CodeMirror.Editor}
     */
    editor: CodeMirror.Editor;
    /**
     *
     * @param {number | null} beginLine 起始行，传入null时跳转到文档尾部
     * @param {number} [endLine] 终止行
     * @param {number} [percent] 百分比，取值0~1
     */
    jumpToLine(beginLine: number | null, endLine?: number, percent?: number): void;
    /**
     *
     * @param {number | null} lineNum
     * @param {number} [endLine]
     * @param {number} [percent]
     */
    scrollToLineNum(lineNum: number | null, endLine?: number, percent?: number): void;
    /**
     *
     * @returns {HTMLElement}
     */
    getEditorDom(): HTMLElement;
    /**
     *
     * @param {string} event 事件名
     * @param {EditorEventCallback} callback 回调函数
     */
    addListener(event: string, callback: EditorEventCallback): void;
}
export type EditorConfiguration = import('~types/editor').EditorConfiguration;
export type EditorEventCallback = import('~types/editor').EditorEventCallback;
export type CodeMirror = typeof codemirror;
import codemirror from "codemirror";
