export class SubMenu {
    /**
     *
     * @param {MenuBase} menuContext 菜单上下文
     * @param {string} name 菜单项名称
     * @param {Pick<DOMRect, 'left' | 'top' | 'width' | 'height'>} defaultPosition 菜单定位
     * @param {any} menuConfig 菜单项
     * @param {Record<string, Function>} eventHandlers 事件回调
     * @param {'absolute' | 'fixed'} positionModel 菜单定位方式
     */
    constructor(menuContext: MenuBase, name: string, defaultPosition: Pick<DOMRect, 'left' | 'top' | 'width' | 'height'>, menuConfig: any, eventHandlers: Record<string, Function>, positionModel?: 'absolute' | 'fixed');
    name: string;
    dom: HTMLDivElement;
    visible: boolean;
    context: MenuBase;
    positionModel: "fixed" | "absolute";
    init(name: any, defaultPosition: any, menuConfig: any, eventHandlers: any): void;
    /**
     *
     * @param {Pick<DOMRect, 'left' | 'top' | 'width' | 'height'>} [position] 定位
     */
    show(position?: Pick<DOMRect, 'left' | 'top' | 'width' | 'height'>): void;
    hide(): void;
    onClick(): void;
}
/**
 * @typedef {import('@/Editor').default} Editor
 */
/**
 * @class MenuBase
 */
export default class MenuBase {
    static cleanSubMenu(): void;
    /**
     * 隐藏除给定名字外的所有子菜单
     * @param {string | null} name 不隐藏的子菜单
     */
    static hideSubMenuExcept(name: string | null): void;
    /**
     *
     * @param {Editor} editor
     */
    constructor(editor: Editor);
    /**
     * @deprecated
     * @type {MenuBase['$onClick']}
     */
    _onClick: (event: MouseEvent) => void;
    /** @type {boolean} 是否浮动菜单*/
    bubbleMenu: boolean;
    subMenu: SubMenu;
    name: string;
    editor: import("@/Editor").default;
    dom: HTMLSpanElement;
    updateMarkdown: boolean;
    subMenuConfig: any[];
    /**
     * 子菜单的定位方式
     * @property
     * @private
     * @type {'absolute' | 'fixed'}
     */
    private positionModel;
    /**
     * 内部处理菜单项点击事件
     * @param {MouseEvent} event 点击事件
     * @returns {void}
     */
    $onClick(event: MouseEvent): void;
    getSubMenuConfig(): any[];
    /**
     * 设置菜单
     * @param {string} name 菜单名称
     * @param {string} [iconName] 菜单图标名
     */
    setName(name: string, iconName?: string): void;
    iconName: string;
    /**
     * 初始化菜单项
     */
    createBtn(): HTMLSpanElement;
    /**
     *
     * @param {CodeMirror.Editor} codemirror cm实例
     * @param {string[]} selections 选中的文本集合
     * @param {string} key 触发的快捷键
     * @returns
     */
    onKeyDown(codemirror: CodeMirror.Editor, selections: string[], key: string): void;
    bindSubClick(shortcut: any, selection: any): any;
    onClick(selection: any, shortcut: any, callback: any): any;
    get shortcutKeys(): any[];
    shortcutKey(options: any): any;
    initSubMenu(): void;
    showSubMenu(): void;
    hideSubMenu(): void;
    toggleSubMenu(): void;
    onSubClick(clickEventHandler: any, async: any, event: any): void;
}
export type Editor = import('@/Editor').default;
