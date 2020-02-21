MODULE SugarFigureModule
    LOCAL CONST string TAG:="SugarFigureModule";
    TASK PERS robtarget pNext:=[[-15.3,117.2,0],[0.707107,0.707107,-4.90556E-11,1.60172E-10],[1,-1,-1,0],[-113.508,9E+9,9E+9,9E+9,9E+9,9E+9]];
    TASK PERS robtarget pNext2:=[[0,107,0],[0.707107,0.707107,-4.90556E-11,1.60172E-10],[1,-1,-1,0],[-113.508,9E+9,9E+9,9E+9,9E+9,9E+9]];
    CONST pos posMin:=[-250,-250,-1000];
    CONST pos posMax:=[150,250,1000];
!    CONST pos posMin:=[-1000,-1000,-1000];
!    CONST pos posMax:=[1000,1000,1000];
    TASK PERS string str3DModelName:="sugarfigure.bin.data";
    VAR iodev iodev3DModel;
    PERS speeddata speedSugarFigure:=[100,500,5000,1000];
    PERS zonedata zoneSugarFigure:=[FALSE,0.3,0.3,0.3,0.03,0.3,0.03];

    TASK PERS robtarget pPrevious:=[[-13.8098,110.372,-0.00170823],[0.707109,0.707105,4.3058E-7,1.01108E-6],[1,-1,-1,0],[-113.508,9E+9,9E+9,9E+9,9E+9,9E+9]];
    TASK PERS bool boolpPreviousValid:=FALSE;
    TASK PERS pos vectorSugarLine:=[0,209.44,0];
    TASK PERS num numSugarLineInterval:=0.1;
    VAR intnum intSugarLine;

    VAR clock clockJob;
    TASK PERS num numJobTimeConsume:=17.089;
    VAR clock clockJobTotal;

    PROC MakeSugarFigure()
        VAR num numProcessType;
        VAR num numX;
        VAR num numY;
        VAR num numZ;
        VAR num numProcessType2;
        VAR num numX2;
        VAR num numY2;
        VAR num numZ2;
        VAR string strData;
        VAR string strTemp;
        VAR bool ok;
        VAR bool boolEOF;

        Reset sdoDispenseOn;
        Reset sdoTraceOn;
        boolpPreviousValid:=FALSE;

        MoveAbsJ jointHome,speedAir,zoneAir,toolServo\WObj:=wobjCur;

        ClkReset clockJob;
        ClkStart clockJob;
        Logging "Starting to make sugar figure by file: "+str3DModelName;
        Close iodev3DModel;
        Open "HOME:"\File:="Data/"+str3DModelName,iodev3DModel\Read;

        boolEOF:=FALSE;
        WHILE NOT boolEOF DO
            strData:=ReadStr(iodev3DModel);
            IF strData=EOF THEN
                boolEOF:=TRUE;
            ELSE
                ok:=GetProcessData(strData,numProcessType,numX,numY,numZ);
                IF ok=FALSE THEN
                    boolEOF:=TRUE;
                    Logging\ERRORING\LoggerName:=TAG,"Error raised when executing GetProcessData";
                ELSE
                    pNext.trans.x:=numX;
                    pNext.trans.y:=numY;
                    pNext.trans.z:=numZ;
                ENDIF
            ENDIF

            IF boolEOF=FALSE THEN
                IF numProcessType=4 OR numProcessType=5 OR numProcessType=6 OR numProcessType=8 THEN
                    strData:=ReadStr(iodev3DModel);
                    IF strData=EOF THEN
                        boolEOF:=TRUE;
                    ELSE
                        ok:=GetProcessData(strData,numProcessType2,numX2,numY2,numZ2);
                        IF ok=FALSE THEN
                            boolEOF:=TRUE;
                            Logging\ERRORING\LoggerName:=TAG,"Error raised when executing GetProcessData";
                        ELSE
                            pNext2.trans.x:=numX2;
                            pNext2.trans.y:=numY2;
                            pNext2.trans.z:=numZ2;
                            ProcessMove numProcessType,pNext\CirPointEnd:=pNext2;
                        ENDIF
                    ENDIF
                ELSE
                    ProcessMove numProcessType,pNext;
                ENDIF
            ENDIF

        ENDWHILE
        Close iodev3DModel;

        ClkStop clockJob;
        numJobTimeConsume:=ClkRead(clockJob);
        Logging\ERRORING\LoggerName:=TAG,"Time consume of job: "+ValToStr(numJobTimeConsume);

        Reset sdoDispenseOn;

        MoveAbsJ jointHome,speedAir,fine,toolServo\WObj:=wobjCur;

    ERROR
        IF ERRNO=ERR_RANYBIN_EOF THEN
            Close iodev3DModel;
            Reset sdoDispenseOn;
            RETURN ;
        ELSEIF ERRNO=ERR_FILEOPEN THEN
            Logging\ERRORING\LoggerName:=TAG,"File '"+str3DModelName+"' can not be opened";
            Reset sdoDispenseOn;
            RETURN ;
        ENDIF

    UNDO
        Reset sdoDispenseOn;
        Reset sdoTraceOn;
        boolpPreviousValid:=FALSE;
    ENDPROC

    FUNC bool GetProcessData(string strData,INOUT num numProcessType,INOUT num numX,INOUT num numY,INOUT num numZ)
        VAR string strTemp;
        VAR num found;
        VAR num iPos:=1;
        VAR bool ok:=TRUE;

        iPos:=1;
        found:=StrFind(strData,iPos,"\09");
        strTemp:=StrPart(strData,iPos,found-iPos);
        ok:=StrToVal(strTemp,numProcessType) AND ok;

        iPos:=found+1;
        found:=StrFind(strData,iPos,"\09");
        strTemp:=StrPart(strData,iPos,found-iPos);
        ok:=StrToVal(strTemp,numX) AND ok;

        iPos:=found+1;
        found:=StrFind(strData,iPos,"\09");
        strTemp:=StrPart(strData,iPos,found-iPos);
        ok:=StrToVal(strTemp,numY) AND ok;

        iPos:=found+1;
        strTemp:=StrPart(strData,iPos,StrLen(strData)-iPos+1);
        ok:=StrToVal(strTemp,numZ) AND ok;

        IF numX<posMin.x OR numX>posMax.x THEN
            Logging\ERRORING\LoggerName:=TAG,"The x coordinate value "+ValToStr(numX)+" is out of range. ("+ValToStr(posMin.x)+", "+ValToStr(posMax.x)+")";
            ok:=FALSE;
        ENDIF
        IF numY<posMin.y OR numY>posMax.y THEN
            Logging\ERRORING\LoggerName:=TAG,"The y coordinate value "+ValToStr(numY)+" is out of range. ("+ValToStr(posMin.y)+", "+ValToStr(posMax.y)+")";
            ok:=FALSE;
        ENDIF
        IF numZ<posMin.z OR numZ>posMax.z THEN
            Logging\ERRORING\LoggerName:=TAG,"The z coordinate value "+ValToStr(numZ)+" is out of range. ("+ValToStr(posMin.z)+", "+ValToStr(posMax.z)+")";
            ok:=FALSE;
        ENDIF

        RETURN ok;

    ERROR
        Logging\ERRORING\LoggerName:=TAG,"ERRNO = "+ValToStr(ERRNO);
        RETURN FALSE;
    ENDFUNC

    !1 - ProcessLStart; 2 - ProcessL; 3 - ProcessLEnd; 4 - ProcessCStart; 5 - ProcessC; 6 - ProcessCEnd, 7 - MoveL; 8 - MoveC; 9 - MoveJ;
    PROC ProcessMove(num numProcessType,robtarget ToPoint\robtarget CirPointEnd)
        TEST numProcessType
        CASE 1:
            Reset sdoTraceOn;
            boolpPreviousValid:=FALSE;
            vectorSugarLine:=[0,0,0];
            MoveLDO ToPoint,speedAir,fine,toolServo\WObj:=wobjCur,sdoDispenseOn,1;
            !Set sdoDispenseOn;
            IDelete intSugarLine;
            CONNECT intSugarLine WITH TRAPGetEZ;
            ITimer numSugarLineInterval,intSugarLine;
        CASE 2:
            MoveL ToPoint,speedSugarFigure,zoneSugarFigure,toolServo\WObj:=wobjCur;
        CASE 3:
            MoveLDO ToPoint,speedSugarFigure,fine,toolServo\WObj:=wobjCur,sdoDispenseOn,0;
            !Reset sdoDispenseOn;
            Reset sdoTraceOn;
            boolpPreviousValid:=FALSE;
            IDelete intSugarLine;
        CASE 4:
            Reset sdoTraceOn;
            boolpPreviousValid:=FALSE;
            vectorSugarLine:=[0,0,0];
            MoveCDO ToPoint,CirPointEnd,speedAir,fine,toolServo\WObj:=wobjCur,sdoDispenseOn,1;
            !Set sdoDispenseOn;
            IDelete intSugarLine;
            CONNECT intSugarLine WITH TRAPGetEZ;
            ITimer numSugarLineInterval,intSugarLine;
        CASE 5:
            MoveC ToPoint,CirPointEnd,speedSugarFigure,zoneSugarFigure,toolServo\WObj:=wobjCur;
        CASE 6:
            MoveCDO ToPoint,CirPointEnd,speedSugarFigure,fine,toolServo\WObj:=wobjCur,sdoDispenseOn,0;
            !Reset sdoDispenseOn;
            Reset sdoTraceOn;
            boolpPreviousValid:=FALSE;
            IDelete intSugarLine;
        CASE 7:
            MoveL ToPoint,speedAir,zoneAir,toolServo\WObj:=wobjCur;
        CASE 8:
            MoveC ToPoint,CirPointEnd,speedAir,zoneAir,toolServo\WObj:=wobjCur;
        CASE 9:
            MoveJ ToPoint,speedAir,zoneAir,toolServo\WObj:=wobjCur;
        DEFAULT:
            Reset sdoDispenseOn;
            MoveL ToPoint,speedAir,zoneAir,toolServo\WObj:=wobjCur;
        ENDTEST
    ENDPROC

    TRAP TRAPGetEZ
        VAR robtarget robtCur;
        VAR pos posVector;
        VAR num numHypotenuseLength;
        VAR num numCosineAngle;
        robtCur:=CRobT();
        IF boolpPreviousValid=FALSE THEN
            boolpPreviousValid:=TRUE;
        ELSE
            posVector:=robtCur.trans-pPrevious.trans;
            posVector.z:=0;
            numHypotenuseLength:=Distance(posVector,[0,0,0]);
            IF numHypotenuseLength>0 THEN
                IF posVector.y>=0 THEN
                    numCosineAngle:=Round(ACos(posVector.x/numHypotenuseLength));
                ELSE
                    numCosineAngle:=Round(360-ACos(posVector.x/numHypotenuseLength));
                ENDIF
                vectorSugarLine.x:=0*PI/180*1000;
                vectorSugarLine.z:=0*PI/180*1000;
                vectorSugarLine.y:=(numCosineAngle-90)*PI/180*1000;
                Set sdoTraceOn;
                !Logging\DEBUG\LoggerName:=TAG,"pPrevious = "+ValToStr(pPrevious.trans);
                !Logging\DEBUG\LoggerName:=TAG,"robtCur = "+ValToStr(robtCur.trans);
                !Logging\DEBUG\LoggerName:=TAG,"vectorSugarLine = "+ValToStr(vectorSugarLine);
            ENDIF
        ENDIF
        pPrevious:=robtCur;
    ENDTRAP
ENDMODULE