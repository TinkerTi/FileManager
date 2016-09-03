package com.tinker.filemanager;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by tiankui on 16/9/3.
 */
public class FileManagerContext extends ContextWrapper{
    private static Context mBaseContext;

    public static void init(Context context){
        mBaseContext=context;
    }
    private FileManagerContext(Context base) {
        super(base);
    }


    private static FileManagerContext singleInstance;

    public static FileManagerContext getInstance(){
        if(singleInstance==null){
            synchronized (FileManagerContext.class){
                if(singleInstance==null){
                    singleInstance=new FileManagerContext(mBaseContext);
                }
            }
        }
        return singleInstance;
    }
}
