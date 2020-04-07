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
package com.smartpack.smartflasher.utils

import android.content.Context
import android.preference.PreferenceManager

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 24, 2019
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

/***
 * Example of kotlin/java interoperability.
 *
 * As this class requires no instances, it is written as an "Object".
 * To call a method with no class instance, the method must be inside an [object]
 * hence, [object Prefs] too.
 *
 * For interop, [kotlin.jvm.JvmStatic] annotation is used since there are no
 * static methods in Kotlin.
 *
 * No java code was touched to create this class, good to go
 */
object Prefs {
    @JvmStatic
    fun getInt(name: String?, defaults: Int, context: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(name, defaults)
    }

    @JvmStatic
    fun saveInt(name: String?, value: Int, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(name, value).apply()
    }

    @JvmStatic
    fun getBoolean(name: String?, defaults: Boolean, context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults)
    }

    @JvmStatic
    fun saveBoolean(name: String?, value: Boolean, context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply()
    }
}