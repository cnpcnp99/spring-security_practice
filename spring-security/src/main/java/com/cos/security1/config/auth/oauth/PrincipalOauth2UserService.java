package com.cos.security1.config.auth.oauth;

import com.cos.security1.config.SecurityConfig;
import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired private UserRepository userRepository;

    /**
     * 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        printOAuth2UserInformation(userRequest);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String userName = provider + "_" + providerId; // google_1012312312~
        String password = bCryptPasswordEncoder.encode("갯인데어");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(userName);

        if (userEntity == null) {
            User user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .username(userName)
                    .password(password)
                    .email(email)
                    .role(role)
                    .build();
            userRepository.save(user);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); // 얘가 Authentication 객체에 들어감
    }

    private void printOAuth2UserInformation(OAuth2UserRequest userRequest) {
        System.out.println("userRequest = " + userRequest);
        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken().getTokenValue());
        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 헀는지 확인
        // 구글 로그인 버튼 클릭 => 구글 로그인 => code를 리턴(OAuth-Client 라이브러리) => AccessToken 요청
        // userRequest 정보 => loadUser 함수 호출 => 구글로부터 회원 프로필을 받아준다.
        System.out.println("getAttributes = " + super.loadUser(userRequest).getAttributes());
    }
}
