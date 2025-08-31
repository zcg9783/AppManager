package com.zcg.appmanager.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zcg.appmanager.databinding.ActivityMainBinding;
import com.zcg.appmanager.databinding.AppItemBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupRecyclerView(binding);
    }

    private void setupRecyclerView(ActivityMainBinding binding) {
        binding.appList.setLayoutManager(new LinearLayoutManager(this));
        binding.appList.setAdapter(new AppAdapter(getAppList()));
    }

    private List<AppInfo> getAppList() {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        for (ResolveInfo resolveInfo : pm.queryIntentActivities(mainIntent, 0)) {
            String packageName = resolveInfo.activityInfo.packageName;
            String appName = resolveInfo.loadLabel(pm).toString();
            Drawable icon = resolveInfo.loadIcon(pm);
            apps.add(new AppInfo(appName, packageName, icon));
        }
        
        return apps;
    }

    private class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
        private final List<AppInfo> appList;

        public AppAdapter(List<AppInfo> appList) {
            this.appList = appList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AppItemBinding binding = AppItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppInfo app = appList.get(position);
            holder.binding.appName.setText(app.name);
            holder.binding.appIcon.setImageDrawable(app.icon);
            
            holder.itemView.setOnClickListener(v -> {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) startActivity(launchIntent);
            });
        }

        @Override
        public int getItemCount() {
            return appList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final AppItemBinding binding;
            ViewHolder(AppItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    static class AppInfo {
        final String name;
        final String packageName;
        final Drawable icon;

        AppInfo(String name, String packageName, Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }
}
