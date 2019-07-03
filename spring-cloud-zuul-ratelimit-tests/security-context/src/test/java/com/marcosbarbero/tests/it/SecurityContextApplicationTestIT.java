package com.marcosbarbero.tests.it;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import com.swce.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.swce.cloud.autoconfigure.zuul.ratelimit.support.SecuredRateLimitUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityContextApplicationTestIT {

    @Autowired
    private ApplicationContext context;

    @Test
    public void securedRateLimitUtils() {
        RateLimitUtils rateLimitUtils = context.getBean(RateLimitUtils.class);
        assertThat(rateLimitUtils, instanceOf(SecuredRateLimitUtils.class));
    }
}
