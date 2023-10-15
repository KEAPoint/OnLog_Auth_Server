package keapoint.onlog.auth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import keapoint.onlog.auth.base.AccountType;
import keapoint.onlog.auth.base.BaseErrorCode;
import keapoint.onlog.auth.base.BaseException;
import keapoint.onlog.auth.base.Role;
import keapoint.onlog.auth.dto.PostLoginRes;
import keapoint.onlog.auth.dto.SocialAccountUserInfo;
import keapoint.onlog.auth.dto.TokensDto;
import keapoint.onlog.auth.entity.User;
import keapoint.onlog.auth.repository.UserRepository;
import keapoint.onlog.auth.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId; // Rest Api Key

    /**
     * 카카오 인가 코드로 카카오 access token 발급받기
     *
     * @param authCode Kakao auth code
     * @return kakao access token
     * @throws Exception
     */
    public String getKakaoAccessToken(String authCode) throws Exception {
        try {
            // webClient 설정
            WebClient kakaoWebClient =
                    WebClient.builder()
                            .baseUrl("https://kauth.kakao.com/oauth/token")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                            .build();

            // token api 호출
            Map<String, Object> tokenResponse =
                    kakaoWebClient
                            .post()
                            .uri(uriBuilder -> uriBuilder
                                    .queryParam("grant_type", "authorization_code")
                                    .queryParam("client_id", clientId)
                                    .queryParam("code", authCode)
                                    .build())
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            return (String) tokenResponse.get("access_token");

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * 카카오 access token으로 사용자의 정보 발급받기
     *
     * @param accessToken Kakao access token
     * @return 사용자의 이름과 메일이 들어있는 객체
     * @throws BaseException
     */
    public SocialAccountUserInfo getKakaoUserInfo(String accessToken) throws BaseException {
        try {
            // webClient 설정
            WebClient kakaoApiWebClient =
                    WebClient.builder()
                            .baseUrl("https://kapi.kakao.com/v2/user/me")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                            .build();

            String response =
                    kakaoApiWebClient
                            .post()
                            .header("Authorization", "Bearer " + accessToken)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

            log.info("User Information Request Results : " + response);

            JsonObject kakaoAccount = JsonParser.parseString(response)
                    .getAsJsonObject()
                    .get("kakao_account")
                    .getAsJsonObject();

            // 닉네임 정보 담기
            String username = kakaoAccount.get("profile")
                    .getAsJsonObject()
                    .get("nickname")
                    .getAsString();

            // 이메일 정보 담기
            String email;
            if (kakaoAccount.get("has_email").getAsBoolean()) {
                email = kakaoAccount.get("email").getAsString();

            } else { // 이메일이 없는 경우 Exception. 이메일이 사용자의 식별자로 사용되고 있기 때문에 무조건 필요함
                throw new BaseException(BaseErrorCode.EMAIL_NOT_FOUND_EXCEPTION);
            }

            return SocialAccountUserInfo.builder()
                    .userName(username)
                    .userEmail(email)
                    .build();

        } catch (BaseException exception) {
            throw exception;

        } catch (Exception exception) {
            log.error("Exception in get Kakao user info : " + exception.getMessage());
            throw new BaseException(BaseErrorCode.Internal_Server_Error);
        }
    }

    /**
     * 로그인 - 소셜 계정
     */
    public PostLoginRes loginWithSocialAccount(SocialAccountUserInfo data, AccountType type) throws BaseException {
        try {
            // 이메일을 기반으로 사용자를 조회한다
            // 만약 조회된 결과가 없으면 사용자를 생성하고 DB에 저장한다
            User user = userRepository.findByUserEmail(data.getUserEmail())
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .userEmail(data.getUserEmail())
                                .userPassword(passwordEncoder.encode(data.getUserEmail()))
                                .userPhoneNumber(null)
                                .agreePersonalInfo(false)
                                .agreePromotion(false)
                                .refreshToken(null)
                                .role(Role.USER)
                                .accountType(type)
                                .userName(data.getUserName())
                                .build();

                        return userRepository.save(newUser);
                    });

            // --- 로그인 처리 ---
            // 토큰을 발급받고, refresh token을 DB에 저장한다.
            TokensDto token = generateToken(user.getUserEmail(), user.getUserPassword(), user.getUserIdx(), user.getUserEmail());
            user.updateRefreshToken(token.getRefreshToken());

            // 사용자 정보 로깅
            log.info(user.toString());

            return new PostLoginRes(user.getUserIdx(), token);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.Internal_Server_Error);
        }
    }

    /**
     * 토큰 발행
     *
     * @param principal
     * @param credentials
     * @param userIdx
     * @param password
     * @return 토큰이 들어있는 객체
     * @throws Exception
     */
    public TokensDto generateToken(Object principal, Object credentials, UUID userIdx, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, credentials);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtUtil.createTokens(authentication, userIdx, password);
    }

}
