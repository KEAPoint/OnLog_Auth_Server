package keapoint.onlog.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import keapoint.onlog.auth.base.AccountType;
import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.PostLoginRes;
import keapoint.onlog.auth.dto.PostLogoutRes;
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
    @Operation(summary = "카카오 계정을 통한 로그인", description = "카카오 계정을 통해서 로그인을 진행합니다.")
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
        return authService.loginWithSocialAccount(data, AccountType.KAKAO);
    }

    /**
     * 로그아웃
     *
     * @param token 사용자 refresh token
     * @return 성공 여부가 들어있는 객체
     * @throws Exception
     */
    @PostMapping("/logout")
    @Operation(summary = "블로그 로그아웃", description = "블로그를 로그아웃 합니다.")
    public BaseResponse<PostLogoutRes> userLogout(@RequestHeader("Authorization") String token) throws Exception {
        log.info("logout token(" + token +")");
        return authService.logout(extractToken(token));
    }

    /**
     * 토큰 추출하는 메소드
     *
     * @param token 사용자 token
     * @return token 문자열 또는 null (추출 실패 시)
     */
    private String extractToken(String token) {
        String[] parsedToken = token.split(" ");

        if (parsedToken[0].equalsIgnoreCase("Bearer")) { // Bearer 시작 형식인지 확인
            log.info("parsed token: " + parsedToken[1]);
            return parsedToken[1]; // "Bearer " 부분을 제외한 실제 토큰 문자열 반환
        }

        return null;
    }
}
