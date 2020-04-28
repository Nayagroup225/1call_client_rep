//package com.block.phonecall.model;
package com.android.internal.telephony;

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}