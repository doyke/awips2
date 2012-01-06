C MEMBER FADD
C  (from old member FCEX10)
C
      SUBROUTINE FADD(IADD,ICO,NEG,IDT,IODT,QZERO,QIN,QOUT,QT)
C.......................................
C      THIS SUBROUTINE ADDS OR SUBTRACTS TIME SERIES VALUES.
C        FCWTCO IS CALLED IF FADD IS CALLED BY EX10.
C.......................................
C      WRITTEN BY ERIC ANDERSON - HRL OCT. 1979
C.......................................
      DIMENSION QIN(1),QOUT(1),QT(1)
C
C     COMMON BLOCKS
      COMMON/FDBUG/IODBUG,ITRACE,IDBALL,NDEBUG,IDEBUG(20)
      COMMON/FCTIME/IDARUN,IHRRUN,LDARUN,LHRRUN,LDACPD,LHRCPD,NOW(5),
     1   LOCAL,NOUTZ,NOUTDS,NLSTZ,IDA,IHR,LDA,LHR,IDADAT
      COMMON/FCARY/IFILLC,NCSTOR,ICDAY(20),ICHOUR(20)
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcst_ex/RCS/fadd.f,v $
     . $',                                                             '
     .$Id: fadd.f,v 1.1 1995/09/17 18:57:27 dws Exp $
     . $' /
C    ===================================================================
C
C.......................................
C     INITIAL CHECKS
      IF (ITRACE.GE.1) WRITE(IODBUG,901)
  901 FORMAT (1H0,15H** FADD ENTERED)
      IEADD=1
      IF (IADD.GE.0) GO TO 100
      IADD = IADD+2
      IEADD = 0
C.......................................
C     BEGIN EXECUTION - CHECK IF FCHGDT NEEDS TO BE CALLED.
  100 ISAME=1
      IF(IDT.NE.IODT)ISAME=0
      IF(ISAME.EQ.1) GO TO 110
C     CHANGE TIME INTERVAL.
      CALL FCHGDT(QIN(1),IDT,QT(1),IODT,QZERO)
C.......................................
C     CHECK IF CARRYOVER NEEDS TO BE SAVED.
      IF(ICO.NE.1) GO TO 110
      IF(IFILLC.EQ.0) GO TO 110
C     SAVE LAST VALUE IN QIN FOR THIS PERIOS
      I=(LDA-IDADAT)*24/IDT+LHR/IDT
      QZERO=QIN(I)
      IF(IEADD.EQ.1) GO TO 110
C
C     CHECK IF INTERMEDIATE CARRYOVER VALUES NEED TO BE SAVED.
      IF(NCSTOR.EQ.0) GO TO 110
      DO 105 J=1,NCSTOR
      KDA=ICDAY(J)
      KHR=ICHOUR(J)
      I=(KDA-IDADAT)*24/IDT+KHR/IDT
      CALL FCWTCO(KDA,KHR,QIN(I),1)
  105 CONTINUE
C.......................................
C     TIME INTERVALS ARE THE SAME--NOW ADD OR SUBTRACT.
  110 KDA=IDA
      KHR=IHR
C
C     GET LOCATION OF DATA VALUES IN THE TWO ARRAYS.
  130 I=(KDA-IDADAT)*24/IODT+KHR/IODT
C     GET VALUE TO BE ADDED OR SUBTRACTED FROM PROPER ARRAY.
      IF(ISAME.EQ.1) GO TO 111
      QPM=QT(I)
      GO TO 115
  111 QPM=QIN(I)
C
C     CHECK IF EITHER VALUE IS MISSING.
  115 IF(IFMSNG(QPM).EQ.1) GO TO 119
      IF(IFMSNG(QOUT(I)).EQ.1) GO TO 119
      GO TO 120
C
C     AT LEAST ONE VALUE IS MISSING - RESULT IS THUS MISSING.
  119 QOUT(I)=-999.0
      GO TO 125
C
C     BOTH VALUES ARE OKAY - ADD OR SUBTRACT.
  120 IF(IADD.EQ.1) GO TO 121
C
C     SUBTRACT VALUES.
      QOUT(I)=QOUT(I)-QPM
      GO TO 122
C
C     ADD VALUES.
  121 QOUT(I)=QOUT(I)+QPM
C
C     CHECK FOR NEGATIVE RESULT
  122 IF(NEG.EQ.1) GO TO 125
      IF(QOUT(I).LT.0.0) QOUT(I)=0.0
C
C     CHECK FOR END OF EXECUTION PERIOD
  125 IF((KDA.EQ.LDA).AND.(KHR.EQ.LHR)) GO TO 190
C     INCREMENT TO NEXT TIME STEP.
      KHR=KHR+IODT
      IF(KHR.LE.24) GO TO 130
      KHR=IODT
      KDA=KDA+1
      GO TO 130
C     END OF COMPUTATIONS
C.......................................
  190 CONTINUE
      IF (ITRACE.GE.1) WRITE(IODBUG,900)
  900 FORMAT(1H0,12H** EXIT FADD)
      RETURN
      END
