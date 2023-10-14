package keapoint.onlog.auth.service;

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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    /**
     * 카카오 인가 코드로 카카오 access token 발급받기
     *
     * @param authCode Kakao auth code
     * @return kakao access token
     * @throws Exception
     */
    public String getKakaoAccessToken(String authCode) throws Exception {
        try {
            log.info("kakao auth code" + authCode);

            // GET 요청을 보낼 URL
            String url = "https://kauth.kakao.com/oauth/token";

            // URL 객체 생성
            URL obj = new URL(url);

            // HttpURLConnection 객체 생성 및 설정
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            log.info("Response Code at Kakao access token: " + responseCode);

            // 응답 데이터 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 카카오 access token 추출
            String accessToken = JsonParser.parseString(response.toString())
                    .getAsJsonObject()
                    .get("access_token")
                    .getAsString();

            log.info("Kakao access token: " + accessToken);

            return accessToken;

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
            // 카카오 정보를 요청할 URL
            String url = "https://kapi.kakao.com/v2/user/me";

            // URL 객체 생성
            URL obj = new URL(url);

            // HttpURLConnection 객체 생성 및 설정
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            log.info("Response Code at Kakao user info : " + responseCode);

            // 응답 데이터 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            // JsonObject로 형 변환
            JsonObject object = JsonParser.parseString(result.toString())
                    .getAsJsonObject();

            // 이메일 있는지 여부
            boolean hasEmail = object.get("kakao_account")
                    .getAsJsonObject()
                    .get("has_email")
                    .getAsBoolean();

            // 이메일이 없는 경우 Exception
            // 이메일이 사용자의 식별자로 사용되고 있기 때문에 무조건 필요함
            if (!hasEmail) throw new BaseException(BaseErrorCode.EMAIL_NOT_FOUND_EXCEPTION);

            // 사용자의 이메일 추출
            String email = object.get("kakao_account").getAsJsonObject().get("email").getAsString();

            // 사용자의 이름 추출
            String name = object.get("kakao_account")
                    .getAsJsonObject().get("profile")
                    .getAsJsonObject().get("nickname").getAsString();

            SocialAccountUserInfo user = SocialAccountUserInfo.builder()
                    .userName(name)
                    .userEmail(email)
                    .build();

            log.info(user.toString());

            return user;

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
