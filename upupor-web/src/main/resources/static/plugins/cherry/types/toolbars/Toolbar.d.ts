export default class Toolbar {
    constructor(options: any);
    /**
     * @property
     * @type {string} 实例ID
     */
    instanceId: string;
    options: {
        dom: HTMLDivElement;
        buttonConfig: string[];
        editor: {};
        extensions: any[];
        keysmap: {};
        engine: {};
        customMenu: any[];
    };
    init(): void;
    previewOnly(): void;
    showToolbar(): void;
    initExtension(): void;
    collectShortcutKey(): void;
    collectToolbarHandler(): void;
    toolbarHandlers: {};
    matchShortcutKey(evt: any): boolean;
    fireShortcutKey(evt: any, codemirror: any): void;
}
