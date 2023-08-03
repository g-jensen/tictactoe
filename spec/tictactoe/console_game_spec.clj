(ns tictactoe.console-game-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-state :as gs]
            [tictactoe.console-game :refer :all]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec])
  (:import (tictactoe.game_mode PvCGame)))

(def file-3x3-pvp
  {:state :done
   :database :file
   :load-type :new
   :board-size 3
   :board (utils/empty-board 3)
   :versus-type :pvp})

(def file-4x4-pvp
  {:state :done
   :database :file
   :load-type :new
   :board-size 4
   :board (utils/empty-board 4)
   :versus-type :pvp})

(def file-3x3-pvc
  {:state :done
   :database :file
   :load-type :new
   :board-size 3
   :board (utils/empty-board 3)
   :versus-type :pvc
   :difficulty :hard
   :character \x})

(def console-empty-board {:ui :console
                          :board (utils/empty-board 3)})

(def console-pvc-x {:ui :console
                    :gamemode (PvCGame. 3 [] :hard)
                    :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                    :character \x
                    :difficulty :hard})

(describe "A Console TicTacToe Game"
  (with-stubs)
  (it "calculates the initial state of a file-based 3x3 pvp game"
    (with-redefs [evaluate-menu (stub :evaluate-menu {:return file-3x3-pvp})
                  utils/now (stub :now {:return "the date right now"})]
      (should= (assoc file-3x3-pvp :gamemode (game-mode/->PvPGame 3 (utils/empty-board 3))
                                   :date "the date right now")
               (initial-state))))

  (it "calculates the initial state of a file-based 4x4 pvp game"
    (with-redefs [evaluate-menu (stub :evaluate-menu {:return file-4x4-pvp})
                  utils/now (stub :now {:return "the date right now"})]
      (should= (assoc file-4x4-pvp :gamemode (game-mode/->PvPGame 4 (utils/empty-board 4))
                                   :date "the date right now")
               (initial-state))))

  (it "calculates the initial state of a file-based 3x3 pvc game"
    (with-redefs [evaluate-menu (stub :evaluate-menu {:return file-3x3-pvc})
                  utils/now (stub :now {:return "the date right now"})]
      (should= (assoc file-3x3-pvc :gamemode (game-mode/->PvCGame 3 (utils/empty-board 3) :hard)
                                   :date "the date right now")
               (initial-state))))

  (it "prints a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (print-board (utils/empty-board 3))))
    (should= "x _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (print-board (utils-spec/first-move-board 3))))
    (should= (str "_ _ _\n_ _ _\n_ _ _\n\n"
                  "_ _ _\n_ _ _\n_ _ _\n\n"
                  "_ _ _\n_ _ _\n_ _ _\n\n")
             (with-out-str (print-board (repeat 3 (utils/empty-board 3))))))

  (it "prints the game over message"
    (should= "x has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\o \o \_ \x \x \x \_ \_ \_])))
    (should= "tie!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                     [\o \x \x \x \o \o \o \x \x]))))

  (it "draws the state"
    (should= "_ _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (console-draw {:board (utils/empty-board 3)})))
    (should= "x _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (console-draw {:board [\x \_ \_ \_ \_ \_ \_ \_ \_]})))
    (should= "x o x\no x o\nx _ _\n\nx has won!\n\nPlay Again:\n"
             (with-out-str (console-draw {:board [\x \o \x \o \x \o \x \_ \_]
                                          :over? true}))))

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

  (it "gets the next board"
    (with-redefs [get-user-move (stub :get-user-move {:return 0})]
      (should= (assoc console-empty-board :board [\x \_ \_ \_ \_ \_ \_ \_ \_])
               (gs/next-board console-empty-board))
      (should= (assoc console-pvc-x :board [\x \_ \_ \_ \o \_ \_ \_ \_])
               (gs/next-board console-pvc-x)))))