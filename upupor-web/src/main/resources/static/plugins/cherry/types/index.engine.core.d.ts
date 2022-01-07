export default CherryEngine;
import SyntaxHookBase from "./core/SyntaxBase";
import MenuHookBase from "./toolbars/MenuBase";
declare class CherryEngine extends CherryStatic {
    /**
     * @private
     */
    private static initialized;
    /**
     * @readonly
     */
    static readonly config: {
        defaults: Partial<import("../types/cherry").CherryOptions>;
    };
    /**
     *
     * @param {any} options
     */
    constructor(options: any);
}
import { CherryStatic } from "./CherryStatic";
export { SyntaxHookBase, MenuHookBase };
