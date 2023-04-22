/**
 * 面板语法
 * 例：
 *  :::tip
 *  这是一段提示信息
 *  :::
 *  :::warning
 *  这是一段警告信息
 *  :::
 *  :::danger
 *  这是一段危险信息
 *  :::
 */
export default class Panel extends ParagraphBase {
    constructor(options: any);
    $getPanelInfo(name: any, str: any, sentenceMakeFunc: any): {
        type: string;
        title: any;
        body: any;
    };
    $getTitle(name: any): any;
    $getTargetType(name: any): "success" | "primary" | "info" | "warning" | "danger";
}
import ParagraphBase from "@/core/ParagraphBase";
