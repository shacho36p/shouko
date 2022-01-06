package xyz.ivaniskandar.shouko.service

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import xyz.ivaniskandar.shouko.activity.TileBoardingActivity
import xyz.ivaniskandar.shouko.service.TadanoTileParentService.Companion.ACTION_START_SERVICE
import xyz.ivaniskandar.shouko.service.TadanoTileParentService.Companion.ACTION_STOP_SERVICE
import xyz.ivaniskandar.shouko.service.TadanoTileParentService.Companion.EXTRA_SERVICE_TYPE
import xyz.ivaniskandar.shouko.util.Prefs

class CoffeeTileService : TileService() {

    override fun onBind(intent: Intent?): IBinder? {
        requestListeningState(this, ComponentName(this, CoffeeTileService::class.java))
        return super.onBind(intent)
    }

    override fun onStartListening() {
        super.onStartListening()
        updateQsState()
    }

    override fun onClick() {
        super.onClick()
        with(qsTile) {
            when (state) {
                STATE_ACTIVE -> {
                    switchService(false)
                }
                STATE_INACTIVE -> {
                    switchService(true)
                }
            }
        }
    }

    private fun updateQsState() {
        with(qsTile) {
            state = if (TadanoTileParentService.isCoffeeActive) {
                STATE_ACTIVE
            } else {
                STATE_INACTIVE
            }
            updateTile()
        }
    }

    private fun switchService(start: Boolean) {
        val serviceIntent = Intent(this, TadanoTileParentService::class.java)
        if (start) {
            val prefs = Prefs(this)
            if (!prefs.coffeeBoardingDone) {
                startActivityAndCollapse(
                    Intent(this, TileBoardingActivity::class.java).apply {
                        putExtra(EXTRA_SERVICE_TYPE, TadanoTileParentService.Type.COFFEE)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                prefs.coffeeBoardingDone = true
            }
            startForegroundService(
                serviceIntent.apply {
                    action = ACTION_START_SERVICE
                    putExtra(EXTRA_SERVICE_TYPE, TadanoTileParentService.Type.COFFEE)
                }
            )
        } else {
            startService(
                serviceIntent.apply {
                    action = ACTION_STOP_SERVICE
                }
            )
        }
    }
}
