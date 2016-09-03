package com.tinker.filemanager.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.tinker.filemanager.FileManagerContext;
import com.tinker.filemanager.R;
import com.tinker.filemanager.model.FileInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tiankui on 16/7/27.
 */
public class FileTypeUtils {

    private List<FileInfo> textFilesInfo;
    private List<FileInfo> videoFilesInfo;
    private List<FileInfo> audioFilesInfo;
    private List<FileInfo> otherFilesInfo;
    private static FileTypeUtils singleInstance;

    private FileTypeUtils() {
    }

    public static FileTypeUtils getInstance() {
        if (singleInstance == null) {
            synchronized (FileTypeUtils.class) {
                if (singleInstance == null) {
                    singleInstance = new FileTypeUtils();
                }
            }
        }
        return singleInstance;
    }

    public int fileTypeImageId(String fileName) {
        int id;
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix)))
            id = R.mipmap.rc_file_icon_picture;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix)))
            id = R.mipmap.rc_file_icon_file;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix)))
            id = R.mipmap.rc_file_icon_video;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix)))
            id = R.mipmap.rc_file_icon_audio;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix)))
            id = R.mipmap.rc_file_icon_word;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix)))
            id = R.mipmap.rc_file_icon_excel;
        else
            id = R.mipmap.rc_file_icon_else;
        return id;
    }

    private boolean checkSuffix(String fileName,
                                String[] fileSuffix) {
        for (String suffix : fileSuffix) {
            if (fileName != null) {
                if (fileName.toLowerCase().endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Intent getOpenFileIntent(String fileName, String fileSavePath) {
        Intent intent = null;
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "image/*");
        }
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "text/plain");
        }
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "video/*");
        }
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "audio/*");
        }
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "application/msword");
        }
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix))) {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(fileSavePath));
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        return intent;
    }

    /**
     * 文件过滤,将手机中隐藏的文件给过滤掉
     */

    public static final FileFilter ALL_FOLDER_AND_FILES_FILTER = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    };

    public List<FileInfo> getTextFilesInfo(File fileDir) {
        textFilesInfo = new ArrayList<>();
        textFilesInfo(fileDir);
        return textFilesInfo;
    }

    public void textFilesInfo(File fileDir) {
        File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    textFilesInfo(file);
                } else {
                    if (checkSuffix(file.getName(), FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
                        FileInfo fileInfo = getFileInfoFromFile(file);
                        textFilesInfo.add(fileInfo);
                    }
                }
            }
        }
    }

    public List<FileInfo> getVideoFilesInfo(File fileDir) {
        videoFilesInfo = new ArrayList<>();
        videoFilesInfo(fileDir);
        return videoFilesInfo;
    }

    public void videoFilesInfo(File fileDir) {
        File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    videoFilesInfo(file);
                } else {
                    if (checkSuffix(file.getName(), FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
                        FileInfo fileInfo = getFileInfoFromFile(file);
                        videoFilesInfo.add(fileInfo);
                    }
                }
            }
        }
    }

    public List<FileInfo> getAudioFilesInfo(File fileDir) {
        audioFilesInfo = new ArrayList<>();
        audioFilesInfo(fileDir);
        return audioFilesInfo;
    }

    public void audioFilesInfo(File fileDir) {
        File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    audioFilesInfo(file);
                } else {
                    if (checkSuffix(file.getName(), FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
                        FileInfo fileInfo = getFileInfoFromFile(file);
                        audioFilesInfo.add(fileInfo);
                    }
                }
            }
        }
    }

    public List<FileInfo> getOtherFilesInfo(File fileDir) {
        otherFilesInfo = new ArrayList<>();
        otherFilesInfo(fileDir);
        return otherFilesInfo;
    }

    public void otherFilesInfo(File fileDir) {
        File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    otherFilesInfo(file);
                } else {
                    if (checkSuffix(file.getName(), FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_other_file_suffix))) {
                        FileInfo fileInfo = getFileInfoFromFile(file);
                        otherFilesInfo.add(fileInfo);
                    }
                }
            }
        }
    }

    public List<FileInfo> getFileInfosFromFileArray(File[] files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            FileInfo fileInfo = getFileInfoFromFile(file);
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    public FileInfo getFileInfoFromFile(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setFilePath(file.getPath());
        fileInfo.setFileSize(file.length());
        fileInfo.setDirectory(file.isDirectory());
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex > 0) {
            String fileSuffix = file.getName().substring(lastDotIndex + 1);
            fileInfo.setSuffix(fileSuffix);
        }
        return fileInfo;
    }

    /**
     * 根据文件名进行比较排序
     */
    public static class FileNameComparator implements Comparator<FileInfo> {
        protected final static int
        FIRST = -1,
        SECOND = 1;

        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (lhs.isDirectory() || rhs.isDirectory()) {
                if (lhs.isDirectory() == rhs.isDirectory())
                    return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
                else if (lhs.isDirectory()) return FIRST;
                else return SECOND;
            }
            return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
        }
    }

    /**
     * 获取文件夹中文件的个数
     *
     * @param fileInfo
     * @return
     */
    public static int getNumFilesInFolder(FileInfo fileInfo) {
        if (fileInfo.isDirectory() == false) return 0;
        File[] files = new File(fileInfo.getFilePath()).listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (files == null) return 0;
        return files.length;
    }

    /**
     * 文件管理器中文件列表图标显示:根据文件的类型来获取文件的图标
     */
    public int getFileIconResource(FileInfo file) {
        if (file.isDirectory()) {
            return R.mipmap.rc_ad_list_folder_icon;
        } else {
            return getFileTypeImageId(file.getFileName());
        }
    }

    private int getFileTypeImageId(String fileName) {
        int id;
        if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix)))
            id = R.mipmap.rc_ad_list_file_icon;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix)))
            id = R.mipmap.rc_ad_list_video_icon;
        else if (checkSuffix(fileName, FileManagerContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix)))
            id = R.mipmap.rc_ad_list_audio_icon;
        else
            id = R.mipmap.rc_ad_list_other_icon;
        return id;
    }

    public final int
    KILOBYTE = 1024,
    MEGABYTE = KILOBYTE * 1024,
    GIGABYTE = MEGABYTE * 1024,
    MAX_BYTE_SIZE = KILOBYTE / 2,
    MAX_KILOBYTE_SIZE = MEGABYTE / 2,
    MAX_MEGABYTE_SIZE = GIGABYTE / 2;

    /**
     * 将文件的大小转换成便于认识的字符串
     */
    public String formatFileSize(long size) {
        if (size < MAX_KILOBYTE_SIZE)
            return String.format("%.2f K", (float) size / KILOBYTE);
        else if (size < MAX_MEGABYTE_SIZE)
            return String.format("%.2f M", (float) size / MEGABYTE);
        else
            return String.format("%.2f G", (float) size / GIGABYTE);
    }

    public String getSDCardPath() {
        String SDCardPath = null;
        String SDCardDefaultPath = Environment.getExternalStorageDirectory()
                                   .getAbsolutePath();
        if (SDCardDefaultPath.endsWith("/")) {
            SDCardDefaultPath = SDCardDefaultPath.substring(0, SDCardDefaultPath.length() - 1);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            String line;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.toLowerCase().contains("sdcard") && line.contains(".android_secure")) {
                    String[] array = line.split(" ");
                    if (array != null && array.length > 1) {
                        String temp = array[1].replace("/.android_secure", "");
                        if (!SDCardDefaultPath.equals(temp)) {
                            SDCardPath = temp;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SDCardPath;
    }
}