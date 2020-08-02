package org.dz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 @desc 一个实例代表一个目标ip
 */
public class TargetProxy {
    private ThreadPoolExecutor dataForwardPool;
    private ThreadPoolExecutor dataBackwardPool;
    private String ip;
    private int port;

    public TargetProxy(String ip, int port, int corePoolSize, int maxPoolSize, int keepAliveTime) {
        this.ip = ip;
        this.port = port;
        this.dataForwardPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        this.dataBackwardPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     @desc 接收前方来的socket
     @param frontSocket
     */
    public void recieve(Socket frontSocket) {
        Socket proxySocket = null;
        try {
            proxySocket = new Socket(InetAddress.getByName(this.ip), this.port);
            dataForwardPool.execute(new SocketTransfer(frontSocket, proxySocket));
            dataBackwardPool.execute(new SocketTransfer(proxySocket, frontSocket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class SocketTransfer implements Runnable {

        /**
         * 读入流的数据的套接字。
         */
        private Socket readSocket;

        /**
         * 输出数据的套接字。
         */
        private Socket writeSocket;

        /**
         * 两个套接字参数分别用来读数据和写数据。这个方法仅仅保存套接字的引用，
         * 在运行线程的时候会用到。
         * @param readSocket 读取数据的套接字。
         * @param writeSocket 输出数据的套接字。
         */
        public SocketTransfer(Socket readSocket, Socket writeSocket) {
            this.readSocket = readSocket;
            this.writeSocket = writeSocket;
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];
            InputStream is = null;
            OutputStream os = null;
            try {
                is = readSocket.getInputStream();
                os = writeSocket.getOutputStream();
                System.out.println("transfer begin from " + readSocket.getInetAddress().getHostAddress()
                        + " to "+ writeSocket.getInetAddress().getHostAddress());
                while(!readSocket.isClosed() && !writeSocket.isClosed()){
                    int size = is.read(b);
                    if (size > -1) {
                        os.write(b, 0, size);
                    } else {
                        break;
                    }
                }
                System.out.println("transfer end from " + readSocket.getInetAddress().getHostAddress()
                        + " to "+ writeSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    os.flush();
                    readSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
