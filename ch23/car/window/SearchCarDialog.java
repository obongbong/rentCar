package com.oracle.rent.ch23.car.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oracle.rent.ch23.Socket.Client;
import com.oracle.rent.ch23.car.controller.CarController;
import com.oracle.rent.ch23.car.vo.CarVO;
import com.oracle.rent.ch23.common.RentTableModel;
import com.oracle.rent.ch23.res.window.RegResDialog;

public class SearchCarDialog extends JDialog {
	JPanel panelSearch, panelBtn;
	JLabel lCarName;
	JTextField tf;
	JButton btnSearch;
	JButton btnReg;
	JButton btnModify;
	JButton btnDelete;
	
	JButton btnResReg; // 렌터카 예약하기 버튼 

	JTable carTable;
	RentTableModel rentTableModel;
	String[] columnNames = { "차번호", "차이름", "차색상", "배기량", "차제조사" };
	Object[][] carItems;
	int rowIdx = 0, colIdx = 0; // 테이블 수정 시 선택한 행과 열 인덱스 저장

	CarController carController;
	Client client;
/*
	public SearchCarDialog(CarController carController, String str) {
		this.carController = carController;
		setTitle(str);
		init();
		

	}
*/
	public SearchCarDialog(CarController carController, String title) {
		this.carController = carController;
		setTitle(title);
		try {
			this.client = new Client("localhost", 5002); // 각 창마다 새로운 클라이언트 인스턴스 생성
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "서버 연결 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
		}
		init();
	}

	private void init() {
		carTable = new JTable();
		ListSelectionModel rowSel = carTable.getSelectionModel();
		rowSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowSel.addListSelectionListener(new ListRowSelectionHandler()); // 테이블 행 클릭 시 이벤트 처리

		ListSelectionModel colSel = carTable.getColumnModel().getSelectionModel();
		colSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		colSel.addListSelectionListener(new ListColSelectionHandler());
		
		panelSearch = new JPanel();
		panelBtn = new JPanel();

		lCarName = new JLabel("차번호");
		tf = new JTextField("차번호을 입력하세요.");
		btnSearch = new JButton("조회하기");
		btnSearch.addActionListener(new CarBtnHandler());

		panelSearch.add(lCarName);
		panelSearch.add(tf);
		panelSearch.add(btnSearch);
		
		btnReg = new JButton("렌터카등록하기");
		btnModify = new JButton("수정하기");
		btnDelete = new JButton("삭제하기");
		btnResReg = new JButton("렌터카 예약하기");
		
		btnReg.addActionListener(new CarBtnHandler());
		btnModify.addActionListener(new CarBtnHandler());
		btnDelete.addActionListener(new CarBtnHandler());
		btnResReg.addActionListener(new ResBtnHandler());

		panelBtn.add(btnReg);
		panelBtn.add(btnModify);
		panelBtn.add(btnDelete);
		panelBtn.add(btnResReg);

		add(panelSearch, BorderLayout.NORTH);
		add(panelBtn, BorderLayout.SOUTH);

		carItems = new String[0][5];
		rentTableModel = new RentTableModel(carItems, columnNames);
		carTable.setModel(rentTableModel);
		add(new JScrollPane(carTable), BorderLayout.CENTER);

		setLocation(300, 100);// 다이얼로그 출력 위치를 정한다.
		setSize(600, 600);
		setModal(true); // 항상 부모창 위에 보이게 한다.
		setVisible(true);
	}

	private void loadTableData(List<CarVO> carList) {
		if (carList != null && carList.size() != 0) {
			carItems = new String[carList.size()][5];

			for (int i = 0; i < carList.size(); i++) {
				CarVO carVO = carList.get(i);
				carItems[i][0] = carVO.getCarNumber();
				carItems[i][1] = carVO.getCarName();
				carItems[i][2] = carVO.getCarColor();
				carItems[i][3] = Integer.toString(carVO.getCarSize());
				carItems[i][4] = carVO.getCarMaker();
			}

			rentTableModel = new RentTableModel(carItems, columnNames);
			carTable.setModel(rentTableModel);
		} else {
			message("조회한 정보가 없습니다.");
			carItems = new Object[10][10];
			rentTableModel = new RentTableModel(carItems, columnNames);
			carTable.setModel(rentTableModel);
		}
	}

