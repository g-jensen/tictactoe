(ns tictactoe.menu-spec
  (:require [speclj.core :refer :all]
            [tictactoe.menu :refer :all])
  (:import (tictactoe.game_mode PvPGame)))

;TODO - make more specific like "displays the UI options"
(describe "A TicTacToe Menu"
  (it "displays the options of a menu"
    (should= "1: Console UI\n2: Quil UI\n"
             (with-out-str (display-options (:options game-mode-menu)))))

  (with-stubs)
  (it "chooses an option from a menu"
    (with-redefs [println (stub :println {:return 0})]

      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should= (first (:options game-mode-menu))
                 (choose-option game-mode-menu))
        (should= (first (:options (second (:options game-mode-menu))))
                 (choose-option (second (:options game-mode-menu))))
        (should-have-invoked :read-line))

      (with-redefs [read-line (stub :read-line {:return "2"})]
        (should= (second (:options game-mode-menu))
                 (choose-option game-mode-menu))
        (should-have-invoked :read-line))
      (should-have-invoked :println)))

  (it "evaluates a menu"
    (with-redefs [println (stub :println {:return 0})]

      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should-be-a PvPGame (:gamemode (evaluate-menu game-mode-menu)))
        (should-have-invoked :read-line))
      (should-have-invoked :println))))