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

-- 添加索引
# CREATE INDEX index_content_content_id ON content(content_id);
# CREATE INDEX index_content_title ON content(title);
# CREATE INDEX index_content_extend_content_id ON content_extend(content_id);
# CREATE INDEX index_content_tag_id ON content(tag_ids);
# CREATE INDEX index_content_data_id ON content_data(content_id);
# CREATE INDEX index_content_edit_reason_content_id ON content_edit_reason(content_id);
# CREATE INDEX index_comment_comment_id ON comment(comment_id);
# CREATE INDEX index_comment_target_id ON comment(target_id);
# CREATE INDEX index_tag_tag_id ON tag(tag_id);
# CREATE INDEX index_tag_tag_name ON tag(tag_name);
# CREATE INDEX index_tag_in_tag_id ON tag_in(tag_id);
# CREATE INDEX index_tag_in_tag_name ON tag_in(tag_name);
# CREATE INDEX index_tag_in_parent_tag_id ON tag_in(parent_tag_id);
# CREATE INDEX index_member_user_id ON member(user_id);
# CREATE INDEX index_member_extend_user_id ON member_extend(user_id);
# CREATE INDEX index_member_integral_integral_user_id ON member_integral(integral_user_id);