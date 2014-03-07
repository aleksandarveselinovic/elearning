package elearning.services.security;

import elearning.entities.User;
import elearning.services.UserManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

public class EntityRealm extends AuthorizingRealm {

    private UserManager userManager;

    public EntityRealm(UserManager userManager) {
        setCacheManager(new MemoryConstrainedCacheManager());
        setAuthorizationCachingEnabled(true);
        setCredentialsMatcher(createHashCredentialsMatcher());
        setName("EntityRealm");
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        this.userManager = userManager;
    }

    private CredentialsMatcher createHashCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher("SHA-256");
        matcher.setHashIterations(1024);
        matcher.setStoredCredentialsHexEncoded(false);
        return matcher;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException(
                "PrincipalCollection was null, which should not happen");
        }

        Collection<?> applicablePrincipals = principals.fromRealm(getName());

        if (applicablePrincipals.isEmpty()) {
            return null;
        }

        User user = userManager.findByUsername((String) applicablePrincipals.iterator().next());
        if (user == null) {
            throw new UnauthenticatedException();
        }

        Set<String> roles = new HashSet<String>();
        if(user.getRole() != null){
            roles.add(user.getRole().name().toLowerCase());
        }

        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        String username = usernamePasswordToken.getUsername();
        if (username == null) {
            throw new AccountException(
                "Null usernames are not allowed by this realm.");
        }

        User user = userManager.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException();
        }

        if (user.getPasswordHash().length() < 1) {
            throw new IncorrectCredentialsException("Password cannot be null.");
        }

        SimpleByteSource bs = new SimpleByteSource(Base64.decode(user.getPasswordSalt()));

        return new SimpleAuthenticationInfo(username, user.getPasswordHash(), bs, getName());
    }

}
