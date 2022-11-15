/**
 * 插入字体颜色或者字体背景颜色的按钮
 */
export default class Color extends MenuBase {
    constructor($cherry: any);
    bubbleColor: BubbleColor;
    $testIsColor(type: any, selection: any): boolean;
}
import MenuBase from "@/toolbars/MenuBase";
/**
 * 调色盘
 */
declare class BubbleColor {
    constructor($cherry: any);
    editor: any;
    /**
     * 定义调色盘每个色块的颜色值
     */
    colorStack: string[];
    /**
     * 用来暂存选中的内容
     * @param {string} selection 编辑区选中的文本内容
     */
    setSelection(selection: string): void;
    selection: string;
    getFontColorDom(title: any): string;
    getDom(): HTMLDivElement;
    init(): void;
    dom: HTMLDivElement;
    onClick(): string;
    initAction(): void;
    colorValue: string;
    type: string;
    /**
     * 在对应的坐标展示调色盘
     * @param {Object} 坐标
     */
    show({ left, top, $color }: any): void;
    $color: any;
}
export {};
