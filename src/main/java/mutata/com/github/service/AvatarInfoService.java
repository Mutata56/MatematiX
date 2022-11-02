package mutata.com.github.service;

import mutata.com.github.entity.AvatarInfo;
import mutata.com.github.repository.AvatarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AvatarInfoService {

    private final AvatarInfoRepository repository;

    @Autowired
    public AvatarInfoService(AvatarInfoRepository repository) {
        this.repository = repository;
    }


    public Optional<AvatarInfo> findByName(String username) {
        return repository.findByUsername(username);
    }
    @Transactional(readOnly = false)
    public void save(AvatarInfo theInfo) {
        repository.save(theInfo);
    }
}
