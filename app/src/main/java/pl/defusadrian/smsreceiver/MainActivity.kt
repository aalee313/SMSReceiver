package pl.defusadrian.smsreceiver

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.smsreceiver.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECEIVE_SMS
        ), 101)
    }
}
