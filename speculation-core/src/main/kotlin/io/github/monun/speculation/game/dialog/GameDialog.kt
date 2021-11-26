package io.github.monun.speculation.game.dialog

import io.github.monun.speculation.game.Piece
import io.github.monun.speculation.game.message.GameMessage
import io.github.monun.speculation.game.zone.ZoneProperty

abstract class GameDialog<R> {
    lateinit var piece: Piece
        private set

    lateinit var message: GameMessage
        private set

    lateinit var default: () -> R
        private set

    internal fun initialize(piece: Piece, message: GameMessage, default: () -> R) {
        this.piece = piece
        this.message = message
        this.default = default
    }
}

class GameDialogDice(val numberOfDice: Int) : GameDialog<List<Int>>()
class GameDialogAcquisition(val property: ZoneProperty) : GameDialog<Boolean>()
class GameDialogUpgrade(val property: ZoneProperty, val level: ZoneProperty.Level) : GameDialog<Boolean>()
class GameDialogSeizure() : GameDialog<List<ZoneProperty>>()