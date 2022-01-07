/**
 * 插入1级~5级标题
 */
export default class Header extends MenuBase {
    constructor(editor: any);
    /**
     * 解析快捷键，判断插入的标题级别
     * @param {string} shortKey 快捷键
     * @returns
     */
    $getFlagStr(shortKey: string): string;
}
import MenuBase from "@/toolbars/MenuBase";
