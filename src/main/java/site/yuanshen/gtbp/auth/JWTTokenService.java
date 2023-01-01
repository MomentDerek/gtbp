package site.yuanshen.gtbp.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

/**
 * 用于创建 JWT 对象的服务，当交易所提供基本身份验证时使用此服务。如果身份验证成功，则会在响应中添加令牌
 */
public class JWTTokenService {

    /**
     * 使用已经验证过的用户信息，创建 JWT 对象并对其进行签名
     *
     * @param subject     Name of current principal
     * @param credentials Credentials of current principal
     * @param authorities A collection of granted authorities for this principal
     * @return String representing a valid token
     */
    public static String generateToken(String subject, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        SignedJWT signedJWT;
        JWTClaimsSet claimsSet;

        claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .expirationTime(new Date(getExpiration()))
                //权限写死
                //.claim("roles", authorities
                //        .stream()
                //        .map(GrantedAuthority.class::cast)
                //        .map(GrantedAuthority::getAuthority)
                //        .collect(Collectors.joining(",")))
                .build();

        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(new JWTCustomSigner().getSigner());
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return signedJWT.serialize();
    }

    /**
     * Returns a millisecond time representation 24hrs from now
     * to be used as the time the currently token will be valid
     *
     * @return Time representation 24 from now
     */
    private static long getExpiration(){
        return new Date().toInstant()
                .plus(45, ChronoUnit.MINUTES)
                .toEpochMilli();
    }
}