	private void message(String msg) {
		JOptionPane.showMessageDialog(this, msg, "메지지박스", JOptionPane.INFORMATION_MESSAGE);
	}

	class CarBtnHandler implements ActionListener {
		String carNumber = null, carName = null, carColor = null, carMaker = null;
		int carSize = 0;
		List<CarVO> carList = null;

        @Override
        public void actionPerformed(ActionEvent e) {
			if (!client.isConnected()) {
				client.reconnectToServer();
			}
            if (e.getSource() == btnSearch) {

                String carNumber = tf.getText().trim();
                CarVO carVO = new CarVO();
                if (carNumber != null && carNumber.length() != 0) {
                    carVO.setCarNumber(carNumber);
                }

				// SwingWorker를 사용하여 차량 조회를 백그라운드에서 수행
				SwingWorker<List<CarVO>, Void> worker = new SwingWorker<List<CarVO>, Void>() {
					@Override
					protected List<CarVO> doInBackground() throws Exception {
						synchronized(client){
							client.sendCarRequest("차량 조회", carVO);
							return client.receiveCarListResponse();
						}
					}

					@Override
					protected void done() {
						try {
							carList = get();
							loadTableData(carList);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};

				worker.execute(); // SwingWorker 실행
				/* 
                try {
                    client.sendCarRequest("차량 조회", carVO);
                    carList = client.receiveCarListResponse();
                    loadTableData(carList);
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                return;
				*/
            } else if (e.getSource() == btnDelete) {
                carNumber = (String) carItems[rowIdx][0];
                carName = (String) carItems[rowIdx][1];
                carColor = (String) carItems[rowIdx][2];
                carSize = Integer.parseInt((String) carItems[rowIdx][3]);
                carMaker = (String) carItems[rowIdx][4];
                CarVO carVO = new CarVO(carNumber, carName, carColor, carSize, carMaker);

                try {
                    client.sendCarRequest("차량 삭제", carVO);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else if (e.getSource() == btnModify) {
                carNumber = (String) carItems[rowIdx][0];
                carName = (String) carItems[rowIdx][1];
                carColor = (String) carItems[rowIdx][2];
                carSize = Integer.parseInt((String) carItems[rowIdx][3]);
                carMaker = (String) carItems[rowIdx][4];
                CarVO carVO = new CarVO(carNumber, carName, carColor, carSize, carMaker);

				try {
					client.sendCarRequest("차량 수정", carVO);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
                //carController.modCarInfo(carVO);

            } else if (e.getSource() == btnReg) {
                new RegCarDialog(carController, "차량 등록창");
                return;
            }

            //carList = carController.listCarInfo(new CarVO());
            //loadTableData(carList);
        }
    }
	
	
	class ResBtnHandler implements ActionListener {
		String carNumber = null, carName = null, carColor = null, carMaker = null;
		int carSize = 0;
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btnResReg) {
				carNumber = (String) carItems[rowIdx][0];
				System.out.println("차번호 : " + carNumber);
				
//				carName = (String) carItems[rowIdx][1];
//				carColor = (String) carItems[rowIdx][2];
//				carSize = Integer.parseInt((String) carItems[rowIdx][3]);
//				carMaker = (String) carItems[rowIdx][4];
//				CarVO carVO = new CarVO(carNumber, carName, carColor, carSize, carMaker);
				
				new RegResDialog(carNumber);
			}
			
		}
		
	}

	// 테이블의 행 클릭 시 이벤트 처리
	class ListRowSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				rowIdx = lsm.getMinSelectionIndex();
				System.out.println(rowIdx + " 번째 행이 선택됨...");
//				System.out.println(memData[rowIdx][colIdx]);
			}
		}
	}

	class ListColSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			colIdx = lsm.getMinSelectionIndex();
			if (!e.getValueIsAdjusting()) {
				System.out.println(rowIdx + " 번째 행, " + colIdx + "열 선택됨...");
//			System.out.println(carData[rowIdx][colIdx]);
			}
		}

	}

}
