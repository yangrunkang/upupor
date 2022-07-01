/**
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
export function compileRegExp(obj: any, flags: any, allowExtendedFlags: any): RegExp;
export function isLookbehindSupported(): boolean;
export function getTableRule(merge?: boolean): RegExp | {
    strict: {
        begin: string;
        content: string;
        end: string;
    };
    loose: {
        begin: string;
        content: string;
        end: string;
    };
};
export function getCodeBlockRule(): {
    /**
     * (?:^|\n)是区块的通用开头
     * (\n*)捕获区块前的所有换行
     * (?:[^\S\n]*)捕获```前置的空格字符
     */
    begin: RegExp;
    content: RegExp;
    end: RegExp;
};
export const HORIZONTAL_WHITESPACE: "[ \\t\\u00a0]";
export const ALLOW_WHITESPACE_MULTILINE: "(?:.*?)(?:(?:\\n.*?)*?)";
export const DO_NOT_STARTS_AND_END_WITH_SPACES: "(?:\\S|(?:\\S.*?\\S))";
export const DO_NOT_STARTS_AND_END_WITH_SPACES_MULTILINE: "(?:(?:\\S|(?:\\S[^\\n]*?\\S))(?:\\n(?:\\S|(?:\\S[^\\n]*?\\S)))*?)";
export const DO_NOT_STARTS_AND_END_WITH_SPACES_MULTILINE_ALLOW_EMPTY: "(?:(?:\\S|(?:\\S.*?\\S))(?:[ \\t]*\\n.*?)*?)";
export const NOT_ALL_WHITE_SPACES_INLINE: "(?:[^\\n]*?\\S[^\\n]*?)";
export const NORMAL_INDENT: "[ ]{0, 3}|\\t";
export const NO_BACKSLASH_BEFORE_CAPTURE: "[^\\\\]";
export const PUNCTUATION: "[\\u0021-\\u002F\\u003a-\\u0040\\u005b-\\u0060\\u007b-\\u007e]";
export const CHINESE_PUNCTUATION: "[！“”¥‘’（），。—：；《》？【】「」·～｜]";
export const UNDERSCORE_EMPHASIS_BOUNDARY: string;
export const EMAIL_INLINE: RegExp;
export const EMAIL: RegExp;
export const URL_INLINE_NO_SLASH: RegExp;
export const URL_INLINE: RegExp;
export const URL_NO_SLASH: RegExp;
export const URL: RegExp;
