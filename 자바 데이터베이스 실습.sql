-- ��� ���̺� ����
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


-- �ڵ��� ���̺� ����
CREATE TABLE t_Car(
    carNumber VARCHAR2(30) PRIMARY KEY,
    carName VARCHAR(50),
    carColor VARCHAR2(30),
    carSize NUMBER(5),
    carMaker VARCHAR2(50)
);

-- ���� ���̺� ����
CREATE TABLE t_Res(
    resNumber VARCHAR2(30) PRIMARY KEY,
    resDate DATE,
    useBeginDate DATE,
    returnDate DATE,
    resCarNumber VARCHAR2(30),
    resUserId VARCHAR2(20),
    payment_status VARCHAR2(20) DEFAULT '�̰���',
    
    CONSTRAINT FK_RES_USER_ID FOREIGN KEY(resUserId) REFERENCES t_member (memId),
    CONSTRAINT FK_RES_CAR_NUMBER FOREIGN KEY(resCarNumber) REFERENCES t_Car (carNumber)
);




ALTER TABLE t_Res
ADD paymentDate DATE NULL;

CREATE OR REPLACE TRIGGER trg_update_payment_date
BEFORE UPDATE OF payment_status ON t_Res
FOR EACH ROW
BEGIN
    IF :NEW.payment_status = '����' AND :OLD.payment_status != '����' THEN
        :NEW.paymentDate := SYSDATE;
    END IF;
END;
/


CREATE OR REPLACE TRIGGER update_payment_count
AFTER UPDATE OF payment_status ON t_Res
FOR EACH ROW
BEGIN
    IF :NEW.payment_status = '����' AND :OLD.payment_status <> :NEW.payment_status THEN
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
    IF :NEW.payment_status = '����' AND :OLD.payment_status <> :NEW.payment_status THEN
        -- ���� ���°� '����'�� ����� �� payment_count�� ������Ŵ
        UPDATE t_member
        SET payment_count = payment_count + 1
        WHERE memId = :NEW.resUserId;
    ELSIF :NEW.payment_status = '�̰���' AND :OLD.payment_status <> :NEW.payment_status THEN
        -- ���� ���°� '�̰���'�� ����� �� payment_count�� ���ҽ�Ŵ
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
        :NEW.memRank := 'Bronze'; -- 0���� 9 ������ ��� Bronze�� ����
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

-- ��� ���̺� ���� ������ ����
INSERT INTO t_member(memId, memPassword, memName, memAddress, memPhoneNum)
VALUES ('user1', 'password1', 'John Doe', '123 Main St, City, Country', '123-456-7890');

-- �ڵ��� ���̺� ���� ������ ����
INSERT INTO t_Car(carNumber, carName, carColor, carSize, carMaker)
VALUES ('ABC123', 'Toyota Corolla', 'Silver', 4500, 'Toyota');

-- ���� ���̺� ���� ������ ����
-- user1�� ABC123 ������ �̰��� ���·� 3�� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('1', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('2', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('3', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('4', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('5', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('6', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('7', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('8', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('9', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('10', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('11', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- user1�� ABC123 ������ �߰��� ������ ����
INSERT INTO t_Res(resNumber, resDate, useBeginDate, returnDate, resCarNumber, resUserId, payment_status)
VALUES ('12', SYSDATE, SYSDATE, SYSDATE, 'ABC123', 'user1', '�̰���');

-- ���� ��ȣ�� '1'�� ���� ������ ���� ���¸� "�̰���"���� "����"�� ������Ʈ
UPDATE t_Res
SET payment_status = '����'
WHERE resNumber = '10'
AND payment_status = '�̰���';


-- ���� ��ȣ�� '1'�� ���� ������ ���� ���¸� "�̰���"���� "����"�� ������Ʈ
UPDATE t_Res
SET payment_status = '�̰���'
WHERE resNumber = '10'
AND payment_status = '����';


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