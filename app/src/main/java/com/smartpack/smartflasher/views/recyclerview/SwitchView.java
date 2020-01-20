/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Smart Flasher, which is a simple app aimed to make flashing
 * recovery zip files much easier. Significant amount of code for this app has been from
 * Kernel Adiutor by Willi Ye <williye97@gmail.com>.
 *
 * Smart Flasher is a free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Smart Flasher is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Smart Flasher. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.smartflasher.views.recyclerview;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.smartpack.smartflasher.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by sunilpaulmathew <sunil.kde@gmail.com> on January 18, 2020
 *
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class SwitchView extends RecyclerViewItem {

    public interface OnSwitchListener {
        void onChanged(SwitchView switchView, boolean isChecked);
    }

    private AppCompatImageView mImageView;
    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;
    private SwitchCompat mSwitcher;

    private Drawable mImage;
    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private boolean mChecked;

    private List<OnSwitchListener> mOnSwitchListeners = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_switch_view;
    }

    @Override
    public void onCreateView(View view) {
        mImageView = view.findViewById(R.id.image);
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mSwitcher = view.findViewById(R.id.switcher);

        super.onCreateView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitcher.setChecked(!mChecked);
            }
        });
        mSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChecked = isChecked;
                List<OnSwitchListener> applied = new ArrayList<>();
                for (OnSwitchListener onSwitchListener : mOnSwitchListeners) {
                    if (applied.indexOf(onSwitchListener) == -1) {
                        onSwitchListener.onChanged(SwitchView.this, isChecked);
                        applied.add(onSwitchListener);
                    }
                }
            }
        });
    }

    public void setDrawable(Drawable drawable) {
        mImage = drawable;
        refresh();
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void addOnSwitchListener(OnSwitchListener onSwitchListener) {
        mOnSwitchListeners.add(onSwitchListener);
    }

    public void clearOnSwitchListener() {
        mOnSwitchListeners.clear();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mImageView != null && mImage != null) {
            mImageView.setImageDrawable(mImage);
            mImageView.setVisibility(View.VISIBLE);
        }
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mSwitcher != null) {
            mSwitcher.setChecked(mChecked);
        }
    }
}
