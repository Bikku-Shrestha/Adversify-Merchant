/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.nepal.adversify.di.module;


import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.nepal.adversify.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.inflationx.calligraphy3.CalligraphyConfig;

@Module
public class AppModule {


    @Provides
    @Singleton
    CalligraphyConfig provideCalligraphyDefaultConfig() {
        return new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/product-sans/ProductSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build();
    }

    @Provides
    @Singleton
    ConnectionsClient providesConnectionsClient(Context context) {
        return Nearby.getConnectionsClient(context);
    }

}
