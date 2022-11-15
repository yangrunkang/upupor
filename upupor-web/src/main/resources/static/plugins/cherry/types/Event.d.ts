declare var _default: {
    /**
     * 事件列表
     * @property
     */
    Events: {
        previewerClose: string;
        previewerOpen: string;
        editorClose: string;
        editorOpen: string;
        toolbarHide: string;
        toolbarShow: string;
        cleanAllSubMenus: string;
    };
    /**
     * @property
     * @private
     * @type {import('mitt').Emitter}
     */
    emitter: import("mitt").Emitter<any>;
    /**
     * 注册监听事件
     * @param {string} instanceId 接收消息的频道
     * @param {string} event 要注册监听的事件
     * @param {(event: any) => void} handler 事件回调
     */
    on(instanceId: string, event: string, handler: (event: any) => void): void;
    /**
     * 触发事件
     * @param {string} instanceId 发送消息的频道
     * @param {string} event 要触发的事件
     */
    emit(instanceId: string, event: string): void;
};
export default _default;
