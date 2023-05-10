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
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Country object that holds the country iso2 code, name, and dial code.
 */
@SuppressWarnings("WeakerAccess")
public class Country implements Comparable<Country> {

    private final String mCode;

    private final String mName;

    public Country(String code, String name) {
        mCode = code;
        mName = name;
    }

    public String getCountryCode() {
        return mCode;
    }

    public String getCountryName() {
        return mName;
    }

    public @DrawableRes
    int getResId(Context context) {
        String name = String.format("country_flag_%s", mCode.toLowerCase());
        final Resources resources = context.getResources();
        @DrawableRes int resourceId = resources.getIdentifier(name, "drawable", context.getPackageName());
        if (resourceId == 0) {
            // Resource not found. Fallback to UNKNOWN flag.
            name = "country_flag_unknown";
            resourceId = resources.getIdentifier(name, "drawable", context.getPackageName());
        }

        return resourceId;
    }

    @Override
    public int compareTo(Country o) {
        Collator coll = Collator.getInstance(Locale.getDefault());
        coll.setStrength(Collator.PRIMARY);
        return coll.compare(this.getCountryName(), o.getCountryName());
    }

    public static Comparator<Country> CountryComparatorByName = new Comparator<Country>() {
        @Override
        public int compare(Country o1, Country o2) {
            return o1.compareTo(o2);
        }
    };
}
