/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.test;


import com.upupor.framework.utils.CcUtils;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.data.dao.mapper.ContentExtendMapper;
import com.upupor.service.data.dao.mapper.ContentMapper;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.scheduled.DetectUserOperationScheduled;
import com.upupor.service.scheduled.GenerateSiteMapScheduled;
import com.upupor.service.utils.AvatarHelper;
import com.upupor.service.utils.CcEmailUtils;
import com.upupor.service.utils.HtmlTemplateUtils;
import com.upupor.web.UpuporWebApplication;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.util.*;


@SpringBootTest(classes = UpuporWebApplication.class)
@RequiredArgsConstructor
public class UpuporApplicationTests {

    private final ContentService contentService;

    private final GenerateSiteMapScheduled generateSiteMapScheduled;

    @Test
    void testDeadEvent() {
        //
        try {
//            eventPublisher.publishEvent(new UpuporApplicationTests());
//            Thread.sleep(4000*9);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * ??????????????????
     */
    @Test
    void testSendEmail() {
        CcEmailUtils.sendEmail("1743703238@qq.com", "?????????????????????????????????", null, "<h1>??????????????????</h1>");
    }

    /**
     * ??????????????????
     */
    private final DetectUserOperationScheduled detectUserOperationScheduled;

    @Test
    void detectUserOperationScheduled() {
        detectUserOperationScheduled.detectUserNotLogin();
    }

    /**
     * ??????????????????????????????
     */
    @Test
    void testUseTemplateToSendEmail() {
        String code = "6677YangrunKang";
        Map<String, Object> maps = new HashMap<>();
        maps.put(CcTemplateConstant.TITLE, "??????????????????");
        maps.put(CcTemplateConstant.CONTENT, "341754");
        String render = HtmlTemplateUtils.renderEmail(CcTemplateConstant.TEMPLATE_EMAIL, maps);
        CcEmailUtils.sendEmail("1743703238@qq.com", "?????????????????????????????????", null, render);
    }

    @Test
    void testUseTemplateGoogleSeoSiteMap() {
        generateSiteMapScheduled.scheduled();
    }


    @Test
    void uplaodOss() throws FileNotFoundException {
    }

    public static void renderCssf() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 123; i++) {
            String f = "INSERT INTO `dev_codingvcr`.`message`( `message_id`, `user_id`, `message_type`, `message`, `status`, `create_time`, `sys_update_time`) " +
                    "VALUES ( '" + CcUtils.getUuId() + "', '20012616420235183104', 0, '????????????" + CcUtils.getUuId() + "', 0, 1352345, '2020-02-05 18:33:19');";
//            String f = CcUtils.getUuId();
            sb.append(f + "\n");
        }
        System.out.println(sb.toString());
    }

    public static void renderCss() {
        String f = "markup+css+clike+javascript+applescript+aspnet+bash+basic+c+csharp+cpp+coffeescript+d+django+docker+erlang+ftl+git+go+groovy+j+java+jq+json+jsonp+json5+kotlin+latex+less+lisp+lua+markup-templating+neon+nginx+perl+php+php-extras+powershell+r+ruby+rust+sass+scss+scala+sql+tap+vim+visual-basic+wasm+yaml";

        String[] split = f.split("\\+");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            System.out.println("{language: '" + s.trim() + "', label: '" + s.trim() + "', class: 'language-" + s.trim() + "'},");

        }

    }

    public static final String s = "# /icons/system/";

    public static void removeRepeate() {
        SortedSet set = new TreeSet<String>();
        for (String st : s.split("\n")) {
            set.add(st);
        }

        Iterator it = set.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

    }


    public static void main(String[] args) {
        removeRepeate();
    }


    @Test
    public void testUploadUserNamePhoto() throws Exception {
        System.out.println(AvatarHelper.generateAvatar(1));
    }


    private final ContentMapper contentMapper;

    private final ContentExtendMapper contentExtendMapper;

}
