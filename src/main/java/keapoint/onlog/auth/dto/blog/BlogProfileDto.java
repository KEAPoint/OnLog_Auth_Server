package keapoint.onlog.auth.dto.blog;

import lombok.Data;

import java.util.UUID;

@Data
public class BlogProfileDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogThemeImg; // 사용자 블로그 테마 이미지
    private long postCount; // 작성한 글 개수
    private long likeCount; // 좋아요 개수
    private long subscriberCount; // 구독자 수
}
