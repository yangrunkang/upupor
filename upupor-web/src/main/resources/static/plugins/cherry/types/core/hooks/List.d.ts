export function makeChecklist(text: any): any;
export default class List extends ParagraphBase {
    constructor({ config }: {
        config: any;
    });
    config: any;
    tree: any[];
    emptyLines: number;
    indentSpace: number;
    addNode(node: any, current: any, parent: any, last: any): void;
    buildTree(html: any, sentenceMakeFunc: any): void;
    renderSubTree(node: any, children: any, type: any): string;
    renderTree(current: any): string;
}
import ParagraphBase from "@/core/ParagraphBase";
