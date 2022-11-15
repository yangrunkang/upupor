/**
 * 插入1级~5级标题
 */
export default class Header extends MenuBase {
    constructor($cherry: any);
    /**
     * 解析快捷键，判断插入的标题级别
     * @param {string} shortKey 快捷键
     * @returns
     */
    $getFlagStr(shortKey: string): string;
    $testIsHead(selection: any): boolean;
}
import MenuBase from "@/toolbars/MenuBase";
