export default class Suggester extends SyntaxBase {
    constructor({ config }: {
        config: any;
    });
    initConfig(config: any): void;
    suggester: {};
    toHtml(str: any): any;
}
import SyntaxBase from "@/core/SyntaxBase";
