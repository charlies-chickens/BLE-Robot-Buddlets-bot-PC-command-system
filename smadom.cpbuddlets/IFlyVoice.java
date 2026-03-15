package com.smadom.cpbuddlets;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import io.flutter.plugin.common.MethodChannel;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class IFlyVoice {
    private String language;
    private Locale locale;
    Context mContext;
    private InitListener mInitListener;
    MethodChannel methodChannel;
    private RecognizerListener recognizerListener;
    private SpeechRecognizer speechRecognizer;
    private final String TAG = "yqy";
    private boolean canStorage = false;
    private boolean canRecode = false;
    private boolean isOpenVoiceRecognize = false;
    public boolean isSpeechRecognizeInitSuc = false;
    public boolean isKeepVoiceRecognize = false;

    IFlyVoice(Context context, MethodChannel methodChannel) {
        this.mContext = context;
        this.methodChannel = methodChannel;
        SpeechUtility.createUtility(context, "appid=5a2b3a3f");
        initVoiceRecognize();
    }

    public void stopVoiceRecognize() {
        if (this.speechRecognizer.isListening()) {
            this.speechRecognizer.stopListening();
        }
    }

    public void startVoiceRecognize() {
        if (this.speechRecognizer.isListening()) {
            return;
        }
        this.speechRecognizer.startListening(this.recognizerListener);
    }

    private void initVoiceRecognize() {
        initRecognizeData();
        if (Build.VERSION.SDK_INT >= 24) {
            this.locale = LocaleList.getDefault().get(0);
        } else {
            this.locale = Locale.getDefault();
        }
        this.language = this.locale.getLanguage().toLowerCase() + "-" + this.locale.getCountry().toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append("系统默认language:");
        sb.append(this.language);
        Log.d("yqy", sb.toString());
        SpeechRecognizer speechRecognizerCreateRecognizer = SpeechRecognizer.createRecognizer(this.mContext, this.mInitListener);
        this.speechRecognizer = speechRecognizerCreateRecognizer;
        if (speechRecognizerCreateRecognizer != null) {
            speechRecognizerCreateRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            this.speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
            this.speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
            if (this.language.contains("zh") || this.language.contains("cn")) {
                this.speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            } else {
                this.speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
            }
            Log.d("yqy", "语音识别对象完成初始化");
            return;
        }
        Log.d("yqy", "语音识别对象 == null");
    }

    private void initRecognizeData() {
        this.mInitListener = new InitListener() { // from class: com.smadom.cpbuddlets.-$$Lambda$IFlyVoice$R5fSogyxX8sgQRKNO-FNxqvmkRU
            @Override // com.iflytek.cloud.InitListener
            public final void onInit(int i) {
                this.f$0.lambda$initRecognizeData$0$IFlyVoice(i);
            }
        };
        this.recognizerListener = new RecognizerListener() { // from class: com.smadom.cpbuddlets.IFlyVoice.1
            @Override // com.iflytek.cloud.RecognizerListener
            public void onBeginOfSpeech() {
            }

            @Override // com.iflytek.cloud.RecognizerListener
            public void onEvent(int i, int i2, int i3, Bundle bundle) {
            }

            @Override // com.iflytek.cloud.RecognizerListener
            public void onVolumeChanged(int i, byte[] bArr) {
            }

            @Override // com.iflytek.cloud.RecognizerListener
            public void onEndOfSpeech() {
                if (IFlyVoice.this.isKeepVoiceRecognize) {
                    IFlyVoice.this.speechRecognizer.startListening(IFlyVoice.this.recognizerListener);
                }
            }

            @Override // com.iflytek.cloud.RecognizerListener
            public void onResult(RecognizerResult recognizerResult, boolean z) {
                if (recognizerResult == null) {
                    Log.d("yqy", "语音识别结果 onResult:" + z + "  recognizerResult == null");
                    return;
                }
                Log.d("yqy", "语音识别结果 onResult:" + z + "  content:" + recognizerResult.getResultString());
                String resultString = recognizerResult.getResultString();
                if (resultString == null || resultString.isEmpty()) {
                    return;
                }
                IFlyVoice.this.methodChannel.invokeMethod("voiceCommand", resultString);
            }

            @Override // com.iflytek.cloud.RecognizerListener
            public void onError(SpeechError speechError) {
                Log.e("yqy", "识别onError()-->" + speechError.getErrorDescription());
            }
        };
    }

    public /* synthetic */ void lambda$initRecognizeData$0$IFlyVoice(int i) {
        Log.d("yqy", "SpeechRecognizer initListener() code = " + i);
        if (i != 0) {
            Log.e("yqy", "语音识别初始化失败，错误码：" + i);
            this.isSpeechRecognizeInitSuc = false;
            return;
        }
        Log.w("yqy", "语音识别初始化成功!!!");
        this.isSpeechRecognizeInitSuc = true;
    }
}