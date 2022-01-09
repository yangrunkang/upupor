export default class Suggester extends SyntaxBase {
    constructor({ config }: {
        config: any;
    });
    initConfig(config: any): void;
    suggester: {};
    toHtml(wholeMatch: any, leadingChar: any, keyword: any, text: any): any;
}
import SyntaxBase from "@/core/SyntaxBase";
