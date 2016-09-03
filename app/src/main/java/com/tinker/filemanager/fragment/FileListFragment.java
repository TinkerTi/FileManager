package com.tinker.filemanager.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinker.filemanager.R;
import com.tinker.filemanager.activity.FileListActivity;
import com.tinker.filemanager.model.FileInfo;
import com.tinker.filemanager.utils.FileTypeUtils;
import com.tinker.filemanager.widget.FileListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Created by tiankui on 16/7/30.
 */
public class FileListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String MOBILE_DIR = "directory";
    private static final int RESULT_SELECTED_FILES = 1000;
    private static final int ALL_FILE_FILES = 1;
    private static final int ALL_VIDEO_FILES = 2;
    private static final int ALL_AUDIO_FILES = 3;
    private static final int ALL_other_FILES = 4;
    private static final int ALL_RAM_FILES = 5;
    private static final int ALL_SD_FILES = 6;
    private static final int ROOT_DIR = 100;
    private static final int SD_CARD_ROOT_DIR = 101;
    private static final int FILE_TRAVERSE_TYPE_ONE = 200;
    private static final int FILE_TRAVERSE_TYPE_TWO = 201;

    private LinearLayout mFileListTitleLinearLayout;
    private TextView mFilesCategoryTitleTextView;
    private TextView mFileSelectStateTextView;
    private ListView mFilesListView;
    private LinearLayout mFileLoadingLinearLayout;
    private TextView mNoFileMessageTextView;

    private FileListAdapter mFileListAdapter;
    private AsyncTask mLoadFilesTask ;
    private List<FileInfo> mFilesList ;
    private HashSet<FileInfo> mSelectedFiles = new HashSet<>();;

    private File currentDir;
    private File startDir;
    private String mFileInfoMessage ;
    private int fileTraverseType;
    private int fileFilterType;

    public FileListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        int rootDirType = intent.getIntExtra("rootDirType", -1);
        fileFilterType = intent.getIntExtra("fileFilterType", -1);
        fileTraverseType = intent.getIntExtra("fileTraverseType", -1);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(MOBILE_DIR)) {
            currentDir = new File(arguments.getString(MOBILE_DIR));
        } else {
            if (rootDirType == ROOT_DIR) {
                String path = Environment.getExternalStorageDirectory().getPath();
                currentDir = new File(path);
            } else if (rootDirType == SD_CARD_ROOT_DIR) {
                String path = FileTypeUtils.getInstance().getSDCardPath();
                currentDir = new File(path);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_file_list, container, false);
        mFileListTitleLinearLayout = (LinearLayout) view.findViewById(R.id.rc_ad_ll_file_list_title);
        mFilesCategoryTitleTextView = (TextView) view.findViewById(R.id.rc_ad_tv_file_list_title);
        mFileSelectStateTextView = (TextView) view.findViewById(R.id.rc_ad_tv_file_list_select_state);

        mFilesListView = (ListView) view.findViewById(R.id.rc_fm_lv_storage_folder_list_files);
        mFileLoadingLinearLayout = (LinearLayout) view.findViewById(R.id.rc_fm_ll_storage_folder_list_load);
        mNoFileMessageTextView = (TextView) view.findViewById(R.id.rc_fm_tv_no_file_message);
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFileList();
        String text = "";
        switch (fileFilterType) {
            case ALL_FILE_FILES:
                text = getString(R.string.rc_fr_file_category_title_text);
                break;
            case ALL_VIDEO_FILES:
                text = getString(R.string.rc_fr_file_category_title_video);
                break;
            case ALL_AUDIO_FILES:
                text = getString(R.string.rc_fr_file_category_title_audio);
                break;
            case ALL_other_FILES:
                text = getString(R.string.rc_fr_file_category_title_other);
                break;
            case ALL_RAM_FILES:
                text = getString(R.string.rc_fr_file_category_title_ram);
                break;
            case ALL_SD_FILES:
                text = getString(R.string.rc_fr_file_category_title_sd);
                break;
        }
        mFilesCategoryTitleTextView.setText(text);
        mFilesListView.setOnItemClickListener(this);
        mFileListTitleLinearLayout.setOnClickListener(this);
        mFileSelectStateTextView.setOnClickListener(this);
        mFileSelectStateTextView.setClickable(false);
        mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_no_select_file_text_state));
    }

    @Override
    public void onDestroyView() {
        mFilesListView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mLoadFilesTask != null)
            mLoadFilesTask.cancel(true);
        super.onDestroy();
    }

    @TargetApi(11)
    private void loadFileList() {
        if (mLoadFilesTask != null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.mLoadFilesTask = new AsyncTask<File, Void, List<FileInfo>>() {
                @Override
                protected void onPreExecute() {
                    if (fileTraverseType == FILE_TRAVERSE_TYPE_ONE) {
                        showLoadingFileView();
                    }
                    super.onPreExecute();
                }

                @Override
                protected List<FileInfo> doInBackground(File... params) {
                    mFileInfoMessage = "";
                    try {
                        List<FileInfo> fileInfos = new ArrayList<>();
                        if (fileTraverseType == FILE_TRAVERSE_TYPE_TWO) {
                            File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
                            fileInfos = FileTypeUtils.getInstance().getFileInfosFromFileArray(files);
                        } else if (fileTraverseType == FILE_TRAVERSE_TYPE_ONE) {
                            startDir = new File(Environment.getExternalStorageDirectory().getPath());
                            switch (fileFilterType) {
                                case ALL_FILE_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getTextFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_text);
                                    break;
                                case ALL_VIDEO_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getVideoFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_video);
                                    break;
                                case ALL_AUDIO_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getAudioFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_audio);
                                    break;
                                case ALL_other_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getOtherFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_other);
                                    break;
                            }
                        }
                        if (fileInfos == null) {
                            return new ArrayList<>();
                        }
                        if (isCancelled()) {
                            return new ArrayList<>();
                        }
                        Collections.sort(fileInfos, new FileTypeUtils.FileNameComparator());
                        return fileInfos;
                    } catch (Exception e) {
                        return new ArrayList<>();
                    }
                }

                @Override
                protected void onCancelled() {
                    mLoadFilesTask = null;
                    super.onCancelled();
                }

                @Override
                protected void onPostExecute(List<FileInfo> fileInfos) {
                    mFileLoadingLinearLayout.setVisibility(View.GONE);
                    mFilesListView.setVisibility(View.VISIBLE);
                    mLoadFilesTask = null;
                    try {
                        mFilesList = fileInfos;
                        if (mFilesList.isEmpty()) {
                            showNoFileMessage(mFileInfoMessage);
                            return;
                        }
                        mFileListAdapter = new FileListAdapter(getActivity(), mFilesList, mSelectedFiles);
                        setListViewAdapter(mFileListAdapter);
                    } catch (Exception e) {
                        showNoFileMessage(e.getMessage());
                    }
                    super.onPostExecute(fileInfos);
                }
            } .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentDir);
        } else {
            this.mLoadFilesTask = new AsyncTask<File, Void, List<FileInfo>>() {
                @Override
                protected void onPreExecute() {
                    if (fileTraverseType == FILE_TRAVERSE_TYPE_ONE) {
                        showLoadingFileView();
                    }
                    super.onPreExecute();
                }

                @Override
                protected List<FileInfo> doInBackground(File... params) {
                    mFileInfoMessage = "";
                    try {
                        List<FileInfo> fileInfos = new ArrayList<>();
                        if (fileTraverseType == FILE_TRAVERSE_TYPE_TWO) {
                            File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
                            fileInfos = FileTypeUtils.getInstance().getFileInfosFromFileArray(files);
                        } else if (fileTraverseType == FILE_TRAVERSE_TYPE_ONE) {
                            switch (fileFilterType) {
                                case ALL_FILE_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getTextFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_text);
                                    break;
                                case ALL_VIDEO_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getVideoFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_video);
                                    break;
                                case ALL_AUDIO_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getAudioFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_audio);
                                    break;
                                case ALL_other_FILES:
                                    fileInfos = FileTypeUtils.getInstance().getOtherFilesInfo(startDir);
                                    mFileInfoMessage = getString(R.string.rc_fr_file_category_title_other);
                                    break;
                            }
                        }
                        if (fileInfos == null) {
                            return new ArrayList<>();
                        }
                        if (isCancelled()) {
                            return new ArrayList<>();
                        }
                        Collections.sort(fileInfos, new FileTypeUtils.FileNameComparator());
                        return fileInfos;
                    } catch (Exception e) {
                        return new ArrayList<>();
                    }
                }

                @Override
                protected void onCancelled() {
                    mLoadFilesTask = null;
                    super.onCancelled();
                }

                @Override
                protected void onPostExecute(List<FileInfo> fileInfos) {
                    mFileLoadingLinearLayout.setVisibility(View.GONE);
                    mFilesListView.setVisibility(View.VISIBLE);
                    mLoadFilesTask = null;
                    try {
                        mFilesList = fileInfos;
                        if (mFilesList.isEmpty()) {
                            showNoFileMessage(mFileInfoMessage);
                            return;
                        }
                        mFileListAdapter = new FileListAdapter(getActivity(), mFilesList, mSelectedFiles);
                        setListViewAdapter(mFileListAdapter);
                    } catch (Exception e) {
                        showNoFileMessage(e.getMessage());
                    }
                    super.onPostExecute(fileInfos);
                }
            } .execute(currentDir);
        }
    }

    private void setListViewAdapter(FileListAdapter fileListAdapter) {
        this.mFileListAdapter = fileListAdapter;
        if (mFilesListView != null) {
            mFilesListView.setAdapter(fileListAdapter);
        }
    }

    private void showLoadingFileView() {
        mFilesListView.setVisibility(View.GONE);
        mNoFileMessageTextView.setVisibility(View.GONE);
        mFileLoadingLinearLayout.setVisibility(View.VISIBLE);
    }

    private void showNoFileMessage(String message) {
        mFilesListView.setVisibility(View.GONE);
        mFileLoadingLinearLayout.setVisibility(View.GONE);
        mNoFileMessageTextView.setVisibility(View.VISIBLE);
        mNoFileMessageTextView.setText(getResources().getString(R.string.rc_fr_no_file_message, message));
    }

    private void navigateTo(File folder) {
        FileListActivity activity = (FileListActivity) getActivity();
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(MOBILE_DIR, folder.getAbsolutePath());
        fragment.setArguments(args);
        activity.showFragment(fragment);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object selectedObject = parent.getItemAtPosition(position);
        if (selectedObject instanceof FileInfo) {
            FileInfo selectedFile = (FileInfo) selectedObject;

            if (selectedFile.isDirectory()) {
                navigateTo(new File(selectedFile.getFilePath()));
            } else {
                if (mSelectedFiles.contains(selectedFile)) {
                    mSelectedFiles.remove(selectedFile);
                    mFileListAdapter.notifyDataSetChanged();
                } else if (!view.isSelected() && mSelectedFiles.size() < 20) {
                    mSelectedFiles.add(selectedFile);
                    mFileListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.rc_fr_file_list_most_selected_files), Toast.LENGTH_SHORT).show();
                }

                if (mSelectedFiles.size() > 0) {
                    mFileSelectStateTextView.setClickable(true);
                    mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_select_file_text_state));
                    mFileSelectStateTextView.setText(getResources().getString(R.string.rc_ad_send_file_select_file, mSelectedFiles.size()));
                } else {
                    mFileSelectStateTextView.setClickable(false);
                    mFileSelectStateTextView.setText(getResources().getString(R.string.rc_ad_send_file_no_select_file));
                    mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_no_select_file_text_state));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mFileSelectStateTextView) {
            Intent intent = new Intent();
            intent.putExtra("selectedFiles", mSelectedFiles);
            getActivity().setResult(RESULT_SELECTED_FILES, intent);
            getActivity().finish();
        }
        if (v == mFileListTitleLinearLayout) {
            getActivity().finish();
        }
    }


}
