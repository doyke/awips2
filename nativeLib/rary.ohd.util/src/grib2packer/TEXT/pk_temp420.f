      SUBROUTINE PK_TEMP420(KFILDO,IPACK,ND5,IS4,NS4,L3264B,
     1                      LOCN,IPOS,IER,*)
C
C        MARCH   2000   LAWRENCE   GSC/TDL   ORIGINAL CODING
C        JANUARY 2001   GLAHN      COMMENTS; IER = 403 CHANGED TO 402
C
C        PURPOSE
C            PACKS TEMPLATE 4.20 INTO PRODUCT DEFINITION SECTION 
C            OF A GRIB2 MESSAGE.  TEMPLATE 4.20 IS FOR A RADAR PRODUCT.
C            IT IS THE RESPONSIBILITY OF THE CALLING ROUTINE TO PACK
C            THE FIRST 9 OCTETS IN SECTION 4.
C
C        DATA SET USE
C           KFILDO - UNIT NUMBER FOR OUTPUT (PRINT) FILE. (OUTPUT)
C
C        VARIABLES
C              KFILDO = UNIT NUMBER FOR OUTPUT (PRINT) FILE. (INPUT)
C            IPACK(J) = THE ARRAY THAT HOLDS THE ACTUAL PACKED MESSAGE
C                       (J=1,ND5). (INPUT/OUTPUT)
C                 ND5 = THE SIZE OF THE ARRAY IPACK( ). (INPUT)
C              IS4(J) = CONTAINS THE PRODUCT DEFINITION INFORMATION
C                       FOR TEMPLATE 4.20 (IN ELEMENTS 10 THROUGH 42)
C                       TO BE PACKED INTO IPACK( ) (J=1,NS4). (INPUT)
C                 NS4 = SIZE OF IS4( ). (INPUT)
C              L3264B = THE INTEGER WORD LENGTH IN BITS OF THE MACHINE
C                       BEING USED. VALUES OF 32 AND 64 ARE
C                       ACCOMMODATED. (INPUT)
C                LOCN = THE WORD POSITION TO PLACE THE NEXT VALUE.
C                       (INPUT/OUTPUT)
C                IPOS = THE BIT POSITION IN LOCN TO START PLACING
C                       THE NEXT VALUE. (INPUT/OUTPUT)
C                 IER = RETURN STATUS CODE. (OUTPUT)
C                         0 = GOOD RETURN.
C                       1-4 = ERROR CODES GENERATED BY PKBG. SEE THE 
C                             DOCUMENTATION IN THE PKBG ROUTINE.
C                       402 = IS4( ) HAS NOT BEEN DIMENSIONED LARGE
C                             ENOUGH TO CONTAIN THE ENTIRE TEMPLATE.
C                   * = ALTERNATE RETURN WHEN IER NE 0.
C
C             LOCAL VARIABLES
C             MINSIZE = THE SMALLEST ALLOWABLE DIMENSION FOR IS4( ).
C                   N = L3264B = THE INTEGER WORD LENGTH IN BITS OF
C                       THE MACHINE BEING USED. VALUES OF 32 AND
C                       64 ARE ACCOMMODATED.
C
C        NON SYSTEM SUBROUTINES CALLED
C           PKBG
C
      PARAMETER(MINSIZE=42)
C
      DIMENSION IPACK(ND5),IS4(NS4)
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/util/src/grib2packer/RCS/pk_temp420.f,v $
     . $',                                                             '
     .$Id: pk_temp420.f,v 1.1 2004/09/16 16:52:29 dsa Exp $
     . $' /
C    ===================================================================
C
C
      N=L3264B
      IER=0
C
C        CHECK THE DIMENSIONS OF IS4( ).
C
      IF(NS4.LT.MINSIZE)THEN
D        WRITE(KFILDO,10)NS4,MINSIZE
D10      FORMAT(/' IS4( ) IS CURRENTLY DIMENSIONED TO CONTAIN'/
D    1           ' NS4=',I4,' ELEMENTS. THIS ARRAY MUST BE'/
D    2           ' DIMENSIONED TO AT LEAST ',I4,' ELEMENTS'/
D    3           ' TO CONTAIN ALL OF THE DATA IN PRODUCT'/
D    4           ' DEFINITION TEMPLATE 4.20.'/)
         IER=402
      ELSE
C
C           PACK THE PARAMETER CATEGORY.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(10),8,N,IER,*900)
C
C           PACK THE PARAMETER NUMBER.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(11),8,N,IER,*900)
C
C           PACK THE TYPE OF GENERATING PROCESS.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(12),8,N,IER,*900)
C
C           PACK THE NUMBER OF RADAR SITES USED.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(13),8,N,IER,*900)
C
C           PACK THE INDICATOR OF UNIT OF TIME RANGE.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(14),8,N,IER,*900)
C
C           PACK THE LATITUDE & LONGITUDE OF THE SITE.
         ISIGN=0
         IF(IS4(15).LT.0) ISIGN=1
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,ISIGN,1,N,IER,*900)
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,ABS(IS4(15)),
     1             31,N,IER,*900)
         ISIGN=0
         IF(IS4(19).LT.0) ISIGN=1
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,ISIGN,1,N,IER,*900)
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,ABS(IS4(19)),
     1             31,N,IER,*900)
C
C           PACK THE SITE ELEVATION.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(23),16,N,IER,*900)
C 
C           PACK THE ALPHANUMERIC SITE IDENTIFIER.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(25),32,N,IER,*900)
C
C           PACK THE NUMERIC SITE IDENTIFIER. 
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(29),16,N,IER,*900)
C
C           PACK THE OPERATING MODE, REFLECTIVITY CALIBRATION
C           CONSTANT, QUALITY CONTROL INDICATOR, CLUTTER FILTER
C           INDICATOR, AND CONSTANT ANTENNA ELEVATION ANGLE.
C
         DO K=31,35
            CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(K),8,N,IER,*900)
         END DO
C
C           PACK THE ACCUMULATION INTERVAL.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(36),16,N,IER,*900)
C
C           PACK THE REFERENCE REFLECTIVITY FOR ECHO TOP.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(38),8,N,IER,*900)
C
C           PACK THE RANGE BIN SPACING.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(39),24,N,IER,*900)
C
C           PACK THE RADIAL ANGULAR SPACING.
         CALL PKBG(KFILDO,IPACK,ND5,LOCN,IPOS,IS4(42),16,N,IER,*900)
C
      ENDIF
C
C       ERROR RETURN SECTION
 900  IF(IER.NE.0)RETURN 1 
C
      RETURN
      END
