package keapoint.onlog.auth.dto.blog;

import lombok.Data;

import java.util.UUID;

@Data
public class PostCreateBlogReqDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogProfileImg; // 사용자 블로그 프로필

    public PostCreateBlogReqDto(UUID blogId, String name, String blogProfileImg) {
        this.blogId = blogId;
        this.blogNickname = blogId.toString();
        this.blogName = name;
        this.blogIntro = name + " 블로그에요~!";
        this.blogProfileImg = blogProfileImg;
    }
}

