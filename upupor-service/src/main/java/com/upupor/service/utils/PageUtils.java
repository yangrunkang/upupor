/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.utils;

import com.github.pagehelper.PageInfo;
import com.upupor.service.common.CcConstant;
import joptsimple.internal.Strings;

import java.util.Objects;

/**
 * 分页工具
 *
 * @author YangRunkang(cruise)
 * @date 2021/01/08 19:59
 */
public class PageUtils {
    public static void main(String[] args) {
        // 数据总条数
        Integer totalPageSize = 83;
        // 用户翻页
        Integer userPageIndex = 1;

        Page page = new Page(totalPageSize, userPageIndex);
        System.out.println(page.exportPaginationHtml());
//        System.out.println(Integer.MAX_VALUE);
//        System.out.println(Math.toIntExact(Integer.MAX_VALUE));
    }


    /**
     * 写这个的原因是为了IE上能显示分页
     *
     * @param pageInfo
     * @return
     */
    public static String paginationHtml(PageInfo pageInfo) {
        if (Objects.isNull(pageInfo)) {
            return Strings.EMPTY;
        }
        Long total = pageInfo.getTotal();
        Integer userCurrentPage = pageInfo.getPageNum();

        if (Objects.isNull(total) || total <= 0) {
            return Strings.EMPTY;
        }
        Page page = new Page(Math.toIntExact(total), userCurrentPage);
        return page.exportPaginationHtml();
    }

    public static String paginationHtmlForComment(Long total, Integer userCurrentPage, Integer pageSize) {
        if (Objects.isNull(total) || total <= 0) {
            return Strings.EMPTY;
        }
        Page page = new Page(Math.toIntExact(total), userCurrentPage, pageSize);
        return page.exportPaginationHtml();
    }

    public static Integer calcMaxPage(Integer totalSize, Integer pageSize) {
        int pageNumber = totalSize / pageSize;
        return totalSize % pageSize == 0 ? pageNumber : pageNumber + 1;
    }

    /**
     * 页
     */
    public static class Page {
        /**
         * 显示的分页按钮数
         */
        private final Integer pageBtnNum = 6;
        /**
         * 总数据数
         */
        private Integer totalSize = 0;
        /**
         * 当前页面
         */
        private Integer currentPage = 0;
        /**
         * 每页条数
         */
        private Integer pageSize = CcConstant.Page.SIZE;


        public Page(Integer totalSize, Integer currentPage) {
            init(totalSize, currentPage);
        }

        public Page(Integer totalSize, Integer currentPage, Integer commentPageSize) {
            this.pageSize = commentPageSize;
            init(totalSize, currentPage);
        }

        public Page() {
        }

        /**
         * 初始化
         *
         * @param totalSize
         * @param currentPage
         */
        private void init(Integer totalSize, Integer currentPage) {
            this.totalSize = totalSize;
            this.currentPage = currentPage;
            if (Objects.isNull(currentPage) || currentPage <= 0) {
                this.currentPage = 1;
            }

            if (Objects.isNull(currentPage) || currentPage >= effectivePageNumber()) {
                this.currentPage = effectivePageNumber();
            }
        }

        /**
         * 输出分页的html代码
         *
         * @return
         */
        private String exportPaginationHtml() {
            StartEndPageNum startEnd = getStartEnd();
            Integer start = startEnd.getStart();
            Integer end = startEnd.getEnd();
            if (startEnd.same()) {
                return renderEmpty();
            }


            // 如果结束页码减去起始页码不足系统默认展示的按钮数
            // 6 7 8 9*  9-6=3  3+1 才是4个按钮
            int btnNum = end - start + 1;
            if (btnNum < pageBtnNum) {
                int i = end - pageBtnNum + 1;
                int absi = 0;
                if (i <= 0) {
                    i = 1;
                    absi = Math.abs(i) + 1;
                }
                int endPage = end + absi;

                if (endPage >= effectivePageNumber()) {
                    endPage = effectivePageNumber();
                }
                return render(i, endPage);
            }

            return render(start, end);
        }

        private String renderEmpty() {
            return Strings.EMPTY;
        }

        /*


         */
        private String render(Integer start, Integer end) {

            String aTagClass = "page-link cv-link cv-font-sm text-secondary ";
            String aTagActiveClass = aTagClass + " cv-active-page ";
            String pageParm = "?pageNum=";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<ul class=\"pagination\">\n");
            stringBuilder.append("\t<li class=\"page-item\"><a class=\"" + aTagClass + "\" href=\"" + pageParm + "1\">首页</a></li>\n");
            for (int page = start; page <= end; page++) {
                if (currentPage == page) {
                    stringBuilder.append("\t<li class=\"page-item\"><a class=\"" + aTagActiveClass + "\" href=\"" + pageParm + "" + page + "\">" + page + "</a></li>\n");
                } else {
                    stringBuilder.append("\t<li class=\"page-item\"><a class=\"" + aTagClass + "\" href=\"" + pageParm + "" + page + "\">" + page + "</a></li>\n");
                }
            }
            stringBuilder.append("\t<li class=\"page-item\"><a class=\"" + aTagClass + "\" href=\"" + pageParm + "" + effectivePageNumber() + "\">末页</a></li>\n");
            stringBuilder.append("\t<li class=\"page-item disabled d-none d-sm-block\"><a class=\"" + aTagClass + "\" >总数 " + totalSize + "</a></li>\n");
            stringBuilder.append("</ul>");
            return stringBuilder.toString();
        }

        /**
         * 获取组件渲染的开始和结束页码
         */
        private StartEndPageNum getStartEnd() {
            Integer effectivePageNumber = effectivePageNumber();
            if (effectivePageNumber == 1) {
                return new StartEndPageNum();
            }


            // 有效页码小于系统默认显示的按钮数
            if (effectivePageNumber <= pageBtnNum) {
                return new StartEndPageNum(1, effectivePageNumber);
            }

            // 用户未翻页
            if (currentPage == 1) {
                return new StartEndPageNum(1, pageBtnNum);
            }

            StartEndPageNum startEndPageNum = new StartEndPageNum();

            int halfPageBtn = pageBtnNum / 2;

            // 设定开始页
            int up = currentPage - halfPageBtn;
            if (up <= 0) {
                int i = currentPage - Math.abs(up) + 1;
                startEndPageNum.setStart(i);
            } else {
                startEndPageNum.setStart(up);
            }

            // 设定结束页
            int last = currentPage + halfPageBtn;
            if (last >= effectivePageNumber) {
                startEndPageNum.setEnd(effectivePageNumber);
            } else {
                startEndPageNum.setEnd(last - 1);
            }
            return startEndPageNum;
        }

        /**
         * 有效页码数
         *
         * @return
         */
        private Integer effectivePageNumber() {
            int pageNumber = totalSize / pageSize;
            return totalSize % pageSize == 0 ? pageNumber : pageNumber + 1;
        }

        public Integer getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(Integer totalSize) {
            this.totalSize = totalSize;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }
    }

    private static class StartEndPageNum {
        private Integer start;
        private Integer end;

        public StartEndPageNum(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }

        public StartEndPageNum() {
            this.start = this.end = 1;
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getEnd() {
            return end;
        }

        public void setEnd(Integer end) {
            this.end = end;
        }

        public Boolean same() {
            return this.start.equals(this.end);
        }
    }

}
