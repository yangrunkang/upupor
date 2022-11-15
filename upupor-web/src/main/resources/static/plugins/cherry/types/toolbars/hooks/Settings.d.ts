/**
 * 设置按钮
 */
export default class Settings extends MenuBase {
    /**
     * TODO: 需要优化参数传入方式
     */
    constructor($cherry: any);
    engine: any;
    instanceId: any;
    shortcutKeyMaps: {
        shortKey: string;
        shortcutKey: string;
    }[];
    /**
     * 切换预览按钮
     * @param {boolean} isOpen 预览模式是否打开
     */
    togglePreviewBtn(isOpen: boolean): void;
    /**
     * 绑定预览事件
     */
    attachEventListeners(): void;
    /**
     * 解析快捷键
     * @param {string} shortcutKey 快捷键
     * @returns
     */
    matchShortcutKey(shortcutKey: string): string;
    /**
     * 切换Toolbar显示状态
     */
    toggleToolbar(): void;
}
import MenuBase from "@/toolbars/MenuBase";
