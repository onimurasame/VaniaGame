package com.onimurasame.vania.rule

import com.onimurasame.vania.entity.Player

inline fun Player.isIdle(): Boolean = this.state == Player.State.IDLE
inline fun Player.isCrouching(): Boolean = this.state == Player.State.CROUCHING
