package keapoint.onlog.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SocialAccountUserInfo {

    @Schema(name = "userName", example = "남승현", requiredProperties = "true", description = "사용자 이름")
    private final String userName;

    @Schema(name = "userEmail", example = "namsh1125@naver.com", requiredProperties = "true", description = "사용자 이메일")
    private final String userEmail;
}