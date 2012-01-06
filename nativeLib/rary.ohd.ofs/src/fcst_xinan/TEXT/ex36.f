C MEMBER EX36
C  (from old member FCEX36)
C
      SUBROUTINE EX36(PL,CL,PE,LPXRO,D,CO,COI,CS,BADJ)
C.......................................
C     THIS IS THE EXECUTION SUBROUTINE FOR THE XINANJIANG MODEL
C          PRECIPITATION - CHANNEL INFLOW COMPOUTATION OPERATION.
C.......................................
C     SUBROUTINE INITIALLY WRITTEN BY. . .
C            QINGPING ZHU -YRCC CHINA   JUNE 1988     VERSION 1
C     MODIFIED BY ... ERIC ANDERSON HRL JULY 1988
C.......................................
      DIMENSION PL(1),CL(1),PE(1),LPXRO(1),D(1),CO(1),COI(1),CS(1)
      DIMENSION EPDIST(24),DIST(24),NDAYS(12),SNAME(2),BADJ(1)
      REAL K,IMP,KG,KSS,KSSD,KGD
C
C     COMMON BLOCKS.
      COMMON/FDBUG/IODBUG,ITRACE,IDBALL,NDEBUG,IDEBUG(20)
      COMMON/IONUM/IN,IPR,IPU
      COMMON/FCTIME/IDARUN,IHRRUN,LDARUN,LHRRUN,LDACPD,LHRCPD,NOW(5),
     1LOCAL,NOUTZ,NOUTDS,NLSTZ,IDA,IHR,LDA,LHR,IDADAT
      COMMON/FCARY/IFILLC,NCSTOR,ICDAY(20),ICHOUR(20)
      COMMON/FNOPR/NOPROT
C     NOPROT-- INDICATES IF OPERATION SHOULD GENERATAS ANY PRINTER
C     OUTPUT .                 =0, NO PRINTER OUTPUT
C                              =1, USE CRITERIA DETERMINING.
      COMMON/FPROG/MAINUM,VERS,VDATE(2),PNAME(5),NDD
      COMMON/FSACPR/IPRSAC,NOFRZE
      COMMON/FPM36/K,IMP,WM,WUM,WLM,WDM,WMM,SM,SMM,B,EX,C,KSS,KG,
     1KSSD,KGD,CI,CG,CID,CGD
      COMMON/FCO36/WUC,WLC,WDC,SC,FRC,QIC,QGC
      COMMON/MOD136/NDT36,IDT36(10),IUT36(10),VAL36(8,10)
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcst_xinan/RCS/ex36.f,v $
     . $',                                                             '
     .$Id: ex36.f,v 1.1 1995/09/17 18:57:09 dws Exp $
     . $' /
C    ===================================================================
C
C.......................................
C
C     DATA STATEMENTS.
      DATA DIST/0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.02,0.05,0.10,0.16,
     10.20,0.18,0.14,0.09,0.05,0.01,0.0,0.0,0.0,0.0,0.0,0.0/
      DATA NDAYS/31,28,31,30,31,30,31,31,30,31,30,31/
      DATA SNAME/4HEX36,4H    /
C.......................................
C     CHECK TRACE LEVEL -- TRACE LEVEL FOR THIS SUBROUTINE=1.
C     CHECK TO SEE IF DEBUG OUTPUT IS NEEDED FOR THIS OPERATION.
      CALL FPRBUG(SNAME,1,36,IBUG)
C.......................................
C     VALUES OF CONTROL VARIABLES.
      IWARN=0
      IDT=PL(2)
      NUNIT=PL(3)
      ISUM=PL(9)
      IF(IFILLC.EQ.0) ISUM=0
      IPRINT=PL(10)
      IF (IPRSAC.EQ.1) IPRINT=1
      IF (IPRSAC.EQ.-1) IPRINT=0
      IF (MAINUM.EQ.2) IPRINT=0
      IF (MAINUM.EQ.4) IPRINT=0
      IF (NOPROT.EQ.1) IPRINT=0
      IPLET=PL(18)
      LP=PL(17)
      NC=NUNIT*7
      IF(ISUM.EQ.0) GO TO 104
      NC=NC+10*NUNIT
  104 IET=PL(11)
      IPLPM=PL(19)
      NV=24/IDT
