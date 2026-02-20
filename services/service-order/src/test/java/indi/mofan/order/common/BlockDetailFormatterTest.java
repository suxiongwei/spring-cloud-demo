package indi.mofan.order.common;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BlockDetailFormatterTest {

    @Test
    void shouldFormatFlowRule() {
        FlowRule rule = new FlowRule();
        rule.setResource("rateLimit-qps");
        rule.setGrade(1);
        rule.setCount(5.0d);
        rule.setStrategy(0);

        BlockException ex = Mockito.mock(BlockException.class);
        when(ex.getRule()).thenReturn(rule);

        String formatted = BlockDetailFormatter.format(ex);
        assertThat(formatted).contains("resource=rateLimit-qps", "grade=1", "count=5.00", "strategy=0");
    }

    @Test
    void shouldFormatDegradeRule() {
        DegradeRule rule = new DegradeRule();
        rule.setResource("degrade-rt");
        rule.setGrade(0);
        rule.setCount(2000d);
        rule.setTimeWindow(5);

        BlockException ex = Mockito.mock(BlockException.class);
        when(ex.getRule()).thenReturn(rule);

        String formatted = BlockDetailFormatter.format(ex);
        assertThat(formatted).contains("resource=degrade-rt", "grade=0", "count=2000.00", "timeWindow=5");
    }

    @Test
    void shouldFormatParamFlowRule() {
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource("hotspot-param");
        rule.setParamIdx(0);
        rule.setCount(3d);

        BlockException ex = Mockito.mock(BlockException.class);
        when(ex.getRule()).thenReturn(rule);

        String formatted = BlockDetailFormatter.format(ex);
        assertThat(formatted).contains("resource=hotspot-param", "paramIdx=0", "count=3.00");
    }

    @Test
    void shouldFormatAuthorityRule() {
        AuthorityRule rule = new AuthorityRule();
        rule.setResource("authority-control");
        rule.setStrategy(0);
        rule.setLimitApp("admin");

        BlockException ex = Mockito.mock(BlockException.class);
        when(ex.getRule()).thenReturn(rule);

        String formatted = BlockDetailFormatter.format(ex);
        assertThat(formatted).contains("resource=authority-control", "strategy=0", "limitApp=admin");
    }
}
