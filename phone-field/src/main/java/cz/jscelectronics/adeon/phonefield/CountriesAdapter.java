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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.List;

/**
 * Adapter for the countries list spinner
 */
public class CountriesAdapter extends ArrayAdapter<Country> implements SpinnerAdapter {

    private final LayoutInflater mInflater;
    private final PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    CountriesAdapter(Context context, List<Country> countries) {
        super(context, R.layout.item_country, R.id.name, countries);
        mInflater = LayoutInflater.from(getContext());
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Country country = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_value, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.flag);
        imageView.setImageResource(country != null ? country.getResId(getContext()) : 0);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_country, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mName = convertView.findViewById(R.id.name);
            viewHolder.mDialCode = convertView.findViewById(R.id.dial_code);
            viewHolder.mFlag = convertView.findViewById(R.id.flag);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Country country = getItem(position);
        if (country != null) {
            viewHolder.mFlag.setImageResource(country.getResId(getContext()));
            viewHolder.mName.setText(country.getCountryName());
            viewHolder.mDialCode.setText(String.format("+%s",
                    mPhoneUtil.getCountryCodeForRegion(country.getCountryCode())));
        }
        return convertView;

    }

    private static class ViewHolder {
        TextView mName;
        TextView mDialCode;
        ImageView mFlag;
    }
}
