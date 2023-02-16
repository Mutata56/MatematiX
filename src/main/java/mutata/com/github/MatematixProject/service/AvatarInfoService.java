package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.repository.AvatarInfoRepository;
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

    public AvatarInfo findByNameOrReturnEmpty(String name) {
        var result = findByName(name).orElse(null);
        if(result == null)
            result = new AvatarInfo(name);
        return result;
    }
}
