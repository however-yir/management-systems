package io.howeveryir.talentflow.utils;

import io.howeveryir.talentflow.model.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/however-yir
 * @博客 http://wangsong.blog.csdn.net
 * @网站 https://github.com/however-yir
 * @时间 2019-10-24 8:11
 */
public class HrUtils {
    public static Hr getCurrentHr() {
        return ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
