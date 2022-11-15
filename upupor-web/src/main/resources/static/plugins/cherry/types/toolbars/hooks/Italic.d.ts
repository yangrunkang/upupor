/**
 * 插入斜体的按钮
 */
export default class Italic extends MenuBase {
    constructor($cherry: any);
    /**
     * 是不是包含加粗语法
     * @param {String} selection
     * @returns {Boolean}
     */
    $testIsItalic(selection: string): boolean;
}
import MenuBase from "@/toolbars/MenuBase";
