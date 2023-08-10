(ns clj.tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [clj.tictactoe.game-state :refer :all]
            [clj.tictactoe.utils :as utils])
  (:import (clj.tictactoe.game_mode PvCGame PvPGame)))

(describe "A TicTacToe Game State"

  (it "initializes a gamemode"
    (should= (PvPGame. 3 (utils/empty-board 3))
             (init-gamemode {:versus-type :pvp :board-size 3 :board (utils/empty-board 3)}))
    (should= (PvPGame. 4 (utils/empty-board 4))
             (init-gamemode {:versus-type :pvp :board-size 4 :board (utils/empty-board 4)}))
    (should= (PvCGame. 3 (utils/empty-board 3) :hard)
             (init-gamemode {:versus-type :pvc
                                :board-size 3
                                :difficulty :hard
                                :board (utils/empty-board 3)}))
    (should= (PvCGame. 3 (utils/empty-board 3) :medium)
             (init-gamemode {:versus-type :pvc
                                :board-size 3
                                :difficulty :medium
                                :board (utils/empty-board 3)}))
    (should= (PvCGame. 3 (utils/empty-board 3) :easy)
             (init-gamemode {:versus-type :pvc
                                :board-size 3
                                :difficulty :easy
                                :board (utils/empty-board 3)}))
    (should= (PvCGame. 4 (utils/empty-board 4) :hard)
             (init-gamemode {:versus-type :pvc
                                :board-size 4
                                :difficulty :hard
                                :board (utils/empty-board 4)})))

  (it "checks if it is the computer's turn"
    (should-not (computer-turn? {:gamemode (PvPGame. 3 (utils/empty-board 3))}))
    (should-not (computer-turn? {:gamemode (PvCGame. 3 (utils/empty-board 3) :hard)
                                 :board (utils/empty-board 3)
                                 :character \x}))
    (should (computer-turn? {:gamemode (PvCGame. 3 (utils/empty-board 3) :hard)
                                 :board (utils/empty-board 3)
                                 :character \o}))
    (should-not (computer-turn? {:gamemode (PvCGame. 3 (utils/empty-board 3) :hard)
                             :board [\x \o \x \o \x \o \x \_ \_]
                             :character \o}))))