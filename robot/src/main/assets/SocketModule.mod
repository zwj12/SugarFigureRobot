MODULE SocketModule
    LOCAL CONST string TAG:="SocketModule";
    VAR socketdev socketServer;
    VAR socketdev socketClient;
    VAR string strIPAddressServer:="127.0.0.1";
    VAR num numPort:=3003;
    VAR string strIPAddressClient:="";
    VAR rawbytes raw_data_out;
    VAR rawbytes raw_data_in;
    !stringHeader: Only indexs from 1 to 128 can be set, the indexs from 129 to 256 are for response command
    CONST string stringHeader{256}:=["CloseConnection","GetOperatingMode","GetRunMode","","","","","GetRobotStatus",
        "GetSignalDo","GetSignalGo","GetSignalAo","GetSignalDi","GetSignalGi","GetSignalAi","","",
        "SetSignalDo","SetSignalGo","SetSignalAo","PulseSignalDO","","","","",
        "GetNumData","SetNumData","","","","","GetDataTaskName","SetDataTaskName",
        "GetWeldData","SetWeldData","GetSeamData","SetSeamData","GetWeaveData","SetWeaveData","","",
        "OpenDataFile","OpenDataBinFile","WriteDatatoFile","CloseDataFile","","","","",
        "DecodeDataFile","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""];

    PERS string strDataTaskName:="T_ROB1";
    PERS num numRobotStatus:=43;
    VAR num numDataInLength:=0;
    VAR num numMessageFormatError:=0;

    VAR string strDataFileName:="sugarfigure.bin";
    VAR iodev iodevDataFile;

    PROC main()
        SetTPHandlerLogLevel\WARNING;
        SetFileHandlerLogLevel\DEBUG;
        IF RobOS()=FALSE THEN
            strIPAddressServer:="192.168.2.52";
            !strIPAddressServer:="127.0.0.1";
        ENDIF
        WHILE TRUE DO
            SocketCreate socketServer;
            SocketBind socketServer,strIPAddressServer,numPort;
            SocketListen socketServer;
            commandReceive;
            SocketClose socketServer;
            WaitTime 2;
        ENDWHILE
    ENDPROC

    PROC CommandReceive()
        VAR byte commandIn;
        SocketAccept socketServer,socketClient\ClientAddress:=strIPAddressClient\Time:=WAIT_MAX;
        Logging\WARNING,\LoggerName:="SocketModule","Client "+strIPAddressClient+" is connected.";
        WHILE true DO
            numMessageFormatError:=0;
            ClearRawBytes raw_data_in;
            SocketReceive socketClient\RawData:=raw_data_in\Time:=WAIT_MAX;
            IF RawBytesLen(raw_data_in)>=3 THEN
                UnpackRawBytes raw_data_in\Network,1,commandIn\Hex1;
                UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
                IF RawBytesLen(raw_data_in)=numDataInLength+3 THEN
                    IF commandIn=0 THEN
                        ResponseSocketCommand commandIn+128;
                        SocketClose socketClient;
                        Logging\WARNING,\LoggerName:="SocketModule","The connection "+strIPAddressClient+" is closed!";
                        RETURN ;
                    ELSE
                        IF StrLen(stringHeader{commandIn+1})>0 THEN
                            Logging\DEBUG,\LoggerName:="SocketModule","Execute command "+ValToStr(commandIn+1)+" of "+stringHeader{commandIn+1};
                            %stringHeader{commandIn+1}%commandIn;
                        ELSE
                            numMessageFormatError:=3;
                        ENDIF
                    ENDIF
                ELSE
                    numMessageFormatError:=2;
                ENDIF
            ELSE
                numMessageFormatError:=1;
            ENDIF

            TEST numMessageFormatError
            CASE 1:
                Logging\ERRORING,\LoggerName:="SocketModule","The command ("+ValToStr(commandIn)+") message should be sent from client as a whole!";
                ResponseError commandIn;
            CASE 2:
                Logging\ERRORING,\LoggerName:="SocketModule","The command's ("+ValToStr(commandIn)+") data length ("+ValToStr(numDataInLength)+") is not right!";
                ResponseError commandIn;
            CASE 3:
                Logging\ERRORING,\LoggerName:="SocketModule","Command ("+ValToStr(commandIn)+") is not set in stringHeader!";
                ResponseError commandIn;
            DEFAULT:
            ENDTEST

        ENDWHILE

    ERROR
        IF ERRNO=ERR_SOCK_TIMEOUT OR ERRNO=ERR_SOCK_CLOSED THEN
            SocketClose socketClient;
            Logging\ERRORING,\LoggerName:="SocketModule","The connection "+strIPAddressClient+" is closed abnormally!";
            RETURN ;
        ELSEIF ERRNO=ERR_REFUNKPRC THEN
            Logging\ERRORING,\LoggerName:="SocketModule","ERR_REFUNKPRC!";
            TRYNEXT;
        ELSEIF ERRNO=ERR_CALLPROC THEN
            Logging\ERRORING,\LoggerName:="SocketModule","ERR_CALLPROC!";
            TRYNEXT;
        ENDIF
    ENDPROC

    PROC PackSocketHeader(byte commandOut)
        VAR rawbytes raw_data_temp;
        CopyRawBytes raw_data_out,1,raw_data_temp,1;
        ClearRawBytes raw_data_out;
        PackRawBytes commandOut,raw_data_out\Network,1\Hex1;
        PackRawBytes RawBytesLen(raw_data_temp),raw_data_out\Network,2\IntX:=UINT;
        CopyRawBytes raw_data_temp,1,raw_data_out,4;
    ENDPROC

    PROC ResponseSocketCommand(byte commandOut)
        ClearRawBytes raw_data_out;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;
    ENDPROC

    PROC ResponseError(byte commandIn)
        VAR byte commandOut;
        commandOut:=255;
        ClearRawBytes raw_data_out;
        PackRawBytes commandIn,raw_data_out\Network,1\Hex1;
        PackRawBytes numRobotStatus,raw_data_out\Network,2\IntX:=DINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;
    ENDPROC

    PROC GetOperatingMode(byte commandIn)
        VAR byte commandOut;
        VAR num numTemp;
        TEST OpMode()
        CASE OP_AUTO:
            numTemp:=1;
        CASE OP_MAN_PROG:
            numTemp:=2;
        CASE OP_MAN_TEST:
            numTemp:=3;
        DEFAULT:
            numTemp:=0;
        ENDTEST
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes numTemp,raw_data_out\Network,1\IntX:=DINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;
    ENDPROC

    PROC GetRunMode(byte commandIn)
        VAR byte commandOut;
        VAR num numTemp;
        TEST RunMode(\Main)
        CASE RUN_CONT_CYCLE:
            numTemp:=1;
        CASE RUN_INSTR_FWD:
            numTemp:=2;
        CASE RUN_INSTR_BWD:
            numTemp:=3;
        CASE RUN_SIM:
            numTemp:=4;
        CASE RUN_STEP_MOVE:
            numTemp:=5;
        DEFAULT:
            numTemp:=0;
        ENDTEST
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes numTemp,raw_data_out\Network,1\IntX:=DINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;
    ENDPROC

    PROC GetRobotStatus(byte commandIn)
        VAR byte commandOut;
        ClearRawBytes raw_data_out;
        commandOut:=commandIn+128;
        PackRawBytes numRobotStatus,raw_data_out\Network,1\IntX:=DINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+ValToStr(numRobotStatus);
    ENDPROC

    PROC SetSignalDo(byte commandIn)
        VAR byte commandOut;
        VAR byte byteDoValue;
        VAR string strSignalName;
        VAR signaldo signaldoTemp;
        UnpackRawBytes raw_data_in\Network,4,byteDoValue\Hex1;
        UnpackRawBytes raw_data_in\Network,5,strSignalName\ASCII:=numDataInLength-1;
        AliasIO strSignalName,signaldoTemp;
        IF byteDoValue<>0 THEN
            byteDoValue:=1;
        ENDIF
        SetDO signaldoTemp,byteDoValue;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+strSignalName+" : "+ValToStr(byteDoValue);
    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC SetSignalAo(byte commandIn)
        VAR byte commandOut;
        VAR num numTemp;
        VAR string strSignalName;
        VAR signalao signalTemp;
        UnpackRawBytes raw_data_in\Network,4,numTemp\Float4;
        UnpackRawBytes raw_data_in\Network,8,strSignalName\ASCII:=numDataInLength-4;
        AliasIO strSignalName,signalTemp;
        SetAO signalTemp,numTemp;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+strSignalName+" : "+ValToStr(numTemp);
    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC SetSignalGo(byte commandIn)
        VAR byte commandOut;
        VAR num numTemp;
        VAR string strSignalName;
        VAR signalgo signalgoTemp;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,numTemp\IntX:=UDINT;
        UnpackRawBytes raw_data_in\Network,8,strSignalName\ASCII:=numDataInLength-4;
        AliasIO strSignalName,signalgoTemp;
        SetGO signalgoTemp,numTemp;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+strSignalName+" : "+ValToStr(numTemp);
    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC PulseSignalDO(byte commandIn)
        VAR byte commandOut;
        VAR num numPLength;
        VAR string strSignalName;
        VAR signaldo signaldoTemp;
        UnpackRawBytes raw_data_in\Network,4,numPLength\Float4;
        UnpackRawBytes raw_data_in\Network,8,strSignalName\ASCII:=numDataInLength-4;
        AliasIO strSignalName,signaldoTemp;
        PulseDO\PLength:=numPLength,signaldoTemp;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+strSignalName;
    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalDo(byte commandIn)
        VAR byte commandOut;
        VAR byte byteDoValue;
        VAR string strSignalName;
        VAR signaldo signaldoTemp;
        VAR byte byteSignalValue;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        Logging\DEBUG,\LoggerName:="SocketModule","GetSignalDo AliasIO"+" : "+strSignalName;
        AliasIO strSignalName,signaldoTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        byteSignalValue:=signaldoTemp;
        PackRawBytes byteSignalValue,raw_data_out\Network,1\Hex1;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalAo(byte commandIn)
        VAR byte commandOut;
        VAR byte byteAoValue;
        VAR string strSignalName;
        VAR signalao signalaoTemp;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        Logging\DEBUG,\LoggerName:="SocketModule","GetSignalAo AliasIO"+" : "+strSignalName;
        AliasIO strSignalName,signalaoTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes AOutput(signalaoTemp),raw_data_out\Network,1\Float4;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalGo(byte commandIn)
        VAR byte commandOut;
        VAR byte byteGoValue;
        VAR string strSignalName;
        VAR signalgo signalgoTemp;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        Logging\DEBUG,\LoggerName:="SocketModule","GetSignalGo AliasIO"+" : "+strSignalName;
        AliasIO strSignalName,signalGoTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes GOutput(signalgoTemp),raw_data_out\Network,1\IntX:=UDINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalDi(byte commandIn)
        VAR byte commandOut;
        VAR byte byteDiValue;
        VAR string strSignalName;
        VAR signaldi signaldiTemp;
        VAR byte byteSignalValue;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        AliasIO strSignalName,signaldiTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        byteSignalValue:=signaldiTemp;
        PackRawBytes byteSignalValue,raw_data_out\Network,1\Hex1;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalAi(byte commandIn)
        VAR byte commandOut;
        VAR byte byteAiValue;
        VAR string strSignalName;
        VAR signalai signalaiTemp;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        AliasIO strSignalName,signalaiTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes AInput(signalaiTemp),raw_data_out\Network,1\Float4;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetSignalGi(byte commandIn)
        VAR byte commandOut;
        VAR byte byteGiValue;
        VAR string strSignalName;
        VAR signalgi signalgiTemp;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSignalName\ASCII:=numDataInLength;
        AliasIO strSignalName,signalGiTemp;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes GInput(signalgiTemp),raw_data_out\Network,1\IntX:=UDINT;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_ALIASIO_DEF OR ERRNO=ERR_ALIASIO_TYPE OR ERRNO=ERR_NO_ALIASIO_DEF THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Signal ("+strSignalName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC GetNumData(byte commandIn)
        VAR byte commandOut;
        VAR string strSymbolName;
        VAR num symbolValue;
        UnpackRawBytes raw_data_in\Network,2,numDataInLength\IntX:=UINT;
        UnpackRawBytes raw_data_in\Network,4,strSymbolName\ASCII:=numDataInLength;
        GetDataVal strSymbolName\TaskName:=strDataTaskName,symbolValue;
        commandOut:=commandIn+128;
        ClearRawBytes raw_data_out;
        PackRawBytes symbolValue,raw_data_out\Network,1\Float4;
        PackSocketHeader commandOut;
        SocketSend socketClient\RawData:=raw_data_out;

    ERROR
        IF ERRNO=ERR_SYM_ACCESS OR ERRNO=ERR_INVDIM OR ERRNO=ERR_SYMBOL_TYPE OR ERRNO=ERR_TASKNAME THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Symbol ("+strSymbolName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC SetNumData(byte commandIn)
        VAR byte commandOut;
        VAR string strSymbolName;
        VAR num symbolValue;
        UnpackRawBytes raw_data_in\Network,4,symbolValue\Float4;
        UnpackRawBytes raw_data_in\Network,8,strSymbolName\ASCII:=numDataInLength-4;
        SetDataVal strSymbolName\TaskName:=strDataTaskName,symbolValue;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule",stringHeader{commandIn+1}+" : "+strSymbolName+" : "+ValToStr(symbolValue);

    ERROR
        IF ERRNO=ERR_SYM_ACCESS OR ERRNO=ERR_INVDIM OR ERRNO=ERR_SYMBOL_TYPE OR ERRNO=ERR_TASKNAME THEN
            Logging\ERRORING,\LoggerName:="SocketModule","Symbol ("+strSymbolName+") is not exist";
            ResponseError commandIn;
            RETURN ;
        ENDIF
    ENDPROC

    PROC OpenDataFile(byte commandIn)
        VAR byte commandOut;
        Close iodevDataFile;
        UnpackRawBytes raw_data_in\Network,4,strDataFileName\ASCII:=numDataInLength;
        Open "HOME:"\File:="Data/"+strDataFileName,iodevDataFile\Write;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule","Open file: "+ValToStr(strDataFileName);
    ENDPROC

    PROC OpenDataBinFile(byte commandIn)
        VAR byte commandOut;
        Close iodevDataFile;
        UnpackRawBytes raw_data_in\Network,4,strDataFileName\ASCII:=numDataInLength;
        Open "HOME:"\File:="Data/"+strDataFileName,iodevDataFile\Write\Bin;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule","Open file: "+ValToStr(strDataFileName);
    ENDPROC

    PROC WriteDatatoFile(byte commandIn)
        VAR byte commandOut;
        VAR rawbytes raw_data_temp;
        IF numDataInLength<1022 AND numDataInLength>0 THEN
            CopyRawBytes raw_data_in,4,raw_data_temp,1\NoOfBytes:=numDataInLength;
            WriteRawBytes iodevDataFile,raw_data_temp\NoOfBytes:=numDataInLength;
            commandOut:=commandIn+128;
            ResponseSocketCommand commandOut;
            Logging\DEBUG\LoggerName:=TAG,"numDataInLength = "+ValToStr(numDataInLength)+"("+ValToStr(RawBytesLen(raw_data_in))+")";
        ELSE
            Logging\DEBUG\LoggerName:=TAG,"numDataInLength = "+ValToStr(numDataInLength)+", it can not be great than 1021";
            ResponseError commandIn;
        ENDIF
    ENDPROC

    PROC CloseDataFile(byte commandIn)
        VAR byte commandOut;
        Close iodevDataFile;
        commandOut:=commandIn+128;
        ResponseSocketCommand commandOut;
        Logging\DEBUG,\LoggerName:="SocketModule","Close file: "+ValToStr(strDataFileName);
    ENDPROC

    FUNC string GetStringofSymbolValue(inout num numStartIndex,string strDelimiterCharacter\switch includeDelimiter)
        VAR num numCurIndex;
        VAR string strCharacter;
        VAR string strSymbolValue;
        numCurIndex:=numStartIndex;
        UnpackRawBytes raw_data_in\Network,numCurIndex,strCharacter\ASCII:=1;
        WHILE strCharacter<>strDelimiterCharacter DO
            Incr numCurIndex;
            UnpackRawBytes raw_data_in\Network,numCurIndex,strCharacter\ASCII:=1;
        ENDWHILE
        IF Present(includeDelimiter) THEN
            UnpackRawBytes raw_data_in\Network,numStartIndex,strSymbolValue\ASCII:=numCurIndex+1-numStartIndex;
        ELSE
            UnpackRawBytes raw_data_in\Network,numStartIndex,strSymbolValue\ASCII:=numCurIndex-numStartIndex;
        ENDIF
        numStartIndex:=numCurIndex+1;
        !        Logging\DEBUG,\LoggerName:="SocketModule","numCurIndex="+ValToStr(numCurIndex);
        !        Logging\DEBUG,\LoggerName:="SocketModule",strSymbolValue;
        RETURN strSymbolValue;
    ENDFUNC

    PROC DecodeDataFile(byte commandIn)
        VAR byte commandOut;
        UnpackRawBytes raw_data_in\Network,4,strDataFileName\ASCII:=numDataInLength;
        IF FileSize("Data/"+strDataFileName)=0 THEN
            Logging\ERRORING,\LoggerName:=TAG,"sugar figure file "+ValToStr(strDataFileName)+" is empty";
            ResponseError commandIn;
        ELSE
            DecodeSugarFigureDataFile;
            commandOut:=commandIn+128;
            ResponseSocketCommand commandOut;
        ENDIF
    ENDPROC

    PROC DecodeSugarFigureDataFile()
        VAR string strDecodeDataFileName;
        VAR iodev iodevDecodeDataFile;
        VAR rawbytes raw_data_temp;
        VAR num numDataFileSize;
        VAR num numRawBytesIndex;
        VAR num numReadRawBytesCount;
        VAR num numRawBytesLength;
        VAR num numProcessType;
        VAR num numX;
        VAR num numY;
        VAR num numZ;
        Close iodevDataFile;
        Close iodevDecodeDataFile;
        strDecodeDataFileName:=strDataFileName+".data";
        numDataFileSize:=FileSize("Data/"+strDataFileName);
        numReadRawBytesCount:=numDataFileSize DIV 1024;
        IF numDataFileSize MOD 1024>0 THEN
            Incr numReadRawBytesCount;
        ENDIF
        Open "HOME:"\File:="Data/"+strDataFileName,iodevDataFile\Read\Bin;
        Open "HOME:"\File:="Data/"+strDecodeDataFileName,iodevDecodeDataFile\Write;
        FOR i FROM 1 TO numReadRawBytesCount DO
            ReadRawBytes iodevDataFile,raw_data_temp;
            numRawBytesLength:=RawBytesLen(raw_data_temp);
            numRawBytesIndex:=1;
            WHILE numRawBytesIndex<=numRawBytesLength DO
                UnpackRawBytes raw_data_temp\Network,numRawBytesIndex,numProcessType\IntX:=DINT;
                UnpackRawBytes raw_data_temp\Network,numRawBytesIndex+4,numX\Float4;
                UnpackRawBytes raw_data_temp\Network,numRawBytesIndex+8,numY\Float4;
                UnpackRawBytes raw_data_temp\Network,numRawBytesIndex+12,numZ\Float4;
                Write iodevDecodeDataFile,ValToStr(numProcessType)+"\09"+ValToStr(Round(numX\Dec:=1))+"\09"+ValToStr(Round(numY\Dec:=1))+"\09"+ValToStr(Round(numZ\Dec:=1));
                numRawBytesIndex:=numRawBytesIndex+16;
            ENDWHILE
        ENDFOR
        Close iodevDataFile;
        Close iodevDecodeDataFile;
        Logging\DEBUG,\LoggerName:="SocketModule","Decode sugar figure file: "+ValToStr(strDataFileName);
    ENDPROC

ENDMODULE