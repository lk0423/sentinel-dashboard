package com.alibaba.csp.sentinel.dashboard.rule.memory;

import com.alibaba.csp.sentinel.dashboard.datasource.RuleConfigTypeEnum;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractpersistentRuleApiClient;

import java.util.List;

/**
 * It only makes sure this project starting success, don't have any function.
 * It can not be used to manage rules as memory client.
 * When need to manage rules as memory client, should use the SentinelApiClient in package com.alibaba.csp.sentinel.dashboard.client.
 *
 * @Author Jiajiangnan
 * @E-mail jiajiangnan.office@foxmail.com
 * @Date 2020/8/31
 * @Version 1.0
 */
public class MemoryApiClient<T> extends AbstractpersistentRuleApiClient<T> {


    @Deprecated
    @Override
    public List<T> fetch(String app, RuleConfigTypeEnum configType) throws Exception {
        return null;
    }

    @Deprecated
    @Override
    public void publish(String app, RuleConfigTypeEnum configType, List<T> rules) throws Exception {

    }
}
