(ns tictactoe.ui-spec
  (:require [speclj.core :refer :all]
            [tictactoe.ui :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec])
  (:import (tictactoe.game_mode PvPGame PvCGame)))

(describe "A TicTacToe Console UI"
  (it "displays a guide"
    (should= "Pick a tile 1-9\n"
             (with-out-str (display-guide))))

  (it "displays a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (display-board utils/empty-board)))
    (should= "x _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (display-board utils-spec/first-move-board))))

  (it "displays the game over message"
    (should= "x has won!\n" (with-out-str (display-game-over-message
                                        [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\n" (with-out-str (display-game-over-message
                                            [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\n" (with-out-str (display-game-over-message
                                            [\o \o \_ \x \x \x \_ \_ \_])))
    (should= "tie!\n" (with-out-str (display-game-over-message
                                    [\o \x \x \x \o \o \o \x \x]))))

  (context "Menu Navigator"
    (it "displays the options of a menu"
      (should= "1: Versus Player\n2: Versus Computer\n"
               (with-out-str (display-options (:options game-mode-menu))))
      (should= "1: Easy\n2: Medium\n3: Hard\n"
               (with-out-str (display-options (:options (get (:options game-mode-menu) 1))))))

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
          (should= (second (:options (second (:options game-mode-menu))))
                   (choose-option (second (:options game-mode-menu))))
          (should-have-invoked :read-line))
        (should-have-invoked :println)))

    (it "evaluates a menu"
      (with-redefs [println (stub :println {:return 0})]

        (with-redefs [read-line (stub :read-line {:return "1"})]
          (should-be-a PvPGame (evaluate-menu game-mode-menu))
          (should-have-invoked :read-line))

        (with-redefs [read-line (stub :read-line {:return "2"})]
          (should-be-a PvCGame (evaluate-menu game-mode-menu))
          (should-have-invoked :read-line))
        (should-have-invoked :println)))))