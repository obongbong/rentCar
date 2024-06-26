package com.oracle.rent.ch23.res.vo;

import java.io.Serializable;

public class ResVO implements Serializable{
	private String resNumber;  //예약 번호
	private String resCarNumber;  //예약 차번호
	private String resDate;
	private String useBeginDate;
	private String returnDate;
	private String resUserId;  //예약자 아이디
	private String resPaymentStatus;
	private String resPaymentDate; //결제일

	//생성자
	public ResVO() {}
	public ResVO(String resNumber, String resCarNumber, String resDate, String useBeginDate, String returnDate, String resUserId, String resPaymentStatus, String resPaymentDate) {
		this.resNumber = resNumber;
		this.resCarNumber = resCarNumber;
		this.resDate = resDate;
		this.useBeginDate = useBeginDate;
		this.returnDate = returnDate;
		this.resUserId = resUserId;
		this.resPaymentStatus = resPaymentStatus;
		this.resPaymentDate = resPaymentDate;
	}
	public ResVO(String resNumber, String resCarNumber, String resDate, String useBeginDate, String returnDate, String resUserId) {
		this.resNumber = resNumber;
		this.resCarNumber = resCarNumber;
		this.resDate = resDate;
		this.useBeginDate = useBeginDate;
		this.returnDate = returnDate;
		this.resUserId = resUserId;
	}
	
	public String getResNumber() {
		return resNumber;
	}
	public void setResNumber(String resNumber) {
		this.resNumber = resNumber;
	}
	public String getResCarNumber() {
		return resCarNumber;
	}
	public void setResCarNumber(String resCarNumber) {
		this.resCarNumber = resCarNumber;
	}
	public String getResDate() {
		return resDate;
	}
	public void setResDate(String resDate) {
		this.resDate = resDate;
	}
	public String getUseBeginDate() {
		return useBeginDate;
	}
	public void setUseBeginDate(String useBeginDate) {
		this.useBeginDate = useBeginDate;
	}
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	public String getResUserId() {
		return resUserId;
	}
	public void setResUserId(String resUserId) {
		this.resUserId = resUserId;
	}
	public String getResPaymentStatus(){
		return resPaymentStatus;
	}
	public void setResPaymentStatus(String resPaymentStatus){
		this.resPaymentStatus = resPaymentStatus;
	}
	public String getResPaymentDate(){
		return resPaymentDate;
	}
	public void setResPaymentDate(String resPaymentDate){
		this.resPaymentDate = resPaymentDate;
	}
	

}
