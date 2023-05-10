/*
 * Copyright 2016 lamudi-gmbh
 * Copyright 2019 and modified by JSC electronics
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
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.ShortNumberInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * PhoneField is a custom view for phone numbers with the corresponding country flag. It uses
 * libphonenumber to validate the phone number.
 * <p>
 */
@SuppressWarnings("unused")
public abstract class PhoneField extends LinearLayoutCompat {

    private Spinner mSpinner;

    private ImageView mCountryFlag;

    private CountriesAdapter mAdapter = null;

    private TextView mTextView;

    private final PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    private String mDefaultCountryCode;

    private String mSelectedCountryCode = null;

    private String[] mRestrictToCountries = null;

    private Locale mCountryLocale = null;

    private final String mPhoneNumber;

    private final boolean mHideCountryFlag;

    private final List<Country> mCountries = new ArrayList<>(0);

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     */
    public PhoneField(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PhoneField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public PhoneField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PhoneField,
                defStyleAttr, 0);

        try {
            mDefaultCountryCode = a.getString(R.styleable.PhoneField_defaultCountry);
            String countryLocale = a.getString(R.styleable.PhoneField_countryLocale);
            if (countryLocale != null) {
                mCountryLocale = new Locale(countryLocale);
            }

            int countryArrayResId = a.getResourceId(R.styleable.PhoneField_countries, 0);
            if (countryArrayResId != 0) {
                mRestrictToCountries = a.getResources().getStringArray(countryArrayResId);
            }

            mPhoneNumber = a.getString(R.styleable.PhoneField_phoneNumber);

            mHideCountryFlag = a.getBoolean(R.styleable.PhoneField_hideCountryFlag, false);
        } finally {
            a.recycle();
        }

