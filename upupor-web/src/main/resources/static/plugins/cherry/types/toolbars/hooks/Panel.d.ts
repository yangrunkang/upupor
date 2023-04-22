/**
 * 插入面板
 */
export default class Panel extends MenuBase {
    constructor($cherry: any);
    panelRule: any;
    /**
     * 从字符串中找打面板的name
     * @param {string} str
     * @returns {string | false}
     */
    $getNameFromStr(str: string): string | false;
    $getTitle(str: any): string;
}
import MenuBase from "@/toolbars/MenuBase";
