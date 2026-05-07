package io.howeveryir.talentflow.controller.config;

import io.howeveryir.talentflow.model.Menu;
import io.howeveryir.talentflow.model.RespBean;
import io.howeveryir.talentflow.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/however-yir
 * @博客 http://wangsong.blog.csdn.net
 * @网站 https://github.com/however-yir
 * @时间 2019-09-27 7:10
 */
@RestController
@RequestMapping("/system/config")
public class SystemConfigController {
    @Autowired
    MenuService menuService;
    @GetMapping("/menu")
    public List<Menu> getMenusByHrId() {
        return menuService.getMenusByHrId();
    }
}