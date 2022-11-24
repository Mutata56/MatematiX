package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

     Optional<User> findUserByNameIgnoreCase(String name);

     Optional<User> findUserByEmailIgnoreCase(String email);

}
