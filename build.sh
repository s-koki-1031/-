#!/bin/bash
# Android録音アプリ - ビルドスクリプト

echo "========================================"
echo "Android録音アプリ ビルドスクリプト"
echo "========================================"
echo ""

# 環境変数の確認
if [ -z "$ANDROID_HOME" ]; then
    echo "警告: ANDROID_HOMEが設定されていません。"
    echo "Android SDKのパスを設定してください。"
    echo "例: export ANDROID_HOME=\$HOME/Android/Sdk"
    echo ""
fi

# Gradle Wrapperでビルド
echo "Gradle Wrapperでビルドを開始します..."
echo ""

# クリーンビルド
echo "[1/3] クリーンビルドを実行中..."
./gradlew clean
if [ $? -ne 0 ]; then
    echo "エラー: クリーンビルドに失敗しました。"
    exit 1
fi

# デバッグAPKのビルド
echo ""
echo "[2/3] デバッグAPKをビルド中..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "エラー: デバッグAPKのビルドに失敗しました。"
    exit 1
fi

# リリースAPKのビルド
echo ""
echo "[3/3] リリースAPKをビルド中..."
./gradlew assembleRelease
if [ $? -ne 0 ]; then
    echo "エラー: リリースAPKのビルドに失敗しました。"
    exit 1
fi

echo ""
echo "========================================"
echo "ビルドが完了しました！"
echo "========================================"
echo ""
echo "デバッグAPK: app/build/outputs/apk/debug/app-debug.apk"
echo "リリースAPK: app/build/outputs/apk/release/app-release.apk"
echo ""

