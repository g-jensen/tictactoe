(ns tictactoe.board)

(def empty-tile \_)
(def empty-board (vec (repeat 9 empty-tile)))
(def first-move-board (vec (cons \x (repeat 8 empty-tile))))