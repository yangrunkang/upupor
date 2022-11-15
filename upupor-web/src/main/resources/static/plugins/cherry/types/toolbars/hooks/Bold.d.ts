/**
 * 加粗按钮
 */
export default class Bold extends MenuBase {
    constructor($cherry: any);
    /**
     * 是不是包含加粗语法
     * @param {String} selection
     * @returns {Boolean}
     */
    $testIsBold(selection: string): boolean;
}
import MenuBase from "@/toolbars/MenuBase";
