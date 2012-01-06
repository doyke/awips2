C MEMBER PRP13
C  (from old member FCPRP13)
C
      SUBROUTINE PRP13(P)
C
C     THIS SUBROUTINE PRINTS THE PARAMETERS FOR THE
C     TATUM COEFFICIENT ROUTING OPERATION
C
C     THIS SUBROUTINE INITIALLY WRITTEN BY
C          DAVID REED--HRL    DEC. 1979
C
      DIMENSION P(1)
C
C     COMMON BLOCKS
C
      COMMON/FDBUG/IODBUG,ITRACE,IDBALL,NDEBUG,IDEBUG(20)
      COMMON/IONUM/IN,IPR,IPU
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcinit_prpc/RCS/prp13.f,v $
     . $',                                                             '
     .$Id: prp13.f,v 1.1 1995/09/17 18:50:00 dws Exp $
     . $' /
C    ===================================================================
C
C
C     CHECK TRACE LEVEL--FOR THIS SUBROUTINE =1
C
      IF(ITRACE.GE.1)WRITE(IODBUG,900)
  900 FORMAT(1H0,16H** PRP13 ENTERED)
C
C     NO DEBUG OUTPUT FOR THIS SUBROUTINE
C
      IDTQI=P(10)
      IDTQO=P(14)
      NL=P(16)
      NLM1=NL-1
      NQST=17+NL
      NCST=NQST+NLM1
C
C     CHECK TO SEE IF ROUTING AT A REACH OR A POINT
C
      IF(IDTQO.EQ.0) GO TO 100
C
C     PRINT HEADING FOR ROUTING THROUGH A REACH
C
      WRITE(IPR,901)(P(I),I=2,6)
  901 FORMAT(1H0,10X,35HTATUM COEFFICIENT ROUTING FOR REACH,1X,5A4)
      WRITE(IPR,902)IDTQI,NL
  902 FORMAT(1H0,10X,31HCOMPUTATIONAL TIME INTERVAL IS ,I3,6H HOURS
     1//11X,17HNUMBER OF LAYERS ,I2)
      WRITE(IPR,903)
  903 FORMAT(1H0,20X,34HTIME SERIES USED BY THIS OPERATION//16X,
     18HCONTENTS,17X,4HI.D.,9X,4HTYPE,6X,13HTIME INTERVAL)
      WRITE(IPR,904)P(7),P(8),P(9),IDTQI,P(11),P(12),P(13),IDTQO
  904 FORMAT(1H0,15X,6HINFLOW,19X,2A4,5X,A4,7X,I2,6H HOURS//16X,
     17HOUTFLOW,18X,2A4,5X,A4,7X,I2,6H HOURS)
      GO TO 101
C
C     PRINT HEADING FOR ROUTING AT A POINT
C
  100 WRITE(IPR,905)(P(I),I=2,6)
  905 FORMAT(1H0,15X,29HTATUM COEFFICIENT ROUTING FOR,1X,5A4)
      WRITE(IPR,902)IDTQI,NL
      WRITE(IPR,903)
      WRITE(IPR,906)P(7),P(8),P(9),IDTQI
  906 FORMAT(1H0,15X,14HINFLOW-OUTFLOW,11X,2A4,5X,A4,7X,I2,6H HOURS)
C
C     PRINT PARAMETERS
C
  101 WRITE(IPR,907)
  907 FORMAT(1H0,15X,5HLAYER,12X,11HRANGE (CMS),20X,12HCOEFFICIENTS)
      IPTR=NCST-1
      DO 200 I=1,NL
      NCL=P(16+I)
      IPTR1 = IPTR+1
      IPTNCL = IPTR+NCL
      IF(NL.EQ.1)GO TO 201
      IF(I.EQ.1)GO TO 202
      IF(I.EQ.NL)GO TO 203
      NQSTI2 = NQST+I-2
      NQSTI1 = NQST+I-1
C
C  FOLLOWING WRITE CONVERTED TO CONFORM WITH VS FORTRAN LVL 66
C
C         WRITE(IPR,920)I,P(NQST+I-2),P(NQST+I-1),(P(IPTR+J),J=1,NCL)
C
      WRITE(IPR,920)I,P(NQSTI2),P(NQSTI1),(P(J),J=IPTR1,IPTNCL)
  920 FORMAT(1H0,17X,I2,5X,F10.3,4H TO ,F10.3,5X,10F6.3/(54X,10F6.3))
      GO TO 200
  201 WRITE(IPR,921)(P(J),J=IPTR1,IPTNCL)
  921 FORMAT(1H0,17X,2H 1,5X,19HQ GREATER THAN ZERO,10X,10F6.3/
     1 (54X,10F6.3))
      GO TO 200
  202 WRITE(IPR,922)I,P(NQST),(P(J),J=IPTR1,IPTNCL)
  922 FORMAT(1H0,17X,I2,5X,14HQ LESS THAN   ,F10.3,5X,10F6.3/
     1 (54X,10F6.3))
      GO TO 200
  203 WRITE(IPR,923)I,P(NCST-1),(P(J),J=IPTR1,IPTNCL)
  923 FORMAT(1H0,17X,I2,5X,14HQ GREATER THAN,F10.3,5X,10F6.3/
     1 (54X,10F6.3))
  200 IPTR=IPTR+NCL
      RETURN
      END

