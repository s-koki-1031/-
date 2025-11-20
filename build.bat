@echo off
REM Android録音アプリ - ビルドスクリプト

echo ========================================
echo Android録音アプリ ビルドスクリプト
echo ========================================
echo.

REM 環境変数の確認
if "%ANDROID_HOME%"=="" (
    echo 警告: ANDROID_HOMEが設定されていません。
    echo Android SDKのパスを設定してください。
    echo 例: set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
    echo.
)

REM Gradle Wrapperでビルド
echo Gradle Wrapperでビルドを開始します...
echo.

REM クリーンビルド
echo [1/3] クリーンビルドを実行中...
call gradlew.bat clean
if %ERRORLEVEL% neq 0 (
    echo エラー: クリーンビルドに失敗しました。
    pause
    exit /b 1
)

REM デバッグAPKのビルド
echo.
echo [2/3] デバッグAPKをビルド中...
call gradlew.bat assembleDebug
if %ERRORLEVEL% neq 0 (
    echo エラー: デバッグAPKのビルドに失敗しました。
    pause
    exit /b 1
)

REM リリースAPKのビルド
echo.
echo [3/3] リリースAPKをビルド中...
call gradlew.bat assembleRelease
if %ERRORLEVEL% neq 0 (
    echo エラー: リリースAPKのビルドに失敗しました。
    pause
    exit /b 1
)

echo.
echo ========================================
echo ビルドが完了しました！
echo ========================================
echo.
echo デバッグAPK: app\build\outputs\apk\debug\app-debug.apk
echo リリースAPK: app\build\outputs\apk\release\app-release.apk
echo.
pause

