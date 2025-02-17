
/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.prodman

import android.app.Application
import com.example.prodman.data.ProdRoomDatabase


class ProductApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    //val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
    val database: ProdRoomDatabase by lazy { ProdRoomDatabase.getDatabase(this) }
}
