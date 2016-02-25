package com.centerm.yuhc.smtools;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.centerm.yuhc.smtools.menumanager.OptionMenu;
import com.centerm.yuhc.smtools.smmanager.Sm2Controller;
import com.centerm.yuhc.smtools.smmanager.Sm3Controller;
import com.centerm.yuhc.smtools.smmanager.Sm4Controller;
import com.centerm.yuhc.smtools.smmanager.SmKeyController;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayList<View> tabViews;
    private ArrayList<String> tabTitles;
    private OptionMenu optionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strip_tab_activity);

        initMenuItems();
        initActivity();
    }

    private void initMenuItems(){
        optionMenu = new OptionMenu(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "版本").setIcon(R.drawable.version);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.my_menu_information:
                optionMenu.ShowIntroduction();
                break;
            case R.id.my_menu_version:
                optionMenu.ShowAppVersionInfo();
                break;
        }
        return true;
    }

    /**
     * 初始化界面内容，标题，内容等
     */
    private void initActivity(){
        tabViews = new ArrayList<View>();
        //内容初始化
        tabViews.add(new SmKeyController(this).getSmKeyView());
        Sm2Controller sm2Controller = new Sm2Controller(this);
        tabViews.add(sm2Controller.getSm2SignView());
        tabViews.add(sm2Controller.getSm2EncryptView());
        tabViews.add(new Sm3Controller(this).getSm3View());
        tabViews.add(new Sm4Controller(this).getSm4View());

        //标题初始化
        tabTitles = new ArrayList<String>();
        tabTitles.add("SM密钥");
        tabTitles.add("SM2签名");
        tabTitles.add("SM2加密");
        tabTitles.add("SM3");
        tabTitles.add("SM4");

        //初始化界面
        initViewPager();
    }

    private void initViewPager(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tabViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(tabViews.get(position));
                return tabViews.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(tabViews.get(position));
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles.get(position);
            }
        });

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.tab_strip);
        pagerTabStrip.setTabIndicatorColor(Color.YELLOW);
    }

}
