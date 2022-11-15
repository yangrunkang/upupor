export default class Toolbar {
    constructor(options: any);
    /**
     * @type {Record<string, any>} 外部获取 toolbarHandler
     */
    toolbarHandlers: Record<string, any>;
    menus: HookCenter;
    shortcutKeyMap: {};
    subMenus: {};
    options: {
        dom: HTMLDivElement;
        buttonConfig: string[];
        customMenu: any[];
    };
    $cherry: any;
    instanceId: any;
    init(): void;
    previewOnly(): void;
    showToolbar(): void;
    isHasLevel2Menu(name: any): string[];
    isHasConfigMenu(name: any): any[];
    /**
     * 判断是否有子菜单，目前有两种子菜单配置方式：1、通过`subMenuConfig`属性 2、通过`buttonConfig`配置属性
     * @param {string} name
     * @returns {boolean} 是否有子菜单
     */
    isHasSubMenu(name: string): boolean;
    /**
     * 根据配置画出来一级工具栏
     */
    drawMenus(): void;
    drawSubMenus(name: any): void;
    onClick(event: any, name: any, focusEvent?: boolean): void;
    toggleSubMenu(name: any): void;
    /**
     * 隐藏所有的二级菜单
     */
    hidAlleSubMenu(): void;
    /**
     * 收集快捷键
     */
    collectShortcutKey(): void;
    collectToolbarHandler(): void;
    /**
     * 监测是否有对应的快捷键
     * @param {KeyboardEvent} evt keydown 事件
     * @returns {boolean} 是否有对应的快捷键
     */
    matchShortcutKey(evt: KeyboardEvent): boolean;
    /**
     * 触发对应快捷键的事件
     * @param {KeyboardEvent} evt
     */
    fireShortcutKey(evt: KeyboardEvent): void;
    /**
     * 格式化当前按键，mac下的command按键转换为ctrl
     * @param {KeyboardEvent} event
     * @returns
     */
    getCurrentKey(event: KeyboardEvent): string;
}
import HookCenter from "./HookCenter";
