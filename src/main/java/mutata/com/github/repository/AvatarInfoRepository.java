package mutata.com.github.repository;

import mutata.com.github.entity.AvatarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarInfoRepository extends JpaRepository<AvatarInfo,String> {
    Optional<AvatarInfo> findByUsername(String username);
}
