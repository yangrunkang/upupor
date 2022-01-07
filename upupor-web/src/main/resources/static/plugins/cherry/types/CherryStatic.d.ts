export class CherryStatic {
    static createSyntaxHook: typeof createSyntaxHook;
    static createMenuHook: typeof createMenuHook;
    static constants: {
        HOOKS_TYPE_LIST: import("./core/SyntaxBase").HookTypesList;
    };
    static VERSION: string;
    /**
     * @this {typeof import('./Cherry').default | typeof CherryStatic}
     * @param {{ install: (defaultConfig: any, ...args: any[]) => void }} PluginClass 插件Class
     * @param  {...any} args 初始化插件的参数
     * @returns
     */
    static usePlugin(PluginClass: {
        install: (defaultConfig: any, ...args: any[]) => void;
    }, ...args: any[]): void;
}
import { createSyntaxHook } from "./Factory";
import { createMenuHook } from "./Factory";
