package keapoint.onlog.auth.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 모든 엔티티에 공통으로 들어가는 요소
 * @author LEE JIHO
 * @usage 각 엔티티데 BaseEntity를 extends 받아 사용
 * */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)// 기본값 지정
    @CreationTimestamp
    private LocalDateTime createdAt; // Row 생성 시점

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt; // Row 업데이트 시점

    @Column(name = "status", nullable = false)
    private Boolean status = true; // Row 유효 상태


    public BaseEntity(LocalDateTime createdAt, LocalDateTime updatedAt, Boolean status) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }
}
