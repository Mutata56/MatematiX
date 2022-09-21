package mutata.com.github.repository;

import mutata.com.github.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

     Optional<User> findUserByNameIgnoreCase(String name);

     Optional<User> findUserByEmailIgnoreCase(String email);


}
