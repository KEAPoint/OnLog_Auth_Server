package keapoint.onlog.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRes {
    @Schema(name = "userIdx", example = "01234567-8910-abcd-efgh-ijklmnopqrst", requiredProperties = "true", description = "사용자 식별자")
    private UUID userIdx;
    private TokensDto tokenInfo;
}
