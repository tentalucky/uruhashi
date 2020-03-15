package mahoroba.uruhashi.di

import android.app.Application
import mahoroba.uruhashi.domain.game.IGameExporter
import mahoroba.uruhashi.exStorage.writer.GameExporter

object ImportExportServicePresenter {
    fun getGameExporter(application: Application): IGameExporter =
        GameExporter(application)
}