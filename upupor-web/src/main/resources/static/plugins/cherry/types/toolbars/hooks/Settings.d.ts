/**
 * 设置按钮
 */
export default class Settings extends MenuBase {
    /**
     * TODO: 需要优化参数传入方式
     * @param {Object} editor 编辑器实例
     * @param {Object} engine 引擎实例
     */
    constructor(editor: any, engine: any);
    engine: any;
    instanceId: any;
    /**
     * 切换预览按钮
     * @param {boolean} isOpen 预览模式是否打开
     */
    togglePreviewBtn(isOpen: boolean): void;
    /**
     * 绑定预览事件
     */
    attachEventListeners(): void;
}
import MenuBase from "@/toolbars/MenuBase";
