package org.springframework.boot.autoconfigure.jooq;

import org.jooq.*;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import javax.sql.DataSource;

/**
 * @author Kevin Davin
 */
public class JooqConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext>  {

    private final JooqProperties jooqProperties;

    public JooqConfigurationInitializer(JooqProperties jooqProperties) {
        this.jooqProperties = jooqProperties;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        if (context.getBeanFactory().getBeanNamesForType(DSLContext.class).length != 0) {
            return;
        }

        context.registerBean(DataSourceConnectionProvider.class, () -> new JooqAutoConfiguration().dataSourceConnectionProvider(context.getBean(DataSource.class)));
        context.registerBean(DefaultExecuteListenerProvider.class, () -> new JooqAutoConfiguration().jooqExceptionTranslatorExecuteListenerProvider());
        context.registerBean(DefaultConfiguration.class,
                () -> new JooqAutoConfiguration.DslContextConfiguration().jooqConfiguration(
                        jooqProperties,
                        context.getBean(ConnectionProvider.class),
                        context.getBean(DataSource.class),
                        context.getBeanProvider(ExecuteListenerProvider.class),
                        context.getBeanProvider(DefaultConfigurationCustomizer.class)
                )
        );

        context.registerBean("dslContext", DSLContext.class, () -> new JooqAutoConfiguration.DslContextConfiguration().dslContext(context.getBean(Configuration.class)));
    }
}
