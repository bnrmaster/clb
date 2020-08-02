package org.dz;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Param at least 1 local port and target ip:port");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Map<String, Integer> address = new HashMap<>();
        for(int i = 1; i < args.length; i++) {
            String[] ipAndPort = args[i].split(":");
            address.put(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
        }

        LoadBalancer frontServer = new LoadBalancer(port, address, "ROUND");
        frontServer.start();
    }
}
