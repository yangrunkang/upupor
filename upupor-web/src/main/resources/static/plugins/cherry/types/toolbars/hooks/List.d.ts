/**
 * 插入有序/无序/checklist列表的按钮
 */
export default class List extends MenuBase {
    constructor($cherry: any);
    /**
     * 处理编辑区域有选中文字时的操作
     * @param {string} selection 编辑区选中的文本内容
     * @param {string} type 操作类型：ol 有序列表，ul 无序列表，checklist 检查项
     * @returns {string} 对应的markdown源码
     */
    $dealSelection(selection: string, type: string): string;
}
import MenuBase from "@/toolbars/MenuBase";
