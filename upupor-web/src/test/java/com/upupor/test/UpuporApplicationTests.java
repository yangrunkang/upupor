package com.upupor.test;


import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.scheduled.DetectUserOperationScheduled;
import com.upupor.service.scheduled.GenerateSiteMapScheduled;
import com.upupor.service.service.ContentService;
import com.upupor.service.utils.AvatarHelper;
import com.upupor.service.utils.CcEmailUtils;
import com.upupor.service.utils.CcUtils;
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
     * 邮件发送测试
     */
    @Test
    void testSendEmail() {
        CcEmailUtils.sendEmail("1743703238@qq.com", "使用阿里云测试邮件发送", null, "<h1>我是邮件正文</h1>");
    }

    /**
     * 邮件发送测试
     */
    private final DetectUserOperationScheduled detectUserOperationScheduled;
    @Test
    void detectUserOperationScheduled() {
        detectUserOperationScheduled.detectUserNotLogin();
    }

    /**
     * 测试使用模板发送邮件
     */
    @Test
    void testUseTemplateToSendEmail() {
        String code = "6677YangrunKang";
        Map<String, Object> maps = new HashMap<>();
        maps.put(CcTemplateConstant.TITLE, "注册邮件测试");
        maps.put(CcTemplateConstant.CONTENT, "341754");
        String render = HtmlTemplateUtils.renderEmail(CcTemplateConstant.TEMPLATE_EMAIL, maps);
        CcEmailUtils.sendEmail("1743703238@qq.com", "使用阿里云测试邮件发送", null, render);
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
                    "VALUES ( '" + CcUtils.getUuId() + "', '20012616420235183104', 0, '注册成功" + CcUtils.getUuId() + "', 0, 1352345, '2020-02-05 18:33:19');";
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
            System.out.println("{language: '"+s.trim()+"', label: '"+s.trim()+"', class: 'language-"+s.trim()+"'},");

        }

    }

    public static final String s ="# /icons/system/\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/empty.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/email.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/nickname.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/register-time.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/jifen2.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/fans.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/attention.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/profile.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/apply.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/manage3.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/message.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/comment.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/goutong.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/collection.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/settings.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/liulan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/content-time.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/undo.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/draft.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/message.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/jifen2.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/upload-radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/liulan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/content-time.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/login.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/message.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/register.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/zhishi.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/zhishi.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/liulan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/comment-yellow.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/content-time.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/zan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/collection.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/zan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/collection.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/goutong.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/undo.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/draft.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/topic.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/liulan.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/comment-yellow.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/content-time.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/goutong.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/jifen2.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/rule.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/topic.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/recommand.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/radio.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/jifen2.svg\n" +
            "\n" +
            "\n" +
            "# /icons/system/footer/\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/3361607101759_.pic_hd.jpg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/vision.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/brand-story.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/teamwork.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/thanks.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/design.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/business-co.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/ad.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/zhiding.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/wechat.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/sina.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/telegram.svg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/wechat.svg\n" +
            "\n" +
            "\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/team.jpeg\n" +
            "\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/all.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/topic.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-tech.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-share.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-work.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-record.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-qa.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/without_comment.svg\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/pages/business-co.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/qianjiajia.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/xiaoguanglin.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/wujinyi.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/wuruifen.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/liyanggyang.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/iconOne.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/iconSecond.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/iconThird.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/logoOne.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/logoSecond.png\n" +
            "\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-content.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-tech.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-qa.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-share.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-work.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-record.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-content.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/uricon.ico\" />\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/upupor-wechat.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/wechat.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/footer/3361607101759_.pic_hd.jpg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/upupor-weibo.jpg\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/qa_share_workplace.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/top.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/asserts/bottom.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/LinkedIn.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/weibo.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/qq_zone.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/facebook.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/twitter.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/goutong.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/home.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/system/email.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/js/cropper.min.css\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/js/cropper.min.js\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/system/profile-photo.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/home.png\n" +
            "\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-tech.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-qa.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-share.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-work.png\n" +
            "https://upupor-img.oss-cn-hangzhou.aliyuncs.com/icons/write/write-record.png\n";
    public static void removeRepeate() {
        SortedSet set = new TreeSet<String>();
        for(String st :s.split("\n")){
            set.add(st);
        }

        Iterator it = set.iterator();
        while (it.hasNext()){
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



}
