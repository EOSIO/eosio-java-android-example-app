echo "starting veracode scan" && \
./gradlew clean assembleDebug && \
java -jar vosp-api-wrappers-java-$VERACODE_WRAPPER_VERSION.jar -vid $VERACODE_API_ID -vkey $VERACODE_API_KEY \
-action uploadandscan -appname $VERACODE_APP_NAME -createprofile false \
-filepath app/build/outputs/apk/debug/app-debug.apk -version "$TRAVIS_JOB_ID - $TRAVIS_JOB_NUMBER $DATE" -scantimeout 3600 