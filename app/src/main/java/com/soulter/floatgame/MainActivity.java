package com.soulter.floatgame;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Soulter
 * @author's qq: 905617992
 *
 */
public class MainActivity extends AppCompatActivity {


    public static String SHARED_FILE_NAME="spfs";

    private static final String qqUrl = "https://qm.qq.com/cgi-bin/qm/qr?k=OZpb_cIHzdm2X3ni9KkuF9FJGyMb7cGL";
    private static final String qqGroupUrl = "https://dwz.cn/tcwbUEb3";
    private static final String servUrl = "http://app.soulter.xyz/index.html";
    private static final String FNAME = "FloatGame";

    public static final String PIC_GESHI = ".webp";

    //权限
    String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //悬浮权限返回
    private static final int REQUEST_CODE=0;
    private static final int IMAGE_RESULT_HEAD=1;
    //存储权限返回
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int IMAGE_RESULT_WIN=3;
    private int fabTAG = 0;
    private static final String CVTag = "cvtag";
    private static final String WINALPHA_1 = "winalpha1";
    private static final String WINALPHA_2 = "winalpha2";
    private FloatingActionButton fab;
    private CardView myCV1;
    private CardView myCV2;
    private CardView mySetin;
//    private CardView myBack;

    private MyTask mtask;
    private PicTask picTask;
    private PicWinTask picWinTask;

    private View mainView, setinView;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组

    private List<SetinItem> setinList = new ArrayList<SetinItem>();

    private AppBarLayout barLayout;

    private Intent servIntent;

    private SetinListView setinListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        servIntent = new Intent(MainActivity.this,WindowService.class);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        mainView = inflater.inflate(R.layout.content_scrolling, null);
        setinView = inflater.inflate(R.layout.settings_activity,null);


        barLayout = (AppBarLayout)findViewById(R.id.app_bar);


        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(mainView);
        viewList.add(setinView);

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };

        viewPager.setAdapter(pagerAdapter);




        initSetinListView();

        init();

        fab = (FloatingActionButton) findViewById(R.id.fab);


        if (fabTAG == 0)
            fab.setImageResource(R.drawable.openw);
        else
            fab.setImageResource(R.drawable.stopw);





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkWP()) {
                    if (fabTAG == 0) {
                        startService(servIntent);
                        fab.setImageResource(R.drawable.stopw);
                        fabTAG = 1;
                        Snackbar.make(view, "悬浮窗服务已打开~", Snackbar.LENGTH_LONG).show();
                    } else {
                        stopService(servIntent);
                        fab.setImageResource(R.drawable.openw);
                        fabTAG = 0;
                        Snackbar.make(view, "悬浮窗服务已关闭~", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        myCV1 = (CardView)mainView.findViewById(R.id.myCV1);
        myCV2 = (CardView)mainView.findViewById(R.id.myCV2);
        mySetin =(CardView)mainView.findViewById(R.id.myCVSetting);
//        myBack = (CardView)setinView.findViewById(R.id.cv_back);

        myCV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myCV1.setCardBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                myCV2.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ShaPreUtils.putToSpfs(MainActivity.this, CVTag, 1);
                if (fabTAG != 0) {
                    stopService(servIntent);
                    startService(servIntent);
                }

            }
        });
        myCV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(servIntent);
                myCV2.setCardBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                myCV1.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ShaPreUtils.putToSpfs(MainActivity.this, CVTag, 2);
                if (fabTAG != 0) {
                    stopService(servIntent);
                    startService(servIntent);
                }
            }
        });
        mySetin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.arrowScroll(View.FOCUS_RIGHT);
            }
        });
