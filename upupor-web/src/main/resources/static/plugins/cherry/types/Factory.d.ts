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
        subMenuConfig: any;
        onClick(...args: any[]): any;
        readonly shortcutKeys: any;
        _onClick: (event: MouseEvent) => void;
        bubbleMenu: boolean;
        subMenu: import("./toolbars/MenuBase").SubMenu;
        name: string;
        editor: import("./Editor").default;
        dom: HTMLSpanElement;
        updateMarkdown: boolean;
        positionModel: "fixed" | "absolute";
        $onClick(event: MouseEvent): void;
        getSubMenuConfig(): any[];
        setName(name: string, iconName?: string): void;
        iconName: string;
        createBtn(): HTMLSpanElement;
        onKeyDown(codemirror: import("codemirror").Editor, selections: string[], key: string): void;
        bindSubClick(shortcut: any, selection: any): any;
        shortcutKey(options: any): any;
        initSubMenu(): void;
        showSubMenu(): void;
        hideSubMenu(): void;
        toggleSubMenu(): void;
        onSubClick(clickEventHandler: any, async: any, event: any): void;
    };
    cleanSubMenu(): void;
    hideSubMenuExcept(name: string): void;
};
