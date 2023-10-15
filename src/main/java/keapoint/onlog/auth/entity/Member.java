package keapoint.onlog.auth.entity;

import jakarta.persistence.*;
import keapoint.onlog.auth.base.AccountType;
import keapoint.onlog.auth.base.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_idx", nullable = false)
    private UUID memberIdx;

    @Column(name = "email", length = 40, nullable = false, unique = true)
    private String email;

    private String password;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "agree_personal_info", nullable = false)
    private boolean agreePersonalInfo;

    @Column(name = "agree_promotion", nullable = false)
    private boolean agreePromotion;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(length = 20, name = "name")
    private String userName;

    @Builder
    public Member(String email, String password, String phoneNumber, boolean agreePersonalInfo, boolean agreePromotion, String refreshToken, Role role, AccountType accountType, String userName) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.agreePersonalInfo = agreePersonalInfo;
        this.agreePromotion = agreePromotion;
        this.refreshToken = refreshToken;
        this.role = role;
        this.accountType = accountType;
        this.userName = userName;
    }

    /**
     * 사용자의 refresh token 갱신
     * @param refreshToken
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
