package keapoint.onlog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRes {
    private UUID memberIdx;
    private String email;
    private String profileImgUrl;
    private TokensDto tokenInfo;
}
