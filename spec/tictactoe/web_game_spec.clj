(ns tictactoe.web-game-spec
  (:require [speclj.core :refer :all])
  (:require [tictactoe.utils :as utils]
            [tictactoe.web-game :refer :all])
  (:import (org.httpserver HttpMessage)))

(def database-html (str "Database"
                        "<form action=\"/tictactoe\" method=\"post\">"
                        "<button type=\"submit\" name=\"choice\" value=\"1\">1. File Database</button>"
                        "<button type=\"submit\" name=\"choice\" value=\"2\">2. SQL Database</button>"
                        "</form>"))

(def load-type-html (str "Load Type"
                         "<form action=\"/tictactoe\" method=\"post\">"
                         "<button type=\"submit\" name=\"choice\" value=\"1\">1. New Game</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"2\">2. Load Game</button>"
                         "</form>"))

(def board-size-html (str "Enter Board Size"
                          "<form action=\"/tictactoe\" method=\"post\">"
                          "<input type=\"number\" id=\"choice\" name=\"choice\" value=\"3\">"
                          "<input type=\"submit\" value=\"Submit\">"
                          "</form>"))

(def versus-type-html (str "Versus Type"
                           "<form action=\"/tictactoe\" method=\"post\">"
                           "<button type=\"submit\" name=\"choice\" value=\"1\">1. Versus Player</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"2\">2. Versus Computer</button>"
                           "</form>"))

(def empty-3x3-html (str "Board"
                         "<form action=\"/tictactoe\" method=\"post\">"
                         "<button type=\"submit\" name=\"choice\" value=\"1\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"2\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"3\">_</button><br>"
                         "<button type=\"submit\" name=\"choice\" value=\"4\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"5\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"6\">_</button><br>"
                         "<button type=\"submit\" name=\"choice\" value=\"7\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"8\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"9\">_</button><br>"
                         "</form>"))

(def over-3x3-html (str "Board"
                         "<form action=\"/tictactoe\" method=\"post\">"
                         "<button type=\"submit\" name=\"choice\" value=\"1\">x</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"2\">x</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"3\">x</button><br>"
                         "<button type=\"submit\" name=\"choice\" value=\"4\">o</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"5\">o</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"6\">_</button><br>"
                         "<button type=\"submit\" name=\"choice\" value=\"7\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"8\">_</button>"
                         "<button type=\"submit\" name=\"choice\" value=\"9\">_</button><br>"
                        "<button type=\"submit\" name=\"choice\" value=\"1\">Play Again</button>"
                        "</form>"))

(def empty-3x3x3-html (str "Board"
                           "<form action=\"/tictactoe\" method=\"post\">"
                           "<button type=\"submit\" name=\"choice\" value=\"1\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"2\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"3\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"4\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"5\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"6\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"7\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"8\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"9\">_</button><br><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"10\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"11\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"12\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"13\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"14\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"15\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"16\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"17\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"18\">_</button><br><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"19\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"20\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"21\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"22\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"23\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"24\">_</button><br>"
                           "<button type=\"submit\" name=\"choice\" value=\"25\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"26\">_</button>"
                           "<button type=\"submit\" name=\"choice\" value=\"27\">_</button><br><br>"
                           "</form>"))

