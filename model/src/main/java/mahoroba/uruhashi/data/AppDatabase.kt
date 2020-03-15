package mahoroba.uruhashi.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import mahoroba.uruhashi.data.converter.*
import mahoroba.uruhashi.data.game.*
import mahoroba.uruhashi.data.query.GameQueryServiceDao
import mahoroba.uruhashi.data.query.PlayerQueryServiceDao
import mahoroba.uruhashi.data.query.TeamQueryServiceDao

@Database(
    entities = [
        StadiumData::class,
        TeamProfileData::class,
        PlayerData::class,
        RegisterData::class,
        GameData::class,
        StartingMemberData::class,
        InningData::class,
        PlateAppearanceData::class,
        PeriodData::class,
        SubstitutionData::class,
        PlayerChangingData::class,
        PositionChangingData::class,
        PlayData::class,
        BattedBallDirectionData::class,
        FieldPlayData::class,
        FieldersActionData::class,
        RunnersActionData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    NameTypeConverter::class,
    HandTypeConverter::class,
    DateConverter::class,
    IDConverter::class,
    TeamClassConverter::class,
    PositionConverter::class,
    BattingResultConverter::class,
    PitchTypeConverter::class,
    BattingOptionConverter::class,
    StrikeTypeConverter::class,
    FoulBallDirectionConverter::class,
    BattedBallTypeConverter::class,
    BattedBallStrengthConverter::class,
    FieldPlayFactorConverter::class,
    FieldPositionConverter::class,
    FieldingRecordConverter::class,
    RunnerTypeConverter::class,
    RunnerStateConverter::class,
    GameStatusConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract val stadiumDao: StadiumDao
    abstract val teamDao: TeamDao
    abstract val playerDao: PlayerDao
    abstract val registerDao: RegisterDao
    abstract val gameDao: GameDao
    abstract val startingMemberDao: StartingMemberDao
    abstract val inningDao: InningDao
    abstract val plateAppearanceDao: PlateAppearanceDao
    abstract val periodDao: PeriodDao
    abstract val substitutionDao: SubstitutionDao
    abstract val playerChangingDao: PlayerChangingDao
    abstract val positionChangingDao: PositionChangingDao
    abstract val playDao: PlayDao
    abstract val battedBallDirectionDao: BattedBallDirectionDao
    abstract val fieldPlayDao: FieldPlayDao
    abstract val fieldersActionDao: FieldersActionDao
    abstract val runnersActionDao: RunnersActionDao

    abstract val teamQueryDao: TeamQueryServiceDao
    abstract val playerQueryDao: PlayerQueryServiceDao
    abstract val gameQueryDao: GameQueryServiceDao
}