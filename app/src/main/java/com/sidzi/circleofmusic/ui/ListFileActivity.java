package com.sidzi.circleofmusic.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.helpers.VerticalSpaceDecorationHelper;

import java.io.File;
import java.util.ArrayList;

public class ListFileActivity extends AppCompatActivity {

    private FilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        mAdapter = new FilesAdapter();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvListFiles);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mi_directory_up) {
            mAdapter.levelUp();
        }
        return super.onOptionsItemSelected(item);
    }

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
        private ArrayList<String> fileList;
        private String path;

        FilesAdapter() {
            path = Environment.getExternalStorageDirectory().getPath();
            setFileList();
        }

        private void levelDown(String next) {
            path = path + File.separator + next;
            setFileList();
        }

        private void levelUp() {
            path = path.substring(0, path.lastIndexOf("/"));
            setFileList();
        }

        private void setFileList() {
            fileList = new ArrayList<>();
            File dir = new File(path);
            if (!dir.canRead()) {
                setTitle("(inaccessible)");
            }
            if (dir.isDirectory()) {
                String[] list = dir.list();
                if (list != null) {
                    for (String file : list) {
                        if (!file.startsWith(".")) {
                            fileList.add(file);
                        }
                    }
                }
            } else {
                selectedLevel();
            }
            notifyDataSetChanged();
        }

        private void selectedLevel() {
            Intent dataRec = new Intent();
            dataRec.putExtra("filepath", path);
            setResult(RESULT_OK, dataRec);
            finish();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_files, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvDirPath.setText(fileList.get(position));
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView tvDirPath;

            ViewHolder(View view) {
                super(view);
                this.tvDirPath = (TextView) view.findViewById(R.id.tvFileNameOrPath);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                levelDown(tvDirPath.getText().toString());
            }
        }
    }
}