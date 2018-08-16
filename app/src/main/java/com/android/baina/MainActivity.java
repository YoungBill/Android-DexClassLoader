package com.android.baina;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    // dex压缩文件的路径（可以是apk,jar,zip格式）
    private static final String PATH_DEX = Environment.getExternalStorageDirectory().toString() + File.separator + "jar/proguard-AndroidJar-dex-demo-1.0.0-release.jar";
    private static final String DEX = "dex";
    private static final String PATH_SHOWSTRINGCLASS = "com.android.baina.showstringclass.ShowStringClass";

    private TextView mHelloTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelloTv = findViewById(R.id.helloTv);
        AndPermission.with(MainActivity.this).runtime().permission(Permission.Group.STORAGE).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                DexClassLoader(MainActivity.this);
            }
        }).start();
    }

    /**
     * 使用DexClassLoader方式加载类
     */
    public void DexClassLoader(Context context) {
        //指定dexoutputpath为APP自己的缓存目录
        File dexOutputDir = context.getDir(DEX, 0);
        // 定义DexClassLoader
        // 第一个参数：是dex压缩文件的路径
        // 第二个参数：是dex解压缩后存放的目录
        // 第三个参数：是C/C++依赖的本地库文件目录,可以为null
        // 第四个参数：是上一级的类加载器
        //DexClassLoader dexClassLoader = new DexClassLoader(dexPath,dexOutputDirs,null,getClassLoader());
        DexClassLoader dexClassLoader = new DexClassLoader(PATH_DEX, dexOutputDir.getAbsolutePath(), null, getClassLoader());
        Class libProvierClazz = null;
        // 使用DexClassLoader加载类
        try {
            libProvierClazz = dexClassLoader.loadClass(PATH_SHOWSTRINGCLASS);
            if (libProvierClazz != null) {
                Method method = libProvierClazz.getDeclaredMethod("sayHello", null);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                String showString = (String) method.invoke(libProvierClazz.newInstance(), null);
                if (showString != null) {
                    mHelloTv.setText(showString);
                }
            } else {
                Log.d(TAG, "showStringClass is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
