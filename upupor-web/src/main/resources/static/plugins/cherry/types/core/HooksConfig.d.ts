export default hooksConfig;
/**
 * 引擎各语法的配置
 * 主要决定支持哪些语法，以及各语法的执行顺序
 */
declare const hooksConfig: (typeof Strikethrough | typeof CodeBlock | typeof InlineCode | typeof Link | typeof Emphasis | typeof Header | typeof Transfer | typeof Table | typeof Br | typeof Hr | typeof Image | typeof List | typeof AutoLink | typeof MathBlock | typeof InlineMath | typeof Toc | typeof Footnote | typeof CommentReference | typeof HtmlBlock | typeof Emoji | typeof Underline | typeof Suggester)[];
import Strikethrough from "./hooks/Strikethrough";
import CodeBlock from "./hooks/CodeBlock";
import InlineCode from "./hooks/InlineCode";
import Link from "./hooks/Link";
import Emphasis from "./hooks/Emphasis";
import Header from "./hooks/Header";
import Transfer from "./hooks/Transfer";
import Table from "./hooks/Table";
import Br from "./hooks/Br";
import Hr from "./hooks/Hr";
import Image from "./hooks/Image";
import List from "./hooks/List";
import AutoLink from "./hooks/AutoLink";
import MathBlock from "./hooks/MathBlock";
import InlineMath from "./hooks/InlineMath";
import Toc from "./hooks/Toc";
import Footnote from "./hooks/Footnote";
import CommentReference from "./hooks/CommentReference";
import HtmlBlock from "./hooks/HtmlBlock";
import Emoji from "./hooks/Emoji";
import Underline from "./hooks/Underline";
import Suggester from "./hooks/Suggester";
