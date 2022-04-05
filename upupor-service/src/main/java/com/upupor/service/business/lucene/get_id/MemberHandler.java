/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
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

package com.upupor.service.business.lucene.get_id;

import com.upupor.lucene.AbstractGetTargetId;
import com.upupor.service.common.CcResponse;
import com.upupor.service.dto.OperateMemberDto;

/**
 * 用户处理
 *
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 11:41
 * @email: yangrunkang53@gmail.com
 */
public class MemberHandler extends AbstractGetTargetId {

    @Override
    public String targetId() {
        CcResponse sourceData = (CcResponse) this.sourceData;
        if (sourceData.getData() instanceof OperateMemberDto) {
            return ((OperateMemberDto) sourceData.getData()).getMemberId();
        }
        return null;
    }
}
