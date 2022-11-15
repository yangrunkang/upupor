export default class Size extends MenuBase {
    constructor($cherry: any);
    shortKeyMap: {
        'Alt-1': string;
        'Alt-2': string;
        'Alt-3': string;
        'Alt-4': string;
    };
    _getFlagStr(shortKey: any): string;
    $testIsSize(selection: any): boolean;
    $getSizeByShortKey(shortKey: any): any;
}
import MenuBase from "@/toolbars/MenuBase";
