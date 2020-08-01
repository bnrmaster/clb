package org.dz.lbstrategy.impl;

import org.dz.TargetProxy;
import org.dz.lbstrategy.ILoadBalanceStrategy;

import java.net.Socket;
import java.util.List;

public class RoundBalanceStrategy implements ILoadBalanceStrategy {

    static int counter = 0;
    @Override
    public void run(List<TargetProxy> targetProxies, Socket frontSocket) {
        counter ++;
        counter %= targetProxies.size();
        targetProxies.get(counter).recieve(frontSocket);
    }
}
