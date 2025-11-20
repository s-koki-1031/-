# 録音アプリ

Android用の録音アプリです。録音ボタンで録音を開始し、もう一度押すと録音を停止します。録音終了後、音声データと文字起こしファイルの2パターンで保存されます。

## 機能

- **録音機能**: 録音ボタンを押して録音を開始/停止
- **データ保存**: 録音した音声ファイルを保存
- **文字起こし保存**: 録音情報を含む文字起こしファイルを保存

## 必要な権限

- `RECORD_AUDIO`: マイクからの録音に必要
- `WRITE_EXTERNAL_STORAGE`: ファイル保存に必要（Android 10以下）
- `READ_EXTERNAL_STORAGE`: ファイル読み込みに必要（Android 10以下）

## 保存先

録音ファイルと文字起こしファイルは、アプリの外部ストレージに保存されます：

- **音声ファイル**: `/Android/data/com.example.recordingapp/files/Recordings/`
- **データファイル**: `/Android/data/com.example.recordingapp/files/Data/`
- **文字起こしファイル**: `/Android/data/com.example.recordingapp/files/Transcriptions/`

## ビルド方法

1. Android Studioでプロジェクトを開く
2. Gradle Syncを実行
3. デバイスまたはエミュレータで実行

## 注意事項

現在の実装では、文字起こしファイルには録音情報が保存されます。実際の音声認識による文字起こし機能を使用するには、Google Cloud Speech-to-Text APIの実装が必要です。

## 開発環境

- Android Studio
- Kotlin
- Min SDK: 24
- Target SDK: 34


