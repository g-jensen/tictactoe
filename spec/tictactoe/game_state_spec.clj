(ns tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.game-state :refer :all]
            [tictactoe.ui :as ui]
            [tictactoe.utils :as utils]
            [tictactoe.database :as database]))

(def pvp-game-3x3 (game-mode/->PvPGame 3 (utils/empty-board 3)))
(def menu-eval-pvp-3x3 {:gamemode pvp-game-3x3
                        :database (database/->FileDatabase "games.txt")
                        :old-date "Tue Jun 20 17:05:25 EDT 2023"})

(def init-state-pvp-3x3 {:gamemode pvp-game-3x3
                         :old-date "Tue Jun 20 17:05:25 EDT 2023"
                         :database (database/->FileDatabase "games.txt")
                         :date "Tue Jun 20 17:05:30 EDT 2023"
                         :board (utils/empty-board 3)})

(describe "A TicTacToe Game State"

  (with-stubs)
  (it "gets the initial game state"
    (with-redefs [ui/evaluate-menu (stub :evaluate-menu {:return menu-eval-pvp-3x3})
                  utils/now (stub :now {:return "Tue Jun 20 17:05:30 EDT 2023"})]
      (should= init-state-pvp-3x3
               (initial-state))))

  (it "gets the updated state"
    (with-redefs [read-line (stub :read-line {:return "1"})
                  println (stub :println {:return 0})
                  database/update-game (stub :update-game {:return 0})]
      (should= {:gamemode pvp-game-3x3
                :old-date "Tue Jun 20 17:05:25 EDT 2023"
                :date "Tue Jun 20 17:05:30 EDT 2023"
                :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                :database (database/->FileDatabase "games.txt")}
               (update-state init-state-pvp-3x3))))

  (it "checks if the game-state is over"
    (should-not (over? init-state-pvp-3x3))
    (should (over? {:gamemode pvp-game-3x3
                    :old-date "Tue Jun 20 17:05:25 EDT 2023"
                    :date "Tue Jun 20 17:05:30 EDT 2023"
                    :board [\x \x \x \o \o \_ \_ \_ \_]}))))