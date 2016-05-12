package br.ufba.exerciserecognition.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by igor.faria on 11/05/2016.
 */
public class MainGridViewPager extends FragmentGridPagerAdapter {

    private final Activity mContext;
    private final Fragment[][] mFragment;

    public MainGridViewPager(Activity ctx, FragmentManager fm, Fragment[][] fragments)
    {
        super(fm);
        mContext = ctx;
        this.mFragment = fragments;
    }


    @Override
    public Fragment getFragment(int row, int col)
    {
        return mFragment[row][col];
    }

    @Override
    public int getRowCount()
    {
        return mFragment.length;
    }

    @Override
    public int getColumnCount(int rowNum)
    {
        return mFragment[rowNum].length;
    }
}
