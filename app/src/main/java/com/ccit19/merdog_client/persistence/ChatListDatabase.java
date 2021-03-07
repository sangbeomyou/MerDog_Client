/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ccit19.merdog_client.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * The Room database that contains the Users table
 */
@Database(entities = {ChatList.class}, version = 1,exportSchema = false)
public abstract class ChatListDatabase extends RoomDatabase {

    private static volatile ChatListDatabase INSTANCE;

    public abstract ChatListDao chatListDao();

    public static ChatListDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ChatListDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatListDatabase.class, "Sample.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
