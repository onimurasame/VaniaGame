package com.onimurasame.vania.entity

import com.onimurasame.vania.util.ext.logger

class Player {

    companion object {
        @JvmStatic
        private val log = logger<Player>()

        const val IDLE_ANIMATION_UNARMED = "adventurer-idle-0"
        const val IDLE_ANIMATION_ARMED = "adventurer-idle-2"
        const val IDLE_ANIMATION_CROUCH = "adventurer-crouch-0"
        const val ATTACK_ANIMATION_UNARMED = "adventurer-attack1"
    }

    var x: Float = 0f
    var y: Float = 0f

    var state: State = State.IDLE
        set(value) {
            log.debug("Changing from $field to $value")
            field = value
            log.debug("Now $field")
        }


    enum class State {
        IDLE,
        STANDING,
        ATTACKING,
        MOVING,
        JUMPING,
        CROUCHING
    }

}