C.......................................
C     DEBUG OUTPUT - PRINT PL( ) AND CL( )
      IF (IBUG.EQ.0) GO TO 105
      WRITE (IODBUG,910) LP,NC
  910 FORMAT(1H0,29HCONTENTS OF PL AND CL ARRAYS.,5X,
     121HNUMBER OF VALUES--PL=,I3,2X,3HCL=,I2)
      WRITE (IODBUG,901) (PL(I),I=1,LP)
  901 FORMAT (1H0,15F8.3)
      WRITE (IODBUG,901) (CL(I),I=1,NC)
C.......................................
C     DEFINE DAILY ET-DISTRIBUTION FOR INTERNAL TIME
  105 J=PL(20)
      IF (J.GT.0) GO TO 106
C     UNIFORM DAILY ET DISTRIBUTION
      V=1.0/NV
      DO 107 I=1,NV
  107 EPDIST(I)=V
      GO TO 110
C     DIURNAL DAILY ET DISTRIBUTION
  106 DO 108 I=1,NV
      I2=I*IDT
      I1=(I-1)*IDT+1
      EPDIST(I)=0.0
      DO 108 J=I1,I2
      L=J+LOCAL
      IF (L.GT.24) L=L-24
      EPDIST(I)=EPDIST(I)+DIST(L)
  108 CONTINUE
C.......................................
C     GET ACTUAL TIME FOR START OF EXECUTION PERIOD.
  110 CALL MDYH1 (IDA,IHR,MONTH,IDAY,IYEAR,IHOUR,NOUTZ,NOUTDS,TZCODE)
C     PRINT HEADING FOR DETAILED OUTPUT
      IF (IBUG.EQ.1) IPRINT=1
      IF (IPRINT.EQ.0) GO TO 115
      LAST=NDAYS(MONTH)
      IF ((MONTH.EQ.2).AND.(((IYEAR/4)*4).EQ.IYEAR)) LAST=LAST+1
      IF(IPRINT.EQ.1) GO TO 101
C     CHECK IF DETAILED OUTPUT NEEDED FOR THIS MONTH.
      ICKMO=PL(IPRINT)
      MOPR=PL(ICKMO)
      IYPR=PL(ICKMO+1)
      IF ((MONTH.EQ.MOPR).AND.(IYEAR.EQ.IYPR)) GO TO 102
      IPRINT=0
      GO TO 115
  102 IF (ICKMO.LT.(IPRINT+13)) ICKMO=ICKMO+2
      PL(IPRINT)=ICKMO+0.01
      IPRINT=1
C
  101 IOUT=IPR
      IF (IBUG.EQ.1) IOUT=IODBUG
      IF (IBUG.EQ.0) WRITE (IOUT,903)
  903 FORMAT (1H0)
      WRITE (IOUT,902) (PL(I),I=4,8),MONTH,IYEAR,TZCODE
  902 FORMAT (1H0,44HDETAILED SOIL-MOISTURE ACCOUNTING OUTPUT FOR,
     11X,5A4,5X,I2,1H/,I4,3X,10HTIME ZONE=,A4,10X,13HUNITS ARE MM.)
      WRITE(IOUT,903)
      WRITE (IOUT,904)
  904 FORMAT(1H ,4HUNIT,1X,3HDAY,1X,2HHR,3X,4HRAIN,1X,5HET-DM,1X,5HACT-E
     1,1X,6HRUNOFF,2X,2HRS,4X,3HRSS,4X,2HRG,5X,3HCIN,5X,3HQIC,5X,
     23HQGC,3X,2HWC,3X,3HWUC,3X,3HWLC,4X,3HWDC,3X,2HSC,4X,3HFRC)
C.......................................
C     GET PARAMETER VALUES
  115 CALL PM36(PL,IPLPM,IDT)
C.......................................
C     STORE INITIAL CARRYOVER
      J=7*NUNIT
      DO 113 I=1,J
      CO(I)=CL(I)
  113 COI(I)=CL(I)
C......................................
C     CHECK FOR CHANGES TO CARRYOVER.
      DO 114 I=1,NUNIT
  114 BADJ(I)=0.0
      IF(MAINUM.NE.1) GO TO 140
      NM=1
      IF(NDT36.EQ.0) GO TO 140
      L=IHR-IDT
      JH=(IDA-1)*24+L
      IF(JH.NE.IDT36(NM)) GO TO 140
  145 N=NM
      NM=NM+1
      CALL MOD36(IDA,L,NOUTZ,NOUTDS,N,NUNIT,CO,BADJ,IBUG,IPRINT,IOUT)
      IF(NM.GT.NDT36) GO TO 140
      IF(JH.EQ.IDT36(NM)) GO TO 145
