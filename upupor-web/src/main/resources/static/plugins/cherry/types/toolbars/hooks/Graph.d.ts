/**
 * 插入“画图”的按钮
 * 本功能依赖[Mermaid.js](https://mermaid-js.github.io)组件，请保证调用CherryMarkdown前已加载mermaid.js组件
 */
export default class Graph extends MenuBase {
    constructor($cherry: any);
    /**
     * 画图的markdown源码模版
     * @param {string} type 画图的类型
     * @returns
     */
    $getSampleCode(type: string): any;
}
import MenuBase from "@/toolbars/MenuBase";
