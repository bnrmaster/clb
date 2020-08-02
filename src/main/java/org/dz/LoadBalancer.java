package org.dz;

import org.dz.lbstrategy.ILoadBalanceStrategy;
import org.dz.lbstrategy.LoadBalanceFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadBalancer {

    private static final int MAX_CONNECTION_NUM = 50;
    private  List<TargetProxy> targetProxies = new ArrayList<>();
    private  ILoadBalanceStrategy lbStrategy = null;
    private  String serverIp;
    private  int serverPort;

    public LoadBalancer(int serverPort, Map<String, Integer> address, String strategy) {
        // 配置到本地
        this.serverIp = "0.0.0.0";
        this.serverPort = serverPort;
        address.forEach(
                (ip, port)->{
                    this.targetProxies.add(
                            new TargetProxy(ip,
                            port,
                            10,
                            100,
                            1000)
                    );
                }
        );

        this.lbStrategy = LoadBalanceFactory.createInstance(strategy);
    }

    public void start() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(serverPort, MAX_CONNECTION_NUM, InetAddress.getByName(serverIp));

            while(true){
                // 用户连接到当前服务器的socket
                Socket frontSocket = ss.accept();
                System.out.println("get accept");
                lbStrategy.run(targetProxies, frontSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ss) {
                    ss.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}