C
C     INITIALIZE SUMS FOR EXECUTION PERIOD.
  140 J=NUNIT*5
      IF(ISUM.EQ.1) J=NUNIT*10
      DO 111 I=1,J
  111 CS(I)=0.0
      IF(ISUM.EQ.0) GO TO 109
C     INITIALIZE SUMS FOR TOTAL RUNNING PERIOD AT FIRST EXECUTION.
      IF(IDA.NE.IDARUN) GO TO 109
      IF(IHR.NE.IHRRUN) GO TO 109
      L=7*NUNIT
      DO 112 I=1,J
  112 CL(L+I)=0.0
C.......................................
C     INITIAL ET-DEMAND OR PE-ADJUSTMENT
C     GET MONTH AND DAY NUMBER FOR INTERNAL TIME
  109 CALL MDYH1(IDA,IHR,MOET,IDET,IYET,I,100,0,V)
C     DETERMINE NUMBER OF DAYS IN MONTH FOR INCREMENTING.
      ND=NDAYS(MOET)
      IF ((MOET.EQ.2).AND.(((IYET/4)*4).EQ.IYET)) ND=ND+1
      IF (IDET.LT.16) GO TO 117
      EI=PL(IPLET+11+MOET)
      V=IDET-16
      ET=PL(IPLET+MOET-1)+V*EI
      GO TO 118
  117 J=MOET-1
      IF (J.EQ.0) J=12
      EI=PL(IPLET+11+J)
      V=16-IDET
      ET=PL(IPLET+MOET-1)-V*EI
C
C     INITIAL TIMING VALUES AND DATA OFFSET IN DAYS.
  118 KDA=IDA
      KHR=IHR
      KOFF=KDA-IDADAT
      IC=1
C     IC IS A SAVE CARRYOVER COUNTER
      GO TO 200
C.......................................
C     BEGINNING OF DAY AND HOUR LOOP.
C.......................................
  205 IF (KHR.NE.IDT) GO TO 220
      ET=ET+EI
      IF (IDET.EQ.16) EI=PL(IPLET+11+MOET)
  200 IF (IET.GT.0) GO TO 201
C     NO PE INPUT - GET ET DEMAND FROM SEASONAL CURVE.
      EPUADJ=ET
      GO TO 202
C     DAILY PE TIME SERIES AVAIABLE
  201 EPUADJ=PE(KOFF+1)
      IF (EPUADJ.LT.0.00001) EPUADJ=0.0
      EPUADJ=EPUADJ*ET
  202 EP=EPUADJ*K
      ETD=EP
C     OBTAIN ET DEMAND FOR CURRENT TIME INTERVAL.
  220 J=KHR/IDT
      ETDM=ETD*EPDIST(J)
      KINT=KHR/IDT
      J=KOFF*(24/IDT)+KINT
C.......................................
C     BEGINNING OF UNIT LOOP.
C.......................................
      DO 130 IXIN=1,NUNIT
C     GET STATE VARIABLES OF THIS UNIT FOR CURRENT INTERVAL.
      I=7*(IXIN-1)
      WUC=CO(I+1)
      WLC=CO(I+2)
      WDC=CO(I+3)
      WC=WUC+WLC+WDC
      SC=CO(I+4)
      QIC=CO(I+5)
      QGC=CO(I+6)
      FRC=CO(I+7)
      IF(FRC.LT.0.0001) FRC=0.0001
C     OBTAIN  PERCIP. DATUM FOR CURRENT UNIT.
      LPX=J+LPXRO(2*IXIN-1)-1
      PXV=D(LPX)
C     PXV IS THE PRECIP. VALUE FOR THE TIME INTERVAL.
C.......................................
C     PERFORM PRECIPITATION -CHANNEL INFLOW COMPUTATIONS.
  225 CALL RO36(PXV,ETDM,E,R,RS,RI,RG,CIN,WC,WUC,WLC,WDC,SC,QIC,QGC,FRC)
C     SET A WARNING FLAG IF ZERO VALUE OF TENSION WATER OCCURED.
      IF(WC.LT.0.0001) IWARN=1
C.......................................
C     STORE CHANNEL INFLOW VALUE.
  169 LRO=J+LPXRO(2*IXIN)-1
      D(LRO)=CIN
