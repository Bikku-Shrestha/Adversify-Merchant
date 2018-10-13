/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

package com.nepal.adversify.ui.home;

import com.generic.appbase.domain.event.OnItemClickCallback;
import com.nepal.adversify.data.ConnectedClient;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class HomeBinderModule {

    @Binds
    abstract OnItemClickCallback<ConnectedClient> bindsOnItemClickCallback(HomeFragment homeFragment);

}
