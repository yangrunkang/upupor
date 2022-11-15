/**
 * @typedef {import('@/Editor').default} Editor
 */
/**
 * @class MenuBase
 */
export default class MenuBase {
    /**
     *
     * @param {*} $cherry
     */
    constructor($cherry: any);
    /**
     * @deprecated
     * @type {MenuBase['fire']}
     */
    _onClick: MenuBase['fire'];
    $cherry: any;
    bubbleMenu: boolean;
    subMenu: any;
    name: string;
    editor: any;
    dom: HTMLSpanElement;
    updateMarkdown: boolean;
    subMenuConfig: any[];
    noIcon: boolean;
    cacheOnce: boolean;
    /**
     * 子菜单的定位方式
     * @property
     * @type {'absolute' | 'fixed'}
     */
    positionModel: 'absolute' | 'fixed';
    /**
     * 处理菜单项点击事件
     * @param {MouseEvent | KeyboardEvent | undefined} [event] 点击事件
     * @returns {void}
     */
    fire(event?: MouseEvent | KeyboardEvent | undefined, shortKey?: string): void;
    getSubMenuConfig(): any[];
    /**
     * 设置菜单
     * @param {string} name 菜单名称
     * @param {string} [iconName] 菜单图标名
     */
    setName(name: string, iconName?: string): void;
    iconName: string;
    /**
     * 设置一个一次性缓存
     * 使用场景：
     *  当需要异步操作是，比如上传视频、选择字体颜色、通过棋盘插入表格等
     * 实现原理：
     *  1、第一次点击按钮时触发fire()方法，触发选择文件、选择颜色、选择棋盘格的操作。此时onClick()不返回任何数据。
     *  2、当异步操作完成后（如提交了文件、选择了颜色等），调用本方法（setCacheOnce）实现缓存，最后调用fire()方法
     *  3、当fire()方法再次调用onClick()方法时，onClick()方法会返回缓存的数据（getAndCleanCacheOnce）
     *
     * 这么设计的原因：
     *  1、可以复用MenuBase的相关方法
     *  2、避免异步操作直接与codemirror交互
     * @param {*} info
     */
    setCacheOnce(info: any): void;
    getAndCleanCacheOnce(): boolean;
    hasCacheOnce(): boolean;
    /**
     * 创建一个一级菜单
     * @param {boolean} asSubMenu 是否以子菜单的形式创建
     */
    createBtn(asSubMenu?: boolean): HTMLSpanElement;
    createSubBtnByConfig(config: any): HTMLSpanElement;
    isSelections: boolean;
    /**
     * 获取当前选择区域的range
     */
    $getSelectionRange(): {
        begin: any;
        end: any;
    };
    /**
     * 注册点击事件渲染后的回调函数
     * @param {function} cb
     */
    registerAfterClickCb(cb: Function): void;
    afterClickCb: Function;
    /**
     * 点击事件渲染后的回调函数
     */
    $afterClick(): void;
    /**
     * 选中除了前后语法后的内容
     * @param {String} lessBefore
     * @param {String} lessAfter
     */
    setLessSelection(lessBefore: string, lessAfter: string): void;
    /**
     * 基于当前已选择区域，获取更多的选择区
     * @param {string} [appendBefore] 选择区前面追加的内容
     * @param {string} [appendAfter] 选择区后面追加的内容
     * @param {function} [cb] 回调函数，如果返回false，则恢复原来的选取
     */
    getMoreSelection(appendBefore?: string, appendAfter?: string, cb?: Function): void;
    /**
     * 获取用户选中的文本内容，如果没有选中文本，则返回光标所在的位置的内容
     * @param {string} selection 当前选中的文本内容
     * @param {string} type  'line': 当没有选择文本时，获取光标所在行的内容； 'word': 当没有选择文本时，获取光标所在单词的内容
     * @param {boolean} focus true；强行选中光标处的内容，否则只获取选中的内容
     * @returns {string}
     */
    getSelection(selection: string, type?: string, focus?: boolean): string;
    /**
     * 反转子菜单点击事件参数顺序
     * @deprecated
     */
    bindSubClick(shortcut: any, selection: any): void;
    onClick(selection: any, shortcut: any, callback: any): any;
    get shortcutKeys(): any[];
    /**
     * 获取当前菜单的位置
     */
    getMenuPosition(): Pick<DOMRect, "left" | "top" | "width" | "height">;
}
export type Editor = import('@/Editor').default;
