package com.onimurasame.vania.controller

import com.onimurasame.vania.entity.Player

class PlayerController {

    var player = Player()
    var inputController = PlayerInputController(player)

}
