# ニュース投稿・管理プラットフォーム

大学の卒業制作として作成した、Spring Boot と Vue による Web アプリケーションです。
ニュースの投稿・閲覧、コメント、いいね、お気に入り、イベントへの参加申し込みと、管理者向けの管理画面を備えています。

このリポジトリはソースコードを紹介するために整理したものです。画面と既存のコードコメントは中国語です。

> **再現環境について：** 現在のソース一式には、元のデータベースの作成 SQL と初期データが含まれていません。フロントエンドの構成やバックエンドの実装は確認できますが、全機能を動作させるには元のデータベース定義の追加が必要です。

## 主な機能

以下はソースコードで確認できる機能です。データベースを含む動作確認の結果ではありません。

| 分野 | 実装内容 |
| --- | --- |
| アカウント | ユーザー登録、ログイン、管理者・一般ユーザーの区分、プロフィール編集、パスワード変更 |
| ニュース | 記事の投稿・編集・削除、カテゴリー・タグ、検索、ページ分割、閲覧数によるランキング |
| 関連記事 | 記事のタグを使った関連記事の抽出 |
| ユーザー間の交流 | コメント、いいね、お気に入り、各ユーザーの投稿・反応の一覧 |
| イベント | イベント情報の管理・閲覧、参加申し込み、申し込み情報の管理 |
| 管理画面 | ユーザー、管理者、記事、カテゴリー、コメント、お知らせなどの管理 |
| ファイル | 画像のアップロードと取得、リッチテキストエディターへの画像挿入 |

関連記事の処理はタグの照合によるもので、機械学習モデルを使った推薦ではありません。

## 使用技術

バージョンは `pom.xml` と同梱の `package-lock.json` に基づきます。

| 領域 | 技術・バージョン |
| --- | --- |
| バックエンド | Java（ビルド設定の対象：Java 8）、Spring Boot 2.5.9 |
| データアクセス | MyBatis Spring Boot Starter 2.2.1、PageHelper 1.4.6 |
| データベース | MySQL（元のサーバーバージョンは不明） |
| 認証 | java-jwt 4.3.0、Spring MVC のインターセプター |
| フロントエンド | Vue 2.7.14、Vue Router 3 系、Element UI 2.15.14 |
| HTTP 通信 | Axios 1.5.1 |
| 記事編集 | wangEditor 4.7.15、highlight.js |
| ビルド | Maven、Vue CLI Service 5.0.8、npm |

`vue/package.json` では Vue を `^2.6.14` と指定していますが、ロックファイルに記録されているバージョンは `2.7.14` です。

## 構成とコードの読み方

フロントエンドから Axios で Spring Boot の API を呼び出し、Controller → Service → Mapper の順に処理します。Mapper の SQL と MySQL がデータの保存・取得を担当します。

| パス | 内容 |
| --- | --- |
| `springboot/` | Maven プロジェクト |
| `springboot/src/main/java/com/example/controller/` | API の入口 |
| `springboot/src/main/java/com/example/service/` | 業務処理 |
| `springboot/src/main/java/com/example/mapper/` | MyBatis の Mapper インターフェース |
| `springboot/src/main/resources/mapper/` | SQL マッピング |
| `springboot/src/main/java/com/example/common/config/` | 認証・CORS などの設定 |
| `vue/src/views/front/` | 一般ユーザー向け画面 |
| `vue/src/views/manager/` | 管理画面 |
| `vue/src/router/index.js` | 画面のルーティング |
| `vue/src/utils/request.js` | API 通信とトークンの付与 |
| `package.json` | プロジェクト直下のエディター関連依存関係 |
| `vue/package.json` | フロントエンドの依存関係とビルドコマンド |

実装を確認する場合は、[記事編集画面](vue/src/views/front/NewBlog.vue)、[記事 API](springboot/src/main/java/com/example/controller/BlogController.java)、[記事の業務処理](springboot/src/main/java/com/example/service/BlogService.java)、[SQL マッピング](springboot/src/main/resources/mapper/BlogMapper.xml) の順に読むと、画面からデータベースまでの処理を追えます。

