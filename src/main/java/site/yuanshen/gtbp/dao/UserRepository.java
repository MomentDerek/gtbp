package site.yuanshen.gtbp.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import site.yuanshen.gtbp.model.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

}
