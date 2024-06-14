-- 멤버 테이블 생성
CREATE TABLE t_member(
    memId VARCHAR2(20) PRIMARY KEY,
    memPassword VARCHAR2(20),
    memName VARCHAR2(30),
    memAddress VARCHAR2(200),
    memPhoneNum VARCHAR2(50),
    memRank VARCHAR2(20) DEFAULT 'Bronze',
    memPoint NUMBER DEFAULT 0
);

ALTER TABLE t_member
ADD payment_count NUMBER DEFAULT 0;


-- 자동차 테이블 생성
CREATE TABLE t_Car(
    carNumber VARCHAR2(30) PRIMARY KEY,
    carName VARCHAR(50),
    carColor VARCHAR2(30),
    carSize NUMBER(5),
    carMaker VARCHAR2(50)
);

-- 예약 테이블 생성
CREATE TABLE t_Res(
    resNumber VARCHAR2(30) PRIMARY KEY,
    resDate DATE,
    useBeginDate DATE,
    returnDate DATE,
    resCarNumber VARCHAR2(30),
    resUserId VARCHAR2(20),
    payment_status VARCHAR2(20) DEFAULT '미결제',
    
    CONSTRAINT FK_RES_USER_ID FOREIGN KEY(resUserId) REFERENCES t_member (memId),
    CONSTRAINT FK_RES_CAR_NUMBER FOREIGN KEY(resCarNumber) REFERENCES t_Car (carNumber)
);




ALTER TABLE t_Res
ADD paymentDate DATE NULL;

CREATE OR REPLACE TRIGGER trg_update_payment_date
BEFORE UPDATE OF payment_status ON t_Res
FOR EACH ROW
BEGIN
    IF :NEW.payment_status = '결제' AND :OLD.payment_status != '결제' THEN
        :NEW.paymentDate := SYSDATE;
    END IF;
END;
/


CREATE OR REPLACE TRIGGER update_payment_count
AFTER UPDATE OF payment_status ON t_Res
FOR EACH ROW
BEGIN
    IF :NEW.payment_status = '결제' AND :OLD.payment_status <> :NEW.payment_status THEN
        UPDATE t_member
        SET payment_count = payment_count + 1
        WHERE memId = :NEW.resUserId;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER update_payment_count
AFTER UPDATE OF payment_status ON t_Res
FOR EACH ROW
BEGIN
    IF :NEW.payment_status = '결제' AND :OLD.payment_status <> :NEW.payment_status THEN
        -- 결제 상태가 '결제'로 변경될 때 payment_count를 증가시킴
        UPDATE t_member
        SET payment_count = payment_count + 1
        WHERE memId = :NEW.resUserId;
    ELSIF :NEW.payment_status = '미결제' AND :OLD.payment_status <> :NEW.payment_status THEN
        -- 결제 상태가 '미결제'로 변경될 때 payment_count를 감소시킴
        UPDATE t_member
        SET payment_count = payment_count - 1
        WHERE memId = :NEW.resUserId;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER update_member_rank
BEFORE UPDATE OF payment_count ON t_member
FOR EACH ROW
BEGIN
    IF :NEW.payment_count >= 10 AND :NEW.payment_count < 20 THEN
        :NEW.memRank := 'SILVER';
    ELSIF :NEW.payment_count >= 20 THEN
        :NEW.memRank := 'GOLD';
    ELSE
        :NEW.memRank := 'Bronze'; -- 0에서 9 사이의 경우 Bronze로 설정
    END IF;
END;
/





CREATE OR REPLACE TRIGGER update_member_rank
BEFORE UPDATE OF payment_count ON t_member
FOR EACH ROW
BEGIN
    IF :NEW.payment_count >= 10 AND :NEW.payment_count < 20 THEN
        :NEW.memRank := 'SILVER';
    ELSIF :NEW.payment_count >= 20 THEN
        :NEW.memRank := 'GOLD';
    END IF;
END;
/





select *
from t_member

select *
from t_res

-- 멤버 테이블에 예시 데이터 삽입
INSERT INTO t_member(memId, memPassword, memName, memAddress, memPhoneNum)
VALUES ('user1', 'password1', 'John Doe', '123 Main St, City, Country', '123-456-7890');

-- 자동차 테이블에 예시 데이터 삽입
INSERT INTO t_Car(carNumber, carName, carColor, carSize, carMaker)
VALUES ('ABC123', 'Toyota Corolla', 'Silver', 4500, 'Toyota');

-- 예약 테이블에 예시 데이터 삽입
-- user1이 ABC123 차량을 미결제 상태로 3번 예약한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('1', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('2', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('3', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('4', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('5', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('6', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('7', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('8', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('9', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('10', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('11', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- user1이 ABC123 차량을 추가로 결제한 상태
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('12', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '미결제');

-- 예약 번호가 '1'인 예약 정보의 결제 상태를 "미결제"에서 "결제"로 업데이트
UPDATE t_Res
SET payment_status = '결제'
WHERE resNumber = '10'
AND payment_status = '미결제';


-- 예약 번호가 '1'인 예약 정보의 결제 상태를 "미결제"에서 "결제"로 업데이트
UPDATE t_Res
SET payment_status = '미결제'
WHERE resNumber = '10'
AND payment_status = '결제';


delete from t_member;

delete from t_car;

delete from t_res;

select *
from t_member;

select *
from t_car;

select *
from t_Res;

select memId, memRank
from t_member
where memid = 'user1';


drop table t_member;
drop table t_car;
drop table t_Res;
drop table t_payment;