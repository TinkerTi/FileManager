package com.tinker.filemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinker.filemanager.R;
import com.tinker.filemanager.model.FileInfo;
import com.tinker.filemanager.utils.FileTypeUtils;

import java.util.HashSet;


/**
 * Created by tiankui on 16/7/30.
 */
public class FileManagerActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_FOR_SELECTED_FILES = 730;
    private static final int RESULT_SELECTED_FILES_TO_SEND = 731;

    private final static int ALL_FILE_FILES = 1;
    private final static int ALL_VIDEO_FILES = 2;
    private final static int ALL_AUDIO_FILES = 3;
    private final static int ALL_OTHER_FILES = 4;
    private final static int ALL_RAM_FILES = 5;
    private final static int ALL_SD_FILES = 6;

    private final static int ROOT_DIR = 100;
    private final static int SD_CARD_ROOT_DIR = 101;

    private final static int FILE_TRAVERSE_TYPE_ONE = 200;
    private final static int FILE_TRAVERSE_TYPE_TWO = 201;

    private LinearLayout mGoBackLinearLayout;
    private TextView mFileTextView;
    private TextView mVideoTextView;
    private TextView mAudioTextView;
    private TextView mOtherTextView;
    private TextView mMobileMemoryTextView;
    private TextView mSDCardTextView;
    private LinearLayout mSDCardLinearLayout;

    private String mSDCardPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_manager);

        mGoBackLinearLayout = (LinearLayout) findViewById(R.id.rc_ac_ll_go_back);
        mFileTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_file);
        mVideoTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_video);
        mAudioTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_audio);
        mOtherTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_picture);
        mMobileMemoryTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_mobile_memory);
        mSDCardTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_SD_card);
        mSDCardLinearLayout = (LinearLayout)findViewById(R.id.rc_ac_ll_sd_card);

        mGoBackLinearLayout.setOnClickListener(this);
        mFileTextView.setOnClickListener(this);
        mVideoTextView.setOnClickListener(this);
        mAudioTextView.setOnClickListener(this);
        mOtherTextView.setOnClickListener(this);
        mMobileMemoryTextView.setOnClickListener(this);
        mSDCardTextView.setOnClickListener(this);

        mSDCardPath = FileTypeUtils.getInstance().getSDCardPath();
        if (mSDCardPath != null) {
            mSDCardLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mGoBackLinearLayout) {
            finish();
        } else {
            Intent intent = new Intent(this, FileListActivity.class);
            if (v == mFileTextView) {
                intent.putExtra("rootDirType", ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_FILE_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
            }
            if (v == mVideoTextView) {
                intent.putExtra("rootDirType", ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_VIDEO_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
            }
            if (v == mAudioTextView) {
                intent.putExtra("rootDirType", ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_AUDIO_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
            }
            if (v == mOtherTextView) {
                intent.putExtra("rootDirType", ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_OTHER_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
            }

            if (v == mMobileMemoryTextView) {
                intent.putExtra("rootDirType", ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_RAM_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);
            }
            if (v == mSDCardTextView) {
                intent.putExtra("rootDirType", SD_CARD_ROOT_DIR);
                intent.putExtra("fileFilterType", ALL_SD_FILES);
                intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);

            }
            startActivityForResult(intent, REQUEST_FOR_SELECTED_FILES);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FOR_SELECTED_FILES) {
            if (data != null) {
                HashSet<FileInfo> selectedFileInfos = (HashSet<FileInfo>) data.getSerializableExtra("selectedFiles");
                Intent intent = new Intent();
                intent.putExtra("sendSelectedFiles", selectedFileInfos);
                setResult(RESULT_SELECTED_FILES_TO_SEND, intent);
                finish();
            }
        }
    }
}
