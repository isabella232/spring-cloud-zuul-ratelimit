package com.github.swce.cloud.autoconfigure.zuul.ratelimit.filters.pre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.RateLimitKeyGenerator;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.RateLimiter;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties.Policy;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties.Policy.MatchType;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitType;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.filters.RateLimitPreFilter;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.filters.commons.TestRouteLocator;
import com.github.swce.cloud.autoconfigure.zuul.ratelimit.support.DefaultRateLimitUtils;
import com.google.common.collect.Lists;
import com.netflix.zuul.context.RequestContext;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UrlPathHelper;

public class RateLimitPreFilterTest {

    private RateLimitPreFilter target;

    @Mock
    private RateLimiter rateLimiter;
    @Mock
    private RateLimitKeyGenerator rateLimitKeyGenerator;
    @Mock
    private RequestAttributes requestAttributes;
    @Mock
    private HttpServletRequest httpServletRequest;

    private RateLimitProperties rateLimitProperties = new RateLimitProperties();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/servicea/test");
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(httpServletRequest);
        RequestContext.testSetCurrentContext(requestContext);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        rateLimitProperties = new RateLimitProperties();
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        RateLimitUtils rateLimitUtils = new DefaultRateLimitUtils(rateLimitProperties);
        Route route = new Route("servicea", "/test", "servicea", "/servicea", null, Collections.emptySet());
        TestRouteLocator routeLocator = new TestRouteLocator(Collections.emptyList(), Lists.newArrayList(route));
        target = new RateLimitPreFilter(rateLimitProperties, routeLocator, urlPathHelper, rateLimiter, rateLimitKeyGenerator, rateLimitUtils);
    }

    @Test
    public void testFilterType() {
        assertThat(target.filterType()).isEqualTo(FilterConstants.PRE_TYPE);
    }

    @Test
    public void testFilterOrder() {
        assertThat(target.filterOrder()).isEqualTo(FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER);
    }

    @Test
    public void testShouldFilterOnDisabledProperty() {
        assertThat(target.shouldFilter()).isEqualTo(false);
    }

    @Test
    public void testShouldFilterOnNoPolicy() {
        rateLimitProperties.setEnabled(true);

        assertThat(target.shouldFilter()).isEqualTo(false);
    }

    @Test
    public void testShouldFilterOnNonMatchingPolicyType() {
        rateLimitProperties.setEnabled(true);
        Policy servicebPolicy = new Policy();
        MatchType matchType = new MatchType(RateLimitType.URL, "other");
        servicebPolicy.getType().add(matchType);
        rateLimitProperties.getPolicyList().put("servicea", Lists.newArrayList(servicebPolicy));

        assertThat(target.shouldFilter()).isEqualTo(false);
    }

    @Test
    public void testShouldFilter() {
        rateLimitProperties.setEnabled(true);
        Policy defaultPolicy = new Policy();
        rateLimitProperties.setDefaultPolicyList(Lists.newArrayList(defaultPolicy));

        assertThat(target.shouldFilter()).isEqualTo(true);
    }
}