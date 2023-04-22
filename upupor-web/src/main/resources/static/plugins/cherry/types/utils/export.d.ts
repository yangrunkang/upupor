/**
 * 利用window.print导出成PDF
 * @param {HTMLElement} previeweDom 预览区域的dom
 */
export function exportPDF(previeweDom: HTMLElement): void;
/**
 * 利用canvas将html内容导出成图片
 * @param {HTMLElement} previeweDom 预览区域的dom
 */
export function exportScreenShot(previeweDom: HTMLElement): void;
/**
 * 导出 markdown 文件
 * @param {String} markdownText markdown文本
 */
export function exportMarkdownFile(markdownText: string): void;
/**
 * 导出预览区 HTML 文件
 * @param {String} HTMLText HTML文本
 */
export function exportHTMLFile(HTMLText: string): void;
