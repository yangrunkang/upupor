/**
 * "插入"按钮
 */
export default class Insert extends MenuBase {
    constructor(editor: any, options: any, engine: any);
    subBubbleTableMenu: BubbleTableMenu;
    /**
     * 上传文件的逻辑
     * @param {string} type 上传文件的类型
     */
    handleUpload(type?: string): void;
    /**
     * 解析快捷键
     * @param {string} shortcutKey 快捷键
     * @returns
     */
    matchShortcutKey(shortcutKey: string): string;
    /**
     * 获得监听的快捷键
     * 根据系统字段监听Ctrl+*和 cmd+*
     */
    shortcutKeyMaps(): {
        shortKey: string;
        shortcutKey: string;
    }[];
}
import MenuBase from "@/toolbars/MenuBase";
import BubbleTableMenu from "@/toolbars/BubbleTable";
