package indi.mofan.order.common;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;

public final class BlockDetailFormatter {
    private BlockDetailFormatter() {}

    public static String format(BlockException ex) {
        Rule rule = ex.getRule();
        String type = ex.getClass().getSimpleName();
        if (rule instanceof FlowRule) {
            FlowRule r = (FlowRule) rule;
            return String.format("type=%s, resource=%s, grade=%d, count=%.2f, strategy=%d",
                    type, r.getResource(), r.getGrade(), r.getCount(), r.getStrategy());
        } else if (rule instanceof DegradeRule) {
            DegradeRule r = (DegradeRule) rule;
            return String.format("type=%s, resource=%s, grade=%d, count=%.2f, timeWindow=%d",
                    type, r.getResource(), r.getGrade(), r.getCount(), r.getTimeWindow());
        } else if (rule instanceof ParamFlowRule) {
            ParamFlowRule r = (ParamFlowRule) rule;
            return String.format("type=%s, resource=%s, paramIdx=%d, count=%.2f",
                    type, r.getResource(), r.getParamIdx(), r.getCount());
        } else if (rule instanceof SystemRule) {
            SystemRule r = (SystemRule) rule;
            return String.format("type=%s, systemRule=%s", type, r.toString());
        } else if (rule instanceof AuthorityRule) {
            AuthorityRule r = (AuthorityRule) rule;
            return String.format("type=%s, resource=%s, strategy=%d, limitApp=%s",
                    type, r.getResource(), r.getStrategy(), r.getLimitApp());
        } else if (rule != null) {
            return String.format("type=%s, rule=%s", type, rule.toString());
        }
        return String.format("type=%s, detail=%s", type, ex.toString());
    }
}