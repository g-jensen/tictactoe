(ns tictactoe.ui-spec
  (:require [speclj.core :refer :all]
            [tictactoe.ui :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec])
  (:import (tictactoe.game_mode PvPGame PvCGame)))

(describe "A TicTacToe Console UI"
  (it "displays a guide"
    (should= "Pick a tile 1-9\n"
             (with-out-str (display-guide (utils/empty-board 3)))))

  (it "displays a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (display-board (utils/empty-board 3))))
    (should= "x _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (display-board (utils-spec/first-move-board 3)))))

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
      (should= "1: File Persistence\n2: SQL Persistence\n"
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
        (should-have-invoked :println)))))