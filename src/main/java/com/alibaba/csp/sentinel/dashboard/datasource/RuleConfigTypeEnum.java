package com.alibaba.csp.sentinel.dashboard.datasource;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayParamFlowItemEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;

/**
 * keys of rule seetings stored in config center
 *
 * @Author Jiajiangnan
 * @E-mail jiajiangnan.office@foxmail.com
 * @Date 2020/8/30
 * @since 1.8.0
 */
public enum RuleConfigTypeEnum {

    FLOW("flow-rules", FlowRuleEntity.class),
    PARAM_FLOW("param-rules", ParamFlowRuleEntity.class),
    AUTHORITY("authority-rules", AuthorityRuleEntity.class),
    DEGRADE("degrade-rules", DegradeRuleEntity.class),
    SYSTEM("system-rules", SystemRuleEntity.class),
    GATEWAY_FLOW("gateway-flow-rules", GatewayFlowRuleEntity.class),
    GATEWAY_PARAM_FLOW("gateway-param-flow-rules", GatewayParamFlowItemEntity.class);


    private String value;
    private Class clazz;

    RuleConfigTypeEnum(String value, Class clazz){
        this.value = value;
        this.clazz = clazz;
    }

    public String getValue(){
        return this.value;
    }

    public Class getClazz() {
        return this.clazz;
    }

}
