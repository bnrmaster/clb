package org.dz.lbstrategy;

import org.dz.TargetProxy;

import java.net.Socket;
import java.util.List;

public interface ILoadBalanceStrategy {
    public void run(List<TargetProxy> targetProxies, Socket frontSocket);
}
