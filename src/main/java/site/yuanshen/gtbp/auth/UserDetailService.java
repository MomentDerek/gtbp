package site.yuanshen.gtbp.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
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
public class UserDetailService implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final UserRepository userRepository;

    /**
     * 修改指定用户的密码。这应该会更改持久用户存储库（database，LDAP等）中的用户密码。
     */
    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        Long uid = Long.valueOf(user.getUsername());
        return userRepository.findById(uid)
                .doOnNext(entity -> entity.setPassword(newPassword))
                .doOnNext(userRepository::save)
                .map(entity-> userRepository.findById(uid)).ofType(UserDetails.class);
    }

    @Override
    public Mono<UserDetails> findByUsername(String uid) {
        return userRepository.findById(Long.valueOf(uid)).ofType(UserDetails.class);
    }
}
