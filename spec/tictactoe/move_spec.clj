(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.game-state :as game-state]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec]))

(defn every-computer-move [board]
  (cond
    (game-state/game-over? board)
      [board]
    (= \x (player-to-move board))
      (cons board (every-computer-move (play-move board (get-computer-move :hard board))))
    :else
      (apply concat (map #(every-computer-move (play-move board %)) (utils/empty-indices board)))))


(describe "A TicTacToe Mover"

  (it "gets whose move it is"
    (should= \x (player-to-move (utils/empty-board 3)))
    (should= \o (player-to-move (utils-spec/first-move-board 3)))
    (should= \x (player-to-move [\x \o \_ \_ \_ \_ \_ \_ \_])))

  (it "checks if a move is valid"
    (should (move-valid? (utils/empty-board 3) 0))
    (should-not (move-valid? (utils/empty-board 3) -1))
    (should-not (move-valid? (utils/empty-board 3) 9))
    (should-not (move-valid? (utils-spec/first-move-board 3) 0))
    (should (move-valid? (utils/empty-board 4) 0))
    (should-not (move-valid? (utils/empty-board 4) -1))
    (should-not (move-valid? (utils/empty-board 4) 16))
    (should-not (move-valid? (utils-spec/first-move-board 4) 0)))

  (it "plays a move on a board"
    (should= (utils/empty-board 3) (play-move (utils/empty-board 3) -1))
    (should= (utils/empty-board 3) (play-move (utils/empty-board 3) 9))
    (should= (utils-spec/first-move-board 3) (play-move (utils-spec/first-move-board 3) 0))
    (should= (utils-spec/first-move-board 3) (play-move (utils/empty-board 3) 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move (utils-spec/first-move-board 3) 1)))

  (with-stubs)
  (it "gets the user's next move"
    (with-redefs [read-line (stub :read-line {:return "1"})]
      (should= 0 (get-user-move (utils/empty-board 3)))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "2"})]
      (should= 1 (get-user-move (utils/empty-board 3)))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "0"})]
      (should= -1 (get-user-move (utils/empty-board 3)))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "10"})]
      (should= -1 (get-user-move (utils/empty-board 3)))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "g"})]
      (should= -1 (get-user-move (utils/empty-board 3)))
      (should-have-invoked :read-line)))

  (context "A Computer"
    (context "Hard Mode"

      (it "weighs the value of a move"
        (should= 10 (move-weight [\_ \x \x \o \o \_ \_ \_ \_] 0 5))
        (should= -10 (move-weight [\_ \o \o \x \x \_ \x \_ \_] 0 5))
        (should= 0 (move-weight [\_ \o \x \x \o \o \o \x \x] 0 5))
        (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 7 5))
        (should= 0 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 4 5))
        (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \o \x] 4 5))
        (should= -10 (move-weight [\_ \o \o \_ \_ \x \x \_ \x] 0 5))
        (should= 0 (move-weight (utils/empty-board 3) 8 5)))

      (it "picks a corner given an empty board"
        (should= 8 (get-computer-move :hard (utils/empty-board 3)))
        (should= 15 (get-computer-move :hard (utils/empty-board 4))))

      (it "wins given the chance"
        (should= 6 (get-computer-move :hard [\_ \_ \_ \_ \o \o \_ \x \x]))
        (should= 2 (get-computer-move :hard [\_ \_ \_ \_ \o \x \_ \o \x]))
        (should= 0 (get-computer-move :hard [\_ \_ \_ \o \x \_ \o \_ \x]))
        (should= 3 (get-computer-move :hard [\_ \_ \_ \_ \x \x \x \o \_]))
        (should= 0 (get-computer-move :hard [\_ \x \x \x \_ \_ \_ \_ \_ \_ \_ \_ \_ \o \o \o])))

      (it "blocks the other player from winning"
        (should= 2 (get-computer-move :hard [\_ \_ \_ \_ \o \_ \o \x \x]))
        (should= 12 (get-computer-move :hard [\x \x \_ \_ \x \_ \_ \_ \_ \_ \_ \_ \_ \o \o \o]))))

    (with-stubs)
    (context "Medium Mode"

      (it "has a 70% chance to play the Hard Mode move"
        (with-redefs [rand (stub :rand {:return 0.6})]
          (should= (get-computer-move :hard (utils/empty-board 3))
                   (get-computer-move :medium (utils/empty-board 3)))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.69})]
          (should= (get-computer-move :hard (utils/empty-board 3))
                   (get-computer-move :medium (utils/empty-board 3)))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.3})]
          (should= (get-computer-move :hard (utils/empty-board 3))
                   (get-computer-move :medium (utils/empty-board 3)))
          (should-have-invoked :rand)))

      (it "has a 30% chance to pick the first empty tile on the board"
        (with-redefs [rand (stub :rand {:return 0.8})]
          (should= 0
                   (get-computer-move :medium (utils/empty-board 3)))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.7})]
          (should= 2
                   (get-computer-move :medium [\x \x \_ \_ \_ \_ \_ \_ \_]))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.9})]
          (should= 3
                   (get-computer-move :medium [\x \x \x \_ \x \_ \_ \_ \_]))
          (should-have-invoked :rand))))

    (context "Easy Mode"
      (it "has a 20% chance to pick the Hard Mode move"
        (with-redefs [rand (stub :rand {:return 0.1})]
          (should= (get-computer-move :hard (utils/empty-board 3))
                   (get-computer-move :easy (utils/empty-board 3)))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.19})]
          (should= (get-computer-move :hard (utils/empty-board 3))
                   (get-computer-move :easy (utils/empty-board 3)))
          (should-have-invoked :rand)))

      (it "has a 80% chance to pick the first empty tile on the board"
        (with-redefs [rand (stub :rand {:return 0.3})]
          (should= 0
                   (get-computer-move :easy (utils/empty-board 3)))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.6})]
          (should= 2
                   (get-computer-move :easy [\x \x \_ \_ \_ \_ \_ \_ \_]))
          (should-have-invoked :rand))

        (with-redefs [rand (stub :rand {:return 0.9})]
          (should= 3
                   (get-computer-move :easy [\x \x \x \_ \x \_ \_ \_ \_]))
          (should-have-invoked :rand))))))