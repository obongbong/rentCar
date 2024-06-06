package com.oracle.rent.ch23.res.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.oracle.rent.ch23.common.base.AbstractBaseDAO;
import com.oracle.rent.ch23.member.vo.MemberVO;
import com.oracle.rent.ch23.res.vo.ResVO;

public class ResDAOImpl  extends AbstractBaseDAO implements ResDAO{
	@Override
	public List<ResVO> selectResInfo(ResVO resVO) throws SQLException, ClassNotFoundException{
		List<ResVO> resList = new ArrayList<ResVO>();
		String _resNumber = resVO.getResNumber();
		if(_resNumber != null && _resNumber.length() != 0) {
			pstmt = conn.prepareStatement("SELECT resNumber,"
										     + "resCarNumber,"
					                         + "TO_CHAR(resDate,'YYYY-MM-DD') resDate," 
										     + "TO_CHAR(useBeginDate, 'YYYY-MM-DD') useBeginDate," 
					                         + "TO_CHAR(returnDate, 'YYYY-MM-DD') returnDate,"
										     + "resUserId,"
											 + "payment_status "
					                         + "FROM t_res  WHERE resNumber = ? ORDER BY resNumber");
			pstmt.setString(1, _resNumber);
			
		}else {
			pstmt = conn.prepareStatement("SELECT resNumber,"
				     + "resCarNumber,"
                     + "TO_CHAR(resDate,'YYYY-MM-DD') resDate," 
				     + "TO_CHAR(useBeginDate, 'YYYY-MM-DD') useBeginDate," 
                     + "TO_CHAR(returnDate, 'YYYY-MM-DD') returnDate,"
				     + "resUserId,"
					 + "payment_status "
				     + "FROM t_res  ORDER BY resNumber");
		}
		
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			String resNumber = rs.getString("resNumber");
			String resCarNumber = rs.getString("resCarNumber");
			String resDate = rs.getString("resDate");
			String useBeginDate = rs.getString("useBeginDate");
			String returnDate = rs.getString("returnDate");
			String resUserId = rs.getString("resUserId");
			String resPaymentStatus = rs.getString("payment_status");
			
			ResVO _resVO = new ResVO();
			
			_resVO.setResNumber(resNumber);
			_resVO.setResCarNumber(resCarNumber);
			_resVO.setResDate(resDate);
			_resVO.setUseBeginDate(useBeginDate);
			_resVO.setReturnDate(returnDate);
			_resVO.setResUserId(resUserId);
			_resVO.setResPaymentStatus(resPaymentStatus);
			
			resList.add(_resVO);
		} // end while
		rs.close();
		return resList;

	}

	@Override
	public void insertResInfo(ResVO resVO) throws SQLException, ClassNotFoundException{
		pstmt = conn.prepareStatement("INSERT INTO t_res (resNumber, resCarNumber, resDate, useBeginDate, returnDate, resUserId) " +
                "VALUES (?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, resVO.getResNumber());
		pstmt.setString(2, resVO.getResCarNumber());
		pstmt.setString(3, resVO.getResDate());
		pstmt.setString(4, resVO.getUseBeginDate());
		pstmt.setString(5, resVO.getReturnDate());
		pstmt.setString(6, resVO.getResUserId());
		
		pstmt.executeUpdate();	
		
	}

	@Override
	public void updateResInfo(ResVO resVO) throws SQLException, ClassNotFoundException{
		pstmt = conn.prepareStatement("UPDATE t_res SET  resCarNumber = ?, resDate = ?, useBeginDate = ?, returnDate = ?, resUserId = ?  "+
	                                  "WHERE resNumber = ? ");
		pstmt.setString(1, resVO.getResCarNumber());
		pstmt.setString(2, resVO.getResDate());
		pstmt.setString(3, resVO.getUseBeginDate());
		pstmt.setString(4, resVO.getReturnDate());
		pstmt.setString(5, resVO.getResUserId());
		pstmt.setString(6, resVO.getResNumber());
		
		pstmt.executeUpdate();		
	}

	@Override
	public void deleteResInfo(ResVO resVO) throws SQLException, ClassNotFoundException{
		pstmt = conn.prepareStatement("DELETE t_res WHERE resNumber = ?");
		pstmt.setString(1, resVO.getResNumber());
		pstmt.executeUpdate();
	}

	@Override
    public void updatePaymentStatus(ResVO resVO) throws ClassNotFoundException, SQLException {
		pstmt = conn.prepareStatement("UPDATE t_res SET payment_status = '결제' WHERE resNumber = ? ");
		//pstmt.setString(1, resVO.getResPaymentStatus());
		pstmt.setString(1, resVO.getResNumber());
		pstmt.executeUpdate();
    }

	//int 형으로 렌터카 대여일 리턴
	@Override
	public int getResDate(ResVO resVO) throws ClassNotFoundException, SQLException {
		int rentalDays = 0;
		try {
			// Connection 객체 생성 및 연결 코드
	
			pstmt = conn.prepareStatement("SELECT TO_DATE(TO_CHAR(returnDate, 'YYYYMMDD')) - TO_DATE(TO_CHAR(useBeginDate, 'YYYYMMDD')) AS rentalDays "
				+ "FROM t_res WHERE resNumber = ?");
			pstmt.setString(1, resVO.getResNumber());
	
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// ResultSet에서 대여일수 값을 가져와서 정수로 변환하여 rentalDays 변수에 저장
				rentalDays = rs.getInt("rentalDays");
			}
		} finally {
			// 리소스 해제 코드
		}
		return rentalDays+1;
	}
	
    @Override
    public int calculatePoints(ResVO resVO) throws ClassNotFoundException, SQLException {
        int rentalDays = getResDate(resVO);
        int totalAmount = rentalDays * 40000;
    
        int points = 0;
    
        try {
            // 예약한 회원 아이디를 이용해서 회원 정보를 조회
            //String resUserId = resVO.getResUserId();
            pstmt = conn.prepareStatement("SELECT memRank as memberRank FROM t_member WHERE memId = ?");
            pstmt.setString(1, resVO.getResUserId());
    
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String memRank = rs.getString("memberRank");
    
				System.out.println("Member Rank: " + memRank);
                // 등급에 따른 포인트 비율 계산
                switch (memRank) {
                    case "BRONZE":
                        points = (int) (totalAmount * 0.01);
                        break;
                    case "SILVER":
                        points = (int) (totalAmount * 0.03);
                        break;
                    case "GOLD":
                        points = (int) (totalAmount * 0.05);
                        break;
                    default:
                        points = 0; // 기본적으로 포인트가 0일 때
                        break;
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    
        return points;
    }

    @Override
    public void updateMemberPoints(ResVO resVO) throws ClassNotFoundException, SQLException {

    

	}
}