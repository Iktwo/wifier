package com.iktwo.wifier;

import android.content.Context;

import com.iktwo.wifier.data.source.NetworksLocalDataSource;
import com.iktwo.wifier.data.source.NetworksRepository;

public class Injection {

    public static NetworksRepository provideNetworksRepository(Context context) {
        return NetworksRepository.getInstance(NetworksLocalDataSource.getInstance(context));
    }
}