//        myBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewPager.arrowScroll(View.FOCUS_LEFT);
//            }
//        });




    }

    private void init(){

        isFirst();
//        checkUpdate();
        isRunning();

        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (fileIsExists(Environment.getExternalStorageDirectory()+File.separator+FNAME+File.separator+"head"+PIC_GESHI)){
                try {
                    Bitmap headPhoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+File.separator+FNAME+File.separator+"head"+PIC_GESHI);
                    barLayout.setBackground(new BitmapDrawable(headPhoto));
                }catch (Exception ignore){
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == -1 || grantResults[1] == -1)
            finish();

    }

    @Override
    protected void onResume() {
        isRunning();
        super.onResume();
    }

    private void isRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.soulter.floatgame.WindowService".equals(service.service.getClassName())) {
                fabTAG = 1;
                break;
            }
        }
    }

    private void isFirst(){
        if((int)ShaPreUtils.getFromSpfs(MainActivity.this,"firstuse",1) == 1){
            ShaPreUtils.putToSpfs(MainActivity.this, "firstuse", 0);


            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setMessage("需要必要的权限才能保证完整的服务QAQ")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                //检测是否有写的权限

                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            checkWP();

                        }
                    }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this,"哼",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).setCancelable(false).show();


            final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.first_use_dialog_layout, null);
            builder1.setTitle("关于")
                    .setView(dialogLayout)
                    .setPositiveButton("我知道啦", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).setCancelable(false).show();



        }

    }


    private void checkUpdate(){
        mtask = new MyTask();
        mtask.execute(servUrl);
    }


    private void initSetinListView(){
        initSetins();
        SetinAdapter adapter = new SetinAdapter(MainActivity.this,R.layout.setin_item,setinList);
        setinListView = (SetinListView) setinView.findViewById(R.id.setin_list_view);
        setinListView.setAdapter(adapter);
        setinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        Intent it2 = new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl));
                        startActivity(it2);
                        break;
                    case 2:
                        Intent it3 = new Intent(Intent.ACTION_VIEW, Uri.parse(qqGroupUrl));
                        startActivity(it3);
                        break;
                    case 3:
                        checkUpdate();
                        break;
                    case 4:
                        Log.v("tag","start->head");
                        Intent it4 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(it4, IMAGE_RESULT_HEAD);
                        break;

                    case 5:
                        Log.v("tag","start->win");
                        Intent it5 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(it5, IMAGE_RESULT_WIN);
                        break;
                    case 6:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.set_win_alpha_dialog, null);
                        final EditText editAlpha1 = (EditText)dialogLayout.findViewById(R.id.alpha_edit_1);
                        final EditText editAlpha2 = (EditText)dialogLayout.findViewById(R.id.alpha_edit_2);
                        editAlpha1.setText(String.valueOf(ShaPreUtils.getFromSpfs(MainActivity.this, WINALPHA_1, WindowService.DEFAULT_ALPHA_1)));
                        editAlpha2.setText(String.valueOf(ShaPreUtils.getFromSpfs(MainActivity.this, WINALPHA_2, WindowService.DEFAULT_ALPHA_2)));
                        builder.setTitle(getResources().getString(R.string.win_alpha_text))
                                .setView(dialogLayout)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        int alpha1;
                                        int alpha2;
                                        try {
                                            alpha1 = Integer.parseInt(editAlpha1.getText().toString());
                                            alpha2 = Integer.parseInt(editAlpha2.getText().toString());
                                            if (alpha1 > 255 || alpha1 < 0 ||alpha2 > 255 || alpha2 < 0){
                                                Toast.makeText(MainActivity.this,"输入错了~",Toast.LENGTH_SHORT).show();
                                                alpha1 = 150;
                                                alpha2 = 60;
                                            }
                                            ShaPreUtils.putToSpfs(MainActivity.this, WINALPHA_1, alpha1);
                                            ShaPreUtils.putToSpfs(MainActivity.this, WINALPHA_2, alpha2);
                                        }catch (Exception e){
                                            Toast.makeText(MainActivity.this,"输入错了~",Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }


                                        runningSoRestartService();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setCancelable(false).show();
                    default:
                        break;
                }
            }
        });
    }

    private void initSetins() {
        setinList.add(new SetinItem("作者","Soulter"));
        setinList.add(new SetinItem("QQ","905617992"));
        setinList.add(new SetinItem("QQ群","363040190"));
        //TODO: VER 2
        setinList.add(new SetinItem("检查更新","Ver1.0"));
        setinList.add(new SetinItem("自定义头图",""));
        setinList.add(new SetinItem("自定义悬浮窗",""));
        setinList.add(new SetinItem(getResources().getString(R.string.win_alpha_text),"设置悬浮窗的透明度"));

    }

    public boolean checkWP() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请允许我使用悬浮窗权限~")
                        .setTitle("提示")
                        .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, REQUEST_CODE);

                            }
                        })
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this,"哼。",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setCancelable(false).show();
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "请允许我使用悬浮窗权限!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == IMAGE_RESULT_HEAD  && resultCode == RESULT_OK && data != null || requestCode == IMAGE_RESULT_WIN  && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (requestCode == IMAGE_RESULT_HEAD) {
                Log.v("tag","set->head");
                picTask = new PicTask();
                picTask.execute(picturePath);
            }
            if (requestCode == IMAGE_RESULT_WIN){
                Log.v("tag","set->win");
                picWinTask = new PicWinTask();
                picWinTask.execute(picturePath);

            }

        }
    }

    private void runningSoRestartService(){
        if (fabTAG == 1){
            stopService(servIntent);
            startService(servIntent);
        }
    }


    public static Bitmap bmpCompresser(String imagePath,int inSampleSize){

        try{

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
//            BitmapFactory.decodeFile(imagePath, options);
//            int height = options.outHeight;
//            int width= options.outWidth;
//            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
//            int minLen = Math.min(height, width); // 原图的最小边长
//            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//                inSampleSize = (int)ratio;
//            }
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            Bitmap b = BitmapFactory.decodeFile(imagePath, options); // 解码文件

            return b;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static void bmpSaver(Bitmap b,String headName){
        Log.v("tag","Pic Save Start");
        File appFile = new File(Environment.getExternalStorageDirectory()+File.separator+FNAME);
        if (!appFile.exists()){
            Log.v("tag","Pic mkdir");
            appFile.mkdir();
        }
        String fileName = headName+PIC_GESHI;
        File path = new File(appFile,fileName);

        try{
            OutputStream os = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.WEBP,100,os);
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean fileIsExists(String strFile) {
        try {
            File f=new File(strFile);
            if(!f.exists())
                return false;
        }
        catch (Exception e) {
            return false;
        }
        return true;

    }


    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output;
    }

    @Override
    protected void onDestroy() {
        Log.v("TAG","Ac Des.");
        super.onDestroy();
    }


    private class MyTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String websiteResults = null;
            try{
                websiteResults = NetUtils.getHtml(urls[0]);
            }catch (UnknownHostException e){
                e.printStackTrace();

            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }


            Log.v("tag",websiteResults+":web");
            return websiteResults;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            int version = 0;
            String varInfo = "";
            if (s!= null) {

                try {
                    JSONObject result = new JSONObject(s);
                    //取数据
                    Log.v("TAG", "MSG::::" + result.get("var") + "\n" + result.get("info"));

                    varInfo = String.valueOf(result.get("info"));
                    version = Integer.parseInt(String.valueOf(result.get("var")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(MainActivity.this,"获取新版本失败QAQ",Toast.LENGTH_LONG).show();
                return;
            }


            Log.v("tag",s+"\n");

            try {

                //TODO:版本修改2
                if (s != null && version > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("新版本预警OvO")
                            .setMessage(""+varInfo)
                            .setPositiveButton("前往酷安下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this,"事件。",Toast.LENGTH_LONG).show();
                                }
                            }).setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).setCancelable(false).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"获取新版本失败QAQ",Toast.LENGTH_LONG).show();
            }
        }


    }


    private class PicTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap b = bmpCompresser(strings[0],2);
            bmpSaver(b, "head");
            return b;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            barLayout.setBackground(new BitmapDrawable(bitmap));
            Toast.makeText(MainActivity.this, "加载完成",Toast.LENGTH_SHORT).show();
            super.onPostExecute(bitmap);
            bitmap.recycle();
        }
    }

    private class PicWinTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {

            Bitmap b = bmpCompresser(strings[0],6);
            b = toRoundBitmap(b);
            bmpSaver(b, "win");
            b.recycle();//垃圾回收
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            runningSoRestartService();
            Toast.makeText(MainActivity.this, "加载完成",Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }


}
