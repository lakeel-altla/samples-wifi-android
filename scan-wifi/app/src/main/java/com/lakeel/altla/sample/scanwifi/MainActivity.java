package com.lakeel.altla.sample.scanwifi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 1;

    private static final int MAX_SIGNAL_LEVEL = 4;

    private WifiManager wifiManager;

    private Button buttonScan;

    private AccessPointsAdapter accessPointsAdapter;

    private ListView listViewAccessPoints;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        buttonScan = (Button) findViewById(R.id.button_scan);
        buttonScan.setOnClickListener(this);

        accessPointsAdapter = new AccessPointsAdapter(this);

        listViewAccessPoints = (ListView) findViewById(R.id.list_view_access_points);
        listViewAccessPoints.setAdapter(accessPointsAdapter);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults = wifiManager.getScanResults();

                List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();

                for (ScanResult scanResult : scanResults) {
                    AccessPoint accessPoint = new AccessPoint();
                    accessPoint.scanResult = scanResult;

                    if (scanResult.SSID != null) {
                        for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                            // wifiConfiguration の BSSID は null だったが、
                            // デバイスか OS バージョンにより異なる可能性がある。
                            // API 仕様によると、wifiConfiguration の BSSID は " でエンクローズされている。
                            if (wifiConfiguration.SSID == null) {
                                continue;
                            }

                            String rawSsid = null;
                            if (2 < wifiConfiguration.SSID.length()) {
                                rawSsid = wifiConfiguration.SSID.substring(1, wifiConfiguration.SSID.length() - 1);
                            } else {
                                rawSsid = wifiConfiguration.SSID;
                            }

                            if (scanResult.SSID.equals(rawSsid)) {
                                accessPoint.wifiConfiguration = wifiConfiguration;
                                break;
                            }
                        }
                    }

                    accessPointsAdapter.add(accessPoint);
                }

                hideProgressDialog();

                accessPointsAdapter.notifyDataSetChanged();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CODE_REQUEST_PERMISSIONS == requestCode) {
            if (!WifiPermissions.isRequestedPermissionsGranted(grantResults)) {
                // rationale
                Toast.makeText(this, "Can not use any functions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (WifiPermissions.isPermissionsGranted(this)) {
            startScan();
        } else {
            WifiPermissions.requestPermissions(this, REQUEST_CODE_REQUEST_PERMISSIONS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideProgressDialog();
    }

    private void startScan() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Making Wi-Fi enabled.", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        clearAccessPoints();

        showProgressDialog();

        wifiManager.startScan();
    }

    private void clearAccessPoints() {
        accessPointsAdapter.clear();
        accessPointsAdapter.notifyDataSetChanged();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.scanning));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private final class AccessPoint {

        ScanResult scanResult;

        WifiConfiguration wifiConfiguration;
    }

    private final class AccessPointsAdapter extends ArrayAdapter<AccessPoint> {

        private LayoutInflater inflater;

        private AccessPointsAdapter(Context context) {
            super(context, 0);

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_access_point, parent, false);
            }

            AccessPoint accessPoint = getItem(position);

            setText(convertView, R.id.text_view_bssid, accessPoint.scanResult.BSSID);
            setText(convertView, R.id.text_view_ssid, accessPoint.scanResult.SSID);
            setText(convertView, R.id.text_view_capabilities, accessPoint.scanResult.capabilities);

            int signalLevel = wifiManager.calculateSignalLevel(accessPoint.scanResult.level, MAX_SIGNAL_LEVEL);
            signalLevel++;
            String level = String.valueOf(accessPoint.scanResult.level) + "(" + signalLevel + "/" + MAX_SIGNAL_LEVEL
                           + ")";
            setText(convertView, R.id.text_view_level, level);

            View layoutConfiguration = convertView.findViewById(R.id.layout_configuration);
            layoutConfiguration.setVisibility(View.GONE);
            if (accessPoint.wifiConfiguration != null) {
                layoutConfiguration.setVisibility(View.VISIBLE);

                setText(convertView, R.id.text_view_network_id, String.valueOf(accessPoint.wifiConfiguration
                                                                                       .networkId));
                setText(convertView, R.id.text_view_status, WifiConfiguration.Status.strings[accessPoint
                        .wifiConfiguration.status]);
            }

            return convertView;
        }

        private void setText(View view, int resourceId, CharSequence text) {
            TextView textView = (TextView) view.findViewById(resourceId);
            textView.setText(text);
        }
    }
}
