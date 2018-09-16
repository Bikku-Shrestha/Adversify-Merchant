/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

package com.nepal.adversify.ui.home;

import com.nepal.adversify.domain.callback.ConnectionCallback;
import com.nepal.adversify.domain.callback.PayloadCallback;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class HomeModule {

    @Binds
    abstract ConnectionCallback bindsConnectionCallback(HomeFragment homeFragment);

    @Binds
    abstract PayloadCallback bindsPayloadCallback(HomeFragment homeFragment);

}
