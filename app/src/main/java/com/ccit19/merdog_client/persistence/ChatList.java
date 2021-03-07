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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.UUID;

/**
 * Immutable model class for a ChatList
 */
@Entity(tableName = "chatlist")
public class ChatList {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "room_id")
    public String mRoomID;

    @ColumnInfo(name = "pet_name")
    public String mPetName;

    @ColumnInfo(name = "room_state")
    public String mRoomState;

    @ColumnInfo(name = "chat_request_id")
    public String mRequestID;

    @Ignore
    public ChatList(String petName,String roomState, String requestID) {
        mRoomID = UUID.randomUUID().toString();
        mPetName = petName;
        mRoomState=roomState;
        mRequestID=requestID;
    }

    public ChatList(String roomID,String petName,String roomState, String requestID) {
        this.mRoomID = roomID;
        this.mPetName = petName;
        this.mRoomState=roomState;
        this.mRequestID=requestID;
    }


    @NonNull
    public String getmRoomID() {
        return mRoomID;
    }

    public void setmRoomID(@NonNull String mRoomID) {
        this.mRoomID = mRoomID;
    }

    public String getmPetName() {
        return mPetName;
    }

    public void setmPetName(String mPetName) {
        this.mPetName = mPetName;
    }

    public String getmRoomState() {
        return mRoomState;
    }

    public void setmRoomState(String mRoomState) {
        this.mRoomState = mRoomState;
    }

    public String getmRequestID() {
        return mRequestID;
    }

    public void setmRequestID(String mRequestID) {
        this.mRequestID = mRequestID;
    }
}
