package com.oracle.rent.ch23.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import com.oracle.rent.ch23.car.controller.CarController;
import com.oracle.rent.ch23.car.controller.CarControllerImpl;  // 구현 클래스 임포트
import com.oracle.rent.ch23.car.vo.CarVO;
import com.oracle.rent.ch23.main.RentMainWindow;
import com.oracle.rent.ch23.member.controller.MemberController;
import com.oracle.rent.ch23.member.controller.MemberControllerImpl;
import com.oracle.rent.ch23.member.vo.MemberVO;
import com.oracle.rent.ch23.res.controller.ResController;
import com.oracle.rent.ch23.res.controller.ResControllerImpl;
import com.oracle.rent.ch23.res.vo.ResVO;


public class Server {
    private ServerSocket serverSocket;

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("서버 실행중.. 포트번호 " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 실행 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private CarController carController;
        private MemberController memberController;
        private ResController resController;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.carController = new CarControllerImpl();  // 구현 클래스의 인스턴스 생성
            this.memberController = new MemberControllerImpl();
            this.resController = new ResControllerImpl();
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            try {
                while (true) {
                    String operation = (String) in.readObject();

                    switch (operation) {
                        case "차량 조회":
                            CarVO carVO = (CarVO) in.readObject();
                            List<CarVO> carList = null;
                            carList = carController.listCarInfo(carVO);
                            out.writeObject(carList);  // 조회된 차량 목록을 클라이언트로 전송
                            break;
                        case "차량 삭제":
                            carVO = (CarVO) in.readObject();
                            carController.removeCarInfo(carVO);
                            break;
                        case "차량 수정":
                            carVO = (CarVO) in.readObject();
                            carController.modCarInfo(carVO);
                            break;
                        case "차량 등록":
                            carVO = (CarVO) in.readObject();
                            carController.regCarInfo(carVO);  // 차량 등록 로직 호출
                            break;
                        case "회원 등록":
                            MemberVO memberVO = (MemberVO) in.readObject();
                            memberController.regMember(memberVO);
                            break;
                        case "회원 조회":
                            memberVO = (MemberVO) in.readObject();
                            List<MemberVO> memberList = null;
                            memberList = memberController.listMember(memberVO);
                            out.writeObject(memberList);
                            break;
                        case "회원 수정":
                            memberVO = (MemberVO) in.readObject();
                            memberController.modMember(memberVO);
                            break;
                        case "회원 삭제":
                            memberVO = (MemberVO) in.readObject();
                            memberController.removeMember(memberVO);
                            break;
                        case "예약 등록":
                            ResVO resVO = (ResVO) in.readObject();
                            resController.regResInfo(resVO);
                            break;
                        case "예약 조회":
                            resVO = (ResVO) in.readObject();
                            List<ResVO> resList = null;
                            resList = resController.listResInfo(resVO);
                            out.writeObject(resList);
                            break;
                        case "예약 수정":
                            resVO = (ResVO) in.readObject();
                            resController.modResInfo(resVO);
                            break;
                        case "예약 삭제":
                            resVO = (ResVO) in.readObject();
                            resController.cancelResInfo(resVO);
                            break; 
                        case "결제":
                            resVO = (ResVO) in.readObject();
                            //resController.modResInfo(resVO);
                            resController.updatePaymentStatus(resVO);
                            break; 
                        default:
                            out.writeObject("알 수 없는 요청 유형");
                    }
                    out.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 5002; // 서버 포트 번호를 5001에서 5002로 변경
        Server server = new Server();
        server.startServer(port);
    }

   public void start() {
      // TODO Auto-generated method stub
      
   }
    
}

