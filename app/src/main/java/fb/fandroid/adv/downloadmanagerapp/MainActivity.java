package fb.fandroid.adv.downloadmanagerapp;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/*
https://www.google.ru/search?newwindow=1&ei=VeQIXLyNK8ilsgH4oJnYCg&q=android+imageview+set+image+from+url&oq=android+imageview+set+image&gs_l=psy-ab.1.0.0i71l8.0.0..494166...0.0..0.0.0.......0......gws-wiz.1E4TO5aPbQw

https://medium.com/@crossphd/android-image-loading-from-a-string-url-6c8290b82c5e
        https://android--code.blogspot.com/2015/08/android-imageview-set-image-from.html
        https://android--code.blogspot.com/2015/08/android-imageview-set-image-from-url.html
*/


public class MainActivity extends AppCompatActivity {

    public static final String URL_REGEX = "^((https?|ftp):\\/\\/|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([\\/?].*)?$";
    ArrayList<Long> list = new ArrayList<>();
    private DownloadManager downloadManager;
    private long refid;    //отправляется в broadcast receiver
    private Uri Download_Uri; //http://www.yaplakal.com/html/static/top-logo.png
    private Button mButtonOne;
    private Button mButtonTwo;
    BroadcastReceiver onComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("IN", "" + referenceId);


            list.remove(referenceId);


            if (list.isEmpty()) {


                Log.e("INSIDE", "" + referenceId);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("ImageDownLoading")
                                .setContentText("All Download completed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());

                mButtonTwo.setEnabled(true); //теперь можно и загрузить картинку

            }

        }
    };

    private EditText mEditText;
    private ImageView mImageView;
    //-----обработка нажатия на кнопки
    private View.OnClickListener mOnButtonOneClickListener;
    private View.OnClickListener mOnButtonTwoClickListener;

    private boolean validateURL(String urlString){
/*
        проверяем УРЛ в переданной строке на предмет правильности
        т.е.соответвие шаблону  android.util.Patterns.WEB_URL
        если неправльный УРЛ то возвращаем false

*/

        if ( Patterns.WEB_URL.matcher(urlString).find() ){

 /*           и еще проверяем 3 последних символа в строке
            Если ссылка, не оканчивается на .jpeg/.png/.bmp то возвращаем false
*/

            urlString.lastIndexOf("/");
            String strExt=urlString.substring(urlString.lastIndexOf(".")+1, urlString.length());
            strExt.toLowerCase();

            switch (strExt.toLowerCase()){
                case "jpeg":
                    return true;
                case "png":
                    return true;
                case "bmp":
                    return true;
                default:
                    Toast.makeText(this,"Wrong file extention",Toast.LENGTH_SHORT).show();
                    return false;
            }

        }
        else {
            Toast.makeText(this, "URL неправильный должно быть https",Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    {
        mOnButtonOneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
               сначала мы здесь проверяем введеный EditText на ссответвие REGEXP
               т.е. явлвяется ли  это УРЛом вида https://yaplakal.com
                Заодно проверим 3 последних символа в ссылке
               Если ссылка, не оканчивается на .jpeg/.png/.bmp то выходим
*/
                if ( !validateURL(String.valueOf(mEditText.getText()))  ) {

                    System.exit(1);
                }
                else {

                    Download_Uri = Uri.parse( String.valueOf(mEditText.getText()) );
                    //здесь мы производим загрузку 1 файла через класс DownloadManager
                    list.clear();
                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);
                    request.setTitle("YP Downloading " + "YapLogo" + ".png");
                    request.setDescription("Downloading " + "YapLogo" + ".png");
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Yaplakal/" + "/" + "YapLogo" + ".png");
                    refid = downloadManager.enqueue(request);
                    list.add(refid);
                    Log.e("OUT", "" + refid);
                }

            }

        };
    }

    {
        mOnButtonTwoClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view){



                File imgFile = new  File("/sdcard/"+Environment.DIRECTORY_DOWNLOADS+"/Yaplakal/"  + "/" + "YapLogo" + ".png");

                if(imgFile.exists()) {

                    Bitmap downloadImageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    mImageView.setImageBitmap(downloadImageBitmap);
                }
            }

        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //будем использвать бродкаст ресивер для сигнала об окончании загрузки
        //Download_Uri = Uri.parse("http://www.yaplakal.com/html/static/top-logo.png");


        mButtonOne =findViewById(R.id.buttonOne);
        mButtonTwo =findViewById(R.id.buttonTwo);
        mEditText =findViewById(R.id.editText);
        mImageView=findViewById(R.id.imageView);

        mButtonOne.setOnClickListener(mOnButtonOneClickListener);//иницыализируем обработчик нажатия на кнопки
        mButtonTwo.setOnClickListener(mOnButtonTwoClickListener);
        mButtonTwo.setEnabled(false);

        mEditText.setText("http://www.yaplakal.com/html/static/top-logo.png");

        if (!isStoragePermissionGranted()) {
            //разрешений нет работу прекращаем
            Toast.makeText(this, "Не удалось получить разрешение на запись во внешнее хранилище ", Toast.LENGTH_SHORT).show();
            System.exit(1);
        }

    }

    public boolean isStoragePermissionGranted() {//проверим разрешение на запись во внешнее хранилище  WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //На sdk<23 разрешение выдается автоматически при установке программы
            return true;
        }
    }

    protected void onDestroy() {


        super.onDestroy();

        unregisterReceiver(onComplete); //уничтожаем ненужный BroadcastReceiver

    }



}
