package com.tinker.filemanager.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinker.filemanager.FileManagerContext;
import com.tinker.filemanager.R;
import com.tinker.filemanager.model.FileInfo;
import com.tinker.filemanager.utils.FileTypeUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by tiankui on 16/7/31.
 */
public class FileListAdapter extends android.widget.BaseAdapter {

    private List<FileInfo> mFileList;
    private HashSet<FileInfo> mSelectedFiles;
    private Context mContext;

    public FileListAdapter(Context context, List<FileInfo> mFileList, HashSet<FileInfo> mSelectedFiles) {
        this.mFileList = mFileList;
        this.mContext = context;
        this.mSelectedFiles = mSelectedFiles;
    }

    @Override
    public int getCount() {
        if (mFileList != null) return mFileList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFileList == null)
            return null;

        if (position >= mFileList.size())
            return null;

        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(mContext).inflate(R.layout.rc_wi_file_list_adapter, null);
        viewHolder = new ViewHolder();
        viewHolder.fileCheckStateImageView = (ImageView) view.findViewById(R.id.rc_wi_ad_iv_file_check_state);
        viewHolder.fileIconImageView = (ImageView) view.findViewById(R.id.rc_wi_ad_iv_file_icon);
        viewHolder.fileNameTextView = (TextView) view.findViewById(R.id.rc_wi_ad_tv_file_name);
        viewHolder.fileDetailsTextView = (TextView) view.findViewById(R.id.rc_wi_ad_tv_file_details);

        FileInfo file = mFileList.get(position);
        viewHolder.fileNameTextView.setText(file.getFileName());
        if (file.isDirectory()) {
            int filesNumber = FileTypeUtils.getNumFilesInFolder(file);
            if (filesNumber == 0)
                viewHolder.fileDetailsTextView.setText(FileManagerContext.getInstance().getString(R.string.rc_ad_folder_no_files));
            else
                viewHolder.fileDetailsTextView.setText(FileManagerContext.getInstance().getString(R.string.rc_ad_folder_files_number, filesNumber));
            viewHolder.fileIconImageView.setImageResource(FileTypeUtils.getInstance().getFileIconResource(file));
        } else {
            if (mSelectedFiles.contains(file)) {
                viewHolder.fileCheckStateImageView.setImageResource(R.mipmap.rc_ad_list_file_checked);
            } else {
                viewHolder.fileCheckStateImageView.setImageResource(R.mipmap.rc_ad_list_file_unchecked);
            }
            viewHolder.fileDetailsTextView.setText(FileManagerContext.getInstance().getString(R.string.rc_ad_file_size, FileTypeUtils.getInstance().formatFileSize(file.getFileSize())));
            viewHolder.fileIconImageView.setImageResource(FileTypeUtils.getInstance().getFileIconResource(file));
        }
        return view;
    }

    private class ViewHolder {
        ImageView fileCheckStateImageView;
        ImageView fileIconImageView;
        TextView fileNameTextView;
        TextView fileDetailsTextView;
    }
}
