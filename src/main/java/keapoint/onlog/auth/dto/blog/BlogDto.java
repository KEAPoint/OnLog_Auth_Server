package keapoint.onlog.auth.dto.blog;

import lombok.Data;

import java.util.UUID;

@Data
public class BlogDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogThemeImg; // 사용자 블로그 테마 이미지
}