C.......................................
      IF (IPRINT.EQ.0) GO TO 230
      WRITE(IOUT,907) IXIN,IDAY,IHOUR,PXV,ETDM,E,R,RS,RI,RG,CIN,QIC,QGC,
     1WC,WUC,WLC,WDC,SC,FRC
  907 FORMAT(1H0,2X,I2,2X,I2,1X,I2,1X,7F6.1,3F8.2,5F6.1,F6.2)
C.......................................
C    COMPUTE AND STORE SUMS
  230 L=(IXIN-1)*5
      IF(ISUM.EQ.1) L=(IXIN-1)*10
      CS(L+1)=CS(L+1)+PXV
      CS(L+2)=CS(L+2)+E
      CS(L+3)=CS(L+3)+RS
      CS(L+4)=CS(L+4)+RI
      CS(L+5)=CS(L+5)+RG
      IF(ISUM.EQ.0) GO TO 125
      CS(L+6)=CS(L+6)+R
      CS(L+7)=CS(L+7)+CIN
      CS(L+8)=CS(L+8)+QIC
      CS(L+9)=CS(L+9)+QGC
C     RETAIN CARRYOVERIN ARRAY FOR NEXT INTERVAL.
  125 CALL FSCO36(CO,IXIN)
  130 CONTINUE
C.......................................
C     END OF UNIT LOOP.
C.......................................
C     CHECK FOR CHANGES TO CARRYOVER.
      IF(MAINUM.NE.1) GO TO 235
      IF (NM.GT.NDT36) GO TO 235
      JH=JH+IDT
      IF(JH.NE.IDT36(NM)) GO TO 235
  234 N=NM
      NM=NM+1
      CALL MOD36(KDA,KHR,NOUTZ,NOUTDS,N,NUNIT,CO,BADJ,IBUG,IPRINT,IOUT)
      IF(NM.GT.NDT36) GO TO 235
      IF(JH.EQ.IDT36(NM)) GO TO 234
C.......................................
C     CHECK TO SEE IF CARRYOVER SHOULD BE SAVED.
  235 IF (IFILLC.EQ.0) GO TO 131
      IF (NCSTOR.EQ.0) GO TO 131
      IF (IC.GT.NCSTOR) GO TO 131
      IF ((KDA.EQ.ICDAY(IC)).AND.(KHR.EQ.ICHOUR(IC))) GO TO 120
      GO TO 131
C
C     CARRYOVER SHOULD BE SAVED.
  120 NC1=7*NUNIT
      CALL FCWTCO(KDA,KHR,CO,NC1)
      IC=IC+1
C.......................................
C     CHECK FOR THE END OF THE EXECUTION PERIOD.
  131 IF((KDA.EQ.LDA).AND.(KHR.EQ.LHR)) GO TO 170
C.......................................
C     INCREMENT TIME VALUES.
      IF (IPRINT.EQ.0) GO TO 155
      IHOUR=IHOUR+IDT
      IF (IHOUR.LE.24) GO TO 155
      IHOUR=IHOUR-24
      IDAY=IDAY+1
      IF (IDAY.LE.LAST) GO TO 155
      IDAY=1
      MONTH=MONTH+1
      IF (MONTH.LE.12) GO TO 154
      MONTH=1
      IYEAR=IYEAR+1
  154 LAST=NDAYS(MONTH)
      IF ((MONTH.EQ.2).AND.(((IYEAR/4)*4).EQ.IYEAR)) LAST=LAST+1
  155 KHR=KHR+IDT
      IF (KHR.LE.24) GO TO 205
      KHR=IDT
      KDA=KDA+1
      KOFF=KOFF+1
      IDET=IDET+1
      IF (IDET.LE.ND) GO TO 205
      IDET=1
      MOET=MOET+1
      IF (MOET.LE.12) GO TO 156
      MOET=1
      IYET=IYET+1
  156 ND=NDAYS(MOET)
      IF ((MOET.EQ.2).AND.(((IYET/4)*4).EQ.IYET)) ND=ND+1
      GO TO 205
C.......................................
C     END OF DAY AND HOUR LOOP.
C.......................................
  170 IF(IWARN.EQ.0) GO TO 172
      WRITE(IPR,933)  MONTH,IYEAR
      CALL WARN
  933 FORMAT(1H0,10X,47H **WARNING** TENSION WATER WAS DRIED UP DURING
     1 ,I2,1H/,I4,2H . )
