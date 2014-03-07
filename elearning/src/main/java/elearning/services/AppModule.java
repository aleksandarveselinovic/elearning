
package elearning.services;

import elearning.Symbols;
import elearning.entities.Role;
import elearning.entities.User;
import elearning.services.security.EntityRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.PartialMarkupRenderer;
import org.apache.tapestry5.services.PartialMarkupRendererFilter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

import java.io.IOException;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 */
public class AppModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(UserManager.class, UserManagerImpl.class);
        binder.bind(TestService.class, TestServiceImpl.class);
        binder.bind(TapestryHelper.class, TapestryHelperImpl.class);
        binder.bind(TestSessionManager.class, TestSessionManagerImpl.class);
    }

    public static void contributeFactoryDefaults(
        MappedConfiguration<String, Object> configuration) {
        configuration.override(SymbolConstants.APPLICATION_VERSION, "1");
    }

    public static void contributeApplicationDefaults(
        MappedConfiguration<String, Object> configuration) {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        configuration.add(SecuritySymbols.LOGIN_URL, "/index");
        configuration.add(SecuritySymbols.SUCCESS_URL, "/");
        configuration.add(SecuritySymbols.UNAUTHORIZED_URL, "/index");
        configuration.add(Symbols.DEFAULT_OPTION_COUNT, 4);
    }

    public RequestFilter buildTimingFilter(final Logger log) {
        return new RequestFilter() {
            public boolean service(Request request, Response response, RequestHandler handler)
                throws IOException {
                long startTime = System.currentTimeMillis();

                try {
                    return handler.service(request, response);
                } finally {
                    long elapsed = System.currentTimeMillis() - startTime;

                    log.info(String.format("Request time: %d ms", elapsed));
                }
            }
        };
    }

    public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
        @Local
        RequestFilter filter) {
        configuration.add("Timing", filter);
    }

    public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> c,
        SecurityFilterChainFactory f) {
        c.add(f.createChain("/teacher/**").add(f.roles(), "teacher").build());
        c.add(f.createChain("/student/**").add(f.roles(), "student").build());
        c.add(f.createChain("/admin/**").add(f.roles(), "admin").build());
        c.add(f.createChain("/user/**").add(f.authc()).build());
    }

    public static void contributeWebSecurityManager(UserManager userManager,
        Configuration<Realm> configuration) {
        configuration.add(new EntityRealm(userManager));
    }

    @Match({"*Manager", "*Service"})
    public static void adviseTransactions(MethodAdviceReceiver receiver, HibernateTransactionAdvisor advisor) {
        advisor.addTransactionCommitAdvice(receiver);
    }

    @Startup
    public void createTestUsers(UserManager userManager) {
        if (userManager.getAllUsers().size() != 0) {
            return;
        }

        userManager.persist(createTestUser("Administrator", null, "admin", "password", Role.Admin));
        userManager.persist(createTestUser("Teacher", null, "teacher", "password", Role.Teacher));
        userManager.persist(createTestUser("Student", null, "student", "password", Role.Student));
    }

    private User createTestUser(String firstName, String lastName, String username, String password, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    public static void contributeMarkupRenderer(
        OrderedConfiguration<MarkupRendererFilter> configuration,
        final Environment environment) {

        MarkupRendererFilter validationDecorator = new MarkupRendererFilter() {

            public void renderMarkup(MarkupWriter writer, MarkupRenderer renderer) {
                ValidationDecorator decorator = new
                    BootstrapValidationDecorator(environment, writer);

                environment.push(ValidationDecorator.class, decorator);
                renderer.renderMarkup(writer);
                environment.pop(ValidationDecorator.class);

            }
        };

        configuration.override("ValidationDecorator", validationDecorator);

    }

    public static void contributeClasspathAssetAliasManager(
        MappedConfiguration<String, String> configuration) {
        configuration.add("bootstrap-core", "bootstrap");
        configuration.add("bootstrap-editable", "bootstrap-editable");
        configuration.add("select2", "select2");
        configuration.add("bootstrap-wysihtml5", "bootstrap-wysihtml5");
    }

    public static void contributePartialMarkupRenderer(
        OrderedConfiguration<PartialMarkupRendererFilter> configuration,
        final Environment environment) {

        PartialMarkupRendererFilter validationDecorator = new PartialMarkupRendererFilter() {

            public void renderMarkup(MarkupWriter writer, JSONObject json,
                PartialMarkupRenderer renderer) {
                ValidationDecorator decorator = new
                    BootstrapValidationDecorator(environment, writer);

                environment.push(ValidationDecorator.class, decorator);
                renderer.renderMarkup(writer, json);
                environment.pop(ValidationDecorator.class);
            }
        };

        configuration.override("ValidationDecorator", validationDecorator);

    }
}
