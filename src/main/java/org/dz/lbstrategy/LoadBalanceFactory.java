package org.dz.lbstrategy;

import org.dz.enums.LbType;
import org.dz.lbstrategy.impl.RoundBalanceStrategy;

public class LoadBalanceFactory {
    public static ILoadBalanceStrategy createInstance(String lbName) {
        LbType lbType = LbType.valueOf(lbName);
        switch (lbType) {
            case ROUND: return new RoundBalanceStrategy();
        }
        return null;
    }
}
