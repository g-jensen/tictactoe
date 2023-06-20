(ns tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.game-state :refer :all]
            [tictactoe.ui :as ui]
            [tictactoe.utils :as utils]))

(def pvp-game-3x3 (game-mode/->PvPGame 3 (utils/empty-board 3)))
(def menu-eval-pvp-3x3 {:game-mode pvp-game-3x3
                        :old-date "Tue Jun 20 17:05:25 EDT 2023"})

(describe "A TicTacToe Game State"

  (with-stubs)
  (it "gets the initial game state"
    (with-redefs [ui/evaluate-menu (stub :evaluate-menu {:return menu-eval-pvp-3x3})
                  utils/now (stub :now {:return "Tue Jun 20 17:05:30 EDT 2023"})]
      (should= {:game-mode pvp-game-3x3
                :old-date "Tue Jun 20 17:05:25 EDT 2023"
                :date "Tue Jun 20 17:05:30 EDT 2023"
                :board (utils/empty-board 3)}
               (initial-state)))))