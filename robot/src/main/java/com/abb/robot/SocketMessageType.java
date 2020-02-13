package com.abb.robot;

public enum SocketMessageType {
    CloseConnection(0, 0, 128, 0),
    GetOperatingMode(1, 0, 129, 4),
    GetRunMode(2, 0, 130, 4),
    GetRobotStatus(7, 0, 135, 4),

    GetSignalDo(8, -1, 136, 1),
    GetSignalGo(9, -1, 137, 4),
    GetSignalAo(10, -1, 138, 4),
    GetSignalDi(11, -1, 139, 1),
    GetSignalGi(12, -1, 140, 4),
    GetSignalAi(13, -1, 141, 4),

    SetSignalDo(16, -1, 144, 0),
    SetSignalGo(17, -1, 145, 0),
    SetSignalAo(18, -1, 146, 0),

    GetNumData(24, -1, 152, 4),
    SetNumData(25, -1, 153, 0),
    GetWeldData(32, -1, 160, -1),
    SetWeldData(33, -1, 161, 0),
    GetSeamData(34, -1, 162, -1),
    SetSeamData(35, -1, 163, 0),
    GetWeaveData(36, -1, 164, -1),
    SetWeaveData(37, -1, 165, 0),

    OpenDataFile(40, -1, 168, 0),
    OpenDataBinFile(41, -1, 169, 0),
    WriteDatatoFile(42, -1, 170, 0),
    CloseDataFile(43, -1, 171, 0),

    Error(-1, 0, 255, 0);

    private static final String TAG = "SocketMessageType";

    private final int requestCommand;
    private final int responseCommand;

    public int getRequestCommand() {
        return requestCommand;
    }

    public int getResponseCommand() {
        return responseCommand;
    }

    //    Not include the header's length, which means the whole socket data length
    //    is requestDataLength+3 or responseDataLength+3
    //    -1 means the data length is alterable
    private int requestDataLength;
    private int responseDataLength;

    public int getRequestDataLength() {
        return requestDataLength;
    }

//    public void setRequestDataLength(int requestDataLength) {
//        this.requestDataLength = requestDataLength;
//    }

    public int getResponseDataLength() {
        return responseDataLength;
    }

//    public void setResponseDataLength(int responseDataLength) {
//        this.responseDataLength = responseDataLength;
//    }

     SocketMessageType(int requestCommand, int requestDataLength, int responseCommand, int responseDataLength) {
        this.requestCommand = requestCommand;
        this.requestDataLength = requestDataLength;
        this.responseCommand = responseCommand;
        this.responseDataLength = responseDataLength;
    }


}
