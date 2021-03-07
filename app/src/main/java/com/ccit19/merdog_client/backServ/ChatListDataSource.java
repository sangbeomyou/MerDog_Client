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

package com.ccit19.merdog_client.backServ;

import com.ccit19.merdog_client.persistence.ChatList;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Access point for managing user data.
 */
public interface ChatListDataSource {

    /**
     * Gets the user from the data source.
     *
     * @return the user from the data source.
     */
    Flowable<ChatList> getChatList();

    /**
     * Inserts the user into the data source, or, if this is an existing user, updates it.
     *
     * @param chatList the user to be inserted or updated.
     */
    Completable insertOrUpdateChatList(ChatList chatList);

    /**
     * Deletes all users from the data source.
     */
    void deleteAllChatList();
}
