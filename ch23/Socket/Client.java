package com.oracle.rent.ch23.Socket;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private String serverHost;
    private int serverPort;

    public Client(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.socket = new Socket(serverHost, serverPort);
        System.out.println("서버에 연결되었습니다.");
    }


}

