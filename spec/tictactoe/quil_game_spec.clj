(ns tictactoe.quil-game-spec
  (:require [speclj.core :refer :all]
            [tictactoe.quil-game :refer :all]
            [tictactoe.utils :as utils]))

(describe "A Quil TicTacToe Game"

  (it "checks if a point is in a rectangle"
    (should (point-in-rect? [0 0] [0 0 1 1]))
    (should-not (point-in-rect? [-1 -1] [0 0 1 1]))
    (should (point-in-rect? [1 0] [0 0 1 1]))
    (should (point-in-rect? [0 -1] [0 0 1 1]))
    (should (point-in-rect? [1 -1] [0 0 1 1])))

  (it "converts a board index to coordinates"
    (should= [50 100] (board-index-to-coords 0 20 50))
    (should= [50 100] (board-index-to-coords 0 10 50))
    (should= [50 150] (board-index-to-coords 1 20 50))
    (should= [50 200] (board-index-to-coords 2 20 50))
    (should= [50 250] (board-index-to-coords 3 10 50)))

  (it "clicks a button"
    (should= :database (:state (update-ui {:state :database} {:x 0 :y 0})))
    (should= :load-type (:state (update-ui {:state :database} {:x 5 :y 60})))
    (should= :load-type (:state (update-ui {:state :database} {:x 5 :y 80})))
    (should= :database (:state (update-ui {:state :database} {:x 5 :y 100}))))

  (it "clicks a tile on a board"
    (should= (utils/empty-board 3)
             (:board (click-tile {:board (utils/empty-board 3)} {:x 0 :y 0})))
    (should= [\x \_ \_ \_ \_ \_ \_ \_ \_]
             (:board (click-tile {:board (utils/empty-board 3)} {:x 50 :y 100})))
    (should= [\_ \x \_ \_ \_ \_ \_ \_ \_]
             (:board (click-tile {:board (utils/empty-board 3)} {:x 50 :y 150})))))