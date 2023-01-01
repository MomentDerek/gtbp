package site.yuanshen.gtbp.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户系统表
 *
 * @author Moment
 * @since 2022-04-20 10:18:18
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@With
@EqualsAndHashCode(callSuper = false)
@Table(schema = "public")
public class User extends BaseEntity implements UserDetails {

    /**
     * uid
     */
    @Id
    private Long uid;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 分数
     */
    private Integer score;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(()->"USER");
    }

    @Override
    public String getUsername() {
        return uid.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
