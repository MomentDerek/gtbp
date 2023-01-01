package site.yuanshen.gtbp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import site.yuanshen.gtbp.dao.UserRepository;
import site.yuanshen.gtbp.model.User;

/**
 * TODO
 *
 * @author Moment
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(Long uid, String password, String nickname) {
        try {
            return userRepository.save(
                    new User()
                            .withUid(uid)
                            .withPassword(passwordEncoder.encode(password))
                            .withNickname(nickname));
        } catch (Exception e) {
            throw new RuntimeException("用户注册失败，请检查uid是否已经被注册",e);
        }
    }

    public Mono<User> getUser(Long uid) {
        return userRepository.findById(uid);
    }

}
