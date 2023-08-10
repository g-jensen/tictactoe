(ns clj.tictactoe.console-game-spec
  (:require [speclj.core :refer :all]
            [clj.tictactoe.console-game :refer :all]
            [clj.tictactoe.utils :as utils]
            [clj.tictactoe.utils-spec :as utils-spec]))

(describe "A TicTacToe Console Game"

  (context "converts a board to a string"

    (it "for a 2d board"
      (should= "_ _ _\n_ _ _\n_ _ _"
               (board->str (utils/empty-board 3)))
      (should= "x _ _\n_ _ _\n_ _ _"
               (board->str (utils-spec/first-move-board 3)))
      (should= "_ _ _ _\n_ _ _ _\n_ _ _ _\n_ _ _ _"
               (board->str (utils/empty-board 4)))
      (should= "x _ _ _\n_ _ _ _\n_ _ _ _\n_ _ _ _"
               (board->str (utils-spec/first-move-board 4))))
    (it "for a 3d board"
      (should= "_ _ _\n_ _ _\n_ _ _\n\n_ _ _\n_ _ _\n_ _ _\n\n_ _ _\n_ _ _\n_ _ _\n\n"
               (board->str (repeat 3 (utils/empty-board 3))))))

  (context "converts a state to a string"

    (it "for a menu"
      (should= "Database\n1. File Database\n2. SQL Database"
               (state->str {:state :database}))
      (should= "Load Type\n1. New Game\n2. Load Game"
               (state->str {:state :load-type})))

    (it "for a counter"
      (should= "Enter Board Size" (state->str {:state :board-size})))

    (it "for a board"
      (should= "_ _ _\n_ _ _\n_ _ _"
               (state->str {:state :done :board (utils/empty-board 3)}))
      (should= "_ _ _\n_ _ _\n_ _ _\n\n_ _ _\n_ _ _\n_ _ _\n\n_ _ _\n_ _ _\n_ _ _\n\n"
               (state->str {:state :done :board (repeat 3 (utils/empty-board 3))})))

    (it "for a won game"
      (should= "x x x\no o _\n_ _ _\nx has won!\nPlay Again:"
               (state->str {:state :done :board [\x \x \x \o \o \_ \_ \_ \_] :over? true})))

    (it "for a tied game"
      (should= "x o x\no x x\no x o\ntie!\nPlay Again:"
               (state->str {:state :done :board [\x \o \x \o \x \x \o \x \o] :over? true})))))