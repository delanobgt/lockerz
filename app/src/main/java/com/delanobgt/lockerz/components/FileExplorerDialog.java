package com.delanobgt.lockerz.components;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.adapters.FileExplorerAdapter;
import com.delanobgt.lockerz.modules.FileExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExplorerDialog extends Dialog {

    private TextView tvCurrentDir;
    private Button btnAdd;
    private Button btnCancel;
    private RecyclerView recyclerView;
    private FileExplorerAdapter fileExplorerAdapter;
    private FileExplorer fileExplorer;
    private OnSelectedFilesCallback onSelectedFilesCallback;
    private Map<String, FileExplorer.FileItem> addedFileItemDict;

    public FileExplorerDialog(Activity activity, Map<String, FileExplorer.FileItem> addedFileItemDict) {
        super(activity);
        this.fileExplorer = new FileExplorer();
        this.addedFileItemDict = addedFileItemDict;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_file_explorer);

        tvCurrentDir = findViewById(R.id.tv_current_dir);
        tvCurrentDir.setText(fileExplorer.getCurrentDir().getAbsolutePath());

        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectedFilesCallback != null) {
                    Map<Integer, Boolean> selectedIndices = fileExplorerAdapter.getSelectedIndices();
                    FileExplorer.FileItem[] fileItems = fileExplorer.getFileItemList();
                    List<FileExplorer.FileItem> selectedFileItems = new ArrayList<>();
                    for (int i = 0; i < fileItems.length; i++) {
                        if (selectedIndices.containsKey(i) && selectedIndices.get(i)) {
                            selectedFileItems.add(fileItems[i]);
                        }
                    }
                    onSelectedFilesCallback.callback(selectedFileItems);
                    dismiss();
                }
            }
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        fileExplorerAdapter = new FileExplorerAdapter(getContext(), fileExplorer, addedFileItemDict);
        fileExplorerAdapter.setOnDirChangedCallback(new FileExplorerAdapter.OnDirChangedCallback() {
            @Override
            public void callback(File currentDir) {
                tvCurrentDir.setText(currentDir.getAbsolutePath());
            }
        });
        recyclerView.setAdapter(fileExplorerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setOnSelectedFilesCallback(OnSelectedFilesCallback onSelectedFilesCallback) {
        this.onSelectedFilesCallback = onSelectedFilesCallback;
    }

    public interface OnSelectedFilesCallback {
        void callback(List<FileExplorer.FileItem> fileItems);
    }
}
