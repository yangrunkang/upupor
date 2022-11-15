export function createSyntaxHook(name: any, type: any, options: any): {
    new (editorConfig?: {}): {
        config: any;
        beforeMakeHtml(...args: any[]): any;
        makeHtml(...args: any[]): any;
        afterMakeHtml(...args: any[]): any;
        test(...args: any[]): any;
        rule(...args: any[]): any;
    };
    HOOK_NAME: any;
};
export function createMenuHook(name: any, options: any): {
    new (editorInstance: any): {
        noIcon: boolean;
        subMenuConfig: any;
        onClick(...args: any[]): any;
        readonly shortcutKeys: any;
        _onClick: (event?: MouseEvent | KeyboardEvent, shortKey?: string) => void;
        $cherry: any;
        bubbleMenu: boolean;
        subMenu: any;
        name: string;
        editor: any;
        dom: HTMLSpanElement;
        updateMarkdown: boolean;
        cacheOnce: boolean;
        positionModel: "fixed" | "absolute";
        fire(event?: MouseEvent | KeyboardEvent, shortKey?: string): void;
        getSubMenuConfig(): any[];
        setName(name: string, iconName?: string): void;
        iconName: string;
        setCacheOnce(info: any): void;
        getAndCleanCacheOnce(): boolean;
        hasCacheOnce(): boolean;
        createBtn(asSubMenu?: boolean): HTMLSpanElement;
        createSubBtnByConfig(config: any): HTMLSpanElement;
        isSelections: boolean;
        $getSelectionRange(): {
            begin: any;
            end: any;
        };
        registerAfterClickCb(cb: Function): void;
        afterClickCb: Function;
        $afterClick(): void;
        setLessSelection(lessBefore: string, lessAfter: string): void;
        getMoreSelection(appendBefore?: string, appendAfter?: string, cb?: Function): void;
        getSelection(selection: string, type?: string, focus?: boolean): string;
        bindSubClick(shortcut: any, selection: any): void;
        getMenuPosition(): Pick<DOMRect, "left" | "top" | "width" | "height">;
    };
};
