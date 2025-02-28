dependencies {
    // Room
    implementation("androidx.room:room-runtime:$androidxRoomVersion")
    ksp("androidx.room:room-compiler:$androidxRoomVersion")
    implementation("androidx.room:room-ktx:$androidxRoomVersion")
    implementation("androidx.room:room-rxjava2:$androidxRoomVersion")
    implementation("androidx.room:room-rxjava3:$androidxRoomVersion")
    implementation("androidx.room:room-guava:$androidxRoomVersion")
    testImplementation("androidx.room:room-testing:$androidxRoomVersion")
    implementation("androidx.room:room-paging:$androidxRoomVersion")

    // AndroidX
    implementation("androidx.compose.ui:ui:$androidxComposeVersion")
    implementation("androidx.compose.material:material3:$androidxMaterialVersion")
    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$androidxLifecycleVersion")
    implementation("androidx.activity:activity-compose:$androidxComposeVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling:$androidxComposeVersion")
}