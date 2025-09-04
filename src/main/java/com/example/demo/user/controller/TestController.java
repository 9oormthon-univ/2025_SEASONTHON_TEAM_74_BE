package com.example.demo.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login")
public class TestController {

    @Value("${kakao.client_id}")
    private String kClientId;

    @Value("${kakao.redirect_uri}")
    private String kRedirectUri;

    @Deprecated
    @Operation(summary = "카카오 로그인 테스트 페이지", description = "카카오 로그인 테스트 페이지")
    @GetMapping("/page")
    public String loginPage(Model model) {
        log.info("카카오 리디렉션 URI: {}", kRedirectUri);
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kClientId+"&redirect_uri="+kRedirectUri;
        model.addAttribute("location", location);
        return "login";
    }
}

