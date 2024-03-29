(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.board-state :as board-state]
            [tictactoe.move :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec]))


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
    (should-not (move-valid? (utils-spec/first-move-board 4) 0))
    (should (move-valid? (repeat 3 (utils/empty-board 3)) 0))
    (should-not (move-valid? (repeat 3 (utils/empty-board 3)) 28)))

  (it "plays a move on a board"
    (should= (utils/empty-board 3) (play-move (utils/empty-board 3) -1))
    (should= (utils/empty-board 3) (play-move (utils/empty-board 3) 9))
    (should= (utils-spec/first-move-board 3) (play-move (utils-spec/first-move-board 3) 0))
    (should= (utils-spec/first-move-board 3) (play-move (utils/empty-board 3) 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move (utils-spec/first-move-board 3) 1))
    (should= [[\x \_ \_ \_ \_ \_ \_ \_ \_]
              [\_ \_ \_ \_ \_ \_ \_ \_ \_]
              [\_ \_ \_ \_ \_ \_ \_ \_ \_]]
             (play-move (repeat 3 (utils/empty-board 3)) 0)))

  (context "A Computer"

    (context "Hard Mode"

      (context "plays the winning set of moves for 3x3x3"
        (it "first move is the middle of the cube"
          (should= 13 (get-3d-move (repeat 3 (utils/empty-board 3)))))

        (it "second move does not allow opponent to get 2 in a row"
          (should= 4 (get-3d-move [[\o \_ \_ \_ \_ \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   (utils/empty-board 3)]))
          (should= 3 (get-3d-move [[\_ \_ \_ \_ \o \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   (utils/empty-board 3)]))
          (should= 22 (get-3d-move [(utils/empty-board 3)
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   [\o \_ \_ \_ \_ \_ \_ \_ \_]]))
          (should= 21 (get-3d-move [(utils/empty-board 3)
                                    [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                    [\_ \_ \_ \_ \o \_ \_ \_ \_]]))
          (should= 22 (get-3d-move [(utils/empty-board 3)
                                   [\_ \o \_ \_ \x \_ \_ \_ \_]
                                   (utils/empty-board 3)])))

        (it "third move wins given the chance"
          (should= 12 (get-3d-move [[\o \o \_ \_ \_ \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \x \x \_ \_ \_]
                                   (utils/empty-board 3)]))
          (should= 22 (get-3d-move [[\_ \o \_ \_ \x \_ \_ \_ \_]
                                    [\_ \_ \o \_ \x \_ \_ \_ \_]
                                    (utils/empty-board 3)])))

        (it "third move forks opponent"
          (should= 0 (get-3d-move [[\_ \o \_ \_ \x \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \o \_ \_ \_ \_]]))
          (should= 6 (get-3d-move [[\o \_ \_ \_ \x \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \o \_ \_ \_ \_]]))
          (should= 18 (get-3d-move [[\_ \_ \_ \_ \o \_ \_ \_ \_]
                                    [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                    [\_ \o \_ \_ \x \_ \_ \_ \_]]))
          (should= 24 (get-3d-move [[\_ \_ \_ \_ \o \_ \_ \_ \_]
                                    [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                    [\o \_ \_ \_ \x \_ \_ \_ \_]])))

        (it "fourth move wins"
          (should= 26 (get-3d-move [[\x \o \_ \_ \x \_ \_ \_ \o]
                                   [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                   [\_ \_ \_ \_ \o \_ \_ \_ \_]]))
          (should= 8 (get-3d-move [[\x \o \_ \_ \x \_ \_ \_ \_]
                                    [\_ \_ \_ \_ \x \_ \_ \_ \_]
                                    [\_ \_ \_ \_ \o \_ \_ \_ \o]]))))

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