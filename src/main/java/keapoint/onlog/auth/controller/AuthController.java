package keapoint.onlog.auth.controller;

import keapoint.onlog.auth.base.AccountType;
import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.PostLoginRes;
import keapoint.onlog.auth.dto.SocialAccountUserInfo;
import keapoint.onlog.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 콜백 받은 카카오 auth code로 사용자 로그인처리
     *
     * @param code 카카오 인가코드
     * @return 사용자 식별자, 토큰 정보가 들어있는 객체
     */
    @ResponseBody
    @RequestMapping("/kakao/login")
    public BaseResponse<PostLoginRes> kakaoCallback(@RequestParam String code) throws Exception {
        // 카카오 인가 코드 로깅
        log.info("kakao auth code = " + code);

        // 카카오 access token 받아오기
        String accessToken = authService.getKakaoAccessToken(code);
        log.info("kakao access token = " + accessToken);

        // 받은 access token으로 사용자 정보 받아오기
        SocialAccountUserInfo data = authService.getKakaoUserInfo(accessToken);
        log.info("user = " + data.toString());

        // 서비스에 로그인 후 응답 반환
        return new BaseResponse<>(authService.loginWithSocialAccount(data, AccountType.KAKAO));
    }
}
