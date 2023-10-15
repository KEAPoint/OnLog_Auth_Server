package keapoint.onlog.auth.repository;

import keapoint.onlog.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);
}
