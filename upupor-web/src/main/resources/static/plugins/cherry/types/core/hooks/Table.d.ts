export default class Table extends ParagraphBase {
    constructor({ externals, config }: {
        externals: any;
        config: any;
    });
    chartRenderEngine: any;
    $extendColumns(row: any, colCount: any): any;
    $parseChartOptions(cell: any): {
        type: any;
        options: any;
    };
    $parseColumnAlignRules(row: any): {
        textAlignRules: any;
        COLUMN_ALIGN_MAP: {
            L: string;
            R: string;
            C: string;
        };
    };
    $parseTable(lines: any, sentenceMakeFunc: any, dataLines: any): {
        html: string;
        sign: any;
    };
    $renderTable(COLUMN_ALIGN_MAP: any, tableHeader: any, tableRows: any, dataLines: any): {
        html: string;
        sign: any;
    };
}
import ParagraphBase from "@/core/ParagraphBase";
