C MODULE MXX_RHRP
C ----------------------------------------------------------------------
C  READS HRAP SEGMENT FILE OF BASIN BOUNDARIES GENERATED BY ll_grid PROGRAM
C  THIS FILE CONSISTS OF N+2 RECORDS FOR EACH BASIN;
C    - FIRST RECORD IS BASIN ID
C    - SECOND RECORD INCLUDES BASIN#, # OF SEGMENTS (N), & BASIN AREA
C    - N-RECORDS OF SEGMENT LINES: HRAP ROW #, LEFTEST & RIGHTEST HRAP COLUMNS
C        FOR THIS ROW
C
C  SUBROUTINE PARAMETERS:
C    - FLNAME IS HRAP SEGMENT FILE NAME
C    - SEG_BND IS 2-DIMENTIONAL ARRAY OF HRAP ROWS AND COLUMNS 
C    - NSGM IS ARRAY OF NUMBER SEGMENTS OF EACH BASIN
C    - BSN_NAME IS ARRAY OF BASIN IDs
C    - MAREAS IS MAXIMUM NUMBER OF BASINS ( MAPX PREPROC. USES 100)
C    - NAREA IS NUMBER OF BASINS TO GENERATE MAPX TIME SERIES
C    - ULOG IS LOG FILE DEVICE NUMBER
C ----------------------------------------------------------------------
C 
      SUBROUTINE MXX_RHRP(flname,seg_bnd,nsgm,bsn_name,maxbsn,narea,
     +                   M,LNUM,IPR,ISTAT,istnum,istbsn)

      PARAMETER (MAXSGM=10000)
          
      integer         seg_bnd(3,MAXSGM),nsgm(*),tmnsgm,IPR
      integer         istnum
      character*8     istbsn(*),tmbasn
      character*8     bsn_name(*)*8,bsn,bsn_tmp(maxbsn)*8
      character*(*)   flname
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/calb/src/mapx_sub/RCS/mxx_rhrp.f,v $
     . $',                                                             '
     .$Id: mxx_rhrp.f,v 1.3 2005/06/09 19:27:55 dws Exp $
     . $' /
C    ===================================================================

C
      
      ISTAT = 0
      LNUM  = 0
      istnum = 0

      IUN = 3
      CALL UPOPEN(IUN,FLNAME,0,'F',IERR)
      IF (IERR .NE. 0) THEN
        ISTAT = 5
        GOTO 88
      ENDIF

      jj=0
      m=0
      do i=1,maxbsn
       LNUM = LNUM+1
       READ(IUN,90,IOSTAT=IERR) bsn
       IF (IERR .GT. 0) THEN
         ISTAT = 4
         GOTO 99
       ENDIF
       IF (IERR .LT. 0) GOTO 77

c  if MAPX will be calculated for selected basins only
       if(narea .ne. 0) then
        do j=1,narea
            if(bsn .eq. bsn_name(j)) then
             LNUM = LNUM+1
             READ(IUN,91,IOSTAT=ISTAT) nx,tmnsgm,area
             IF (ISTAT .NE. 0) THEN
               ISTAT = 3
               GOTO 97
             ENDIF

             do ii=1,tmnsgm
               jj=jj+1
               LNUM = LNUM+1
               READ(IUN,92,IOSTAT=ISTAT) k,(seg_bnd(l,jj),l=1,3)
               IF (ISTAT .NE. 0) THEN
                 ISTAT = 2
                 GOTO 97
               ENDIF
             enddo
             goto 10
            endif
        enddo

        LNUM = LNUM+1
        READ(IUN,91,IOSTAT=ISTAT) nx,nsg
        IF (ISTAT .NE. 0) THEN
          ISTAT = 3
          GOTO 97
        ENDIF

        do ii=1,nsg
          LNUM = LNUM+1
          READ(IUN,92,IOSTAT=ISTAT)
          IF (ISTAT .NE. 0) THEN
            ISTAT = 2
            GOTO 97
          ENDIF
        enddo         
        istnum = istnum+1
        istbsn(istnum) = bsn
        goto 12

c  all basins in the segment file will be selected
       else
        LNUM = LNUM+1
        READ(IUN,91,IOSTAT=ISTAT) nx,tmnsgm,area
        IF (ISTAT .NE. 0) THEN
          ISTAT = 2
          GOTO 97
        ENDIF

        do ii=1,tmnsgm
         jj=jj+1
         LNUM = LNUM+1
         READ(IUN,92,IOSTAT=ISTAT) ktemp,(seg_bnd(k,jj),k=1,3)
         IF (ISTAT .NE. 0) THEN
           ISTAT = 2
           GOTO 97
         ENDIF
        enddo  
       endif

   10  continue
       m=m+1
       bsn_tmp(m)=bsn
       nsgm(m)=tmnsgm
       goto 12

   97  continue
       ISTAT = -1
       istnum = istnum+1
       istbsn(istnum) = bsn

   12 continue
      enddo

90    format(3X,a8)
91    format(3x,i3, 3x,i4,3x,f10.4)      
92    format(3x,4I4)      
      
   77 continue

      IF (ISTAT .EQ. 0) THEN
        IF (NAREA.NE.0 .AND. M.EQ.0) THEN
          ISTAT =  1
        ELSEIF (NAREA.NE.0 .AND. M.NE.NAREA) THEN
          ISTAT = -1
        ENDIF
      ENDIF

      if (ISTAT .LE. 0) THEN
CCC     narea=m

        if ( narea .gt. 0 .and. m .ne. 0 ) then
          do 30 ii=1,narea
            tmbasn = bsn_name(ii)
            do 28 jj=1,m
              if ( bsn_tmp(jj) .eq. tmbasn ) goto 30
   28       continue
            istnum = istnum+1
            istbsn(istnum) = tmbasn
            ISTAT = -1
   30     continue
        endif
      
        do i=1,m
         bsn_name(i)=bsn_tmp(i)
        enddo

C          Cumulate number of segments for all areas

        if (m .GE. 2) then
         do i=2,m
          nsgm(i) = nsgm(i) + nsgm(i-1)
         enddo
        endif
      endif

   99 continue
      CALL UPCLOS(IUN,' ',IERR)

   88 continue

      return     
      end      
