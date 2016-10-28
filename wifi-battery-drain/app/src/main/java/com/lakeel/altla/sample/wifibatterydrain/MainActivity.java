package com.lakeel.altla.sample.wifibatterydrain;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 1;

    @BindView(R.id.view_top)
    View mViewTop;

    @BindView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;

    @BindView(R.id.edit_text_interval)
    EditText mEditTextInterval;

    @BindView(R.id.button_start)
    Button mButtonStart;

    @BindView(R.id.button_stop)
    Button mButtonStop;

    private WifiManager mWifiManager;

    private PermissionManager mPermissionManager;

    public static Intent createStartIntent(@NonNull Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mPermissionManager = new PermissionManager.Builder(this)
                .addPermission(Manifest.permission.ACCESS_WIFI_STATE)
                .addPermission(Manifest.permission.CHANGE_WIFI_STATE)
                .build();

        if (!mPermissionManager.isPermissionsGranted()) {
            mPermissionManager.requestPermissions(REQUEST_CODE_REQUEST_PERMISSIONS);
        }

        mEditTextInterval.setText(String.valueOf(MainService.DEFAULT_INTERVAL));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSIONS) {
            if (mPermissionManager.isRequestedPermissionsGranted(grantResults)) {
                mButtonStart.setEnabled(true);
            } else {
                // rationale
                Snackbar.make(mViewTop, R.string.permission_error, Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnTextChanged(value = R.id.edit_text_interval, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChanged(Editable editable) {
        if (editable.length() == 0) {
            mTextInputLayout.setError(getString(R.string.error_required));
            mButtonStart.setEnabled(false);
        } else {
            mTextInputLayout.setError(null);
            mButtonStart.setEnabled(true);
        }
    }

    @OnClick(R.id.button_start)
    void onClickButtonStart() {
        mWifiManager.setWifiEnabled(true);

        String intervalString = mEditTextInterval.getText().toString();
        int interval = Integer.parseInt(intervalString);

        Intent intent = MainService.createIntent(getApplicationContext(), interval);
        startService(intent);
    }

    @OnClick(R.id.button_stop)
    void onClickButtonStop() {
        Intent intent = MainService.createIntent(getApplicationContext());
        stopService(intent);
    }
}
