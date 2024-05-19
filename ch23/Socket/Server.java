package com.oracle.rent.ch23.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.oracle.rent.ch23.main.RentMainWindow;

public class Server {
    private ServerSocket serverSocket;
    private boolean running;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("서버가 " + port + " 포트에서 실행 중...");

            running = true;
            // 클라이언트 연결을 계속해서 대기
            while (running) {
                // 클라이언트의 연결을 수락하고 통신을 담당할 스레드 생성
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다: " + clientSocket.getInetAddress());

                // 클라이언트 요청을 처리하는 스레드 생성 및 시작
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 5002; // 서버 포트 번호를 5001에서 5002로 변경
        Server server = new Server(port);
    }

   public void start() {
      // TODO Auto-generated method stub
      
   }
    
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("IP 주소: " + clientSocket.getInetAddress().getHostAddress() + " 접속");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // 클라이언트로부터 메시지 수신 및 처리
            Object receivedObject = in.readObject();
            System.out.println("클라이언트로부터 받은 메시지: " + receivedObject);

            // 클라이언트에게 응답 전송
            String responseMessage = "서버에서 보내는 응답 메시지";
            out.writeObject(responseMessage);
            out.flush();
            System.out.println("클라이언트에게 응답 메시지 전송: " + responseMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 클라이언트와의 연결 종료
                clientSocket.close();
                System.out.println("IP 주소: " + clientSocket.getInetAddress().getHostAddress() + " 종료");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