## ローカル環境での準備

必要なものは JDK、Maven、Node.js/npm、および元のテーブル定義を復元した MySQL です。Java の対象バージョンは `pom.xml` で 8 に設定されています。元の開発環境の JDK・Node.js・MySQL の実バージョンは、この資料だけでは確認できません。

### 1. データベースを用意する

元の `manager` データベースの **テーブル定義** を用意してください。空のデータベースを作成するだけでは機能を利用できません。

元の SQL はまだ同梱されていないため、この手順を現時点で完了することはできません。追加する場合は、個人情報や実際のパスワードを含まない定義を `database/schema.sql` に配置する想定です。

### 2. バックエンドを起動する

プロジェクト直下で、データベースへの接続情報を環境変数に設定します。値は自分のローカル環境に合わせて変更してください。

```bash
export DB_USERNAME='your_local_db_user'
export DB_PASSWORD='your_local_db_password'
```

必要に応じて `DB_URL` も設定できます。省略すると `localhost:3306` の `manager` データベースに接続します。

```bash
cd springboot
mvn spring-boot:run
```

API のポートは `9090` です。`mvn` は Maven の実行コマンド、`spring-boot:run` はアプリケーションを起動するゴールです。

画像はバックエンドの作業ディレクトリー内の `files/` に保存されます。この起動方法では `springboot/files/` になります。過去にアップロードした画像はリポジトリには含めていません。

### 3. フロントエンドを起動する

別のターミナルで、プロジェクト直下から実行します。

```bash
npm ci
cd vue
npm ci
npm run serve
```

直下の `npm ci` も必要です。元の構成では wangEditor などの依存関係がプロジェクト直下に置かれているためです。`npm ci` はロックファイルを基に依存関係をインストールします。

起動後は `http://localhost:8080/login` にアクセスします。API の接続先は `vue/.env.development` の `http://localhost:9090` です。

### 4. フロントエンドをビルドする

`vue/` 内で実行します。

```bash
npm run build
```

出力先は `vue/dist/` です。`vue/.env.production` もローカル確認用の API URL にしてあります。別のサーバーを使う場合は、Git 管理対象外の `vue/.env.production.local` に `VUE_APP_BASEURL` を設定してからビルドしてください。

このソースコードを GitHub に置くだけでは、MySQL や Java の API サーバーは起動しません。

## 確認状況

- Vue コンポーネント 31 個のテンプレートとスクリプト、および JavaScript ファイル 5 個の構文チェックを実施し、構文エラーは検出されませんでした。
- XML 10 ファイル、JSON 5 ファイル、YAML 設定の解析を確認しました。
- フロントエンドの完全なビルドは、検証環境でネットワークインターフェース情報を取得できず、Vue CLI の初期化時に停止しました。ビルド成功は確認できていません。
- バックエンドは Maven とデータベースが検証環境にないため、ビルド・起動・API の動作は未確認です。

詳細は [整理・検証メモ](docs/PREPARATION.zh-CN.md) を参照してください。

## 現状の制約

- データベースの作成 SQL、初期データ、実行時にアップロードされた画像は含まれていません。
- パスワードの比較・保存と JWT の署名には卒業制作時の実装が残っています。運用する場合は、パスワードのハッシュ化と署名鍵の管理方法を見直す必要があります。
- JWT による認証はありますが、管理者操作や本人のデータに対する操作の認可は別途確認が必要です。
- ファイル操作は認証対象から除外されているため、外部公開する場合はアクセス制御とファイル名の検証が必要です。

今回の整理では、上記の業務処理や認証方式の改修は行っていません。

## ファイルの扱い

依存パッケージ、実行時の画像、ビルド成果物、ローカルのパスワードは Git 管理対象から外しています。データベースの接続情報は環境変数から読み込みます。

使用しているライブラリーのライセンスは、それぞれの配布元に従います。この整理作業では、プロジェクト全体に新たなオープンソースライセンスを付与していません。
