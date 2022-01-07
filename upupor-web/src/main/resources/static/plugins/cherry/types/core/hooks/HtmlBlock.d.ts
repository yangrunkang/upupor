export default class HtmlBlock extends ParagraphBase {
    constructor();
    isAutoLinkTag(tagMatch: any): boolean;
    isHtmlComment(match: any): boolean;
    /**
     * @property
     * @type {false | RegExp}
     */
    htmlWhiteListAppend: false | RegExp;
    /**
     * @property
     * @type {string[]}
     */
    htmlWhiteList: string[];
}
import ParagraphBase from "@/core/ParagraphBase";