(describe "A TicTacToe Web Game"

  (it "gets the choice of a request"
    (let [r1 (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                   (.putHeader "Content-Length" "8")
                                   (.setBody "choice=1"))
          r2 (doto (HttpMessage.) (.setStartLine "GET /tictactoe HTTP/1.1")
                                  (.putHeader "Host" "me"))]
      (should= "1" (get-choice r1))
      (should-be-nil (get-choice r2))))

  (it "gets the state of a request"
    (let [r1 (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                  (.putHeader "Cookie" "state={:state :database}")
                                  (.putHeader "Content-Length" "8")
                                  (.setBody "choice=1"))
          r2 (doto (HttpMessage.) (.setStartLine "GET /tictactoe HTTP/1.1")
                                  (.putHeader "Host" "me"))]
      (should= {:database :file :state :load-type} (get-state r1))
      (should= {:state :database} (get-state r2))))

  (it "generates HTML for a given state"
    (should= database-html (generate-html {}))
    (should= database-html (generate-html {:state :database}))
    (should= load-type-html (generate-html {:state :load-type}))
    (should= board-size-html (generate-html {:state :board-size}))
    (should= empty-3x3-html (generate-html {:state :done
                                            :board (utils/empty-board 3)
                                            :board-size 3}))
    (should= empty-3x3x3-html (generate-html {:state :done
                                              :board (repeat 3 (utils/empty-board 3))
                                              :board-size 3})))

  (it "parses cookie"
    (should= "{hello}" (parse-cookie "state={hello}")))

  (it "handles an initial GET request"
    (let [req (HttpMessage. "GET /tictactoe HTTP/1.1\r\nHost: me\r\n\r\n")
          html database-html
          res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                   (.putHeader "Content-Type" "text/html")
                                   (.putHeader "Content-Length" (str (count html)))
                                   (.putHeader "Set-Cookie" "state={:state :database}")
                                   (.setBody (str html)))]
      (should= res (handle req))))

  (it "handles initial form submit"
    (let [req (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                   (.putHeader "Content-Length" "8")
                                   (.putHeader "Cookie" "state={:state :database}")
                                   (.setBody "choice=1"))
          html load-type-html
          res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                   (.putHeader "Content-Type" "text/html")
                                   (.putHeader "Content-Length" (str (count html)))
                                   (.putHeader "Set-Cookie" (str "state={:state :load-type, "
                                                                        ":database :file}"))
                                   (.setBody html))]
      (should= res (handle req))))

  (it "handles load-type form submit"
    (let [req (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                   (.putHeader "Cookie" "state={:database :file :state :load-type}")
                                   (.putHeader "Content-Length" "8")
                                   (.setBody "choice=1"))
          html (str "Dimension"
                    "<form action=\"/tictactoe\" method=\"post\">"
                    "<button type=\"submit\" name=\"choice\" value=\"1\">1. 2D</button>"
                    "<button type=\"submit\" name=\"choice\" value=\"2\">2. 3D (3x3x3)</button>"
                    "</form>")
          res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                   (.putHeader "Content-Type" "text/html")
                                   (.putHeader "Content-Length" (str (count html)))
                                   (.putHeader "Set-Cookie" (str "state={:database :file, "
                                                                 ":state :dimension, "
                                                                 ":load-type :new}"))
                                   (.setBody html))]
      (should= res (handle req))))

  (it "handles board-size form submit"
    (let [req (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                   (.putHeader "Cookie" "state={:state :board-size}")
                                   (.putHeader "Content-Length" "8")
                                   (.setBody "choice=4"))
          html versus-type-html
          res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                   (.putHeader "Content-Type" "text/html")
                                   (.putHeader "Content-Length" (str (count html)))
                                   (.putHeader "Set-Cookie" (str "state={:state :versus-type, "
                                                                 ":board-size 4, "
                                                                 ":board [\\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]}"))
                                   (.setBody (str html)))]
      (should= res (handle req))))

  (it "handles final menu form submit"
    (let [state (str "state={:state :versus-type, :dimension 2, :board-size 3, :board [\\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]}")
          req (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                   (.putHeader "Cookie" state)
                                   (.putHeader "Content-Length" "8")
                                   (.setBody "choice=1"))
          html empty-3x3-html
          res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                   (.putHeader "Content-Type" "text/html")
                                   (.putHeader "Content-Length" (str (count html)))
                                   (.putHeader "Set-Cookie" (str "state={:state :done, "
                                                                 ":dimension 2, "
                                                                 ":board-size 3, "
                                                                 ":board [\\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_], "
                                                                 ":versus-type :pvp}"))
                                   (.setBody (str html)))]
      (should= res (handle req))))

  (with-stubs)
  (it "handles game over form submit"
    (with-redefs [utils/now (stub :now {:return "current-date"})]
      (let [state (str "state={:state :done,:versus-type :pvp, :dimension 2, :board-size 3, :board [\\_ \\x \\x \\o \\o \\_ \\_ \\_ \\_]}")
            req (doto (HttpMessage.) (.setStartLine "POST /tictactoe HTTP/1.1")
                                     (.putHeader "Cookie" state)
                                     (.putHeader "Content-Length" "8")
                                     (.setBody "choice=1"))
            html over-3x3-html
            res (doto (HttpMessage.) (.setStartLine HttpMessage/HttpOK)
                                     (.putHeader "Content-Type" "text/html")
                                     (.putHeader "Content-Length" (str (count html)))
                                     (.putHeader "Set-Cookie" (str "state={:state :done, "
                                     ":versus-type :pvp, "
                                     ":dimension 2, "
                                     ":board-size 3, "
                                     ":board [\\x \\x \\x \\o \\o \\_ \\_ \\_ \\_], "
                                     ":gamemode #tictactoe.game_mode.PvPGame{:size 3, :init-board [\\x \\x \\x \\o \\o \\_ \\_ \\_ \\_]}, "
                                     ":date \"current-date\", "
                                     ":over? true}"))
                                     (.setBody (str html)))]
        (should= res (handle req))))))