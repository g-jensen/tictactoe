(ns tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-state :refer :all]
            [tictactoe.utils :as utils]))

(describe "A TicTacToe Game State"

  (it "checks if it is the computer's turn"
    (should-not (computer-turn? {:versus-type :pvp}))
    (should-not (computer-turn? {:versus-type :pvc
                                 :board (utils/empty-board 3)
                                 :character \x}))
    (should (computer-turn? {:versus-type :pvc
                                 :board (utils/empty-board 3)
                                 :character \o}))
    (should-not (computer-turn? {:versus-type :pvc
                             :board [\x \o \x \o \x \o \x \_ \_]
                             :character \o})))

  (it "selects the tile to play"
    (let [s1 {:state :done :board [\_ \_ \_ \_ \_ \_ \_ \_ \_] :versus-type :pvp}
          s2 {:state :done :board [\_ \x \_ \_ \_ \_ \_ \_ \_] :versus-type :pvp}
          s3 {:state :done :board (repeat 3 [\_ \_ \_ \_ \_ \_ \_ \_ \_]) :versus-type :pvp}]
      (should= [\x \_ \_ \_ \_ \_ \_ \_ \_] (:board (next-state s1 1)))
      (should= [\o \x \_ \_ \_ \_ \_ \_ \_] (:board (next-state s2 1)))
      (should= [\_ \_ \_ \_ \_ \_ \_ \_ \_] (:board (next-state s1 10)))
      (should= [\_ \x \_ \_ \_ \_ \_ \_ \_] (:board (next-state s2 2)))
      (should= [[\_ \_ \_ \_ \x \_ \_ \_ \_] [\_ \_ \_ \_ \_ \_ \_ \_ \_] [\_ \_ \_ \_ \_ \_ \_ \_ \_]]
               (:board (next-state s3 5)))))

  (it "plays computer move"
    (let [s1 {:state :done
              :board [\_ \_ \_ \_ \_ \_ \_ \_ \_]
              :difficulty :hard
              :versus-type :pvc}]
      (should= [\x \_ \_ \_ \o \_ \_ \_ \_] (:board (next-state s1 1)))))

  (it "sets game to over"
    (let [s1 {:state :done
              :board [\_ \x \x \o \o \_ \_ \_ \_]
              :versus-type :pvp}]
      (should= [\x \x \x \o \o \_ \_ \_ \_] (:board (next-state s1 1)))
      (should (:over? (next-state s1 1)))))

  (with-stubs)
  (it "saves game to db"
    (with-redefs [db-initialize (stub :initialize {:return nil})
                  db-save-game (stub :save-game {:return nil})]
      (let [s1 {:state :done
                :database :file
                :board [\_ \_ \x \o \o \x \_ \_ \_]
                :versus-type :pvp}]
        (next-state s1 1)
        (should-have-invoked :save-game))))

  (it "deletes game from db when over"
    (with-redefs [db-initialize (stub :initialize {:return nil})
                  db-save-game (stub :save-game {:return nil})
                  db-delete-game (stub :delete-game {:return nil})]
      (let [s1 {:state :done
                :database :file
                :board [\_ \x \x \o \o \_ \_ \_ \_]
                :versus-type :pvp}]
        (next-state s1 1)
        (should-have-invoked :delete-game)))))