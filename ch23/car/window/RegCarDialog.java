package com.oracle.rent.ch23.car.window;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oracle.rent.ch23.Socket.Client;
import com.oracle.rent.ch23.car.controller.CarController;
import com.oracle.rent.ch23.car.controller.CarControllerImpl;
import com.oracle.rent.ch23.car.vo.CarVO;

public class RegCarDialog  extends JDialog{
	JPanel jPanel;
	JLabel lCarNum,lCarName,lSize,lColor,lMaker;
	JTextField tfCarNum,tfCarName,tfSize,tfColor,tfMaker ;
    JButton btnCarReg;
    
    CarController carController;
    Client client; // Added for sending request to server
    
    public RegCarDialog(CarController carController, String str){
    	this.carController = carController;
    	setTitle(str);
    	try {
            this.client = new Client("localhost", 5002); // Each window creates a new Client instance
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    	init();
    }
    
    private void init(){
    	lCarNum = new JLabel("차량번호");
    	lCarName = new JLabel("차량명");
    	lSize = new JLabel("배기량");
    	lColor = new JLabel("차색상");
    	lMaker = new JLabel("차제조사");
    	
    	
    	tfCarNum=new JTextField(20);
    	tfCarName=new JTextField(20);
    	tfSize=new JTextField(20);
    	tfColor=new JTextField(20);
    	tfMaker=new JTextField(20);
    	
    	btnCarReg=new JButton("렌터카등록하기");
    	
   	 	btnCarReg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String carNum=tfCarNum.getText().trim();
				String carName=tfCarName.getText().trim();
				int carSize=Integer.parseInt(tfSize.getText().trim());
				String carColor=tfColor.getText().trim();
				String carMaker=tfMaker.getText().trim();
				
				CarVO carVO=new CarVO(carNum,carName,carColor,carSize,carMaker);
				try {
					client.sendCarRequest("차량 등록", carVO);  // Send request to server with request type
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				showMessage("차량을  등록했습니다.");
				tfCarNum.setText("");
				tfCarName.setText("");
				tfSize.setText("");
				tfColor.setText("");
				tfMaker.setText("");
				
				//dispose();
				
			}
        });
    	
    	
    	jPanel=new JPanel(new GridLayout(0,2));
    	jPanel.add(lCarNum);
    	jPanel.add(tfCarNum);
    	
    	jPanel.add(lCarName);
    	jPanel.add(tfCarName);
    	
    	jPanel.add(lSize);
    	jPanel.add(tfSize);
    	
    	jPanel.add(lColor);
    	jPanel.add(tfColor);
    	
    	jPanel.add(lMaker);
    	jPanel.add(tfMaker);
    	
    	add(jPanel,BorderLayout.NORTH);
    	add(btnCarReg,BorderLayout.SOUTH);
    	
        setLocation(400, 200);
        setSize(400,400);
        setModal(true); //항상 부모창 위에 보이게 한다.
        setVisible(true);
    }
    
    
    
    private void showMessage(String msg){
    	JOptionPane.showMessageDialog(this,
    			msg, 
    			"메지지박스",
               JOptionPane.INFORMATION_MESSAGE);
    }
}
