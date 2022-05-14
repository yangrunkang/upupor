export default tableContentHander;
declare namespace tableContentHander {
    namespace tableEditor {
        const info: {};
        const tableCodes: any[];
        const editorDom: {};
    }
    function emit(type: any, event?: {}, callback?: () => void): void;
    function emit(type: any, event?: {}, callback?: () => void): void;
    function $tryRemoveMe(event: any, callback: any): void;
    function $tryRemoveMe(event: any, callback: any): void;
    /**
     * 获取目标dom的位置信息和尺寸信息
     */
    function $getTdPosition(): {
        top: number;
        height: any;
        width: any;
        left: number;
        maxHeight: any;
    };
    /**
     * 获取目标dom的位置信息和尺寸信息
     */
    function $getTdPosition(): {
        top: number;
        height: any;
        width: any;
        left: number;
        maxHeight: any;
    };
    function $setInputOffset(): void;
    function $setInputOffset(): void;
    function $remove(): void;
    function $remove(): void;
    /**
     * 收集编辑器中的表格语法，并记录表格语法的开始的offset
     */
    function $collectTableCode(): void;
    /**
     * 收集编辑器中的表格语法，并记录表格语法的开始的offset
     */
    function $collectTableCode(): void;
    /**
     * 获取预览区域被点击的table对象，并记录table的顺位
     */
    function $collectTableDom(): boolean;
    /**
     * 获取预览区域被点击的table对象，并记录table的顺位
     */
    function $collectTableDom(): boolean;
    /**
     * 选中对应单元格、所在行、所在列的内容
     * @param {Number} index
     * @param {String} type 'td': 当前单元格, 'table': 当前表格
     */
    function $setSelection(index: number, type?: string): void;
    /**
     * 选中对应单元格、所在行、所在列的内容
     * @param {Number} index
     * @param {String} type 'td': 当前单元格, 'table': 当前表格
     */
    function $setSelection(index: number, type?: string): void;
    /**
     * 获取对应单元格的偏移量
     * @param {String} tableCode
     * @param {Boolean} isTHead
     * @param {Number} trIndex
     * @param {Number} tdIndex
     */
    function $getTdOffset(tableCode: string, isTHead: boolean, trIndex: number, tdIndex: number): {
        preLine: number;
        preCh: number;
        plusCh: number;
        currentTd: string;
    };
    /**
     * 获取对应单元格的偏移量
     * @param {String} tableCode
     * @param {Boolean} isTHead
     * @param {Number} trIndex
     * @param {Number} tdIndex
     */
    function $getTdOffset(tableCode: string, isTHead: boolean, trIndex: number, tdIndex: number): {
        preLine: number;
        preCh: number;
        plusCh: number;
        currentTd: string;
    };
    /**
     * 在编辑器里找到对应的表格源码，并让编辑器选中
     */
    function $findTableInEditor(): boolean;
    /**
     * 在编辑器里找到对应的表格源码，并让编辑器选中
     */
    function $findTableInEditor(): boolean;
    function $initReg(): void;
    function $initReg(): void;
    function showBubble(currentElement: any, container: any, previewerDom: any, codeMirror: any): void;
    function showBubble(currentElement: any, container: any, previewerDom: any, codeMirror: any): void;
    /**
     * 判断是否处于编辑状态
     * @returns {boolean}
     */
    function $isEditing(): boolean;
    /**
     * 判断是否处于编辑状态
     * @returns {boolean}
     */
    function $isEditing(): boolean;
    /**
     * 把表格上的input单行文本框画出来
     */
    function $drawEditor(): void;
    /**
     * 把表格上的input单行文本框画出来
     */
    function $drawEditor(): void;
    function $onInputChange(e: any): void;
    function $onInputChange(e: any): void;
    /**
     * 更新编辑器的位置（尺寸和位置）
     */
    function $updateEditorPosition(): void;
    /**
     * 更新编辑器的位置（尺寸和位置）
     */
    function $updateEditorPosition(): void;
    function $getClosestNode(node: any, targetNodeName: any): any;
    function $getClosestNode(node: any, targetNodeName: any): any;
}
