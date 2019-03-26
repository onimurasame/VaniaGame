package com.onimurasame.vania.rule

import com.onimurasame.vania.entity.Player

fun Player.isIdle(): Boolean = this.state == Player.State.IDLE
fun Player.isCrouching(): Boolean = this.state == Player.State.CROUCHING
fun Player.wasCrouching(): Boolean = this.state == Player.State.CROUCHING_ATTACK