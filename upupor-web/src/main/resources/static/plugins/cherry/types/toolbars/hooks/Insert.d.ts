/**
 * "插入"按钮
 */
export default class Insert extends MenuBase {
    constructor($cherry: any);
    subBubbleTableMenu: BubbleTableMenu;
    /**
     * 上传文件的逻辑
     * @param {string} type 上传文件的类型
     */
    handleUpload(type?: string): void;
}
import MenuBase from "@/toolbars/MenuBase";
import BubbleTableMenu from "@/toolbars/BubbleTable";
