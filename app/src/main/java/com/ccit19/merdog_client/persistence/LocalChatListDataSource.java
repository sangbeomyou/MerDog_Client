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

import com.ccit19.merdog_client.backServ.ChatListDataSource;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Using the Room database as a data source.
 */
public class LocalChatListDataSource implements ChatListDataSource {

    private final ChatListDao mChatListDao;

    public LocalChatListDataSource(ChatListDao chatListDao) {
        mChatListDao = chatListDao;
    }

    @Override
    public Flowable<ChatList> getChatList() {
        return mChatListDao.getChatList();
    }

    @Override
    public Completable insertOrUpdateChatList(ChatList chatList) {
        return mChatListDao.insertUser(chatList);
    }

    @Override
    public void deleteAllChatList() {
        mChatListDao.deleteAllUsers();
    }
}