C     COMPUTE AND STORE WATER BALANCE FOR ALL UNITS
  172 DO 175 IXIN=1,NUNIT
      LCO=(IXIN-1)*7
      LCS=(IXIN-1)*5
      IF(ISUM.EQ.1) LCS=(IXIN-1)*10
      SBAL=CS(LCS+1)-CS(LCS+2)-(CS(LCS+3)+CS(LCS+4)+CS(LCS+5))+
     1COI(LCO+1)+COI(LCO+2)+COI(LCO+3)-(CO(LCO+1)+CO(LCO+2)+CO(LCO+3))
     2-CO(LCO+4)*CO(LCO+7)+COI(LCO+4)*COI(LCO+7)+BADJ(IXIN)
      IF(ISUM.EQ.1) GO TO 171
      IF(ABS(SBAL).LE.1.0) GO TO 175
      WRITE( IPR,906) SBAL,IXIN
      CALL WARN
      GO TO 175
  171 CS(LCS+10)=SBAL
  175 CONTINUE
C     STORE CARRYOVERS FOR EXECUTION PERIOD IF REQUESTED.
      IF(IFILLC.EQ.0) GO TO 199
C     STORE SOIL MOISTURE CARRYOVER.
      J=7*NUNIT
      DO 174 I=1,J
  174 CL(I)=CO(I)
C     STORE THE SUM CARRYOVER FOR RUNNING PERIOD.
      IF(ISUM.EQ.0) GO TO 199
      L=NUNIT*7
      J=NUNIT*10
      DO 176 I=1,J
  176 CL(L+I)=CL(L+I)+CS(I)
C.......................................
  199 CONTINUE
      IF(ISUM.EQ.0) GO TO 211
      IF(NOPROT.EQ.1) GO TO 211
C     PRINT THE SUMS AND WATER BALANCE FOR EXECUTION PERIOD.
      WRITE(IPR,903)
      WRITE(IPR,905)  MONTH,IYEAR
      WRITE(IPR,911)
      WRITE(IPR,908)
  905 FORMAT(1H0,40X,24HEXECUTION PERIOD SUMMARY ,4H FOR,1X,I2,1H/,I4)
  911 FORMAT(1H0,9X,43H UNIT    TOTAL     ACTUAL SURFACE INTERFLOW
     1 ,55H   GROUND   TOTAL   CHANNEL  INTERFLOW  GROUND   WATER     )
  908 FORMAT(1X,18X,36HPRECIP.   EVAPOT. RUNOFF   RUNOFF
     1 ,55H RUNOFF  RUNOFF   INLFOW    INFLOW    INFLOW  BALANCE     )
      DO 204 IXIN=1,NUNIT
      J=(IXIN-1)*10
      WRITE(IPR,912) IXIN,(CS(I+J),I=1,10)
  912 FORMAT(1H0,10X,1X,I2,2X,10(1X,F8.1))
      SBAL=CS(J+10)
      IF (ABS(SBAL).LE.1.0) GO TO 204
      WRITE(IPR,906) SBAL,IXIN
  906 FORMAT (1H0,10X,48H**WARNING** WATER BALANCE RESIDUAL EXCEEDS 1 MM
     1.,3X,9HRESIDUAL=,F7.2,1X,8HFOR UNIT,1X,I2)
      CALL WARN
C
  204 CONTINUE
      IF((LDA.NE.LDARUN).OR.(LHR.NE.LHRRUN)) GO TO 211
      IF(IFILLC.EQ.0) GO TO 211
C     PRINT SUMS AND WATER BALANCE FOR RUNNING PERIOD IN CALIBRATION.
      WRITE(IPR,903)
      WRITE(IPR,909)
  909 FORMAT(1H0,40X,28HTOTAL RUNNING PERIOD SUMMARY)
      WRITE(IPR,911)
      WRITE(IPR,908)
      DO 206 IXIN=1,NUNIT
      J=7*NUNIT+(IXIN-1)*10
      WRITE(IPR,912) IXIN,(CL(I+J),I=1,10)
      TBAL=CL(J+10)
      IF (ABS(TBAL).LE.1.0) GO TO 206
      WRITE (IPR,906) TBAL,IXIN
      CALL WARN
C
  206 CONTINUE
C.......................................
C     DEBUG OUTPUT
  211 IF (IBUG.EQ.0) GO TO 212
      WRITE (IODBUG,901) (CL(I),I=1,NC)
  212 RETURN
      END
