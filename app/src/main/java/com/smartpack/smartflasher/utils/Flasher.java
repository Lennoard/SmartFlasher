/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.smartflasher.utils;

import android.os.Environment;

import com.smartpack.smartflasher.utils.root.RootUtils;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class Flasher {

    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";

    private static final String RECOVERY = "/cache/recovery/";

    private static final String FLASHFILE = Utils.getInternalDataStorage() + "/flasher_log.txt";

    private static final String PATHFILE = Utils.getInternalDataStorage() + "/last_flash.txt";

    private static final String BOOT_PARTITION_INFO = Environment.getDataDirectory() + "/.boot_partition_info";

    private static final String RECOVERY_PARTITION_INFO = Environment.getDataDirectory() + "/.recovery_partition_info";

    public static boolean isZIPFileExtracted() {
        return Utils.existFile(ZIPFILE_EXTRACTED);
    }

    public static boolean hasRecovery() {
        return Utils.existFile(RECOVERY);
    }

    public static boolean isFlashLog() {
        return Utils.existFile(FLASHFILE);
    }

    public static boolean hasBootPartitionInfo() {
        return Utils.existFile(BOOT_PARTITION_INFO);
    }

    public static boolean hasRecoveryPartitionInfo() {
        return Utils.existFile(RECOVERY_PARTITION_INFO);
    }

    public static boolean isPathLog() {
        return Utils.existFile(PATHFILE);
    }

    public static void cleanLogs() {
        File PathLog = new File(PATHFILE);
        File FlashLog = new File(FLASHFILE);
        if (isPathLog()) {
            PathLog.delete();
        }
        if (isFlashLog()) {
            FlashLog.delete();
        }
    }

    public static void makeInternalStorageFolder() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void backupBootPartition(String name) {
        String backupFolder = Utils.getInternalDataStorage() + "/backup";
        makeInternalStorageFolder();
        if (!Utils.existFile(backupFolder)) {
            File bachupFolderPath = new File(backupFolder);
            if (bachupFolderPath.exists() && bachupFolderPath.isFile()) {
                bachupFolderPath.delete();
            }
            bachupFolderPath.mkdirs();
        }
        String bootPartition = Utils.getInternalDataStorage() + "/backup/" + name;
        String command = "dd if=" + findBootPartition() + " of=" + bootPartition;
        RootUtils.runCommand(command);
    }

    public static void backupRecoveryPartition(String name) {
        String backupFolder = Utils.getInternalDataStorage() + "/backup";
        makeInternalStorageFolder();
        if (!Utils.existFile(backupFolder)) {
            File bachupFolderPath = new File(backupFolder);
            if (bachupFolderPath.exists() && bachupFolderPath.isFile()) {
                bachupFolderPath.delete();
            }
            bachupFolderPath.mkdirs();
        }
        String recoveryPartition = Utils.getInternalDataStorage() + "/backup/" + name;
        String command = "dd if=" + findRecoveryPartition() + " of=" + recoveryPartition;
        RootUtils.runCommand(command);
    }

    public static long fileSize(File file) {
        return file.length();
    }

    public static void manualFlash(File file) {
        FileDescriptor fd = new FileDescriptor();
        String path = file.toString();
        String flashFolder = Utils.getInternalDataStorage() + "/flash";
        String RECOVERY_API = "3";
        String CleanUpCommand = "rm -r '" + flashFolder + "'";
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        makeInternalStorageFolder();
        if (Utils.existFile(flashFolder)) {
            RootUtils.runCommand(CleanUpCommand);
        }
        File flashFolderPath = new File(flashFolder);
        flashFolderPath.mkdirs();
        RootUtils.runCommand("unzip '" + path + "' -d '" + flashFolder + "'");
        if (isZIPFileExtracted()) {
            RootUtils.runCommand("cd '" + flashFolder + "' && mount -o remount,rw / && mkdir /tmp");
            RootUtils.runCommand("mke2fs -F tmp.ext4 250000 && mount -o loop tmp.ext4 /tmp/");
            RootUtils.runCommand("sh META-INF/com/google/android/update-binary '" + RECOVERY_API + "' " + fd + " '" + path + "'| tee '" + Utils.getInternalDataStorage() + "'/flasher_log.txt");
            // Maintain flashing history
            String date = RootUtils.runCommand("date");
            RootUtils.runCommand("echo '" + date + "' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt");
            RootUtils.runCommand("echo -- '" + path + "' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt");
            RootUtils.runCommand("echo ' ' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt");
            RootUtils.runCommand(CleanUpCommand);
        }
    }

    public static void flashBootPartition(File file) {
        String command = "dd if='" + file.toString() + "' of=" + findBootPartition();
        RootUtils.runCommand(command);
    }

    public static void flashRecoveryPartition(File file) {
        String command = "dd if='" + file.toString() + "' of=" + findRecoveryPartition();
        RootUtils.runCommand(command);
    }

    public static void exportBootPartitionInfo() {
        /*
         * Inspired from the "find_block()" function on Magisk by topjohnwu @ xda-developers.com
         * Ref: https://github.com/topjohnwu/Magisk/blob/074b1f8c61e0cd03aea152346ad233d2278354f4/scripts/util_functions.sh#L146
         */
        String Command = "echo $(find /dev/block/ -type l -iname boot$(getprop ro.boot.slot_suffix)) Created by Smart Flasher > " + BOOT_PARTITION_INFO;
        if (!hasBootPartitionInfo()) {
            RootUtils.runCommand(Command);
        }
    }

    public static boolean emptyBootPartitionInfo() {
        return Utils.readFile(BOOT_PARTITION_INFO).isEmpty();
    }

    public static boolean BootPartitionInfo() {
        return Utils.readFile(BOOT_PARTITION_INFO).contains("boot");
    }

    public static String findBootPartition() {
        String partitions = Utils.readFile(BOOT_PARTITION_INFO);
        int i = partitions.indexOf(' ');
        return partitions.substring(0, i);
    }

    public static void exportRecoveryPartitionInfo() {
        String Command = "echo $(find /dev/block/ -type l -iname recovery) Created by Smart Flasher > " + RECOVERY_PARTITION_INFO;
        if (!isABDevice() && !hasRecoveryPartitionInfo()) {
            RootUtils.runCommand(Command);
        }
    }

    public static boolean emptyRecoveryPartitionInfo() {
        return Utils.readFile(RECOVERY_PARTITION_INFO).isEmpty();
    }

    public static boolean RecoveryPartitionInfo() {
        return Utils.readFile(RECOVERY_PARTITION_INFO).contains("recovery");
    }

    public static String findRecoveryPartition() {
        String partitions = Utils.readFile(RECOVERY_PARTITION_INFO);
        int i = partitions.indexOf(' ');
        return partitions.substring(0, i);
    }

    public static boolean isABDevice() {
        return Utils.readFile(BOOT_PARTITION_INFO).contains("boot_a") || Utils.readFile(BOOT_PARTITION_INFO).contains("boot_b");
    }

    public static String getPath() {
        File file = new File(Utils.getInternalDataStorage() + "/backup/");
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
        return file.toString();
    }

}