        inflate(getContext(), getLayoutResId(), this);
        updateLayoutAttributes();
        prepareView();
    }

    /**
     * Prepare view.
     */
    protected void prepareView() {
        mSpinner = findViewWithTag(getResources().getString(R.string.phonefield_flag_spinner));
        mCountryFlag = findViewWithTag(getResources().getString(R.string.phonefield_flag_country));
        mTextView = findViewWithTag(getResources().getString(R.string.phonefield_textview));

        if ((mSpinner == null && mCountryFlag == null) || mTextView == null) {
            throw new IllegalStateException("Please provide a valid xml layout");
        }

        if (mSpinner != null) {
            mSpinner.setOnTouchListener((v, event) -> {
                hideKeyboard();
                v.performClick();
                return false;
            });
        }

        if (mHideCountryFlag) {
            if (mSpinner != null) {
                mSpinner.setVisibility(GONE);
            } else if (mCountryFlag != null) {
                mCountryFlag.setVisibility(GONE);
            }
        }

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String rawNumber = s.toString();
                if (rawNumber.isEmpty()) {
                    selectCountry(mDefaultCountryCode);
                } else {
                    if (rawNumber.startsWith("00")) {
                        rawNumber = rawNumber.replaceFirst("00", "+");
                        mTextView.removeTextChangedListener(this);
                        mTextView.setText(rawNumber);
                        mTextView.addTextChangedListener(this);
                        ((EditText) mTextView).setSelection(rawNumber.length());
                    }
                    try {
                        Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
                        String regionCode = mPhoneUtil.getRegionCodeForNumber(number);
                        if (regionCode != null && !regionCode.equalsIgnoreCase(mSelectedCountryCode)) {
                            selectCountry(regionCode);
                        }
                    } catch (NumberParseException ignored) {
                    }
                }
            }
        };

        if (mTextView instanceof EditText) {
            mTextView.addTextChangedListener(textWatcher);
        }

        if (mSpinner != null) {
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Country country = mAdapter.getItem(position);

                    if (country != null) {
                        mSelectedCountryCode = country.getCountryCode();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mSelectedCountryCode = null;
                }
            });
        }

        AsyncTask.execute(() -> {
            for (String countryId : mRestrictToCountries != null ?
                    mRestrictToCountries : Locale.getISOCountries()) {
                mCountries.add(new Country(countryId,
                        mCountryLocale != null ?
                                new Locale("", countryId).getDisplayCountry(mCountryLocale) :
                                new Locale("", countryId).getDisplayCountry()));
            }

            Collections.sort(mCountries, Country.CountryComparatorByName);
            mAdapter = new CountriesAdapter(getContext(), mCountries);

            if (mSelectedCountryCode == null) {
                mSelectedCountryCode = mDefaultCountryCode;
            }

            if (mSpinner != null) {
                mSpinner.post(() -> {
                    mSpinner.setAdapter(mAdapter);
                    selectCountry(mSelectedCountryCode);
                });
            }

            if (mCountryFlag != null) {
                mCountryFlag.post(() -> selectCountry(mSelectedCountryCode));
            }

            if (mPhoneNumber != null) {
                mTextView.post(() -> setPhoneNumber(mPhoneNumber));
            }
        });
    }

    /**
     * Gets spinner.
     *
     * @return the spinner
     */
    public Spinner getSpinner() {
        return mSpinner;
    }

    /**
     * Gets text view (or edit text).
     *
     * @return the edit text
     */
    public TextView getTextView() {
        return mTextView;
    }

    /**
     * Checks whether the entered phone number is valid or not.
     *
     * @return a boolean that indicates whether the number is of a valid pattern
     */
    public boolean isValid() {
        try {
            return mPhoneUtil.isValidNumber(parsePhoneNumber(getRawInput()));
        } catch (NumberParseException e) {
            return false;
        }
    }

    /**
     * Checks whether the entered phone number is short number for a given country,
     * e.g. 1144 for Slovakia. This check is not as reliable, but it's a trade off for us.
     *
     * @return <code>true</code> if a number is a possible short number,
     * <code>false</code> otherwise.
     */
    public boolean isShortNumber() {
        try {
            return ShortNumberInfo.getInstance().
                    isPossibleShortNumber(parsePhoneNumber(getRawInput()));
        } catch (NumberParseException e) {
            return false;
        }
    }

    private Phonenumber.PhoneNumber parsePhoneNumber(String number) throws NumberParseException {
        String defaultRegion = mSelectedCountryCode != null ? mSelectedCountryCode.toUpperCase() : "";
        return mPhoneUtil.parseAndKeepRawInput(number, defaultRegion);
    }

    /**
     * Get phone number in E164 international format.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return getPhoneNumber(false);
    }

    /**
     * Get phone number in international or national format.
     * @param nationalFormat specifies to return number in national format instead of E164 format.
     * @return the phone number
     */
    public String getPhoneNumber(boolean nationalFormat) {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(getRawInput());
            return mPhoneUtil.format(number, nationalFormat ?
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL :
                    PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException ignored) {
        }
        return getRawInput();
    }

    /**
     * Get currently set default country as uppercase ISO 3166 2-letter code.
     */
    public String getDefaultCountry() {
        if (mDefaultCountryCode == null) {
            return "";
        }

        return mDefaultCountryCode;
    }

    /**
     * Set default country.
     *
     * @param countryCode Uppercase ISO 3166 2-letter code.
     */
    public void setDefaultCountry(String countryCode) {
        mDefaultCountryCode = countryCode;
        if (mTextView.getText().toString().isEmpty()) {
            selectCountry(countryCode);
        }
    }

    private void selectCountry(@Nullable final String countryCode) {
        if (countryCode != null) {
            AsyncTask.execute(() -> {
                for (int i = 0; i < mCountries.size(); i++) {
                    Country country = mCountries.get(i);
                    if (country.getCountryCode().equalsIgnoreCase(countryCode)) {
                        mSelectedCountryCode = country.getCountryCode();
                        final int countryIdx = i;

                        if (mSpinner != null) {
                            mSpinner.post(() -> mSpinner.setSelection(countryIdx));
                        } else if (mCountryFlag != null) {
                            mCountryFlag.post(() -> mCountryFlag.setImageResource(mCountries.get(countryIdx).getResId(getContext())));
                        }
                    }
                }
            });
        }
    }

    /**
     * Sets phone number.
     *
     * @param rawNumber the raw number
     */
    public void setPhoneNumber(String rawNumber) {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
            String regionCode = mPhoneUtil.getRegionCodeForNumber(number);
            if (regionCode != null && !regionCode.equalsIgnoreCase(mSelectedCountryCode)) {
                selectCountry(regionCode);
            }

            mTextView.setText(mPhoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
        } catch (NumberParseException ignored) {
        }
    }

    private void hideKeyboard() {
        try {
            ((InputMethodManager) getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTextView.getWindowToken(), 0);
        } catch (NullPointerException e) {
            if (e.getMessage() != null) {
                Log.e("PhoneField", e.getMessage());
            }
        }
    }

    /**
     * Update layout attributes.
     */
    protected abstract void updateLayoutAttributes();

    /**
     * Gets layout res id.
     *
     * @return the layout res id
     */
    public abstract int getLayoutResId();

    /**
     * Sets hint.
     *
     * @param resId the res id
     */
    public void setHint(int resId) {
        mTextView.setHint(resId);
    }

    /**
     * Gets raw input.
     *
     * @return the raw input
     */
    public String getRawInput() {
        return mTextView.getText().toString();
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(String error) {
        mTextView.setError(error);
    }

    /**
     * Sets text color.
     *
     * @param resId the res id
     */
    public void setTextColor(int resId) {
        mTextView.setTextColor(resId);
    }
}
