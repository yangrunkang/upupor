export default class Image extends SyntaxBase {
    constructor({ config, globalConfig }: {
        config: any;
        globalConfig: any;
    });
    urlProcessor: any;
    extendMedia: {
        tag: string[];
        replacer: {
            video(match: any, leadingChar: any, alt: any, link: any, title: any, poster: any): any;
            audio(match: any, leadingChar: any, alt: any, link: any, title: any, poster: any): any;
        };
    };
    toHtml(match: any, leadingChar: any, alt: any, link: any, title: any, ref: any): any;
    toMediaHtml(match: any, leadingChar: any, mediaType: any, alt: any, link: any, title: any, ref: any, posterWrap: any, poster: any, ...args: any[]): any;
    testMedia(str: any): any;
}
import SyntaxBase from "@/core/SyntaxBase";
