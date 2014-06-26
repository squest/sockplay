(ns sockplay.handler
  (:use [compojure.route :only [files not-found resources]]
        [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server)
  (:require [selmer.parser :as page]
            [com.ashafa.clutch :as cl]
            [couchbase-clj.client :as cc]
            [cheshire.core :as cs]
            [ring.middleware.session :as session]))

(defonce server (atom nil))
(def popop (atom 0N))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

; TODO Very very very IMPORTANT
; channels is a vector of channels' data with each channel's map consists of
; {:user :chatroom :message :dataType }

(def channels (atom []))
(def current-user (atom ""))

(defn send-message
  [data]
  (loop [ch @channels]
    (if (empty? ch)
        (do (println @channels))
        (recur (do (send! (:channel (first ch))
                          (let [map-data (cs/parse-string data true)]
                            (-> (assoc map-data :type "message")
                                (cs/generate-string)))
                          false)
                   (rest ch))))))

(defn send-message-to-room
  [data]
  (let [map-data (cs/parse-string data true)
        room-channel (filter #(= (:chatroom map-data)
                                 (:chatroom %))
                             @channels)]
    (loop [ch room-channel]
      (if (empty? ch)
          (do (println room-channel))
          (recur (do (send! (:channel (first ch))
                            (-> (assoc map-data :type "message")
                                (cs/generate-string))
                            false)
                     (rest ch)))))))

; FIXME this is just boong-boongan
(defn send-soal-to-room
  [data]
  (let [map-data (cs/parse-string data true)
        room-channel (filter #(= (:chatroom map-data)
                                 (:chatroom %))
                             @channels)]
    (loop [ch room-channel]
      (if (empty? ch)
        (do (println room-channel))
        (recur (do (send! (:channel (first ch))
                          (-> (assoc map-data :type "message")
                              (cs/generate-string))
                          false)
                   (rest ch)))))))

(defn send-soal
  [data]
  (loop [ch @channels]
    (if (empty? ch)
        (do (println @channels))
        (recur (do (send! (:channel (first ch))
                          (let [map-data (cs/parse-string data true)]
                            (-> (assoc map-data :type "soal")
                                (cs/generate-string)))
                          false)
                   (rest ch))))))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn on-receive-data
  [data]
  (let [chan-data (cs/parse-string data true)]
    (cond (= "message" (:dataType chan-data))
          (if (zero? (rem @popop 10))
              (do (send-message-to-room data)
                  (send-soal data)
                  (swap! popop inc))
              (do (send-message-to-room data)
                  (swap! popop inc)))
          (= "answer" (:dataType chan-data))
          (println chan-data))))

(defn on-open
  [channel]
  (if (websocket? channel)
      (do (println "WebSocket channel")
          (swap! channels conj (assoc @current-user
                                      :channel channel))
          (loop [ch (filter #(= (:chatroom @current-user)
                                (:chatroom %))
                            @channels)]
            (if (empty? ch)
                (do (println @channels))
                (recur (do (send! (:channel (first ch))
                                  (-> {:type "new-user"
                                       :list (-> #(dissoc % :channel)
                                                 (map (filter #(= (:chatroom @current-user)
                                                                  (:chatroom %))
                                                              @channels)))}
                                      (cs/generate-string))
                                  false)
                           (rest ch))))))
      (println "HTTP channel")))


(defn handler [req]
  (with-channel req channel              ; get the channel
                (on-close channel
                          (fn [status]
                            (do (reset! channels
                                        (vec (remove #(= channel (:channel %)) @channels)))
                                (loop [ch (filter #(= (:chatroom @current-user)
                                                      (:chatroom %))
                                                  @channels)]
                                  (if (empty? ch)
                                      (do (println @channels))
                                      (recur (do (send! (:channel (first ch))
                                                        (-> {:type "new-user"
                                                             :list (-> #(dissoc % :channel)
                                                                       (map (filter #(= (:chatroom @current-user)
                                                                                        (:chatroom %))
                                                                                    @channels)))}
                                                            (cs/generate-string))
                                                        false)
                                                 (rest ch))))))))
                (on-open channel)
                (on-receive channel on-receive-data)))

(defn homepage [req]
  (page/render-file "public/home.html"
                    {:page {:title "Welcome"
                            :headline "Hello Jon"}}))

(defn chatpage [user]
  (page/render-file "public/chat.html"
                    {:page {:title "Let's chat"
                            :headline "Hello joon"}
                     :user user}))

(defroutes all-routes
           (GET "/" [req] homepage)
           (POST "/login" req
                 (do (println req)
                     (let [user (:username (:params req))
                           chatroom (:chatroom (:params req))]
                       (do (reset! current-user {:username (str user)
                                                 :chatroom (str chatroom)})
                           (chatpage {:username (str user)
                                      :chatroom (str chatroom)})))))
           (GET "/private/:user1/:user2/:uuid" [user1 user2 uuid]
                nil)
           (GET "/ws" req
                (do (println req)
                    (handler req)))       ;; websocket
           (files "/" {:root "resources/public/"}) ;; static file url prefix /static, in `public` folder
           (not-found "<p>Page not found.</p>")
           (resources "/" {:root "resources/public/"})) ;; all other, return 404

(defn -main [& args]
  ;; The #' is useful, when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (reset! server (run-server (site #'all-routes) {:port 3000}))
  (println "Server is running at port 3000"))
