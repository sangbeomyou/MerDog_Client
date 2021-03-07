package com.ccit19.merdog_client.ui.dashboard;

import androidx.lifecycle.ViewModel;

import com.ccit19.merdog_client.backServ.ChatListDataSource;
import com.ccit19.merdog_client.persistence.ChatList;
import com.ccit19.merdog_client.persistence.ChatListDao;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class DashboardViewModel extends ViewModel {

    private final ChatListDataSource mDataSource;

    private ChatListDao mListDao;

    private ChatList mChatList;

    public DashboardViewModel(ChatListDataSource dataSource) {
        mDataSource = dataSource;
    }

    /**
     * Get the user name of the user.
     *
     * @return a {@link Flowable} that will emit every time the user name has been updated.
     */
    public Flowable<ChatList> getChatListAll(){
        return mListDao.getChatList();
    }

    public Flowable<String> getChatList() {
        return mDataSource.getChatList()
                // for every emission of the user, get the user name
                .map(chatList -> {
                    mChatList = chatList;
                    return chatList.getmPetName();
                });

    }

    /**
     * Update the user name.
     *
     *
     * @param roomID
     * @param roomstate
     * @param requestid
     * @param petName the new user name
     * @return a {@link Completable} that completes when the user name is updated
     */
    public Completable updateChatList(String roomID,final String petName, String roomstate, String requestid ) {
        // if there's no user, create a new user.
        // if we already have a user, then, since the user object is immutable,
        // create a new user, with the id of the previous user and the updated user name.
        mChatList = mChatList == null ? new ChatList(roomID,petName,roomstate,requestid) : new ChatList(mChatList.mRoomID, petName,roomstate,requestid);
        return mDataSource.insertOrUpdateChatList(mChatList);
    }
}