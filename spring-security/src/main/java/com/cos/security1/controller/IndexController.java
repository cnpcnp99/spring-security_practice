package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String loginTest(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails principalDetails2) {
        System.out.println("/test/login==============");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication = " + principalDetails.getUser());
        System.out.println("userDetails = " + principalDetails2.getUser());
        return "세션 정보 확인하기";

    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String oauthLoginTest(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth2User2) {
        System.out.println("/test/oauth/login==============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User = " + oAuth2User.getAttributes());
        System.out.println("oAuth2User2 = " + oAuth2User2.getAttributes());
        return "OAuth 세션 정보 확인하기";

    }

    @GetMapping({"", "/"}) // localhost:8080/ or localhost:8080
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());

        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 스프링시큐리티가 해당 주소를 낚아챔 - SecurityConfig 파일 작성 이후에는 작동 안
    @GetMapping("/loginForm")
    public  String login() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public  String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public @ResponseBody String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String password = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encPassword);
        userRepository.save(user); // 회원가입이 잘되지만 비밀번호가 그대로 테이블에 들어감 => 시큐리티로 로그인할 수 없음. 암호화가 안됨

        return "join";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터 정보";
    }
}
