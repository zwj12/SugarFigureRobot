package com.abb.robot;

/**
 * @author Michael
 * @date 11/15/2019
 * description：
 */

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketMessageData {
    private static final String TAG = "SocketMessageData";
    private SocketMessageType socketMessageType;
    private int requestDataLength;
    private int responseDataLength;
    private String signalName;
    private double signalValue;
    private String symbolName;
    private Object symbolValue;
    private boolean responseError;

    public SocketMessageType getSocketMessageType() {
        return socketMessageType;
    }

    public void setSocketMessageType(SocketMessageType socketMessageType) {
        this.socketMessageType = socketMessageType;
    }

    public int getRequestDataLength() {
        return requestDataLength;
    }

    public void setRequestDataLength(int requestDataLength) {
        this.requestDataLength = requestDataLength;
    }

    public int getResponseDataLength() {
        return responseDataLength;
    }

    public void setResponseDataLength(int responseDataLength) {
        this.responseDataLength = responseDataLength;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public double getSignalValue() {
        return signalValue;
    }

    public void setSignalValue(double signalValue) {
        this.signalValue = signalValue;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public Object getSymbolValue() {
        return symbolValue;
    }

    public void setSymbolValue(Object symbolValue) {
        this.symbolValue = symbolValue;
    }

    public boolean isResponseError() {
        return responseError;
    }

    public void setResponseError(boolean responseError) {
        this.responseError = responseError;
    }
    public Object responseValue;

    public SocketMessageData(SocketMessageType socketMessageType){
        this.socketMessageType=socketMessageType;
    }
    
    public byte[] getRequestRawBytes() throws IOException {
        ByteArrayOutputStream requestBAOS = new ByteArrayOutputStream(1024);
        this.packRequestRawBytes(requestBAOS);
//        Log.d(TAG,String.format("%d,%d,%d",this.requestCommand,this.requestDataLength,requestBAOS.toByteArray().length));
        return requestBAOS.toByteArray();
    }

    public void packRequestRawBytes(ByteArrayOutputStream requestBAOS) throws IOException {
        DataOutputStream requestDOS = new DataOutputStream(requestBAOS);
        this.packRequestRawBytes(requestDOS);
    }

    //Don't support Socket stream, only support ByteArrayInputStream or ByteArrayOutputStream
    public void packRequestRawBytes(DataOutputStream requestDOS) throws IOException {
         switch (socketMessageType) {
            case CloseConnection:
            case GetOperatingMode:
            case GetRunMode:
            case GetRobotStatus:
                this.packSocketHeader(requestDOS);
                break;
            case GetSignalDo:
            case GetSignalGo:
            case GetSignalAo:
            case GetSignalDi:
            case GetSignalGi:
            case GetSignalAi:
                this.requestDataLength = this.getSignalName().length();
                this.packSocketHeader(requestDOS);
                requestDOS.writeBytes(this.getSignalName());
                break;
            case SetSignalDo:
                this.requestDataLength = this.getSignalName().length() + 1;
                this.packSocketHeader(requestDOS);
                requestDOS.writeByte((int) Math.round(this.getSignalValue()));
                requestDOS.writeBytes(this.getSignalName());
                break;
            case SetSignalGo:
                this.requestDataLength = this.getSignalName().length() + 4;
                this.packSocketHeader(requestDOS);
                requestDOS.writeInt((int) Math.round(Math.abs(this.getSignalValue())));
                requestDOS.writeBytes(this.getSignalName());
                break;
            case SetSignalAo:
                this.requestDataLength = this.getSignalName().length() + 4;
                this.packSocketHeader(requestDOS);
                requestDOS.writeFloat((float) this.getSignalValue());
                requestDOS.writeBytes(this.getSignalName());
//                Log.d(TAG,String.format("%d,%d",this.requestCommand,this.requestDataLength));
                break;
            case GetNumData:
            case GetWeldData:
            case GetSeamData:
            case GetWeaveData:
                this.requestDataLength = this.getSymbolName().length();
                this.packSocketHeader(requestDOS);
                requestDOS.writeBytes(this.getSymbolName());
                Log.d(TAG,this.getSymbolName());
                break;
            case SetNumData:
                this.requestDataLength = this.getSymbolName().length() + 4;
                this.packSocketHeader(requestDOS);
                requestDOS.writeFloat(Float.parseFloat(this.getSymbolValue().toString()));
                requestDOS.writeBytes(this.getSymbolName());
                break;
            case SetWeldData:
            case SetSeamData:
            case SetWeaveData:
                this.requestDataLength = this.getSymbolName().length() + 1 + this.getSymbolValue().toString().length();
                this.packSocketHeader(requestDOS);
                requestDOS.writeBytes(this.getSymbolName());
                requestDOS.write(0);
                requestDOS.writeBytes(this.getSymbolValue().toString());
                break;
        }
    }

    private void packSocketHeader(DataOutputStream requestDOS) throws IOException {
        requestDOS.writeByte(this.socketMessageType.getRequestCommand());
        requestDOS.writeShort(this.requestDataLength);
    }

    //The length of rawBytes maybe greater than the valid byte data
    //which means that the responseDataLength+3 may be less than the length of the rawBytes
    public int unpackResponseRawBytes(byte[] rawBytes) throws IOException {
        ByteArrayInputStream requestBAIS = new ByteArrayInputStream(rawBytes);
        return this.unpackResponseRawBytes(requestBAIS);
    }

    public int unpackResponseRawBytes(ByteArrayInputStream requestBAIS) throws IOException {
        DataInputStream requestDIS = new DataInputStream(requestBAIS);
        return this.unpackResponseRawBytes(requestDIS);
    }

    //Don't support Socket stream, only support ByteArrayInputStream or ByteArrayOutputStream
    public int unpackResponseRawBytes(DataInputStream requestDIS) throws IOException {
        int responseCommand = requestDIS.readByte() & 0xFF;
        int responseDataLength = requestDIS.readShort();
        if (responseCommand == SocketMessageType.Error.getResponseCommand()) {
            requestDIS.skipBytes(responseDataLength);
            return -1;
        }
        this.setResponseError(true);
        byte[] valueBytes;
        int readDataLength;
        switch (socketMessageType) {
            case CloseConnection:
            case SetSignalDo:
            case SetSignalGo:
            case SetSignalAo:
            case SetNumData:
            case SetWeldData:
            case SetSeamData:
            case SetWeaveData:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                }
                break;
            case GetOperatingMode:
            case GetRunMode:
            case GetRobotStatus:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                } else {
                    this.responseValue = requestDIS.readInt();
                }
                break;
            case GetSignalDo:
            case GetSignalDi:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                } else {
                    this.setSignalValue(requestDIS.readByte());
                    this.responseValue = this.getSignalValue();
                }
                break;
            case GetSignalGo:
            case GetSignalGi:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                } else {
                    this.setSignalValue(requestDIS.readInt());
                    this.responseValue = this.getSignalValue();
                }
                break;
            case GetSignalAo:
            case GetSignalAi:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                } else {
                    this.setSignalValue(requestDIS.readFloat());
                    this.responseValue = this.getSignalValue();
                }
                break;
            case GetNumData:
                if (responseDataLength != this.getResponseDataLength()) {
                    requestDIS.skipBytes(responseDataLength);
                    this.setResponseError(false);
                    return -1;
                } else {
                    this.setSymbolValue( requestDIS.readFloat());
                    this.responseValue = this.getSymbolValue();
                }
                break;
            case GetWeldData:
                valueBytes = new byte[responseDataLength];
                readDataLength = requestDIS.read(valueBytes, 0, responseDataLength);
                if (readDataLength != responseDataLength) {
                    this.setResponseError(false);
                    return -1;
                } else {
                    String strValue = new String(valueBytes);
                    if (!(this.getSymbolValue() instanceof WeldData)) {
                        this.setSymbolValue( new WeldData());
                    }
                    ((WeldData) this.getSymbolValue()).parse(strValue);
                    this.responseValue = this.getSymbolValue();
                }
                break;
            case GetSeamData:
                valueBytes = new byte[responseDataLength];
                readDataLength = requestDIS.read(valueBytes, 0, responseDataLength);
                if (readDataLength != responseDataLength) {
                    this.setResponseError(false);
                    return -1;
                } else {
                    String strValue = new String(valueBytes);
                    if (!(this.getSymbolValue() instanceof SeamData)) {
                        this.setSymbolValue( new SeamData());
                    }
                    ((SeamData) this.getSymbolValue()).parse(strValue);
                    this.responseValue = this.getSymbolValue();
                }
                break;
            case GetWeaveData:
                valueBytes = new byte[responseDataLength];
                readDataLength = requestDIS.read(valueBytes, 0, responseDataLength);
                if (readDataLength != responseDataLength) {
                    this.setResponseError(false);
                    return -1;
                } else {
                    String strValue = new String(valueBytes);
                    if (!(this.getSymbolValue() instanceof WeaveData)) {
                        this.setSymbolValue( new WeaveData());
                    }
                    ((WeaveData) this.getSymbolValue()).parse(strValue);
                    this.responseValue = this.getSymbolValue();
                }
                break;
        }
        return 0;
    }

}
