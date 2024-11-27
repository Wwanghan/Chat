package com.example.chat;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LAR_mainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lar_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPaper);

        // 直接在 MainActivity 中设置适配器
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // 返回对应的 Fragment：0 为登录页面，1 为注册页面
                return position == 0 ? new loginFragment() : new registerFragment();
            }

            @Override
            public int getItemCount() {
                return 2; // 两个页面：登录和注册
            }
        });

        // 将 TabLayout 和 ViewPager2 绑定
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "登录" : "注册");
        }).attach();
    }

    public void switchToLoginFragment() {
        if (viewPager != null) {
            viewPager.setCurrentItem(0, true); // 设置 ViewPager2 当前页面为登录页面
        }
    }


}
