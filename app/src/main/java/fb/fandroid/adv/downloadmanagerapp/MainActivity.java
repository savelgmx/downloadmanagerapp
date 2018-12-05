package fb.fandroid.adv.downloadmanagerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!isStoragePermissionGranted())
        {
            //разрешений нет работу прекращаем
            Toast.makeText(this,"Не удалось получить разрешение на запись во внешнее хранилище ",Toast.LENGTH_SHORT).show();
        }

    }

    public  boolean isStoragePermissionGranted() {//проверим разрешение на запись во внешнее хранилище  WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //На sdk<23 разрешение выдается автоматически при установке программы
            return true;
        }
    }

}
