(ns tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-state :refer :all]
            [tictactoe.utils :as utils])
  (:import (tictactoe.game_mode PvCGame PvPGame)))

(def evaluated-menu {:state :done
                      :versus-type :pvp
                      :board-size 3
                      :board (utils/empty-board 3)})

(def computer-turn-state {:state :done
                          :versus-type :pvc
                          :difficulty :hard
                          :ui :console
                          :character \o
                          :board-size 3
                          :board (utils/empty-board 3)
                          :gamemode (PvCGame. 3 (utils/empty-board 3) :hard)})

(def game-over-state {:state :done
                      :versus-type :pvc
                      :difficulty :hard
                      :ui :console
                      :character \o
                      :board-size 3
                      :board [\x \o \x \o \x \o \x \_ \_]
                      :gamemode (PvCGame. 3 (utils/empty-board 3) :hard)})

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