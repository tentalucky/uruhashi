package mahoroba.uruhashi.data

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import mahoroba.uruhashi.data.game.GameData
import mahoroba.uruhashi.data.game.StartingMemberData
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType
import mahoroba.uruhashi.domain.game.TeamClass
import java.util.*
import kotlin.concurrent.thread

object AppDatabaseProvider {
    @Volatile private var database : AppDatabase? = null
    private val lock = Any()

    fun getDatabase(application: Application) : AppDatabase {
        if (database == null) {
            synchronized(lock) {
                if (database == null) {
                    database = Room.databaseBuilder(
                        application.applicationContext, AppDatabase::class.java, "uruhashi_db")
                        .addCallback(callback)
                        .build()
                }
            }
        }

        return database as AppDatabase
    }

    private val callback = object: RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
//            thread {
//                refreshDatabase()
//            }
            return
        }
    }

    private fun refreshDatabase() {

        // region * Teams *

        database?.teamDao?.deleteAll()
        database?.teamDao?.insertTeam(TeamProfileData("c974b662-4e0c-4082-af73-d2fc1f25b384", "中日ドラゴンズ", "中日", 5))
        database?.teamDao?.insertTeam(TeamProfileData("975126f1-416e-4190-b373-f9614ded977b", "読売ジャイアンツ", "巨人", 4))
        database?.teamDao?.insertTeam(TeamProfileData("8e7c1eb0-0305-4224-9bd9-2833984c201d", "阪神タイガース", "阪神", 4))
        database?.teamDao?.insertTeam(TeamProfileData("56855afa-d8dc-4458-accf-0a3ba13f7c42", "広島東洋カープ", "広島", 4))
        database?.teamDao?.insertTeam(TeamProfileData("5ca5b52e-755f-44a0-891a-7b099b9cfcd6", "横浜DeNAベイスターズ", "DeNA", 4))
        database?.teamDao?.insertTeam(TeamProfileData("ffc480c0-8fe9-4ca5-93ee-9bdcc18a56d7", "東京ヤクルトスワローズ", "ヤクルト", 4))
        database?.teamDao?.insertTeam(TeamProfileData("14d9bb4b-e1c7-4ac1-b511-8e230f30a8f0", "西武ライオンズ", "西武", 3))
        database?.teamDao?.insertTeam(TeamProfileData("fb667f17-2615-434d-8102-49112018302b", "福岡ソフトバンクホークス", "ソフトバンク", 3))
        database?.teamDao?.insertTeam(TeamProfileData("3626add0-c694-42a0-9d09-8f03a75e5506", "東北楽天ゴールデンイーグルス", "楽天", 3))
        database?.teamDao?.insertTeam(TeamProfileData("bfb974e8-5cfa-4f7d-b9cd-6427edc731cf", "北海道日本ハムファイターズ", "日本ハム", 3))
        database?.teamDao?.insertTeam(TeamProfileData("95e81419-5add-4be9-8bf1-b95e4d51d2e8", "千葉ロッテマリーンズ", "ロッテ", 3))
        database?.teamDao?.insertTeam(TeamProfileData("930c8abb-2578-416a-9c9f-d2f061a41dc0", "オリックスバファローズ", "オリックス", 3))

        // endregion

        // region * Players *

        database?.playerDao?.deleteAll()
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "72c652c1-34ab-4743-8f05-a223e1458486"; this.familyName = "小笠原"; this.firstName = "慎之介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7d726e44-bbd9-4d43-bd6b-b8c4f03dd137"; this.familyName = "田島"; this.firstName = "慎二"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "3b596863-1945-4ad7-b483-3a6b7a84a1f0"; this.familyName = "橋本"; this.firstName = "侑樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "e38513fb-2099-4842-b13a-24c92f832f32"; this.familyName = "谷元"; this.firstName = "圭介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7a59ff44-4bc9-4e52-9dc5-ea4de17f6c12"; this.familyName = "又吉"; this.firstName = "克樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "3d07c6bf-6d09-4e94-8750-3a00138cf69b"; this.familyName = "柳"; this.firstName = "裕也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "0df40763-7315-4921-8c98-4b4070297899"; this.familyName = "吉見"; this.firstName = "一起"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "460e0c10-e7b5-4be7-9c9a-c47536cddc7d"; this.familyName = "岡田"; this.firstName = "俊哉"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "467c6976-a950-4c74-8e9d-43644a58ed8e"; this.familyName = "大野"; this.firstName = "雄大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "04c33493-07be-4bfc-bb95-79064ed26a6a"; this.familyName = "福谷"; this.firstName = "浩司"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "3d046b48-251b-4777-b872-4bb19965c79a"; this.familyName = "佐藤"; this.firstName = "優"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "fd16abf2-3e1f-499c-bab6-0d2117d31e64"; this.familyName = "梅津"; this.firstName = "晃大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "baffd98f-57f8-41cf-a118-25669b4125a2"; this.familyName = "山井"; this.firstName = "大介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a088694d-2423-4fe3-9d35-2629e16e2231"; this.familyName = "阿知羅"; this.firstName = "拓馬"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "9d6cdcf5-5679-408e-a1a6-4555825fabe0"; this.familyName = "祖父江"; this.firstName = "大輔"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6ebe9ae7-d720-4662-a162-402d6ab5ab38"; this.familyName = "福"; this.firstName = "敬登"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a450a34f-2271-4e04-a8be-51cc0375f1b9"; this.familyName = "岡野"; this.firstName = "祐一郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8a7204fc-d3e1-407d-9d4e-d90e8adb470b"; this.familyName = "松葉"; this.firstName = "貴大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7bb17feb-2fc2-490f-8423-cb5f10d7e97e"; this.familyName = "石川"; this.firstName = "翔"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "eb61f947-a1a9-4f73-b04c-d51344d370b4"; this.familyName = "勝野"; this.firstName = "昌慶"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "3eea1d3e-76b0-460c-b625-310d9657711e"; this.familyName = "三ツ間"; this.firstName = "卓也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "05ccddab-1b41-49ea-afc3-e344177a39ca"; this.familyName = "鈴木"; this.firstName = "博志"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "56ee84e7-69b5-470f-bb84-0681d198e68f"; this.familyName = "笠原"; this.firstName = "祥太郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "75f2b449-9005-46ca-b412-289f29854e69"; this.familyName = "清水"; this.firstName = "達也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "dcc5237d-cf55-4cf6-9bed-aa147db23518"; this.familyName = "ゴンサレス"; this.firstName = "ルイス"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "816476c8-46d6-4520-b258-c9aa6d2a0e80"; this.familyName = "藤嶋"; this.firstName = "健人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "0e968360-3d3b-4561-92c1-bed36458a01f"; this.familyName = "山本"; this.firstName = "拓実"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "90c6932a-6b33-49a1-b722-0c571a892606"; this.familyName = "垣越"; this.firstName = "建伸"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "300da9a8-75a7-4f7b-a9bb-8ce1e93ed7c0"; this.familyName = "竹内"; this.firstName = "龍臣"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6f186413-33f7-40e7-8a53-6ee4b5d02111"; this.familyName = "小熊"; this.firstName = "凌祐"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "646b35ce-a535-44e0-bb52-0e1afa364da7"; this.familyName = "伊藤"; this.firstName = "準規"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d931922c-e45f-401c-98cc-308a1c699f70"; this.familyName = "濱田"; this.firstName = "達郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ed38fac8-ef80-4ec6-9350-7d3060bc4839"; this.familyName = "ロメロ"; this.firstName = "エンニー"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "08ac255d-fe25-4775-97c1-df305cfb1af8"; this.familyName = "マルティネス"; this.firstName = "ライデル"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1aa52a10-d195-49a7-823b-0335729b6e36"; this.familyName = "木下"; this.firstName = "雄介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d87b892e-b394-49f5-8744-ccd113e9b201"; this.familyName = "鈴木"; this.firstName = "翔太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "38a55bd3-0a4b-40fa-b8b2-366b654dbf72"; this.familyName = "大野"; this.firstName = "奨太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "0f0c0ecd-97ae-412d-85fe-4c44f8dc4a51"; this.familyName = "木下"; this.firstName = "拓哉"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a60badd8-0731-4f6a-9564-1256d589f350"; this.familyName = "郡司"; this.firstName = "裕也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a6097af6-9c0a-4474-b311-6779a9a94541"; this.familyName = "加藤"; this.firstName = "匠馬"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8c9e8560-717b-4d58-9850-da40698c5805"; this.familyName = "石橋"; this.firstName = "康太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c4d10ee1-d879-47d5-9676-89d83ff2a3d3"; this.familyName = "桂"; this.firstName = "依央利"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "92586ac5-dc8f-48e0-82a9-1f9f79d7eeb7"; this.familyName = "髙松"; this.firstName = "渡"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ec5aefc4-0403-4984-84f0-f3b4c8c3cf1d"; this.familyName = "京田"; this.firstName = "陽太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6788d153-9ab1-4803-92b1-d403468fe591"; this.familyName = "石川"; this.firstName = "昂弥"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "29ec348d-9aad-4446-a444-3993ff48478f"; this.familyName = "高橋"; this.firstName = "周平"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "75637c0e-553d-4e5b-a80a-802b48cc54f2"; this.familyName = "阿部"; this.firstName = "寿樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "dc9bedad-86b7-4d91-a103-4a29b5d47633"; this.familyName = "根尾"; this.firstName = "昂"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "e4688ccb-1a6a-4e54-b1eb-6ae7791b7217"; this.familyName = "石川"; this.firstName = "駿"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "2ca010f1-5fcf-4c66-9c76-2c886cb45273"; this.familyName = "石垣"; this.firstName = "雅海"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "37a3dd0e-4fff-480a-b3b0-6cd1c95dd033"; this.familyName = "三ツ俣"; this.firstName = "大樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "65c72860-9ce5-4343-9de1-7e21c2da4918"; this.familyName = "溝脇"; this.firstName = "隼人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6741dbb0-63c3-4b39-a8c7-e1ee521bf9b9"; this.familyName = "福田"; this.firstName = "永将"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "44d2eb46-82f0-450d-9389-2ea89f4be388"; this.familyName = "堂上"; this.firstName = "直倫"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1164c7c7-f101-4472-b599-03da052317e4"; this.familyName = "ビシエド"; this.firstName = "ダヤン"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "95125dac-2b9d-4acd-b678-1af7cf29647a"; this.familyName = "藤井"; this.firstName = "淳志"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.BOTH; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ba0730ea-5502-47f1-b20a-71b0306e0a5c"; this.familyName = "平田"; this.firstName = "良介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "95bbfea3-9117-429a-91fb-0e32466b8e97"; this.familyName = "大島"; this.firstName = "洋平"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ae43c670-9a08-4673-94fb-ab321ba3f949"; this.familyName = "遠藤"; this.firstName = "一星"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "5fc83951-1652-4479-91f6-fc907e88568f"; this.familyName = "井領"; this.firstName = "雅貴"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8a180d6b-1e6d-4fe6-81bd-6c90bf2927ad"; this.familyName = "渡辺"; this.firstName = "勝"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bc6bf8bb-37da-4c11-bbe9-4755a44a00fd"; this.familyName = "アルモンテ"; this.firstName = "ソイロ"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.BOTH; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7a17bf87-e324-4c83-84f1-e918b1e185d5"; this.familyName = "伊藤"; this.firstName = "康祐"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d9680a4f-ec91-484e-af12-1617b096d5ec"; this.familyName = "滝野"; this.firstName = "要"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d9af855d-8b31-42cc-9611-86c3030efcdb"; this.familyName = "武田"; this.firstName = "健吾"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "244de43e-826b-4ce5-81af-19ee83da9128"; this.familyName = "岡林"; this.firstName = "勇希"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "5cf351bf-3267-49be-8d41-bc68dc6286ba"; this.familyName = "ブリトー"; this.firstName = "サンディ"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "b8264e33-fd03-4e41-92bd-09032682ad37"; this.familyName = "浜田"; this.firstName = "智博"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6394d9d5-328f-4f0f-b14c-2a63adcb1c5b"; this.familyName = "丸山"; this.firstName = "泰資"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8f9e82a6-61b6-4ee3-a73c-2b609b6db215"; this.familyName = "大藏"; this.firstName = "彰人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "15f5096e-de6d-43ca-b8fc-afd5094956d4"; this.familyName = "松田"; this.firstName = "亘哲"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a2ca4455-02f1-4594-b925-5e9efc239512"; this.familyName = "石田"; this.firstName = "健人マルク"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c4067bdc-6f43-4d12-99d5-c8696bc148c5"; this.familyName = "ロドリゲス"; this.firstName = "ヤリエル"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a9db0e13-6cf4-48b7-b4f0-ed443bdb9ea9"; this.familyName = "マルティネス"; this.firstName = "アリエル"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4e756f9e-0aa0-4d95-bf52-bc4329affe94"; this.familyName = "石岡"; this.firstName = "諒太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "9ce1faea-ad38-4661-91de-005f2e2218a8"; this.familyName = "シエラ"; this.firstName = "モイセ"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8fc83f91-8664-4946-8b5b-33b4798919ea"; this.familyName = "デラロサ"; this.firstName = "ルビー"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "408a37f3-74d1-4cda-826f-350507884eac"; this.familyName = "松井"; this.firstName = "裕樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bcea2564-5cca-46da-a3a1-de18c2a2d945"; this.familyName = "岸"; this.firstName = "孝之"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4293bdc4-6c6e-4e8b-a959-ace9e034ae5c"; this.familyName = "近藤"; this.firstName = "弘樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "cba9be53-73d4-4691-a9de-ac7a7b846fc5"; this.familyName = "森原"; this.firstName = "康平"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bade3237-3525-4d28-b70e-406bbe5c9911"; this.familyName = "則本"; this.firstName = "昂大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4da052b7-dbc9-481a-a32a-996a1900408b"; this.familyName = "シャギワ"; this.firstName = "ジョン・トーマス"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ef0b31e7-cd82-4387-b50a-1875399d35d2"; this.familyName = "涌井"; this.firstName = "秀章"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "03a8ac1b-270e-4811-82db-6dcf46cb2a59"; this.familyName = "塩見"; this.firstName = "貴洋"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bab8c75c-2dad-4e43-9bfe-1107902659da"; this.familyName = "藤平"; this.firstName = "尚真"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "96e00149-d65e-47c7-ab97-19652612087b"; this.familyName = "安樂"; this.firstName = "智大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "b0aa6878-abe1-41ca-80a3-3e9140bac7c0"; this.familyName = "釜田"; this.firstName = "佳直"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7b85af6e-26fd-41dd-b56b-4c63e5808bc7"; this.familyName = "牧田"; this.firstName = "和久"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bc9b09b3-c798-40a9-81ea-d41ff3285b72"; this.familyName = "弓削"; this.firstName = "隼人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "39622de7-069e-4aba-9f33-c91130c70b5b"; this.familyName = "酒居"; this.firstName = "知史"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "3afe3e1d-cc87-4baf-9b8a-92a196ccf98f"; this.familyName = "福井"; this.firstName = "優也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d2cf65b7-5022-4269-9c95-351b2deca9af"; this.familyName = "ブセニッツ"; this.firstName = "アラン"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7bdeb5ef-7a98-4ee2-87ae-80896698ce06"; this.familyName = "引地"; this.firstName = "秀一郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "06eda5bd-242e-47bb-b477-f63e407a47cf"; this.familyName = "青山"; this.firstName = "浩二"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "e1750735-7c40-4c95-b776-f489d3fd3d5a"; this.familyName = "宋"; this.firstName = "家豪"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c87d26ab-1ffd-4838-aa05-74a2b3321f04"; this.familyName = "菅原"; this.firstName = "秀"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ee5cab34-07ef-4d4c-b7c3-378328e364c8"; this.familyName = "渡邊"; this.firstName = "佑樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d97e7bf8-6a6e-42d4-90a6-3f2b4f2f41ce"; this.familyName = "福森"; this.firstName = "耀真"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "36e7b821-9bc3-47ac-b4e4-7a7d7f5eecd2"; this.familyName = "津留﨑"; this.firstName = "大成"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d50c5dfc-c125-4e3f-a187-6e6d919504c1"; this.familyName = "高梨"; this.firstName = "雄平"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1789e491-aa67-41db-b5b0-de4e8a53fefc"; this.familyName = "鈴木"; this.firstName = "翔天"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8bf9a54b-dad6-4486-bfbe-4fa46a7d7f74"; this.familyName = "瀧中"; this.firstName = "瞭太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4a8af660-f267-4521-a06e-e54665fed85d"; this.familyName = "辛島"; this.firstName = "航"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "aae36025-cd3e-4899-a5df-93d0db8bbe08"; this.familyName = "熊原"; this.firstName = "健人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c26d5a0a-35b1-43eb-a07e-8567dc111ea8"; this.familyName = "石橋"; this.firstName = "良太"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "14f5139f-e97d-4cd9-9fae-3867137e05c0"; this.familyName = "佐藤"; this.firstName = "智輝"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "947ffc3c-a8fc-420a-80f7-4d7825e910bd"; this.familyName = "西口"; this.firstName = "直人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "b262f976-b67e-4994-9a35-f2879b6cce01"; this.familyName = ""; this.firstName = "由規"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "edf9e714-c4ea-4a77-91ef-c7fda5bfa6d8"; this.familyName = "寺岡"; this.firstName = "寛治"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "2b67e23c-006f-429b-939b-25585e0f021a"; this.familyName = "久保"; this.firstName = "裕也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "6f0b850d-4d82-4b3b-8042-fc74ae89810c"; this.familyName = "太田"; this.firstName = "光"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "e255a710-693b-47ac-9711-c201c5170dae"; this.familyName = "岡島"; this.firstName = "豪郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d124dc08-b718-4c72-8783-af5175320b0f"; this.familyName = "山下"; this.firstName = "斐紹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "a69eaff8-38a4-4748-bc41-9e87198b027b"; this.familyName = "足立"; this.firstName = "祐一"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4b951a3f-b87b-4b32-8666-fd9a1d470857"; this.familyName = "堀内"; this.firstName = "謙伍"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "051cce0d-cbb8-4889-9336-39faa58c594b"; this.familyName = "下妻"; this.firstName = "貴寛"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "b1b02b22-74b7-4a7f-83e3-a2c15e0a3f26"; this.familyName = "石原"; this.firstName = "彪"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "34f1a308-701c-489f-9f67-d28a450c9270"; this.familyName = "水上"; this.firstName = "桂"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8b15f74c-f974-49b8-9897-70ef9efd01c9"; this.familyName = "小深田"; this.firstName = "大翔"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "dcda758c-fc41-47a7-a2f2-c103d82e7037"; this.familyName = "浅村"; this.firstName = "栄斗"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "654d5bb7-b063-4704-b7e9-e43b30017246"; this.familyName = "茂木"; this.firstName = "栄五郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "98175012-48e6-4a41-9016-0cdf3a5f45f2"; this.familyName = "藤田"; this.firstName = "一也"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1410eeed-2cc7-4bc4-a73a-acdc8d9aaaf4"; this.familyName = "鈴木"; this.firstName = "大地"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4294b8fc-55d0-4d47-b65f-32c6f13908a8"; this.familyName = "黒川"; this.firstName = "史陽"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "44ab33f2-d1e9-4ebd-b9a6-0e5d58138161"; this.familyName = "渡辺"; this.firstName = "直人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8b48f4fc-a84b-4361-a58e-6c73607244e4"; this.familyName = ""; this.firstName = "銀次"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "2880d68b-d16f-4a83-be1b-c4e6cd032a3c"; this.familyName = "山﨑"; this.firstName = "幹史"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "450caf6c-8e32-406a-838b-1f38fdf70f83"; this.familyName = "内田"; this.firstName = "靖人"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "629413e1-4801-47aa-8968-b1d8dde1b2a9"; this.familyName = "ウィーラー"; this.firstName = "ゼラス"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d1369ae3-f54d-4fee-9ced-e261ff2a08bc"; this.familyName = "渡邊"; this.firstName = "佳明"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "24c732b9-ef87-422b-a24a-9a47dec6ec12"; this.familyName = "村林"; this.firstName = "一輝"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "939fd05c-c672-4e9c-b53e-e9017b32e263"; this.familyName = "オコエ"; this.firstName = "瑠偉"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "e6a77b99-5051-4fac-a0fd-701538410065"; this.familyName = "辰己"; this.firstName = "涼介"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8ef20cf0-3281-43b7-8797-02382c358a4f"; this.familyName = "田中"; this.firstName = "和基"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.BOTH; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c3aaa44f-000b-49a9-b803-f45e58947a60"; this.familyName = "島内"; this.firstName = "宏明"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "7b69cc41-4bd8-4d5d-9c14-bf07ef4eb913"; this.familyName = "岩見"; this.firstName = "雅紀"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ca694499-20d0-4b24-bf89-2e0544a069e6"; this.familyName = "下水流"; this.firstName = "昂"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "535a353a-cc4f-4861-aa5b-269a196b157e"; this.familyName = "小郷"; this.firstName = "裕哉"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "bc95563b-dd3a-4eef-a13c-369df68e044c"; this.familyName = "和田"; this.firstName = "恋"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "fe20c854-6608-4689-a5d7-da48a32b8172"; this.familyName = "ブラッシュ"; this.firstName = "ジャバリ"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1456de85-9ab1-4b72-9359-080782661e3f"; this.familyName = "武藤"; this.firstName = "敦貴"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "f6b23232-ea18-4931-a24a-fdd8f1a6ea64"; this.familyName = "フェルナンド"; this.firstName = "ルシアノ"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "8a00d815-bd99-4454-86c3-56cc8fda0931"; this.familyName = "ロメロ"; this.firstName = "ステフェン"
            this.nameType = NameType.FIRST_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "0f8413e6-d996-4dd0-952b-056769c7dcad"; this.familyName = "森"; this.firstName = "雄大"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "22b8447d-5148-4511-9271-2570e6627bab"; this.familyName = "王"; this.firstName = "彦程"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "dcb74dee-c49d-4984-9b5f-3c5cc2b6cfba"; this.familyName = "池田"; this.firstName = "隆英"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "4abf2acf-8a96-437b-8c6c-8899d688bef4"; this.familyName = "木村"; this.firstName = "敏靖"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "1b4570d6-d278-446e-928a-23d01a14d7ee"; this.familyName = "清宮"; this.firstName = "虎多朗"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "eddc93be-4b21-4ebe-ba82-7c95b628a52d"; this.familyName = "則本"; this.firstName = "佳樹"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "55330be9-a6bb-4312-be78-2c03e9d08dfa"; this.familyName = "小峯"; this.firstName = "新陸"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "d52220cf-1692-40ba-ae88-3985e7185c47"; this.familyName = "福山"; this.firstName = "博之"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c07450b6-a972-442c-872e-0c09decac1a1"; this.familyName = "江川"; this.firstName = "侑斗"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "2878f38e-38fd-4d67-8889-f17b17cbf964"; this.familyName = "吉持"; this.firstName = "亮汰"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "c657164e-5219-406c-990c-a698c82197e6"; this.familyName = "南"; this.firstName = "要輔"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "903b4b26-7e00-482f-b66d-d6396f2fbca3"; this.familyName = "松本"; this.firstName = "京志郎"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "b6efdac0-d57e-4bae-b1a4-013e8a9b9192"; this.familyName = "山﨑"; this.firstName = "真彰"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "516ef726-f2fa-4a7c-bf54-7c969b613c63"; this.familyName = "澤野"; this.firstName = "聖悠"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.RIGHT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "ddf39515-a0e1-433b-9636-4717df5b8405"; this.familyName = "中村"; this.firstName = "和希"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.LEFT; this.throwHand = HandType.LEFT
        })
        database?.playerDao?.insertPlayer(PlayerData().apply {
            this.id = "54731acc-331e-481d-bcce-4921e8ec702e"; this.familyName = ""; this.firstName = "耀飛"
            this.nameType = NameType.FAMILY_NAME_FIRST; this.batHand = HandType.RIGHT; this.throwHand = HandType.RIGHT
        })

        // endregion

        // region * Registers *

        database?.registerDao?.deleteAll()
        database?.registerDao?.insertRegisters(arrayListOf(
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "72c652c1-34ab-4743-8f05-a223e1458486"; this.uniformNumber = "11" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "7d726e44-bbd9-4d43-bd6b-b8c4f03dd137"; this.uniformNumber = "12" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "3b596863-1945-4ad7-b483-3a6b7a84a1f0"; this.uniformNumber = "13" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "e38513fb-2099-4842-b13a-24c92f832f32"; this.uniformNumber = "14" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "7a59ff44-4bc9-4e52-9dc5-ea4de17f6c12"; this.uniformNumber = "16" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "3d07c6bf-6d09-4e94-8750-3a00138cf69b"; this.uniformNumber = "17" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "0df40763-7315-4921-8c98-4b4070297899"; this.uniformNumber = "19" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "460e0c10-e7b5-4be7-9c9a-c47536cddc7d"; this.uniformNumber = "21" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "467c6976-a950-4c74-8e9d-43644a58ed8e"; this.uniformNumber = "22" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "04c33493-07be-4bfc-bb95-79064ed26a6a"; this.uniformNumber = "24" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "3d046b48-251b-4777-b872-4bb19965c79a"; this.uniformNumber = "25" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "fd16abf2-3e1f-499c-bab6-0d2117d31e64"; this.uniformNumber = "28" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "baffd98f-57f8-41cf-a118-25669b4125a2"; this.uniformNumber = "29" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a088694d-2423-4fe3-9d35-2629e16e2231"; this.uniformNumber = "30" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "9d6cdcf5-5679-408e-a1a6-4555825fabe0"; this.uniformNumber = "33" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "6ebe9ae7-d720-4662-a162-402d6ab5ab38"; this.uniformNumber = "34" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a450a34f-2271-4e04-a8be-51cc0375f1b9"; this.uniformNumber = "36" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "8a7204fc-d3e1-407d-9d4e-d90e8adb470b"; this.uniformNumber = "38" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "7bb17feb-2fc2-490f-8423-cb5f10d7e97e"; this.uniformNumber = "40" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "eb61f947-a1a9-4f73-b04c-d51344d370b4"; this.uniformNumber = "41" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "3eea1d3e-76b0-460c-b625-310d9657711e"; this.uniformNumber = "43" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "05ccddab-1b41-49ea-afc3-e344177a39ca"; this.uniformNumber = "46" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "56ee84e7-69b5-470f-bb84-0681d198e68f"; this.uniformNumber = "47" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "75f2b449-9005-46ca-b412-289f29854e69"; this.uniformNumber = "50" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "dcc5237d-cf55-4cf6-9bed-aa147db23518"; this.uniformNumber = "53" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "816476c8-46d6-4520-b258-c9aa6d2a0e80"; this.uniformNumber = "54" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "0e968360-3d3b-4561-92c1-bed36458a01f"; this.uniformNumber = "59" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "90c6932a-6b33-49a1-b722-0c571a892606"; this.uniformNumber = "61" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "300da9a8-75a7-4f7b-a9bb-8ce1e93ed7c0"; this.uniformNumber = "62" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "6f186413-33f7-40e7-8a53-6ee4b5d02111"; this.uniformNumber = "64" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "646b35ce-a535-44e0-bb52-0e1afa364da7"; this.uniformNumber = "65" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "d931922c-e45f-401c-98cc-308a1c699f70"; this.uniformNumber = "69" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "ed38fac8-ef80-4ec6-9350-7d3060bc4839"; this.uniformNumber = "70" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "08ac255d-fe25-4775-97c1-df305cfb1af8"; this.uniformNumber = "97" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "1aa52a10-d195-49a7-823b-0335729b6e36"; this.uniformNumber = "98" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "d87b892e-b394-49f5-8744-ccd113e9b201"; this.uniformNumber = "99" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "38a55bd3-0a4b-40fa-b8b2-366b654dbf72"; this.uniformNumber = "27" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "0f0c0ecd-97ae-412d-85fe-4c44f8dc4a51"; this.uniformNumber = "35" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a60badd8-0731-4f6a-9564-1256d589f350"; this.uniformNumber = "44" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a6097af6-9c0a-4474-b311-6779a9a94541"; this.uniformNumber = "52" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "8c9e8560-717b-4d58-9850-da40698c5805"; this.uniformNumber = "58" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "c4d10ee1-d879-47d5-9676-89d83ff2a3d3"; this.uniformNumber = "68" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "92586ac5-dc8f-48e0-82a9-1f9f79d7eeb7"; this.uniformNumber = "0" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "ec5aefc4-0403-4984-84f0-f3b4c8c3cf1d"; this.uniformNumber = "1" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "6788d153-9ab1-4803-92b1-d403468fe591"; this.uniformNumber = "2" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "29ec348d-9aad-4446-a444-3993ff48478f"; this.uniformNumber = "3" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "75637c0e-553d-4e5b-a80a-802b48cc54f2"; this.uniformNumber = "5" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "dc9bedad-86b7-4d91-a103-4a29b5d47633"; this.uniformNumber = "7" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "e4688ccb-1a6a-4e54-b1eb-6ae7791b7217"; this.uniformNumber = "9" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "2ca010f1-5fcf-4c66-9c76-2c886cb45273"; this.uniformNumber = "32" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "37a3dd0e-4fff-480a-b3b0-6cd1c95dd033"; this.uniformNumber = "37" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "65c72860-9ce5-4343-9de1-7e21c2da4918"; this.uniformNumber = "48" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "6741dbb0-63c3-4b39-a8c7-e1ee521bf9b9"; this.uniformNumber = "55" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "44d2eb46-82f0-450d-9389-2ea89f4be388"; this.uniformNumber = "63" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "1164c7c7-f101-4472-b599-03da052317e4"; this.uniformNumber = "66" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "95125dac-2b9d-4acd-b678-1af7cf29647a"; this.uniformNumber = "4" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "ba0730ea-5502-47f1-b20a-71b0306e0a5c"; this.uniformNumber = "6" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "95bbfea3-9117-429a-91fb-0e32466b8e97"; this.uniformNumber = "8" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "ae43c670-9a08-4673-94fb-ab321ba3f949"; this.uniformNumber = "23" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "5fc83951-1652-4479-91f6-fc907e88568f"; this.uniformNumber = "26" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "8a180d6b-1e6d-4fe6-81bd-6c90bf2927ad"; this.uniformNumber = "31" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "bc6bf8bb-37da-4c11-bbe9-4755a44a00fd"; this.uniformNumber = "42" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "7a17bf87-e324-4c83-84f1-e918b1e185d5"; this.uniformNumber = "49" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "d9680a4f-ec91-484e-af12-1617b096d5ec"; this.uniformNumber = "51" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "d9af855d-8b31-42cc-9611-86c3030efcdb"; this.uniformNumber = "56" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "244de43e-826b-4ce5-81af-19ee83da9128"; this.uniformNumber = "60" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "5cf351bf-3267-49be-8d41-bc68dc6286ba"; this.uniformNumber = "201" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "b8264e33-fd03-4e41-92bd-09032682ad37"; this.uniformNumber = "202" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "6394d9d5-328f-4f0f-b14c-2a63adcb1c5b"; this.uniformNumber = "204" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "8f9e82a6-61b6-4ee3-a73c-2b609b6db215"; this.uniformNumber = "206" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "15f5096e-de6d-43ca-b8fc-afd5094956d4"; this.uniformNumber = "207" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a2ca4455-02f1-4594-b925-5e9efc239512"; this.uniformNumber = "209" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "c4067bdc-6f43-4d12-99d5-c8696bc148c5"; this.uniformNumber = "212" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "a9db0e13-6cf4-48b7-b4f0-ed443bdb9ea9"; this.uniformNumber = "210" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "4e756f9e-0aa0-4d95-bf52-bc4329affe94"; this.uniformNumber = "205" },
            RegisterData().apply { this.teamId = "c974b662-4e0c-4082-af73-d2fc1f25b384"; this.playerId = "9ce1faea-ad38-4661-91de-005f2e2218a8"; this.uniformNumber = "211" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "408a37f3-74d1-4cda-826f-350507884eac"; this.uniformNumber = "1" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "bcea2564-5cca-46da-a3a1-de18c2a2d945"; this.uniformNumber = "11" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4293bdc4-6c6e-4e8b-a959-ace9e034ae5c"; this.uniformNumber = "12" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "cba9be53-73d4-4691-a9de-ac7a7b846fc5"; this.uniformNumber = "13" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "bade3237-3525-4d28-b70e-406bbe5c9911"; this.uniformNumber = "14" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4da052b7-dbc9-481a-a32a-996a1900408b"; this.uniformNumber = "15" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "ef0b31e7-cd82-4387-b50a-1875399d35d2"; this.uniformNumber = "16" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "03a8ac1b-270e-4811-82db-6dcf46cb2a59"; this.uniformNumber = "17" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "bab8c75c-2dad-4e43-9bfe-1107902659da"; this.uniformNumber = "19" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "96e00149-d65e-47c7-ab97-19652612087b"; this.uniformNumber = "20" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "b0aa6878-abe1-41ca-80a3-3e9140bac7c0"; this.uniformNumber = "21" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "7b85af6e-26fd-41dd-b56b-4c63e5808bc7"; this.uniformNumber = "22" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "bc9b09b3-c798-40a9-81ea-d41ff3285b72"; this.uniformNumber = "23" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "39622de7-069e-4aba-9f33-c91130c70b5b"; this.uniformNumber = "28" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "3afe3e1d-cc87-4baf-9b8a-92a196ccf98f"; this.uniformNumber = "31" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d2cf65b7-5022-4269-9c95-351b2deca9af"; this.uniformNumber = "32" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "7bdeb5ef-7a98-4ee2-87ae-80896698ce06"; this.uniformNumber = "39" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "06eda5bd-242e-47bb-b477-f63e407a47cf"; this.uniformNumber = "41" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "e1750735-7c40-4c95-b776-f489d3fd3d5a"; this.uniformNumber = "43" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "c87d26ab-1ffd-4838-aa05-74a2b3321f04"; this.uniformNumber = "45" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "ee5cab34-07ef-4d4c-b7c3-378328e364c8"; this.uniformNumber = "47" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d97e7bf8-6a6e-42d4-90a6-3f2b4f2f41ce"; this.uniformNumber = "49" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "36e7b821-9bc3-47ac-b4e4-7a7d7f5eecd2"; this.uniformNumber = "52" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d50c5dfc-c125-4e3f-a187-6e6d919504c1"; this.uniformNumber = "53" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "1789e491-aa67-41db-b5b0-de4e8a53fefc"; this.uniformNumber = "56" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "8bf9a54b-dad6-4486-bfbe-4fa46a7d7f74"; this.uniformNumber = "57" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4a8af660-f267-4521-a06e-e54665fed85d"; this.uniformNumber = "58" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "aae36025-cd3e-4899-a5df-93d0db8bbe08"; this.uniformNumber = "59" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "c26d5a0a-35b1-43eb-a07e-8567dc111ea8"; this.uniformNumber = "60" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "14f5139f-e97d-4cd9-9fae-3867137e05c0"; this.uniformNumber = "61" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "947ffc3c-a8fc-420a-80f7-4d7825e910bd"; this.uniformNumber = "62" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "b262f976-b67e-4994-9a35-f2879b6cce01"; this.uniformNumber = "63" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "edf9e714-c4ea-4a77-91ef-c7fda5bfa6d8"; this.uniformNumber = "68" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "2b67e23c-006f-429b-939b-25585e0f021a"; this.uniformNumber = "91" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "6f0b850d-4d82-4b3b-8042-fc74ae89810c"; this.uniformNumber = "2" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "e255a710-693b-47ac-9711-c201c5170dae"; this.uniformNumber = "27" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d124dc08-b718-4c72-8783-af5175320b0f"; this.uniformNumber = "29" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "a69eaff8-38a4-4748-bc41-9e87198b027b"; this.uniformNumber = "44" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4b951a3f-b87b-4b32-8666-fd9a1d470857"; this.uniformNumber = "65" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "051cce0d-cbb8-4889-9336-39faa58c594b"; this.uniformNumber = "67" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "b1b02b22-74b7-4a7f-83e3-a2c15e0a3f26"; this.uniformNumber = "70" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "34f1a308-701c-489f-9f67-d28a450c9270"; this.uniformNumber = "78" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "8b15f74c-f974-49b8-9897-70ef9efd01c9"; this.uniformNumber = "0" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "dcda758c-fc41-47a7-a2f2-c103d82e7037"; this.uniformNumber = "3" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "654d5bb7-b063-4704-b7e9-e43b30017246"; this.uniformNumber = "5" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "98175012-48e6-4a41-9016-0cdf3a5f45f2"; this.uniformNumber = "6" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "1410eeed-2cc7-4bc4-a73a-acdc8d9aaaf4"; this.uniformNumber = "7" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4294b8fc-55d0-4d47-b65f-32c6f13908a8"; this.uniformNumber = "24" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "44ab33f2-d1e9-4ebd-b9a6-0e5d58138161"; this.uniformNumber = "26" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "8b48f4fc-a84b-4361-a58e-6c73607244e4"; this.uniformNumber = "33" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "2880d68b-d16f-4a83-be1b-c4e6cd032a3c"; this.uniformNumber = "34" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "450caf6c-8e32-406a-838b-1f38fdf70f83"; this.uniformNumber = "36" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "629413e1-4801-47aa-8968-b1d8dde1b2a9"; this.uniformNumber = "40" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d1369ae3-f54d-4fee-9ced-e261ff2a08bc"; this.uniformNumber = "48" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "24c732b9-ef87-422b-a24a-9a47dec6ec12"; this.uniformNumber = "66" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "939fd05c-c672-4e9c-b53e-e9017b32e263"; this.uniformNumber = "4" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "e6a77b99-5051-4fac-a0fd-701538410065"; this.uniformNumber = "8" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "8ef20cf0-3281-43b7-8797-02382c358a4f"; this.uniformNumber = "25" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "c3aaa44f-000b-49a9-b803-f45e58947a60"; this.uniformNumber = "35" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "7b69cc41-4bd8-4d5d-9c14-bf07ef4eb913"; this.uniformNumber = "38" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "ca694499-20d0-4b24-bf89-2e0544a069e6"; this.uniformNumber = "46" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "535a353a-cc4f-4861-aa5b-269a196b157e"; this.uniformNumber = "51" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "bc95563b-dd3a-4eef-a13c-369df68e044c"; this.uniformNumber = "54" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "fe20c854-6608-4689-a5d7-da48a32b8172"; this.uniformNumber = "69" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "1456de85-9ab1-4b72-9359-080782661e3f"; this.uniformNumber = "71" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "f6b23232-ea18-4931-a24a-fdd8f1a6ea64"; this.uniformNumber = "97" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "8a00d815-bd99-4454-86c3-56cc8fda0931"; this.uniformNumber = "99" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "0f8413e6-d996-4dd0-952b-056769c7dcad"; this.uniformNumber = "016" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "22b8447d-5148-4511-9271-2570e6627bab"; this.uniformNumber = "017" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "dcb74dee-c49d-4984-9b5f-3c5cc2b6cfba"; this.uniformNumber = "130" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "4abf2acf-8a96-437b-8c6c-8899d688bef4"; this.uniformNumber = "131" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "1b4570d6-d278-446e-928a-23d01a14d7ee"; this.uniformNumber = "135" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "eddc93be-4b21-4ebe-ba82-7c95b628a52d"; this.uniformNumber = "136" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "55330be9-a6bb-4312-be78-2c03e9d08dfa"; this.uniformNumber = "138" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "d52220cf-1692-40ba-ae88-3985e7185c47"; this.uniformNumber = "164" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "c07450b6-a972-442c-872e-0c09decac1a1"; this.uniformNumber = "137" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "2878f38e-38fd-4d67-8889-f17b17cbf964"; this.uniformNumber = "008" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "c657164e-5219-406c-990c-a698c82197e6"; this.uniformNumber = "129" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "903b4b26-7e00-482f-b66d-d6396f2fbca3"; this.uniformNumber = "133" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "b6efdac0-d57e-4bae-b1a4-013e8a9b9192"; this.uniformNumber = "140" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "516ef726-f2fa-4a7c-bf54-7c969b613c63"; this.uniformNumber = "141" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "ddf39515-a0e1-433b-9636-4717df5b8405"; this.uniformNumber = "134" },
            RegisterData().apply { this.teamId = "3626add0-c694-42a0-9d09-8f03a75e5506"; this.playerId = "54731acc-331e-481d-bcce-4921e8ec702e"; this.uniformNumber = "150" }
       ))

        // endregion

    }
}