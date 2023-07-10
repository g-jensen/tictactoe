(ns tictactoe.console-game-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-state :as gs]
            [tictactoe.console-game :refer :all]
            [tictactoe.database]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec]))

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

(describe "A Console TicTacToe Game"

  (it "initializes a gamemode"
    (should= (game-mode/->PvPGame 3 (utils/empty-board 3))
             (gs/init-gamemode {:versus-type :pvp :board-size 3 :board (utils/empty-board 3)}))
    (should= (game-mode/->PvPGame 4 (utils/empty-board 4))
             (gs/init-gamemode {:versus-type :pvp :board-size 4 :board (utils/empty-board 4)}))
    (should= (game-mode/->PvCGame 3 (utils/empty-board 3) :hard)
             (gs/init-gamemode {:versus-type :pvc
                             :board-size 3
                             :difficulty :hard
                             :board (utils/empty-board 3)}))
    (should= (game-mode/->PvCGame 3 (utils/empty-board 3) :medium)
             (gs/init-gamemode {:versus-type :pvc
                             :board-size 3
                             :difficulty :medium
                             :board (utils/empty-board 3)}))
    (should= (game-mode/->PvCGame 3 (utils/empty-board 3) :easy)
             (gs/init-gamemode {:versus-type :pvc
                             :board-size 3
                             :difficulty :easy
                             :board (utils/empty-board 3)}))
    (should= (game-mode/->PvCGame 4 (utils/empty-board 4) :hard)
             (gs/init-gamemode {:versus-type :pvc
                             :board-size 4
                             :difficulty :hard
                             :board (utils/empty-board 4)})))

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
             (with-out-str (print-board (utils-spec/first-move-board 3)))))

  (it "prints the game over message"
    (should= "x has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                           [\o \o \_ \x \x \x \_ \_ \_])))
    (should= "tie!\n\nPlay Again:\n" (with-out-str (print-game-over-message
                                                     [\o \x \x \x \o \o \o \x \x])))))