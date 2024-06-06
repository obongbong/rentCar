package com.oracle.rent.ch23.res.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
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
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oracle.rent.ch23.Socket.Client;
import com.oracle.rent.ch23.common.RentTableModel;
import com.oracle.rent.ch23.res.controller.ResController;
import com.oracle.rent.ch23.res.vo.ResVO;

public class SearchResDialog extends JDialog {
	JPanel panelSearch, panelBtn;
	JLabel lResNumber;
	JTextField tfResNumber;
	JButton btnResSearch;
	JButton btnResReg;
	JButton btnResModify;
	JButton btnResDelete;
	JButton btnPayment;
	JButton btnSales;

	JTable resTable;
	RentTableModel rentTableModel;
	String[] columnNames = { "예약번호", "예약차번호", "예약일자", "렌터카이용시작일자", "렌터카반납일자", "예약자아이디", "결제 여부", "결제일" };

	Object[][] resItems = new String[0][8]; // 테이블에 표시될 회원 정보 저장 2차원 배열
	int rowIdx = 0, colIdx = 0; // 테이블 수정 시 선택한 행과 열 인덱스 저장

	ResController resController;
	Client client;
	
	public SearchResDialog(ResController resController, String str) {
		this.resController = resController;
		setTitle(str);
				try {
			this.client = new Client("localhost", 5002);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();

	}

	private void init() {
		resTable = new JTable();
		ListSelectionModel rowSel = resTable.getSelectionModel();
		rowSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowSel.addListSelectionListener(new ListRowSelectionHandler()); // 테이블 행 클릭 시 이벤트 처리

		ListSelectionModel colSel = resTable.getColumnModel().getSelectionModel();
		colSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		colSel.addListSelectionListener(new ListColSelectionHandler());

		panelSearch = new JPanel();
		panelBtn = new JPanel();

		lResNumber = new JLabel("예약번호");
	
		tfResNumber = new JTextField("예약번호를 입력하세요");
		btnResSearch = new JButton("조회하기");
		btnResSearch.addActionListener(new ResBtnHandler());

		panelSearch.add(lResNumber);
		panelSearch.add(tfResNumber);
		panelSearch.add(btnResSearch);

		btnResReg = new JButton("렌터카 예약하기");
		btnResModify = new JButton("수정하기");
		btnResDelete = new JButton("삭제하기");
		btnPayment = new JButton("결제");
		btnSales = new JButton("매출");

		btnResReg.addActionListener(new ResBtnHandler());
		btnResModify.addActionListener(new ResBtnHandler());
		btnResDelete.addActionListener(new ResBtnHandler());
		btnPayment.addActionListener(new ResBtnHandler());
		btnSales.addActionListener(new ResBtnHandler());

		panelBtn.add(btnResReg);
		panelBtn.add(btnResModify);
		panelBtn.add(btnResDelete);
		panelBtn.add(btnPayment);
		panelBtn.add(btnSales);

		add(panelSearch, BorderLayout.NORTH);
		add(panelBtn, BorderLayout.SOUTH);

		rentTableModel = new RentTableModel(resItems, columnNames);
		resTable.setModel(rentTableModel);
		add(new JScrollPane(resTable), BorderLayout.CENTER);

		setLocation(300, 100);// 다이얼로그 출력 위치를 정한다.
		setSize(600, 600);
		setModal(true); // 항상 부모창 위에 보이게 한다.
		setVisible(true);
	}

	private void loadTableData(List<ResVO> resList) {
		if (resList != null && resList.size() != 0) {
			resItems = new String[resList.size()][8];
			for (int i = 0; i < resList.size(); i++) {
				ResVO resVO = resList.get(i);
				resItems[i][0] = resVO.getResNumber();
				resItems[i][1] = resVO.getResCarNumber();
				resItems[i][2] = resVO.getResDate();
				resItems[i][3] = resVO.getUseBeginDate();
				resItems[i][4] = resVO.getReturnDate();
				resItems[i][5] = resVO.getResUserId();
				resItems[i][6] = resVO.getResPaymentStatus();
				resItems[i][7] = resVO.getResPaymentDate();
			}

			rentTableModel = new RentTableModel(resItems, columnNames);
			resTable.setModel(rentTableModel);
		} else {
			showMessage("조회한 정보가 없습니다.");
			resItems = new Object[10][10];
			rentTableModel = new RentTableModel(resItems, columnNames);
			resTable.setModel(rentTableModel);
		}
	}

	private void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "메지지박스", JOptionPane.INFORMATION_MESSAGE);
	}

	class ResBtnHandler implements ActionListener {
		String resNumber = null, resCarNumber = null, resDate = null, useBeginDate = null, returnDate = null, resUserId = null, resPaymentStatus = null, resPaymentDate = null;
		List<ResVO> resList = null;

		@Override
        public void actionPerformed(ActionEvent e) {
            if (!client.isConnected()) {
                client.reconnectToServer();
            }
            if (e.getSource() == btnResSearch) {
                String resNumber = tfResNumber.getText().trim();
                ResVO resVO = new ResVO();

                if (resNumber != null && resNumber.length() != 0) {
                    resVO.setResNumber(resNumber);
                }		



                SwingWorker<List<ResVO>, Void> worker = new SwingWorker<List<ResVO>, Void>() {
                    @Override
                    protected List<ResVO> doInBackground() throws Exception {
                        synchronized (client) {
                            client.sendResRequest("예약 조회", resVO);
                            return client.receiveResListResponse();
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            resList = get();
                            loadTableData(resList);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

                worker.execute(); // SwingWorker 실행
            } else if (e.getSource() == btnResDelete) {
                resNumber = (String) resItems[rowIdx][0];
                resCarNumber = (String) resItems[rowIdx][1];
                resDate = (String) resItems[rowIdx][2];
                useBeginDate = (String) resItems[rowIdx][3];
                returnDate = (String) resItems[rowIdx][4];
                resUserId = (String) resItems[rowIdx][5];
                resPaymentStatus = (String) resItems[rowIdx][6];
				resPaymentDate = (String) resItems[rowIdx][7];
                ResVO resVO = new ResVO(resNumber, resCarNumber, resDate, useBeginDate, returnDate, resUserId, resPaymentStatus, resPaymentDate);

                try {
                    client.sendResRequest("예약 삭제", resVO);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else if (e.getSource() == btnResModify) {
                resNumber = (String) resItems[rowIdx][0];
                resCarNumber = (String) resItems[rowIdx][1];
                resDate = (String) resItems[rowIdx][2];
                useBeginDate = (String) resItems[rowIdx][3];
                returnDate = (String) resItems[rowIdx][4];
                resUserId = (String) resItems[rowIdx][5];
                resPaymentStatus = (String) resItems[rowIdx][6];
				resPaymentDate = (String) resItems[rowIdx][7];
                ResVO resVO = new ResVO(resNumber, resCarNumber, resDate, useBeginDate, returnDate, resUserId, resPaymentStatus, resPaymentDate);

                try {
                    client.sendResRequest("예약 수정", resVO);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } else if (e.getSource() == btnResReg) {
                new RegResDialog(resController, "예약 등록창");
                return;
            } else if (e.getSource() == btnPayment){
				resNumber = (String) resItems[rowIdx][0];
				resCarNumber = (String) resItems[rowIdx][1];
				resDate = (String) resItems[rowIdx][2];
				useBeginDate = (String) resItems[rowIdx][3];
				returnDate = (String) resItems[rowIdx][4];
				resUserId = (String) resItems[rowIdx][5];
				resPaymentStatus = (String) resItems[rowIdx][6];
				resPaymentDate = (String) resItems[rowIdx][7];
				ResVO resVO = new ResVO(resNumber, resCarNumber, resDate, useBeginDate, returnDate, resUserId, resPaymentStatus, resPaymentDate);
				//resVO.setResPaymentStatus("결제");

				try {
					client.sendResRequest("결제", resVO);

					// 서버로부터 받은 메시지 저장
					String paymentMessage = client.receiveMessage();

					// 메시지를 showMessageDialog로 출력
					showMessageDialog(paymentMessage);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if (e.getSource() == btnSales) {
				// 오늘 날짜 가져오기
				LocalDate today = LocalDate.now();
				int totalSales = 0; // 총 매출액 변수 초기화
				
				// 오늘 날짜 출력
				System.out.println("오늘 날짜: " + today.toString());
				
				// 테이블의 각 행을 순회하며 결제일과 오늘 날짜를 비교하여 총 매출액 계산
				for (int i = 0; i < resItems.length; i++) {
					String paymentDate = (String) resItems[i][7]; // 결제일
					// 결제일이 null이거나 포맷이 올바르지 않은 경우 건너뜀
					if (paymentDate == null || paymentDate.length() < 10) {
						continue;
					}
					// 결제일의 YYYY-MM-DD 부분만 추출
					String paymentDateYYYYMMDD = paymentDate.substring(0, 10);
					// 결제일 출력
					System.out.println("결제일: " + paymentDateYYYYMMDD);
					if (paymentDateYYYYMMDD.equals(today.toString())) { // 결제일의 YYYY-MM-DD 부분과 오늘 날짜 비교
						// 해당 예약 정보에 대한 ResVO 객체 생성
						String resNumber = (String) resItems[i][0];
						String resCarNumber = (String) resItems[i][1];
						String resDate = (String) resItems[i][2];
						String useBeginDate = (String) resItems[i][3];
						String returnDate = (String) resItems[i][4];
						String resUserId = (String) resItems[i][5];
						String resPaymentStatus = (String) resItems[i][6];
						String resPaymentDate = (String) resItems[i][7];
						ResVO resVO = new ResVO(resNumber, resCarNumber, resDate, useBeginDate, returnDate, resUserId, resPaymentStatus, resPaymentDate);
						
						// 대여일 계산
						int rentalDays = resController.getResDate(resVO); // getResDate 메서드 호출하여 대여일 구함
						// 하루 매출금액 계산
						int dailySales = rentalDays * 40000;
						totalSales += dailySales; // 총 매출액에 하루 매출금액 추가
					}
				}
				
				// 계산된 총 매출액을 메시지 창으로 출력
				showMessageDialog("오늘 하루의 매출은 " + totalSales + "원 입니다.");
			}
			
        }
    }// end MemberBtnHandler

	private void showMessageDialog(String message) {
		// 여기서는 적절한 UI 창을 사용하여 message를 출력합니다.
		// 예를 들어, Java Swing에서는 JOptionPane.showMessageDialog() 메서드를 사용할 수 있습니다.
		JOptionPane.showMessageDialog(null, message);
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
			colIdx = lsm.getMinSelectionIndex(); // 클릭한 열 인덱스를 얻습니다.
			if (!e.getValueIsAdjusting()) {
				System.out.println(rowIdx + " 번째 행, " + colIdx + "열 선택됨...");
//			System.out.println(memData[rowIdx][colIdx]);
			}
		}

	}

}
