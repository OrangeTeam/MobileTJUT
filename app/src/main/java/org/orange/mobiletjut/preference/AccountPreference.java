package org.orange.mobiletjut.preference;

import org.orange.mobiletjut.R;
import org.orange.mobiletjut.util.Crypto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 账号设置Preference。<br />
 * 目前不能通过{@link android.preference.PreferenceScreen PreferenceScreen}
 * xml设置此{@link DialogPreference}的默认值。
 */
public class AccountPreference extends DialogPreference {
    public static final String USER_ID_SUFFIX = "_student_id";
    public static final String PASSWORD_SUFFIX = "_password";

    private final Animation mAnimationShake;
    private EditText mUserId;
    private EditText mPassword;

    public AccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnimationShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        setDialogLayoutResource(R.layout.account_setting_dialog);
    }
    public AccountPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAnimationShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        setDialogLayoutResource(R.layout.account_setting_dialog);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mUserId = (EditText) view.findViewById(R.id.user_id);
        mPassword = (EditText) view.findViewById(R.id.password);
        SharedPreferences pref = getSharedPreferences();
        String userid = pref.getString(getKey() + USER_ID_SUFFIX, null);
        String password = pref.getString(getKey() + PASSWORD_SUFFIX, null);
        if(userid != null) {
            mUserId.append(userid);
            if(password != null) {
                password = decrypt(getStoragePassword(userid), password);
                mPassword.append(password);
            }
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        requestInputMethod(getDialog());
        try {
            setupButtonPositivesOnClickListener((AlertDialog) getDialog());
        } catch (ClassCastException e) {
            Log.w("TAG", "Can't prevent dialog from closing when user didn't input user id", e);
        }
    }
    /**
     * Sets the required flags on the dialog window to enable input method window to show up.
     */
    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    /**
     * 如果用户没有输入用户名，禁止确认输入
     */
    private void setupButtonPositivesOnClickListener(final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUserId.getText().toString())) {
                    mUserId.requestFocus();
                    ((InputMethodManager) mUserId.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(mUserId, 0);
                    mUserId.startAnimation(mAnimationShake);
                } else {
                    AccountPreference.super.onClick(dialog, AlertDialog.BUTTON_POSITIVE);
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            final String userid = mUserId.getText().toString();
            final String password = mPassword.getText().toString();
            // 以userid作为newValue触发Preference.OnPreferenceChange
            if(!callChangeListener(userid)) return;
            SharedPreferences.Editor editor = getEditor();
            editor.putString(getKey() + USER_ID_SUFFIX, userid);
            editor.putString(getKey() + PASSWORD_SUFFIX,
                    encrypt(getStoragePassword(userid), password));
            editor.apply();
        }
    }

    /**
     * 把plaintext加密为密文
     */
    private static String encrypt(String password, String plaintext) {
        return Crypto.encrypt(plaintext, password);
    }

    /**
     * 把{@code ciphertext}解密为明文
     *
     * @param password   解密密钥（暂时为账户ID）
     * @param ciphertext 待解密的密文
     * @return 解密得到的明文
     */
    public static String decrypt(String password, String ciphertext) {
        return Crypto.decrypt(ciphertext, password);
    }

    /**
     * @param userName 可以是null或空字符串""
     */
    public static String getStoragePassword(String userName) {
        if (userName == null || userName.length() < 3) {
            userName = "HhAkM5BpDFtMByffteLgkkzq9HFUtVueynFRjk5zMJkt9" +
                    "CN82s8jGvjAww5AdsqL2mvAj3E3b8bX8pXbrRLsuSeq23jwgdLEzmMMsaWTJVd4HcXjcHCDged6";
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(userName.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return new String(digest, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return userName;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
