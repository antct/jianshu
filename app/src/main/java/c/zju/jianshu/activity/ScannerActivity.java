package c.zju.jianshu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }
}