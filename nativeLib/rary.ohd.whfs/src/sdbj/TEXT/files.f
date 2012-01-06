c ---------------------------------------------------------------------
      SUBROUTINE FILES(INA,IN,IOUT,IOUTA,IOUTR,IBC,IREPT,ICAT,IBTH,
     . IBOTH,ISAVR)
C
C          THIS SUBROUTINE DETERMINES AND OPENS THE FILES NEED TO RUN THE
C          SIMPLIFIED DAMBREAK MODEL ON THE PR1ME COMPUTER.
C
      CHARACTER*14 FILIN,FILOUT
      CHARACTER*1 YES,ANSW,PC
      CHARACTER DATFIL2*12
      COMMON /DATFL/DATFIL2
      COMMON/FILS/FILIN,FILOUT
      COMMON/PC/PC
      DATA YES/'Y'/
C
C          DEFINITION OF VARIABLES
C
C          IN=    BATCH INPUT FILE UNIT NO.
C          INA=   INTERACTIVE INPUT FILE UNIT NO.
C          IOUT=  OUTPUT FILE UNIT NO.
C          IOUTA= INTERACTIVE OUTPUT FILE UNIT NO.
C          IOUTR= MODIFIED INPUT FILE UNIT NO.
C          IBOTH= OUTPUT WILL BE BOTH INTERACTIVE AND STORED IN FILE
C
C          IBC=0  CREATING A FILE INTERACTIVELY
C          IBC=1  RUNNING A BATCH FILE
C          IBC=2  READING IN A BATCH FILE WHICH WILL BE MODIFIED
C          IBC=3  MODIFYING THE BATCH FILE
C          IBC=4  UPDATING DAM CATALOG
C
C          ICAT=0 RUN SMPDBK WITHOUT DAM CATALOG DATA
C          ICAT=1 GENERATE SMPDBK DATA SET USING DAM CATALOG DATA
C          ICAT=2 RUN SMPDBK USING DATA SET GENERATED FROM DAMCAT DATA
C
C          IREPT=0 INPUTING DATA FROM THE TERMINAL
C          IREPT>0 UPDATING DATA READ-IN FROM A FILE
C
      INA=0
      IF(PC.NE.'Y') INA=1
      IN=5
      IOUT=6
      IBOTH=IOUT
      IOUTA=INA
      IOUTR=7
      IBC=0
      IREPT=0
      IBTH=0
      ISAVR=0
      IF(ICAT.EQ.2) THEN
        IBC=2
        IREPT=1
        FILIN=DATFIL2
        GO TO 1006
      ELSE IF(ICAT.EQ.1) THEN
        IBC=4
        FILIN=DATFIL2
        GO TO 1016
      END IF
C
      PRINT *, 'IN FILES SECOND TIME'    
    8 WRITE(IOUTA,10,ERR=9)
      GO TO 11
    9 IOUTA=1
      INA=1
      GO TO 8
   10 FORMAT(' DO YOU WISH TO RUN AN EXISTING FILE?  (ENTER YES OR NO) '
     *)
   11 READ(INA,900) ANSW
      IF(ANSW.EQ.YES) IBC=1
      IF(IBC.EQ.0) GO TO 1005
C
      WRITE(IOUTA,20)
   20 FORMAT(' WHAT IS NAME OF THE DATA SET YOU WISH TO RUN? ')
      READ(INA,905) FILIN
      WRITE(IOUTA,30)
   30 FORMAT(' DO YOU WANT YOUR OUTPUT TO COME TO THE TERMINAL? ')
      READ(INA,900) ANSW
      IF(ANSW.EQ.YES) IOUT=IOUTA
      IF(ANSW.NE.YES) GO TO 35
      WRITE(IOUTA,32)
   32 FORMAT(' DO YOU ALSO WANT TO STORE THE OUTPUT IN A FILE? ')
      READ(INA,900) ANSW
      IF(ANSW.NE.YES) GO TO 5
      IOUT=IBOTH
      IBTH=1
   35 WRITE(IOUTA,40)
   40 FORMAT(' ENTER THE FILENAME FOR THE OUTPUT INFORMATION: '/)
      READ(INA,905)FILOUT
C
      OPEN(FILE=FILOUT,UNIT=IOUT)
    5 OPEN(FILE=FILIN,UNIT=IN,STATUS='OLD',ERR=45)
C
      GO TO 1025
   45 IBC=0
      CLOSE(IN)
      WRITE(IOUTA,47) FILIN
   47 FORMAT(/2X,'FILE= ',A40/'  THIS FILE DOES NOT EXIST.  DO YOU WANT
     .TO CREATE IT? '/)
      READ(INA,900) ANSW
      IF(ANSW.NE.YES) CALL EXIT
      WRITE(IOUTA,95)
      OPEN(FILE=FILIN,UNIT=IOUTR)
      GO TO 1025
C
 1005 WRITE(IOUTA,50)
   50 FORMAT(' DO YOU WISH TO MODIFY AN EXISTING FILE?  (ENTER YES OR NO
     *) '/)
      READ(INA,900) ANSW
      IF(ANSW.NE.YES) GO TO 1015
      IBC=2
      IREPT=1
      ISAVR=1
C
      WRITE(IOUTA,60)
   60 FORMAT(' WHAT IS NAME OF THE DATA SET YOU WISH TO UPDATE? '/)
      READ(INA,905) FILIN
 1006 CONTINUE
C
 1013 WRITE(IOUTA,30,ERR=1012)
      GO TO 1011
 1012 IOUTA=1
      INA=1
      GO TO 1013
 1011 READ(INA,900) ANSW
      IF(ANSW.EQ.YES) IOUT=IOUTA
      IF(ANSW.NE.YES) GO TO 200
      WRITE(IOUTA,32)
      READ(INA,900) ANSW
      IF(ANSW.NE.YES) GO TO 1014
      IOUT=IBOTH
      IBTH=1
  200 WRITE(IOUTA,40)
      READ(INA,905)FILOUT
C
      OPEN(FILE=FILOUT,UNIT=IOUT)
 1014 OPEN(FILE=FILIN,UNIT=IN,ERR=45)
C
      GO TO 1025
C
 1015 WRITE(IOUTA,95)
   95 FORMAT(' ***** A NEW FILE IS BEING CREATED INTERACTIVELY. ******')
      WRITE(IOUTA,100)
  100 FORMAT(' WHAT IS NAME OF THE DATA SET YOU WISH TO CREATE? '/)
      READ(INA,905) FILIN
C
 1016 ISAVR=1
      WRITE(IOUTA,30)
      READ(INA,900,ERR=1017) ANSW
      GO TO 1018
 1017 IOUTA=1
      INA=1
      GO TO 1016
 1018 IF(ANSW.EQ.YES)IOUT=IOUTA
      IF(ANSW.NE.YES) GO TO 1020
      WRITE(IOUTA,32)
      READ(INA,900) ANSW
      IF(ANSW.NE.YES) GO TO 1025
      IOUT=IBOTH
      IBTH=1
 1020 WRITE(IOUTA,40)
      READ(INA,905)FILOUT
C
      OPEN(FILE=FILOUT,UNIT=IOUT)
C
  900 FORMAT(A1)
  905 FORMAT(A14)
 1025 RETURN
      END
