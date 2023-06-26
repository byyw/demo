package com.byyw.demo.util;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class StateManager {
    private static final StateMachineConfig<StatusEnum, TriggerEnum> stateMachineConfig = new StateMachineConfig<>();

    static {
        // 初始状态为新客
        stateMachineConfig.configure(StatusEnum.CREATE)
                .permit(TriggerEnum.OVERTIME, StatusEnum.OVERTIME);
    }

    // 状态枚举
    public static enum StatusEnum {
        CREATE, OVERTIME;
    }

    // 行为枚举
    public static enum TriggerEnum {
        CREATE, OVERTIME;
    }

    public static void main(String[] args) {
        StateMachine<StatusEnum,TriggerEnum> machine1 = new StateMachine<>(StatusEnum.CREATE, stateMachineConfig);
        machine1.fire(TriggerEnum.OVERTIME);
        
    }
}
