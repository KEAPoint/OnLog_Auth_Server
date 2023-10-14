package keapoint.onlog.auth.controller;

import keapoint.onlog.auth.base.AccountType;
import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.PostLoginRes;
import keapoint.onlog.auth.dto.SocialAccountUserInfo;
import keapoint.onlog.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @param authCode 카카오 인가코드
     * @return 사용자 식별자, 토큰 정보가 들어있는 객체
     */
    @ResponseBody
    @RequestMapping("/kakao/callback")
    public BaseResponse<PostLoginRes> kakaoCallback(@RequestParam String authCode) throws Exception {
        String authToken = authService.getKakaoAccessToken(authCode);

        SocialAccountUserInfo data = authService.getKakaoUserInfo(authToken);

        return new BaseResponse<>(authService.loginWithSocialAccount(data, AccountType.KAKAO));
    }
}
