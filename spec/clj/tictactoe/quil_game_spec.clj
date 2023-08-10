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

  (it "adds points"
    (should= [0 0] (add-points [0 0] [0 0]))
    (should= [1 0] (add-points [1 0] [0 0]))
    (should= [1 0] (add-points [0 0] [1 0]))
    (should= [0 1] (add-points [0 0] [0 1]))
    (should= [4 6] (add-points [1 2] [3 4])))

  (it "subtracts points"
    (should= [0 0] (sub-points [0 0] [0 0]))
    (should= [1 0] (sub-points [1 0] [0 0]))
    (should= [-1 0] (sub-points [0 0] [1 0]))
    (should= [0 -1] (sub-points [0 0] [0 1]))
    (should= [-2 -2] (sub-points [1 2] [3 4])))

  (it "converts a board index to coordinates"
    (should= [50 100] (board-index-to-coords 0 20 50))
    (should= [50 100] (board-index-to-coords 0 10 50))
    (should= [50 150] (board-index-to-coords 1 20 50))
    (should= [50 200] (board-index-to-coords 2 20 50))
    (should= [50 250] (board-index-to-coords 3 10 50)))

  (context "updates the ui state"

    (it "for a counter"
      (should= {:state :board-size, :counter-val 3}
               (update-ui {:state :board-size} {:x 0 :y 0}))
      (should= {:state :versus-type, :counter-val 3, :board-size 3, :board [\_ \_ \_ \_ \_ \_ \_ \_ \_]}
               (update-ui {:state :board-size, :counter-val 3} {:x 5 :y 85}))
      (should= {:state :board-size, :counter-val 4}
               (update-ui {:state :board-size, :counter-val 3} {:x 36 :y 59}))
      (should= {:state :board-size, :counter-val 3}
               (update-ui {:state :board-size, :counter-val 3} {:x 6 :y 59}))
      (should= {:state :board-size, :counter-val 3}
               (update-ui {:state :board-size, :counter-val 4} {:x 6 :y 59})))

    (it "for a menu"
      (should= {:state :database}
               (update-ui {:state :database} {:x 0 :y 0}))
      (should= {:state :database}
               (update-ui {:state :database} {:x 5 :y 85})))

    (it "for a board"
      (should= {:state :done}
               (update-ui {:state :done} {:x 0 :y 0}))
      (should= {:state :done}
               (update-ui {:state :done} {:x 36 :y 59}))))

  (context "gets the input of a click"

    (it "for a menu"
      (should= 1 (get-input {:state :database} {:x 5 :y 40}))
      (should= 2 (get-input {:state :database} {:x 5 :y 61}))
      (should-be-nil (get-input {:state :database} {:x 0 :y 0})))

    (it "for a counter"
      (should= 3 (get-input {:state :board-size :counter-val 3} {:x 5 :y 85}))
      (should-be-nil (get-input {:state :board-size :counter-val 3} {:x 0 :y 0})))

    (it "for a board"
      (should= 1 (get-input {:state :done :board (utils/empty-board 3)} {:x 50 :y 50}))
      (should= 2 (get-input {:state :done :board (utils/empty-board 3)} {:x 50 :y 101}))
      (should= 4 (get-input {:state :done :board (utils/empty-board 3)} {:x 101 :y 50}))
      (should-be-nil (get-input {:state :done} {:x 0 :y 0})))))