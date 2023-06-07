(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.board :as board]
            [tictactoe.game-state :as game-state]))

(defn unbeatable-computer? [board]
  (should-not (o-wins? board))
  (cond
    (game-state/game-over? board) nil
    (=  \x (player-to-move board)) (unbeatable-computer? (play-move board (get-computer-move board)))
    :else (doall (map #(unbeatable-computer? (play-move board %)) (empty-indices board)))))

(describe "A TicTacToe Mover"
  (it "gets whose move it is"
    (should= \x (player-to-move board/empty-board))
    (should= \o (player-to-move board/first-move-board))
    (should= \x (player-to-move [\x \o \_ \_ \_ \_ \_ \_ \_])))

  (it "checks if a move is valid"
    (should (move-valid? board/empty-board 0))
    (should-not (move-valid? board/empty-board -1))
    (should-not (move-valid? board/empty-board 9))
    (should-not (move-valid? board/first-move-board 0)))

  (it "plays a move on a board"
    (should= board/empty-board (play-move board/empty-board -1))
    (should= board/empty-board (play-move board/empty-board 9))
    (should= board/first-move-board (play-move board/first-move-board 0))
    (should= board/first-move-board (play-move board/empty-board 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move board/first-move-board 1)))

  (for [board [[\o \o \o \_ \_] [] ]]
    (it (str "has a computer that always wins when it can " board)
    #_(unbeatable-computer? empty-board)
    (should true)#_(should (unbeatable-computer? board)))))