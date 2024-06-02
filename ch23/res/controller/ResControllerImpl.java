package com.oracle.rent.ch23.res.controller;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.oracle.rent.ch23.common.base.AbstractBaseController;
import com.oracle.rent.ch23.res.dao.ResDAO;
import com.oracle.rent.ch23.res.dao.ResDAOImpl;
import com.oracle.rent.ch23.res.vo.ResVO;

public class ResControllerImpl extends AbstractBaseController implements ResController{
	private ResDAO resDAO;
	
	public ResControllerImpl() {
		resDAO = new ResDAOImpl();
		
	}
	@Override
	public List<ResVO> listResInfo(ResVO resVO) {
		List<ResVO> resList = new ArrayList<ResVO>();
		try {
			resList = resDAO.selectResInfo(resVO);
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return resList;
		
	}

	@Override
	public void regResInfo(ResVO resVO)  {
		try {
			resDAO.insertResInfo(resVO);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void modResInfo(ResVO resVO)  {
		try {
			resDAO.updateResInfo(resVO);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();			
		}
					
	}

	@Override
	public void cancelResInfo(ResVO resVO)  {
		try {
			resDAO.deleteResInfo(resVO);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
    @Override
    public void updatePaymentStatus(ResVO resVO) {
        try {
            resDAO.updatePaymentStatus(resVO);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMemberPoints(ResVO resVO, int amount) {
        try {
            // 결제 금액을 기준으로 멤버 포인트를 계산하고 업데이트
            String memRank = resVO.getMemRank();
            double pointRatio = 0.0;

            // 멤버 랭크에 따른 포인트 적립 비율 설정
            switch (memRank) {
                case "BRONZE":
                    pointRatio = 0.01;
                    break;
                case "SILVER":
                    pointRatio = 0.03;
                    break;
                case "GOLD":
                    pointRatio = 0.05;
                    break;
                default:
                    break;
            }

            int pointsEarned = (int) (amount * pointRatio);
            resVO.setPointsEarned(pointsEarned);

            // 멤버 포인트 업데이트
            resDAO.updateMemberPoints(resVO);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
