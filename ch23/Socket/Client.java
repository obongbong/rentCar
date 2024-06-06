package com.oracle.rent.ch23.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.oracle.rent.ch23.car.vo.CarVO;
import com.oracle.rent.ch23.member.vo.MemberVO;
import com.oracle.rent.ch23.res.vo.ResVO;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(String serverHost, int serverPort) throws IOException {
        connect(serverHost, serverPort);
    }

    private void connect(String serverHost, int serverPort) throws IOException {
        while (true) {
            try {
                socket = new Socket(serverHost, serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                break;
            } catch (IOException e) {
                System.out.println("연결 실패, 재시도 중...");
                try {
                    Thread.sleep(5000); // 5초 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 클라이언트에서 서버로 차량 정보(carVO) 보내기
    public void sendCarRequest(String operation, CarVO carVO) throws IOException {
        out.writeObject(operation);
        out.writeObject(carVO);
        out.flush();
    }

    public void sendMemberRequest(String operation, MemberVO memVO) throws IOException {
        out.writeObject(operation);
        out.writeObject(memVO);
        out.flush();
    }

    public List<CarVO> receiveCarListResponse() throws IOException, ClassNotFoundException {
        return (List<CarVO>) in.readObject();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public List<MemberVO> receiveMemberListResponse() throws IOException, ClassNotFoundException {
        return (List<MemberVO>) in.readObject();
    }
    
    public void sendResRequest(String operation, ResVO resVO) throws IOException {
        out.writeObject(operation);
        out.writeObject(resVO);
        out.flush();
    }

    public List<ResVO> receiveResListResponse() throws IOException, ClassNotFoundException {
        return (List<ResVO>) in.readObject();
    }

    public void reconnectToServer() {
        if (!isConnected()) {
            try {
                socket = new Socket("localhost", 5002);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


       // 서버로부터 메시지를 수신하는 메서드
       public String receiveMessage() throws IOException {
        try {
            return (String) in.readObject();
        } catch (ClassNotFoundException e) {
            // ClassNotFoundException을 처리하는 코드
            e.printStackTrace();
            return null; // 혹은 다른 적절한 처리를 수행
        }
    }
}