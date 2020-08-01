package org.dz;

import org.dz.lbstrategy.ILoadBalanceStrategy;
import org.dz.lbstrategy.LoadBalanceFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FrontServer {

    private static final int MAX_CONNECTION_NUM = 50;
    private static List<TargetProxy> targetProxies = new ArrayList<>();
    private static ILoadBalanceStrategy lbStrategy = null;
    private static String serverIp;
    private static int serverPort;

    public static void start() {
        init();
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

    /**
     读取被代理的后端服务器设置，初始化
     */
    private static void init() {
        // TODO: 读取配置文件
        serverIp = "0.0.0.0";
        serverPort = 7777;
        //TODO: 读取配置文件 实例化多个proxy
        targetProxies.add(new TargetProxy("192.168.163.24",
                4000,
                10,
                100,
                1000));
        //TODO: 读取配置文件 实例化负载均衡策略
        lbStrategy = LoadBalanceFactory.createInstance("ROUND");
    }

}