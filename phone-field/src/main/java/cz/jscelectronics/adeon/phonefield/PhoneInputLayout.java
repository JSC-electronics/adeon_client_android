/*
 * Copyright 2016 lamudi-gmbh
 * Copyright 2019 JSC electronics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.jscelectronics.adeon.phonefield;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Implementation of PhoneField that uses {@link TextInputLayout}
 */
@SuppressWarnings("unused")
public class PhoneInputLayout extends PhoneField {

    private TextInputLayout mTextInputLayout;

    public PhoneInputLayout(Context context) {
        this(context, null);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateLayoutAttributes() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.TOP);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void prepareView() {
        super.prepareView();
        mTextInputLayout = findViewWithTag(getResources().getString(R.string.phonefield_til_phone));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.phone_text_input_layout;
    }

    @Override
    public void setHint(int resId) {
        mTextInputLayout.setHint(getContext().getString(resId));
    }

    @Override
    public void setError(String error) {
        mTextInputLayout.setErrorEnabled(error != null && error.length() != 0);
        mTextInputLayout.setError(error);
    }

    public TextInputLayout getTextInputLayout() {
        return mTextInputLayout;
    }
}
